package com.yjkj.chainup.wedegit;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.ImageView;

import com.yjkj.chainup.util.FileUtils;
import com.yjkj.chainup.util.LogUtil;

import java.io.File;

/**
 * @Author lianshangljl
 * @Date 2020-02-20-17:06
 * @Email buptjinlong@163.com
 * @description
 */
public class DownLoadReceiver extends BroadcastReceiver {
    private String mTag = getClass().getSimpleName();
    //    private NotificationManager nm;
    //    private Intent mIntent;
    private DownloadManager downloadManager;
    private long mTaskId;
    private Context mContext;
    private String mFileName;

    public DownLoadReceiver(Context context, DownloadManager downloadManager, long taskId, String fileName) {
        this.downloadManager = downloadManager;
        this.mTaskId = taskId;
        this.mContext = context;
        this.mFileName = fileName;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 检查下载状态
        checkDownloadStatus();
    }

    //检查下载状态
    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    LogUtil.e(mTag, ">>>下载暂停");
                case DownloadManager.STATUS_PENDING:
                    LogUtil.e(mTag, ">>>下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    LogUtil.e(mTag, ">>>正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    LogUtil.e(mTag, ">>>下载完成");
                    File file = new File(FileUtils.PICTURE_DIR, mFileName);
                    Log.e("jinlong", "file: " + file.getParent());
                    c.close();
                    //                    UtilsTools.deleteFile(fileName);
                    //downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + versionName;
                    //                    installAPK(new File(downloadPath));
                    break;
                case DownloadManager.STATUS_FAILED:
                    LogUtil.e(mTag, ">>>下载失败");
                    break;
                default:
                    break;
            }
        }
        c.close();
    }

}
