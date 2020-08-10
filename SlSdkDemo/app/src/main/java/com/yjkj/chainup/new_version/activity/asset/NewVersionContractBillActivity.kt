package com.yjkj.chainup.new_version.activity.asset

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.yjkj.chainup.R
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.net.HttpClient
import com.yjkj.chainup.net.retrofit.NetObserver
import com.yjkj.chainup.new_version.activity.NewBaseActivity
import com.yjkj.chainup.new_version.adapter.NewVersionContracBillAdapter
import com.yjkj.chainup.new_version.view.EmptyForAdapterView
import com.yjkj.chainup.new_version.view.PersonalCenterView
import com.yjkj.chainup.new_version.view.ScreeningPopupWindowView
import com.yjkj.chainup.treaty.bean.ContractCashFlowBean
import com.yjkj.chainup.util.NToastUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_contract_bill.*

const val TRANSFERCONTRACT = "TRANSFER"
const val CLOSING_INCOME = "CLOSING_INCOME"
const val CONTRACT_FEE = "CONTRACT_FEE"
const val BLAST_POSITION = "BLAST_POSITION"
const val CAPITAL_FEE = "CAPITAL_FEE"

/**
 * @Author lianshangljl
 * @Date 2019/6/21-3:58 PM
 * @Email buptjinlong@163.com
 * @description 合约账单页面
 */
class NewVersionContractBillActivity : NewBaseActivity() {

    var item = "$TRANSFERCONTRACT,$CLOSING_INCOME,$CONTRACT_FEE,$BLAST_POSITION,$CAPITAL_FEE"


    var childItem = ""
    var startTime = ""
    var endTime = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contract_bill)
        loopOrderList4Contract()
        setOnclick()
        title_layout?.setContentTitle(LanguageUtil.getString(this,"assets_action_contractNote"))
    }

    companion object {
        fun enter2(context: Context) {
            context.startActivity(Intent(context, NewVersionContractBillActivity::class.java))
        }
    }


    fun initView(bean: ArrayList<ContractCashFlowBean.Transactions>) {
        list.clear()
        list.addAll(bean)
        adapter = NewVersionContracBillAdapter(list)
        recycler_view.layoutManager = LinearLayoutManager(this)
        adapter?.bindToRecyclerView(recycler_view)
        recycler_view.adapter = adapter
        adapter?.setEmptyView(EmptyForAdapterView(this))
    }

    var isFrist = true
    fun setOnclick() {
        title_layout?.listener = object : PersonalCenterView.MyProfileListener {
            override fun onclickHead() {

            }

            override fun onclickRightIcon() {
                if (spw_layout?.visibility == View.GONE) {
                    spw_layout?.visibility = View.VISIBLE
                    if (isFrist) {
                        isFrist = false
                        spw_layout?.setMage()
                    }
                } else {
                    spw_layout?.visibility = View.GONE
                }
            }

            override fun onclickName() {

            }

            override fun onRealNameCertificat() {

            }
        }
        spw_layout?.contractBillListener = object : ScreeningPopupWindowView.ContractBillListener {
            override fun returnContractBill(transfer: String, closingIncome: String, contractFee: String, blastPosition: String, capitalFee: String, begin: String, end: String) {
                startTime = begin
                endTime = end
                item = ""
                childItem = ""
                if (transfer.isNotEmpty()) {
                    childItem = "$childItem$transfer,"
                }

                if (closingIncome.isNotEmpty()) {
                    childItem = "$childItem$closingIncome,"
                }

                if (contractFee.isNotEmpty()) {
                    childItem = "$childItem$contractFee,"
                }

                if (blastPosition.isNotEmpty()) {
                    childItem = "$childItem$blastPosition,"
                }
                if (capitalFee.isNotEmpty()) {
                    childItem = "$childItem$capitalFee,"
                }


                if (transfer.isEmpty()) {
                    item = "$item$TRANSFERCONTRACT,"
                }
                if (closingIncome.isEmpty()) {
                    item = "$item$CLOSING_INCOME,"
                }
                if (contractFee.isEmpty()) {
                    item = "$item$CONTRACT_FEE,"
                }
                if (blastPosition.isEmpty()) {
                    item = "$item$BLAST_POSITION,"
                }
                if (capitalFee.isEmpty()) {
                    item = "$item$CAPITAL_FEE,"
                }
                loopOrderList4Contract()
            }

        }

    }


    var adapter: NewVersionContracBillAdapter? = null

    var list: ArrayList<ContractCashFlowBean.Transactions> = arrayListOf()


    /**
     * 活动委托列表
     */
    private fun loopOrderList4Contract() {
        if (!LoginManager.checkLogin(this, false)) return
        HttpClient.instance
                .getBusinessTransferList(item, childItem, startTime, endTime, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : NetObserver<ContractCashFlowBean>() {
                    override fun onHandleSuccess(bean: ContractCashFlowBean?) {

                        if (bean?.transactionsList == null || bean?.transactionsList.isEmpty()) {
                            list.clear()
                            adapter?.notifyDataSetChanged()
                        }

                        if (bean?.transactionsList?.isNotEmpty() == true) {
                            initView(bean.transactionsList as ArrayList<ContractCashFlowBean.Transactions>)
                        }
                    }

                    override fun onHandleError(code: Int, msg: String?) {
                        NToastUtil.showTopToast(false, msg)
                    }
                })
    }

}