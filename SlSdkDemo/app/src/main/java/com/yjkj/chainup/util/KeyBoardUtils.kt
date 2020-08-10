package com.yjkj.chainup.util

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager

/**
 * @Author: Bertking
 * @Date：2019-06-26-16:10
 * @Description:
 */
object KeyBoardUtils {
    /**
     * 不适用Dialog
     */
    fun closeKeyBoard(context: Context){
        // 关闭键盘
        val inputManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        Log.d("=isActive=", "=======${inputManager?.isActive}===========")
        inputManager.hideSoftInputFromWindow((context as Activity)?.window?.decorView?.windowToken, 0)
    }
}