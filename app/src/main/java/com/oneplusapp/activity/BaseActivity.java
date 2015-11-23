package com.oneplusapp.activity;

import android.app.Activity;

import com.oneplusapp.BuildConfig;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {

    @Override
    protected void onPause() {
        super.onPause();
        if (!BuildConfig.DEBUG) {
            MobclickAgent.onPause(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!BuildConfig.DEBUG) {
            MobclickAgent.onResume(this);
        }
    }
}
