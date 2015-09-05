package swj.swj.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.common.RoundedImageView;

public class RegisterStepThree extends CommonMethods {

    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    private static final String IMAGE_FILE_NAME = "personalImage.jpg";
    private String[] options;

    private RoundedImageView faceImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step_three);

        options = new String[]{getResources().getString(R.string.set_avatar_from_local), getResources().getString(R.string.set_avatar_from_camera)};

        faceImage = (RoundedImageView) findViewById(R.id.roundedImageForAvatar);
        faceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAvatarOptionDialog();
            }
        });

        final EditText nicknameInput = (EditText) findViewById(R.id.setNickName);
        final EditText setPwd = (EditText) findViewById(R.id.setPwd);
        final TextView registerStepThreeMsg = (TextView) findViewById(R.id.registerStepThreeMessage);
        Button registerStepThree = (Button) findViewById(R.id.finishRegisger);

        final RadioGroup radioGroupForGender = (RadioGroup) findViewById(R.id.radioGroupForGender);
        final RadioButton radioButtonForMale = (RadioButton) findViewById(R.id.radioButtonForMale);
        RadioButton radioButtonForFemale = (RadioButton) findViewById(R.id.radioButtonForFemale);

        registerStepThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nicknameInput.getText().toString().trim().equals("")) {
                    registerStepThreeMsg.setText(getResources().getString(R.string.nickname_required));
                } else if (!isValidPwd(setPwd.getText().toString())) {
                    registerStepThreeMsg.setText(getResources().getString(R.string.validation_pwd));
                } else if (radioGroupForGender.getCheckedRadioButtonId() == -1) {
                    registerStepThreeMsg.setText(getResources().getString(R.string.gender_required));
                } else if (radioButtonForMale.isChecked()) {
                    registerStepThreeMsg.setText(getResources().getString(R.string.register_finish_hint_nickname) + nicknameInput.getText().toString() + "(" + getResources().getString(R.string.register_set_gender_male) + ")");
                } else  {
                    registerStepThreeMsg.setText(getResources().getString(R.string.register_finish_hint_nickname) + nicknameInput.getText().toString() + "(" + getResources().getString(R.string.register_set_gender_female) + ")");
                }
            }
        });
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
                    if (hasSdCard()) {
                        File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                        startPhotoZoom(Uri.fromFile(tempFile));
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
        if (hasSdCard()) {
            intentFromCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
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
