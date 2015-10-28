package swj.swj.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.soundcloud.android.crop.Crop;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.jdeferred.DoneCallback;
import org.json.JSONObject;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import swj.swj.R;
import swj.swj.common.BitmapUtil;
import swj.swj.common.CommonDialog;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.ResetViewClickable;
import swj.swj.common.RestClient;
import swj.swj.model.User;

public class RegisterStepThree extends BaseActivity {

    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final String IMAGE_FILE_NAME = "personalImage.jpg";

    private CircleImageView faceImage;

    private EditText nicknameInput;
    private EditText passwordInput;
    private RadioGroup radioGroup4Gender;
    private AbstractContentBody imageBody;

    private View.OnClickListener onSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!inputValidation()) {
                return;
            }
            view.setEnabled(false);
            String username = getIntent().getStringExtra("username");
            String nickname = nicknameInput.getText().toString();
            String password = passwordInput.getText().toString();

            RestClient.getInstance().signUp(username, nickname,
                    password, getSelectedGender(), imageBody).done(
                    new DoneCallback<JSONObject>() {
                        @Override
                        public void onDone(JSONObject response) {
                            User.updateCurrentUser(response.toString());
                            Intent intent = new Intent(RegisterStepThree.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }).fail(new JsonErrorListener(getApplicationContext(), null))
                    .always(new ResetViewClickable<JSONObject, VolleyError>(view));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step_three);

        faceImage = (CircleImageView) findViewById(R.id.iv_avatar);
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
            CommonDialog.showDialog(this, R.string.nickname_required);
            return false;
        } else if (!CommonMethods.isValidPwd(passwordInput.getText().toString().trim())) {
            CommonDialog.showDialog(this, R.string.password_invalid_format);
            return false;
        } else if (radioGroup4Gender.getCheckedRadioButtonId() == -1) {
            CommonDialog.showDialog(this, R.string.gender_required);
            return false;
        }
        return true;
    }

    //show the option dialog to select
    private void showAvatarOptionDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(RegisterStepThree.this).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.activity_dialog);
        TextView tvTakePhoto = (TextView) window.findViewById(R.id.tv_camera);
        TextView tvGallery = (TextView) window.findViewById(R.id.tv_gallery);
        TextView tvCancel = (TextView) window.findViewById(R.id.tv_cancel);
        tvTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromCamera();
                alertDialog.cancel();
            }
        });
        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();
                alertDialog.cancel();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
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
                        CommonDialog.showDialog(this, R.string.unable_to_find_sd_card);
                    }
                    break;
                case Crop.REQUEST_CROP:
                    if (data == null) {
                        return;
                    }
                    if (resultCode == Crop.RESULT_ERROR) {
                        CommonDialog.showDialog(this, Crop.getError(data).getMessage());
                        return;
                    }
                    BitmapUtil.prepareImageForUploading(this, Crop.getOutput(data)).done(new DoneCallback<Bitmap>() {
                        @Override
                        public void onDone(Bitmap bitmap) {
                            byte[] imageData = BitmapUtil.compressBitmap(bitmap, BitmapUtil.DEFAULT_QUALITY);
                            imageBody = new ByteArrayBody(imageData, ContentType.create("image/jpeg"), "avatar.jpg");
                            faceImage.setImageBitmap(bitmap);
                        }
                    });
            }
        }
    }

    private void getImageFromGallery() {
        Intent intentFromGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
    }

    private void getImageFromCamera() {
        Intent intentFromCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (CommonMethods.hasSdCard()) {
            File avatar = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
            intentFromCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(avatar));
        }
        startActivityForResult(intentFromCamera, CAMERA_REQUEST_CODE);
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }
}
