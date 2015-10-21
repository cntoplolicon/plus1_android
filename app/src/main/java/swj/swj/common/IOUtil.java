package swj.swj.common;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by cntoplolicon on 10/21/15.
 */
public class IOUtil {

    public static void closeSilently(Closeable stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        } catch (IOException e) {
            Log.e(IOUtil.class.getName(), "failed closing input stream", e);
        }
    }
}
