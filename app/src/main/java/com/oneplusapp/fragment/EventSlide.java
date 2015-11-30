package com.oneplusapp.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneplusapp.R;

public class EventSlide extends Fragment {

    private String bitmapUrl;

    public static EventSlide newInstance(String eventBitmapUrl) {
        EventSlide eventSlide = new EventSlide();
        eventSlide.setEventBitmapUrl(eventBitmapUrl);
        return eventSlide;
    }

    public void setEventBitmapUrl(String eventBitmapUrl) {
        this.bitmapUrl = eventBitmapUrl;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(bitmapUrl);
        ImageView ivEvent = (ImageView) view.findViewById(R.id.iv_event);
        ivEvent.setImageBitmap(bitmap);
        return view;
    }
}
