package com.yjkj.chainup.new_version.activity.login


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.timmy.tdialog.TDialog
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.view.CommonlyUsedButton
import com.yjkj.chainup.new_version.view.Gt3GeeListener
import com.yjkj.chainup.new_version.view.PwdSettingView
import com.yjkj.chainup.util.*
import kotlinx.android.synthetic.main.activity_new_version_login.*
import org.json.JSONObject

/**
 * @Author lianshangljl
 * @Date 2019/3/9-3:37 PM
 * @Email buptjinlong@163.com
 * @description 登录
 */
@Route(path = RoutePath.NewVersionLoginActivity)
class NewVersionLoginActivity : NBaseActivity(), Gt3GeeListener {
    override fun setContentView() = R.layout.activity_new_version_login

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initView()
    }


    var list: ArrayList<String> = arrayListOf()
    /**
     * 密码
     */
    var pwdTextContent = ""
    /**
     * 账号
     */
    var accountContent = ""
    /**
     * 极验
     */
    var gee3test = arrayListOf<String>()


    /**
     * 安全验证
     *  0是谷歌
     *  1是手机验证
     *  2是邮箱验证
     */
    var securityVerification = ParamConstant.LOGIN_PHONE

    override fun initView() {
        setListener()
        list.add(LanguageUtil.getString(mActivity, "safety_text_googleAuth"))
        list.add(LanguageUtil.getString(mActivity, "safety_text_phoneAuth"))
        list.add(LanguageUtil.getString(mActivity, "safety_text_mailAuth"))

        var logoBeanLogos = PublicInfoDataService.getInstance().getApp_logo_list_new(null)

        if (logoBeanLogos != null && logoBeanLogos?.size > 0) {
            var logo_black = logoBeanLogos[0]
            var logo_white = ""
            if (logoBeanLogos.size > 1) {
                logo_white = logoBeanLogos[1]
            }
            if (PublicInfoDataService.getInstance().themeMode == 0) {

                if (StringUtil.checkStr(logo_white)) {
                    GlideUtils.loadImageHeader(this, logo_white, app_logo)
                }
            } else {
                if (StringUtil.checkStr(logo_black)) {
                    GlideUtils.loadImageHeader(this, logo_black, app_logo)
                }
            }
        }


        security_view?.setSelect(securityVerification)
        ce_account?.isFocusable = true
        ce_account?.isFocusableInTouchMode = true
        cbtn_view?.isEnable(false)
        ce_account?.setOnFocusChangeListener { v, hasFocus ->
            v_line?.setBackgroundResource(if (hasFocus) R.color.main_blue else R.color.new_edit_line_color)
        }
        ce_account?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                accountContent = s.toString().trim()
                if (pwdTextContent.isNotEmpty() && accountContent.isNotEmpty()) {
                    cbtn_view?.isEnable(true)
                } else {
                    cbtn_view?.isEnable(false)
                }

            }

        })
        setTextContent()
    }


    fun setTextContent() {
        tv_cancel?.text = LanguageUtil.getString(this, "common_text_btnCancel")
        tv_forget_pwd?.text = LanguageUtil.getString(this, "login_action_fogotPassword")
        tv_account_info?.text = LanguageUtil.getString(this, "login_action_noAccount")
        tv_to_register?.text = LanguageUtil.getString(this, "login_action_register")
        ce_account?.hint = LanguageUtil.getString(this, "login_text_phoneOrMail")
        pws_view?.setHintEditText(LanguageUtil.getString(this, "login_text_pwd"))
        security_view?.setHintEditText(LanguageUtil.getString(this, "safety_text_phoneAuth"))
        cbtn_view?.setBottomTextContent(LanguageUtil.getString(this, "login_action_login"))
    }

    var dialog: TDialog? = null
    /**
     *
     */
    var tDialog: TDialog? = null

    fun setListener() {

        pws_view?.setListener(object : PwdSettingView.OnTextListener {
            override fun onclickImage() {

            }

            override fun returnItem(item: Int) {

            }

            override fun showText(text: String): String {
                pwdTextContent = text
                if (pwdTextContent.isNotEmpty() && accountContent.isNotEmpty()) {
                    cbtn_view?.isEnable(true)
                } else {
                    cbtn_view?.isEnable(false)
                }
                return text
            }
        })

        security_view?.setListener(object : PwdSettingView.OnTextListener, NewDialogUtils.DialogOnclickListener {
            override fun clickItem(data: ArrayList<String>, item: Int) {
                securityVerification = item
                security_view?.setEditText(list[item])
                tDialog?.dismiss()
            }

            override fun onclickImage() {
                tDialog = NewDialogUtils.showBottomListDialog(this@NewVersionLoginActivity, list, securityVerification, this)
            }

            override fun showText(text: String): String {
                return text
            }

            override fun returnItem(item: Int) {
            }

        })


        tv_cancel?.setOnClickListener {
            KeyBoardUtils.closeKeyBoard(this)
            finish()
        }

        /**
         * 点击登录
         */
        cbtn_view?.listener = object : CommonlyUsedButton.OnBottonListener {
            override fun bottonOnClick() {

                LogUtil.d(TAG, "点击登录==verificationType is $verificationType")
                if (2 == verificationType) {
                    Utils.gee3test(this@NewVersionLoginActivity, this@NewVersionLoginActivity)
                } else {
                    login(accountContent, pwdTextContent)
                }
            }
        }
    }

    var verificationType = PublicInfoDataService.getInstance().getVerifyType(null)

    /**
     * 如果有极验 此处是极验成功返回的接口
     */
    override fun onSuccess(result: ArrayList<String>) {
        gee3test = result
        Utils.setGeetestDeatroy()
        login(accountContent, pwdTextContent, gee3test[0], gee3test[1], gee3test[2])
    }

    var token: String = ""
    /**
     * 登录
     */
    private fun login(mobile: String, password: String, geetest_challenge: String = "",
                      geetest_validate: String = "", geetest_seccode: String = "") {

        addDisposable(getMainModel().getLoginByMobile(mobile, password, geetest_challenge = geetest_challenge, geetest_validate = geetest_validate,
                geetest_seccode = geetest_seccode, consumer = object : NDisposableObserver(mActivity, true) {
            override fun onResponseSuccess(jsonObject: JSONObject) {

                LogUtil.d(TAG, "login==onResponseSuccess==jsonObject $jsonObject ")
                var data = jsonObject.optJSONObject("data")

                // 保存token
                token = data?.optString("token") ?: ""

                /**
                 * 保存登录信息
                 */
                val loginInfo = LoginManager.getInstance().loginInfo
                if (mobile != loginInfo.account) {
                    UserDataService.getInstance().saveGesturePass(null)
                    UserDataService.getInstance().saveData(JSONObject())
                    LoginManager.getInstance().saveFingerprint(0)
                }

                LoginManager.getInstance().saveLoginPwd(password)
                loginInfo.account = mobile
                loginInfo.loginPwd = password
                LoginManager.getInstance().saveLoginInfo(loginInfo)

                Log.d(TAG, "=====是否登录：======" + UserDataService.getInstance().isLogined)
                // {"code":"0","msg":"成功","data":{"type":"2","token":"39257f8399139a329ca6637f6c9f6474"}}
                Log.d("=== mobile login====", "登录成功" + data.toString())


                val typeList = data.optString("typeList") ?: ""
                if (typeList.isNotEmpty() && StringUtil.checkStr(typeList) && typeList.split(",").size <= 2) {
                    val googleAuth = data.optString("googleAuth") ?: "0"
                    nextPageLoginType(ParamConstant.LOGIN_GOOOGLE,typeList,googleAuth)
                } else {
                    /**
                     * 登录新逻辑
                     * 跳到验证码页面
                     */
                    /**
                     * 登录新逻辑
                     * 跳到验证码页面
                     */
                    val googleAuth = data.optString("googleAuth") ?: "0"
                    if (googleAuth == "1") {
                        nextPage(ParamConstant.LOGIN_GOOOGLE)
                    } else if (StringUtils.isNumeric(accountContent)) {
                        nextPage(ParamConstant.LOGIN_PHONE)
                    } else if (StringUtils.checkEmail(accountContent)) {
                        nextPage(ParamConstant.LOGIN_EMAIL)
                    }
                }
                finish()
            }
        }))


    }

    private fun nextPage(type: Int) {
        val bundle = Bundle()
        bundle.putString("send_account", accountContent)
        bundle.putString("send_token", token)
        bundle.putString("send_countryCode", "")
        bundle.putInt("send_position", type)
        bundle.putInt("send_islogin", 0)
        ArouterUtil.greenChannel("/login/newphoneverificationactivity", bundle)
    }

    private fun nextPageLoginType(type: Int, typeList: String? = "",googleAuth : String? = "") {
        val bundle = Bundle()
        bundle.putString("send_account", accountContent)
        bundle.putString("send_token", token)
        bundle.putString("send_countryCode", "")
        bundle.putInt("send_position", type)
        bundle.putInt("send_islogin", 0)
        bundle.putString("send_verifitionType", typeList)
        bundle.putString("send_googleAuth", googleAuth)
        ArouterUtil.greenChannel(RoutePath.NewVersionCodeActivity, bundle)
    }
}