package swj.swj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.jdeferred.DoneCallback;
import org.json.JSONObject;

import swj.swj.R;
import swj.swj.common.ActivityHyperlinkClickListener;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.ResetViewClickable;
import swj.swj.common.RestClient;
import swj.swj.model.User;

public class LoginActivity extends Activity {

    private EditText usernameInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginSubmit = (Button) findViewById(R.id.btn_submit);
        usernameInput = (EditText) findViewById(R.id.et_username);
        passwordInput = (EditText) findViewById(R.id.et_password);

        loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inputValidation()) {
                    return;
                }
                v.setEnabled(false);
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                RestClient.getInstance().signIn(username, password).done(
                        new DoneCallback<JSONObject>() {
                            @Override
                            public void onDone(JSONObject response) {
                                User.updateCurrentUser(response.toString());
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).fail(
                        new JsonErrorListener(getApplicationContext(), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject errors) {
                                CommonMethods.toastError(LoginActivity.this, errors, "username");
                                CommonMethods.toastError(LoginActivity.this, errors, "password");
                            }
                        })).always(new ResetViewClickable<JSONObject, VolleyError>(v));
            }
        });

        TextView toForgetPwd = (TextView) findViewById(R.id.tv_to_reset_pwd);
        toForgetPwd.setOnClickListener(new ActivityHyperlinkClickListener(this, ResetPwdStepOne.class));

        TextView toRegister = (TextView) findViewById(R.id.tv_to_register);
        toRegister.setOnClickListener(new ActivityHyperlinkClickListener(this, RegisterStepOne.class));
    }

    private boolean inputValidation() {
        if (!CommonMethods.isValidUsername(usernameInput.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.username_invalid_format), Toast.LENGTH_LONG).show();
            return false;
        } else if (!CommonMethods.isValidPwd(passwordInput.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.password_invalid_format), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
