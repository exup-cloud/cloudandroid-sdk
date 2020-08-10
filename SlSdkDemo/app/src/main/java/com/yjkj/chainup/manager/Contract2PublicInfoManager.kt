package com.yjkj.chainup.manager

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import com.yjkj.chainup.R
import com.yjkj.chainup.bean.ContractMode
import com.yjkj.chainup.bean.coin.CoinBean
import com.yjkj.chainup.net.HttpClient
import com.yjkj.chainup.net.retrofit.NetObserver
import com.yjkj.chainup.treaty.bean.ContractBean
import com.yjkj.chainup.treaty.bean.ContractPublicInfoBean
import com.yjkj.chainup.treaty.bean.ContractSceneList
import com.yjkj.chainup.util.BigDecimalUtils
import com.yjkj.chainup.util.LogUtil
import com.yjkj.chainup.util.StringUtil
import com.yjkj.chainup.util.StringUtils
import io.reactivex.schedulers.Schedulers

/**
 * @Author: Bertking
 * @Date：2019-05-06-19:41
 * @Description:合约的公共信息管理类
 */
object Contract2PublicInfoManager {
    val TAG = Contract2PublicInfoManager::class.java.simpleName
    private val mmkv: MMKV?
        get() {
            val mmkv = MMKV.mmkvWithID("contract_public_info")
            return mmkv
        }

    init {
        mmkv
    }


    /**
     * 合约列表
     */
    private const val MARKET = "market"

    /**
     * 合约模式
     */
    const val CONTRACT_MODE = "switch"
    /**
     * 默认的合约
     */
    private const val DEFAULT_CONTRACT = "marketSymbol"

    /**
     * 合约资金流水类型
     */
    private const val SCENELIST = "sceneList"

    /**
     * 合约ID
     */
    private const val CONTRACT_ID = "contractId"

    private const val COIN_LIST = "coinList"


    fun getContractPublicInfo() {
        LogUtil.d("Contract2PublicInfoManager", "getContractPublicInfo")
        HttpClient.instance
                .getPublicInfo4Contract()
                .subscribeOn(Schedulers.io())
                .subscribe(object : NetObserver<ContractPublicInfoBean>() {
                    override fun onHandleSuccess(bean: ContractPublicInfoBean?) {
                        Log.d(TAG, "=====AAÀa===${bean?.market.toString()}==")
                        val marketJson = Gson().toJson(bean?.market, HashMap<String, ArrayList<ContractBean>>()::class.java)
                        val sceneListJson = Gson().toJson(bean?.sceneList, ArrayList<ContractSceneList>()::class.java)
                        mmkv?.encode(MARKET, marketJson)
                        mmkv?.encode(DEFAULT_CONTRACT, bean?.marketSymbol)
                        mmkv?.encode(SCENELIST, sceneListJson)
                        /**
                         * 合约模式
                         */
                        mmkv?.encode(CONTRACT_MODE, Gson().toJson(bean?.switch, ContractMode::class.java))
                        /**
                         * 存储coinList
                         */
                        mmkv?.encode(COIN_LIST, Gson().toJson(bean?.coinList, HashMap<String, CoinBean>()::class.java))
                    }

                    override fun onHandleError(code: Int, msg: String?) {
                        super.onHandleError(code, msg)
                        Log.d(TAG, "=====AAÀa===${msg}==")
                    }
                })
    }

    /**
     * 获取全部合约信息
     */
    fun getContracts(): HashMap<String, ArrayList<ContractBean>> {
        val string = mmkv?.decodeString(MARKET)
        return if (TextUtils.isEmpty(string)) {
            linkedMapOf()
        } else {
            val type = object : TypeToken<HashMap<String, ArrayList<ContractBean>>>() {}.type
            Gson().fromJson(string, type)
        }
    }

    /**
     * 获取合约列表
     *
     *1. 按照合约币对首字母的字典先后顺序自然排列，
     *2. 排序依次为：永续、当周、次周、当月、季度
     *  PS:9ms
     */
    @JvmStatic
    fun getAllContracts(): ArrayList<ContractBean> {
        var arrayList = arrayListOf<ContractBean>()

        if (getContracts().isEmpty()) {
            arrayList
        } else {
            val associateBy = Contract2PublicInfoManager.getContracts().entries.sortedBy {
                it.key
            }
            associateBy.forEach {
                arrayList.addAll(it.value.sortedBy { it.contractType })
            }
        }
        return arrayList
    }


    /**
     * 获取币种列表
     */
    fun getCoinList(): HashMap<String, CoinBean> {
        val string = mmkv?.decodeString(COIN_LIST, "")
        return if (TextUtils.isEmpty(string)) {
            HashMap<String, CoinBean>()
        } else {
            var type = object : TypeToken<HashMap<String, CoinBean>>() {}.type
            Gson().fromJson(string, type)
        }
    }

    /**
     * @param key 币种名称
     * 根据币种名称获取对应的ConBean
     */
    fun getCoinByName(key: String): CoinBean? {
        return getCoinList()[key]
    }


    /**
     * 获取同一market下的所有合约
     * @param market
     */
    fun getContractByMarket(market: String): ArrayList<ContractBean> {
        return getContracts()[market] ?: arrayListOf<ContractBean>()
    }


    /**
     * 获取合约
     * @param contractId 合约ID
     */
    fun getContractByContractId(contractId: Int): ContractBean? {
        return if (getAllContracts().isNotEmpty()) {
            val filter = getAllContracts().filter {
                it.id == contractId
            }
            filter.firstOrNull()
        } else {
            null
        }
    }

    /**
     * 获取默认的合约
     */
    private fun getDefaultContract(): ContractBean? {
        return if (getAllContracts().isNotEmpty()) {
            val filter = getAllContracts().filter {
                it.symbol == mmkv?.decodeString(DEFAULT_CONTRACT)
            }
            filter.firstOrNull()
        } else {
            null
        }
    }

    /**
     * 获取默认的合约
     */
    fun getSceneList(): ArrayList<ContractSceneList> {
        var sceneList = mmkv?.decodeString(SCENELIST)
        if (TextUtils.isEmpty(sceneList) || !StringUtil.checkStr(sceneList)) {
            return arrayListOf()
        } else {
            val type = object : TypeToken<ArrayList<ContractSceneList>>() {}.type
                    ?: return arrayListOf()
            return Gson().fromJson(sceneList, type)
        }
    }


    /**
     * 根据合约ID获取相应的杠杆
     */
    fun getLevelsByContractId(contractId: Int): ArrayList<String> {
        val contractBean = getContractByContractId(contractId)
        Log.d(TAG, "=====xxxxx${contractBean.toString()}======")
        var levels = ArrayList(contractBean?.leverTypes?.split(",") ?: arrayListOf())
        return levels
    }


    /**
     * 保存OR获取当前合约
     * @param contractId 合约ID
     * @param isSave 是否保存
     *
     * TODO 优化
     */
    @JvmStatic
    fun currentContractId(contractId: Int? = 0, isSave: Boolean = false): Int {
        return if (isSave) {
            mmkv?.encode(CONTRACT_ID, contractId ?: 0)
            0
        } else {
            mmkv?.decodeInt(CONTRACT_ID, getDefaultContract()?.id ?: 0) ?: 0
        }
    }

    /**
     * TODO 优化 -->此方法耗时(160ms左右) (尽量少用)
     */
    fun currentContract(lastSymbol: String = ""): ContractBean? {
        return if (getAllContracts().isNotEmpty()) {
            val filter = getAllContracts().filter {
                it.id == currentContractId()
            }

            if (filter.isNotEmpty()) {
                filter.first().lastSymbol = lastSymbol
                filter.first()
            } else {
                null
            }

        } else {
            getDefaultContract()?.lastSymbol = lastSymbol
            getDefaultContract()
        }
    }


    /**
     * 根据"精度"截取数据
     */
    fun cutValueByPrecision(value: String, precision: Int = 4): String {
        val intercept = BigDecimalUtils.divForDown(value, precision).toPlainString()
        return intercept
    }


    /**
     * 根据"精度"截取数据
     * 保证金精度
     */
    fun cutDespoitByPrecision(value: String, coinName: String = "btc"): String {
        val precision = getCoinByName(coinName)?.showPrecision ?: 8
        val intercept = BigDecimalUtils.divForDown(value, precision).toPlainString()
        return intercept
    }


    /**
     * 根据"精度"截取数据(暂不推荐使用)
     * TODO 耗时操作...(180ms)
     */
    fun cutValueByPrecision(value: String): String {
        var value = value
        if (value.contains("\"")) {
            value = value.replace("\"", "")
        }
        val intercept = BigDecimalUtils.divForDown(value, currentContract()?.pricePrecision
                ?: 4).toPlainString()
        return intercept
    }

    /**
     * @return 合约类型 + 交割时间
     *
     * 永续合约不显示时间
     */
    fun getContractType(context: Context, contractType: Int?, settleTime: String?): String {
        val contractTypeName = getContractTypeText(context,contractType)

        var time = ""
        if (contractType != 0) {
            val split = settleTime?.split(" ")
            if (split?.isNotEmpty() == true && split.size >= 2) {
                val yearMonthDay = split[0].split("-")
                if (yearMonthDay.isNotEmpty() && yearMonthDay.size >= 3) {
                    time = yearMonthDay.elementAt(1) + yearMonthDay.elementAt(2)
                }
            }
        }
        return "$contractTypeName  $time"
    }


    fun getContractTypeText(context: Context, contractType: Int?): String {
        val contractBean = getContractByContractId(contractType ?: 0)
        LogUtil.d(TAG,"=====getContractTypeText:${contractBean.toString()}=====")
        return when (contractBean?.contractType) {
            1 -> LanguageUtil.getString(context,"contract_text_currentWeek")
            2 -> LanguageUtil.getString(context,"contract_text_nextWeek")
            3 -> LanguageUtil.getString(context,"noun_date_month")
            4 -> LanguageUtil.getString(context,"noun_date_quarter")

            else -> LanguageUtil.getString(context,"contract_text_perpetual")
        }
    }


    fun getContractType(context: Context, contractId: Int?): String {
        val contractBean = getContractByContractId(contractId ?: 0)
        return getContractType(context, contractBean?.contractType, contractBean?.settleTime)
    }


    /**
     * 1-分仓。0净持仓
     * 默认净持仓
     */
    fun isPureHoldPosition(): Boolean {
        val string = mmkv?.decodeString(CONTRACT_MODE, "")
        return if (TextUtils.isEmpty(string)) {
            true
        } else {
            Gson().fromJson(string, ContractMode::class.java)?.isMorePosition ?: "0" == "0"
        }
    }

}