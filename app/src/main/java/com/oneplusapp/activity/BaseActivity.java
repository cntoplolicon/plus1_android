package com.oneplusapp.activity;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;

import com.oneplusapp.BuildConfig;

/**
 * Created by Administrator on 2015/10/24.
 */
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
