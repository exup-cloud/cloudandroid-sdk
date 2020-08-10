package com.yjkj.chainup.new_version.activity.login

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import kotlinx.android.synthetic.main.activity_guide_gesture_pwd.*
import org.json.JSONObject

/**
 * @date 2018-11-13
 * @author Bertking
 * @description 手势引导页
 */
@Route(path = "/login/guidegesturepwdactivity")
class GuideGesturePwdActivity : NBaseActivity() {
    override fun setContentView(): Int {
        return R.layout.activity_guide_gesture_pwd
    }

    var type = 1
    var pwd = ""
    var newhandPwd = ""

    companion object {
        const val GUIDEGESTURETYPE = "guidegesturetype"
        const val GUIDEGESTUREPWD = "guidegesturepwd"
        const val GUIDEGESTUREHANDPWD = "guidegesturehandpwd"
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initView()
        loadData()
    }

    override fun loadData() {
    }

    override fun initView() {
        getData()
        initClicker()
        setTextConetnt()
    }
    fun setTextConetnt(){
        tv_guide_tips?.text = LanguageUtil.getString(this,"login_tip_fingerprint")
        btn_now_set?.text = LanguageUtil.getString(this,"safety_action_activeFaceId")
        btn_cancel?.text = LanguageUtil.getString(this,"safety_action_faceIdNextTime")
    }


    fun getData() {
        if (intent != null) {
            type = intent.getIntExtra(GUIDEGESTURETYPE, 1)
            pwd = intent.getStringExtra(GUIDEGESTUREPWD)
            newhandPwd = intent.getStringExtra(GUIDEGESTUREHANDPWD)
        }
    }

    fun initClicker() {
        when (type) {
            1 -> {
                /**
                 * 手势密码
                 */
                tv_guide_title?.text =  LanguageUtil.getString(mActivity,"set_gesture_pwd_title")
                iv_guide?.setImageResource(R.drawable.ic_guide_gesture)
                btn_now_set?.setOnClickListener {
                    getToken4Pwd()
                }

            }

            2 -> {
                /**
                 * 指纹密码
                 */
                tv_guide_title?.text =  LanguageUtil.getString(mActivity,"login_text_fingerprint")
                iv_guide?.setImageResource(R.drawable.login_fingerprintlogin)

                btn_now_set?.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putInt("type", TouchIDFaceIDActivity.FINGERPRINT)
                    bundle.putBoolean("is_first_login", true)
                    ArouterUtil.navigation("/login/touchidfaceidactivity", bundle)
                    finish()
                }
            }

            3 -> {
                /**
                 * 人脸识别
                 */
                tv_guide_title?.text =  LanguageUtil.getString(mActivity,"faceID_login")
                iv_guide?.setImageResource(R.drawable.ic_guide_face)

                btn_now_set?.setOnClickListener {
                    // TODO
                }
            }
        }


        btn_cancel.setOnClickListener {
            finish()
        }
    }


    /**
     * 设置手势密码第一步 获取token
     */
    fun getToken4Pwd() {
        addDisposable(getMainModel().checkLocalPwd(UserDataService.getInstance().userInfo4UserId, pwd, object : NDisposableObserver(this,true) {
            override fun onResponseSuccess(jsonObject: JSONObject) {
                var json = jsonObject.optJSONObject("data")
                if (json?.has("isPass")!!) {
                    val isPass = json.optInt("isPass")
                    if (isPass == 1) {
                        val token = json.optString("token")

                        var bundle = Bundle()
                        bundle.putInt("SET_TYPE", 0)
                        bundle.putString("SET_TOKEN", token)
                        bundle.putBoolean("SET_STATUS", true)
                        bundle.putBoolean("SET_LOGINANDSET", false)
                        ArouterUtil.navigation("/login/gesturespasswordactivity", bundle)

                        finish()
                    }
                }
            }

            override fun onResponseFailure(code: Int, msg: String?) {
                super.onResponseFailure(code, msg)
                ArouterUtil.greenChannel(RoutePath.NewVersionLoginActivity, null)
            }
        }))
    }


}
