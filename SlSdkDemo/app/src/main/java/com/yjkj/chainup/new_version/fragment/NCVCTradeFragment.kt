package com.yjkj.chainup.new_version.fragment


import android.support.v4.app.Fragment
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.db.constant.ParamConstant

/**
 * A simple [Fragment] subclass.
 */
class NCVCTradeFragment : NBaseFragment(){
    override fun initView() {

    }
    companion object {
        var curDepthIndex = 0
        var tradeOrientation = ParamConstant.TYPE_BUY
        var tapeLevel = 0
    }


    override fun setContentView() = R.layout.fragment_cvctrade

}
