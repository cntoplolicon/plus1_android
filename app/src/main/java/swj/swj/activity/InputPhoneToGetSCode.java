package swj.swj.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.common.CommonMethods;

public class InputPhoneToGetSCode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_phone_to_get_scode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input_phone_to_get_scode, menu);
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

    public void usernameGotConfirm(final Button button, final Class<? extends Activity> ActivityToOpen) {
        final TextView phoneNumberError = (TextView) findViewById(R.id.tv_username_error);
        final EditText phoneNumberInput = (EditText) findViewById(R.id.et_username);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phoneNumber = phoneNumberInput.getText().toString();
                if (!CommonMethods.isValidUsername(phoneNumber)) {
                    phoneNumberError.setText(getResources().getString(R.string.username_invalid_format));
                } else {
                    phoneNumberError.setText("");
                    Intent intent = new Intent(getBaseContext(), ActivityToOpen);
                    intent.putExtra("phoneToGetSCode", phoneNumber);
                    startActivityForResult(intent, 0);
                }
            }
        });
    }
}
