package com.yjkj.chainup.new_version.activity.personalCenter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.yjkj.chainup.R
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.new_version.activity.NewBaseActivity


/**
 * @Author lianshangljl
 * @Date 2019/4/24-9:49 AM
 * @Email buptjinlong@163.com
 * @description 实名制认证上传图片
 */
class RealNameCertificaionDownloadImgActivity : NewBaseActivity() {

    companion object {
        const val IDCARD = 1 // 身份证
        const val PASSPORT = 2 // 护照
        const val ORHERID = 3 // 其他
        const val DRIVERLICENSE = 4 // 其他

        /**
         * 作为标记照片位置
         */
        const val FIRST_INDEX = 0 // 第一张照片
        const val SECOND_INDEX = 1 // 第二张照片
        const val THIRD_INDEX = 2// 第三张照片

        const val CREDENTIALS_TYPE = 1//证件
        const val PHOTO_TYPE = 2//图片选择
        const val AREA_INFO = "area_info"//国家号 + 国家码
        const val REAL_NAME = "real_name"//名字
        const val SURNAME_NAME = "surname_name"//名
        const val FRISTNAME_NAME = "fristName_name"//姓
        const val CERT_NUM = "cert_num"//身份证号
        const val CREDENTIALSTYPE = "credentials_type"//姓

        /**
         * 中国
         */
        fun enter2(context: Context, areaInfo: String, certNum: String, realName: String, credentials_type: Int, areaCode: String) {
            var intent = Intent()
            intent.setClass(context, RealNameCertificaionDownloadImgActivity::class.java)
            intent.putExtra(AREA_INFO, areaInfo)
            intent.putExtra(REAL_NAME, realName)
            intent.putExtra(SURNAME_NAME, "")
            intent.putExtra(FRISTNAME_NAME, "")
            intent.putExtra(ParamConstant.AREA_CODE, areaCode)
            intent.putExtra(CREDENTIALSTYPE, credentials_type)
            intent.putExtra(CERT_NUM, certNum)
            context.startActivity(intent)
        }

        /**
         * 非中国
         */
        fun enter2(context: Context, areaInfo: String, certNum: String, surname: String, fristName: String, credentials_type: Int, areaCode: String) {
            var intent = Intent()
            intent.setClass(context, RealNameCertificaionDownloadImgActivity::class.java)
            intent.putExtra(AREA_INFO, areaInfo)
            intent.putExtra(REAL_NAME, "")
            intent.putExtra(SURNAME_NAME, surname)
            intent.putExtra(FRISTNAME_NAME, fristName)
            intent.putExtra(ParamConstant.AREA_CODE, areaCode)
            intent.putExtra(CREDENTIALSTYPE, credentials_type)
            intent.putExtra(CERT_NUM, certNum)
            context.startActivity(intent)
        }
    }

    /**
     * 证件类型
     * 默认：身份证
     */
    var credentials_type: Int = IDCARD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_name_certification_download_img)

    }



}