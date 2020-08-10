package com.yjkj.chainup.util

import android.text.TextUtils
import com.google.gson.Gson
import com.yjkj.chainup.bean.kline.WsLink
import com.yjkj.chainup.bean.kline.WsLinkBean
import java.util.*

/**
 * @Author: Bertking
 * @Date：2018/12/12-4:07 PM
 * @Description: 统一处理WS所需的链接
 *
 * WARN ：
 * 方法返回WsLinkBean的原因：
 * 1. channel (原因在于：Java版的WS有BUG,取消订阅依然会返回上一次订阅的数据，故需要filter)
 * 2. json WS链接的请求
 */
class WsLinkUtils {

    companion object {
        /**
         * 请求---k线历史数据
         * @param symbol 类似于btcusdt
         * @param time （1min , 5min, 15min,30min,1h,1week,1month）
         */
        @JvmStatic
        fun getKLineHistoryLink(symbol: String?, time: String?): WsLinkBean {

            var klineHistoryChannel = "market_${symbol}_kline_$time"
            var wsLink = WsLink()
            wsLink.event = "req"
            wsLink.params?.channel = klineHistoryChannel

            return WsLinkBean(klineHistoryChannel, toJson(wsLink))

        }

        @JvmStatic
        fun getKlineHistoryOther(symbol: String?, time: String?, endId: String): String {
            return "{\"event\":\"req\",\"params\":{\"channel\":\"market_${symbol}_kline_${time}\",\"cb_id\":\"$symbol\",\"endIdx\":$endId,\"pageSize\":300}}"
        }

        fun getKLineHistoryKey(symbol: String): String {
            return "market_${symbol}_kline_1min"
        }

        /**
         * 订阅---K线最新行情
         * @param symbol 类似于btcusdt
         * @param isSub 订阅 OR 取消
         * @param time （1min , 5min, 15min,30min,1h,1week,1month）
         */
        @JvmStatic
        fun getKlineNewLink(symbol: String, time: String?, isSub: Boolean = true): WsLinkBean {
            val event = if (isSub)
                "sub"
            else
                "unsub"
            var newKLineChannel = "market_${symbol}_kline_$time"

            var wsLink = WsLink()
            wsLink.event = event
            wsLink.params?.channel = newKLineChannel

            return WsLinkBean(newKLineChannel, toJson(wsLink))


        }

        /**
         * 推荐使用tickerFor24HLink()
         * 订阅---24H的行情
         * @param symbol 类似于btcusdt
         * @param isSub 订阅 OR 取消
         */
//        @Deprecated("多余的序列化&反序列化过程")
//        fun getTickerFor24HLink(symbol: String, isSub: Boolean = true): WsLinkBean {
//            val event = if (isSub)
//                "sub"
//            else
//                "unsub"
//            var dayTickerChannel = "market_${symbol}_ticker"
//
//            var wsLink = WsLink()
//            wsLink.event = event
//            wsLink.params?.channel = dayTickerChannel
//            return WsLinkBean(dayTickerChannel, toJson(wsLink))
//        }

        /**
         * 订阅---24H的行情
         * @param symbol 类似于btcusdt
         * @param isSub 订阅 OR 取消
         */
        @JvmStatic
        fun tickerFor24HLink(symbol: String, isSub: Boolean = true, isChannel: Boolean = false): String {
            // 此处的toLowerCase()是为了兼容合约
            val channel = "market_${symbol.toLowerCase()}_ticker"
            return if (isChannel) {
                channel
            } else {
                val event = if (isSub)
                    "sub"
                else
                    "unsub"
                "{\"event\":\"$event\",\"params\":{\"channel\":\"$channel\",\"cb_id\":\"自定义\"}}"
            }
        }

        /**
         * 订阅---24H的行情
         * @param symbol 类似于btcusdt
         * @param isSub 订阅 OR 取消
         */
        @JvmStatic
        fun tickerFor24HLinkBean(symbol: String, isSub: Boolean = true): WsLinkBean {
            // 此处的toLowerCase()是为了兼容合约
            val channel = "market_${symbol.toLowerCase()}_ticker"
            val event = if (isSub)
                "sub"
            else
                "unsub"
            val bean = WsLinkBean(channel, "{\"event\":\"$event\",\"params\":{\"channel\":\"$channel\",\"cb_id\":\"自定义\"}}")
            bean.symbol = symbol
            return bean

        }


        @JvmStatic
        fun tickerReqReview(cid: String): String {
            if (TextUtils.isEmpty(cid)) {
                return "{\"event\":\"req\",\"params\":{\"channel\":\"review\"}}"
            } else {
                return "{\"event\":\"req\",\"params\":{\"channel\":\"review2\",\"cid\":\"$cid\"}}"
            }

        }


        /**
         * 请求---成交历史
         * @param symbol 类似于btcusdt
         */
        fun getDealHistoryLink(symbol: String): WsLinkBean {
            var dealHistoryChannel = "market_${symbol}_trade_ticker"

            var wsLink = WsLink()
            wsLink.event = "req"
            wsLink.params?.channel = dealHistoryChannel
            return WsLinkBean(dealHistoryChannel, toJson(wsLink))
        }

        /**
         * 订阅---最新成交信息
         * @param symbol 类似于btcusdt
         * @param isSub 订阅 OR 取消
         */
        fun getDealNewLink(symbol: String, isSub: Boolean = true): WsLinkBean {
            val event = if (isSub)
                "sub"
            else
                "unsub"

            var newDealChannel = "market_${symbol}_trade_ticker"

            var wsLink = WsLink()
            wsLink.event = event
            wsLink.params?.channel = newDealChannel

            return WsLinkBean(newDealChannel, toJson(wsLink))

        }


        /**
         * 订阅---深度盘口
         * @param symbol 类似于btcusdt
         * @param isSub 订阅 OR 取消
         * @param step 深度(default 0)
         */
        fun getDepthLink(symbol: String, isSub: Boolean = true, step: String = "0"): WsLinkBean {
            val name = symbol
            val event = if (isSub)
                "sub"
            else
                "unsub"
            var depthChannel = "market_${name}_depth_step${step}"
            var json = "{\"event\":\"$event\",\"params\":{\"channel\":\"$depthChannel\",\"cb_id\":\"自定义\",\"asks\":150,\"bids\":150}}"
            val bean = WsLinkBean(depthChannel, json)
            bean.symbol = symbol
            bean.step = step
            return bean

        }

        @JvmStatic
        fun pongBean(): WsLinkBean {
            var json = "{\"pong\":\"${Date().time/1000}\""
            val bean = WsLinkBean("", json)
            return bean
        }

        fun toJson(bean: Any): String {
            return Gson().toJson(bean)
        }

    }
}