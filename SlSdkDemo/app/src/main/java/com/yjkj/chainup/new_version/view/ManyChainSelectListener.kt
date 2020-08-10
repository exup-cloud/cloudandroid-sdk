package com.yjkj.chainup.new_version.view

import org.json.JSONObject

/**
 * @Author lianshangljl
 * @Date 2019-12-24-15:21
 * @Email buptjinlong@163.com
 * @description
 */
interface ManyChainSelectListener {
    fun selectCoin(coinName: JSONObject)
}