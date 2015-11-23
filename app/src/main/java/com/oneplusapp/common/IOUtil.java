package com.oneplusapp.common;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;

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
