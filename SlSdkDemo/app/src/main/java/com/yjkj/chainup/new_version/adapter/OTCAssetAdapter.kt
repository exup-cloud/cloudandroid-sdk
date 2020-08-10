package com.yjkj.chainup.new_version.adapter

import android.text.TextUtils
import android.util.Log
import android.widget.Filter
import android.widget.Filterable
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yjkj.chainup.R
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.NCoinManager
import com.yjkj.chainup.manager.RateManager
import com.yjkj.chainup.util.BigDecimalUtils
import com.yjkj.chainup.util.Utils
import org.json.JSONObject

/**
 * Created by $USER_NAME on 2018/10/15.
 *
 */
open class OTCAssetAdapter(var data: ArrayList<JSONObject>) :
        BaseQuickAdapter<JSONObject, BaseViewHolder>(R.layout.item_asset_otc, data), Filterable {
    private var filter: MyFilter? = null
    private var listener: FilterListener? = null

    var assetState: String = ParamConstant.FABI_INDEX

    fun setType(type: String) {
        assetState = type
    }

    interface FilterListener {
        fun getFilterData(list: ArrayList<JSONObject>) //获取过滤后的数据
    }

    fun setListener(listener: FilterListener) {
        this.listener = listener
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

                    when (assetState) {
                        ParamConstant.LEVER_INDEX -> {
                            if (null != s.optString("name") && NCoinManager.getShowMarket(s.optString("name")).toLowerCase().contains(constraint.toString().trim { it <= ' ' }.toLowerCase())) {
                                // 规则匹配的话就往集合中添加该数据
                                filteredList.add(s)
                            }
                        }
                        ParamConstant.FABI_INDEX -> {
                            if (null != s.optString("coinSymbol") && NCoinManager.getShowMarket(s.optString("coinSymbol")).toLowerCase().contains(constraint.toString().trim { it <= ' ' }.toLowerCase())) {
                                // 规则匹配的话就往集合中添加该数据
                                filteredList.add(s)
                            }
                        }
                        ParamConstant.B2C_INDEX -> {
                            if (null != s.optString("symbol") && NCoinManager.getShowMarket(s.optString("symbol")).toLowerCase().contains(constraint.toString().trim { it <= ' ' }.toLowerCase())) {
                                // 规则匹配的话就往集合中添加该数据
                                filteredList.add(s)
                            }
                        }
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
            if (null != results.values) {
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
        var mnormal = ""
        var mlock = ""
        var result = ""

        var secondNormal = ""
        var secondmlock = ""
        var secondresult = ""
        helper?.setText(R.id.tv_coin_title,LanguageUtil.getString(mContext,"assets_text_available"))
        helper?.setText(R.id.tv_canUse_title,LanguageUtil.getString(mContext,"assets_text_freeze"))
        helper?.setText(R.id.tv_equivalent,LanguageUtil.getString(mContext,"assets_text_equivalence"))
        when (assetState) {
            ParamConstant.FABI_INDEX -> {
                helper?.setText(R.id.tv_coin_name, NCoinManager.getShowMarket(item?.optString("coinSymbol")))

                helper?.setText(R.id.tv_equivalent,  LanguageUtil.getString(mContext, "assets_text_equivalence") + "(${RateManager.getCurrencySign()})")

                mnormal = BigDecimalUtils.showSNormal(item?.optString("normal") ?: "0")
                mlock = BigDecimalUtils.showSNormal(item?.optString("lock") ?: "0")
                result = RateManager.getCNYByCoinName("BTC", item?.optString("btcValuation"), isOnlyResult = true)


            }

            ParamConstant.B2C_INDEX -> {
                helper?.setText(R.id.tv_equivalent,  LanguageUtil.getString(mContext, "otc_text_orderTotal"))


                mnormal = BigDecimalUtils.divForDown(item?.optString("normalBalance")
                        ?: "0", item?.optInt("showPrecision", 1) ?: 1).toPlainString()
                mlock = BigDecimalUtils.divForDown(item?.optString("lockBalance")
                        ?: "0", item?.optInt("showPrecision", 1) ?: 1).toPlainString()
                result = RateManager.getCNYByCoinName("BTC", item?.optString("totalBalance"), isOnlyResult = true)

                helper?.setText(R.id.tv_coin_name, NCoinManager.getShowMarket(item?.optString("symbol")))

            }
            ParamConstant.LEVER_INDEX -> {
                helper?.apply {
                    setText(R.id.tv_coin_title,  LanguageUtil.getString(mContext, "common_text_coinsymbol"))
                    setText(R.id.tv_canUse_title,  LanguageUtil.getString(mContext, "assets_text_available"))
                    setText(R.id.tv_equivalent,  LanguageUtil.getString(mContext, "leverage_have_borrowed"))
                }
                helper?.setGone(R.id.ll_second_layout, true)
                helper?.setGone(R.id.tv_equivalent_all, true)

                mnormal = NCoinManager.getShowMarket(item?.optString("baseCoin"))
                mlock = BigDecimalUtils.divForDown(item?.optString("baseNormalBalance"), ParamConstant.NORMAL_PRECISION).toPlainString()
                result = BigDecimalUtils.divForDown(item?.optString("baseBorrowBalance"), ParamConstant.NORMAL_PRECISION).toPlainString()


                secondNormal = NCoinManager.getShowMarket(item?.optString("quoteCoin"))
                secondmlock = BigDecimalUtils.divForDown(item?.optString("quoteNormalBalance"), ParamConstant.NORMAL_PRECISION).toPlainString()
                secondresult = BigDecimalUtils.divForDown(item?.optString("quoteBorrowBalance"), ParamConstant.NORMAL_PRECISION).toPlainString()
                /**
                 * 币种名字
                 */
                helper?.setText(R.id.tv_coin_name, NCoinManager.getShowMarketName(item?.optString("name")))

                /**
                 * 折合
                 */
                val result = RateManager.getCNYByCoinName("BTC", item?.optString("symbolBalance"), true, true)
                helper?.setText(R.id.tv_equivalent_all,  LanguageUtil.getString(mContext, "assets_text_equivalence") + " " + result + RateManager.getCurrencyLang())
            }

        }

        var isShowAssets = UserDataService.getInstance().isShowAssets

        /**
         * 一下是否隐藏资产
         */
        Utils.assetsHideShow(isShowAssets, helper?.getView(R.id.tv_normal_balance), mnormal)
        Utils.assetsHideShow(isShowAssets, helper?.getView(R.id.tv_lock_balance), mlock)
        Utils.assetsHideShow(isShowAssets, helper?.getView(R.id.tv_equivalent_content), result)



        Utils.assetsHideShow(isShowAssets, helper?.getView(R.id.tv_normal_second_balance), secondNormal)
        Utils.assetsHideShow(isShowAssets, helper?.getView(R.id.tv_lock_second_balance), secondmlock)
        Utils.assetsHideShow(isShowAssets, helper?.getView(R.id.tv_equivalent_second_content), secondresult)


    }
}
