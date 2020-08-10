package com.yjkj.chainup.new_version.activity.leverage


import android.arch.lifecycle.MutableLiveData
import android.support.v4.app.Fragment
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.extra_service.eventbus.MessageEvent


/**
 * A simple [Fragment] subclass.
 * Use the [TradeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TradeFragment : NBaseFragment() {
    override fun initView() {

    }

    companion object {
        var currentIndex = ParamConstant.CVC_INDEX_TAB
        var liveData4DepthData = MutableLiveData<MessageEvent>()
    }
    override fun setContentView() = R.layout.fragment_trade


}
