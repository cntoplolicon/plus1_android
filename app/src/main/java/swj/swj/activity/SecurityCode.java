package swj.swj.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.common.CommonMethods;

public class SecurityCode extends AppCompatActivity {

    protected SecurityCodeCountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_code_fragment);

        startResendCountDown();
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

    public void securityCodeConfirm(final Button button, final Class<? extends Activity> ActivityToOpen) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText securityCodeInput = (EditText) findViewById(R.id.et_security_code);
                TextView securityCodeError = (TextView) findViewById(R.id.tv_security_code_error);
                String securityCode = securityCodeInput.getText().toString();
                if (!CommonMethods.isValidSCode(securityCode)) {
                    securityCodeError.setText(getResources().getString(R.string.security_code_invalid_format));
                } else if (!securityCode.equals("123456")) {
                    securityCodeError.setText(getResources().getString(R.string.security_code_incorrect));
                } else {
                    securityCodeError.setText("Loading");
                    Intent intent = new Intent(getBaseContext(), ActivityToOpen);
                    startActivityForResult(intent, 0);
                }
            }
        });
    }

    protected void startResendCountDown() {
        Button btnResendCode = (Button) findViewById(R.id.btn_resend_security_code);
        long counterStart = getIntent().getLongExtra("counter_start", System.currentTimeMillis());
        long remainingTime = counterStart + 60000 - System.currentTimeMillis();
        if (remainingTime > 0) {
            if (timer != null) {
                timer.cancel();
            }
            timer = new SecurityCodeCountDownTimer(this, btnResendCode, remainingTime, 1000);
            timer.start();
        }
    }

    protected static class SecurityCodeCountDownTimer extends CountDownTimer {
        private Button button;
        private Context context;

        public SecurityCodeCountDownTimer(Context context, Button button,
                                          long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            this.context = context;
            this.button = button;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            button.setClickable(false);
            button.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            button.setText(context.getResources().getString(R.string.send_again));
            button.setClickable(true);
        }
    }
}
