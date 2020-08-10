package com.yjkj.chainup.new_version.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import butterknife.ButterKnife
import com.jaeger.library.StatusBarUtil
import com.yjkj.chainup.R
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.util.DisplayUtil
import com.yjkj.chainup.util.LocalManageUtil
import com.yjkj.chainup.util.NToastUtil
import com.yjkj.chainup.util.ToastUtils
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.Method

open class NewBaseActivity : AppCompatActivity() {
    val TAG = this::class.java.simpleName
    lateinit var context: Context
    private var mProgressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        if (Build.VERSION.SDK_INT >= 26) {
            convertActivityFromTranslucent(this)
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        /**
         * 这里 暂时处理把状态栏颜色调成黑色
         * setDarkMode 设置成白色
         */
        StatusBarUtil.setLightMode(this)
        ButterKnife.bind(this)
    }

    /**
     * 设置状态栏的颜色
     * @param 0 是 白天模式，状态栏是白底黑字  1是夜间模式 状态栏是黑底白字
     */
    fun setBarColor(index: Int) {
        when (index) {
            0 -> {
                StatusBarUtil.setLightMode(this)
            }
            1 -> {
                StatusBarUtil.setDarkMode(this)
            }
        }
    }

    private fun transparentStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            activity.window.statusBarColor = Color.TRANSPARENT
        } else {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }


    /**
     * 处理 8.0以上系统 强制竖屏crash问题
     */
    fun convertActivityFromTranslucent(activity: Activity) {
        try {
            var method: Method = Activity::class.java.getDeclaredMethod("convertFromTranslucent")
            method.isAccessible = true
            method.invoke(activity)
        } catch (t: Throwable) {
        }
    }

    fun printLogcat(msg:String?) {
        if(!TextUtils.isEmpty(msg)){
            Log.d(TAG,msg)
        }
    }

    fun showToast(msg: String) {
        if (!TextUtils.isEmpty(msg)) {
            ToastUtils.showToast(msg)
        }
    }

    var listener: TitleShowListener? = null

    fun showProgressDialog(msg: String = "") {
        var msg = msg
        if (isFinishing || isDestroyed) {
            return
        }
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this, R.style.progressDialog)
            mProgressDialog?.setCancelable(true)
        }

        val progressLayout = LayoutInflater.from(baseContext).inflate(R.layout.ly_progress_dialog, null)
        val ivProgress = progressLayout.findViewById<ImageView>(R.id.iv_progress)

        val animation = AnimationUtils.loadAnimation(baseContext, R.anim.anim_progress)
        progressLayout.startAnimation(animation)


        if (TextUtils.isEmpty(msg)) {
            msg = LanguageUtil.getString(this,"common_text_refreshing")
        }
        mProgressDialog?.setMessage(msg)
        mProgressDialog?.show()
        mProgressDialog?.setContentView(progressLayout)
    }


    fun cancelProgressDialog() {
        if (!isDestroyed && !isFinishing && mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog?.cancel()
            mProgressDialog?.dismiss()
        }
    }


    fun showSnackBar(msg: String?, isSuc: Boolean = true) {
        NToastUtil.showTopToast(isSuc, msg)
    }


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase))
    }


    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }

        cancelProgressDialog()
    }

    var y1 = 0f
    var y2 = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            y1 = event.y
        }

        if (event?.action == MotionEvent.ACTION_MOVE) {
            y2 = event.y
            if (y1 - y2 > 50) {
                if (listener != null) {
                    listener?.TopAndBottom(false)
                }
            } else if (y2 - y1 > 50) {
                if (listener != null) {
                    listener?.TopAndBottom(true)
                }
            }
        }
        return super.onTouchEvent(event)
    }

}

