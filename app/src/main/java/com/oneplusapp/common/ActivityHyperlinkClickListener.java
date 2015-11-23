package com.oneplusapp.common;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewStub;

public class ActivityHyperlinkClickListener implements ViewStub.OnClickListener {

    private Context context;
    private Class<?> activity;

    public ActivityHyperlinkClickListener(Context context, Class<?> activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        context.startActivity(new Intent(context, activity));
    }


}
