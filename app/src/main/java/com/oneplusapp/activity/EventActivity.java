package com.oneplusapp.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import com.github.paolorotolo.appintro.AppIntro;
import com.oneplusapp.common.EventChecker;
import com.oneplusapp.fragment.EventSlide;

import butterknife.Bind;
import butterknife.ButterKnife;


public class EventActivity extends AppIntro {

    @Bind(com.github.paolorotolo.appintro.R.id.next)
    ImageView nextButton;

    @Override
    public void init(Bundle bundle) {
        ButterKnife.bind(this);

        EventChecker.EventInfo eventInfo = EventChecker.getInstance().getNewEvent();
        for (Bitmap bitmap : eventInfo.getBitmaps()) {
            addSlide(EventSlide.newInstance(bitmap));
        }

        showSkipButton(false);
        showDoneButton(true);
        setSeparatorColor(Color.TRANSPARENT);

        EventChecker.getInstance().recordEventShown();
    }

    @Override
    protected void onStart() {
        super.onStart();
        nextButton.setOnClickListener(null);
        nextButton.setImageDrawable(null);
    }

    @Override
    public void onSkipPressed() {
        // do nothing
    }

    @Override
    public void onDonePressed() {
        EventChecker.getInstance().clearEvent();
        finish();
    }
}
