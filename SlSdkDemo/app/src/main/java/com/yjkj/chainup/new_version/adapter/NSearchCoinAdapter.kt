package com.yjkj.chainup.new_version.adapter

import android.text.TextUtils
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yjkj.chainup.R
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.NCoinManager
import com.yjkj.chainup.new_version.view.CustomTagView
import com.yjkj.chainup.util.ColorUtil
import com.yjkj.chainup.util.DecimalUtil
import org.json.JSONObject

/**
 * @Author: Bertking
 * @Date：2019-09-26-11:02
 * @Description:
 */
class NSearchCoinAdapter : BaseQuickAdapter<JSONObject, BaseViewHolder>(R.layout.item_search_coin) {

    var isSelfData = false
    public fun setParams(isSelfData: Boolean) {
        this.isSelfData = isSelfData
    }

    var isShowLever: Boolean = false


    override fun convert(helper: BaseViewHolder?, item: JSONObject?) {

        if (null == helper || null == item)
            return

        if (isShowLever) {
            val multiple = item.optString("multiple", "")
            helper.setGone(R.id.tv_lever, !TextUtils.isEmpty(multiple))
        } else {
            helper.setGone(R.id.tv_lever, false)
        }

        var name = NCoinManager.showAnoterName(item)//item.optString("name")
        var newcoinFlag = item.optInt("newcoinFlag")
        var vol = item.optString("vol")
        var close = item.optString("close")
        var price = item.optInt("price")
        var rose = item.optDouble("rose")

        val mainArea =  LanguageUtil.getString(mContext, "transaction_text_mainZone")
        val innovationArea =  LanguageUtil.getString(mContext, "market_text_innovationZone")
        val observeArea =  LanguageUtil.getString(mContext, "market_text_observeZone")
        val unlock_area =  LanguageUtil.getString(mContext, "market_text_unlockZone")


        var tagCoin = NCoinManager.getMarketShowCoinName(item?.optString("name"))
        if (!TextUtils.isEmpty(NCoinManager.getCoinTag4CoinName(tagCoin))) {
            helper?.getView<CustomTagView>(R.id.ctv_content)?.setTextViewContent(NCoinManager.getCoinTag4CoinName(tagCoin))
            helper?.apply {
                setGone(R.id.ctv_content, true)
            }
        } else {
            helper?.apply {
                setGone(R.id.ctv_content, false)
            }
        }

        val alveZone = LanguageUtil.getString(mContext,"common_text_halveZone")

        var ll_main_area = helper.getView<View>(R.id.ll_main_area)
        var v_line = helper.getView<View>(R.id.v_line)
//        if (isSelfData) {
//            ll_main_area.visibility = View.GONE
//            v_line.visibility = View.GONE
//
//        } else {
            if (helper.adapterPosition == 0) {
                ll_main_area.visibility = View.VISIBLE
                v_line.visibility = View.VISIBLE
                when (newcoinFlag) {
                    1 -> {
                        helper.setText(R.id.ll_title_content, mainArea)
                    }
                    2 -> {
                        helper.setText(R.id.ll_title_content, innovationArea)
                    }
                    3 -> {
                        helper.setText(R.id.ll_title_content, observeArea)
                    }
                    0 -> {
                        helper.setText(R.id.ll_title_content, alveZone)
                    }
                    4 -> {
                        helper.setText(R.id.ll_title_content, unlock_area)
                    }
                }
            } else {
                if (data[helper.adapterPosition - 1].optInt("newcoinFlag") != newcoinFlag) {
                    ll_main_area.visibility = View.VISIBLE
                    v_line.visibility = View.VISIBLE
                    when (newcoinFlag) {
                        1 -> {
                            helper.setText(R.id.ll_title_content, mainArea)
                        }
                        2 -> {
                            helper.setText(R.id.ll_title_content, innovationArea)
                        }
                        3 -> {
                            helper.setText(R.id.ll_title_content, observeArea)
                        }
                        0 -> {
                            helper.setText(R.id.ll_title_content, alveZone)
                        }
                        4 -> {
                            helper.setText(R.id.ll_title_content, unlock_area)
                        }
                    }
                } else {
                    ll_main_area.visibility = View.GONE
                    v_line.visibility = View.GONE
                }
//            }
        }



        if (null != name && name.contains("/")) {
            val split = name.split("/")

            helper?.setText(R.id.tv_coin_name, split[0])
            helper?.setText(R.id.tv_market_name, "/" + split[1])
        }

        /**
         * 收盘价
         */
        if (TextUtils.isEmpty(close)) {
            helper?.setText(R.id.tv_close_price, "--")
        } else {
            helper?.setText(R.id.tv_close_price, DecimalUtil.cutValueByPrecision(close, price))
        }

        helper.setTextColor(R.id.tv_close_price, ColorUtil.getMainColorType(rose >= 0))
    }
}