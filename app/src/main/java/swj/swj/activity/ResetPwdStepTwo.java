package swj.swj.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import swj.swj.R;

public class ResetPwdStepTwo extends VerifySecurityCodeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd_step_two);

        ButterKnife.bind(this);
        setPageTitle(getResources().getString(R.string.reset_pwd_step_two));
        setChoosenUsername();
        startResendCountDown();
    }

    @Override
    protected Class<?> getNextActivity() {
        return ResetPwdStepThree.class;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reset_pwd_step_two, menu);
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
