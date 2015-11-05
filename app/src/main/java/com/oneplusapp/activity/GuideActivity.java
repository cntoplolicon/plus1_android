package com.oneplusapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.github.paolorotolo.appintro.AppIntro;
import com.oneplusapp.R;
import com.oneplusapp.fragment.IntroductionSlide;

import butterknife.Bind;
import butterknife.ButterKnife;


public class GuideActivity extends AppIntro {

    @Bind(com.github.paolorotolo.appintro.R.id.next)
    ImageView nextButton;

    @Override
    public void init(Bundle bundle) {
        addSlide(IntroductionSlide.newInstance(R.layout.fragment_guide_01));
        addSlide(IntroductionSlide.newInstance(R.layout.fragment_guide_02));
        addSlide(IntroductionSlide.newInstance(R.layout.fragment_guide_03));

        showSkipButton(false);
        showDoneButton(false);
        setSeparatorColor(Color.TRANSPARENT);

        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        nextButton.setOnClickListener(null);
        nextButton.setImageDrawable(null);
    }

    public void onStartButtonClicked(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("config",
                Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("is_guide_showed", true).commit();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
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
