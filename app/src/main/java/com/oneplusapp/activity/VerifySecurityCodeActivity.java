package com.oneplusapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.oneplusapp.R;
import com.oneplusapp.common.CommonDialog;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.RestClient;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

public abstract class VerifySecurityCodeActivity extends BaseActivity {

    private static final String USERNAME = "username";
    private static final Integer ONE_MINUTE = 60000;
    private static final Integer ONE_SECOND = 1000;
    private boolean resendButtonTicking;
    private boolean verificationInProgress = false;

    @Bind(R.id.et_security_code)
    EditText securityCodeInput;

    @Bind(R.id.tv_chosen_username)
    TextView chosenUsername;

    @Bind(R.id.btn_resend_security_code)
    Button btnResend;

    private SecurityCodeCountDownTimer timer;

    protected void setupChosenUsername() {
        Intent intent = getIntent();
        chosenUsername.setText(intent.getStringExtra(USERNAME));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected boolean inputValidation() {
        if (!CommonMethods.isValidSCode(securityCodeInput.getText().toString().trim())) {
            CommonDialog.showDialog(this, R.string.security_code_invalid_format);
            return false;
        }
        return true;
    }

    @OnClick(R.id.btn_resend_security_code)
    protected void onResendSecurityCode() {
        Intent intent = getIntent();
        String username = intent.getStringExtra(USERNAME);
        RestClient.getInstance().newSecurityCode4Account(username).done(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject response) {
                String securityCode = response.optString("security_code", "");
                if (!securityCode.isEmpty()) {
                    Log.d("security_code", securityCode);
                    Toast.makeText(getBaseContext(), securityCode, Toast.LENGTH_LONG).show();
                }
            }
        }).fail(new JsonErrorListener(getApplicationContext(), null));
        intent.putExtra("counter_start", System.currentTimeMillis());
        startResendCountDown();
    }

    @OnClick(R.id.btn_submit)
    protected void onSubmit(final View view) {
        final String username = getIntent().getStringExtra(USERNAME);
        if (!inputValidation()) {
            return;
        }
        view.setEnabled(false);
        verificationInProgress = true;
        updateResendButtonState();
        String securityCode = ((EditText) findViewById(R.id.et_security_code)).getText().toString();
        RestClient.getInstance().verifySecurityCode(username, securityCode).done(
                new DoneCallback<JSONObject>() {
                    @Override
                    public void onDone(JSONObject response) {
                        Intent intent = new Intent(getBaseContext(), getNextActivity());
                        intent.putExtra(USERNAME, username);
                        startActivity(intent);
                        finish();
                    }
                }).fail(
                new JsonErrorListener(getApplicationContext(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject errors) {
                        CommonMethods.showError(VerifySecurityCodeActivity.this, errors, "security_code");
                    }
                })).always(
                new AlwaysCallback<JSONObject, VolleyError>() {
                    @Override
                    public void onAlways(Promise.State state, JSONObject resolved, VolleyError rejected) {
                        view.setEnabled(true);
                        verificationInProgress = false;
                        updateResendButtonState();
                    }
                });
    }

    protected abstract Class<?> getNextActivity();

    protected void startResendCountDown() {
        resendButtonTicking = true;
        long counterStart = getIntent().getLongExtra("counter_start", System.currentTimeMillis());
        long remainingTime = counterStart + ONE_MINUTE - System.currentTimeMillis();
        if (remainingTime > 0) {
            if (timer != null) {
                timer.cancel();
            }
            timer = new SecurityCodeCountDownTimer(remainingTime);
            timer.start();
        }
    }

    protected void updateResendButtonState() {
        btnResend.setEnabled(!resendButtonTicking && !verificationInProgress);
    }

    protected class SecurityCodeCountDownTimer extends CountDownTimer {

        public SecurityCodeCountDownTimer(long millisInFuture) {
            super(millisInFuture, (long) VerifySecurityCodeActivity.ONE_SECOND);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            resendButtonTicking = true;
            updateResendButtonState();
            btnResend.setText(millisUntilFinished / ONE_SECOND + "秒");

        }

        @Override
        public void onFinish() {
            btnResend.setText(R.string.send_again);
            resendButtonTicking = false;
            updateResendButtonState();
        }
    }
}
