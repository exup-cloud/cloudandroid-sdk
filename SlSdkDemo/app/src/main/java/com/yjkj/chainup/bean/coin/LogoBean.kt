package com.yjkj.chainup.bean.coin

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class LogoBean(
        /**
         * 登录logo
         */
        @SerializedName("login_logo") val loginLogo: String ?= "", //http://chaindown-oss.oss-cn-hongkong.aliyuncs.com/upload/20180630110250711.png
        /**
         * 启动页logo
         */
        @SerializedName("startup_logo") val startupLogo: String ?= "", //http://chaindown-oss.oss-cn-hongkong.aliyuncs.com/upload/20180630110424597.png
        /**
         * 用户中心logo
         */
        @SerializedName("user_center_logo") val userCenterLogo: String ?= "", //http://chaindown-oss.oss-cn-hongkong.aliyuncs.com/upload/20180630110443869.png

        /**
         * 行情顶部logo
         */
        @SerializedName("market_logo") val marketLogo: String ?= "" //http://chaindown-oss.oss-cn-hongkong.aliyuncs.com/upload/20180630110405343.png
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(loginLogo)
        writeString(startupLogo)
        writeString(userCenterLogo)
        writeString(marketLogo)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<LogoBean> = object : Parcelable.Creator<LogoBean> {
            override fun createFromParcel(source: Parcel): LogoBean = LogoBean(source)
            override fun newArray(size: Int): Array<LogoBean?> = arrayOfNulls(size)
        }
    }
}