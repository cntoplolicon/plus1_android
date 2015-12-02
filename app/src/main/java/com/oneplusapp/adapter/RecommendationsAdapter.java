package com.oneplusapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneplusapp.R;
import com.oneplusapp.application.SnsApplication;
import com.oneplusapp.model.Event;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecommendationsAdapter extends ArrayAdapter<Event> {

    private LayoutInflater mInflater;

    private static final DisplayImageOptions DISPLAY_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().cloneFrom(SnsApplication.DEFAULT_DISPLAY_OPTION)
                    .showImageOnLoading(R.color.home_title_color)
                    .showImageOnFail(R.drawable.image_load_fail)
                    .build();

    public RecommendationsAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = getItem(position);
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.recommend_fragment_item, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder();
        ButterKnife.bind(viewHolder, view);
        if (event.getLogo() != null) {
            ImageLoader.getInstance().displayImage(event.getLogo(), viewHolder.ivEventLogo, DISPLAY_IMAGE_OPTIONS);
        }
        view.setTag(event);
        return view;
    }

    static class ViewHolder {
        @Bind(R.id.iv_event_logo)
        ImageView ivEventLogo;
    }
}

