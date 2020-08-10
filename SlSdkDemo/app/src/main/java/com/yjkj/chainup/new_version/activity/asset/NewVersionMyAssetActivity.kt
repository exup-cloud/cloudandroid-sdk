package com.yjkj.chainup.new_version.activity.asset

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.ParamConstant
import com.yjkj.chainup.db.constant.RoutePath
import com.yjkj.chainup.db.service.UserDataService
import com.yjkj.chainup.extra_service.arouter.ArouterUtil
import com.yjkj.chainup.extra_service.eventbus.MessageEvent
import com.yjkj.chainup.util.Utils
import kotlinx.android.synthetic.main.activity_version_my_asset.*

/**
 * @Author lianshangljl
 * @Date 2019/5/21-3:06 PM
 * @Email buptjinlong@163.com
 * @description 资产 activity版本
 */
@Route(path = RoutePath.NewVersionMyAssetActivity)
class NewVersionMyAssetActivity : NBaseActivity() {

    @JvmField
    @Autowired(name = ParamConstant.assetTabType)
    var position = 0

    override fun setContentView(): Int {
        return R.layout.activity_version_my_asset
    }

    var myAssetFragment = NewVersionMyAssetFragment()

    /**
     * 资金密码
     */
    var isShowAssets = true



    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        ArouterUtil.inject(this)

        isShowAssets = UserDataService.getInstance().isShowAssets

        initView()

        setClickListener()

    }


    override fun initView() {
        Utils.showAssetsSwitch(isShowAssets, right_icon2)
        supportFragmentManager
                .beginTransaction().add(R.id.rl_fragme, myAssetFragment).commitAllowingStateLoss()
        myAssetFragment.hideTitle(true)
        myAssetFragment.setViewPagePosition(position)

        myAssetFragment.setFromAssetsActivity(true)

        myAssetFragment.activityRefresh(true)
    }

    fun setClickListener() {
        iv_back?.setOnClickListener { finish() }

        /**
         * 此处隐藏资金
         */
        right_icon2?.setOnClickListener {
            isShowAssets = !isShowAssets
            UserDataService.getInstance().setShowAssetStatus(isShowAssets)
            myAssetFragment.refresh4Homepage()
            Utils.showAssetsSwitch(isShowAssets, right_icon2)
        }

    }

    override fun onMessageEvent(event: MessageEvent) {
        super.onMessageEvent(event)
        if(event?.msg_type == MessageEvent.assets_activity_finish_event){
            finish()
        }
    }

}
