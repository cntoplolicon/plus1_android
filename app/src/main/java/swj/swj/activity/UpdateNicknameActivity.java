package swj.swj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.common.LocalUserInfo;

/**
 * Created by jiewei on 9/3/15.
 */
public class UpdateNicknameActivity extends Activity {

    private TextView tvSave, tvNick;
    private String newNick;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nickname);

        final String nick = LocalUserInfo.getInstance(UpdateNicknameActivity.this).getUserInfo("nick_name");
        tvNick = (EditText) findViewById(R.id.et_nick);
        tvNick.setText(nick);
        tvSave = (TextView) findViewById(R.id.tv_nickname_save);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newNick = tvNick.getText().toString().trim();
                if (nick.equals(newNick) || newNick.equals("")) {
                    return;
                }
                temp_updateNick(newNick);
            }
        });

    }

    public void back(View view) {
        finish();
    }

    private void temp_updateNick(final String newNick) {
        LocalUserInfo.getInstance(UpdateNicknameActivity.this).setUserInfo("nick_name", newNick);
        finish();
    }
}
