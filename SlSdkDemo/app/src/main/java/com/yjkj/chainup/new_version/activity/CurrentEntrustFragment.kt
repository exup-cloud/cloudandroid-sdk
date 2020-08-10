package com.yjkj.chainup.new_version.activity

import android.os.Bundle
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.db.constant.ParamConstant

/**
 * @Author: Bertking
 * @Date：2019/4/4-10:26 AM
 * @Description: 所有当前委托(币币)
 */

class CurrentEntrustFragment : NBaseFragment(){



    companion object {
        @JvmStatic
        fun newInstance(orderType: String) =
                CurrentEntrustFragment().apply {
                    arguments = Bundle().apply {
                        putString(ParamConstant.TYPE, orderType)
                    }
                }
    }

    override fun initView() {
        TODO("Not yet implemented")
    }


    override fun setContentView() = R.layout.activity_current_entrust


}
