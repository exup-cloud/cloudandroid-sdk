package com.bmtc.sdk.contract.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareToolUtil {
    public static final String AUTHORITY = ".fileProvider";
    private static String sharePicName = "share_pic.jpg";
    private static String sharePicPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"intentShare"+ File.separator+"sharepic"+ File.separator;
    public static final int REQUEST_PERMISSION_CODE  = 15;

    public static final int WX_SEND_PEOPLE = 1001;//微信朋友
    public static final int WX_SEND_FIRENDS = 1002;//微信朋友圈

    /**
     * 微信本地分享
     * @param context
     * @param type   WX_SEND_FIRENDS 分享到朋友圈，WX_SEND_PEOPLE 分享给朋友
     */
    public static void sendLocalShare(Context context, int type,Bitmap bmp){
        try {
            final File file = ShareToolUtil.saveSharePic(context,bmp);
            if(type == WX_SEND_PEOPLE ){
                NativeShareTool.getInstance((Activity) context).shareWechatFriend(file,true);
            }else if(type == WX_SEND_FIRENDS ){
                NativeShareTool.getInstance((Activity) context).shareWechatMoment(file);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(file!=null){
                        file.delete();
                    }
                }
            },5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendLocalShare(Context context, Bitmap bmp){
        try {
            final File shareFile = ShareToolUtil.saveSharePic(context,bmp);
            Intent intent = new Intent(Intent.ACTION_SEND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName()+ShareToolUtil.AUTHORITY, shareFile);
                intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }else {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shareFile));
            }
            intent.setType("image/*");
            if(intent.resolveActivity(context.getPackageManager()) != null){
                context.startActivity(Intent.createChooser(intent, "分享到"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File saveSharePic(Context context, Bitmap bitmap){

        if (isSDcardExist()){
            File file = new File(sharePicPath);
            if (!file.exists()){
                file.mkdirs();
            }
            File filePic = new File(sharePicPath,sharePicName);
            if (filePic.exists()){
                filePic.delete();
            }
            try {
                FileOutputStream out = new FileOutputStream(filePic);
                if (bitmap == null) {
                    return null;
                   // bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.share_pic_horse);
                }
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return filePic;
        }

        return null;
    }

    /**
     * 判断存储卡是否存在
     */
    public static boolean isSDcardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static void getPermission(Context context) {
        PackageManager packageManager = context.getPackageManager();
        boolean permission = PackageManager.PERMISSION_GRANTED  == packageManager.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE","com.share.gudd.intentshare");
        if (permission) {
            // 有这个权限
        }else{
            // 没有这个权限
            // 如果android版本大于6.0，需要动态申请权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((Activity) context).requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSION_CODE);
            }
        }

        permission = PackageManager.PERMISSION_GRANTED  == packageManager.checkPermission("android.permission.READ_EXTERNAL_STORAGE","com.share.gudd.intentshare");
        if (permission) {
            // 有这个权限
        }else{
            // 没有这个权限
            // 如果android版本大于6.0，需要动态申请权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((Activity) context).requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_PERMISSION_CODE);
            }
        }
    }
}
