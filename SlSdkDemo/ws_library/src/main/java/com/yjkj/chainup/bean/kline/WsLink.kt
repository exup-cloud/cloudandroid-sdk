package com.yjkj.chainup.bean.kline

import com.google.gson.annotations.SerializedName

/**
 * 成交历史： market_btcusdt_trade_ticker
 */
class WsLink(
        @SerializedName("event")
        var event: String = "", // $event
        @SerializedName("params")
        var params: Params?

) {
    constructor(event: String) : this( "",Params("自定义","hhh")) {
        this.event = event
    }

    constructor() : this("")

    class Params(
            @SerializedName("cb_id")
            var cbId: String, // 自定义
            @SerializedName("channel")
            var channel: String // $oneDayQuotesChannel
    )
}