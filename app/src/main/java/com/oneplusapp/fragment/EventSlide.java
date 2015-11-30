package com.oneplusapp.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.oneplusapp.R;

public class EventSlide extends Fragment {

    private Bitmap bitmap;

    public static EventSlide newInstance(Bitmap bitmap) {
        EventSlide eventSlide = new EventSlide();
        eventSlide.setEventBitmap(bitmap);
        return eventSlide;
    }

    public void setEventBitmap(Bitmap eventBitmap) {
        this.bitmap = eventBitmap;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        ImageView ivEvent = (ImageView) view.findViewById(R.id.iv_event);
        ivEvent.setImageBitmap(bitmap);
        return view;
    }
}
