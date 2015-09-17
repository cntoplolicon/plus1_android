package swj.swj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import swj.swj.common.ActivityHyperlinkClickListener;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginSubmit = (Button) findViewById(R.id.btn_submit);
        usernameInput = (EditText) findViewById(R.id.et_username);
        passwordInput = (EditText) findViewById(R.id.et_password);

        //get extras from reset pwd activities to set message
        Intent intentFromResetPwd = getIntent();
        String msgFromResetPwd = intentFromResetPwd.getStringExtra("resetPwdStepThree");
        TextView loginMessage = (TextView) findViewById(R.id.tv_login_error_message);
        loginMessage.setText(msgFromResetPwd);

        loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inputValidation()) {
                    return;
                }
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                RestClient.getInstance().signIn(username, password, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        User.updateCurrentUser(response.toString());
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    }
                }, new JsonErrorListener(getApplicationContext(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject errors) {
                        CommonMethods.toastError(LoginActivity.this, errors, "username");
                        CommonMethods.toastError(LoginActivity.this, errors, "password");
                    }
                }));
            }
        });

        TextView toForgetPwd = (TextView) findViewById(R.id.tv_to_reset_pwd);
        toForgetPwd.setOnClickListener(new ActivityHyperlinkClickListener(this, ResetPwdStepOne.class));

        TextView toRegister = (TextView) findViewById(R.id.tv_to_register);
        toRegister.setOnClickListener(new ActivityHyperlinkClickListener(this, RegisterStepOne.class));
    }

    private boolean inputValidation() {
        if (!CommonMethods.isValidUsername(usernameInput.getText().toString())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.username_invalid_format), Toast.LENGTH_LONG).show();
            return false;
        } else if (!CommonMethods.isValidPwd(passwordInput.getText().toString())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.password_invalid_format), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
