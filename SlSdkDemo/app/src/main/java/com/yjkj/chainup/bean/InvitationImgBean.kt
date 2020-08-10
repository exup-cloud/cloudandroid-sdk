package com.yjkj.chainup.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author lianshangljl
 * @Date 2019-09-25-11:25
 * @Email buptjinlong@163.com
 * @description
 */
class InvitationImgBean(
        @SerializedName("local_img_cn") var local_img_cn: String = "",
        @SerializedName("local_img_en") var local_img_en: String = "",
        @SerializedName("online_img_cn") var online_img_cn: String = "",
        @SerializedName("online_img_en") var online_img_en: String = ""
)