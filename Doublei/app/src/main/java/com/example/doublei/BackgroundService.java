package com.example.doublei;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class BackgroundService extends Service implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "OpenCV";
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    private static final Scalar EYES_RECT_COLOR = new Scalar(255, 0, 0, 255);
    public static final int JAVA_DETECTOR = 0;
    public static final int NATIVE_DETECTOR = 1;
    private static final int frontCam = 1;
    private static final int backCam = 0;
    private boolean faceState = false;
    private Mat mRgba;
    private Mat mGray;
    private File mCascadeFile;
    private File mCascadeFileEye;
    private CascadeClassifier mJavaDetector;
    private CascadeClassifier mJavaDetectorEye;
    private String[] mDetectorName;
    private int mDetectorType = JAVA_DETECTOR;
    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;
    private String mPath = "";
    private JavaCameraView mOpenCvCameraView;
    TextView resultName;
    Handler mHandler;
    Set<String> uniqueNames = new HashSet<String>(); // 집합 Collection 순서 의미 없고 데이터 중복 할수 없음
    int count = 0;
    int imageCount = 0;
    int[] eyeCount = new int[2];
    int[] leftCount = new int[2];
    int[] middleCount = new int[2];
    int[] rightCount = new int[2];
    boolean[] leftArea = new boolean[2];
    boolean[] middleArea = new boolean[2];
    boolean[] rightArea = new boolean[2];
    boolean strabismus = false;
    boolean trueFace = false;
    private View mPopupView; // 항상 보이게 할 뷰
    private WindowManager mManager; // 최상위에 떠있는 뷰 만들기 위해
    private WindowManager.LayoutParams mParams; // 뷰의 위치 및 크기
    private static int cameraWidth = 320; // 320 280 / 480 320 / 640 480 / 800 600 / 1280 720
    private static int cameraHeight = 240;
    private double leftEyePosition = 0.0;
    private double rightEyePosition = 0.0;
    private double distance = 0.0;

    static {
        OpenCVLoader.initDebug();
        System.loadLibrary("opencv_java");
    }

    public BackgroundService() {
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
                    Log.e(TAG, "OpenCV loaded successfully");

                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;

                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        InputStream ise = getResources().openRawResource(R.raw.haarcascade_righteye_2splits);
                        File cascadeDirEye = getDir("cascadeEye", Context.MODE_PRIVATE);
                        mCascadeFileEye = new File(cascadeDirEye, "haarcascade_lefteye_2splits.xml");
                        FileOutputStream ose = new FileOutputStream(mCascadeFileEye);

                        byte[] bufferEye = new byte[4096];
                        int bytesReadEye;

                        while ((bytesReadEye = ise.read(bufferEye)) != -1) {
                            ose.write(bufferEye, 0, bytesReadEye);
                        }
                        ise.close();
                        ose.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        mJavaDetector.load(mCascadeFile.getAbsolutePath());

                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        }
                        else Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        mJavaDetectorEye = new CascadeClassifier(mCascadeFileEye.getAbsolutePath());
                        mJavaDetectorEye.load(mCascadeFileEye.getAbsolutePath());

                        if (mJavaDetectorEye.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetectorEye = null;
                        }
                        else Log.i(TAG, "Loaded cascade classifier from " + mCascadeFileEye.getAbsolutePath());

                        cascadeDir.delete();
                        cascadeDirEye.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.setMaxFrameSize(cameraWidth, cameraHeight); // 카메라 최대크기 지정
                    mOpenCvCameraView.enableView(); // 카메라뷰 활성화

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onCreate() {
        Log.e(TAG, "Camera Service start");
        super.onCreate();

        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE); // 인플레이터
        mPopupView = mInflater.inflate(R.layout.activity_background_service, null); // 최상단 뷰

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, PixelFormat.TRANSLUCENT); // 레이아웃

        mOpenCvCameraView = (JavaCameraView) mPopupView.findViewById(R.id.java_surface_view);
        mOpenCvCameraView.setCameraIndex(frontCam);
        mOpenCvCameraView.setCvCameraViewListener(this);

        mParams.width = cameraWidth;
        mParams.height = cameraHeight;
        mParams.gravity = Gravity.RIGHT | Gravity.TOP;

        mManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mManager.addView(mPopupView, mParams);

        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "OPENCV initalization error");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_2, this, mLoaderCallback);
        }
        else {
            Log.e(TAG, "OPENCV initialization success");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        mPath = Environment.getExternalStorageDirectory() + "/Doublei/";
        Log.e("Path", mPath);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) { // 메세지 받아서 동작
                String tempName = msg.obj.toString();
                resultName.setText(tempName);
            }
        };

        boolean success = (new File(mPath)).mkdirs();
        if (!success)
        {
            Log.e("Error","Error creating directory");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    public void onDestroy() {
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
        if (mManager != null) {
            if (mPopupView != null) { // 서비스 종료시 뷰를 제거
                mManager.removeView(mPopupView);
            }
        }
        super.onDestroy();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

//        Mat rgbaT = mRgba.t();
//        Core.flip(mRgba.t(), rgbaT, -1);
//        Imgproc.resize(rgbaT, rgbaT, mRgba.size());
//
//        Mat grayT = mGray.t();
//        Core.flip(mGray.t(), grayT, -1);
//        Imgproc.resize(grayT, grayT, mGray.size());

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize); // 높이 * 0.2 를 반올림
            }
            //  mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect faces = new MatOfRect();
        MatOfRect eyes = new MatOfRect();

        if (mDetectorType == JAVA_DETECTOR) { // 자바 디텍터 일때
            if (mJavaDetector != null) // 객체가 null 이 아닐때
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
            if (mJavaDetectorEye != null) {
                mJavaDetectorEye.detectMultiScale(mGray, eyes);
            }
        }
        else if (mDetectorType == NATIVE_DETECTOR) {
            /*if (mNativeDetector != null)
                mNativeDetector.detect(mGray, faces);*/
        }
        else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Rect[] facesArray = faces.toArray();
        Rect[] eyesArray = eyes.toArray();

//        for (int i = 0; i < facesArray.length; i++) // 검출된 얼굴이나 눈을 사각형으로 그리기
//        {
//            Rect r = facesArray[i];
//            Imgproc.rectangle(rgbaT, r.tl(), r.br(), FACE_RECT_COLOR, 3); // 얼굴 그리기
//
//            Mat faceArea = grayT.submat(r);
//
//            int ret;
//            ret = CompareFeature(mPath + "iu.jpg", faceArea);
//
//            if (ret > 0) {
//                trueFace = true;
//                Message msg = new Message();
//                String result = "same person";
//                msg.obj = result;
//                mHandler.sendMessage(msg);
//
//                Log.e("face Test", "Two images are same");
//            }
//            else {
//                trueFace = false;
////                Message msg = new Message();
////                String result = "different person";
////                msg.obj = result;
////                mHandler.sendMessage(msg);
//
//                Log.e("face Test", "Two images are different");
//            }
//        }

        for (int i = 0; i < eyesArray.length; i++) // 인식된 눈 갯수만큼 기능 실행
        {
            if (eyesArray.length == 2) {
                Rect r = eyesArray[i];

//                Rect eyearea = new Rect(r.x, r.y, r.width, r.height);

                Rect eyearea = new Rect(r.x + r.width/6, (int)(r.y + r.height/1.8), (int)(r.width - r.width/2.5), (int)(r.height/3.5)); // 검출된 눈 영역 크기 조절

                Imgproc.rectangle(mRgba, eyearea.tl(), eyearea.br(), EYES_RECT_COLOR, 2); // 조절된 눈 영역 사각형으로 그리기

//                if (i == 0) {
//                    leftEyePosition = r.x;
//                    Log.e("Distance 1", String.valueOf(leftEyePosition));
//                }
//                else if (i == 1) {
//                    rightEyePosition = r.x;
//                    Log.e("Distance 2", String.valueOf(rightEyePosition));
//
//                    distance = Math.abs(leftEyePosition - rightEyePosition);
//                    Log.e("Distance 3", String.valueOf(distance));
//                }
//
//                showNotification(distance); // 거리 판단

                Mat inputGrayImage = mGray.submat(eyearea); // 그레이 이미지

                // OTSU
//                Imgproc.threshold(inputGrayImage, inputGrayImage, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

//                Strabismus(inputGrayImage); // 사시 진단

                SaveBmp(inputGrayImage, mPath); // 이미지 저장
            }
        }

        return mRgba;
    }

    public void showNotification(double distance) {
        if(distance > 80.0) {
            android.support.v4.app.NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_pororoclose)
                            .setContentTitle("경고 알림")
                            .setContentText("스마트폰과 얼굴 사이의 거리가 너무 가까워요.")
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setPriority(Notification.PRIORITY_HIGH);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, mBuilder.build());
        }
    }

    public int CompareFeature(String fileName1, Mat currentImage) { // 이미지 유사 비교
        int retVal = 0;
        long startTime = System.currentTimeMillis();

//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat image1 = Imgcodecs.imread(fileName1, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE); // 이미지 로드
        Mat image2 = currentImage;

        MatOfKeyPoint keyPoint1 = new MatOfKeyPoint(); // 이미지의 키포인트 선언
        MatOfKeyPoint keyPoint2 = new MatOfKeyPoint();
        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();

        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB); // ORB 키포인트 디텍터
        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB); // 묘사 추출자

        detector.detect(image1, keyPoint1); // 키 포인트 탐지
        detector.detect(image2, keyPoint2);

        extractor.compute(image1, keyPoint1, descriptors1); // 묘사점 추출
        extractor.compute(image2, keyPoint2, descriptors2);

        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING); // 묘사 매쳐

        MatOfDMatch matches = new MatOfDMatch(); // 두 이미지의 매치 포인트

        if (descriptors1.cols() == descriptors2.cols()) { // 서로 완전히 다른 이미지면 Assert 오류를 피하기 위해
            matcher.match(descriptors1, descriptors2, matches);

            DMatch[] match = matches.toArray(); // 키 포인트의 매쳐들 체크
            double max_dist = 0;
            double min_dist = 100;

            for (int i = 0; i < descriptors1.rows(); i++) {
                double dist = match[i].distance;
                if (dist < min_dist) min_dist = dist;
                if (dist > max_dist) max_dist = dist;
            }
            Log.e("face Distance Test", "max_dist = " + String.valueOf(max_dist) + " min_dist = " + String.valueOf(min_dist));

            for (int i = 0; i < descriptors1.rows(); i++) {
                if (match[i].distance <= 10) { // 거리가 10 이하면 유사한 이미지
                    retVal++;
                }
            }
            Log.e("face matching count = ", String.valueOf(retVal));
        }

        long estimatedTime = System.currentTimeMillis() - startTime;
        Log.e("face estimatedTime : ", estimatedTime + "ms");

        return retVal;
    }

    public boolean Strabismus(Mat inputGrayImage) { // 사시 진단
        int[] pixelArea = new int[3];
        boolean[] maxArea = new boolean[3];
        int blackCount = 0;
        int whiteCount = 0;

        for (int j = 0; j < inputGrayImage.height(); j++) {
            for (int k = 0; k < inputGrayImage.width(); k++) { // 이진화된 눈영역 검사

                double pixel = inputGrayImage.get(j,k)[0];

                if (pixel == 0) {
                    if (0 <= k && inputGrayImage.width()/3 > k) {
                        pixelArea[0]++;
                    }
                    else if (inputGrayImage.width()/3 <= k && inputGrayImage.width()*2/3 > k) {
                        pixelArea[1]++;
                    }
                    else if (inputGrayImage.width()*2/3 <= k && inputGrayImage.width() >= k) {
                        pixelArea[2]++;
                    }
                    blackCount++;
                }

                else if (pixel == 255) {
                    whiteCount++;
                }
            }
        }

        pixelArea[0] = pixelArea[0] + (blackCount/10); // 왼쪽 오른쪽 가중치 주기
        pixelArea[2] = pixelArea[2] + (blackCount/10);

        if (pixelArea[0] > pixelArea[1] && pixelArea[0] > pixelArea[2]) {
            maxArea[0] = true;
            maxArea[1] = false;
            maxArea[2] = false;
        }
        else if (pixelArea[1] > pixelArea[0] && pixelArea[1] > pixelArea[2]) {
            maxArea[0] = false;
            maxArea[1] = true;
            maxArea[2] = false;
        }
        else if (pixelArea[2] > pixelArea[0] && pixelArea[2] > pixelArea[1]) {
            maxArea[0] = false;
            maxArea[1] = false;
            maxArea[2] = true;
        }

        if (count < 2) { // 이미지 2개씩 비교 왼눈 오른눈 1쌍씩
            leftCount[count] = pixelArea[0];
            middleCount[count] = pixelArea[1];
            rightCount[count] = pixelArea[2];

            leftArea[count] = maxArea[0];
            middleArea[count] = maxArea[1];
            rightArea[count] = maxArea[2];

            Log.e("left Count", String.valueOf(leftCount[count]) + " " + String.valueOf(leftArea[count]));
            Log.e("middle Count", String.valueOf(middleCount[count]) + " " + String.valueOf(middleArea[count]));
            Log.e("right Count", String.valueOf(rightCount[count]) + " " + String.valueOf(rightArea[count]));
            Log.e("Black Count", String.valueOf(blackCount));
            Log.e("White Count", String.valueOf(whiteCount));

            count++;
        }
        if (count == 2) {
            if ((leftArea[0] && leftArea[1]) || (middleArea[0] && middleArea[1]) || (rightArea[0] && rightArea[1])) {
                strabismus = false;
                Log.e("Strabismus Count", String.valueOf(strabismus));
            }
            else {
                strabismus = true;
                Log.e("Strabismus Count", String.valueOf(strabismus));
            }

            // 초기화
            for (int l = 0; l < 2; l++) {
                leftCount[l] = 0;
                middleCount[l] = 0;
                rightCount[l] = 0;

                leftArea[l] = false;
                middleArea[l] = false;
                rightArea[l] = false;
            }

            count = 0;
        }

        return strabismus;
    }

    private void SaveBmp(Mat inputGrayImage, String path) // 비트맵 저장 메서드
    {
        Bitmap bitmap = Bitmap.createBitmap(inputGrayImage.width(), inputGrayImage.height(), Bitmap.Config.ARGB_8888); // 비트맵 만들기
        Utils.matToBitmap(inputGrayImage, bitmap); // mat을 bitmap 형식으로 변환

        FileOutputStream file;
        try {
            file = new FileOutputStream(path + "Test-" + imageCount + ".jpg", true); // 경로에 파일 저장
            imageCount++;

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, file); // 비트맵을 JPEG 으로 압축
            file.close();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("error", e.getMessage() + e.getCause());
            e.printStackTrace();
        }
    }
}
