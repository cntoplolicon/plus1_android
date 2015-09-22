package swj.swj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONObject;

import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;

public class ResetPwdStepThree extends Activity {

    private EditText passwordInput;
    private EditText passwordConfirmationInput;

    private View.OnClickListener onSubmit = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (!inputValidation()) {
                return;
            }

            String username = getIntent().getStringExtra("username");
            String password = passwordInput.getText().toString();
            RestClient.getInstance().resetPassword(username, password, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Intent intent = new Intent(ResetPwdStepThree.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.pwd_reset_succeed), Toast.LENGTH_LONG).show();
                    startActivity(intent);
                    finish();
                }
            }, new JsonErrorListener(getApplicationContext(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject errors) {
                    CommonMethods.toastError(getApplicationContext(), errors, "username");
                    CommonMethods.toastError(getApplicationContext(), errors, "password");
                }
            }));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd_step_three);

        passwordInput = (EditText) findViewById(R.id.et_password);
        passwordConfirmationInput = (EditText) findViewById(R.id.et_password_confirmation);
        TextView tvPageTitle = (TextView) findViewById(R.id.tv_page_title);
        tvPageTitle.setText(getResources().getString(R.string.reset_pwd_step_three));

        Button submitButton = (Button) findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(onSubmit);
    }

    private boolean inputValidation() {
        String password = passwordInput.getText().toString();
        if (!CommonMethods.isValidPwd(password)) {
            Toast.makeText(this, R.string.password_invalid_format, Toast.LENGTH_LONG).show();
            return false;
        }

        String passwordConfirmation = passwordConfirmationInput.getText().toString();
        if (!passwordConfirmation.equals(password)) {
            Toast.makeText(this, R.string.password_confirmation_incorrect, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reset_pwd_step_three, menu);
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
