package com.oneplusapp.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oneplusapp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MenuDialog extends AlertDialog {

    private boolean autoCancel;
    private List<String> buttonText = new ArrayList<>();
    private List<View.OnClickListener> buttonOnClickListeners = new ArrayList<>();

    @Bind(R.id.ll_menu_layout)
    LinearLayout menuLayout;

    public MenuDialog(Context context, boolean autoCancel) {
        super(context);
        this.autoCancel = autoCancel;
    }

    public MenuDialog(Context context) {
        this(context, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setCanceledOnTouchOutside(false);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setContentView(R.layout.menu_dialog);
        ButterKnife.bind(this);

        for (int i = 0; i < buttonText.size(); i++) {
            String text = buttonText.get(i);
            View.OnClickListener onClickListener = buttonOnClickListeners.get(i);
            TextView textView = (TextView) getLayoutInflater().inflate(R.layout.menu_dialog_item, menuLayout, false);
            textView.setText(text);
            textView.setOnClickListener(onClickListener);
            menuLayout.addView(textView);
        }
    }

    public void addButton(int resourceId, View.OnClickListener onClickListener) {
        String text = getContext().getResources().getString(resourceId);
        addButton(text, onClickListener);
    }

    public void addButton(String text, final View.OnClickListener onClickListener) {
        buttonText.add(text);
        if (autoCancel) {
            buttonOnClickListeners.add(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(v);
                    cancel();
                }
            });
        } else {
            buttonOnClickListeners.add(onClickListener);
        }
    }

    public void addCancelButton() {
        buttonText.add(getContext().getResources().getString(R.string.cancel));
        buttonOnClickListeners.add(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    public static void showConfirmDialog(Context context, String text, View.OnClickListener onClickListener) {
        MenuDialog dialog = new MenuDialog(context);
        dialog.addButton(text, onClickListener);
        dialog.addCancelButton();
        dialog.show();
    }

    public static void showConfirmDialog(Context context, int resId, View.OnClickListener onClickListener) {
        String text = context.getResources().getString(resId);
        showConfirmDialog(context, text, onClickListener);
    }
}