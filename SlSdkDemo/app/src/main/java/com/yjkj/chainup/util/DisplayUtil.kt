package com.yjkj.chainup.util

import android.content.Context
import android.view.View
import com.yjkj.chainup.app.ChainUpApp
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import org.jetbrains.anko.dip
import org.jetbrains.anko.sp
import org.json.JSONObject

/**
 * @Author: Bertking
 * @Date：2019/3/6-11:53 AM
 * @Description: 获取屏幕信息的工具类
 * <p>至于 dp & sp的互转，Anko 库已经内置dip() & sp()  </p>
 * <link>https://github.com/Kotlin/anko/wiki/Anko-Commons-%E2%80%93-Misc#dimensions</link>
 */

object DisplayUtil {
    /**
     * @return 屏幕宽度
     */
    fun getScreenWidth(context: Context = ChainUpApp.appContext): Int = context.resources.displayMetrics.widthPixels

    /**
     * @return 屏幕高度
     */
    fun getScreenHeight(context: Context = ChainUpApp.appContext): Int = context.resources.displayMetrics.heightPixels

    /**
     * @return 分辨率
     */
    fun getDisplayDensity(context: Context = ChainUpApp.appContext): Float = context.resources.displayMetrics.density


    /**
     * @param view
     * @param text
     * @param isSuc 是否是成功的状态
     */
    fun showSnackBar(view: View?, text: String?, isSuc: Boolean = true) {

        NToastUtil.showTopToast(isSuc, text)
    }

    fun dip2px(int: Int): Int {
        return ChainUpApp.appContext.dip(int)
    }


    fun sp2px(int: Int): Int {
        return ChainUpApp.appContext.sp(int)
    }


    fun dip2px(float: Float): Float {
        return ChainUpApp.appContext.dip(float).toFloat()
    }


    fun sp2px(float: Float): Float {
        return ChainUpApp.appContext.sp(float).toFloat()
    }


    fun getCerificationStatus(context: Context, beans: ArrayList<JSONObject>): Boolean {
        if (UserDataService.getInstance().googleStatus == 1) {
            if (UserDataService.getInstance().nickName.isEmpty() || UserDataService.getInstance().authLevel != 1 || UserDataService.getInstance().googleStatus != 1) {
                NewDialogUtils.OTCTradingMustPermissionsDialog(context, object : NewDialogUtils.DialogBottomListener {
                    override fun sendConfirm() {
                        if (UserDataService.getInstance().nickName.isEmpty()) {
                            //认证状态 0、审核中，1、通过，2、未通过  3未认证
                            ArouterUtil.navigation(RoutePath.PersonalInfoActivity, null)
                        } else if (UserDataService.getInstance().authLevel != 1) {
                            when (UserDataService.getInstance().authLevel) {
                                0 -> {
                                    ArouterUtil.navigation(RoutePath.RealNameCertificaionSuccessActivity, null)
                                }

                                2, 3 -> {
                                    ArouterUtil.navigation(RoutePath.RealNameCertificationActivity, null)
                                }
                            }
                        } else {
                            ArouterUtil.navigation(RoutePath.SafetySettingActivity, null)
                        }

                    }
                })
                return true
            } else if (UserDataService.getInstance().isCapitalPwordSet != 1 || beans?.size == 0) {
                NewDialogUtils.OTCTradingSecurityDialog(context!!, object : NewDialogUtils.DialogBottomListener {
                    override fun sendConfirm() {
                        if (UserDataService.getInstance().isCapitalPwordSet != 1) {
                            ArouterUtil.navigation(RoutePath.SafetySettingActivity, null)
                        } else {
                            ArouterUtil.navigation(RoutePath.PaymentMethodActivity, null)

                        }

                    }
                }, beans?.size != 0)
                return true
            }
        } else {
            if (UserDataService.getInstance().nickName.isEmpty() || UserDataService.getInstance().authLevel != 1 || (UserDataService.getInstance().isOpenMobileCheck != 1 && UserDataService.getInstance().googleStatus != 1)) {
                NewDialogUtils.OTCTradingPermissionsDialog(context, object : NewDialogUtils.DialogBottomListener {
                    override fun sendConfirm() {
                        if (UserDataService.getInstance().nickName.isEmpty()) {
                            //认证状态 0、审核中，1、通过，2、未通过  3未认证
                            ArouterUtil.navigation(RoutePath.PersonalInfoActivity, null)
                        } else if (UserDataService.getInstance().authLevel != 1) {
                            when (UserDataService.getInstance().authLevel) {
                                0 -> {
                                    ArouterUtil.navigation(RoutePath.RealNameCertificaionSuccessActivity, null)

                                }

                                2, 3 -> {
                                    ArouterUtil.navigation(RoutePath.RealNameCertificationActivity, null)
                                }
                            }
                        } else {
                            ArouterUtil.navigation(RoutePath.SafetySettingActivity, null)

                        }
                    }
                })
                return true
            } else if (UserDataService.getInstance().isCapitalPwordSet != 1 || beans?.size == 0) {
                NewDialogUtils.OTCTradingSecurityDialog(context!!, object : NewDialogUtils.DialogBottomListener {
                    override fun sendConfirm() {
                        if (UserDataService.getInstance().isCapitalPwordSet != 1) {

                            ArouterUtil.navigation(RoutePath.SafetySettingActivity, null)

                        } else {
                            ArouterUtil.navigation(RoutePath.PaymentMethodActivity, null)
                        }

                    }
                }, beans?.size != 0)
                return true
            }
        }
        return false
    }

}