package swj.swj.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import swj.swj.R;
import swj.swj.activity.AddTextActivity;
import swj.swj.activity.PublishActivity;


public class PublishFragment extends BaseFragment {

    private static final int PHOTO_REQUEST_TAKE_PHOTO = 1;  //take photo
    private static final int PHOTO_REQUEST_GALLERY = 2; //get from gallery
    private static final String[] items = {"拍照", "从照片库里选择", "取消"};
    private String fileNames;

    @Override
    public View initView() {
        View v = View.inflate(mActivity, R.layout.fragment_publish, null);
        Button btnAddImage = (Button) v.findViewById(R.id.btn_image);
        Button btnAddText = (Button) v.findViewById(R.id.btn_text);
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
                alertDialog.show();
                Window window = alertDialog.getWindow();
                window.setContentView(R.layout.activity_dialog);
                TextView tvTakePhoto = (TextView) window.findViewById(R.id.tv_1);
                TextView tvGallery = (TextView) window.findViewById(R.id.tv_2);
                TextView tvCancel = (TextView) window.findViewById(R.id.tv_3);
                tvTakePhoto.setText("拍照");
                tvGallery.setText("从相册获取");
                tvCancel.setText("取消");
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

        btnAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, AddTextActivity.class));
            }
        });
        return v;
    }

    private void getCamera() {
        Toast.makeText(mActivity, "进入相机", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + "myImage");
        if (!dir.exists()){
            dir.mkdirs();
        }
        fileNames = getNowTime() + ".jpg";
        File file = new File(dir, fileNames);
        Uri uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, PHOTO_REQUEST_TAKE_PHOTO);
    }

    private void getGallery() {
        Toast.makeText(mActivity, "进入相册", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(PublishFragment.class.toString(), "file not found");
        switch (requestCode) {
            case PHOTO_REQUEST_TAKE_PHOTO:
                String sdStatus = Environment.getExternalStorageState();
                // 检测sd是否可用
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                    Log.e(PublishFragment.class.toString(), "SD card is not available/writable right now.");
                    return;
                }
                File file = new File(Environment.getExternalStorageDirectory() + "/" + "myImage" + "/" + fileNames);
                try {
                    Uri photoUri = Uri.parse(MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), file.getAbsolutePath(), null, null));
                    String photoPath = getFilePath(photoUri);
                    startActivity(new Intent(mActivity, PublishActivity.class).setAction("getCamera").putExtra("photoPath", photoPath));
                } catch (FileNotFoundException e) {
                    Log.e(PublishFragment.class.toString(), "file not found");
                }
                break;
            case PHOTO_REQUEST_GALLERY:
                if (data == null) {
                    return;
                }
                Uri originalUri = data.getData();
                String picturePath = getFilePath(originalUri);
                startActivity(new Intent(mActivity, PublishActivity.class).setAction("getGallery").putExtra("picturePath", picturePath));
        }
    }

    public String getNowTime() {
        Date date = new Date();
        SimpleDateFormat dataFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dataFormat.format(date);
    }

    private String getFilePath(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = mActivity.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
}
