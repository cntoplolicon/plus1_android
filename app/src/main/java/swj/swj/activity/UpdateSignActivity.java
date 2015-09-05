package swj.swj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.others.LocalUserInfo;

/**
 * Created by jiewei on 9/4/15.
 */
public class UpdateSignActivity extends Activity {

    private TextView tv_sign_save;
    private EditText edit_sign;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_sign);

        final String sign = LocalUserInfo.getInstance(UpdateSignActivity.this).getUserInfo("sign");
        edit_sign = (EditText) findViewById(R.id.et_sign);
        edit_sign.setText(sign);
        tv_sign_save = (TextView) findViewById(R.id.tv_sign_save);
        tv_sign_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newSign = edit_sign.getText().toString().trim();
                if (sign.equals(newSign) || newSign.equals("")) {
                    return;
                }
                temp_updateSign(newSign);
            }
        });

    }

    private void temp_updateSign(String newSign) {
        LocalUserInfo.getInstance(UpdateSignActivity.this).setUserInfo("sign", newSign);
        finish();
    }

    public void back(View view) {
        finish();
    }
}
