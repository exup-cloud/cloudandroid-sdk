package com.yjkj.chainup.bean

data class QuotesData(var event_rep: String = "", var channel: String = "", var data: Any? = "null",
                      var tick: Tick = Tick(), var ts: Long = 0, var status: String? = "ok" // 1563755809000
) {


    class Tick(var amount: String = "--", // 交易额
               var vol: String = "0", // 交易量
               var high: String = "--", // 最高价
               var low: String = "--", // 最低价
               var rose: Double = 0.0, // 涨幅
               var close: String = "0", // 收盘价
               var open: String = "--", // 开盘价
               var name: String = "",
               var anotherName: String = "",
               var symbol: String = "",
               var type: Int = 0,
               var isFirst: Boolean = false,
               var pricePrecision: Int = 0,
               var volumePrecision: Int = 0

    ) {
        fun updateTick(tick: Tick) {
            amount = tick.amount
            vol = tick.vol
            high = tick.high
            low = tick.low
            rose = tick.rose
            close = tick.close
            open = tick.open
            name = tick.name
            symbol = tick.symbol
        }


        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + symbol.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Tick

            if (amount != other.amount) return false
            if (vol != other.vol) return false
            if (high != other.high) return false
            if (low != other.low) return false
            if (rose != other.rose) return false
            if (close != other.close) return false
            if (open != other.open) return false
            if (name != other.name) return false
            if (symbol != other.symbol) return false

            return true
        }

        override fun toString(): String {
            return "Tick(amount='$amount', vol='$vol', high='$high', low='$low', rose=$rose, close='$close', open='$open', name='$name', symbol='$symbol', type=$type, isFirst=$isFirst)"
        }
    }

    override fun toString(): String {
        return "QuotesData(channel='$channel', ts=$ts, tick=$tick)"
    }
}

