package swj.swj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.soundcloud.android.crop.Crop;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.model.User;
import swj.swj.view.RoundedImageView;

public class RegisterStepThree extends Activity {

    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final String IMAGE_FILE_NAME = "personalImage.jpg";
    private static final String[] options = new String[]{"从相册选择", "拍照"};

    private RoundedImageView faceImage;

    private EditText nicknameInput;
    private EditText passwordInput;
    private RadioGroup radioGroup4Gender;

    private View.OnClickListener onSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!inputValidation()) {
                return;
            }
            String username = getIntent().getStringExtra("username");
            String nickname = nicknameInput.getText().toString();
            String password = passwordInput.getText().toString();

            RestClient.getInstance().signUp(username, nickname,
                    password, getSelectedGender(), getAvatar(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            User.updateCurrentUser(response.toString());
                            startActivity(new Intent(RegisterStepThree.this, HomeActivity.class));
                            finish();
                        }
                    }, new JsonErrorListener(getApplicationContext(), null));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step_three);

        TextView tvPageTitle = (TextView) findViewById(R.id.tv_page_title);
        tvPageTitle.setText(getResources().getString(R.string.register_step_three));

        faceImage = (RoundedImageView) findViewById(R.id.iv_avatar);
        faceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAvatarOptionDialog();
            }
        });

        nicknameInput = (EditText) findViewById(R.id.et_nickname);
        passwordInput = (EditText) findViewById(R.id.et_password);
        radioGroup4Gender = (RadioGroup) findViewById(R.id.rg_gender);

        Button submitButton = (Button) findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(onSubmit);
    }

    private ByteArrayBody getAvatar() {
        Drawable drawable = faceImage.getDrawable();
        if (drawable == null) {
            return null;
        }
        Bitmap avatarBitmap = ((BitmapDrawable) drawable).getBitmap();
        try {
            byte[] avatarData = CommonMethods.bitmap2ByteArray(avatarBitmap);
            return new ByteArrayBody(avatarData, ContentType.create("image/png"), "avatar.png");
        } catch (IOException e) {
            Log.d(RegisterStepThree.class.getName(), "failed getting avatar data", e);
            return null;
        }
    }

    private int getSelectedGender() {
        int selectedId = radioGroup4Gender.getCheckedRadioButtonId();
        switch (selectedId) {
            case R.id.rb_male:
                return User.GENDER_MALE;
            case R.id.rb_female:
                return User.GENDER_FEMALE;
            default:
                return User.GENDER_UNKNOWN;
        }
    }

    private boolean inputValidation() {
        if (nicknameInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.nickname_required), Toast.LENGTH_LONG).show();
            return false;
        } else if (!CommonMethods.isValidPwd(passwordInput.getText().toString())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.password_invalid_format), Toast.LENGTH_LONG).show();
            return false;
        } else if (radioGroup4Gender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.gender_required), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    //show the option dialog to select
    public void showAvatarOptionDialog() {
        new AlertDialog.Builder(this).setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        getImageFromGallery();
                        break;
                    case 1:
                        getImageFromCamera();
                        break;
                }
            }
        }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    beginCrop(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    if (CommonMethods.hasSdCard()) {
                        File avatar = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                        beginCrop(Uri.fromFile(avatar));
                    } else {
                        //toast error message when unable to find sdcard
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.unable_to_find_sd_card), Toast.LENGTH_LONG).show();
                    }
                    break;
                case Crop.REQUEST_CROP:
                    if (data != null) {
                        handleCrop(resultCode, data);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getImageFromGallery() {
        Intent intentFromGallery = new Intent();
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
    }

    public void getImageFromCamera() {
        Intent intentFromCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (CommonMethods.hasSdCard()) {
            File avatar = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
            intentFromCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(avatar));
        }
        startActivityForResult(intentFromCamera, CAMERA_REQUEST_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_step_three, menu);
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

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            faceImage.setImageDrawable(null);
            faceImage.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
