package swj.swj.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;

public abstract class VerifySecurityCodeActivity extends Activity {

    private static final String USERNAME = "username";
    private static final Integer ONE_MINUTE = 60000;
    private static final Integer ONE_SECOND = 1000;

    @Bind(R.id.et_security_code)
    EditText securityCodeInput;

    @Bind(R.id.tv_choosen_username)
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
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.security_code_invalid_format), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @OnClick(R.id.btn_resend_security_code)
    protected void onResendSecurityCode() {
        Intent intent = getIntent();
        String username = intent.getStringExtra(USERNAME);
        RestClient.getInstance().newSecurityCode4Account(username,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String securityCode = response.optString("security_code", "");
                        if (!securityCode.isEmpty()) {
                            Log.d("security_code", securityCode);
                            Toast.makeText(getBaseContext(), securityCode, Toast.LENGTH_LONG).show();
                        }
                    }
                }, new JsonErrorListener(getApplicationContext(), null));
        intent.putExtra("counter_start", System.currentTimeMillis());
        startResendCountDown();
    }

    @OnClick(R.id.btn_submit)
    protected void onSubmit() {
        final String username = getIntent().getStringExtra(USERNAME);
        if (!inputValidation()) {
            return;
        }
        String securityCode = ((EditText) findViewById(R.id.et_security_code)).getText().toString();
        RestClient.getInstance().verifySecurityCode(username, securityCode,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(getBaseContext(), getNextActivity());
                        intent.putExtra(USERNAME, username);
                        startActivity(intent);
                        finish();
                    }
                }, new JsonErrorListener(getApplicationContext(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject errors) {
                        CommonMethods.toastError(getApplicationContext(), errors, "security_code");
                    }
                }));
    }

    protected abstract Class<?> getNextActivity();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_security_code, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
