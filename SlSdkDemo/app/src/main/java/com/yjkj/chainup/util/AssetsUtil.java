package com.yjkj.chainup.util;

import android.content.Context;
import android.content.res.AssetManager;

import com.yjkj.chainup.app.ChainUpApp;

import java.io.File;
import java.io.IOException;

/**
 * @Description:
 * @Author: wanghao
 * @CreateDate: 2019-11-14 19:36
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-11-14 19:36
 * @UpdateRemark: 更新说明
 */
public class AssetsUtil {

    /*
     * 判断assets目录下文件是否存在
     */

    public static boolean isExist(String fileName) {
        Context context = ChainUpApp.app;
        AssetManager assetManager = context.getAssets();
        if (null == assetManager)
            return false;
        try {
            String[] names = assetManager.list("");
            if (null == names)
                return false;
            for (int i = 0; i < names.length; i++) {
                if (null != fileName && fileName.trim().equals(names[i])) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 判断文件是否存在
     * @param strFile
     * @return
     */
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;

    }
}
