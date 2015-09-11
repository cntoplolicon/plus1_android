package swj.swj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import swj.swj.R;
import swj.swj.common.LocalUserInfo;

/**
 * Created by jiewei on 9/4/15.
 */
public class ChangePasswordActivity extends Activity {

    private EditText etOldPwd, etNewPwd, etNewPwdRe;
    private Button btnSavePwdChange;
    private String testPassword = LocalUserInfo.getInstance(ChangePasswordActivity.this).getUserInfo("password");

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passwrod);
        initView();
    }

    private void initView() {
        etOldPwd = (EditText) findViewById(R.id.et_old_pwd);
        etNewPwd = (EditText) findViewById(R.id.et_new_pwd);
        etNewPwdRe = (EditText) findViewById(R.id.et_new_pwd_repeat);
        btnSavePwdChange = (Button) findViewById(R.id.btn_change_pwd_save);
        btnSavePwdChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatePassword();
            }
        });
    }

    private void UpdatePassword() {
        String OldPwd = etOldPwd.getText().toString().trim();
        String NewPwd = etNewPwd.getText().toString().trim();
        String NewPwdRe = etNewPwdRe.getText().toString().trim();
        if (!OldPwd.equals(testPassword)) {
            Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
        } else if (!NewPwd.equals(NewPwdRe)) {
            Toast.makeText(getApplicationContext(), "密码不一致", Toast.LENGTH_SHORT).show();
        } else {
            LocalUserInfo.getInstance(ChangePasswordActivity.this).setUserInfo("password", NewPwd);
            Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void back(View view) {
        finish();
    }
}