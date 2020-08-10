package com.yjkj.chainup.wedegit

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.yjkj.chainup.R
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.view.ManyChainSelectListener
import com.yjkj.chainup.util.LineSelectOnclickListener
import kotlinx.android.synthetic.main.item_many_chain.view.*
import org.json.JSONObject

/**
 * @Author lianshangljl
 * @Date 2019-12-24-15:14
 * @Email buptjinlong@163.com
 * @description
 */
class ManyChainView @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var listener: ManyChainSelectListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.item_many_chain, this, true)
        initView()
    }


    fun initView() {
        /**
         * 点击图片 问号
         */
        iv_risk_rate?.setOnClickListener {
            setContentListener()
        }
        tv_link_name?.text = LanguageUtil.getString(context, "link_name")
    }

    var content: String = ""
    var follCoinByMainList: ArrayList<JSONObject> = arrayListOf()

    /**
     * @param showSymbol 这里是主链币
     * @param selectSymbol 增加地址时添加的子链币  只展示一个
     */
    fun setManyChainView(showSymbol: String, selectSymbol: String = "") {
        follCoinByMainList = PublicInfoDataService.getInstance().getFollowCoinsByMainCoinName(showSymbol)
        if (null == follCoinByMainList || follCoinByMainList.size == 0) {
            ll_chain_name_layout?.visibility = View.GONE
            rv_chain_list?.visibility = View.GONE
            return
        }
        ll_chain_name_layout?.visibility = View.VISIBLE
        rv_chain_list?.visibility = View.VISIBLE


        follCoinByMainList.sortBy { it.optInt("sort", 0) }


        var selectPosition = 0
        /**
         * 判断selectSymbol是否为空，如果是空就正常，如果不为空，就只显示selectSymbol的子链
         */
        if (TextUtils.isEmpty(selectSymbol)) {
            listener?.selectCoin(follCoinByMainList[0])
        } else {
            var selectJson = JSONObject()
            follCoinByMainList.forEach {
                if (it?.optString("name") == selectSymbol) {
                    selectJson = it
                    return@forEach
                }
            }
            if (null != selectJson && selectJson?.length() ?: 0 > 0) {
                follCoinByMainList.clear()
                follCoinByMainList.add(selectJson)
            }
        }

        rv_chain_list?.setNormalAdapter(follCoinByMainList, selectPosition)
        rv_chain_list?.setLineSelectOncilckListener(object : LineSelectOnclickListener {
            override fun sendOnclickMsg() {

            }

            override fun selectMsgIndex(index: String?) {
                if (null != listener) {
                    var json = follCoinByMainList[index?.toInt() ?: 0]
//                    setIvRiskRateVisible(json)
                    listener?.selectCoin(json)
                }
            }
        })
    }

    /**
     * 设置
     */
    private fun setIvRiskRateVisible(json: JSONObject) {
        if ("1" == json.optString("mainChainType")) {
            iv_risk_rate?.visibility = View.VISIBLE
        } else {
            iv_risk_rate?.visibility = View.GONE
        }
    }

    private fun setContentListener() {
        NewDialogUtils.showSingleDialog(context, content, object : NewDialogUtils.DialogBottomListener {
            override fun sendConfirm() {
            }

        }, "", LanguageUtil.getString(context, "alert_common_iknow"))
    }

    fun clearLables() {
        rv_chain_list?.clearLables()
    }

}