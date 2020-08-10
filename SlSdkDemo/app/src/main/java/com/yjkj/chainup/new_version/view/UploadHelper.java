package com.yjkj.chainup.new_version.view;

import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.yjkj.chainup.app.ChainUpApp;
import com.yjkj.chainup.util.HashUtil;

import java.io.File;
import java.util.Date;

/**
 * @Author lianshangljl
 * @Date 2019/1/25-4:02 PM
 * @Email buptjinlong@163.com
 * @description
 */
public class UploadHelper {
    //与个人的存储区域有关
    //上传仓库名


    private static OSS getOSSClient(String keyId, String keySecret, String endpoint, String keyToken) {
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(keyId, keySecret, keyToken);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(8); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        return new OSSClient(ChainUpApp.appContext, endpoint, credentialProvider, conf);


    }


    public UploadHelper() {

    }

    /**
     * 上传方法
     *
     * @param objectKey 标识
     * @param path      需上传文件的路径
     * @return 外网访问的路径
     */
    private static String upload(String objectKey, String path, String keyId, String keySecret, String bucketname, String endpoint, String keyToken) {
        // 构造上传请求
        PutObjectRequest request =
                new PutObjectRequest(bucketname,
                        objectKey, path);
        try {
            //得到client
            OSS client = getOSSClient(keyId, keySecret, endpoint, keyToken);
            //上传获取结果
            PutObjectResult result = client.putObject(request);
            //获取可访问的url
            String url = client.presignPublicObjectURL(bucketname, objectKey);
            //格式打印输出
            Log.e("PublicObjectURL:%s", url);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    /**
     * 上传普通图片
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadImage(String path, String keyId, String keySecret, String bucketname, String endpoint, String keyToken, String ossPath) {
        String key = getObjectImageKey(path, ossPath);
        return upload(key, path, keyId, keySecret, bucketname, endpoint, keyToken);
    }

    /**
     * 上传头像
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadPortrait(String path, String keyId, String keySecret, String bucketname, String endpoint, String keyToken) {
        String key = getObjectPortraitKey(path);
        return upload(key, path, keyId, keySecret, bucketname, endpoint, keyToken);
    }

    /**
     * 上传audio
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadAudio(String path, String keyId, String keySecret, String bucketname, String endpoint, String keyToken) {
        String key = getObjectAudioKey(path);
        return upload(key, path, keyId, keySecret, bucketname, endpoint, keyToken);
    }


    /**
     * 获取时间
     *
     * @return 时间戳 例如:201805
     */
    private static String getDateString() {
        return DateFormat.format("yyyyMM", new Date()).toString();
    }

    /**
     * 返回key
     *
     * @param path 本地路径
     * @return key
     */
    //格式: image/201805/sfdsgfsdvsdfdsfs.jpg
    private static String getObjectImageKey(String path, String ossPath) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        return String.format(ossPath + "%s.jpg", fileMd5);
    }

    //格式: portrait/201805/sfdsgfsdvsdfdsfs.jpg
    private static String getObjectPortraitKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("portrait/%s.jpg", dateString, fileMd5);
    }

    //格式: audio/201805/sfdsgfsdvsdfdsfs.mp3
    private static String getObjectAudioKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("audio/%s/%s.mp3", dateString, fileMd5);
    }

}