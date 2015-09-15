package swj.swj.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import swj.swj.R;
import swj.swj.activity.PushActivity;


public class PushFragment extends BaseFragment {


    @Override
    public View initView() {
        View v = View.inflate(getMyActivity(), R.layout.fragment_push, null);
        Button bt_image = (Button) v.findViewById(R.id.bt_image);

        bt_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final String[] items = {"拍照", "从照片库里选择", "取消"};
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                getCamera();

                                break;
                            case 1:
                                getGallery();
                                break;
                            case 2:
                                break;
                        }
                    }
                }).show();
            }
        });
        return v;


    }

    @Override
    public void initData() {
        super.initData();
    }

    //use camera
    public void getCamera() {

        Toast.makeText(getMyActivity(), "进入相机", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, 0);

    }

    //Use photo album
    private void getGallery() {
        Toast.makeText(getMyActivity(), "进入相册", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {

            String sdStatus = Environment.getExternalStorageState();
            // 检测sd是否可用
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                Log.v("TestFile", "SD card is not avaiable/writeable right now.");
                return;
            }
            String name = getNowTime() + ".jpg";
            Toast.makeText(getMyActivity(), name, Toast.LENGTH_LONG).show();
            Bundle bundle = data.getExtras();
            if (bundle != null) {


                Bitmap bitmap = (Bitmap) bundle.get("data");
                FileOutputStream outPutStream = null;
                File file = new File("/sdcard/myImage/");
                String fileName = "/sdcard/myImage/" + name;
                file.mkdirs();

                try {
                    outPutStream = new FileOutputStream(fileName);
                    // 把数据写入文件
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outPutStream);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outPutStream != null) {
                            outPutStream.flush();
                            outPutStream.close();
                        }
                    } catch (Exception e) {
                    }

                }
                startActivity(new Intent(getMyActivity(), PushActivity.class).setAction("getCamara").putExtra("fileName", fileName));
            }
            return;

        } else if (requestCode == 1) {
            if (data != null) {
                Uri originalUri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = mActivity.getContentResolver().query(originalUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picPath = cursor.getString(columnIndex);   // 图片的绝对路径
                cursor.close();
                Log.d("picPath", picPath);
                startActivity(new Intent(getMyActivity(), PushActivity.class).setAction("getGallery").putExtra("picPath", picPath));
            }
            return;
        }
    }

    public String getNowTime() {
        Toast.makeText(getMyActivity(), "生成相片", Toast.LENGTH_SHORT).show();
        Date date = new Date();
        SimpleDateFormat dataFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dataFormat.format(date);
    }
}
