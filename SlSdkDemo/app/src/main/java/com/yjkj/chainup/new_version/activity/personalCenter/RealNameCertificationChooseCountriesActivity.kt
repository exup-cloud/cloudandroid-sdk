package com.yjkj.chainup.new_version.activity.personalCenter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.yjkj.chainup.R
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.new_version.activity.NewBaseActivity

/**
 * @Author lianshangljl
 * @Date 2019-08-01-14:30
 * @Email buptjinlong@163.com
 * @description 新版本 实名制认证选择国家
 */

class RealNameCertificationChooseCountriesActivity : NewBaseActivity() {



    companion object {
        fun enter(context: Context, areaNum: String, areaCountry: String, areaCode: String) {
            var intent = Intent()
            intent.setClass(context, RealNameCertificationChooseCountriesActivity::class.java)
            intent.putExtra(ParamConstant.AREA_NUMBER, areaNum)
            intent.putExtra(ParamConstant.AREA_COUNTRY, areaCountry)
            intent.putExtra(ParamConstant.AREA_CODE, areaCode)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_name_certification)


    }



}