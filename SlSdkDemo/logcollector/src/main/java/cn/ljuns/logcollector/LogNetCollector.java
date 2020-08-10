package cn.ljuns.logcollector;

import android.app.Application;
import android.support.annotation.NonNull;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.interceptor.BlacklistTagsFilterInterceptor;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy;
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.ljuns.logcollector.util.FileUtils;
import cn.ljuns.logcollector.util.LevelUtils;
import cn.ljuns.logcollector.util.TypeUtils;

/**
 * 日志收集
 */
public class LogNetCollector {

    private static final String UTF8 = "UTF-8";
    private static volatile LogNetCollector sLogCollector;
    private Application mContext;
    /**
     * 缓存文件
     */
    private File mCacheFile;
    /**
     * 需要过滤的 TAG
     */
    private String[] mTags;
    /**
     * 需要过滤的列表
     */
    private String[] mLevels;
    /**
     * 需要过滤的字符串
     */
    private String mFilterStr;
    private String mFilterType;
    private Map<String, String> mTagWithLevel;
    /**
     * 是否过滤大小写
     */
    private boolean mIgnoreCase = false;
    /**
     * 是否清除缓存日志文件
     */
    private boolean mCleanCache = false;
    private String printLog;

    public String getPrintLog() {
        return printLog;
    }

    public void setPrintLog(String printLog) {
        this.printLog = printLog;
    }

    private LogNetCollector(Application context) {
        this.mContext = context;
        mTagWithLevel = new HashMap<>();
    }

    public static LogNetCollector getInstance(Application context) {
        if (sLogCollector == null) {
            synchronized (LogNetCollector.class) {
                if (sLogCollector == null) {
                    sLogCollector = new LogNetCollector(context);
                }
            }
        }
        return sLogCollector;
    }

    /**
     * 设置缓存文件
     *
     * @param file file
     * @return LogCollector
     */
    public LogNetCollector setCacheFile(@NonNull File file) {
        this.mCacheFile = file;
        return this;
    }

    public LogNetCollector setCacheFile(@NonNull String path) {
        this.mCacheFile = new File(path);
        return this;
    }

    /**
     * 是否清除之前的缓存
     *
     * @param cleanCache cleanCache
     * @return LogCollector
     */
    public LogNetCollector setCleanCache(boolean cleanCache) {
        this.mCleanCache = cleanCache;
        return this;
    }

    /**
     * 设置需要过滤的 TAG
     *
     * @param tags tags
     * @return LogCollector
     */
    public LogNetCollector setTag(@NonNull String... tags) {
        this.mTags = tags;
        return this;
    }

    /**
     * 设置需要过滤的类型
     *
     * @param levels levels
     * @return LogCollector
     */
    public LogNetCollector setLevel(@LevelUtils.Level String... levels) {
        this.mLevels = levels;
        return this;
    }

    /**
     * 设置需要过滤的 tag:level
     *
     * @param tag   tag
     * @param level level
     * @return LogCollector
     */
    public LogNetCollector setTagWithLevel(@NonNull String tag, @LevelUtils.Level String level) {
        this.mTagWithLevel.put(tag, level);
        return this;
    }

    /**
     * 设置需要过滤的字符串，默认区分大小写
     *
     * @param str str
     * @return LogCollector
     */
    public LogNetCollector setString(@NonNull String str) {
        return setString(str, false);
    }

    /**
     * 设置需要过滤的字符串
     *
     * @param str        str
     * @param ignoreCase ignoreCase
     * @return LogCollector
     */
    public LogNetCollector setString(@NonNull String str, boolean ignoreCase) {
        this.mFilterStr = str;
        this.mIgnoreCase = ignoreCase;
        return this;
    }

    /**
     * 设置需要过滤的日志类型
     *
     * @param type type
     * @return LogCollector
     */
    public LogNetCollector setType(@TypeUtils.Type String type) {
        this.mFilterType = type;
        return this;
    }

    public static final int BUFFER_SIZE = 1024 * 400; //400k

    /**
     * 启动
     */
    public synchronized void start(String time) {
        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(BuildConfig.DEBUG ? LogLevel.ALL             // 指定日志级别，低于该级别的日志将不会被打印，默认为 LogLevel.ALL
                        : LogLevel.NONE)
                .tag("ChainUP")                                         // 指定 TAG，默认为 "X-LOG"
                .st(2)                                                 // 允许打印深度为2的调用栈信息，默认禁止
                .addInterceptor(new BlacklistTagsFilterInterceptor(    // 添加黑名单 TAG 过滤器
                        "blacklist1", "blacklist2", "blacklist3"))
                .build();
        String fileLog = FileUtils.getCacheFileDir(mContext, "log");
        com.elvishew.xlog.printer.Printer androidPrinter = new AndroidPrinter();             // 通过 android.util.Log 打印日志的打印器
        Printer filePrinter = new FilePrinter                   // 打印日志到文件的打印器
                .Builder(fileLog)                              // 指定保存日志文件的路径
                .fileNameGenerator(new DateFileNameGenerator())        // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
                .backupStrategy(new NeverBackupStrategy())              // 指定日志文件备份策略，默认为 FileSizeBackupStrategy(1024 * 1024)
                .cleanStrategy(new FileLastModifiedCleanStrategy(MAX_TIME))     // 指定日志文件清除策略，默认为 NeverCleanStrategy()
                .build();

        XLog.init(config, androidPrinter, filePrinter);

    }

    private static final long MAX_TIME = 1000 * 60 * 60 * 24 * 3; // two days

    public void print(String message) {

    }


}