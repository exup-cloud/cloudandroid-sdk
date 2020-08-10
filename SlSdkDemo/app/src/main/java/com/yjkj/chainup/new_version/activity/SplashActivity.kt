package com.yjkj.chainup.new_version.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.util.LogUtil
import com.yjkj.chainup.util.permissionIsGranted

class SplashActivity : NBaseActivity() {

    override fun setContentView(): Int {
        return R.layout.activity_splash
    }

    companion object {
        const val PERMISSION_REQUEST_CODE_STORAGE: Int = 101
        val REQUEST_PERMISSIONS = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)

        if (!this.isTaskRoot) {
            if (intent?.action != null) {
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.action)) {
                    finish()
                    return
                }
            }
        }
        if (hasPermission()) {
            goHome()
        } else {
            requestPermission()
        }
    }

    fun goHome() {
        startActivity(Intent(this@SplashActivity, NewMainActivity::class.java))//
        finish()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (PERMISSION_REQUEST_CODE_STORAGE == requestCode) {
            if (permissions.isNotEmpty() && grantResults.permissionIsGranted()) {
                goHome()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun hasPermission(): Boolean {
        Log.d(TAG, "hasPermission")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private fun requestPermission() {
        Log.d(TAG, "requestPermission")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(REQUEST_PERMISSIONS, PERMISSION_REQUEST_CODE_STORAGE)
        }
    }


    override fun onBackPressed() {
        return
        super.onBackPressed()
    }

    override fun onMessageEvent(event: MessageEvent) {
        super.onMessageEvent(event)
    }


    override fun onMessageStickyEvent(event: MessageEvent) {
        super.onMessageStickyEvent(event)
    }
}
