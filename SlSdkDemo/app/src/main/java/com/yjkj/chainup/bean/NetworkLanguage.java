package com.yjkj.chainup.bean;

import com.yjkj.chainup.db.service.PublicInfoDataService;
import com.yjkj.chainup.manager.LanguageUtil;

import org.json.JSONObject;

/**
 * @Author lianshangljl
 * @Date 2020-04-10-19:27
 * @Email buptjinlong@163.com
 * @description
 */
public class NetworkLanguage {

    public JSONObject languageJson = null;


    public JSONObject getLanguageJson() {
        if (languageJson == null) {
            JSONObject jsonObject = PublicInfoDataService.getInstance().getOnlineText();
            if (jsonObject !=null){
                languageJson = jsonObject.optJSONObject(LanguageUtil.getSelectLanguage());
            }
        }
        return languageJson;
    }

    public void cleanLanguage() {
        languageJson = null;
    }


}
