package swj.swj.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.NonViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.DeferredAsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import swj.swj.R;


public final class BitmapUtil {

    private static int MAX_IMAGE_WIDTH = 960;
    private static int MAX_IMAGE_HEIGHT = 960;

    public static int DEFAULT_QUALITY = 80;

    private BitmapUtil() {
    }

    public static Promise<Bitmap, FailReason, Void> prepareImageForUploading(Context context, Uri uri) {
        DeferredAsyncTask<Object, Void, Uri> normalizeUriTask = new DeferredAsyncTask<Object, Void, Uri>() {
            @Override
            protected Uri doInBackgroundSafe(Object[] params) throws Exception {
                Context context = (Context) params[0];
                Uri uri = (Uri) params[1];
                File tempFile = getTempFile(context);
                File inputFile = CommonMethods.getFileFromMediaUri(context, uri);
                if (inputFile == null) {
                    inputFile = saveUriToFile(context, uri, tempFile);
                }
                if (inputFile != null) {
                    uri = Uri.fromFile(inputFile);
                }
                return uri;
            }
        };
        normalizeUriTask.execute(new Object[]{context, uri});

        final ThrowableDeferredObject<Bitmap, FailReason, Void> deferredObject = new ThrowableDeferredObject<>();
        normalizeUriTask.promise().fail(new ImageProcessingFailureCallback<Throwable>(context) {
            @Override
            public void onFail(Throwable t) {
                super.onFail(t);
                Log.e(BitmapUtil.class.getName(), "failed converting uri to file", t);
            }
        }).then(new DonePipe<Uri, Bitmap, FailReason, Void>() {
            @Override
            public Promise<Bitmap, FailReason, Void> pipeDone(Uri result) {
                ImageSize imageSize = new ImageSize(MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT);
                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(false)
                        .considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).build();
                NonViewAware nonViewAware = new NonViewAware(imageSize, ViewScaleType.FIT_INSIDE);
                ImageLoader.getInstance().displayImage(Uri.decode(result.toString()), nonViewAware, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                deferredObject.reject(failReason);
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                deferredObject.resolve(loadedImage);
                            }
                        });
                return deferredObject.promise();
            }
        }).fail(new ImageProcessingFailureCallback<FailReason>(context));

        return deferredObject.promise();
    }

    private static File getTempFile(Context context) throws IOException {
        File outputDir = context.getCacheDir();
        return File.createTempFile("image", "tmp", outputDir);
    }

    public static File saveUriToFile(Context context, Uri uri, File file) throws IOException {
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
            return file;
        } finally {
            IOUtil.closeSilently(inputStream);
            IOUtil.closeSilently(outputStream);
        }
    }

    public static File getImageFile() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "plus-one-app/");
        if (!dir.exists() && !dir.mkdirs()) {
            Log.e("make dir error", "cannot make dir " + dir.toString());
            return null;
        }
        String filename = "IMG_" + getNowTime() + ".jpg";
        File file = new File(dir, filename);
        return file;
    }

    private static String getNowTime() {
        Date date = new Date();
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
        return dataFormat.format(date);
    }

    public static byte[] compressBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        return outputStream.toByteArray();
    }

    private static class ImageProcessingFailureCallback<T> implements FailCallback<T> {

        private Context context;

        public ImageProcessingFailureCallback(Context context) {
            this.context = context;
        }

        @Override
        public void onFail(T result) {
            Toast.makeText(context, R.string.image_processing_failure, Toast.LENGTH_LONG).show();
        }
    }
}
