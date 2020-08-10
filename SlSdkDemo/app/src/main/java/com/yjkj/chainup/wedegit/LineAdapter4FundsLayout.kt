package com.yjkj.chainup.wedegit

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.yjkj.chainup.R
import com.yjkj.chainup.new_version.adapter.*
import com.yjkj.chainup.new_version.bean.CashFlowSceneBean
import com.yjkj.chainup.treaty.bean.ContractSceneList
import com.yjkj.chainup.util.LineSelectOnclickListener
import com.yjkj.chainup.util.SizeUtils
import kotlinx.android.synthetic.main.item_line_adapter_fund.view.*
import org.json.JSONObject
import java.util.*

/**
 * @Author lianshangljl
 * @Date 2019/6/3-10:40 AM
 * @Email buptjinlong@163.com
 * @description
 */
class LineAdapter4FundsLayout @JvmOverloads constructor(context: Context,
                                                        attrs: AttributeSet? = null,
                                                        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val TAG = LineAdapter4FundsLayout::class.java.simpleName

    private var lineSelectOnclickListener: LineSelectOnclickListener? = null

    fun setLineSelectOncilckListener(lineSelectOncilckListener: LineSelectOnclickListener) {
        this.lineSelectOnclickListener = lineSelectOncilckListener
    }

    var countryAdapter: CountryNumberInfoAdapter? = null
    var paymentAdapter: PaymentBeanAdapter? = null
    var payCoinAdapter: PaycoinsAdapter? = null
    var lineSceneAdapter: LineSceneAdapter? = null
    var lineStringAdapter: LineStringAdapter? = null
    var contractBillAdapter: ContractBillAdapter? = null
    var normalAdapter: NormalAdapter? = null

    var selectPosition = 0


    init {
        LayoutInflater.from(context).inflate(R.layout.item_line_adapter_fund, this, true)
        recycler_view?.isNestedScrollingEnabled = false
    }

    var isfrist = true

    fun setCountryNumberInfoData(lables: ArrayList<JSONObject>, status: Boolean) {
        if (status) {
            if (lables.size > 3) {
                var beans: ArrayList<JSONObject> = arrayListOf()
                beans.add(lables[0])
                beans.add(lables[1])
                beans.add(lables[2])
                countryAdapter = CountryNumberInfoAdapter(beans, selectPosition)
            } else {
                countryAdapter = CountryNumberInfoAdapter(lables, selectPosition)
            }
        } else {
            countryAdapter = CountryNumberInfoAdapter(lables, selectPosition)
        }
        recycler_view?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycler_view?.layoutManager = GridLayoutManager(context, 3)
        recycler_view?.adapter = countryAdapter
        if (isfrist) {
            recycler_view?.addItemDecoration(ItemDecoration())
            (recycler_view?.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
            isfrist = false
        }

        countryAdapter?.bindToRecyclerView(recycler_view ?: return)
        recycler_view?.setHasFixedSize(true)
        countryAdapter?.setOnItemClickListener { adapter, view, position ->
            selectPosition = position
            if (lineSelectOnclickListener != null) {
                lineSelectOnclickListener?.selectMsgIndex(lables[position].optString("numberCode"))
            }
            countryAdapter?.setSelectPosition(position)
        }
    }


    fun setPaymentBeanData(lables: ArrayList<JSONObject>, status: Boolean) {
        if (status) {
            if (lables.size > 3) {
                var beans: ArrayList<JSONObject> = arrayListOf()
                beans.add(lables[0])
                beans.add(lables[1])
                beans.add(lables[2])
                paymentAdapter = PaymentBeanAdapter(beans, selectPosition)
            } else {
                paymentAdapter = PaymentBeanAdapter(lables, selectPosition)
            }
        } else {
            paymentAdapter = PaymentBeanAdapter(lables, selectPosition)
        }
        recycler_view?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycler_view?.layoutManager = GridLayoutManager(context, 3)
        recycler_view?.adapter = paymentAdapter
        if (isfrist) {
            recycler_view?.addItemDecoration(ItemDecoration())
            (recycler_view.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
            isfrist = false
        }
        paymentAdapter?.bindToRecyclerView(recycler_view ?: return)
        recycler_view?.setHasFixedSize(true)
        paymentAdapter?.setOnItemClickListener { adapter, view, position ->
            selectPosition = position
            if (lineSelectOnclickListener != null) {
                lineSelectOnclickListener?.selectMsgIndex(lables[position].optString("key"))
            }
            paymentAdapter?.setSelectPosition(position)
        }
    }

    var lablesForInit: ArrayList<JSONObject> = arrayListOf()

    fun setPaycoinsBeanData(lables: ArrayList<JSONObject>, status: Boolean, scrolling: Boolean = false) {
        lablesForInit = lables
        if (scrolling) {
            selectPosition = -1
        }
        if (status) {
            if (lables.size > 3) {
                var beans: ArrayList<JSONObject> = arrayListOf()
                beans.add(lables[0])
                beans.add(lables[1])
                beans.add(lables[2])
                payCoinAdapter = PaycoinsAdapter(beans, selectPosition)
            } else {
                payCoinAdapter = PaycoinsAdapter(lables, selectPosition)
            }
        } else {
            payCoinAdapter = PaycoinsAdapter(lables, selectPosition)
        }
        recycler_view?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycler_view?.layoutManager = GridLayoutManager(context, 3)
        recycler_view?.adapter = payCoinAdapter
        if (isfrist) {
            recycler_view?.addItemDecoration(ItemDecoration())
            (recycler_view?.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
            isfrist = false
        }
        payCoinAdapter?.bindToRecyclerView(recycler_view ?: return)
        if (scrolling) {
            recycler_view.isNestedScrollingEnabled = false
        }
        recycler_view?.setHasFixedSize(true)
        payCoinAdapter?.setOnItemClickListener { adapter, view, position ->
            selectPosition = position
            if (lineSelectOnclickListener != null) {
                lineSelectOnclickListener?.selectMsgIndex(lables[position].optString("key"))
            }
            if (lables[position].optString("key") != "1") {
                payCoinAdapter?.setSelectPosition(position)
            }

        }
    }


    /**
     * 暂时处理为提币页面
     */
    fun setNormalAdapter(lables: ArrayList<JSONObject>, index: Int) {
        lablesForInit = lables
        selectPosition = index
        normalAdapter = NormalAdapter(lables, selectPosition)
        recycler_view?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycler_view?.layoutManager = GridLayoutManager(context, 3)
        recycler_view?.adapter = normalAdapter
        if (isfrist) {
            recycler_view?.addItemDecoration(ItemDecoration())
            (recycler_view?.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
            isfrist = false
        }
        normalAdapter?.bindToRecyclerView(recycler_view ?: return)
        recycler_view?.isNestedScrollingEnabled = false
        recycler_view?.setHasFixedSize(true)
        normalAdapter?.setOnItemClickListener { adapter, view, position ->
            selectPosition = position
            if (lineSelectOnclickListener != null) {
                lineSelectOnclickListener?.selectMsgIndex(position.toString())
            }
            normalAdapter?.setSelectPosition(position)

        }
    }

    fun setSceneBeanData(lables: ArrayList<CashFlowSceneBean.Scene>, status: Boolean) {
        if (status) {
            if (lables.size > 3) {
                var beans: ArrayList<CashFlowSceneBean.Scene> = arrayListOf()
                beans.add(lables[0])
                beans.add(lables[1])
                beans.add(lables[2])
                lineSceneAdapter = LineSceneAdapter(beans, selectPosition)
            } else {
                lineSceneAdapter = LineSceneAdapter(lables, selectPosition)
            }
        } else {
            lineSceneAdapter = LineSceneAdapter(lables, selectPosition)
        }
//        recycler_view?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycler_view?.layoutManager = GridLayoutManager(context, 3)
        recycler_view?.adapter = lineSceneAdapter
        lineSceneAdapter?.notifyDataSetChanged()
        if (isfrist) {
            recycler_view?.addItemDecoration(ItemDecoration())
            (recycler_view?.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
            isfrist = false
        }

        lineSceneAdapter?.bindToRecyclerView(recycler_view ?: return)
        recycler_view?.setHasFixedSize(true)
        lineSceneAdapter?.setOnItemClickListener { adapter, view, position ->
            selectPosition = position
            if (lineSelectOnclickListener != null) {
                lineSelectOnclickListener?.selectMsgIndex(lables[position].key)
            }
            lineSceneAdapter?.setSelectPosition(position)
        }
    }

    fun setContractBillData(lables: ArrayList<ContractSceneList.ChildItem?>, status: Boolean) {
        if (status) {
            if (lables.size > 3) {
                var beans: ArrayList<ContractSceneList.ChildItem?> = arrayListOf()
                beans.add(lables[0])
                beans.add(lables[1])
                beans.add(lables[2])
                contractBillAdapter = ContractBillAdapter(beans, selectPosition)
            } else {
                contractBillAdapter = ContractBillAdapter(lables, selectPosition)
            }
        } else {
            contractBillAdapter = ContractBillAdapter(lables, selectPosition)
        }
//        recycler_view?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycler_view?.layoutManager = GridLayoutManager(context, 3)
        recycler_view?.adapter = contractBillAdapter
        contractBillAdapter?.notifyDataSetChanged()
        if (isfrist) {
            recycler_view?.addItemDecoration(ItemDecoration())
            (recycler_view?.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
            isfrist = false
        }

        contractBillAdapter?.bindToRecyclerView(recycler_view ?: return)
        recycler_view?.setHasFixedSize(true)
        contractBillAdapter?.setOnItemClickListener { adapter, view, position ->
            selectPosition = position
            if (lineSelectOnclickListener != null) {
                lineSelectOnclickListener?.selectMsgIndex(lables[position]?.item)
            }
            contractBillAdapter?.setSelectPosition(position)
        }
    }

    fun clearLables() {
        selectPosition = 0
        countryAdapter?.setSelectPosition(selectPosition)
        paymentAdapter?.setSelectPosition(selectPosition)
        payCoinAdapter?.setSelectPosition(selectPosition)
        lineSceneAdapter?.setSelectPosition(selectPosition)
        lineStringAdapter?.setSelectPosition(selectPosition)
        contractBillAdapter?.setSelectPosition(selectPosition)
    }

    fun clearPayCoinAdapter(lables: ArrayList<JSONObject>) {
        selectPosition = -1
        lablesForInit.clear()
        lablesForInit.addAll(lables)
        payCoinAdapter?.notifyDataSetChanged()
    }

    fun clearLineCoinAdapter(lables: ArrayList<String>) {
        selectPosition = 0
        lineStringAdapter?.setNewData(lables)
    }


    fun setStringBeanData(lables: ArrayList<String>, status: Boolean) {
        if (status) {
            if (lables.size > 3) {
                var beans: ArrayList<String> = arrayListOf()
                beans.add(lables[0])
                beans.add(lables[1])
                beans.add(lables[2])
                lineStringAdapter = LineStringAdapter(beans, selectPosition)
            } else {
                lineStringAdapter = LineStringAdapter(lables, selectPosition)
            }
        } else {
            lineStringAdapter = LineStringAdapter(lables, selectPosition)
        }
//        recycler_view?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycler_view?.layoutManager = GridLayoutManager(context, 3)
        if (isfrist) {
            recycler_view?.addItemDecoration(ItemDecoration())
            (recycler_view?.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
            isfrist = false
        }
        recycler_view?.adapter = lineStringAdapter
        lineStringAdapter?.notifyDataSetChanged()
        lineStringAdapter?.bindToRecyclerView(recycler_view ?: return)
        recycler_view?.setHasFixedSize(true)
        lineStringAdapter?.setOnItemClickListener { adapter, view, position ->
            selectPosition = position
            if (lineSelectOnclickListener != null) {
                lineSelectOnclickListener?.selectMsgIndex(lables[position])
            }
            lineStringAdapter?.setSelectPosition(position)
        }
    }

    class ItemDecoration : RecyclerView.ItemDecoration() {

        var context: Context? = null
        var space = SizeUtils.dp2px(7f)
        fun ItemDecoration(context: Context) {
            this.context = context
        }

        override fun getItemOffsets(outRect: Rect, view: View,
                                    parent: RecyclerView, state: RecyclerView.State) {
//            val childAdapterPosition = parent.getChildAdapterPosition(view)
//            val manager = parent.layoutManager as GridLayoutManager
//            val columnCount = manager.spanCount
//            val scale = childAdapterPosition % columnCount
//            outRect.right = SizeUtils.dp2px(13f)
//            if (scale == columnCount - 1) {
//                outRect.right = SizeUtils.dp2px(0f)
//            }
//            if (childAdapterPosition > 3) {
//                outRect.top = SizeUtils.dp2px(15f)
//            }
            outRect.left = space / 2
            outRect.right = space / 2
            outRect.bottom = space

            // Add top margin only for the first item to avoid double space between items
//            if (parent.getChildPosition(view) == 0)
//                outRect.top = space
        }
    }


}

