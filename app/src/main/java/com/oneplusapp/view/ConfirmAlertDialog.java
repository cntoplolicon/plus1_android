package com.oneplusapp.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.oneplusapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jiewei on 11/13/15.
 */
public class ConfirmAlertDialog extends AlertDialog {

    @Bind(R.id.tv_confirms)
    TextView tvConfirm;

    public ConfirmAlertDialog(Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setCanceledOnTouchOutside(false);
        Window window = this.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setContentView(R.layout.confirm_dialog);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.tv_cancel)
    public void onClick(View view) {
        cancel();
    }

    public TextView getConfirmTextView() {
        return tvConfirm;
    }
}
