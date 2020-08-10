package com.yjkj.chainup.db.service;

import com.yjkj.chainup.db.MMKVDb;
import com.yjkj.chainup.extra_service.eventbus.MessageEvent;
import com.yjkj.chainup.extra_service.eventbus.NLiveDataUtil;

/**
 * @Description:  涨跌幅颜色设置
 * @Author: wanghao
 * @CreateDate: 2019-09-30 19:49
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-09-30 19:49
 * @UpdateRemark: 更新说明
 */
public class CheckUpdateDataService {

    private MMKVDb mMMKVDb;

    public static final int hideDialog = 1;
    private static final String checkupdate = "checkupdate";

    private CheckUpdateDataService() {
        mMMKVDb = new MMKVDb();
    }

    private static CheckUpdateDataService mCheckUpdateDataService;

    public static CheckUpdateDataService getInstance() {
        if (null == mCheckUpdateDataService)
            mCheckUpdateDataService = new CheckUpdateDataService();
        return mCheckUpdateDataService;
    }

    public void saveData(int value){
        mMMKVDb.saveIntData(checkupdate,value);
    }

    public boolean hideDialog(){
        return hideDialog==mMMKVDb.getIntData(checkupdate,0);
    }

}
