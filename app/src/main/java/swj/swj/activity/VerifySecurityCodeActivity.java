package swj.swj.activity;

import android.app.Activity;
import android.content.Context;
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

import org.jdeferred.DoneCallback;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.common.CommonDialog;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.ResetViewClickable;
import swj.swj.common.RestClient;

public abstract class VerifySecurityCodeActivity extends Activity {

    private static final String USERNAME = "username";
    private static final Integer ONE_MINUTE = 60000;
    private static final Integer ONE_SECOND = 1000;

    @Bind(R.id.et_security_code)
    EditText securityCodeInput;

    @Bind(R.id.tv_chosen_username)
    TextView chosenUsername;

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
            CommonDialog.showDialog(VerifySecurityCodeActivity.this, getResources().getString(R.string.security_code_invalid_format));
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
    protected void onSubmit(View view) {
        final String username = getIntent().getStringExtra(USERNAME);
        if (!inputValidation()) {
            return;
        }
        view.setEnabled(false);
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
                        CommonDialog.showDialog(VerifySecurityCodeActivity.this, getResources().getString(R.string.security_code_incorrect));
                    }
                })).always(new ResetViewClickable<JSONObject, VolleyError>(view));
    }

    protected abstract Class<?> getNextActivity();

    protected void startResendCountDown() {
        Button btnResendCode = (Button) findViewById(R.id.btn_resend_security_code);
        long counterStart = getIntent().getLongExtra("counter_start", System.currentTimeMillis());
        long remainingTime = counterStart + ONE_MINUTE - System.currentTimeMillis();
        if (remainingTime > 0) {
            if (timer != null) {
                timer.cancel();
            }
            timer = new SecurityCodeCountDownTimer(this, btnResendCode, remainingTime);
            timer.start();
        }
    }

    protected static class SecurityCodeCountDownTimer extends CountDownTimer {
        private final Button button;
        private final Context context;

        public SecurityCodeCountDownTimer(Context context, Button button,
                                          long millisInFuture) {
            super(millisInFuture, (long) VerifySecurityCodeActivity.ONE_SECOND);
            this.context = context;
            this.button = button;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            button.setClickable(false);
            button.setText(millisUntilFinished / ONE_SECOND + "秒");
        }

        @Override
        public void onFinish() {
            button.setText(context.getResources().getString(R.string.send_again));
            button.setClickable(true);
        }
    }
}
