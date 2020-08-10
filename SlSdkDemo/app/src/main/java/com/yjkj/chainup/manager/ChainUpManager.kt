package com.yjkj.chainup.manager

import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import android.util.Log
import cn.ljuns.logcollector.LogNetCollector
import cn.ljuns.logcollector.util.FileUtils
import com.elvishew.xlog.LogUtils
import com.yjkj.chainup.BuildConfig
import com.yjkj.chainup.app.ChainUpApp
import com.yjkj.chainup.net.HttpClient
import com.yjkj.chainup.util.CompressUtil
import com.yjkj.chainup.util.RxUtil
import io.reactivex.Observable
import java.io.File
import java.lang.Exception

class ChainUpManager private constructor() {
    val TAG = ChainUpManager::class.java.simpleName

    companion object {
        private var INSTANCE: ChainUpManager? = null

        val instance: ChainUpManager
            get() {
                if (INSTANCE == null) {
                    synchronized(ChainUpManager::class.java) {
                        if (INSTANCE == null) {
                            INSTANCE = ChainUpManager()
                        }
                    }
                }
                return INSTANCE!!
            }
    }


    /**
     * 更新查看所有站内信
     */
    fun updateHttpStatus(): Observable<Boolean> {
        var file = FileUtils.getCacheFileDir(ChainUpApp.appContext, "log")
        return if (file != null && file.isNotEmpty()) {
            Observable.just(file).map {
                Log.e("LogUtils", "zipFile ${file}")
                val zipFile = file + ".zip"
                LogUtils.compress(file, zipFile)
                zipFile
            }.flatMap {
                val zipFile = it
                HttpClient.instance.uploadZip(File(zipFile))
            }.retryWhen { throwableObservable ->
                return@retryWhen throwableObservable.flatMap {
                    return@flatMap Observable.error<Throwable>(IllegalArgumentException(""))
                }
            }.map {
                val success = it.code == "0"
                if (success) {
                    try {
                        FileUtils.clearFile(file)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    LogNetCollector.getInstance(ChainUpApp.app).start("")
                }
                success
            }.compose(RxUtil.applySchedulersToObservable())
        } else {
            Observable.just(false)
        }
    }

    /**
     * 更新查看所有站内信
     */
    fun updateLocalLogShare(): Observable<Uri> {
        var file = FileUtils.getCacheFileDir(ChainUpApp.appContext, "log")
        return if (file != null && file.isNotEmpty()) {
            Observable.just(file).map {
                val zipFile = file + ".zip"
                LogUtils.compress(file, zipFile)
                Log.e("LogUtils", "zipFile ${file}")
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    val uri = FileProvider.getUriForFile(ChainUpApp.appContext,
                            BuildConfig.APPLICATION_ID + ".fileProvider", File(zipFile))
                    uri
                } else {
                    Uri.fromFile(File(zipFile))
                }
            }.compose(RxUtil.applySchedulersToObservable())
        } else {
            Observable.just(Uri.EMPTY)
        }
    }
}