package swj.swj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.common.LocalUserInfo;

/**
 * Created by jiewei on 9/4/15.
 */
public class UpdateSignActivity extends Activity {

    private TextView tvSaveSign;
    private EditText etSign;
    private String newSign;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_sign);

        final String sign = LocalUserInfo.getInstance(UpdateSignActivity.this).getUserInfo("sign");
        etSign = (EditText) findViewById(R.id.et_sign);
        etSign.setText(sign);
        tvSaveSign = (TextView) findViewById(R.id.tv_sign_save);
        tvSaveSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newSign = etSign.getText().toString().trim();
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
