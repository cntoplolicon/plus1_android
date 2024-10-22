package com.oneplusapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.oneplusapp.R;
import com.oneplusapp.common.BitmapUtil;
import com.oneplusapp.common.CommonDialog;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.ResetViewClickable;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.User;
import com.oneplusapp.view.MenuDialog;
import com.soundcloud.android.crop.Crop;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.jdeferred.DoneCallback;
import org.json.JSONObject;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterStepThree extends BaseActivity {

    private static final String KEY_AVATAR_BITMAP = "key_avatar_bitmap";
    private static final String KEY_CAMERA_FILE_URI = "camera_file_uri";

    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;

    @Bind(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @Bind(R.id.et_nickname)
    EditText nicknameInput;
    @Bind(R.id.et_password)
    EditText passwordInput;
    @Bind(R.id.rg_gender)
    RadioGroup radioGroup4Gender;

    private Uri cameraFileUri;
    private Bitmap avatarBitmap;
    private MenuDialog selectAvatarDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step_three);
        ButterKnife.bind(this);

        buildSelectAvatarDialog();

        if (savedInstanceState != null) {
            cameraFileUri = savedInstanceState.getParcelable(KEY_CAMERA_FILE_URI);
            avatarBitmap = savedInstanceState.getParcelable(KEY_AVATAR_BITMAP);
            if (avatarBitmap != null) {
                ivAvatar.setImageBitmap(avatarBitmap);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_CAMERA_FILE_URI, cameraFileUri);
        outState.putParcelable(KEY_AVATAR_BITMAP, avatarBitmap);
    }

    @OnClick(R.id.iv_avatar)
    public void onAvatarImageViewClicked() {
        selectAvatarDialog.show();
    }

    @OnClick(R.id.btn_submit)
    public void onSubmit(View view) {
        if (!inputValidation()) {
            return;
        }
        view.setEnabled(false);
        String username = getIntent().getStringExtra("username");
        String nickname = nicknameInput.getText().toString();
        String password = passwordInput.getText().toString();
        ByteArrayBody imageBody = null;
        if (avatarBitmap != null) {
            byte[] imageData = BitmapUtil.compressBitmap(avatarBitmap, BitmapUtil.DEFAULT_QUALITY);
            imageBody = new ByteArrayBody(imageData, ContentType.create("image/jpeg"), "avatar.jpg");
        }
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

    private void buildSelectAvatarDialog() {
        selectAvatarDialog = new MenuDialog(this);
        selectAvatarDialog.addButton(R.string.get_image_from_camera, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromCamera();
            }
        });
        selectAvatarDialog.addButton(R.string.get_image_from_gallery, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();
            }
        });
        selectAvatarDialog.addCancelButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                beginCrop(data.getData());
                break;
            case CAMERA_REQUEST_CODE:
                if (CommonMethods.hasSdCard()) {
                    BitmapUtil.notifyMediaScanner(this, cameraFileUri);
                    beginCrop(cameraFileUri);
                } else {
                    CommonDialog.showDialog(this, R.string.unable_to_find_sd_card);
                }
                break;
            case Crop.REQUEST_CROP:
                if (data == null) {
                    return;
                }
                if (resultCode == Crop.RESULT_ERROR) {
                    Throwable t = Crop.getError(data);
                    Log.e(RegisterStepThree.class.getName(), "failed cropping image", t);
                    Toast.makeText(RegisterStepThree.this, R.string.image_crop_error, Toast.LENGTH_LONG).show();
                    return;
                }
                BitmapUtil.prepareImageForUploading(this, Crop.getOutput(data)).done(new DoneCallback<Bitmap>() {
                    @Override
                    public void onDone(Bitmap bitmap) {
                        avatarBitmap = bitmap;
                        ivAvatar.setImageBitmap(avatarBitmap);
                    }
                });
        }
    }

    private void getImageFromGallery() {
        Intent intentFromGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
    }

    private void getImageFromCamera() {
        Intent intentFromCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (CommonMethods.hasSdCard()) {
            File avatar = BitmapUtil.getImageFile();
            cameraFileUri = Uri.fromFile(avatar);
            intentFromCamera.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
        }
        startActivityForResult(intentFromCamera, CAMERA_REQUEST_CODE);
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }
}