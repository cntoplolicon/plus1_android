package com.oneplusapp.activity;

import android.os.Bundle;

import butterknife.ButterKnife;
import com.oneplusapp.R;

public class RegisterStepTwo extends VerifySecurityCodeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step_two);

        ButterKnife.bind(this);
        setupChosenUsername();
        startResendCountDown();
    }

    @Override
    protected Class<?> getNextActivity() {
        return RegisterStepThree.class;
    }

}
