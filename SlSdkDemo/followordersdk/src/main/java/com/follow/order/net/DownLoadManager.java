package com.follow.order.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

public class DownLoadManager {

    private DownloadCallBack downloadCallBack;

    private static final String TAG = "DownLoadManager";

    private static String APK_CONTENTTYPE = "application/vnd.android.package-archive";

    private static String PNG_CONTENTTYPE = "image/png";

    private static String JPG_CONTENTTYPE = "image/jpg";

    private static String fileSuffix="";

    private Handler handler;
    private long lastUpdateTime;
    private long loadingUpdateMaxTimeSpan =300;
    private long totalSize;

    public DownLoadManager(DownloadCallBack downloadCallBack) {
        this.downloadCallBack = downloadCallBack;
    }

    private static DownLoadManager sInstance;

    /**
     *DownLoadManager getInstance
     */
    public static synchronized DownLoadManager getInstance(DownloadCallBack downloadCallBack) {
        if (sInstance == null) {
            sInstance = new DownLoadManager(downloadCallBack);
        }
        return sInstance;
    }



    public boolean  writeResponseBodyToDisk(Context context, ResponseBody body) {
        return writeResponseBodyToDisk(context,null,body);
    }
    public boolean  writeResponseBodyToDisk(Context context, File destFile,ResponseBody body) {

        Log.d(TAG, "contentType:>>>>"+ body.contentType().toString());

        String type = body.contentType().toString();

        if (type.equals(APK_CONTENTTYPE)) {

            fileSuffix = ".apk";
        } else if (type.equals(PNG_CONTENTTYPE)) {
            fileSuffix = ".png";
        } else if (type.equals(JPG_CONTENTTYPE)) {
            fileSuffix = ".jpg";
        }

        // 其他同上 自己判断加入

        final String name = System.currentTimeMillis() + fileSuffix;
        final String path = context.getExternalFilesDir(null) + File.separator + name;

        Log.d(TAG, "path:>>>>"+ path);

        try {
            File futureStudioIconFile = new File(path);
            if (destFile!=null){
                futureStudioIconFile = destFile;
            }


            if (futureStudioIconFile.exists()) {
                futureStudioIconFile.delete();
            }

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                totalSize = body.contentLength();
                long currentSize = 0;
                Log.d(TAG, "file length: "+ totalSize);
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    currentSize += read;

//                    Log.d(TAG, "file download: " + currentSize + " of " + totalSize+System.currentTimeMillis());
//                    if (downloadCallBack != null) {
//                        handler = new Handler(Looper.getMainLooper());
//                        final long finalFileSizeDownloaded = currentSize;
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                updateProgress(totalSize,finalFileSizeDownloaded,false);
//                            }
//                        });
//
//                    }
                    updateProgress(totalSize,currentSize,false);
                }

                outputStream.flush();
                Log.d(TAG, "file downloaded: " + currentSize + " of " + totalSize);
                if (downloadCallBack != null) {
                    handler = new Handler(Looper.getMainLooper());
                    final File finalFutureStudioIconFile = futureStudioIconFile;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateProgress(totalSize, totalSize,true);
                            downloadCallBack.onSucess(finalFutureStudioIconFile.getAbsolutePath(), name, totalSize);

                        }
                    });
                    Log.d(TAG, "file downloaded: " + currentSize + " of " + totalSize);
                }

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                if (downloadCallBack != null) {
                    downloadCallBack.onError(e);
                }
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            if (downloadCallBack != null) {
                downloadCallBack.onError(e);
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateProgress(long total, long current, boolean forceUpdateUI) {

//        if (isCancelled() || isFinished()) {
//            return false;
//        }
//        LogUtil.e("lastUpdateTime"+lastUpdateTime);
        if (downloadCallBack != null && total > 0) {
            if (total < current) {
                total = current;
            }
            if (forceUpdateUI) {
                lastUpdateTime = System.currentTimeMillis();
//                 this.update(FLAG_PROGRESS, total, current, request.isLoading());
                downloadCallBack.onProgress(total,current);
            } else {
                long currTime = System.currentTimeMillis();
                if (currTime - lastUpdateTime >= loadingUpdateMaxTimeSpan) {
                    lastUpdateTime = currTime;
//                    this.update(FLAG_PROGRESS, total, current, request.isLoading());
                    downloadCallBack.onProgress(total,current);
                }
            }
        }

        return true;
    }

}
