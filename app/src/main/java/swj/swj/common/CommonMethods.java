package swj.swj.common;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by silentgod on 15-9-5.
 */
public class CommonMethods extends AppCompatActivity {
    // input pattern for validation checked on front_end
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[0-9]{11}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9]{6,20}$");
    private static final Pattern SECURITY_CODE_PATTERN = Pattern.compile("^[0-9]{6}$");

    //method to check input validation
    //username 11digits
    public boolean isValidUsername(String username) {
        Matcher matcher = USERNAME_PATTERN.matcher(username);
        return matcher.matches();
    }

    //pwd 6-20 digits or letters
    public boolean isValidPwd(String pwd) {
        Matcher matcher = PASSWORD_PATTERN.matcher(pwd);
        return matcher.matches();
    }

    //security code 6 digits
    public boolean isValidSCode(String sCode) {
        Matcher matcher = SECURITY_CODE_PATTERN.matcher(sCode);
        return matcher.matches();
    }

    //check if there is an SD card
    public boolean hasSdCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
