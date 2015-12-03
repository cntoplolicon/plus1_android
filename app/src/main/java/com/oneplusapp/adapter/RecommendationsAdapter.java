package com.oneplusapp.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneplusapp.R;
import com.oneplusapp.application.SnsApplication;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.Event;
import com.oneplusapp.view.DetachableImageView;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.json.JSONArray;

import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecommendationsAdapter extends ArrayAdapter<Event> {

    private LayoutInflater mInflater;
    private boolean loading = false;
    private Context context;
    private Set<LoadingStatusObserver> loadingStatusObservers = new HashSet<>();

    public RecommendationsAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Event event = getItem(position);
        final ViewHolder viewHolder;
        View view;
        if (convertView != null) {
            view = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            view = mInflater.inflate(R.layout.recommend_fragment_item, parent, false);
            viewHolder = new ViewHolder();
            view.setTag(viewHolder);
            ButterKnife.bind(viewHolder, view);
        }
        Drawable loadingDrawable = CommonMethods.createLoadingDrawable(context, event.getLogoWidth(), event.getLogoHeight());
        final DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cloneFrom(SnsApplication.DEFAULT_DISPLAY_OPTION)
                .showImageOnLoading(loadingDrawable)
                .showImageOnFail(R.drawable.image_load_fail)
                .build();
        if (viewHolder.ivEventLogo.isAttachedToWindow()) {
            ImageLoader.getInstance().displayImage(event.getLogo(), viewHolder.ivEventLogo, options);
        } else {
            viewHolder.ivEventLogo.setImageDrawable(loadingDrawable);
            viewHolder.ivEventLogo.post(new Runnable() {
                @Override
                public void run() {
                    ImageLoader.getInstance().displayImage(event.getLogo(), viewHolder.ivEventLogo, options);
                }
            });
        }
        return view;
    }

    public boolean isLoading() {
        return loading;
    }

    public boolean isEmpty() {
        return getCount() == 0;
    }

    public void loadEvents() {
        if (loading) {
            return;
        }
        loading = true;
        RestClient.getInstance().getAllEvents().done(
                new DoneCallback<JSONArray>() {
                    @Override
                    public void onDone(JSONArray result) {
                        final Event[] tmpEvents = CommonMethods.createDefaultGson().fromJson(result.toString(), Event[].class);
                        clear();
                        addAll(tmpEvents);
                        notifyDataSetChanged();
                    }
                }).fail(new JsonErrorListener(context, null))
                .always(new AlwaysCallback<JSONArray, VolleyError>() {
                    @Override
                    public void onAlways(Promise.State state, JSONArray resolved, VolleyError rejected) {
                        loading = false;
                        notifyLoadingStatusChanged();
                    }
                });
    }

    private void notifyLoadingStatusChanged() {
        for (LoadingStatusObserver loadingStatusObserver : loadingStatusObservers) {
            loadingStatusObserver.onLoadingStatusChanged(loading);
        }
    }

    public void registerCallback(LoadingStatusObserver observer) {
        loadingStatusObservers.add(observer);
    }

    public void unregisterCallback(LoadingStatusObserver observer) {
        loadingStatusObservers.remove(observer);
    }

    public interface LoadingStatusObserver {
        void onLoadingStatusChanged(boolean loading);
    }


    static class ViewHolder {
        @Bind(R.id.iv_event_logo)
        DetachableImageView ivEventLogo;
    }
}

