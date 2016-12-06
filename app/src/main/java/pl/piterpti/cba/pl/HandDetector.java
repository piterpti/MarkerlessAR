package pl.piterpti.cba.pl;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Piter on 06/12/2016.
 */
public class HandDetector extends Activity implements CvCameraViewListener2 {

    private static final String MAIN_LOG_TAG = "Main::LOG";

    private static final Scalar CONTOUR_COLOR_GREEN = new Scalar(0, 255, 0, 255);



    private Mat mRgba;
    private Mat mGray;

    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;

    private float relativeHandSize = 0.2f;
    private int absoluteHandSize = 0;

    private CameraBridgeViewBase mOpenCvCameraView;

    public HandDetector() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(MAIN_LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.camera_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
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
    public void onPause(){
        super.onPause();
        if(mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback)) {
            Log.i(MAIN_LOG_TAG, "Resume failed!");
        }
    }

    public void onDestroy(){
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status){
            switch(status){
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(MAIN_LOG_TAG, "OpenCV Loaded Successfully");
                    System.loadLibrary("detection_based_tracker");

                    try{
                        InputStream is = getResources().openRawResource(R.raw.hand2);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "hand2.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(MAIN_LOG_TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else {
                            Log.i(MAIN_LOG_TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
                            mJavaDetector.load(mCascadeFile.getAbsolutePath());
                        }

                        cascadeDir.delete();
                    }catch(IOException e){
                        e.printStackTrace();
                        Log.i(MAIN_LOG_TAG, "Failed to load cascade. Exception thrown: " + e);
                    }
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (absoluteHandSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * relativeHandSize) > 0) {
                absoluteHandSize = Math.round(height * relativeHandSize);
            }
        }

        MatOfRect faces = new MatOfRect();

        if (mJavaDetector != null) {
            mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(absoluteHandSize, relativeHandSize), new Size());
        }


        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), CONTOUR_COLOR_GREEN, 3);

        return mRgba;
    }
}
