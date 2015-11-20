package com.oneplusapp.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.oneplusapp.R;
import com.oneplusapp.activity.AddTextActivity;
import com.oneplusapp.activity.PublishActivity;
import com.oneplusapp.common.BitmapUtil;
import com.oneplusapp.view.MenuDialog;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class PublishFragment extends Fragment {

    private static final int PHOTO_REQUEST_TAKE_PHOTO = 1;  //take photo
    private static final int PHOTO_REQUEST_GALLERY = 2; //get from gallery

    private static final String KEY_CAMERA_FILE_URI = "camera_file_uri";

    private MenuDialog addImageDialog;
    private Uri cameraFileUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publish, container, false);
        ButterKnife.bind(this, view);

        buildAddImageDialog();

        if (savedInstanceState != null) {
            cameraFileUri = savedInstanceState.getParcelable(KEY_CAMERA_FILE_URI);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_CAMERA_FILE_URI, cameraFileUri);
    }

    private void buildAddImageDialog() {
        addImageDialog = new MenuDialog(getActivity());
        addImageDialog.addButton(R.string.get_image_from_camera, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromCamera();
            }
        });
        addImageDialog.addButton(R.string.get_image_from_gallery, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();
            }
        });
        addImageDialog.addCancelButton();
    }

    @OnClick(R.id.btn_text)
    public void onPostTextButtonClicked() {
        Intent intent = new Intent(getActivity(), AddTextActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_image)
    public void onPostImageButtonClicked() {
        addImageDialog.show();
    }

    private void getImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraFileUri = Uri.fromFile(BitmapUtil.getImageFile());
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
        startActivityForResult(intent, PHOTO_REQUEST_TAKE_PHOTO);
    }

    private void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case PHOTO_REQUEST_TAKE_PHOTO:
                String sdStatus = Environment.getExternalStorageState();
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                    Log.e(PublishFragment.class.toString(), "SD card is not available/writable right now.");
                    return;
                }
                BitmapUtil.notifyMediaScanner(getActivity(), cameraFileUri);
                Intent intentCamera = new Intent(getActivity(), PublishActivity.class).putExtra("imagePath", cameraFileUri);
                startActivity(intentCamera);
                break;
            case PHOTO_REQUEST_GALLERY:
                if (data == null) {
                    return;
                }
                Uri originalUri = data.getData();
                Intent intentGallery = new Intent(getActivity(), PublishActivity.class).putExtra("imagePath", originalUri);
                startActivity(intentGallery);
                break;
        }
    }
}