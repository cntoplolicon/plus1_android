package swj.swj.common;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public final class BitmapUtil {

    private static int MAX_IMAGE_WIDTH = 960;
    private static int MAX_IMAGE_HEIGHT = 960;
    private static int MAX_IMAGE_BYTES = 150 * 1024;

    public static File prepareBitmapForUploading(Uri uri) {
        Bitmap bitmap = loadScaledBitmap(uri);
        ByteArrayOutputStream byteArrayOutputStream = reduceQualityToMatchCapacity(bitmap, MAX_IMAGE_BYTES);
        File result = saveCompressedBitmap(byteArrayOutputStream);
        bitmap.recycle();
        return result;
    }

    private static Bitmap loadScaledBitmap(Uri uri) {
        ImageSize imageSize = new ImageSize(MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT);
        DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
                .considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).build();
        return ImageLoader.getInstance().loadImageSync(uri.toString(),
                imageSize, displayOptions);
    }

    private static ByteArrayOutputStream reduceQualityToMatchCapacity(Bitmap image, int capacity) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int quality = 100; quality > 0; quality -= 5) {
            byteArrayOutputStream.reset();
            image.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
            if (byteArrayOutputStream.size() <= capacity) {
                break;
            }
        }
        return byteArrayOutputStream;
    }

    private static File saveCompressedBitmap(ByteArrayOutputStream byteArrayOutputStream) {
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + "myImages");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filename = new SimpleDateFormat("yyyy-MM-dd_hhmmss").format(new Date()) + ".jpg";
        File file = new File(dir, filename);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            byteArrayOutputStream.writeTo(fileOutputStream);
        } catch (IOException e) {
            Log.e(BitmapUtil.class.getName(), "failed saving bitmap", e);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                Log.e(BitmapUtil.class.getName(), "failed closing fileOutputStream", e);
            }
        }
        return file;
    }
}

