package com.yjkj.chainup.new_version.adapter

import android.util.Log
import android.widget.ImageView
import com.chad.library.adapter.base.BaseViewHolder
import com.yjkj.chainup.R
import com.yjkj.chainup.manager.Contract2PublicInfoManager
import com.yjkj.chainup.new_version.dialog.DialogUtil
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.new_version.view.PositionITemView
import com.yjkj.chainup.util.BigDecimalUtils
import com.yjkj.chainup.util.ColorUtil
import com.yjkj.chainup.util.StringUtil
import org.json.JSONObject
import java.math.BigDecimal

/**
 * @Author: Bertking
 * @Date：2019-09-11-20:32
 * @Description:
 */
class NPositionAdapter(data: ArrayList<JSONObject>) : NBaseAdapter(data = data, layoutId = R.layout.item_hold_position) {
    override fun convert(helper: BaseViewHolder?, item: JSONObject?) {

        item?.run {
            val baseSymbol = optString("baseSymbol")
            val quoteSymbol = optString("quoteSymbol")
            val leverageLevel = optString("leverageLevel")
            val contractId = optString("contractId")
            val pricePrecision = optString("pricePrecision").toIntOrNull() ?: 2
            val valuePrecision = optString("valuePrecision").toIntOrNull() ?: 4
            val side = optString("side")
            val id = optString("id")
            val liquidationPrice = optString("liquidationPrice")
            val realisedAmountHistory = optString("realisedAmountHistory")
            val holdAmount = optString("holdAmount")
            val unrealisedAmountIndex = optString("unrealisedAmountIndex")
            val avgPrice = optString("avgPrice")
            val volume = optString("volume")
            // 价值
            val indexPrice = optString("indexPrice")
            // 标记价格
            val priceValue = optString("priceValue")
            // 回报率
            var unrealisedRateIndex = optString("unrealisedRateIndex")





            helper?.run {


                /**
                 * 订单方向
                 */
                setTextColor(R.id.tv_side, ColorUtil.getMainColorType(side == "BUY"))
                val orderSide = if (side == "BUY") {
                     LanguageUtil.getString(mContext, "contract_text_long")
                } else {
                     LanguageUtil.getString(mContext, "contract_text_short")
                }
                setText(R.id.tv_side, orderSide)


                /**
                 * 合约币对名称
                 */
                setText(R.id.tv_coin_name, baseSymbol + quoteSymbol)

                val level = if (Contract2PublicInfoManager.isPureHoldPosition()) {
                    ""
                } else {
                    "(" + leverageLevel + "X)"
                }
                setText(R.id.tv_contract_type, Contract2PublicInfoManager.getContractType(mContext, contractId.toInt()) + " $level")

                /**
                 * 合约ID
                 * 分仓模式:显示
                 * 净持仓：隐藏
                 */
                if (Contract2PublicInfoManager.isPureHoldPosition()) {
                    setGone(R.id.tv_position_id, false)
                } else {
                    setGone(R.id.tv_position_id, true)
                    renderData(helper,
                            R.id.tv_position_id,
                             LanguageUtil.getString(mContext, "contract_position_id"),
                            id
                    )
                }


                addOnClickListener(R.id.tv_deposit)
                addOnClickListener(R.id.btn_adjust_lever)
                addOnClickListener(R.id.btn_take_order)


                /**
                 * 强平价格
                 */
                val liquidationPriceByPrecision = Contract2PublicInfoManager.cutValueByPrecision(liquidationPrice, pricePrecision)
                renderData(helper,
                        R.id.tv_liquidation_price,
                         LanguageUtil.getString(mContext, "contract_text_liqPrice") + "(${quoteSymbol})",
                        liquidationPriceByPrecision
                )


                /**
                 * 标记价格
                 */
                val indexPriceByPrecision = Contract2PublicInfoManager.cutValueByPrecision(indexPrice, pricePrecision)
                renderData(helper,
                        R.id.tv_index_price,
                         LanguageUtil.getString(mContext, "contract_text_liqPrice") + "(${quoteSymbol})",
                        indexPriceByPrecision
                )

                /**
                 * 价值
                 */
                val priceValueByPrecision = Contract2PublicInfoManager.cutValueByPrecision(priceValue, pricePrecision)
                renderData(helper,
                        R.id.tv_price_value,
                         LanguageUtil.getString(mContext,"contract_text_value") + "(BTC)",
                        priceValueByPrecision
                )


                /**
                 * 已实现盈亏(历史盈亏)
                 */
                var realisedAmountCurrByPrecision = Contract2PublicInfoManager.cutDespoitByPrecision(realisedAmountHistory)
                if (!realisedAmountCurrByPrecision.contains("-")) {
                    realisedAmountCurrByPrecision = "+$realisedAmountCurrByPrecision"
                }
                renderData(helper,
                        R.id.tv_realised_profit,
                         LanguageUtil.getString(mContext, "contract_text_realisedPNL"),
                        realisedAmountCurrByPrecision
                )


                /**
                 * 未实现盈亏(回报率)
                 */
                var unrealisedAmountIndexByPrecision = Contract2PublicInfoManager.cutDespoitByPrecision(unrealisedAmountIndex)


                Log.d(TAG, "======回报率S:${unrealisedRateIndex}===")
                unrealisedRateIndex = if (StringUtil.checkStr(unrealisedAmountIndex)) {
                    BigDecimal(unrealisedRateIndex).setScale(2, BigDecimal.ROUND_HALF_DOWN).toPlainString()
                } else {
                    "0.00"
                }
                // 回报率
                Log.d(TAG, "======回报率U:${unrealisedRateIndex}===")


                if (!unrealisedAmountIndexByPrecision.contains("-")) {
                    unrealisedAmountIndexByPrecision = "+$unrealisedAmountIndexByPrecision"
                }

                renderData(helper,
                        R.id.tv_unrealised_profit,
                         LanguageUtil.getString(mContext,"contract_text_unrealisedPNL") + "(${ LanguageUtil.getString(mContext, "contract_text_returnRateUnit")})",
                        unrealisedAmountIndexByPrecision + ("($unrealisedRateIndex%)")
                )


                /**
                 * 保证金 = holdAmount + unrealisedAmountIndex
                 */
                val realHoldAmount = BigDecimalUtils.add(holdAmount.toString(), unrealisedAmountIndex)
                val holdAmountByPrecision = Contract2PublicInfoManager.cutValueByPrecision(realHoldAmount.toString(), valuePrecision)
                renderData(helper,
                        R.id.tv_deposit,
                         LanguageUtil.getString(mContext, "contract_text_margin"),
                        holdAmountByPrecision + "BTC"
                )

                /**
                 * 开仓均价
                 */
                val avgPriceByPrecision = Contract2PublicInfoManager.cutValueByPrecision(avgPrice.toString(), pricePrecision)
                renderData(helper,
                        R.id.tv_avg_price,
                         LanguageUtil.getString(mContext, "contract_text_openAveragePrice") + "(${quoteSymbol})",
                        avgPriceByPrecision
                )

                /**
                 * 仓位数量（张）
                 */
                getView<PositionITemView>(R.id.tv_volume)?.run {
                    title =  LanguageUtil.getString(mContext, "contract_text_positionNumber")
                    value = "${volume}"
                    tailValueColor = ColorUtil.getMainColorType(side == "BUY")
                }


                if (Contract2PublicInfoManager.isPureHoldPosition()) {
                    /**
                     * 调整杠杆(25x)
                     */
                    setText(R.id.btn_adjust_lever,  LanguageUtil.getString(mContext, "contract_action_editLever") + "(" + leverageLevel + "x)")
                } else {
                    setText(R.id.btn_adjust_lever,  LanguageUtil.getString(mContext, "contract_text_limitPositions"))
                }


                /**
                 * 调整保证金
                 */

                getView<PositionITemView>(R.id.tv_deposit)?.tailValueColor = ColorUtil.getColor(R.color.main_blue)
                /**
                 * 分享功能
                 */
                getView<ImageView>(R.id.btn_share).setOnClickListener {
                    DialogUtil.showPositionShareDialog(mContext ?: return@setOnClickListener,item)
                }

            }


        }


    }


    private fun renderData(helper: BaseViewHolder?, viedId: Int, string: String, values: String) {
        helper?.getView<PositionITemView>(viedId)?.run {
            title = string
            value = values
        }

    }
}