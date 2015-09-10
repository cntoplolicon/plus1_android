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
import swj.swj.fragment.SecurityCodeFragment;

/**
 * Created by jiewei on 9/7/15.
 */
public class ResetPhoneStepTwoActivity extends SecurityCodeFragment {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_phone_step_two);

        final Button tickingButtonReg = (Button) findViewById(R.id.btn_get_SCode);
        ticking(tickingButtonReg);

        final Button RegToNextPage = (Button) findViewById(R.id.btn_confirm_new_phone);
        RegToNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText securityCodeInput = (EditText) findViewById(R.id.SecurityCodeInput);
                TextView securityCodeError = (TextView) findViewById(R.id.SecurityCodeError);
                String securityCode = securityCodeInput.getText().toString();
                if (!CommonMethods.isValidSCode(securityCode)) {
                    securityCodeError.setText(getResources().getString(R.string.validation_SCode));
                } else if (!securityCode.equals("123456")) {
                    securityCodeError.setText(getResources().getString(R.string.wrong_SCode));
                } else {
                    securityCodeError.setText("Loading");
                    Intent i = getIntent();
                    final String phone = getIntent().getStringExtra("phone");
                    LocalUserInfo.getInstance(getBaseContext()).setUserInfo("telephone", phone);
                    Intent intent = new Intent(getBaseContext(), PersonalProfileActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

}
