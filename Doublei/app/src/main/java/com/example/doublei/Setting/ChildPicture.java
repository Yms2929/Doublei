package com.example.doublei.Setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.doublei.MainActivity;
import com.example.doublei.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class ChildPicture extends Activity implements View.OnClickListener {
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_iMAGE = 2;

    private Uri mImageCaptureUri;
    private ImageView iv_UserPhoto;
    private int id_view;
    private String absoultePath;

//    private DB_Manger dbmanger;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_photo);
//        dbmanger = new DB_Manger();

        iv_UserPhoto = (ImageView) this.findViewById(R.id.user_image);
        Button btn_agreeJoin = (Button) this.findViewById(R.id.btn_UploadPicture);
        btn_agreeJoin.setOnClickListener(this);
    }

    /**
     * 카메라에서 사진 촬영
     */
    public void doTakePhotoAction()//카메라 촬영 후 이미지 가져오기
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //임시로 사용할 파일의 경로 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    /**
     * 앨범에서 이미지 가져오기
     */
    public void doTakeAlbumAction()//앨범에서 이미지 가져오기
    {
        //앨범호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                mImageCaptureUri = data.getData();
                Log.d("smartWheel", mImageCaptureUri.getPath().toString());
            }
            case PICK_FROM_CAMERA: {
                //이미지를 가져온 후 리사이즈할 이미지 크기를 결정
                //이후에 이미지 크롭 어플리케이션 호출하게 됩니다.
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                //CROPgkf 이미지를 200*200 크기로 저장
                intent.putExtra("outputX", 200);//CROP한 이미지의 x축 크기
                intent.putExtra("outputY", 200);//CROP한 이미지의 y축 크기
                intent.putExtra("aspectX", 1);//CROP 박스의 X축 비율
                intent.putExtra("aspectY", 1);//CROP 박스의 Y축 비율
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_iMAGE);//CROP_FROM_CAMERA case문 이동
                break;
            }
            case CROP_FROM_iMAGE: {
                //크롭이 된 이후 이미지를 넘겨 받는다.
                //이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                //임시 파일을 삭제합니다.
                if (resultCode != RESULT_OK) {
                    return;
                }
                final Bundle extras = data.getExtras();

                //CROP된 이미지를 저장하기 위한 FILE경로
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/SmartWheel/" + System.currentTimeMillis() + "jpg";
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data"); //CROP된 BITMAP
                    iv_UserPhoto.setImageBitmap(photo);//레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌

                    storeCroplmage(photo, filePath);//CROP된 이미지를 외부저장소,앨범에 저장한다.
                    absoultePath = filePath;
                    break;
                }
                //임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if (f.exists()) {
                    f.delete();
                }
            }
        }
    }
    @Override
    public void onClick(View v) {
        id_view = v.getId();
        if (v.getId() == R.id.btn_signupfinish) {
            /**SharedPreference 환경 변수 사용**/
            SharedPreferences preferences = getSharedPreferences("Login", 0);
            /**preferences.getString() return값이 null이라면 2번째 함수를 대입한다.**/
            String login = preferences.getString("USER_LOGIN", "LOGOUT");
            String facebook_login = preferences.getString("FACEBOOK_LOGIN", "LOGOUT");
            String user_id = preferences.getString("USER_ID", "");
            String user_name = preferences.getString("USER_NAME", "");
            String user_password = preferences.getString("USER_PASSWORD", "");
            String user_phone = preferences.getString("USER_PHONE", "");
            String user_email = preferences.getString("USER_EMAIL", "");
//            dbmanger.select(user_id, user_name, user_password, user_phone, user_email);
//            dbmanger.selectPhoto(user_name, mImageCaptureUri, absoultePath);

            Intent mainTntent = new Intent(ChildPicture.this, MainActivity.class);
            mainTntent.putExtra("value","signIn");
            ChildPicture.this.startActivity(mainTntent);
            ChildPicture.this.finish();
            Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();

        } else if (v.getId() == R.id.btn_UploadPicture) {
            DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doTakePhotoAction();
                }
            };
            DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doTakeAlbumAction();
                }
            };
            DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
            new AlertDialog.Builder(this)
                    .setTitle("업로드할 이미지 선택")
                    .setPositiveButton("사진촬영", cameraListener)
                    .setNeutralButton("앨범선택", albumListener)
                    .setNegativeButton("취소", cancelListener)
                    .show();
        }
    }
    /*
    *Bitmap을 저장하는 부분
     */
    private void storeCroplmage(Bitmap bitmap, String filePath){
        //SmartWheel 폴더를 생성하여 이미지를 저장하는 방식
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartWheel";
        File directory_SmartWheel=new File(dirPath);

        if(!directory_SmartWheel.exists())//SmartWheel 디렉터리에 폴더가 없다면(새로운 이미지를 저장할 경우에 속한다)

            directory_SmartWheel.mkdir();

        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            //sendBoardcast를 통해 CROP된 사진을 앨범에 보이도록 갱신
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(copyFile)));

            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}