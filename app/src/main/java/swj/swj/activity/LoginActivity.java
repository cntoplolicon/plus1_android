package swj.swj.activity;

import android.content.Intent;
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
import swj.swj.common.LocalUserInfo;

public class LoginActivity extends AppCompatActivity {

    private String phoneNumber;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginSubmit = (Button) findViewById(R.id.btn_submit);
        TextView toRegister = (TextView) findViewById(R.id.tv_to_register);
        TextView toForgetPwd = (TextView) findViewById(R.id.tv_to_reset_pwd);
        final EditText usernameInput = (EditText) findViewById(R.id.et_username);
        final EditText pwdInput = (EditText) findViewById(R.id.et_password);
        final TextView loginMessage = (TextView) findViewById(R.id.tv_login_error_message);

        //get extras from reset pwd activities to set message
        Intent intentFromResetPwd = getIntent();
        String msgFromResetPwd = intentFromResetPwd.getStringExtra("resetPwdStepThree");
        loginMessage.setText(msgFromResetPwd);

        loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = usernameInput.getText().toString();
                pwd = pwdInput.getText().toString();
                if (!CommonMethods.isValidUsername(phoneNumber)) {
                    loginMessage.setText(getResources().getString(R.string.username_invalid_format));
                } else if (!CommonMethods.isValidPwd(pwd)) {
                    loginMessage.setText(getResources().getString(R.string.password_invalid_format));
                } else {
                    loginMessage.setText("Loading...");
                    tempInitUser();
                    finish();
                }
            }
        });

        toForgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ResetPwdStepOne.class);
                startActivityForResult(intent, 0);
            }
        });

        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RegisterStepOne.class);
                startActivityForResult(intent, 0);
            }
        });

    }

    private void tempInitUser() {

        String nickname = "andywangpku";
        String sign = "word is big, let me see see";

        LocalUserInfo.getInstance().setUserInfo("nick_name", nickname);
        LocalUserInfo.getInstance().setUserInfo("sign", sign);
        LocalUserInfo.getInstance().setUserInfo("telephone", phoneNumber);
        LocalUserInfo.getInstance().setUserInfo("password", pwd);
        /*LocalUserInfo.getInstance(PersonalSettingsActivity.this).setUserInfo("avatar",
                avatar);*/
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
