package com.yjkj.chainup.db.service;

import com.yjkj.chainup.db.MMKVDb;
import com.yjkj.chainup.extra_service.eventbus.NLiveDataUtil;
import com.yjkj.chainup.extra_service.eventbus.MessageEvent;

/**
 * @Description:  涨跌幅颜色设置
 * @Author: wanghao
 * @CreateDate: 2019-09-30 19:49
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-09-30 19:49
 * @UpdateRemark: 更新说明
 */
public class ColorDataService {

    private MMKVDb mMMKVDb;

    private static final String color_rise_fall = "color_selected";

    private ColorDataService() {
        mMMKVDb = new MMKVDb();
    }

    private static ColorDataService mContractDataService;

    public static ColorDataService getInstance() {
        if (null == mContractDataService)
            mContractDataService = new ColorDataService();
        return mContractDataService;
    }

    public void setColorType(int type){
        mMMKVDb.saveIntData(color_rise_fall,type);
        MessageEvent messageEvent = new MessageEvent(MessageEvent.color_rise_fall_type);
        messageEvent.setMsg_content(type);
        NLiveDataUtil.postValue(messageEvent);
    }

    public int getColorType(){
        return mMMKVDb.getIntData(color_rise_fall,0);
    }
}
