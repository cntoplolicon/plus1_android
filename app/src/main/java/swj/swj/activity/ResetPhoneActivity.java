package swj.swj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONObject;

import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;

/**
 * Created by jiewei on 9/7/15.
 */
public class ResetPhoneActivity extends Activity {

    private EditText usernameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_phone_step_one);
        Button btnSubmit = (Button) findViewById(R.id.btn_submit);
        usernameInput = (EditText) findViewById(R.id.et_username);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inputValidation()) {
                    return;
                }
                final String username = usernameInput.getText().toString();
                RestClient.getInstance().newSecurityCode4Account(username,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String securityCode = response.optString("security_code", "");
                                if (!securityCode.isEmpty()) {
                                    Log.d("security_code", securityCode);
                                    Toast.makeText(ResetPhoneActivity.this, securityCode, Toast.LENGTH_LONG).show();
                                }

                                Intent intent = new Intent(ResetPhoneActivity.this, ResetPhoneStepTwoActivity.class);
                                intent.putExtra("counter_start", System.currentTimeMillis());
                                intent.putExtra("username", username);
                                startActivity(intent);
                            }
                        },
                        new JsonErrorListener(getApplicationContext(), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject errors) {
                                CommonMethods.toastError(ResetPhoneActivity.this, errors, "username");
                            }
                        }));
            }
        });
    }

    private boolean inputValidation() {
        if (!CommonMethods.isValidUsername(usernameInput.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.username_invalid_format), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void back(View view) {
        finish();
    }
}
