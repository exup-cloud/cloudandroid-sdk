package com.yjkj.chainup.new_version.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.yjkj.chainup.R
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.util.BigDecimalUtils
import com.yjkj.chainup.util.GlideUtils
import kotlinx.android.synthetic.main.item_user_info_view.view.*
import org.json.JSONObject

/**
 * @Author lianshangljl
 * @Date 2019/4/16-5:33 PM
 * @Email buptjinlong@163.com
 * @description
 */
class UserInfoView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    var headerIc = 0


    init {
        attrs.let {
            var typedArray = context.obtainStyledAttributes(it, R.styleable.UserInfoView)
            headerIc = typedArray.getResourceId(R.styleable.UserInfoView_headerIcon, R.drawable.ic_default_head)
        }
        initView(context)
    }

    fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.item_user_info_view, this, true)
        iv_header_view.setImageResource(headerIc)
        tv_transaction_number_title?.text = getStringContent("otc_text_merchantTradeNumber")
        tv_otc_credit_title?.text = getStringContent("otc_text_merchantCredit")
        tv_cumulative_clinch_deal_title?.text = getStringContent("otc_text_totalBargainAmount")
    }

    fun getStringContent(contentId: String): String {
        return LanguageUtil.getString(context, contentId)
    }

    /**
     * 设置昵称
     */
    fun setUserNick(nickName: String) {
        tv_user_name.text = nickName
    }

    /**
     * 设置交易次数
     */
    fun setTransactionNumber(number: String) {
        tv_transaction_number_content.text = number
    }

    /**
     * 设置信用度
     */
    fun setCreditContent(credit: String) {
        tv_otc_credit_content.text = credit
    }

    /**
     * 设置累计成交量
     */
    fun setCumulativeClinch(temp: String) {
        tv_cumulative_clinch_deal_content.text = BigDecimalUtils.showSNormal(temp)
    }


    /**
     * 支付方式
     */
    fun initPayments(bean: List<JSONObject>?) {
        val payments = bean
        when (payments?.size) {
            0 -> {
                iv_pay_1st.visibility = View.GONE
                iv_pay_2nd.visibility = View.GONE
                iv_pay_3rd.visibility = View.GONE
            }

            1 -> {
                iv_pay_1st.visibility = View.VISIBLE
                iv_pay_2nd.visibility = View.GONE
                iv_pay_3rd.visibility = View.GONE
                GlideUtils.loadImage(context, payments[0].optString("icon"), iv_pay_1st)
            }

            2 -> {
                iv_pay_1st.visibility = View.VISIBLE
                iv_pay_2nd.visibility = View.VISIBLE
                iv_pay_3rd.visibility = View.GONE

                GlideUtils.loadImage(context, payments[0].optString("icon"), iv_pay_1st)
                GlideUtils.loadImage(context, payments[1].optString("icon"), iv_pay_2nd)

            }

            else -> {
                iv_pay_1st.visibility = View.VISIBLE
                iv_pay_2nd.visibility = View.VISIBLE
                iv_pay_3rd.visibility = View.VISIBLE

                GlideUtils.loadImage(context, payments?.get(0)?.optString("icon"), iv_pay_1st)
                GlideUtils.loadImage(context, payments?.get(1)?.optString("icon"), iv_pay_2nd)
                GlideUtils.loadImage(context, payments?.get(2)?.optString("icon"), iv_pay_3rd)
            }
        }
    }

}