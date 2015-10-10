package swj.swj.common;

import android.util.Log;
import android.view.View;

public final class ProgressBarUtil {

    public static void LoadProgeressBar(boolean isSendSuccess, boolean isLoadSuccess, View view) {
        if (isSendSuccess) {
            view.setVisibility(View.VISIBLE);
                Log.e("send","success");
            if (isLoadSuccess) {
            }
        }
    }
}
