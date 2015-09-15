package swj.swj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.common.LocalUserInfo;

/**
 * Created by jiewei on 9/7/15.
 */
public class ResetPhoneStepTwoActivity extends VerifySecurityCodeActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_phone_step_two);

        final Button tickingButtonReg = (Button) findViewById(R.id.btn_get_SCode);

        final Button RegToNextPage = (Button) findViewById(R.id.btn_confirm_new_phone);
        RegToNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText securityCodeInput = (EditText) findViewById(R.id.et_security_code);
                TextView securityCodeError = (TextView) findViewById(R.id.tv_security_code_error);
                String securityCode = securityCodeInput.getText().toString();
                if (!CommonMethods.isValidSCode(securityCode)) {
                    securityCodeError.setText(getResources().getString(R.string.security_code_invalid_format));
                } else if (!securityCode.equals("123456")) {
                    securityCodeError.setText(getResources().getString(R.string.security_code_incorrect));
                } else {
                    securityCodeError.setText("Loading");
                    Intent i = getIntent();
                    final String phone = getIntent().getStringExtra("phone");
                    LocalUserInfo.getInstance().setUserInfo("telephone", phone);
                    Intent intent = new Intent(getBaseContext(), PersonalProfileActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

}
