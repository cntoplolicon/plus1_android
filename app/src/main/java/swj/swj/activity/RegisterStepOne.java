package swj.swj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class RegisterStepOne extends GetSecurityCodeActivity {

    private EditText usernameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step_one);
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
                                    Toast.makeText(RegisterStepOne.this, securityCode, Toast.LENGTH_LONG).show();
                                }

                                Intent intent = new Intent(RegisterStepOne.this, RegisterStepTwo.class);
                                intent.putExtra("counter_start", System.currentTimeMillis());
                                intent.putExtra("username", username);
                                startActivity(intent);
                            }
                        },
                        new JsonErrorListener(getApplicationContext(), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject errors) {
                                CommonMethods.toastError(RegisterStepOne.this, errors, "username");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_step_one, menu);
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
