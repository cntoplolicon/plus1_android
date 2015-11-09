package com.oneplusapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.oneplusapp.R;
import com.oneplusapp.common.CommonDialog;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.ResetViewClickable;
import com.oneplusapp.common.RestClient;

import org.jdeferred.DoneCallback;
import org.json.JSONObject;

public class ResetPwdStepThree extends BaseActivity {

    private EditText passwordInput;
    private EditText passwordConfirmationInput;

    private View.OnClickListener onSubmit = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (!inputValidation()) {
                return;
            }
            view.setEnabled(false);
            String username = getIntent().getStringExtra("username");
            String password = passwordInput.getText().toString();
            RestClient.getInstance().resetPassword(username, password).done(
                    new DoneCallback<JSONObject>() {
                        @Override
                        public void onDone(JSONObject response) {
                            Intent intent = new Intent(ResetPwdStepThree.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Toast.makeText(getApplicationContext(), R.string.pwd_reset_succeed, Toast.LENGTH_LONG).show();
                            startActivity(intent);
                            finish();
                        }
                    }).fail(
                    new JsonErrorListener(getApplicationContext(), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject errors) {
                            CommonMethods.showError(getApplicationContext(), errors, "username");
                            CommonMethods.showError(getApplicationContext(), errors, "password");
                        }
                    })).always(new ResetViewClickable<JSONObject, VolleyError>(view));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd_step_three);

        passwordInput = (EditText) findViewById(R.id.et_password);
        passwordConfirmationInput = (EditText) findViewById(R.id.et_password_confirmation);

        Button submitButton = (Button) findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(onSubmit);
    }

    private boolean inputValidation() {
        String password = passwordInput.getText().toString();
        if (!CommonMethods.isValidPwd(password)) {
            CommonDialog.showDialog(this, R.string.password_invalid_format);
            return false;
        }

        String passwordConfirmation = passwordConfirmationInput.getText().toString();
        if (!passwordConfirmation.equals(password)) {
            CommonDialog.showDialog(this, R.string.password_confirmation_incorrect);
            return false;
        }

        return true;
    }

}
