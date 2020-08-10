package com.yjkj.chainup.util

import android.text.InputFilter
import android.text.Spanned

/**
 * @Author: Bertking
 * @Date：2018/10/25-上午10:42
 * @Description:限制EditText的小数点后的位数
 */

class DecimalDigitsInputFilter(decimalDigits: Int) : InputFilter {
    var decimalDigits: Int = decimalDigits


    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        var dotPos = -1
        var len = dest!!.length
        for (i in 0 until len) {
            var c = dest!![i]
            if (c == '.' || c == ',') {
                dotPos = i
                break
            }
        }


        if (dotPos >= 0) {

            // protects against many dots
            if (source!!.equals(".") || source.equals(",")) {
                return ""
            }
            // if the text is entered before the dot
            if (dend <= dotPos) {
                return null
            }
            if (len - dotPos > decimalDigits) {
                return ""
            }
        }

        return null
    }

}