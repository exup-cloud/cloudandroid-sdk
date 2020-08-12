package com.yjkj.chainup.contract.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;

import com.yjkj.chainup.R;
import com.yjkj.chainup.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareToolUtil {
    public static final String AUTHORITY = ".fileProvider";
    private static String sharePicName = "share_pic.jpg";
    public static final int REQUEST_PERMISSION_CODE = 15;

    public static void sendLocalShare(Context context, Bitmap bmp) {
        try {
            final File shareFile = ShareToolUtil.saveSharePic(context, bmp);
            LogUtil.d("DEBUG", "shareFile:" + shareFile);
            Intent intent = new Intent(Intent.ACTION_SEND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + AUTHORITY, shareFile);
                intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shareFile));
            }
            intent.setType("image/*");
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(Intent.createChooser(intent, context.getString(R.string.contract_share_label)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File saveImageToGallery(Context context, Bitmap bitmap) {
        if (isSDcardExist()) {
            File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsoluteFile();
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            String fileName = "img_" + System.currentTimeMillis() + ".png";
            File filePic = new File(appDir,fileName);
            try {
                FileOutputStream out = new FileOutputStream(filePic);
                if (bitmap == null) {
                    return null;
                }
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                LogUtil.d("DEBUG", "----" + filePic.getAbsolutePath());
                MediaStore.Images.Media.insertImage(context.getContentResolver(), filePic.getAbsolutePath(), fileName, null);
                //保存图片后发送广播通知更新数据库
                Uri uri = Uri.fromFile(filePic);
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                return filePic;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        return null;
    }

    public static File saveSharePic(Context context, Bitmap bitmap) {
        String sharePicPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator;
        if (isSDcardExist()) {
            File file = new File(sharePicPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            File filePic = new File(sharePicPath, sharePicName);
            if (!filePic.exists()) {
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
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(filePic);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
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
        boolean permission = PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", "com.share.gudd.intentshare");
        if (permission) {
            // 有这个权限
        } else {
            // 没有这个权限
            // 如果android版本大于6.0，需要动态申请权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            }
        }

        permission = PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.READ_EXTERNAL_STORAGE", "com.share.gudd.intentshare");
        if (permission) {
            // 有这个权限
        } else {
            // 没有这个权限
            // 如果android版本大于6.0，需要动态申请权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            }
        }
    }

}
