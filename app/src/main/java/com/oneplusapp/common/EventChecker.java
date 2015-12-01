package com.oneplusapp.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.oneplusapp.model.Event;

import org.jdeferred.DoneCallback;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONObject;

public class EventChecker {

    private static final String KEY_SHOWN_EVENT_CREATED_AT = "latestShownEventCreatedAt";

    private static EventChecker instance;

    private Context context;
    private EventInfo eventInfo;

    private EventChecker(Context context) {
        this.context = context;
    }

    public static void initialize(Context context) {
        instance = new EventChecker(context);
    }

    public static EventChecker getInstance() {
        return instance;
    }

    public EventInfo getNewEvent() {
        return eventInfo;
    }

    public void loadNewEvent(final EventCheckerListener listener) {
        clearEvent();
        RestClient.getInstance().getLatestEvent().done(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                Event event = CommonMethods.createDefaultGson().fromJson(result.toString(), Event.class);
                if (!needToShowEvent(event)) {
                    listener.onEventSkipped(event);
                    return;
                }
                eventInfo = new EventInfo();
                eventInfo.event = event;
                eventInfo.bitmaps = new Bitmap[event.getEventPages().length];
                for (int i = 0; i < event.getEventPages().length; i++) {
                    final int j = i;
                    ImageLoader.getInstance().loadImage(event.getEventPages()[i].getImage(),
                            new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    eventInfo.bitmaps[j] = loadedImage;
                                    for (Bitmap bitmap : eventInfo.bitmaps) {
                                        if (bitmap == null) {
                                            return;
                                        }
                                    }
                                    listener.onEventLoadingComplete(eventInfo);
                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                    listener.onEventImageLoadingFailed(imageUri, failReason);
                                }
                            });
                }
            }
        }).fail(new JsonErrorListener(context, null) {
            @Override
            public void onFail(VolleyError error) {
                super.onFail(error);
                listener.onEventLoadingFailed(error);
            }
        });
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


    public void clearEvent() {
        if (eventInfo == null) {
            return;
        }
        for (Bitmap bitmap : eventInfo.bitmaps) {
            bitmap.recycle();
        }
        eventInfo = null;
    }

    public void recordEventShown() {
        if (eventInfo != null) {
            LocalUserInfo.getPreferences().edit()
                    .putLong(KEY_SHOWN_EVENT_CREATED_AT, eventInfo.event.getCreatedAt().getMillis()).commit();
        }
    }

    public static class EventInfo {
        private Event event;
        private Bitmap[] bitmaps = new Bitmap[]{};

        public Event getEvent() {
            return event;
        }

        public Bitmap[] getBitmaps() {
            return bitmaps;
        }
    }

    public interface EventCheckerListener {
        void onEventLoadingComplete(EventInfo eventInfo);

        void onEventLoadingFailed(VolleyError error);

        void onEventSkipped(Event event);

        void onEventImageLoadingFailed(String uri, FailReason reason);
    }
}
