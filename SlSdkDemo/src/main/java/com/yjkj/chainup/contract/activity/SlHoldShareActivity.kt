package com.yjkj.chainup.contract.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.ContractUserDataAgent
import com.contract.sdk.data.Contract
import com.contract.sdk.data.ContractPosition
import com.contract.sdk.extra.Contract.ContractCalculate
import com.contract.sdk.utils.MathHelper
import com.contract.sdk.utils.NumberUtil
import com.tbruyelle.rxpermissions2.RxPermissions
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.contract.uilogic.LogicContractSetting
import com.yjkj.chainup.contract.utils.*
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.new_version.view.CommonlyUsedButton
import com.yjkj.chainup.util.BitmapUtils
import com.yjkj.chainup.util.DisplayUtil
import kotlinx.android.synthetic.main.sl_activity_hold_share.*
import java.text.DecimalFormat

/**
 * 仓位分享
 */
class SlHoldShareActivity : NBaseActivity() {
    override fun setContentView(): Int {
        return R.layout.sl_activity_hold_share
    }

    private var coinCode = ""
    private var pid: Long? = 0
    private var instrumentId = 0
    private var mContractPosition: ContractPosition? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        isLandscape = true
        super.onCreate(savedInstanceState)
        coinCode = intent.getStringExtra("coinCode")
        pid = intent.getLongExtra("pid", 0)
        instrumentId = intent.getIntExtra("instrument_id", 0)
        //LogUtil.d("DEBUG","coinCode:"+coinCode+";pid:"+pid)
        loadData()
        val itemView = findViewById<View>(R.id.ll_share_layout)
        val layoutParams = itemView.layoutParams
        layoutParams.height = DisplayUtil.getScreenHeight() * 3 / 4 - 10
        initShareView(itemView, false)
        initShareView(ll_real_share_layout, true)
        initListener()
    }


    override fun loadData() {
        val contractPositions: List<ContractPosition>? = ContractUserDataAgent.getCoinPositions(coinCode)
        if (contractPositions != null) {
            for (i in contractPositions.indices) {
                val position = contractPositions[i]
                if (pid == position.pid) {
                    mContractPosition = position
                    break
                }
            }
        }
    }

    private fun initListener() {
        ll_share_layout.setOnClickListener {
        }
        ll_container.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }
        bt_share.textContent = getLineText("sl_str_share_confirm")
        bt_share.isEnable(true)
        bt_share.listener = object : CommonlyUsedButton.OnBottonListener {
            override fun bottonOnClick() {
                doShare()
            }

        }
    }

    private fun doShare() {
        val rxPermissions = RxPermissions(this@SlHoldShareActivity)
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe { granted ->
                    if (granted) {
                        rl_share_layout.isDrawingCacheEnabled = true
                        rl_share_layout.buildDrawingCache()
                        val bitmap: Bitmap = Bitmap.createBitmap(rl_share_layout.drawingCache)
                        if (bitmap != null) {
                            ShareToolUtil.sendLocalShare(mActivity, bitmap)
                        } else {
                            DisplayUtil.showSnackBar(window?.decorView, getString(R.string.warn_storage_permission), false)
                        }
                    } else {
                        DisplayUtil.showSnackBar(window?.decorView, getString(R.string.warn_storage_permission), false)
                    }

                }
    }

    private fun initShareView(itemView: View, isRealShare: Boolean) {
        val tvType = itemView.findViewById<TextView>(R.id.tv_type)
        val tvContractValue = itemView.findViewById<TextView>(R.id.tv_contract_value)
        val tvLatestPrice = itemView.findViewById<TextView>(R.id.tv_latest_price)
        val tvLatestPriceValue = itemView.findViewById<TextView>(R.id.tv_latest_price_value)
        val tvOpenPriceValue = itemView.findViewById<TextView>(R.id.tv_open_price_value)
        val tvEarned = itemView.findViewById<TextView>(R.id.tv_earned)
        val ivQrCode = itemView.findViewById<ImageView>(R.id.iv_qr_code)
        val tvIntro = itemView.findViewById<TextView>(R.id.tv_intro)
        val ivShareHeader = itemView.findViewById<ImageView>(R.id.iv_share_header)
        //文本动态设置
        itemView.findViewById<TextView>(R.id.sl_str_profit_rate_label).onLineText("sl_str_profit_rate1")
        itemView.findViewById<TextView>(R.id.tv_contract_name_label).onLineText("sl_str_swap")
        itemView.findViewById<TextView>(R.id.tv_open_price_label).onLineText("sl_str_cost_price")
        itemView.findViewById<TextView>(R.id.tv_down_app).onLineText("sl_str_platform_des")
        itemView.findViewById<TextView>(R.id.tv_scan_down_app).onLineText("sl_str_scan_down_app")

        if (isRealShare) {
            ivShareHeader.setImageResource(R.drawable.sl_share_header_big_bg)
        } else {
            ivShareHeader.setImageResource(R.drawable.sl_share_header_small_bg)
        }

        var profitRate = 0.0 //未实现盈亏
        var profitAmount = 0.0 //未实现盈亏额
        val contract: Contract = ContractPublicDataAgent.getContract(instrumentId)
                ?: return
        val contractTicker = ContractPublicDataAgent.getContractTicker(contract.instrument_id)
                ?: return
        val dfDefault: DecimalFormat = NumberUtil.getDecimal(contract.price_index-1)
        tvContractValue.text = contract.symbol
        if (LogicContractSetting.getPnlCalculate(this) === 0) {
            tvLatestPrice.onLineText("sl_str_fair_price")
            tvLatestPriceValue.text = dfDefault.format(MathHelper.round(contractTicker.fair_px, contract.price_index-1))
        } else {
            tvLatestPrice.onLineText("sl_str_latest_price")
            tvLatestPriceValue.text = dfDefault.format(MathHelper.round(contractTicker.last_px, contract.price_index-1))
        }
        //二维码
        var imgUrl = UserDataService.getInstance()?.inviteUrl
        if (TextUtils.isEmpty(imgUrl)) {
            imgUrl = "error!"
        }
        val bmp: Bitmap? = BitmapUtils.generateBitmap(imgUrl, 480, 480)
        ivQrCode.setImageBitmap(bmp)

        if (mContractPosition != null) {
            val pnl_calculate: Int = LogicContractSetting.getPnlCalculate(this)
            val position_type = mContractPosition!!.side
            if (position_type == 1) { //多仓
                tvType.onLineText("sl_str_open_long")
                profitAmount += ContractCalculate.CalculateCloseLongProfitAmount(
                        mContractPosition!!.cur_qty,
                        mContractPosition!!.avg_cost_px,
                        if (pnl_calculate == 0) contractTicker.fair_px else contractTicker.last_px,
                        contract.face_value,
                        contract.isReserve)

                val p: Double = MathHelper.add(mContractPosition!!.cur_qty, mContractPosition!!.close_qty)
                val plus: Double = MathHelper.mul(
                        MathHelper.round(mContractPosition!!.tax),
                        MathHelper.div(MathHelper.round(mContractPosition!!.cur_qty), p))
                profitRate = MathHelper.div(profitAmount, MathHelper.add(MathHelper.round(mContractPosition!!.im), plus)) * 100

            } else if (position_type == 2) { //空仓
                tvType.onLineText("sl_str_open_short")
                // tv_type.setTextColor(getResources().getColor(R.color.colorRed));
                profitAmount += ContractCalculate.CalculateCloseShortProfitAmount(
                        mContractPosition!!.cur_qty,
                        mContractPosition!!.avg_cost_px,
                        if (pnl_calculate == 0) contractTicker.fair_px else contractTicker.last_px,
                        contract.face_value,
                        contract.isReserve)

                val p = MathHelper.add(mContractPosition!!.cur_qty, mContractPosition!!.close_qty)
                val plus = MathHelper.mul(
                        MathHelper.round(mContractPosition!!.tax),
                        MathHelper.div(MathHelper.round(mContractPosition!!.cur_qty), p))
                profitRate = MathHelper.div(profitAmount, MathHelper.add(MathHelper.round(mContractPosition!!.im), plus)) * 100
            }

            val symbol = if (profitRate >= 0) "+" else ""
            tvEarned.text = symbol + NumberUtil.getDecimal(2).format(MathHelper.round(profitRate, contract.value_index)).toString() + "%"
            tvOpenPriceValue.text = dfDefault.format(MathHelper.round(mContractPosition!!.avg_cost_px, contract.price_index))
            when {
                profitRate > 0 -> {
                    when {
                        profitRate > 50 -> {
                            tvIntro.text = "\"${ LanguageUtil.getString(this, "sl_str_win_intro5")}\""
                        }
                        profitRate > 20 -> {
                            tvIntro.text = "\"${ LanguageUtil.getString(this, "sl_str_win_intro4")}\""
                        }
                        profitRate > 10 -> {
                            tvIntro.text = "\"${ LanguageUtil.getString(this, "sl_str_win_intro3")}\""
                        }
                        profitRate > 5 -> {
                            tvIntro.text = "\"${ LanguageUtil.getString(this, "sl_str_win_intro2")}\""
                        }
                        else -> {
                            tvIntro.text = "\"${ LanguageUtil.getString(this, "sl_str_win_intro1")}\""
                        }
                    }
                    tvType.setBackgroundResource(R.drawable.sl_border_green_fill2)
                    tvEarned.setTextColor(resources.getColor(R.color.main_green))
                }
                profitRate < 0 -> {
                    when {
                        profitRate < -50 -> {
                            tvIntro.text = "\"${ LanguageUtil.getString(this, "sl_str_lose_intro5")}\""
                        }
                        profitRate < -20 -> {
                            tvIntro.text = "\"${ LanguageUtil.getString(this, "sl_str_lose_intro4")}\""
                        }
                        profitRate < -10 -> {
                            tvIntro.text = "\"${ LanguageUtil.getString(this, "sl_str_lose_intro3")}\""
                        }
                        profitRate < -5 -> {
                            tvIntro.text = "\"${ LanguageUtil.getString(this, "sl_str_lose_intro2")}\""
                        }
                        else -> {
                            tvIntro.text = "\"${ LanguageUtil.getString(this, "sl_str_lose_intro1")}\""
                        }
                    }
                    tvType.setBackgroundResource(R.drawable.sl_border_red_fill2)
                    tvEarned.setTextColor(resources.getColor(R.color.main_red))
                }
                else -> {
                    tvIntro.text = "\"${ LanguageUtil.getString(this, "sl_str_win_intro1")}\""
                    tvType.setBackgroundResource(R.drawable.sl_border_green_fill2)
                    tvEarned.setTextColor(resources.getColor(R.color.main_green))
                }
            }
        }else{
            tvType.onLineText("sl_str_open_long")
            tvType.setBackgroundResource(R.drawable.sl_border_green_fill2)
            tvEarned.text = "+" + NumberUtil.getDecimal(2).format(MathHelper.round(profitRate, contract.value_index)).toString() + "%"
            tvOpenPriceValue.text = dfDefault.format(MathHelper.round(contractTicker.last_px, contract.price_index))

            tvType.setBackgroundResource(R.drawable.sl_border_green_fill2)
            tvIntro.text = "\"${ LanguageUtil.getString(this, "sl_str_win_intro1")}\""
        }

    }


    companion object {
        fun show(activity: Activity, coinCode: String, instrument_id: Int, pid: Long) {
            val intent = Intent(activity, SlHoldShareActivity::class.java)
            val bundle = Bundle()
            bundle.putString("coinCode", coinCode)
            bundle.putLong("pid", pid)
            bundle.putInt("instrument_id", instrument_id)
            intent.putExtras(bundle)
            activity.startActivity(intent)
        }
    }

}