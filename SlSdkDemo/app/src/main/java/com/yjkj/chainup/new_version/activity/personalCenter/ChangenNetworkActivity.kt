package com.yjkj.chainup.new_version.activity.personalCenter

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Gravity
import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.net.HttpClient
import com.yjkj.chainup.net.api.ApiConstants
import com.yjkj.chainup.net_new.HttpHelper
import com.yjkj.chainup.net_new.JSONUtil
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import com.yjkj.chainup.new_version.adapter.ChangeNetworkAdapter
import com.yjkj.chainup.new_version.view.EmptyForAdapterView
import com.yjkj.chainup.util.Utils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_change_work.*
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

/**
 * @Author lianshangljl
 * @Date 2020-06-18-13:40
 * @Email buptjinlong@163.com
 * @description
 */
@Route(path = RoutePath.ChangenNetworkActivity)
class ChangenNetworkActivity : NBaseActivity() {
    override fun setContentView() = R.layout.activity_change_work


    var adapter: ChangeNetworkAdapter? = null
    var liksArray: ArrayList<JSONObject> = arrayListOf()
    var mdDisposable = CompositeDisposable()
    var links = ""
    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        collapsing_toolbar?.setCollapsedTitleTextColor(ContextCompat.getColor(mActivity, R.color.text_color))
        collapsing_toolbar?.setExpandedTitleColor(ContextCompat.getColor(mActivity, R.color.text_color))
        collapsing_toolbar?.setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        collapsing_toolbar?.expandedTitleGravity = Gravity.BOTTOM
        setTextContentView()
        initView()

    }

    fun setTextContentView() {
        collapsing_toolbar?.title = LanguageUtil.getString(this, "customSetting_action_changeHost")
    }


    override fun initView() {
        super.initView()
        toolbar?.setNavigationOnClickListener {
            finish()
        }
        initAdapter()
        links = PublicInfoDataService.getInstance().links
        getOrderStateEachMin()
    }

    fun initAdapter() {
        adapter = ChangeNetworkAdapter(liksArray)
        recycler_view?.layoutManager = LinearLayoutManager(mActivity)
        adapter?.bindToRecyclerView(recycler_view ?: return)
        adapter?.emptyView = EmptyForAdapterView(this)
        recycler_view?.adapter = adapter
        adapter?.setOnItemClickListener { adapter, view, position ->
            PublicInfoDataService.getInstance().saveNewWorkURL(liksArray[position].optString("hostName"))
            HttpHelper.instance.clearServiceMap()
            HttpClient.instance.refreshApi()
            adapter.notifyDataSetChanged()
        }
    }


    fun getHeath(index: Int, url: String) {
        var startTime = System.currentTimeMillis()
        var baseUrl = Utils.returnSpeedUrl(url, ApiConstants.BASE_URL)
        addDisposable(getSpeedModel().getHealth(baseUrl, object : NDisposableObserver() {
            override fun onResponseSuccess(jsonObject: JSONObject) {
                var endTime = System.currentTimeMillis()
                var consum = endTime - startTime
                liksArray[index].put("networkAppapi", "$consum")
                refreshView(index)
                initSocket(index, Utils.returnSpeedUrl(url, ApiConstants.SOCKET_ADDRESS))
            }

            override fun onResponseFailure(code: Int, msg: String?) {
                super.onResponseFailure(code, msg)
                liksArray[index].put("error", "error")
                refreshView(index)
            }
        }))
    }


    private var mSocketClient: WebSocketClient? = null
    private fun initSocket(index: Int, url: String) {
        if (mSocketClient != null && mSocketClient?.isOpen == true) {
            return
        }
        var startTime = System.currentTimeMillis()
        mSocketClient = object : WebSocketClient(URI(url)) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                var endTime = System.currentTimeMillis()
                var consum = endTime - startTime
                liksArray[index].put("networkWs", "$consum")
                refreshView(index)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.e("jinlong", "onClose:$code  index:$index")
            }

            override fun onMessage(bytes: ByteBuffer?) {
                super.onMessage(bytes)
                Log.e("jinlong", "onMessage")
            }

            override fun onMessage(message: String?) {
                Log.e("jinlong", "onMessage12")
            }

            override fun onError(ex: Exception?) {
                Log.e("jinlong", "onError:${ex?.message}")
                liksArray[index].put("error", "error")
                refreshView(index)
            }
        }
        mSocketClient?.connect()
    }


    fun refreshView(index: Int) {
        runOnUiThread {
            adapter?.notifyItemChanged(index, 0)
        }
    }

    fun loopNetworkState() {
        if (liksArray.isEmpty()) return
        for (num in 0 until liksArray.size) {
            getHeath(num, liksArray[num].optString("hostName"))
        }
        mSocketClient?.close()
    }


    /**
     * 每1分钟调用一次接口x
     */
    private fun getOrderStateEachMin() {
        mdDisposable.add(Observable.interval(0, 10, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver()))
    }

    fun getObserver(): DisposableObserver<Long> {
        return object : DisposableObserver<Long>() {
            override fun onComplete() {
            }

            override fun onNext(t: Long) {
                Log.e("ssssssssssssssssss", t.toString() + "time")
                liksArray?.clear()
                liksArray.addAll(JSONUtil.arrayToList(JSONArray(links)) ?: arrayListOf())
                adapter?.notifyDataSetChanged()
                loopNetworkState()
            }

            override fun onError(e: Throwable) {
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mSocketClient != null && mSocketClient?.isOpen == true) {
            mSocketClient?.closeBlocking()
            mSocketClient?.close()
        }
        mdDisposable?.clear()
    }

}