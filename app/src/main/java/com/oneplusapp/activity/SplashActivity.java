package com.oneplusapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.oneplusapp.R;
import com.oneplusapp.common.EventChecker;
import com.oneplusapp.model.Event;
import com.oneplusapp.model.User;

public class SplashActivity extends BaseActivity {

    private boolean activityFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        delay();
    }

    private void loadEvent() {
        EventChecker.getInstance().loadNewEvent(new EventChecker.EventCheckerListener() {
            @Override
            public void onEventLoadingComplete(EventChecker.EventInfo eventInfo) {
                finishSplashActivity();
            }

            @Override
            public void onEventLoadingFailed(VolleyError error) {
                finishSplashActivity();
            }

            @Override
            public void onEventSkipped(Event event) {
                finishSplashActivity();
            }

            @Override
            public void onEventImageLoadingFailed(String uri, FailReason reason) {
                finishSplashActivity();
            }
        });
    }

    private void finishSplashActivity() {
        if (activityFinished) {
            return;
        }
        activityFinished = true;

        SharedPreferences sharedPreferences = getSharedPreferences("config",
                Context.MODE_PRIVATE);
        boolean isGuideShowed = sharedPreferences.getBoolean("is_guide_showed", false);
        if (!isGuideShowed) {
            startActivity(new Intent(this, GuideActivity.class));
            finish();
            return;
        }

        if (User.current == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void delay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadEvent();
            }
        }, 2000);
    }

}
