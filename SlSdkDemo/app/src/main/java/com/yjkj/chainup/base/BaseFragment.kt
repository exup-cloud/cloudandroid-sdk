package com.yjkj.chainup.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * @Author: Bertking
 * @Date：2019-06-15-10:48
 * @Description:
 */
abstract class BaseFragment : Fragment() {
    val TAG = this::class.java.simpleName

    var rootView: View? = null
    abstract fun setLayoutId(): Int

    abstract fun initData()

    abstract fun initView()


    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG,"======onViewCreated=======")
        initView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(setLayoutId(), container, false)
        Log.d(TAG, "======rootView:${rootView == null}====")
        return rootView
    }


    override fun onDestroy() {
        super.onDestroy()
    }

}