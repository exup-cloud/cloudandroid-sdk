package com.yjkj.chainup.net_new;

import com.yjkj.chainup.R;
import com.yjkj.chainup.app.ChainUpApp;
import com.yjkj.chainup.util.Utils;

/**
 * @Description:
 * @Author: wanghao
 * @CreateDate: 2019-10-14 11:44
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-10-14 11:44
 * @UpdateRemark: 更新说明
 */
public class NetUrl {

    public static final String baseUrl() {
        return Utils.returnAPIUrl(ChainUpApp.app.getString(R.string.baseUrl));
    }

    public static final String getotcBaseUrl() {
        return Utils.returnAPIUrl(ChainUpApp.app.getString(R.string.otcBaseUrl));
    }

    public static final String getsocketAddress() {
        return Utils.returnAPIUrl(ChainUpApp.app.getString(R.string.socketAddress));
    }

    public static final String getotcSocketAddress() {
        return Utils.returnAPIUrl(ChainUpApp.app.getString(R.string.otcSocketAddress));
    }

    public static final String getcontractUrl() {
        return Utils.returnAPIUrl(ChainUpApp.app.getString(R.string.contractUrl));
    }

    public static final String getContractSocketUrl() {
        return Utils.returnAPIUrl(ChainUpApp.app.getString(R.string.contractSocketAddress));
    }

    public static final String getredPackageUrl() {
        return Utils.returnAPIUrl(ChainUpApp.app.getString(R.string.redPackageUrl));
    }

    public static final String biki_monitor_appUrl = "https://security.biki.com/security-microspot/apis/v1/monitor/app";


}
