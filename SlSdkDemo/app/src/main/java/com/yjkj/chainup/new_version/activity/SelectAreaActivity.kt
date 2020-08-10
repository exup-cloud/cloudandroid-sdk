package com.yjkj.chainup.new_version.activity

import android.os.Bundle
import com.yjkj.chainup.R

class SelectAreaActivity : NewBaseActivity() {
    companion object {
        val CHOOSE_COUNTRY_CODE = 111
        val COUNTRYCODE = "countryCode"
        val AREA = "area"
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_area)

    }


}


