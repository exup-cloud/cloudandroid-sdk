package com.yjkj.chainup.new_version.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.yjkj.chainup.R
import com.yjkj.chainup.base.NBaseActivity
import com.yjkj.chainup.db.constant.RoutePath


/**
 * 公告&帮助中心详情
 *  此类不允许再加参数字段
 *  //web_url = "https://www.baidu.com/"
//web_url = "https://www.taobao.com/"
// web_url = "https://m.biki.com/noticeDetail?id=AboutUs&type=cms&isapp=1&lan=zh_CN&au=android"
 */

@Route(path = RoutePath.ItemDetailActivity)
class ItemDetailActivity : NBaseActivity() {

    override fun setContentView() = R.layout.activity_item_detail


}


