package com.example.doublei.MainFuction;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.doublei.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TrainingActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "OpenCV";
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    public static final int JAVA_DETECTOR = 0;
    public static final int NATIVE_DETECTOR = 1;
    private static final int frontCam = 1;
    private static final int backCam = 0;
    private Mat mRgba;
    private Mat mGray;
    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private int mDetectorType = JAVA_DETECTOR;
    private String[] mDetectorName;
    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;
    private String mPath = "";
    public static String text;
    private JavaCameraView mOpenCvCameraView;
    private ImageView Iv;
    private EditText editText;
    private Button capture;
    private Bitmap mBitmap;
    private Handler mHandler;

    static {
        OpenCVLoader.initDebug();
        System.loadLibrary("opencv_java");
    }

    public TrainingActivity() { // 얼굴인식에는 2가지 방법이 있다
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    try {
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface); // raw 폴더에서 xml 파일 불러옴
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE); // 내부 저장소에 사적인 디렉토리 생성
                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml"); // 실제 저장될 파일경로 파일명
                        FileOutputStream os = new FileOutputStream(mCascadeFile); //  파일 아웃풋 스트림 생성

                        byte[] buffer = new byte[4096];
                        int bytesRead;

                        while ((bytesRead = is.read(buffer)) != -1) // 버퍼 크기만큼 읽어서 EOF가 아닐 때까지 쓰기
                        {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close(); // 파일 닫기
                        os.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath()); // 절대 경로
                        mJavaDetector.load(mCascadeFile.getAbsolutePath());

                        if (mJavaDetector.empty())
                        {
                            Log.e(TAG, "Failed to load cascade classifier"); // 자바 디텍터 생성 실패하면 로그 기록
                            mJavaDetector = null;
                        }
                        else Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        cascadeDir.delete(); // 임시로 생성했던 파일 삭제

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.setMaxFrameSize(1600, 1200);
                    mOpenCvCameraView.enableView(); // 카메라 촬영 가능

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        Iv = (ImageView) findViewById(R.id.imagePreview);
        editText = (EditText) findViewById(R.id.nameText);
        capture = (Button) findViewById(R.id.capture); // 캡쳐 버튼

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Canvas canvas = new Canvas();
                canvas.setBitmap(mBitmap);
                FileOutputStream file;

                try {
                    text = editText.getText().toString();
                    file = new FileOutputStream(mPath + text + ".jpg", true); // 경로에 파일 저장

                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, file); // 비트맵을 JPEG 으로 압축

                    Iv.setImageBitmap(mBitmap);
                    Toast.makeText(getApplicationContext(), "캡쳐 완료", Toast.LENGTH_SHORT).show();

                    file.close();
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.e("error", e.getMessage() + e.getCause());
                    e.printStackTrace();
                }
            }
        });

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.java_surface_view); // 자바카메라 뷰
        mOpenCvCameraView.setCameraIndex(backCam); // 카메라 방향
        mOpenCvCameraView.setCvCameraViewListener(this);

        mPath = Environment.getExternalStorageDirectory() + "/Doublei/"; // 비트맵 이미지 저장할 경로
        Log.e("Path", mPath);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj == "IMG") // 메세지 받으면
                {
                    Canvas canvas = new Canvas();
                    canvas.setBitmap(mBitmap);
                    Iv.setImageBitmap(mBitmap);
                }
            }
        };

        boolean success = (new File(mPath)).mkdirs(); // 디렉토리 존재여부

        if (!success)
            Log.e("Error","Error creating directory");
    }

    @Override
    public void onCameraViewStarted(int width, int height) { // 카메라 촬영 시작시
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() { // 카메라 촬영 종료시
        mGray.release();
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) { // 카메라에서 실시간으로 프레임을 받아오는 메서드
        mRgba = inputFrame.rgba(); // 영상의 rgba 값 이진화할때 사용하자
        mGray = inputFrame.gray(); // 영상의 gray 값

        if (mAbsoluteFaceSize == 0) { // 절대 얼굴 크기 0일때
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            //  mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect faces = new MatOfRect(); // 얼굴 영역 표시할 사각형 생성

        if (mDetectorType == JAVA_DETECTOR) { // 자바 디텍터 사용시
            if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }
        else if (mDetectorType == NATIVE_DETECTOR) { // 네이티브 디텍터 사용시
//            if (mNativeDetector != null)
//                mNativeDetector.detect(mGray, faces);
        }
        else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Rect[] facesArray = faces.toArray(); // 사각형 배열 생성

        for (int i = 0; i < facesArray.length; i++) // 인식된 얼굴 갯수
        {
            Rect r = facesArray[i];

            Mat mat = mRgba.submat(r);
            mBitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888); // 비트맵 만들기
            Utils.matToBitmap(mat, mBitmap); // mat을 bitmap 형식으로 변환

            Imgproc.rectangle(mRgba, r.tl(), r.br(), FACE_RECT_COLOR, 3); // 사각형 그리기

            Message msg = new Message();
            String textTochange = "IMG";
            msg.obj = textTochange;
            mHandler.sendMessage(msg);
        }

        return mRgba;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }
}