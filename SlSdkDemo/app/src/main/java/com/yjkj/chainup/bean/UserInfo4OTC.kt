package com.yjkj.chainup.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author lianshangljl
 * @Date 2018/10/18-上午11:56
 * @Email buptjinlong@163.com
 * @description
 */
data class UserInfo4OTC(
        @SerializedName("imageUrl") var imageUrl: String = "",//用户头像
        @SerializedName("lastLoginTime") var lastLoginTime: Long = 0L,//最后登录时间
        @SerializedName("authLevel") var authLevel: Int,//认证状态 0、未审核，1、通过，2、未通过  3、未认证
        @SerializedName("mobileAuthStatus") var mobileAuthStatus: Int,//是否开启了手机认证:0-未开启,1-开启
        @SerializedName("otcNickName") var otcNickName: String = "",//用户昵称
        @SerializedName("completeOrders") var completeOrders: Int,//用户订单成交笔数（交易次数
        @SerializedName("complainNum") var complainNum: Int,//总申诉量
        /**
         * 0 判断此页面访问情况（如下）：
        0：未登录用户查看他人的主页和登录用户查看自己的主页；
        1：登录用户查看他人的主页，并且当前显示用户在登录用户黑名单中；
        2：登录用户查看他人的主页，并且当前显示用户不在登录用户黑名单中
         */
        @SerializedName("identity") var identity: Int,// 是否屏蔽
        @SerializedName("otcLast30DaysComOrders") var otcLast30DaysComOrders: Long = 0L,// 30日成单
        @SerializedName("trustScore") var trustScore: Double = 0.0,// 信用度
        @SerializedName("otcOrderAvePaidTime") var otcOrderAvePaidTime: Double = 0.0,// 场外平均放币时间
        @SerializedName("sucComplainNum") var sucComplainNum: Int,//胜诉量
        @SerializedName("loginStatus") var loginStatus: Int,//在线状态 （1在线，0离线）
        @SerializedName("registerTime") var registerTime: Long = 0L//注册时间
)