package com.yjkj.chainup.new_version.activity.leverage

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.timmy.tdialog.TDialog
import com.yjkj.chainup.R
import com.yjkj.chainup.app.AppConstant
import com.yjkj.chainup.base.NBaseFragment
import com.yjkj.chainup.db.constant.CommonConstant
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.db.service.PublicInfoDataService
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.extra_service.eventbus.NLiveDataUtil
import com.yjkj.chainup.manager.LanguageUtil
import com.yjkj.chainup.manager.LoginManager
import com.yjkj.chainup.manager.NCoinManager
import com.yjkj.chainup.manager.RateManager
import com.yjkj.chainup.net.api.ApiConstants
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import com.yjkj.chainup.new_version.adapter.NCurrentEntrustAdapter
import com.yjkj.chainup.new_version.dialog.NewDialogUtils
import com.yjkj.chainup.new_version.view.EmptyForAdapterView
import com.yjkj.chainup.util.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.depth_vertical_layout_lever.view.*
import kotlinx.android.synthetic.main.fragment_nlever.*
import kotlinx.android.synthetic.main.fragment_nlever.ctv_content
import kotlinx.android.synthetic.main.fragment_nlever.ib_kline
import kotlinx.android.synthetic.main.fragment_nlever.ll_all_entrust_order
import kotlinx.android.synthetic.main.fragment_nlever.rb_buy
import kotlinx.android.synthetic.main.fragment_nlever.rb_sell
import kotlinx.android.synthetic.main.fragment_nlever.rv_current_entrust
import kotlinx.android.synthetic.main.fragment_nlever.tv_all
import kotlinx.android.synthetic.main.fragment_nlever.tv_close_price
import kotlinx.android.synthetic.main.fragment_nlever.tv_coin_map
import kotlinx.android.synthetic.main.fragment_nlever.tv_converted_close_price
import kotlinx.android.synthetic.main.fragment_nlever.view_buy_bg
import kotlinx.android.synthetic.main.fragment_nlever.view_sell_bg
import kotlinx.android.synthetic.main.trade_amount_view.*
import kotlinx.android.synthetic.main.trade_amount_view.view.*
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.textColor
import org.json.JSONObject
import java.net.URI
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * @author Bertking
 * @description 杠杆
 * @date 2019-11-05
 *
 */
class NLeverFragment : NBaseFragment(), View.OnClickListener {



    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                NLeverFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }

        var curDepthIndex = 0
        var tradeOrientation = ParamConstant.TYPE_BUY
        var tapeLevel = 0

    }

    override fun initView() {
    }

    override fun setContentView() = R.layout.fragment_nlever
    override fun onClick(v: View?) {
    }


}
