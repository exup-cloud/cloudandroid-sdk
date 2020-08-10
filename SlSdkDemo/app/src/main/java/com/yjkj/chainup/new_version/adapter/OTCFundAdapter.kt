package com.yjkj.chainup.new_version.adapter

import android.text.TextUtils
import android.view.View
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yjkj.chainup.R
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.NCoinManager
import com.yjkj.chainup.manager.RateManager
import com.yjkj.chainup.util.BigDecimalUtils
import com.yjkj.chainup.util.Utils
import org.json.JSONObject

/**
 * @Author lianshangljl
 * @Date 2018/10/26-上午11:07
 * @Email buptjinlong@163.com
 * @description
 */
open class OTCFundAdapter(var data: ArrayList<JSONObject>) :
        BaseQuickAdapter<JSONObject, BaseViewHolder>(R.layout.item_new_asset_otc, data), Filterable {


    private var filter: MyFilter? = null
    private var listener: FilterListener? = null

    fun setListener(listener: FilterListener) {
        this.listener = listener
    }

    interface FilterListener {
        fun getFilterData(list: List<JSONObject>) //获取过滤后的数据
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = MyFilter(data)
        }
        return filter ?: MyFilter(data)
    }

    /**
     * 创建内部类，实现数据的过滤
     */
    internal inner class MyFilter(originalData: ArrayList<JSONObject>) : Filter() {
        private var originalData = java.util.ArrayList<JSONObject>()

        init {
            this.originalData = originalData
        }

        /**
         * 该方法返回搜索过滤后的数据
         *
         * @param constraint
         * @return
         */
        override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
            val results = Filter.FilterResults()
            /**
             * 没有搜索内容的话就还是给results赋值原始数据的值和大小
             * 执行了搜索的话，根据搜索的规则过滤即可，最后把过滤后的数据的值和大小赋值给results
             *
             */
            if (TextUtils.isEmpty(constraint)) {
                results.values = originalData
                results.count = originalData.size
            } else {
                // 创建集合保存过滤后的数据
                val filteredList = java.util.ArrayList<JSONObject>()
                // 遍历原始数据集合，根据搜索的规则过滤数据
                for (s in originalData) {
                    // 这里就是过滤规则的具体实现【规则有很多，大家可以自己决定怎么实现】
                    if (NCoinManager.getShowMarket(s?.optString("coinName")).toLowerCase().contains(constraint.toString().trim { it <= ' ' }.toLowerCase())) {
                        // 规则匹配的话就往集合中添加该数据
                        filteredList.add(s)
                    }
                }
                results.values = filteredList
                results.count = filteredList.size
            }

            // 返回FilterResults对象
            return results
        }

        /**
         * 该方法用来刷新用户界面，根据过滤后的数据重新展示列表
         */
        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {

            // 获取过滤后的数据
            if (results.values != null) {
                data = results.values as ArrayList<JSONObject>
            }
            // 如果接口对象不为空，那么调用接口中的方法获取过滤后的数据，具体的实现在new这个接口的时候重写的方法里执行
            if (listener != null) {
                listener?.getFilterData(data)
            }
            // 刷新数据源显示
            notifyDataSetChanged()
            notifyItemRangeChanged(0, data.size)
        }
    }

    override fun convert(helper: BaseViewHolder?, item: JSONObject?) {
        /**
         * 币种
         */
        helper?.setText(R.id.tv_coin_name, NCoinManager.getShowMarket(item?.optString("coinName")
                ?: ""))

        helper?.setText(R.id.tv_4th_title,  LanguageUtil.getString(mContext, "assets_text_equivalence") + "(${RateManager.getCurrencySign()})")

        var bean = NCoinManager.getCoinObj(item?.optString("coinName"))

        helper?.setText(R.id.tv_1st_title,LanguageUtil.getString(mContext,"assets_text_available"))
        helper?.setText(R.id.tv_2nd_title,LanguageUtil.getString(mContext,"assets_text_freeze"))
        helper?.setText(R.id.tv_3rd_title,LanguageUtil.getString(mContext,"assets_text_lockup"))
        helper?.setText(R.id.tv_4th_title,LanguageUtil.getString(mContext,"assets_text_equivalence"))
        helper?.setText(R.id.tv_5th_title,LanguageUtil.getString(mContext,"assets_text_equivalence"))


        /**
         * 可用资产
         */
        var mnormal_balance = BigDecimalUtils.divForDown(item?.optString("normal_balance"), NCoinManager.getCoinShowPrecision(item?.optString("coinName")
                ?: "")).toPlainString()
        val normalBalanceTitle =  LanguageUtil.getString(mContext,"assets_text_available")
        /**
         * 锁仓
         */
        var mlock_grant_divided_balance = BigDecimalUtils.divForDown(item?.optString("lock_grant_divided_balance"), NCoinManager.getCoinShowPrecision(item?.optString("coinName")
                ?: "")).toPlainString()
        val lockedBalanceTitle =  LanguageUtil.getString(mContext, "assets_text_lockup")

        /**
         * 冻结
         */
        var mlock_balance = BigDecimalUtils.divForDown(item?.optString("lock_balance"), NCoinManager.getCoinShowPrecision(item?.optString("coinName")
                ?: "")).toPlainString()
        val frozenBalanceTitle =  LanguageUtil.getString(mContext, "assets_text_freeze")

        /**
         * 折合
         */
        val result = RateManager.getCNYByCoinName("BTC", item?.optString("allBtcValuatin")
                ?: "0", isOnlyResult = true)

        val convertBTCTitle =  LanguageUtil.getString(mContext, "assets_text_equivalence")

        var isShowAssets = UserDataService.getInstance().isShowAssets


        /**
         * 加价可用
         */
        val overChargeBalance = BigDecimalUtils.divForDown(item?.optString("overcharge_balance")
                ?: "0", NCoinManager.getCoinShowPrecision(item?.optString("coinName")
                ?: "")).toPlainString()
        val overChargeBalanceTitle =  LanguageUtil.getString(mContext, "common_text_limitAvailable")



        val pairs = ArrayList<Pair<String, String>>(5)
        pairs.add(Pair(normalBalanceTitle, mnormal_balance))
        pairs.add(Pair(frozenBalanceTitle, mlock_balance))
        pairs.add(Pair(lockedBalanceTitle, mlock_grant_divided_balance))
        pairs.add(Pair(convertBTCTitle, result))

        val views = ArrayList<Pair<TextView?, TextView?>>(5)
        views.add(Pair(helper?.getView<TextView>(R.id.tv_1st_title), helper?.getView<TextView>(R.id.tv_1st_value)))
        views.add(Pair(helper?.getView<TextView>(R.id.tv_2nd_title), helper?.getView<TextView>(R.id.tv_2nd_value)))
        views.add(Pair(helper?.getView<TextView>(R.id.tv_3rd_title), helper?.getView<TextView>(R.id.tv_3rd_value)))
        views.add(Pair(helper?.getView<TextView>(R.id.tv_4th_title), helper?.getView<TextView>(R.id.tv_4th_value)))
        views.add(Pair(helper?.getView<TextView>(R.id.tv_5th_title), helper?.getView<TextView>(R.id.tv_5th_value)))

        if (bean?.optInt("isOvercharge") == 1) {
            pairs.add(1, Pair(overChargeBalanceTitle, overChargeBalance))
            views.last().first?.visibility = View.VISIBLE
            views.last().second?.visibility = View.VISIBLE
        } else {
            views.last().first?.visibility = View.GONE
            views.last().second?.visibility = View.GONE
        }

        pairs.forEachIndexed { index, pair ->
            views[index].first?.text = pair.first
            views[index].second?.text = pair.second
        }

        if (!isShowAssets) {
            Utils.assetsHideShow(isShowAssets, helper?.getView(R.id.tv_1st_value), mnormal_balance)
            Utils.assetsHideShow(isShowAssets, helper?.getView(R.id.tv_2nd_value), mlock_balance)
            Utils.assetsHideShow(isShowAssets, helper?.getView(R.id.tv_3rd_value), mlock_grant_divided_balance)
            Utils.assetsHideShow(isShowAssets, helper?.getView(R.id.tv_4th_value), result)
            Utils.assetsHideShow(isShowAssets, helper?.getView(R.id.tv_5th_value), result)
        }


    }

}
