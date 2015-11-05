package com.oneplusapp.activity;

import android.os.Bundle;

import com.android.volley.VolleyError;
import com.oneplusapp.R;
import com.oneplusapp.common.RestClient;

import org.jdeferred.Promise;
import org.json.JSONObject;

import butterknife.ButterKnife;

public class ResetPwdStepOne extends GetSecurityCodeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd_step_one);

        ButterKnife.bind(this);
    }

    @Override
    protected Class<?> getNextActivity() {
        return ResetPwdStepTwo.class;
    }

    @Override
    protected Promise<JSONObject, VolleyError, Void> getSecurityCode(String username) {
        return RestClient.getInstance().newSecurityCode4Password(username);
    }

}
