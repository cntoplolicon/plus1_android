package com.oneplusapp.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import com.github.paolorotolo.appintro.AppIntro;
import com.oneplusapp.fragment.EventSlide;
import com.oneplusapp.model.Event;
import com.oneplusapp.model.EventPage;

import butterknife.Bind;
import butterknife.ButterKnife;


public class EventActivity extends AppIntro {

    @Bind(com.github.paolorotolo.appintro.R.id.next)
    ImageView nextButton;

    @Override
    public void init(Bundle bundle) {
        Bitmap[] eventBitmaps = SplashActivity.getEventBitmaps();
        ButterKnife.bind(this);

        Event event = SplashActivity.getEvent();
        for (EventPage page : event.getEventPages()) {
            addSlide(EventSlide.newInstance(page.getImage()));
        }

        showSkipButton(false);
        showDoneButton(false);
        setSeparatorColor(Color.TRANSPARENT);

        SplashActivity.recordEventShown();
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
        // do nothing
    }
}
