package com.yjkj.chainup.new_version.view

import android.widget.CheckBox
import org.json.JSONObject

/**
 * @Author lianshangljl
 * @Date 2020-06-08-21:30
 * @Email buptjinlong@163.com
 * @description
 */
interface NewReleaseListener {
    fun addOrRemovePaymethodListener(checkbox: CheckBox, isChecked:Boolean, payment: JSONObject)
}