package com.yjkj.chainup.extra_service.push

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.yjkj.chainup.new_version.activity.NewMainActivity
import com.yjkj.chainup.util.LogUtil


class PushControll : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pushUrl = intent.getStringExtra("pushPlayUrl")
        val isMain = ActivityCollector.isActivityExist(NewMainActivity::class.java)
        if (isMain) {
            LogUtil.e("LogUtils", " onCreate() 已经存在 不处理${pushUrl}")
            val intent = this.packageManager.getLaunchIntentForPackage(packageName)//获取启动Activity
            startActivity(intent)
            RouteApp.getInstance().execApp(pushUrl,this)
        }else{
            LogUtil.e("LogUtils", " onCreate() 不存在 跳转 ${pushUrl}")
            startActivity(Intent(this, NewMainActivity::class.java).apply {
                putExtra("pushUrl", pushUrl)
            })
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.e("LogUtils", " onDestroy() PushControll")
    }
}