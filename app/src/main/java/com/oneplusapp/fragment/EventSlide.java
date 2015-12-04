package com.oneplusapp.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.oneplusapp.R;
import com.oneplusapp.common.EventChecker;

public class EventSlide extends Fragment {

    private Bitmap bitmap;
    private boolean clickToFinishActivity;

    public static EventSlide newInstance(Bitmap bitmap, boolean clickToFinishActivity) {
        EventSlide slide = new EventSlide();
        slide.bitmap = bitmap;
        slide.clickToFinishActivity = clickToFinishActivity;
        return slide;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        ImageView ivEvent = (ImageView) view.findViewById(R.id.iv_event);
        if (clickToFinishActivity) {
            ivEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventChecker.getInstance().clearEvent();
                    getActivity().finish();
                }
            });
        }

        ivEvent.setImageBitmap(bitmap);
        return view;
    }
}
