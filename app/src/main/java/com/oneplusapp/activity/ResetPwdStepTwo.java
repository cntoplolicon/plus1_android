package com.oneplusapp.activity;

import android.os.Bundle;

import com.oneplusapp.R;

import butterknife.ButterKnife;

public class ResetPwdStepTwo extends VerifySecurityCodeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd_step_two);

        ButterKnife.bind(this);
        setupChosenUsername();
        startResendCountDown();
    }

    @Override
    protected Class<?> getNextActivity() {
        return ResetPwdStepThree.class;
    }
}
