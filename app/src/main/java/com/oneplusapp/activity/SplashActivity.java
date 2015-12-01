package com.oneplusapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.oneplusapp.R;
import com.oneplusapp.common.EventChecker;
import com.oneplusapp.model.Event;
import com.oneplusapp.model.User;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {

    private boolean activityFinished;
    @Bind(R.id.rl_splash)
    RelativeLayout rlSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        loadEvent();
    }

    private void fadeOutActivity() {
        AlphaAnimation animAlpha = new AlphaAnimation(1, 0.2f);
        animAlpha.setDuration(2000);
        animAlpha.setFillAfter(true);
        rlSplash.startAnimation(animAlpha);
        animAlpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // do nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finishSplashActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // do nothing
            }
        });

    }

    private void loadEvent() {
        EventChecker.getInstance().loadNewEvent(new EventChecker.EventCheckerListener() {
            @Override
            public void onEventLoadingComplete(EventChecker.EventInfo eventInfo) {
                fadeOutActivity();
            }

            @Override
            public void onEventLoadingFailed(VolleyError error) {
                fadeOutActivity();
            }

            @Override
            public void onEventSkipped(Event event) {
                fadeOutActivity();
            }

            @Override
            public void onEventImageLoadingFailed(String uri, FailReason reason) {
                fadeOutActivity();
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

}
