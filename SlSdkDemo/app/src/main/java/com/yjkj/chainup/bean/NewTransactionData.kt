package com.yjkj.chainup.bean

data class NewTransactionData(val channel: String = "", val tick: Tick, val ts: String = "") {

    data class Tick(val data: MutableList<NewTransactionDetail> = mutableListOf())

    data class NewTransactionDetail(val side: String = "", val vol: String = "", val amount: String = "",
                                    val price: String = "", val id: String = "", val ts: String = "", val ds: String = "")
}