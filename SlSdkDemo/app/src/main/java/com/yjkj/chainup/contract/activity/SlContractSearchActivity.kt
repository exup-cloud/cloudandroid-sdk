package com.yjkj.chainup.contract.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.contract.sdk.ContractPublicDataAgent
import com.contract.sdk.data.Contract
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.contract.adapter.SlContractSearchAdapter
import com.yjkj.chainup.contract.utils.getLineText
import com.yjkj.chainup.contract.utils.onLineText
import com.yjkj.chainup.new_version.view.EmptyForAdapterView
import kotlinx.android.synthetic.main.sl_activity_contract_coin_search.*

/**
 * 合约搜索
 */
class SlContractSearchActivity : NBaseActivity(){
    override fun setContentView(): Int {
        return R.layout.sl_activity_contract_coin_search
    }

    private val mList = ArrayList<Contract>()
    private val spitList = ArrayList<Contract>()
    private var adapter: SlContractSearchAdapter?=null

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        loadData()
        initView()
    }

    override fun loadData() {
        super.loadData()
        mList.addAll(ContractPublicDataAgent.getContracts())
        tv_cancel.setOnClickListener {
            finish()
        }
    }

    override fun initView() {
        super.initView()
        adapter = SlContractSearchAdapter(mList)
        lv_layout.layoutManager = LinearLayoutManager(mActivity)
        adapter?.bindToRecyclerView(lv_layout)
        adapter?.emptyView = EmptyForAdapterView(this)
        lv_layout.adapter = adapter
        adapter?.setOnItemClickListener { adapter, _, position ->
            val item = adapter?.getItem(position) as Contract
            val intent = Intent()
            intent.putExtra("instrumentId",item.instrument_id)
            setResult(Activity.RESULT_OK,intent)
            finish()
          }
        tv_cancel.onLineText("common_text_btnCancel")
        et_search.hint = getLineText("common_action_searchCoinPair")
        et_search.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if(TextUtils.isEmpty(text)){
                    adapter?.setNewData(mList)
                    adapter?.notifyDataSetChanged()
                }else{
                    spitList.clear()
                    for (i in mList.indices){
                         val item = mList[i]
                        if(item.getDisplayName(mActivity).contains(text.toUpperCase())){
                            spitList.add(item)
                        }
                    }
                    adapter?.setNewData(spitList)
                    adapter?.notifyDataSetChanged()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    companion object{
        fun show(activity:Activity){
           val intent = Intent(activity,SlContractSearchActivity::class.java)
            activity.startActivityForResult(intent,1001)
        }
    }

}