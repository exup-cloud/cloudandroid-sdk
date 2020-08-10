package com.yjkj.chainup.new_version.view.depth

import com.yjkj.chainup.bean.DepthBean
import com.yjkj.chainup.bean.KlineDepth
import org.json.JSONObject

/**
 * @Author lianshangljl
 * @Date 2020-03-31-11:45
 * @Email buptjinlong@163.com
 * @description
 */
class BBKlineDataDepthHelper {


    private val listeners = ArrayList<DepthKlineDataUpdateListener>()


    /**
     * 初始化深度数据源
     * @param data
     */
    @Synchronized
    fun initSourceDepth(depth: KlineDepth?) {
        if (depth != null) {
            mSourceBuys.clear()
            mSourceSells.clear()
            mSourceBuys.addAll(depth.bids)
            mSourceSells.addAll(depth.asks)
        }
    }


    /**
     * 刷新间隔频率
     */
    val intervalFrequency = 100
    var lastTime = 0L


    fun clearData() {
        mSourceBuys.clear()
        mSourceSells.clear()
    }


    fun bindDepthBeanUpdateListener(listener: DepthKlineDataUpdateListener) {
        if (listener != null) {
            listeners.add(listener)
        }
    }

    fun unBindDepthBeanUpdateListener(listener: DepthKlineDataUpdateListener) {
        if (listener != null && listeners.contains(listener)) {
            listeners.remove(listener)
        }
    }

    /**
     * 深度原数据
     */
    var mSourceBuys = ArrayList<DepthBean>()
        @Synchronized
        get() {
            if (field == null) {
                field = ArrayList()
            }
            return field
        }
    var mSourceSells = ArrayList<DepthBean>()
        @Synchronized
        get() {
            if (field == null) {
                field = ArrayList()
            }
            return field
        }


    /**
     * 更新深度数据
     */
    @Synchronized
    fun updateDepthByType(jsonObject: JSONObject) {
        val dataObj = jsonObject.optJSONObject("tick") ?: return
        try {
            val depth = KlineDepth()
            depth.fromJson(dataObj)
            var bindList: ArrayList<DepthBean> = depth.bids as ArrayList<DepthBean>
            var sellList: ArrayList<DepthBean> = depth.asks as ArrayList<DepthBean>

            mSourceSells.clear()
            mSourceBuys.clear()
            mSourceSells.addAll(sellList)
            mSourceBuys.addAll(bindList)
            //需要排序
            doSourceSort()
            //加上UI刷新频率限制
            val nowTime = System.currentTimeMillis()
            if (nowTime - lastTime < intervalFrequency) {
                return
            }
            lastTime = nowTime
            listeners.forEach {
                it.onUpdateComplete()
            }

        } catch (e: Exception) {
            e.printStackTrace()

        }
    }
    /**
     * 原数据排序
     */
    private fun doSourceSort() {
        //买 降序排列
        mSourceBuys.sortByDescending { it.price.toDouble() }
        //卖 升序排列
        mSourceSells.sortBy { it.price.toDouble() }
    }



    interface DepthKlineDataUpdateListener {
        fun onUpdateComplete()
    }

    companion object {
        @Volatile
        private var mSingleton: BBKlineDataDepthHelper? = null

        val instance: BBKlineDataDepthHelper?
            get() {
                if (mSingleton == null) {
                    synchronized(BBKlineDataDepthHelper::class.java) {
                        if (mSingleton == null) {
                            mSingleton = BBKlineDataDepthHelper()
                        }
                    }
                }
                return mSingleton
            }
    }

}