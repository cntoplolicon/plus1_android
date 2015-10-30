package swj.swj.common;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import swj.swj.R;

/**
 * Created by Administrator on 2015/10/15.
 */
public class CommonDialog {

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

    public static void showDialogUpdateVersions(final Context context, String dialogMsg) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        Display display = window.getWindowManager().getDefaultDisplay();
        lp.width = (int) (display.getWidth() * 0.8);
        window.setContentView(R.layout.custom_dialog_update_versions);
        TextView tvTitle = (TextView) window.findViewById(R.id.tv_title);
        tvTitle.setText("有新版本啦");
        TextView tvUnConfirm = (TextView) window.findViewById(R.id.tv_un_confirm);
        tvUnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        TextView tvConfirm = (TextView) window.findViewById(R.id.tv_confirm);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "你真好，亲爱的", Toast.LENGTH_SHORT).show();
            }
        });
        TextView tvReminder = (TextView) window.findViewById(R.id.tv_reminder);
        tvReminder.setText(dialogMsg);
        window.setAttributes(lp);
    }
    public static void showDialogUpdateVersions(Context context, int resourceId) {
        showDialogUpdateVersions(context, context.getResources().getString(resourceId));
    }
}
