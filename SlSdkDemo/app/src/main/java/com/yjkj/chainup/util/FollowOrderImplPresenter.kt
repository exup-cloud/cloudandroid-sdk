package com.yjkj.chainup.util

import com.follow.order.impl.OnFOResultListener
import com.yjkj.chainup.model.model.MainModel
import com.yjkj.chainup.net_new.rxjava.NDisposableObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.json.JSONObject

/**
 * @Author lianshangljl
 * @Date 2020-03-30-10:33
 * @Email buptjinlong@163.com
 * @description
 */
class FollowOrderImplPresenter {

    companion object {
        private var mainModel: MainModel? = null
        protected fun getMainModel() = mainModel ?: MainModel()
        /**
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

        /**
         * 开始跟单
         * @param trade_currency_id
         * @param total
         * @param is_stop_deficit
         * @param stop_deficit
         * @param is_stop_profit
         * @param stop_profit
         * @param symbol
         */
        fun getInnerFollowbegin(trade_currency_id: String, total: String,
                                is_stop_deficit: String, stop_deficit: String,
                                is_stop_profit: String, stop_profit: String,
                                symbol: String, currency: String, trade_currency: String, follow_immediately: String, resultListener: OnFOResultListener) {
            addDisposable(getMainModel().getInnerFollowbegin(trade_currency_id, total, is_stop_deficit, stop_deficit, is_stop_profit, stop_profit, symbol, currency, trade_currency, follow_immediately, object : NDisposableObserver() {
                override fun onResponseSuccess(jsonObject: JSONObject) {
                    var code = jsonObject.optInt("code")
                    var msg = jsonObject.optString("msg")
                    if (resultListener != null) {
                        if (code == 0) {
                            resultListener?.onSuccess("")
                        } else {
                            resultListener?.onFailed(code, msg)
                        }
                    }
                }

                override fun onResponseFailure(code: Int, msg: String?) {
                    super.onResponseFailure(code, msg)
                    resultListener?.onFailed(code, msg)
                }

            }))

        }

        /**
         * 结束跟单
         * @param follow_id
         */
        fun getInnerFollowEnd(follow_id: String, resultListener: OnFOResultListener) {
            addDisposable(getMainModel().getInnerFollowEnd(follow_id, object : NDisposableObserver() {
                override fun onResponseSuccess(jsonObject: JSONObject) {
                    var code = jsonObject.optInt("code")
                    var msg = jsonObject.optString("msg")
                    if (resultListener != null) {
                        if (code == 0) {
                            resultListener?.onSuccess("")
                        } else {
                            resultListener?.onFailed(code, msg)
                        }
                    }
                }

                override fun onResponseFailure(code: Int, msg: String?) {
                    super.onResponseFailure(code, msg)
                    resultListener?.onFailed(code, msg)
                }
            }))


        }

        /**
         * 获取账户余额信息
         * @param follow_id
         */
        fun getAccountBalance(coinSymbols: String, resultListener: OnFOResultListener) {
            addDisposable(getMainModel().getAccountBalance(coinSymbols, object : NDisposableObserver() {
                override fun onResponseSuccess(jsonObject: JSONObject) {
                    var data = jsonObject.optJSONObject("data")
                    if (data != null && resultListener != null) {
                        resultListener.onSuccess(data.toString())
                    }
                }

                override fun onResponseFailure(code: Int, msg: String?) {
                    super.onResponseFailure(code, msg)
                    resultListener?.onFailed(code, msg)
                }
            }))


        }


        /**
         * 获取kol列表 get
         * @param sort           排序
         * @param style          筛选
         * @param currency           筛选
         * @param just_show_follow           仅显示可跟
         * @param page           分页
         */
        fun getFollowKolList(sort: String, style: String, currency: String, just_show_follow: String, page: String, resultListener: OnFOResultListener) {
            addDisposable(getMainModel().getFollowKolList(sort, style, currency, just_show_follow, page, object : NDisposableObserver() {
                override fun onResponseSuccess(jsonObject: JSONObject) {
                    var data = jsonObject.optString("data")
                    if (data != null && resultListener != null) {
                        resultListener.onSuccess(data)
                    }
                }

                override fun onResponseFailure(code: Int, msg: String?) {
                    super.onResponseFailure(code, msg)
                    resultListener?.onFailed(code, msg)
                }
            }))
        }

        /**
         * 获取跟单列表 get
         * @param status
         * @param page
         */
        fun getFollowList(status: String, page: String, resultListener: OnFOResultListener) {
            addDisposable(getMainModel().getFollowList(status, page, object : NDisposableObserver() {
                override fun onResponseSuccess(jsonObject: JSONObject) {
                    var data = jsonObject.optString("data")
                    if (data != null && resultListener != null) {
                        resultListener.onSuccess(data.toString())
                    }
                }

                override fun onResponseFailure(code: Int, msg: String?) {
                    super.onResponseFailure(code, msg)
                    resultListener?.onFailed(code, msg)
                }
            }))

        }

        /**
         * 获取跟单配置 get
         * @param master_currency_id
         */
        fun getFollowOptions(master_currency_id: String, resultListener: OnFOResultListener) {
            addDisposable(getMainModel().getFollowOptions(master_currency_id, object : NDisposableObserver() {
                override fun onResponseSuccess(jsonObject: JSONObject) {
                    var data = jsonObject.optString("data")
                    if (data != null && resultListener != null) {
                        resultListener.onSuccess(data.toString())
                    }
                }

                override fun onResponseFailure(code: Int, msg: String?) {
                    super.onResponseFailure(code, msg)
                    resultListener?.onFailed(code, msg)
                }
            }))
        }

        /**
         * 获取跟单收益(跟单列表上的跟单收益信息)
         */
        fun getFollowProfit(resultListener: OnFOResultListener) {
            addDisposable(getMainModel().getFollowProfit(object : NDisposableObserver() {
                override fun onResponseSuccess(jsonObject: JSONObject) {
                    var data = jsonObject.optString("data")
                    if (data != null && resultListener != null) {
                        resultListener.onSuccess(data.toString())
                    }
                }

                override fun onResponseFailure(code: Int, msg: String?) {
                    super.onResponseFailure(code, msg)
                    resultListener?.onFailed(code, msg)
                }
            }))

        }


        /**
         * 获取跟单详情
         * @param follow_id
         */
        fun getFollowDetail(follow_id: String, resultListener: OnFOResultListener) {
            addDisposable(getMainModel().getFollowDetail(follow_id, object : NDisposableObserver() {
                override fun onResponseSuccess(jsonObject: JSONObject) {
                    var data = jsonObject.optString("data")
                    if (data != null && resultListener != null) {
                        resultListener.onSuccess(data.toString())
                    }
                }

                override fun onResponseFailure(code: Int, msg: String?) {
                    super.onResponseFailure(code, msg)
                    resultListener?.onFailed(code, msg)
                }
            }))
        }

        /**
         * 获取跟单收益趋势
         * @param follow_id
         */
        fun getFollowTrend(follow_id: String, resultListener: OnFOResultListener) {
            addDisposable(getMainModel().getFollowTrend(follow_id, object : NDisposableObserver() {
                override fun onResponseSuccess(jsonObject: JSONObject) {
                    var data = jsonObject.optString("data")
                    if (data != null && resultListener != null) {
                        resultListener.onSuccess(data.toString())
                    }
                }

                override fun onResponseFailure(code: Int, msg: String?) {
                    super.onResponseFailure(code, msg)
                    resultListener?.onFailed(code, msg)
                }
            }))
        }

        /**
         * 获取跟单分享信息
         * @param follow_id
         */
        fun getFollowShare(follow_id: String, resultListener: OnFOResultListener) {
            addDisposable(getMainModel().getFollowShare(follow_id, object : NDisposableObserver() {
                override fun onResponseSuccess(jsonObject: JSONObject) {
                    var data = jsonObject.optString("data")
                    if (data != null && resultListener != null) {
                        resultListener.onSuccess(data.toString())
                    }
                }

                override fun onResponseFailure(code: Int, msg: String?) {
                    super.onResponseFailure(code, msg)
                    resultListener?.onFailed(code, msg)
                }
            }))
        }
    }


}