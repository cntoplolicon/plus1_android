package swj.swj.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.fragment.SecurityCodeFragment;

public class RegisterStepTwo extends SecurityCodeFragment {

    private Button tickingButtonReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step_two);

        Intent intentFromPhoneInput = getIntent();
        String msgFromPhoneInput = intentFromPhoneInput.getStringExtra("phoneToGetSCode");
        TextView SCodeFragmentTopHint = (TextView) findViewById(R.id.SecurityCodeSent);
        SCodeFragmentTopHint.setText(getResources().getString(R.string.SCode_sent_to_choosen_phone) + msgFromPhoneInput);

        tickingButtonReg = (Button) findViewById(R.id.getSecurityCode);
        ticking(tickingButtonReg);

        Button RegToNextPage = (Button) findViewById(R.id.SecurityCodeConfirm);
        securityCodeConfirm(RegToNextPage, RegisterStepThree.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_step_two, menu);
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
