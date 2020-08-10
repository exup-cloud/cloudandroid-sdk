package com.yjkj.chainup.util;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.geetest.sdk.GT3ConfigBean;
import com.geetest.sdk.GT3ErrorBean;
import com.geetest.sdk.GT3GeetestUtils;
import com.geetest.sdk.GT3Listener;
import com.yjkj.chainup.BuildConfig;
import com.yjkj.chainup.R;
import com.yjkj.chainup.app.ChainUpApp;
import com.yjkj.chainup.db.service.PublicInfoDataService;
import com.yjkj.chainup.manager.LanguageUtil;
import com.yjkj.chainup.new_version.view.Gt3GeeListener;
import com.yjkj.chainup.new_version.view.OnSaveSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {


    public static void copyString(TextView textView) {
        if (textView == null) {
            return;
        }
        ClipboardManager cm = (ClipboardManager) textView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText(null, textView.getText()));
//            UIUtils.showToast("复制成功");
        }
    }


    public static int getScreemWidth(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;     // 屏幕宽度（像素）
    }

    public static String getOrderType(Context context, int num) {
        String type = "";
        switch (num) {
            case 1:
                type = LanguageUtil.getString(context, "otc_text_orderWaitPay");
                break;
            case 2:
            case 6:
                type = LanguageUtil.getString(context, "otc_text_waitReceiveCoin");
                break;
            case 3:
            case 8:
                type = LanguageUtil.getString(context, "otc_text_orderComplete");
                break;
            case 4:
            case 9:
                type = LanguageUtil.getString(context, "filter_otc_cancel");
                break;
            case 5:
                type = LanguageUtil.getString(context, "otc_text_orderAppeal");
                break;
            case 7:
                type = LanguageUtil.getString(context, "otc_abnormal_orders");
                break;
            default:
                break;

        }
        return type;
    }


    public static String getOrderTypeSell(Context context, int num) {
        String type = "";
        switch (num) {
            case 1:
                type = LanguageUtil.getString(context, "otc_text_orderWaitMoney");
                break;
            case 2:
            case 6:
                type = LanguageUtil.getString(context, "otc_text_waitSendCoin");
                break;
            case 3:
            case 8:
                type = LanguageUtil.getString(context, "otc_text_orderComplete");
                break;
            case 4:
                type = LanguageUtil.getString(context, "filter_otc_cancel");
                break;
            case 5:
                type = LanguageUtil.getString(context, "otc_text_orderAppeal");
                break;
            case 7:
                type = LanguageUtil.getString(context, "otc_abnormal_orders");
                break;
            case 9:
                type = LanguageUtil.getString(context, "filter_otc_appealCancel");
                break;
            default:
                break;
        }
        return type;
    }

    /**
     * 复制文本
     *
     * @param string
     */
    public static void copyString(String string) {
        ClipboardManager cm = (ClipboardManager) ChainUpApp.appContext.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText(null, string));
        }
    }


    //高斯模糊背景
    public static Bitmap blurBitmap(Bitmap bitmap, Context context) {
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(context);

        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs,
                Element.U8_4(rs));

        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        blurScript.setRadius(20.f);

        // Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        // Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        // recycle the original bitmap
        bitmap.recycle();

        // After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return outBitmap;
    }

    public static Bitmap createBlurBitmap(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        if (radius < 1) {
            return (null);
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;
        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int[] vmin = new int[Math.max(w, h)];
        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int[] dv = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }
        yw = yi = 0;
        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;
        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;
            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = r1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }


    public static Bitmap stringtoBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static byte[] String2Byte(String string) {
        byte[] bytes = string.getBytes();
        return bytes;
    }


    /**
     * 获取屏幕分辨率
     *
     * @param context
     * @return
     */
    public static int[] getScreenDispaly(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();// 手机屏幕的宽度
        int height = windowManager.getDefaultDisplay().getHeight();// 手机屏幕的高度
        int[] result = {width, height};
        return result;
    }


    public static void isShowPass(boolean isShow, ImageView imageView, EditText editText) {
        if (isShow) {
            imageView.setImageResource(R.drawable.visible);
            //需要两种type一起使用，加上TYPE_CLASS_TEXT是为了防止输入中文
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            imageView.setImageResource(R.drawable.hide);
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        //光标放到最后
        editText.setSelection(editText.length());
    }

    /*
     * 首页资产数据显示与隐藏控制
     */
    public static void assetsHideShow(boolean isShow, TextView textView, String content) {
        if (isShow) {
            if (StringUtil.checkStr(content)) {
                textView.setText(content + "");
            } else {
                textView.setText("0");
            }
        } else {
            textView.setText("*****");
        }
    }

    public static void showAssetsSwitch(boolean isShow, ImageView imageView) {
        if (imageView == null) return;
        if (isShow) {
            imageView.setImageResource(R.drawable.visible);
        } else {
            imageView.setImageResource(R.drawable.hide);
        }
    }

    /*
     * 首页资产数据可见与不可见
     */
    public static void assetsVisible(boolean isShow, View view) {
        view.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    public static String deleteHeader(String code) {
        int start = code.indexOf("<header>");
        int end = code.indexOf("</header>");
        if (start != 0 && end != 0 && start < end) {
            //从起始位置到终止位置，并不包含终止位置
            String content = code.substring(start, end + 9);
            code = code.replace(content, "");
        }
        return code;
    }


    public static String getVolUnit(float num) {

        int e = (int) Math.floor(Math.log10(num));

        if (e >= 8) {
            return "亿手";
        } else if (e >= 4) {
            return "万手";
        } else {
            return "手";
        }
    }


    /**
     * 从assets 目录读取文件
     *
     * @param context
     * @param fileName
     * @return
     */
    private String getCert(Context context, String fileName) {
        InputStream inputStream = null;
        String string = null;
        try {
            inputStream = context.getAssets().open(fileName);
            int size = inputStream.available();
            byte[] byteArray = new byte[size];
            inputStream.read(byteArray);
            string = new String(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return string;
    }

    /**
     * 时间转换
     */
    public static String formatDate(long time, String format) {
        DateFormat dateFormat2 = new SimpleDateFormat(format, Locale.getDefault());
        String formatDate = dateFormat2.format(time);
        return formatDate;
    }


    public static String formatDate(long time) {
        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formatDate = dateFormat2.format(time);
        return formatDate;
    }


    public static String formatDateTime(long date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String formatDate = dateFormat.format(date);
        return formatDate;
    }


    public static String formatTime(long millis) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formatDate = dateFormat.format(millis);
        return formatDate;
    }

    /**
     * 用于判断是否快速点击
     *
     * @return
     */
    private static final int FAST_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;


    public synchronized static boolean isFastClick() {
        boolean flag = false;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) <= FAST_CLICK_DELAY_TIME) {
            return true;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    public static GT3GeetestUtils gt3GeetestUtils;
    public static GT3ConfigBean gt3ConfigBean;

    /**
     * 极验
     *
     * @param context
     * @return
     */
    public static ArrayList<String> gee3test(Context context, Gt3GeeListener listener) {
        ArrayList<String> validateParams = new ArrayList<>(3);

        gt3GeetestUtils = new GT3GeetestUtils(context);
        gt3ConfigBean = new GT3ConfigBean();
        gt3ConfigBean.setDebug(BuildConfig.DEBUG);
        // 设置验证模式，1：bind，2：unbind
        gt3ConfigBean.setPattern(1);
        // 设置点击灰色区域是否消失，默认不消失
        gt3ConfigBean.setCanceledOnTouchOutside(false);
        // 设置语言，如果为null则使用系统默认语言
        gt3ConfigBean.setLang(null);
        // 设置加载webview超时时间，单位毫秒，默认10000，仅且webview加载静态文件超时，不包括之前的http请求
        gt3ConfigBean.setTimeout(10000);
        // 设置webview请求超时(用户点选或滑动完成，前端请求后端接口)，单位毫秒，默认10000
        gt3ConfigBean.setWebviewTimeout(10000);
        // 设置回调监听
        gt3ConfigBean.setListener(new GT3Listener() {
            String validateResult = "";

            /**
             * api1结果回调
             * @param result
             */
            @Override
            public void onApi1Result(String result) {
                Log.e("gee3test", "GT3BaseListener-->onApi1Result-->" + result);
            }

            /**
             * 验证码加载完成
             * @param duration 加载时间和版本等信息，为json格式
             */
            @Override
            public void onDialogReady(String duration) {
                Log.e("gee3test", "GT3BaseListener-->onDialogReady-->" + duration);
            }

            /**
             * 验证结果
             * @param result
             */
            @Override
            public void onDialogResult(String result) {
                validateResult = result;
                Log.e("gee3test", "GT3BaseListener-->onDialogResult-->" + result);
                // 开启api2逻辑
                new RequestAPI2().execute(result);
            }

            /**
             * api2回调
             * @param result
             */
            @Override
            public void onApi2Result(String result) {
                Log.e("gee3test", "GT3BaseListener-->onApi2Result-->" + result);
            }

            /**
             * 统计信息，参考接入文档
             * @param result
             */
            @Override
            public void onStatistics(String result) {
                Log.e("gee3test", "GT3BaseListener-->onStatistics-->" + result);
            }

            /**
             * 验证码被关闭
             * @param num 1 点击验证码的关闭按钮来关闭验证码, 2 点击屏幕关闭验证码, 3 点击返回键关闭验证码
             */
            @Override
            public void onClosed(int num) {
                Log.e("gee3test", "GT3BaseListener-->onClosed-->" + num);
            }

            /**
             * 验证成功回调
             * @param result
             */
            @Override
            public void onSuccess(String result) {
                Log.e("gee3test", "GT3BaseListener-->onSuccess-->" + result + ",validateResult is " + validateResult);

                if (!StringUtil.checkStr(validateResult))
                    return;

                if (!validateParams.isEmpty()) {
                    validateParams.clear();
                }

                try {
                    // 1.取出该接口返回的三个参数用于自定义二次验证
                    JSONObject jsonObject = new JSONObject(validateResult);
                    validateParams.add(jsonObject.optString("geetest_challenge"));
                    validateParams.add(jsonObject.optString("geetest_validate"));
                    validateParams.add(jsonObject.optString("geetest_seccode"));

                    if (listener != null) {
                        listener.onSuccess(validateParams);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            /**
             * 验证失败回调
             * @param errorBean 版本号，错误码，错误描述等信息
             */
            @Override
            public void onFailed(GT3ErrorBean errorBean) {
                Log.e("gee3test", "GT3BaseListener-->onFailed-->" + errorBean.toString());
            }

            /**
             * api1回调
             */
            @Override
            public void onButtonClick() {
                new RequestAPI1().execute();
            }
        });
        gt3GeetestUtils.init(gt3ConfigBean);
        // 开启验证
        gt3GeetestUtils.startCustomFlow();


        return validateParams;
    }

    public static void setGeetestDeatroy() {
        gt3GeetestUtils.destory();
    }

    /**
     * 请求api1
     */
    static class RequestAPI1 extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            String string = HttpUtils.requsetUrl(StringUtils.getString(R.string.baseUrl) + "common/tartCaptcha");

//            HttpClient.Companion.getInstance().getTartCaptcha()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new NetObserver<String>() {
//
//                        @Override
//                        protected void onHandleSuccess(String s) {
//
//                        }
//
//
//                    });

            JSONObject jsonObject = null;
            JSONObject jsonObject2;
            JSONObject jsonObject3 = null;

            try {
                jsonObject = new JSONObject(string);
                jsonObject2 = jsonObject.getJSONObject("data");
                jsonObject3 = jsonObject2.getJSONObject("captcha");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject3;
        }

        @Override
        protected void onPostExecute(JSONObject parmas) {
            // 继续验证

            Log.e("gee3test", "RequestAPI1-->onPostExecute: " + parmas);
            // SDK可识别格式为
            // {"success":1,"challenge":"06fbb267def3c3c9530d62aa2d56d018","gt":"019924a82c70bb123aae90d483087f94","new_captcha":true}
            // TODO 设置返回api1数据，即使为null也要设置，SDK内部已处理
            gt3ConfigBean.setApi1Json(parmas);
            // 继续api验证
            gt3GeetestUtils.getGeetest();
        }
    }


    /**
     * 请求api2
     */
    static class RequestAPI2 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return params[0];
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("gee3test", "RequestAPI2-->onPostExecute: " + result);

            gt3GeetestUtils.showSuccessDialog();
        }
    }


    //获取小时
    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    //获取分钟
    public static int getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    //获取周
    public static int getWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    //获取周
    public static int getWeek(int year, int moth, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, moth - 1, day);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    //获取年
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    //获取月
    public static int getMoth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    //获取日
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DATE);
    }

    public static Date getDate(int year, int moth, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, moth - 1, day, hour, minute);
        return calendar.getTime();
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }


    public static void main(String[] args) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
        try {
            Date date = format.parse("2016-12-15 12");
            System.out.println(getHour(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取过去第几天的日期
     *
     * @return
     */
    public static String getPastDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 7);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        Log.e(null, result);
        return result;
    }

    public static String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        Log.e(null, result);
        return result;
    }

    public static Date parseServerTime(String serverTime) {
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINESE);
        Date date = null;
        try {
            date = sdf.parse(serverTime);
        } catch (Exception e) {
            date = new Date();
        }
        return date;
    }

    /**
     * 从本地path中获取bitmap，压缩后保存小图片到本地
     *
     * @param path 图片存放的路径
     * @return 返回压缩后图片的存放路径
     */
    public static void saveBitmap(String path, OnSaveSuccessListener onSaveSuccessListener) {
        String compressdPicPath = "";

//      ★★★★★★★★★★★★★★重点★★★★★★★★★★★★★
      /*  //★如果不压缩直接从path获取bitmap，这个bitmap会很大，下面在压缩文件到100kb时，会循环很多次，
        // ★而且会因为迟迟达不到100k，options一直在递减为负数，直接报错
        //★ 即使原图不是太大，options不会递减为负数，也会循环多次，UI会卡顿，所以不推荐不经过压缩，直接获取到bitmap
        Bitmap bitmap=BitmapFactory.decodeFile(path);*/
//      ★★★★★★★★★★★★★★重点★★★★★★★★★★★★★
        Bitmap bitmap = decodeSampledBitmapFromPath(path, 720, 1280);
        if (bitmap == null) return;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        /* options表示 如果不压缩是100，表示压缩率为0。如果是70，就表示压缩率是70，表示压缩30%; */
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        while (baos.toByteArray().length / 1024 > 200) {
// 循环判断如果压缩后图片是否大于500kb继续压缩

            baos.reset();
            options -= 10;
            if (options < 11) {//为了防止图片大小一直达不到200kb，options一直在递减，当options<0时，下面的方法会报错
                // 也就是说即使达不到200kb，也就压缩到10了
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                break;
            }
// 这里压缩options%，把压缩后的数据存放到baos中
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }

        String mDir = Environment.getExternalStorageDirectory() + "/FNComman";
        File dir = new File(mDir);
        if (!dir.exists()) {
            dir.mkdirs();//文件不存在，则创建文件
        }
        String fileName = String.valueOf(System.currentTimeMillis());
        File file = new File(mDir, fileName + ".jpg");
        FileOutputStream fOut = null;

        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(baos.toByteArray());
            out.flush();
            out.close();
            onSaveSuccessListener.onSuccess(file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据图片要显示的宽和高，对图片进行压缩，避免OOM
     *
     * @param path
     * @param width  要显示的imageview的宽度
     * @param height 要显示的imageview的高度
     * @return
     */
    private static Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {

//      获取图片的宽和高，并不把他加载到内存当中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = caculateInSampleSize(options, width, height);
//      使用获取到的inSampleSize再次解析图片(此时options里已经含有压缩比 options.inSampleSize，再次解析会得到压缩后的图片，不会oom了 )
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;

    }

    /**
     * 根据需求的宽和高以及图片实际的宽和高计算SampleSize
     *
     * @param options
     * @param reqWidth  要显示的imageview的宽度
     * @param reqHeight 要显示的imageview的高度
     * @return
     * @compressExpand 这个值是为了像预览图片这样的需求，他要比所要显示的imageview高宽要大一点，放大才能清晰
     */
    private static int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;

        int inSampleSize = 1;

        if (width >= reqWidth || height >= reqHeight) {

            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(width * 1.0f / reqHeight);

            inSampleSize = Math.max(widthRadio, heightRadio);

        }

        return inSampleSize;
    }

    public static String[] getLeftTimeFormatedStrings(long leftTime) {
        String days = "00";
        String hours = "00";
        String minutes = "00";
        String seconds = "00";
        String millisSeconds = "000";

        if (leftTime > 0) {
            //毫秒
            long millisValue = leftTime % 1000;
            if (millisValue > 100) {
                millisSeconds = String.valueOf(
                        millisValue);
            } else if (millisValue >= 10 && millisValue < 100) {
                millisSeconds = "0" + millisValue;
            } else {
                millisSeconds = "00" + millisValue;
            }

            //实际多少秒
            long trueSeconds = leftTime / 1000;
            //当前的秒
            long secondValue = trueSeconds % 60;
            if (secondValue < 10) {
                seconds = "0" + secondValue;
            } else {
                seconds = String.valueOf(secondValue);
            }

            //当前的分
            long trueMinutes = trueSeconds / 60;
            long minuteValue = trueMinutes % 60;
            if (minuteValue < 10) {
                minutes = "0" + minuteValue;
            } else {
                minutes = String.valueOf(minuteValue);
            }


            //当前的小时数
            long trueHours = trueMinutes / 60;
            long hourValue = trueHours % 24;
            if (hourValue < 10) {
                hours = "0" + hourValue;
            } else {
                hours = String.valueOf(hourValue);
            }

            //当前的天数
            long dayValue = trueHours / 24;
            if (dayValue < 10) {
                days = "0" + dayValue;
            } else {
                days = String.valueOf(dayValue);
            }
        }
        return new String[]{days, hours, minutes, seconds, millisSeconds};
    }


    public static String getJSONLastNews() throws Exception {
//        String path = "https://lishipeng.oss-cn-hangzhou.aliyuncs.com/testdomain.json";//测试
        String path = "https://chainup.oss-accelerate.aliyuncs.com/update.json";
        HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            try {
                InputStream json = conn.getInputStream();
                String str = getStringFromInputStream(json);
                return str;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String getStringFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 模板代码 必须熟练
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();// 把流中的数据转换成字符串,采用的编码是utf-8(模拟器默认编码)
        os.close();
        return state;
    }


    public static String getJSONLastNews(String path) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            InputStream json = conn.getInputStream();
            String str = getStringFromInputStream(json);
            return str;
        }
        return null;
    }

    public static String getAPIInsideString(String str) {
        if (str.indexOf(".") < 0) {
            return "";
        }
        if (str.lastIndexOf("/") < 0) {
            return "";
        }
        if (str.contains("/hongbaoapi")) {
            return str.substring(str.indexOf(".") + ".".length(), str.indexOf("/hongbaoapi"));
        } else if (str.contains("/kline-api")) {
            return str.substring(str.indexOf(".") + ".".length(), str.indexOf("/kline-api"));
        } else if (str.contains("/otc-chat")) {
            return str.substring(str.indexOf(".") + ".".length(), str.indexOf("/otc-chat"));
        } else if (str.contains("/wsswap/realTime")) {
            return str.substring(str.indexOf(".") + ".".length(), str.indexOf("/wsswap/realTime"));
        } else if (str.contains("/contract-kline-api/ws")) {
            return str.substring(str.indexOf(".") + ".".length(), str.indexOf("/contract-kline-api/ws"));
        } else {
            return str.substring(str.indexOf(".") + ".".length(), str.lastIndexOf("/"));
        }
    }

    public static String getAPIHostInsideString(String str) {
        if (str.indexOf("//") < 0) {
            return "";
        }
        if (str.lastIndexOf("/") < 0) {
            return "";
        }
        if (str.contains("/hongbaoapi")) {
            return str.substring(str.indexOf("//") + "//".length(), str.indexOf("/hongbaoapi"));
        } else if (str.contains("/kline-api")) {
            return str.substring(str.indexOf("//") + "//".length(), str.indexOf("/kline-api"));
        } else if (str.contains("/otc-chat")) {
            return str.substring(str.indexOf("//") + "//".length(), str.indexOf("/otc-chat"));
        } else if (str.contains("/wsswap/realTime")) {
            return str.substring(str.indexOf("//") + "//".length(), str.indexOf("/wsswap/realTime"));
        } else if (str.contains("/contract-kline-api/ws")) {
            return str.substring(str.indexOf("//") + "//".length(), str.indexOf("/contract-kline-api/ws"));
        } else {
            return str.substring(str.indexOf("//") + "//".length(), str.lastIndexOf("/"));
        }
    }


    public static InputStream read_user(String filename) throws Exception {
        FileInputStream inStream = ChainUpApp.appContext.openFileInput(filename);
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();//得到文件的二进制数据
        Log.i("jinlong", new String(data));
        InputStream is = new ByteArrayInputStream(data);
        outStream.close();
        inStream.close();
        return is;
    }

    public static String returnSpeedUrl(String url, String mainUrl) {
        String domain = getAPIInsideString(mainUrl);
        return returnReplaceUrl(mainUrl, domain, url);
    }


    public static String returnAPIUrl(String url) {

        String domain = getAPIInsideString(url);
        String apiHost = getAPIHostInsideString(url);
        ArrayList<JSONObject> specialList = PublicInfoDataService.getInstance().getSpecialList();
        String text = PublicInfoDataService.getInstance().getTextDoMain();
        String domainUrl = PublicInfoDataService.getInstance().getNewWorkURL();

        Log.e("jinlong", "text：" + apiHost);
        if (null == specialList || specialList.size() == 0) {
//            if (!TextUtils.isEmpty(text)) {
//                ArrayList<JSONObject> textList = PublicInfoDataService.getInstance().getTextList();
//                for (JSONObject json : textList) {
//                    if (null != json && json.length() > 0) {
//                        if (json.optString("host").contains(apiHost)) {
//                            return returnReplaceUrl(url, domain, json.optString("force_domain"));
//                        }
//                    }
//                }
//            }


            if (TextUtils.isEmpty(domainUrl)) {
                PublicInfoDataService.getInstance().saveNewWorkURL(domain);
                return url;
            } else {
                return returnReplaceUrl(url, domain, domainUrl);
            }

        } else {
            for (JSONObject json : specialList) {
                if (null != json && json.length() > 0) {
                    if (json.optString("host").equals(apiHost)) {
                        return returnReplaceUrl(url, domain, json.optString("force_domain"));
                    }
                }
            }
//            if (!TextUtils.isEmpty(text)) {
//                ArrayList<JSONObject> textList = PublicInfoDataService.getInstance().getTextList();
//                if (textList != null) {
//                    for (JSONObject json : textList) {
//                        if (null != json && json.length() > 0) {
//                            if (json.optString("host").equals(apiHost)) {
//                                return returnReplaceUrl(url, domain, json.optString("saas_domain"));
//                            }
//                        }
//                    }
//                }
//            }

            if (TextUtils.isEmpty(domainUrl)) {
                PublicInfoDataService.getInstance().saveNewWorkURL(domain);
                return url;
            } else {
                return returnReplaceUrl(url, domain, domainUrl);
            }

        }

//        return returnReplaceUrl(url, domain, PublicInfoDataService.getInstance().getDoMain());
    }


    public static String returnReplaceUrl(String normalUrl, String domain, String replaceUrl) {
        String url = normalUrl;

        url = normalUrl.replace(domain, replaceUrl);
        return url;
    }


    public static String getSpecialList(String url, String domain, String replaceUrl) {

        if (url.contains("/hongbaoapi")) {
            return url.replace(domain, "service." + replaceUrl);
        } else if (url.contains("/kline-api")) {
            return url.replace(domain, "ws." + replaceUrl);

        } else if (url.contains("/otc-chat")) {
            return url.replace(domain, "ws2." + replaceUrl);

        } else if (url.contains("/wsswap/realTime")) {
            return url.replace(domain, "ws3." + replaceUrl);

        } else if (url.contains("/contract-kline-api/ws")) {
            return url.replace(domain, "ws3." + replaceUrl);

        } else if (url.contains("otcappapi")) {
            return url.replace(domain, "otcappapi." + replaceUrl);

        } else if (url.contains("coappapi")) {
            return url.replace(domain, "coappapi." + replaceUrl);

        } else {
            return url.replace(domain, "appapi." + replaceUrl);
        }
    }

}


