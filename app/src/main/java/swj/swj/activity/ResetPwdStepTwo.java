package swj.swj.activity;

import android.os.Bundle;

import butterknife.ButterKnife;
import swj.swj.R;

public class ResetPwdStepTwo extends VerifySecurityCodeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd_step_two);

        ButterKnife.bind(this);
        setupChosenUsername();
        startResendCountDown();
    }

    @Override
    protected Class<?> getNextActivity() {
        return ResetPwdStepThree.class;
    }
}
