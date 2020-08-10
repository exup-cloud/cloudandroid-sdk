package com.yjkj.chainup.new_version.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yjkj.chainup.R
import com.yjkj.chainup.wedegit.WrapContentViewPager


/**
 * @author Bertking
 * @description 市场详情下的"成交记录"
 * @date 2019-3-19
 * DONE
 */
class DealtRecordFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dealt_record, container, false)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(viewPager: WrapContentViewPager) =
                DealtRecordFragment().apply {

                }
    }


}
