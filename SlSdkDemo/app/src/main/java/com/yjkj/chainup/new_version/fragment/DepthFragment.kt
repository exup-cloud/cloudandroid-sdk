package com.yjkj.chainup.new_version.fragment

import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yjkj.chainup.R
import com.yjkj.chainup.new_version.bean.FlagBean
import com.yjkj.chainup.new_version.home.MyWebSocketManager
import com.yjkj.chainup.wedegit.WrapContentViewPager


/**
 * @author Bertking
 * @description 市场详情下的"深度"
 * @date 2019-3-20
 *
 */

class DepthFragment : Fragment(){



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_depth, container, false)

        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(viewPager: WrapContentViewPager) =
                DepthFragment().apply {

                }
    }


}

