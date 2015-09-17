package swj.swj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;

/**
 * Created by jiewei on 9/4/15.
 */
public class ChangePasswordActivity extends Activity {

    private EditText etOldPwd, etNewPwd, etNewPwdConfirm;

    private View.OnClickListener onSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!inputValidation()) {
                return;
            }

            String oldPassword = etOldPwd.getText().toString();
            String password = etNewPwd.getText().toString();
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("old_password", oldPassword);
            attributes.put("password", password);

            RestClient.getInstance().updateUserAttributes(attributes, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    finish();
                }
            }, new JsonErrorListener(getApplicationContext(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject errors) {
                    CommonMethods.toastError(getApplicationContext(), errors, "old_password");
                    CommonMethods.toastError(getApplicationContext(), errors, "password");
                }
            }));
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passwrod);
        initView();

        Button submitButton = (Button)findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(onSubmit);
    }

    private void initView() {
        etOldPwd = (EditText) findViewById(R.id.et_old_password);
        etNewPwd = (EditText) findViewById(R.id.et_password);
        etNewPwdConfirm = (EditText) findViewById(R.id.et_password_confirmation);
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

    public void back(View view) {
        finish();
    }
}