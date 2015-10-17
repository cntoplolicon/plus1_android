package swj.swj.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import swj.swj.R;

/**
 * Created by Administrator on 2015/10/15.
 */
public class CommonDialog extends Activity {

    public static void showDialog(Context context, String dialogMsg) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        Display display = window.getWindowManager().getDefaultDisplay();
        lp.width = (int) (display.getWidth() * 0.9);
        window.setContentView(R.layout.custom_dialog);
        TextView tvReminder = (TextView) window.findViewById(R.id.tv_reminder);
        tvReminder.setText(dialogMsg);
        window.setAttributes(lp);
    }

    public static void showDialog(Context context, int resourceId) {
        showDialog(context, context.getResources().getString(resourceId));
    }
}
