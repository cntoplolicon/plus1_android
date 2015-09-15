package swj.swj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.common.CommonMethods;

public class ResetPwdStepThree extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd_step_three);

        final TextView resetPwdStepThreeErrorMsg = (TextView) findViewById(R.id.resetPwdStepThreeErrorMessage);
        Button resetPwdStepThreeConfirmBtn = (Button) findViewById(R.id.btn_reset_pwd_step_three_confirm);
        final EditText resetPwdStepThreeInput = (EditText) findViewById(R.id.et_reset_pwd_step_three_input);
        final EditText resetPwdStepThreeConfirm = (EditText) findViewById(R.id.et_reset_pwd_step_three_confirm);

        resetPwdStepThreeConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonMethods.isValidPwd(resetPwdStepThreeInput.getText().toString())) {
                    resetPwdStepThreeErrorMsg.setText(getResources().getString(R.string.password_invalid_format));
                } else if (!resetPwdStepThreeInput.getText().toString().equals(resetPwdStepThreeConfirm.getText().toString())) {
                    resetPwdStepThreeErrorMsg.setText(getResources().getString(R.string.password_confirmation_incorrect));
                } else {
                    resetPwdStepThreeErrorMsg.setText(getResources().getString(R.string.pwd_reset_succeed));
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    intent.putExtra("resetPwdStepThree", getResources().getString(R.string.pwd_reset_succeed));
                    startActivityForResult(intent, 0);
                }
            }
        });
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
