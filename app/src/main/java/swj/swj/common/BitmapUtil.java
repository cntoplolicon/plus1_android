package swj.swj.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public final class BitmapUtil {

    public static Bitmap getImage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 960f;
        float ww = 960f;
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) {
            be = 1;
        }
        newOpts.inSampleSize = be;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);
    }

    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        int options = 90;
        while (outputStream.toByteArray().length / 1024 > 100) {
            outputStream.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, outputStream);
            options -= 10;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(outputStream.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    public static String saveBitmap(Bitmap bm) {
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + "myImages");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileNames = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date()) + ".jpg";
        File file = new File(dir, fileNames);
        String absolutePath = file.getAbsolutePath();
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        } catch (IOException e) {
            Log.e(BitmapUtil.class.getName(), "failed saving bitmap", e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.e(BitmapUtil.class.getName(), "failed closing fileOutputStream", e);
            }
        }
        return absolutePath;
    }
}

