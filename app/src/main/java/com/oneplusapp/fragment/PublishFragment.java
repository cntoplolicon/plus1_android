package com.oneplusapp.fragment;

import android.app.AlertDialog;
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
import com.oneplusapp.common.ActivityHyperlinkClickListener;
import com.oneplusapp.common.BitmapUtil;


public class PublishFragment extends Fragment {

    private static final int PHOTO_REQUEST_TAKE_PHOTO = 1;  //take photo
    private static final int PHOTO_REQUEST_GALLERY = 2; //get from gallery

    private static final String KEY_CAMERA_FILE_URI = "camera_file_uri";

    private Uri cameraFileUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publish, container, false);
        TextView btnAddImage = (TextView) view.findViewById(R.id.btn_image);
        TextView btnAddText = (TextView) view.findViewById(R.id.btn_text);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setCanceledOnTouchOutside(false);
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
                Window window = alertDialog.getWindow();
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setContentView(R.layout.activity_dialog);
                TextView tvTakePhoto = (TextView) window.findViewById(R.id.tv_camera);
                TextView tvGallery = (TextView) window.findViewById(R.id.tv_gallery);
                TextView tvCancel = (TextView) window.findViewById(R.id.tv_cancel);
                tvTakePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCamera();
                        alertDialog.cancel();
                    }
                });
                tvGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getGallery();
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
        });
        btnAddText.setOnClickListener(new ActivityHyperlinkClickListener(getActivity(), AddTextActivity.class));

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

    private void getCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraFileUri = Uri.fromFile(BitmapUtil.getImageFile());
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
        startActivityForResult(intent, PHOTO_REQUEST_TAKE_PHOTO);
    }

    private void getGallery() {
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
