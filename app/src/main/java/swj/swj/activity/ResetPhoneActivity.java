package swj.swj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.common.CommonMethods;

/**
 * Created by jiewei on 9/7/15.
 */
public class ResetPhoneActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_phone_step_one);
        final TextView tvGetSCode = (TextView)findViewById(R.id.tv_get_SCode);
        final TextView tvNewPhone = (TextView)findViewById(R.id.et_new_phone);
        final TextView tvError = (TextView)findViewById(R.id.tv_phone_error_message);
        tvGetSCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phoneNumber = tvNewPhone.getText().toString();
                if (!CommonMethods.isValidUsername(phoneNumber)) {
                    tvError.setText(getResources().getString(R.string.validation_username));
                } else {
                    Intent intent = new Intent(ResetPhoneActivity.this, ResetPhoneStepTwoActivity.class);
                    intent.putExtra("phone",phoneNumber);
                    startActivity(intent);
                }
            }
        });

    }

    public void back(View view) {
        finish();
    }
}
