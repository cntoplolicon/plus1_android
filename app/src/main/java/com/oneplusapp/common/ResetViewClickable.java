package com.oneplusapp.common;

import android.view.View;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Promise;

public class ResetViewClickable<D, R> implements AlwaysCallback<D, R> {
    private View view;

    public ResetViewClickable(View view) {
        this.view = view;
    }

    @Override
    public void onAlways(Promise.State state, D resolved, R rejected) {
        view.setEnabled(true);
    }
}
