package swj.swj.common;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

import swj.swj.R;

/**
 * Created by Administrator on 2015/10/15.
 */
public class CommonDialog {
    public static void showDialog(Context context, String dialogMsg) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.custom_dialog);
        TextView tvCancel = (TextView) window.findViewById(R.id.tv_reminder);
        tvCancel.setText(dialogMsg);
    }
}
