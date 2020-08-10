package com.yjkj.chainup.new_version.activity.personalCenter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.follow.order.FollowOrderSDK
import com.follow.order.constant.FOTheme
import com.timmy.tdialog.TDialog
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.db.service.ColorDataService
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.extra_service.eventbus.NLiveDataUtil
import com.yjkj.chainup.manager.ChainUpManager
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.net.HttpClient
import com.yjkj.chainup.net.retrofit.NetObserver
import com.yjkj.chainup.new_version.activity.NewBaseActivity
import com.yjkj.chainup.new_version.activity.TitleShowListener
import com.yjkj.chainup.new_version.activity.personalCenter.push.PushSettingsActivity
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.view.CommonlyUsedButton
import com.yjkj.chainup.util.ColorUtil
import com.yjkj.chainup.util.DisplayUtil
import com.yjkj.chainup.util.visiableOrGone
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_new_setting.*
import android.net.Uri
import com.yjkj.chainup.R
import java.io.File


/**
 * @Author lianshangljl
 * @Date 2019/3/27-5:35 PM
 * @Email buptjinlong@163.com
 * @description 设置页面
 */
class NewSettingActivity : NewBaseActivity() {
    // 绿涨
    var global = ""
    // 红涨
    var china = ""

    // 白天版
    private var themeDay = ""
    // 夜间版
    private var themeNight = ""

    var riseAndFallDialog: TDialog? = null
    var setSkinTDialog: TDialog? = null
    var setLogTDialog: TDialog? = null

    companion object {
        fun enter2(context: Context) {
            var intent = Intent()
            intent.setClass(context, NewSettingActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_setting)
        global = LanguageUtil.getString(this, "customSetting_action_global")
        china = LanguageUtil.getString(this, "customSetting_action_china")
        themeDay = LanguageUtil.getString(this, "customSetting_action_themeDay")
        themeNight = LanguageUtil.getString(this, "customSetting_action_themeNight")
        title_layout?.setContentTitle(LanguageUtil.getString(this, "personal_text_setting"))
        setOnClick()

    }

    override fun onResume() {
        super.onResume()
        initView()
    }


    fun initView() {
        var language = ""
        var languageList = PublicInfoDataService.getInstance().lanList
        for (i in 0 until languageList?.size!!) {
            if (languageList[i].optString("id") == LanguageUtil.getSelectLanguage()) {
                language = languageList[i].optString("name")
            }
        }
        aiv_change_language?.setTitle(LanguageUtil.getString(this, "customSetting_action_language"))
        aiv_rise_and_fall_color?.setTitle(LanguageUtil.getString(this, "customSetting_action_kline"))
        aiv_skin_is_set?.setTitle(LanguageUtil.getString(this, "customSetting_action_theme"))
        login_out?.setBottomTextContent(LanguageUtil.getString(this, "common_text_logout"))
        aiv_change_language.setStatusText(language)

        if (ColorUtil.getColorType() == 0) {
            aiv_rise_and_fall_color?.setStatusText(global)
        } else {
            aiv_rise_and_fall_color?.setStatusText(china)
        }
        if (PublicInfoDataService.getInstance().themeMode == 0) {
            aiv_skin_is_set?.setStatusText(themeDay)
        } else {
            aiv_skin_is_set?.setStatusText(themeNight)
        }
    }

    fun setOnClick() {
        listener = object : TitleShowListener {
            override fun TopAndBottom(status: Boolean) {
                title_layout?.slidingShowTitle(status)
            }

        }
        if (UserDataService.getInstance().isLogined) {
            login_out?.visibility = View.VISIBLE
        } else {
            login_out?.visibility = View.GONE
        }
        val isPush = PublicInfoDataService.getInstance().getPushStatus(null)
        aiv_push.visibility = isPush.visiableOrGone()
        /**
         * 设置语言
         */
        aiv_change_language?.setOnClickListener {
            OTCChooseLanguageActivity.newIntent(this)
        }
        aiv_push?.setOnClickListener {
            if (!LoginManager.checkLogin(this, true)) {
                return@setOnClickListener
            }
            startActivity(Intent(context, PushSettingsActivity::class.java))
        }
        /**
         * 切换网络
         */
        aiv_change_newwork?.setOnClickListener {
            ArouterUtil.navigation(RoutePath.ChangenNetworkActivity, null)
        }

        /**
         * 涨幅颜色
         */
        aiv_rise_and_fall_color?.setOnClickListener {

            riseAndFallDialog = NewDialogUtils.showBottomListDialog(this, arrayListOf(global, china), ColorUtil.getColorType(), object : NewDialogUtils.DialogOnclickListener {
                override fun clickItem(data: ArrayList<String>, item: Int) {
                    ColorDataService.getInstance().colorType = item
                    if (item == 0) {
                        aiv_rise_and_fall_color?.setStatusText(global)
                    } else {
                        aiv_rise_and_fall_color?.setStatusText(china)
                    }
                    riseAndFallDialog?.dismiss()

                    var messageEvent = MessageEvent(MessageEvent.color_rise_fall_type)
                    NLiveDataUtil.postValue(messageEvent)
                }

            })
        }

        /**
         * 设置皮肤颜色
         */
        aiv_skin_is_set?.setOnClickListener {
            setSkinTDialog = NewDialogUtils.showBottomListDialog(this, arrayListOf(themeDay, themeNight), PublicInfoDataService.getInstance().themeMode, object : NewDialogUtils.DialogOnclickListener {
                override fun clickItem(data: ArrayList<String>, item: Int) {
                    PublicInfoDataService.getInstance().themeMode = item
                    setSkinTDialog?.dismiss()
                    if (item == 0) {
                        aiv_skin_is_set?.setStatusText(themeDay)
                        FollowOrderSDK.ins().setTheme(FOTheme.LIGHT)
                    } else {
                        aiv_skin_is_set?.setStatusText(themeNight)
                        FollowOrderSDK.ins().setTheme(FOTheme.DARK)
                    }
                    /**
                     * 这里是设置白天还是晚上
                     * @param item 0 白天版 1夜间版
                     *          * 这里只是改变了状态栏还没有
                     */
                    setBarColor(item)
                    // recreate()

                    ArouterUtil.greenChannelWithFlags(RoutePath.NewMainActivity, null)
                    finish()
                }
            })
        }
        val localShare = LanguageUtil.getString(this, "customSetting_action_log_system")
        val networkShare = LanguageUtil.getString(this, "customSetting_action_log_network")
        aiv_log_upload?.setOnClickListener {
            setLogTDialog = NewDialogUtils.showBottomListDialog(this, arrayListOf(localShare, networkShare), 0, object : NewDialogUtils.DialogOnclickListener {
                override fun clickItem(data: ArrayList<String>, item: Int) {
                    if (item == 0) {
                        ChainUpManager.instance.updateLocalLogShare().subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    setLogTDialog?.dismiss()
                                    Log.e("LogUtils", "it ${it}")
                                    val intent = Intent(Intent.ACTION_SEND)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    intent.putExtra(Intent.EXTRA_STREAM, it)  //传输图片或者文件 采用流的方式
                                    intent.type = "*/*"   //分享文件
                                    context.startActivity(Intent.createChooser(intent, LanguageUtil.getString(this@NewSettingActivity, "contract_share_label")))
                                })
                    } else {
                        showProgressDialog()
                        ChainUpManager.instance.updateHttpStatus().subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    cancelProgressDialog()
                                    Log.e("LogUtils", "it ${it}")
                                    setLogTDialog?.dismiss()
                                    if (it) {
                                        DisplayUtil.showSnackBar(window?.decorView, LanguageUtil.getString(this@NewSettingActivity, "toast_trade_success"), isSuc = it)
                                    }
                                }, {
                                    it.printStackTrace()
                                    cancelProgressDialog()
                                    setLogTDialog?.dismiss()
                                    DisplayUtil.showSnackBar(window?.decorView, LanguageUtil.getString(this@NewSettingActivity, "otc_uplaod_error"), isSuc = false)
                                })
                    }
                }
            })
        }

        /**
         * 退出
         */
        login_out?.isEnable(true)
        login_out?.listener = object : CommonlyUsedButton.OnBottonListener {
            override fun bottonOnClick() {
                showLogoutDialog()
            }

        }
    }

    /**
     * 退出登录的Dialog
     */
    fun showLogoutDialog() {
        NewDialogUtils.showNormalDialog(this, LanguageUtil.getString(this, "common_tip_logoutDesc"), object : NewDialogUtils.DialogBottomListener {
            override fun sendConfirm() {
                logout()
            }
        })
    }

    /**
     * 退出登录
     */
    fun logout() {
        showProgressDialog()
        HttpClient.instance.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : NetObserver<Any>() {
                    override fun onHandleSuccess(t: Any?) {
                        cancelProgressDialog()
                        DisplayUtil.showSnackBar(window?.decorView, LanguageUtil.getString(this@NewSettingActivity, "common_action_logout"), isSuc = true)
                        UserDataService.getInstance().clearToken()
                        LoginManager.postValue(false)
                        finish()
                    }

                    override fun onHandleError(code: Int, msg: String?) {
                        super.onHandleError(code, msg)
                        cancelProgressDialog()
                        DisplayUtil.showSnackBar(window?.decorView, msg, isSuc = false)
                    }
                }
                )

    }

}