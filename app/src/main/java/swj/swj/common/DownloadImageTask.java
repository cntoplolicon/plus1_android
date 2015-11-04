package swj.swj.common;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import swj.swj.R;

/**
 * Created by silentgod on 15-11-4.
 */
public final class DownloadImageTask extends AsyncTask<String, Context, Void> {
    private File imageFile;
    private Exception exception;
    private Context context;

    public DownloadImageTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        imageFile = BitmapUtil.getImageFile();
    }

    @Override
    protected Void doInBackground(String... params) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            URL url = new URL(params[0]);
            inputStream = url.openStream();
            outputStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[1024 * 1024];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            exception = e;
        } finally {
            IOUtil.closeSilently(inputStream);
            IOUtil.closeSilently(outputStream);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (exception != null) {
            Log.e("download image error", "an exception occurred while downloading image", exception);
            return;
        }
        Toast.makeText(context, R.string.downloading_finish, Toast.LENGTH_LONG).show();
    }
}
