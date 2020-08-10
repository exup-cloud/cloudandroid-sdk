package com.yjkj.chainup.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.yjkj.chainup.app.ChainUpApp
import com.yjkj.chainup.extra_service.eventbus.EventBusUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.model.model.ContractModel
import com.yjkj.chainup.model.model.MainModel
import com.yjkj.chainup.model.model.OTCModel
import com.yjkj.chainup.net_new.NLoadingDialog
import com.yjkj.chainup.new_version.view.ForegroundCallbacksListener
import com.yjkj.chainup.new_version.view.ForegroundCallbacksObserver
import com.yjkj.chainup.util.LogUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *

 * @Description:

 * @Author:         wanghao

 * @CreateDate:     2019-07-31 11:56

 * @UpdateUser:     wanghao

 * @UpdateDate:     2019-07-31 11:56

 * @UpdateRemark:   更新说明

 */

abstract class NBaseFragment : Fragment() {

    val TAG = this::class.java.simpleName

    val MARKET_NAME = "market_name"
    val CUR_INDEX = "cur_index"
    val CUR_TYPE = "cur_type"


    var isFirstLoad = false

    protected var layoutView: View? = null

    protected var mActivity: Activity? = null
    protected var mContext: ChainUpApp? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context as Activity
        mContext = mActivity?.application as ChainUpApp
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null == layoutView) {
            isFirstLoad = true

            layoutView = inflater.inflate(setContentView(), container, false)
            loadData()

        } else {
            var viewGroup = layoutView?.parent as ViewGroup?
            viewGroup?.removeView(layoutView)
        }
        return layoutView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBusUtil.register(this)
        initView()
        setForegroundCallbacks()
    }


    fun setForegroundCallbacks() {
        ForegroundCallbacksObserver.getInstance().addListener(listener)
    }

    var listener = object : ForegroundCallbacksListener {
        override fun BackgroundListener() {
            background()
        }

        override fun ForegroundListener() {
            foreground()
        }
    }

    open fun background() {

    }

    open fun foreground() {

    }

    /**
     * 刷新接口
     */
    open fun refreshOkhttp(position: Int) {

    }

    override fun onResume() {
        super.onResume()
        if (userVisibleHint) {
            fragmentVisibile(true)
            /*var fgs = fragmentManager?.fragments
            LogUtil.d("setOnScrowListener", "NBaseFragment==onResume==userVisibleHint is $userVisibleHint，isVisible is $isVisible,fgs is $fgs")
            if(null!=fgs){
                for(i in 0 until fgs.size  ){
                    var fg = fgs[i]
                    LogUtil.d("setOnScrowListener", "NBaseFragment==onResume==userVisibleHint is $userVisibleHint，isVisible is $isVisible,fg is $fg,this is $this")

                    if(null!=fg && fg == this){

                    }
                }
            }*/
            //
        }
    }

    override fun onPause() {
        super.onPause()
        isFirstLoad = false
        if (userVisibleHint) {
            LogUtil.d("setOnScrowListener", "NBaseFragment==onPause==userVisibleHint is $userVisibleHint")
            fragmentVisibile(false)
        }

        closeLoadingDialog()
        clearDisposable()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBusUtil.unregister(this)
        ForegroundCallbacksObserver.getInstance().removeListener(listener)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser && null != layoutView && isResumed) {
            super.setUserVisibleHint(true)
        } else {
            super.setUserVisibleHint(false)
        }
        fragmentVisibile(userVisibleHint)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        fragmentVisibile(!hidden)
    }

    /*
     * 此方法处理view显示，或view绑定数据
     */
    abstract fun initView()

    /*
     * 数据请求，建议重载保持代码风格统一
     */
    open fun loadData() {}

    /*
     * 加载布局文件
     */
    protected abstract fun setContentView(): Int

    /*
     * Fragment 的显示与隐藏
     * @param isVisibleToUser  true为可见，否则不可见
     */
    open var mIsVisibleToUser = false

    open fun fragmentVisibile(isVisibleToUser: Boolean) {
        mIsVisibleToUser = isVisibleToUser
    }

    /*
     * 处理线程跟发消息线程一致
     * 子类重载
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    open fun onMessageEvent(event: MessageEvent) {
    }

    /*
     * 黏性事件处理
     * 子类重载处理完事件后需调用 EventBusUtil.removeStickyEvent(event);
     */
    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    open fun onMessageStickyEvent(event: MessageEvent) {
    }

    /*
     * 添加观察者
     */
    var disposables: CompositeDisposable? = null

    fun addDisposable(disposable: Disposable?) {
        if (null == disposable)
            return
        if (disposables == null) {
            disposables = CompositeDisposable()
        }
        disposables!!.add(disposable)
    }

    /*
     *注销观察者，防止内存泄漏
     */
    fun clearDisposable() {
        disposables?.clear()
        disposables = null
    }

    private var mainModel: MainModel? = null
    protected fun getMainModel(): MainModel {
        if (null == mainModel) {
            mainModel = MainModel()
        }
        return mainModel!!
    }

    private var otcModel: OTCModel? = null

    protected fun getOTCModel(): OTCModel {
        if (null == otcModel) {
            otcModel = OTCModel()
        }
        return otcModel ?: OTCModel()
    }

    private var contractModel: ContractModel? = null
    protected fun getContractModel() = contractModel ?: ContractModel()


    private var mLoadingDialog: NLoadingDialog? = null
    protected fun showLoadingDialog(loadText: String = "") {
        if (mActivity != null && mActivity?.isFinishing == false) {
            if (null == mLoadingDialog) {
                mLoadingDialog = NLoadingDialog(mActivity, loadText)
            } else {
                mLoadingDialog?.setLoadText(loadText)
            }
            mLoadingDialog?.showLoadingDialog()
        }
    }

    protected fun closeLoadingDialog() {
        mLoadingDialog?.closeLoadingDialog()
        mLoadingDialog = null
    }

}