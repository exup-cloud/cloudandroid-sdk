package com.yjkj.chainup.new_version.bean

import com.google.gson.annotations.SerializedName

data class OTCOrderDetailBean(
        @SerializedName("seller") val seller: Seller,
        @SerializedName("complainId") val complainId: Int, //0 申诉id
        @SerializedName("totalPrice") val totalPrice: String ?= "", //13.5600000000000000
        @SerializedName("payTime") val payTime: String = "0", //已支付时间，前端根据这个时间判断是否展示提交申诉按钮
        @SerializedName("complainCommand") val complainCommand: String = "", // 申诉口令
        @SerializedName("paycoin") val paycoin: String = "", //支付币种 - CNY
        @SerializedName("description") val description: String = "", //
        @SerializedName("isTwoMin") val isTwoMin: Int = 0, //是否超过两分钟，0：展示2分钟文案 1：邮箱电话才会给（场外优化0709新增）
        @SerializedName("buyer") val buyer: Buyer,
        @SerializedName("volume") val volume: Double = 0.0, //1.0000000000000000
        @SerializedName("limitTime") val limitTime: Int = 0, //719477
        @SerializedName("sendCoinTime") val sendCoinTime: String = "0", //放币时间，已完成显示（新增）
        @SerializedName("sequence") val sequence: String = "", //2018101811332 订单id
        @SerializedName("isComplainUser") val isComplainUser: Int, //0  0 当前用户不是申诉人 1 当前用户为申诉人 （新增）
        @SerializedName("price") val price: String ?= "", //13.5690930449890000
        @SerializedName("payment") val payment: ArrayList<Payment>,
        @SerializedName("coin") val coin: String = "", // 购买币种 - USDT
        @SerializedName("payKey") val payKey: String = "", // 购买币种 - USDT
        @SerializedName("status") val status: Int, //订单状态 待支付1 已支付2 交易成功3 取消 4 申诉 5 打币中6 异常订单7 申诉处理结束8
        @SerializedName("isBlockTrade") val isBlockTrade: String = "",//0：普通订单，1：大宗订单
        @SerializedName("otcAuthnameOpen") val otcAuthnameOpen: String = "0", // 是否显示实名开关 0：不开 1：开
        @SerializedName("cancelStatus") val cancelStatus: String = "0",//0：默认 无取消状态，1：买家取消 2：申诉判定买家未付款取消 3：超时未支付取消
        @SerializedName("ctime") val ctime: Long = 0L//订单创建时间

) {
    data class Buyer(
            @SerializedName("uid") val uid: Int, //10609
            @SerializedName("otcNickName") val otcNickName: String = "", //186****6503
            @SerializedName("email") val email: String = "", // 邮箱
            @SerializedName("realName") val realName: String = "", // 姓名
            @SerializedName("imageUrl") val imageUrl: String = "",
            @SerializedName("isOnline") val isOnline: Int, //1
            @SerializedName("mobileNumber") val mobileNumber: String = "",//电话
            @SerializedName("countryCode") val countryCode: String = "",
            @SerializedName("companyLevel") val companyLevel: Int,
            @SerializedName("completeOrders") val completeOrders: Int //0
    )


    data class Payment(
            @SerializedName("payment") val payment: String = "", //otc.payment.alipay
            @SerializedName("bankName") val bankName: String = "", //银行名字
            @SerializedName("bankOfDeposit") val bankOfDeposit: String = "", //支行
            @SerializedName("qrcodeImg") val qrcodeImg: String = "", //图片
            @SerializedName("userName") val userName: String = "", //用户名字
            @SerializedName("ifscCode") val ifscCode: String = "",//null
            @SerializedName("account") val account: String = "",//null
            @SerializedName("icon") val icon: String = "", //otc.payment.alipay
            @SerializedName("title") val title: Any //null
    )

    data class Seller(
            @SerializedName("uid") val uid: Int, //10639
            @SerializedName("otcNickName") val otcNickName: String = "", //ja****98
            @SerializedName("email") val email: String = "", // 邮箱
            @SerializedName("realName") val realName: String = "", // 姓名
            @SerializedName("imageUrl") val imageUrl: String = "",
            @SerializedName("isOnline") val isOnline: Int, //0
            @SerializedName("mobileNumber") val mobileNumber: String = "",//电话
            @SerializedName("countryCode") val countryCode: String = "",
            @SerializedName("companyLevel") val companyLevel: Int,
            @SerializedName("completeOrders") val completeOrders: Int //20
    )
}

