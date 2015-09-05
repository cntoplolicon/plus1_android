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

public class LoginActivity extends CommonMethods {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginSubmit = (Button) findViewById(R.id.loginSubmit);
        TextView toRegister = (TextView) findViewById(R.id.toRegister);
        TextView toForgetPwd = (TextView) findViewById(R.id.toForgetPwd);
        final EditText usernameInput = (EditText) findViewById(R.id.userNameInput);
        final EditText pwdInput = (EditText) findViewById(R.id.passwordInput);
        final TextView loginMessage = (TextView) findViewById(R.id.errorMessage);

        //get extras from reset pwd activities to set message
        Intent intentFromResetPwd = getIntent();
        String msgFromResetPwd = intentFromResetPwd.getStringExtra("resetPwdStepThree");
        loginMessage.setText(msgFromResetPwd);

        loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phoneNumber = usernameInput.getText().toString();
                final String pwd = pwdInput.getText().toString();
                if (!isValidUsername(phoneNumber)) {
                    loginMessage.setText(getResources().getString(R.string.validation_username));
                } else if (!isValidPwd(pwd)) {
                    loginMessage.setText(getResources().getString(R.string.validation_pwd));
                } else {
                    loginMessage.setText("Loading...");
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
