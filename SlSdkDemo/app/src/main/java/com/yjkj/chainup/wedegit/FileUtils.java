package com.yjkj.chainup.wedegit;

import android.os.Environment;
import android.util.Log;

import com.yjkj.chainup.app.ChainUpApp;

import java.io.File;

/**
 * @Author lianshangljl
 * @Date 2020-02-04-19:19
 * @Email buptjinlong@163.com
 * @description
 */
public class FileUtils {
    /**
     * sdcard
     */
    private static final String SD_ROOT = Environment.getExternalStorageDirectory().getPath();

    /**
     * app根目录
     */
    public static final String PICTURE_DIR = SD_ROOT + File.separator + ChainUpApp.appContext.getPackageName()
            + File.separator + "yunli" + File.separator;

    private FileUtils() {
    }

    /**
     * 是否存在 SDCard
     *
     * @return 是否存在
     */
    public static boolean sdExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean initPictureDir() {
        if (!sdExist()) {
            return false;
        }
        File picFile = new File(PICTURE_DIR);
        boolean exists = picFile.exists();
        boolean mkdirs = picFile.mkdirs();
        return exists || mkdirs;
    }

    // 获取文件大小
    public static void deDuplication(File file) {
        if (file.exists()) {
            Log.d("fileUtil", "存在了：" + file.getPath());
            boolean isDelete = file.delete();
            Log.d("fileUtil", "删除文件结果：" + isDelete);
        } else {
            Log.d("fileUtil", "文件不存在：" + file.getPath());
        }
    }
}
