package com.yjkj.chainup.treaty

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.coorchice.library.SuperTextView
import com.yjkj.chainup.R
import kotlinx.android.synthetic.main.layout_treaty_risk_show.view.*

/**
 * @Author: Bertking
 * @Date：2019/1/15-11:00 AM
 * @Description:
 */
class ContactShowRiskView @JvmOverloads constructor(context: Context,
                                                    attrs: AttributeSet? = null,
                                                    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
val TAG:String = ContactShowRiskView::class.java.simpleName

     var riskFactor = 0f
        set(value) {
            field = value
            initView(viewList)
        }

    val blue = ContextCompat.getColor(context, R.color.main_blue)
    val outlineColor = ContextCompat.getColor(context, R.color.outline_color)

    var liveData: MutableLiveData<Float>

    var viewList = arrayListOf<SuperTextView?>()


    init {
        attrs?.let {
            var typedArray = context.obtainStyledAttributes(it, R.styleable.ContactShowRiskView, 0, 0)
            riskFactor = typedArray.getFloat(R.styleable.ContactShowRiskView_risk_factor, 0f)
            typedArray.recycle()
        }

        /**
         * 这里的必须为：True
         */
        LayoutInflater.from(context).inflate(R.layout.layout_treaty_risk_show, this, true)

        liveData = MutableLiveData()

         viewList =
                arrayListOf(
                        tv_risk_1st,
                        tv_risk_2nd,
                        tv_risk_3rd,
                        tv_risk_4th,
                        tv_risk_5th
                )



        viewList.forEach {
            it?.solid = outlineColor
        }

    }

    private fun initView(viewList: ArrayList<SuperTextView?>) {
        Log.d("Risk", "========5===" + riskFactor)
        when (riskFactor) {
            0.0f -> {
                Log.d(TAG,"=======1======")
                showRiskWarn(viewList, 0)
            }

            in 0.00000000000001f..20f -> {
                showRiskWarn(viewList, 1)
            }

            in 20f..40f -> {
                showRiskWarn(viewList, 2)
            }

            in 40f..60f -> {
                showRiskWarn(viewList, 3)
            }

            in 60f..80f -> {
                showRiskWarn(viewList, 4)
            }

            else -> {
                showRiskWarn(viewList, 5)
            }
        }
    }


    private fun showRiskWarn(viewList: ArrayList<SuperTextView?>, level: Int) {
        Log.d(TAG,"=======2======")
        viewList.forEach {
            it?.solid = outlineColor
            it?.strokeWidth = 0f
        }

        viewList.subList(0, level).forEach {
            Log.d(TAG,"=======3======")
            it?.solid = blue
            it?.strokeWidth = 0f
        }
    }

}
