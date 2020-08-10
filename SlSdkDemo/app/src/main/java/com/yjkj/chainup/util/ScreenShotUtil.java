package com.yjkj.chainup.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 获取屏幕当前页面的截图
 * Created by ykn on 2018/4/17.
 */

public class ScreenShotUtil {

    /**
     * 当超过一屏时，截取scrollview的屏幕
     *
     * @param scrollView
     * @return
     */
    public static Bitmap getBitmapByView(Context context, ScrollView scrollView, int resourceId) {
        int childHeight = 0;
        Paint paint = new Paint();
        Matrix matrix = new Matrix();
        // 获取scrollview实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            childHeight += scrollView.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        Bitmap bitmap = Bitmap.createBitmap(scrollView.getWidth(), childHeight,
                Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        // 设置背景
        BitmapDrawable bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(resourceId);
        Bitmap backgroundBitmap = bitmapDrawable.getBitmap();
        matrix.postScale(0.8f, 1f);
        canvas.drawBitmap(backgroundBitmap, matrix, paint);
        scrollView.draw(canvas);
        Log.d("yxy", "getBitmapByView: " + bitmap.getByteCount());
        return compressImage(bitmap, Bitmap.CompressFormat.JPEG);
    }

    /**
     * 当不超过一屏时，可调用此方法把当前view上显示的内容转化成bitmap
     *
     * @param view 需要获取的图片的view
     * @return 返回bitmap
     */
    public static Bitmap getScreenshotBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();  //启用DrawingCache并创建位图
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache()); //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        view.setDrawingCacheEnabled(false);  //禁用DrawingCahce否则会影响性能
        return bitmap;
    }

    /**
     * 压缩图片
     *
     * @param image
     * @return
     */
    private static Bitmap compressImage(Bitmap image, Bitmap.CompressFormat type) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Log.d("yxy", "compressImage0: " + image.getByteCount());
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(type, 100, baos);
        Log.d("yxy", "compressImage1: " + baos.toByteArray().length);
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }


    /**
     * 拼接两个图片
     *
     * @param first
     * @param second
     * @return
     */
    public static Bitmap spliceBitmap(Context context, Bitmap first, Bitmap second) {
        int width = first.getWidth();
        int height = first.getHeight();
        Bitmap newSecond = compressImage(second, Bitmap.CompressFormat.PNG);
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(first, 0, 0, null);
        canvas.drawBitmap(newSecond, 0, first.getHeight() - second.getHeight(), null);
        return result;
    }


    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     *
     * @param phoneNum 电话号码
     */
    public static void diallPhone(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }
}
