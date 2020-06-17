package com.bmtc.sdk.contract.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;


import com.bmtc.sdk.contract.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveImageTask extends AsyncTask<Bitmap, Void, String> {
    Context context;

    public SaveImageTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Bitmap... params) {
        String result = context.getString(R.string.sl_str_save_failed);

        try {
            String sdcard = Environment.getExternalStorageDirectory().toString();
            File file = new File(sdcard + "/Photo");
            if (!file.exists()) {
                file.mkdirs();
            }

            DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = fmt.format(new Date()) + ".jpg";
            File imageFile = new File(file.getAbsolutePath(), fileName);
            FileOutputStream outStream = null;
            outStream = new FileOutputStream(imageFile);
            Bitmap image = params[0];
            image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

            MediaStore.Images.Media.insertImage(context.getContentResolver(), imageFile.getAbsolutePath(), fileName, null);
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(imageFile);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

            outStream.flush();
            outStream.close();
            result = context.getString(R.string.sl_str_save_succeed) + file.getAbsolutePath();
        } catch (Exception e) {
            Log.d("WebView", "SaveImageTask " + e.toString());
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }
}