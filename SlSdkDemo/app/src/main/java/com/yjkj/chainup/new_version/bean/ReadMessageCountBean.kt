package com.yjkj.chainup.new_version.bean

import com.google.gson.annotations.SerializedName

/**
 * @Author lianshangljl
 * @Date 2019/5/30-10:15 PM
 * @Email buptjinlong@163.com
 * @description
 */
data class ReadMessageCountBean(
        /**
         * 新增 首页logo夜晚模式
         */
        @SerializedName("noReadMsgCount") val noReadMsgCount: String = "0"
)