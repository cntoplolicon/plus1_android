package swj.swj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.common.RestClient;
import swj.swj.common.RoundedImageView;

public class RegisterStepThree extends Activity {

    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    private static final String IMAGE_FILE_NAME = "personalImage.jpg";
    private static final String[] options = new String[] {"从相册选择", "拍照"};

    private RoundedImageView faceImage;

    EditText nicknameInput;
    EditText passwordInput;
    TextView messageView;
    RadioGroup radioGroup4Gender;

    File avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step_three);

        faceImage = (RoundedImageView) findViewById(R.id.iv_avatar);
        faceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAvatarOptionDialog();
            }
        });

        nicknameInput = (EditText) findViewById(R.id.et_nickname);
        passwordInput = (EditText) findViewById(R.id.et_password);
        messageView = (TextView) findViewById(R.id.tv_message);
        radioGroup4Gender = (RadioGroup) findViewById(R.id.rg_gender);

        Button btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inputValidation()) {
                    return;
                }
                String username = getIntent().getStringExtra("username");
                String password = passwordInput.getText().toString();
                String nickname = nicknameInput.getText().toString();

                //RestClient.getInstance().signUp();
                startActivity(new Intent(RegisterStepThree.this, PersonalSettingsActivity.class));
            }
        });
    }

    private boolean inputValidation() {
        if (nicknameInput.getText().toString().trim().equals("")) {
            messageView.setText(getResources().getString(R.string.nickname_required));
            return false;
        } else if (!CommonMethods.isValidPwd(passwordInput.getText().toString())) {
            messageView.setText(getResources().getString(R.string.validation_pwd));
            return false;
        } else if (radioGroup4Gender.getCheckedRadioButtonId() == -1) {
            messageView.setText(getResources().getString(R.string.gender_required));
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
        }).setNegativeButton(getResources().getString(R.string.cancel_current_movement), new DialogInterface.OnClickListener() {
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
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    if (CommonMethods.hasSdCard()) {
                        avatar = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                        startPhotoZoom(Uri.fromFile(avatar));
                    } else {
                        //toast error message when unable to find sdcard
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.unable_to_find_SdCard), Toast.LENGTH_LONG).show();
                    }
                    break;
                case RESULT_REQUEST_CODE:
                    if (data != null) {
                        setImageToView(data);
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
            avatar = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
            intentFromCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(avatar));
        }
        startActivityForResult(intentFromCamera, CAMERA_REQUEST_CODE);
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //set crop
        intent.putExtra("crop", "true");
        //set width&height ratio
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //set height and width
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    public void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            faceImage.setImageBitmap(photo);
        }

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
}
