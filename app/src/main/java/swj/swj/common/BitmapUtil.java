package swj.swj.common;

import android.content.Context;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public final class BitmapUtil {

    private static int MAX_IMAGE_WIDTH = 960;
    private static int MAX_IMAGE_HEIGHT = 960;
    private static int MAX_IMAGE_BYTES = 150 * 1024;

    public static ImageFileInfo prepareBitmapForUploading(Context context, Uri uri) {
        File outputFile = getOutputFile();
        if (uri.getScheme().equals("content")) {
            uri = saveUriToFile(context, uri, outputFile);
        }
        Bitmap bitmap = loadScaledBitmap(uri);
        ByteArrayOutputStream byteArrayOutputStream = reduceQualityToMatchCapacity(bitmap, MAX_IMAGE_BYTES);
        File result = saveCompressedBitmap(byteArrayOutputStream, outputFile);
        ImageFileInfo imageFileInfo = new ImageFileInfo(result, new ImageSize(bitmap.getWidth(), bitmap.getHeight()));
        bitmap.recycle();
        return imageFileInfo;
    }

    private static File getOutputFile() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + "myImages");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filename = new SimpleDateFormat("yyyy-MM-dd_hhmmss").format(new Date()) + ".jpg";
        return new File(dir, filename);
    }

    private static Uri saveUriToFile(Context context, Uri uri, File file) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024 * 1024];
            int count;
            while ((count = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }
            return Uri.fromFile(file);
        } catch (FileNotFoundException e) {
            Log.e(BitmapUtil.class.getName(), "failed opening file", e);
            return uri;
        } catch (IOException e) {
            Log.e(BitmapUtil.class.getName(), "failed writing to file", e);
            return uri;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(BitmapUtil.class.getName(), "failed closing input stream", e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.e(BitmapUtil.class.getName(), "failed closing input stream", e);
                }
            }
        }
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

    private static File saveCompressedBitmap(ByteArrayOutputStream byteArrayOutputStream, File file) {
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

    public static class ImageFileInfo {
        private File file;
        private ImageSize imageSize;

        public ImageFileInfo(File file, ImageSize imageSize) {
            this.file = file;
            this.imageSize = imageSize;
        }

        public File getFile() {
            return file;
        }

        public ImageSize getImageSize() {
            return imageSize;
        }
    }
}

