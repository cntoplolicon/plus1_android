package com.oneplusapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneplusapp.R;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.LocalUserInfo;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.Event;
import com.oneplusapp.model.User;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONObject;

public class SplashActivity extends BaseActivity {

    private static final String KEY_SHOWN_EVENT_CREATED_AT = "latestShownEventCreatedAt";

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
        LocalUserInfo.getPreferences().edit().putLong(KEY_SHOWN_EVENT_CREATED_AT, event.getCreatedAt().getMillis());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        delay();
    }

    private void loadEvent() {
        RestClient.getInstance().getLastestEvent().done(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                Event latestEvent = CommonMethods.createDefaultGson().fromJson(result.toString(), Event.class);
                if (latestEvent.getId() == 0 || latestEvent.getEventPages() == null ||
                        latestEvent.getEventPages().length == 0 || !needToShowEvent(latestEvent)) {
                    return;
                }
                event = latestEvent;
                eventBitmaps = new Bitmap[event.getEventPages().length];
                for (int i = 0; i < event.getEventPages().length; i++) {
                    Bitmap bitmap = ImageLoader.getInstance().loadImageSync(event.getEventPages()[i].getImage());
                    eventBitmaps[i] = bitmap;
                }
            }
        }).fail(new JsonErrorListener(this, null)).always(new AlwaysCallback<JSONObject, VolleyError>() {
            @Override
            public void onAlways(Promise.State state, JSONObject resolved, VolleyError rejected) {
                showGuideOnFirstLogin();
            }
        });
    }

    private void showGuideOnFirstLogin() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("config",
                Context.MODE_PRIVATE);
        boolean isGuideShowed = sharedPreferences.getBoolean("is_guide_showed", false);
        if (!isGuideShowed) {
            startActivity(new Intent(getApplicationContext(), GuideActivity.class));
            finish();
            return;
        }

        if (User.current == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
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
        DateTime date = DateTime.now();
        int daysBetween = Days.daysBetween(event.getCreatedAt().toLocalDate(), date.toLocalDate()).getDays();
        if (daysBetween >= 1) {
            return false;
        }
        long shownEventCreatedAt = LocalUserInfo.getPreferences().getLong(KEY_SHOWN_EVENT_CREATED_AT, 0);
        return event.getCreatedAt().getMillis() > shownEventCreatedAt;
    }
}
