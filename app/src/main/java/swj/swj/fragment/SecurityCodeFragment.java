package swj.swj.fragment;

import android.app.Activity;
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

public class SecurityCodeFragment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_security_code_fragment);
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

    public void ticking(final Button button) {
        class TimeCount extends CountDownTimer {

            public TimeCount(long millisInFuture, long countDownInterval) {
                super(millisInFuture, countDownInterval);
            }

            @Override
            public void onTick(long millisUntilFinished) {
                button.setClickable(false);
                button.setText(millisUntilFinished / 1000 + "秒");
            }

            @Override
            public void onFinish() {
                button.setText(getResources().getString(R.string.send_again));
                button.setClickable(true);
            }
        }
        final TimeCount time = new TimeCount(60000, 1000);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time.start();
            }
        });
        time.start();
    }

    public void securityCodeConfirm(final Button button, final Class<? extends Activity> ActivityToOpen) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText securityCodeInput = (EditText) findViewById(R.id.SecurityCodeInput);
                TextView securityCodeError = (TextView) findViewById(R.id.SecurityCodeError);
                String securityCode = securityCodeInput.getText().toString();
                if (!CommonMethods.isValidSCode(securityCode)) {
                    securityCodeError.setText(getResources().getString(R.string.validation_SCode));
                } else if (!securityCode.equals("123456")) {
                    securityCodeError.setText(getResources().getString(R.string.wrong_SCode));
                } else {
                    securityCodeError.setText("Loading");
                    Intent intent = new Intent(getBaseContext(), ActivityToOpen);
                    startActivityForResult(intent, 0);
                }
            }
        });
    }
}
