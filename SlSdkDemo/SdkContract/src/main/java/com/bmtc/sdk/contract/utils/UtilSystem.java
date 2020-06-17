package com.bmtc.sdk.contract.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import android.text.TextUtils;


import com.bmtc.sdk.contract.BuildConfig;
import com.contract.sdk.utils.StringUtil;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class UtilSystem {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetworkInfo.isConnected();
    }

    public static int getApkVersionCode(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            return info.versionCode;
        }
        return 0;
    }

    public static String getApkVersionName(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            return info.versionName;
        }
        return "";
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            if (pi != null)
                return true;
        } catch (PackageManager.NameNotFoundException e) {
            // Ignore the exception
        }
        return false;
    }

    private static Intent getLaunchIntent(PackageManager packageManager, String packageName) {
        try {
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent = intent.cloneFilter();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                return intent;
            }

            PackageInfo pkginfo = packageManager.getPackageInfo(packageName, 0);
            if (pkginfo != null) {
                if (pkginfo.activities != null
                        && pkginfo.activities.length == 1) {
                    intent = new Intent(Intent.ACTION_MAIN);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClassName(pkginfo.packageName,
                            pkginfo.activities[0].name);
                    return intent;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void launchApp(Context context, String packageName, String launchActivity) {
        boolean success = false;
        Intent intent = getLaunchIntent(context.getPackageManager(), packageName);
        if (intent != null) {
            if (!TextUtils.isEmpty(launchActivity)) {
                intent.setClassName(packageName, launchActivity);
            }

            try {
                context.startActivity(intent);
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void installApk(Context context, String apkFile) {
        File fileApk = new File(apkFile);
        if (!fileApk.exists() || !fileApk.isFile()) {
            return;
        }

        // 通过Intent安装APK文件
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkFile), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static Drawable getInstalledAppIcon(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi.applicationInfo.loadIcon(pm);
        } catch (Exception e) {
            // ignore
        }

        return null;
    }

    /**
     * 调用系统卸载App界面
     *
     * @param context
     * @param packageName
     */
    @SuppressWarnings("JavaDoc")
    public static void startUninstallActivity(final Context context,
                                              final String packageName) {
        Uri uri = Uri.fromParts("package", packageName, null);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ignored) {

        }
    }

    /**
     * 两个版本号比较
     * 版本号规则：以点分隔的多位数字型版本号 两个版本号点的数量可以不同
     * <p/>
     * 返回值说明：
     * -1:前者小于后者
     * 0：相同
     * 1 前者大于后者
     *
     * @param ver1 版本号1
     * @param ver2 版本号2
     * @return int
     */
    public static int compareVersionStr(String ver1, String ver2) {
        String[] ver1s = ver1.split("\\.");
        String[] ver2s = ver2.split("\\.");
        try {
            int length = Math.max(ver1s.length, ver2s.length);
            String[] ver1snew = new String[length];
            String[] ver2snew = new String[length];

            System.arraycopy(ver1s, 0, ver1snew, 0, ver1s.length);
            System.arraycopy(ver2s, 0, ver2snew, 0, ver2s.length);

            String vs1;
            String vs2;
            for (int i = 0; i < length; i++) {
                vs1 = ver1snew[i].trim();
                vs2 = ver2snew[i].trim();
                if ("".equals(vs1)) vs1 = "0";
                if ("".equals(vs2)) vs2 = "0";

                if (Integer.parseInt(vs2) > Integer.parseInt(vs1)) {
                    return -1;
                }
                if (Integer.parseInt(vs2) < Integer.parseInt(vs1)) {
                    return 1;
                }
                //等于，继续比较
            }
            //所有位都相等，返回0
            return 0;
        } catch (Exception e) {
            //数字转换错误
            return ver1.compareTo(ver2);
        }
    }

    public static Activity getRunningActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread")
                    .invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = (Map) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(activityRecord);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public static void startApkInstallActivity(Context context, Uri uri) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startApkInstallActivity(Context context, File apkFile) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);
    }

    public static String toMD5(String strText)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update( strText.getBytes() );
            StringBuffer buf=new StringBuffer();
            for(byte b:md.digest())
            {
                buf.append(String.format("%02x", b&0xff) );
            }

            return buf.toString();
        } catch( Exception e ) {
            e.printStackTrace(); return null;
        }
    }


    /**
     * Get version code for this application.
     *
     * @param context	The context to use.  Usually your {@link android.app.Application}
     *               	or {@link Activity} object.
     * @return the version code or -1 if package not found
     */
    public static int getVersionCode(Context context){
        int versionCode;
        try {
            versionCode = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            versionCode = -1;
        }

        return versionCode;
    }

    /**
     * Get version code for the application package name.
     *
     * @param context 		The context to use.  Usually your {@link android.app.Application}
     *                		or {@link Activity} object.
     * @param packageName 	application package name
     * @return the version code or -1 if package not found
     */
    public static int getVersionCode(Context context, String packageName) {
        int versionCode;
        try {
            versionCode = context.getPackageManager()
                    .getPackageInfo(packageName, 0).versionCode;
        } catch (Exception e) {
            versionCode = -1;
        }

        return versionCode;
    }

    /**
     * Get version name for this application.
     *
     * @param context	The context to use.  Usually your {@link android.app.Application}
     *               	or {@link Activity} object.
     * @return the version name or empty string if package not found
     */
    public static String getVersionName(Context context) {
        String versionName;
        try {
            versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            versionName = StringUtil.EMPTY_STRING;
        }

        return versionName;
    }

    public static String getVersionName(Context context, String packageName) {
        String versionName;
        try {
            versionName = context.getPackageManager()
                    .getPackageInfo(packageName, 0).versionName;
        } catch (Exception e) {
            versionName = StringUtil.EMPTY_STRING;
        }

        return versionName;
    }

    public static Object getMetaData(Context context, String keyName) {
        return getMetaData(context, context.getPackageName(), keyName);
    }

    private static Object getMetaData(Context context, String packageName, String keyName) {
        try {
            ApplicationInfo appi = context.getPackageManager()
                    .getApplicationInfo(packageName, PackageManager.GET_META_DATA);

            Bundle bundle = appi.metaData;
            Object value = bundle.get(keyName);

            return value;
        } catch (PackageManager.NameNotFoundException ex) {
            // Si el meta-data no existe retorno null
        }
        return null;
    }

    public static void openHtmlActivity(Context context, String url, String title) {
//        Intent intent = new Intent(context, HtmlActivity.class);
//        intent.putExtra("url", url);
//        intent.putExtra("title",title);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
    }

    public static void safeOpenUrl(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static boolean isActivityForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;

    }

    public static boolean isActivityForeground(Context context, Activity activity) {
        if (activity == null) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (activity.getComponentName().getClassName().equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;

    }

    public static String readAssertResource(Context context, String strAssertFileName) {
        AssetManager assetManager = context.getAssets();
        String strResponse = "";
        try {
            InputStream ims = assetManager.open(strAssertFileName);
            strResponse = getStringFromInputStream(ims);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strResponse;
    }

    private static String getStringFromInputStream(InputStream a_is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(a_is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException ignored) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }
        return sb.toString();
    }

    public static File uriToFile(Uri uri,Context context) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'").append(path).append("'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA }, buff.toString(), null, null);
                int index = 0;
                int dataIdx;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index != 0) {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();

            return new File(path);
        }
        return null;
    }

    /** 质量压缩,
     * @param bitmap 要压缩的图片
     *  Hint to the compressor, 0-100. 0 meaning compress for small size, 100 meaning compress for max quality. Some
     * formats, like PNG which is lossless, will ignore the quality setting
     * quality  (0-100)  100是不压缩，值越小，压缩得越厉害
     */
    public static File qualityCompressBitmap(Bitmap bitmap){
        String sdcard = Environment.getExternalStorageDirectory().toString();
        File file = new File(sdcard + "/Photo");
        if (!file.exists()) {
            file.mkdirs();
        }

        DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = fmt.format(new Date()) + ".jpg";
        File imageFile = new File(file.getAbsolutePath(), fileName);

        //字节数组输出流
        ByteArrayOutputStream stream =new ByteArrayOutputStream();
        int quality=20;
        //图片压缩后把数据放在stream中
        bitmap.compress(Bitmap.CompressFormat.JPEG,quality, stream);
        try {
            FileOutputStream fileOutputStream=new FileOutputStream(imageFile);
            //不断把stream的数据写文件输出流中去
            fileOutputStream.write(stream.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageFile;
    }


    /**
     * 判断APP是否在前台
     * @return
     */
    public static Boolean isAPPForeground(Context context) {
        if (context == null) {
            return false;
        }
        ActivityManager mActivityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (mActivityManager.getRunningTasks(1) == null) {
            return false;
        }
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList =
                mActivityManager.getRunningTasks(1);
        if (runningTaskInfoList.size() == 0) {
            return false;
        }
        ActivityManager.RunningTaskInfo mRunningTask = runningTaskInfoList.get(0);
        String pkgName = mRunningTask.topActivity.getPackageName();
        //String activityName =  mRunningTask.topActivity.getClassName();
        return TextUtils.equals(pkgName, context.getPackageName());
    }
}
