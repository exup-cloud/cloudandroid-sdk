package com.yjkj.chainup.util

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.widget.TextView
import com.yjkj.chainup.app.ChainUpApp


/**
 * @Author: Bertking
 * @Date：2019/3/7-3:41 PM
 * @Description: 剪切板功能
 */
object ClipboardUtil {
    /**
     * 复制功能
     * @param textView 复制其文本
     */
    fun copy(textView: TextView) {
        val cm = textView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText(null, textView.text))
        }


    }

    /**
     * 复制功能
     * @param string 目标字符串
     */
    fun copy(string: String) {
        val cm = ChainUpApp.appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (cm != null) {
            cm.setPrimaryClip (ClipData.newPlainText(null, string))
        }
    }


    /**
     * 粘贴功能
     * @param textView 目标文本框
     */
    fun paste(textView: TextView) {
        val cm = textView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 粘贴板有数据，并且是文本
        if(null!=cm.primaryClipDescription){
            if (cm.hasPrimaryClip() && cm.primaryClipDescription!!.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                val item = cm.primaryClip?.getItemAt(0)
                val text = item?.text ?: ""
                textView.text = text
            }
        }

    }
}