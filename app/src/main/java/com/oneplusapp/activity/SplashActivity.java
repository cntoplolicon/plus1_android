package com.oneplusapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.oneplusapp.R;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.LocalUserInfo;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.Event;
import com.oneplusapp.model.User;

import org.jdeferred.DoneCallback;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONObject;

public class SplashActivity extends BaseActivity {

    private static final String KEY_SHOWN_EVENT_CREATED_AT = "latestShownEventCreatedAt";
    private int loadedEventBitmaps = 0;

    private static Event event;
    private static Bitmap[] eventBitmaps;

    public static Event getEvent() {
        return event;
    }

    public static Bitmap[] getEventBitmaps() {
        return eventBitmaps;
    }

    public static void recycleEventBitmaps() {
        for (Bitmap bitmap : eventBitmaps) {
            bitmap.recycle();
        }
        eventBitmaps = null;
    }

    public static void recordEventShown() {
        LocalUserInfo.getPreferences().edit()
                .putLong(KEY_SHOWN_EVENT_CREATED_AT, event.getCreatedAt().getMillis()).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        delay();
    }

    private void loadEvent() {
        event = null;
        eventBitmaps = null;
        RestClient.getInstance().getLatestEvent().done(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                Event latestEvent = CommonMethods.createDefaultGson().fromJson(result.toString(), Event.class);
                if (!needToShowEvent(latestEvent)) {
                    finishSplashActivity();
                    return;
                }
                event = latestEvent;
                eventBitmaps = new Bitmap[event.getEventPages().length];
                for (int i = 0; i < event.getEventPages().length; i++) {
                    ImageLoader.getInstance().loadImage(event.getEventPages()[i].getImage(),
                            new EventImageLoadingListener(eventBitmaps, i));
                }
            }
        }).fail(new JsonErrorListener(this, null) {
            @Override
            public void onFail(VolleyError error) {
                super.onFail(error);
                finishSplashActivity();
            }
        });
    }

    private void finishSplashActivity() {
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

    private boolean needToShowEvent(Event event) {
        if (event.getId() == 0 || event.getEventPages() == null ||
                event.getEventPages().length == 0) {
            return false;
        }
        DateTime date = DateTime.now();
        int daysBetween = Days.daysBetween(event.getCreatedAt().toLocalDate(), date.toLocalDate()).getDays();
        if (daysBetween >= 1) {
            return false;
        }
        long shownEventCreatedAt = LocalUserInfo.getPreferences().getLong(KEY_SHOWN_EVENT_CREATED_AT, 0);
        return event.getCreatedAt().getMillis() > shownEventCreatedAt;
    }

    private class EventImageLoadingListener extends SimpleImageLoadingListener {

        private Bitmap[] eventBitmaps;
        private int index;

        private EventImageLoadingListener(Bitmap[] eventBitmaps, int index) {
            this.eventBitmaps = eventBitmaps;
            this.index = index;
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            loadedEventBitmaps++;
            eventBitmaps[index] = loadedImage;
            if (loadedEventBitmaps == eventBitmaps.length) {
                finishSplashActivity();
            }
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            event = null;
            finishSplashActivity();
        }
    }
}
