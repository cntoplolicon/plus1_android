package swj.swj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONObject;

import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;

public class ResetPwdStepTwo extends VerifySecurityCodeActivity {

    private View.OnClickListener onResendSecurityCode = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = getIntent();
            String username = intent.getStringExtra("username");
            RestClient.getInstance().newSecurityCode4Account(username,
                    null, new JsonErrorListener(getApplicationContext(), null));
            intent.putExtra("counter_start", System.currentTimeMillis());
            startResendCountDown();
        }
    };

    private View.OnClickListener onSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!inputValidation()) {
                return;
            }
            final String username = getIntent().getStringExtra("username");
            String securityCode = ((EditText) findViewById(R.id.et_security_code)).getText().toString();
            RestClient.getInstance().verifySecurityCode(username, securityCode,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Intent intent = new Intent(ResetPwdStepTwo.this, ResetPwdStepThree.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            finish();
                        }
                    }, new JsonErrorListener(getApplicationContext(), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject errors) {
                            CommonMethods.toastError(ResetPwdStepTwo.this, errors, "security_code");
                        }
                    }));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd_step_two);

        Intent intentFromPhoneInput = getIntent();
        String msgFromPhoneInput = intentFromPhoneInput.getStringExtra("username");
        TextView sCodeFragmentTopHint = (TextView) findViewById(R.id.tv_security_code_sent);
        sCodeFragmentTopHint.setText(getResources().getString(R.string.security_code_sent) + msgFromPhoneInput);

        Button resendButton = (Button) findViewById(R.id.btn_resend_security_code);
        resendButton.setOnClickListener(onResendSecurityCode);
        Button submitButton = (Button) findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(onSubmit);
    }


    private boolean inputValidation() {
        String securityCode = ((EditText) findViewById(R.id.et_security_code)).getText().toString();
        if (!CommonMethods.isValidSCode(securityCode)) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.security_code_invalid_format), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reset_pwd_step_two, menu);
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
}
