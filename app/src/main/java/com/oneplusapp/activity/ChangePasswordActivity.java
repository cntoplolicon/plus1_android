package com.oneplusapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.oneplusapp.R;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.ResetViewClickable;
import com.oneplusapp.common.RestClient;

import org.jdeferred.DoneCallback;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends BaseActivity {

    @Bind(R.id.et_old_password)
    EditText etOldPwd;

    @Bind(R.id.et_password)
    EditText etNewPwd;

    @Bind(R.id.et_password_confirmation)
    EditText etNewPwdConfirm;

    @OnClick(R.id.btn_submit)
    public void submit(View view) {
        if (!inputValidation()) {
            return;
        }
        view.setEnabled(false);
        String oldPassword = etOldPwd.getText().toString();
        String password = etNewPwd.getText().toString();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("old_password", oldPassword);
        attributes.put("password", password);

        RestClient.getInstance().updateUserAttributes(attributes).done(
                new DoneCallback<JSONObject>() {
                    @Override
                    public void onDone(JSONObject response) {
                        Toast.makeText(ChangePasswordActivity.this, R.string.pwd_reset_succeed, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).fail(
                new JsonErrorListener(getApplicationContext(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject errors) {
                        CommonMethods.showError(ChangePasswordActivity.this, errors, "old_password");
                        CommonMethods.showError(ChangePasswordActivity.this, errors, "password");
                    }
                })).always(new ResetViewClickable<JSONObject, VolleyError>(view));
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
    }

    private boolean inputValidation() {
        String oldPassword = etOldPwd.getText().toString();
        if (!CommonMethods.isValidPwd(oldPassword)) {
            Toast.makeText(this, R.string.password_invalid_format, Toast.LENGTH_LONG).show();
            return false;
        }

        String password = etNewPwd.getText().toString();
        if (!CommonMethods.isValidPwd(password)) {
            Toast.makeText(this, R.string.password_invalid_format, Toast.LENGTH_LONG).show();
            return false;
        }

        String passwordConfirmation = etNewPwdConfirm.getText().toString();
        if (!passwordConfirmation.equals(password)) {
            Toast.makeText(this, R.string.password_confirmation_incorrect, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}