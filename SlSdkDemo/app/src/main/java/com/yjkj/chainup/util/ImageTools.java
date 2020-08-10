package com.yjkj.chainup.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.yjkj.chainup.db.constant.ParamConstant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageTools {
    private boolean fromFragment;
    private Fragment mFragment;
    private Context mContext;

    //显示选择对话框
    private String[] mItems = new String[]{"相机", "图库"};

    //默认保存路径
    private String mFolderString = "/FNComman/";

    private String mPath;

    //是否裁剪
    private boolean isClip;

    //相机标记  在onAcitvityResult中可以switch这个标记
    public static final int CAMERA = 300;

    //图库标记  在onAcitivtyResult中可以switch这个标记
    public static final int GALLERY = 301;

    //裁剪标记  在onAcitivtyResult中可以switch这个标记
    public static final int BITMAP = 302;

    //默认图片最大高度
    private int defaultHeight = 720;

    //默认图片最大宽度
    private int defaultWidth = 1280;


    /**
     * 默认裁剪后的宽度
     */
    private int defaultClipWidth = 320;
    /**
     * 默认裁剪后的高度
     */
    private int defaultClipHeight = 190;

    private ImageTools() {
    }

    public ImageTools(Fragment fragment) {
        this.mFragment = fragment;
        this.mContext = fragment.getActivity();
        fromFragment = true;
        initFile();
    }

    public ImageTools(Activity activity) {
        this.mContext = activity;
        initFile();
    }

    private void initFile() {
        File file = new File(Environment.getExternalStorageDirectory() + mFolderString);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(file + ".nomedia");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                try {
                    throw new IOException("无法创建" + file.toString() + "文件");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    private void getWriteFilePermission() {

    }

    //设置裁剪宽和高
    public void setClipWidth(int width) {
        defaultClipWidth = width;
    }

    public void setClipHeight(int height) {
        defaultClipHeight = height;
    }


    public void enableClip(boolean isClip) {
        this.isClip = isClip;
    }


    //显示相机或图库的对话框
    public void showGetImageDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setItems(mItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        openCamera("");
                        break;
                    case 1:
                        openGallery("");
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }


    //

    /**
     * 打开相机
     * <p>
     * OPPO 5.1手机报ActivityNotFoundException
     * 需要try...catch处理
     */
    public void openCamera(String index) {
        //获取系統版本
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // 判断存储卡是否可以用，可用进行存储
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + mFolderString;
            mPath += System.currentTimeMillis() + ".jpg";
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra("INDEX_NAME", index);
            if (currentapiVersion < 24) {
                // 从文件中创建uri
                Uri uri = Uri.fromFile(new File(mPath));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            } else {
                //兼容android7.0 使用共享文件的形式
                ContentValues contentValues = new ContentValues(1);
                File saveFile = new File(mPath);
                contentValues.put(MediaStore.Images.Media.DATA, saveFile.getAbsolutePath());
                Uri uri = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
            try {
                if (fromFragment) {
                    mFragment.startActivityForResult(intent, CAMERA);
                } else {
                    ((Activity) mContext).startActivityForResult(intent, CAMERA);
                }
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(mContext, "未检测到CDcard，拍照不可用!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 拍照
     */
    public void takePhoto() {
        //获取系統版本
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // 判断存储卡是否可以用，可用进行存储
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + mFolderString;
            mPath += System.currentTimeMillis() + ".jpg";
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (currentapiVersion < 24) {
                // 从文件中创建uri
                Uri uri = Uri.fromFile(new File(mPath));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            } else {
                //兼容android7.0 使用共享文件的形式
                ContentValues contentValues = new ContentValues(1);
                File saveFile = new File(mPath);
                contentValues.put(MediaStore.Images.Media.DATA, saveFile.getAbsolutePath());
                Uri uri = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
            try {
                if (fromFragment) {
                    mFragment.startActivityForResult(intent, CAMERA);
                } else {
                    ((Activity) mContext).startActivityForResult(intent, CAMERA);
                }
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(mContext, "未检测到CDcard，拍照不可用!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void setmPath(String path) {
        mPath = path;
    }


    //打开相册
    public void openGallery(String index) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.putExtra("INDEX_NAME", index);
        if (fromFragment) {
            mFragment.startActivityForResult(intent, GALLERY);
        } else {
            ((Activity) mContext).startActivityForResult(intent, GALLERY);
        }
    }


    public boolean onAcitvityResult(int requestCode, int resultCode,
                                    Intent data, OnBitmapCreateListener listener) {
        if (resultCode != Activity.RESULT_OK) return false;
        switch (requestCode) {
            case CAMERA:
                if (isClip) {
                    getBitmapFromCamera(new OnBitmapCreateListener() {
                        @Override
                        public void onBitmapCreate(Bitmap bitmap, String path) {
                            startZoomPhoto(Uri.fromFile(new File(path)),
                                    defaultClipWidth, defaultClipHeight);
                        }
                    });
                } else {
                    getBitmapFromCamera(listener);
                }
                break;
            case GALLERY:
                Uri selectedImage = data.getData();
                String[] filePathColum = {MediaStore.Images.Media.DATA};
                Cursor cursor = mContext.getContentResolver().query(selectedImage, filePathColum, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColum[0]);
                String filePath = cursor.getString(columnIndex);
                if (isClip) {
                    startZoomPhoto(Uri.fromFile(new File(filePath)),
                            defaultClipWidth, defaultClipHeight);
                } else {
                    listener.onBitmapCreate(getBitmapFromGallery(data), filePath);
                }
                cursor.close();
                break;
            case BITMAP:
                Bitmap bitmap = getBitmapFromZoomPhoto(data);
                String path = newFile();
                File file = new File(path);
                FileOutputStream fileOutputStream = null;
                try {
                    file.createNewFile();
                    fileOutputStream = new FileOutputStream(path);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                listener.onBitmapCreate(bitmap, path);
        }
        return true;
    }


    //从相机获取bitmap
    public void getBitmapFromCamera(final OnBitmapCreateListener listener) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                listener.onBitmapCreate((Bitmap) msg.obj, mPath);
            }
        };
        new Thread() {
            @Override
            public void run() {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mPath, options);
                int size = calculateInSampleSize(options, defaultWidth, defaultHeight);
                options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inSampleSize = size;
                Bitmap bitmap = BitmapFactory.decodeFile(mPath, options);
                Message message = handler.obtainMessage();
                message.obj = bitmap;
                message.what = 0;
                handler.sendMessage(message);
            }
        }.start();
    }

    //从图库获取位图
    public Bitmap getBitmapFromGallery(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(filePath, options);
                int size = calculateInSampleSize(options, defaultWidth, defaultHeight);
                options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inSampleSize = size;
                return BitmapFactory.decodeFile(filePath, options);
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    //计算图片的缩放值
    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {

        int width = options.outWidth;     //获取图片的宽
        int height = options.outHeight;   //获取图片的高
        int inSampleSize = 4;
        if (height > reqHeight || width > reqWidth) {
            int heightRatio = Math.round(height / reqHeight);
            int widthRatio = Math.round(width / reqHeight);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    /**
     * 裁剪图片
     *
     * @param uri     图片路径URL  可以用uri.FromFile(File)获取
     * @param outputX 裁剪的宽度
     * @param outputY 裁剪的高度
     */
    public void startZoomPhoto(Uri uri, int outputX, int outputY) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //设置裁剪
        intent.putExtra("crop", "true");
        //aspectX  aspectY  是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //outputX  outputY  是裁剪的宽高
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("return-data", true);
        try {
            if (fromFragment) {
                mFragment.startActivityForResult(intent, BITMAP);
            } else {
                ((Activity) mContext).startActivityForResult(intent, BITMAP);
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "未找到可以剪裁图片的程序", Toast.LENGTH_SHORT).show();
        }
    }

    //获取裁剪后的图片
    public Bitmap getBitmapFromZoomPhoto(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            return extras.getParcelable("data");
        }
        return null;
    }

    public String newFile() {
        File file = new File(mContext.getExternalFilesDir(Environment.MEDIA_MOUNTED), "common");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg";
    }


    //处理完bitmap的回调
    public interface OnBitmapCreateListener {
        void onBitmapCreate(Bitmap bitmap, String path);
    }

    //bitmap转String
    public String bitmap2Base64(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        }
        return null;
    }


    /**
     * 保存文件到系统相册
     *
     * @param context
     * @param bmp
     * @return
     */
    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();

            //把文件插入到系统图库
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            return isSuccess;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 保存文件到系统相册
     *
     * @param context
     * @param bmp
     * @return
     */
    public static Uri saveImage2Gallery(Context context, String imageSign, Bitmap bmp) {
        // 首先保存图片
        try {
            String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + imageSign + ".jpg";
            File file = new File(storePath);
            ContentValues contentValues = new ContentValues(1);
            File saveFile = new File(file.getAbsolutePath());
            contentValues.put(MediaStore.Images.Media.DATA, saveFile.getAbsolutePath());
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } else {
                Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                return uri;
            }

            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
            //把文件插入到系统图库
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            //保存图片后发送广播通知更新数据库
//            Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", file);

            //兼容android7.0 使用共享文件的形式

            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            Log.d("XXXXXX", "=======URI:==========" + uri);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            return uri;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
