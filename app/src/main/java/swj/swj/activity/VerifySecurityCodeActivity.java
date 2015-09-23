package swj.swj.activity;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import swj.swj.R;
import swj.swj.common.CommonMethods;

public class VerifySecurityCodeActivity extends Activity {

    private SecurityCodeCountDownTimer timer;
    private static final Integer ONE_MINUTE = 60000;
    private static final Integer ONE_SECOND = 1000;

    @Bind(R.id.et_security_code)
    EditText securityCodeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_security_code);


        startResendCountDown();
    }

    boolean inputValidation() {
        if (!CommonMethods.isValidSCode(securityCodeInput.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.security_code_invalid_format), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

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

    void startResendCountDown() {
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

    static class SecurityCodeCountDownTimer extends CountDownTimer {
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
