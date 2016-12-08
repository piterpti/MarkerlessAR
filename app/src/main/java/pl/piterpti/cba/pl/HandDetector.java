package pl.piterpti.cba.pl;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.*;

/**
 * Created by Piter on 06/12/2016.
 */
public class HandDetector extends Activity implements CvCameraViewListener2 {

    private static final String MAIN_LOG_TAG = "Main::LOG";

    private static final Scalar CONTOUR_COLOR_GREEN = new Scalar(0, 255, 0, 255);
    private static final Scalar CONTOUR_COLOR_RED = new Scalar(255, 0, 0, 255);
    private static final Scalar CONTOUR_COLOR_BLUE = new Scalar(0, 0, 255, 255);

    /**
     * accelerometer data
     */
    private Sensor accelerometer;
    private SensorManager sensorManager;
    private SensorListener mySensorListener;

    private Mat mRgba;
    private Mat mGray;
    private Mat mThresheld;
    private Mat kernel;
    private Mat hierarchy;
    private Mat hand;

    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;

    private float minHandHeight = 0.2f;
    private int minHandWidth = 0;

    private CameraBridgeViewBase mOpenCvCameraView;

    private GLRenderer myGLRenderer;

    private int resWidth = 0;
    private int resHeight = 0;

    private Scalar mBlobColorHsv;

    private ColorDetector colorDetector;

    private Rect handSuspect;
    private ArrayList<Point> fingerTips = new ArrayList<>();
    private Point cogPt;

    private static final double START_OBJ_SIZE = 1;
    private double cubeSize = 0;

    public HandDetector() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(MAIN_LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.camera_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);

        // OpenGL settings
        GLSurfaceView myGLView = new GLSurfaceView(this);
        myGLView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        myGLView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        myGLRenderer = new GLRenderer();
        myGLView.setRenderer(myGLRenderer);
        addContentView(myGLView, new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT));
        myGLView.setZOrderMediaOverlay(true);

        // Accelerometer settings
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mySensorListener = new SensorListener();

        sensorManager.registerListener(mySensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        colorDetector = new ColorDetector();
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
        mThresheld = new Mat();
        kernel = new Mat();
        hierarchy = new Mat();
        hand = new Mat();

        approxStorage = new MatOfPoint2f();
        hullStorage = new MatOfInt();
        defectsStorage = new MatOfInt4();

        resWidth = width;
        resHeight = height;

        myGLRenderer.setVidDim(width, height);
        myGLRenderer.setRenderCube(true);
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
        return detectHand(inputFrame);
    }

    public Mat detectHand(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (minHandWidth == 0) {
            int height = mGray.rows();
            if (Math.round(height * minHandHeight) > 0) {
                minHandWidth = Math.round(height * minHandHeight);
            }
        }

        MatOfRect handSuspects = new MatOfRect();

        if (mJavaDetector != null) {
            mJavaDetector.detectMultiScale(mGray, handSuspects, 1.1, 2, 2, new Size(minHandWidth, minHandHeight), new Size());
        }

        handSuspect = Toolkit.findBiggestRect(handSuspects.toArray());

        if (handSuspect != null) {

            Imgproc.cvtColor(mRgba, mThresheld, Imgproc.COLOR_RGB2HSV_FULL);
            getHsvOfPoint(Toolkit.getRectCenter(handSuspect));

            mThresheld = mThresheld.submat(handSuspect);

            Core.rectangle(mRgba, handSuspect.tl(), handSuspect.br(), CONTOUR_COLOR_GREEN, 3);

            Core.inRange(mThresheld, colorDetector.getlowerBound(), colorDetector.getUpperBound(), mThresheld);
            Imgproc.morphologyEx(mThresheld, mThresheld, Imgproc.MORPH_OPEN, kernel);
            Imgproc.dilate(mThresheld, mThresheld, new Mat());
            MatOfPoint biggestContour = findBiggestContour(mThresheld);

            if (biggestContour != null) {

                findFingerTips(biggestContour);

                for (Point p : fingerTips) {
                    Core.circle(mRgba, new Point(p.x + handSuspect.tl().x, p.y + handSuspect.tl().y), 3, CONTOUR_COLOR_BLUE, 3);
                }
            }
        }

        myGLRenderer.setRenderCube(false);
        return mRgba;
    }


    private void getHsvOfPoint(Point rect) {

        cogPt = new Point(rect.x, rect.y + 50);
        Rect r = new Rect((int)rect.x - 5,(int) rect.y + 45, 10, 10);
        Mat touchedRegionHsv = mThresheld.submat(r);
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = r.width * r.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++) {
            mBlobColorHsv.val[i] /= pointCount;
        }
        colorDetector.setHsvColor(mBlobColorHsv);
    }

    private MatOfPoint findBiggestContour(Mat img) {

        MatOfPoint biggestContour = null;
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat cp = img.clone();
        Imgproc.findContours(cp, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        double maxArea = 30;

        if (contours != null) {

            for (MatOfPoint point : contours) {

                if (point.elemSize() > 0) {
                    double area = Imgproc.contourArea(point);

                    if (area > maxArea) {
                        maxArea = area;
                        biggestContour = point;
                    }
                }
            }
        }

        return biggestContour;
    }

    private MatOfPoint2f approxStorage;
    private MatOfInt hullStorage;
    private MatOfInt4 defectsStorage;

    private void findFingerTips(MatOfPoint contour) {

        Imgproc.approxPolyDP(new MatOfPoint2f(contour.toArray()), approxStorage, 12, false);
        approxStorage.convertTo(contour, CvType.CV_32S);
        Imgproc.convexHull(contour, hullStorage, true);

        try {
            Imgproc.convexityDefects(contour, hullStorage, defectsStorage);
        } catch (Exception e) {
            Log.d("Exception!!", e.toString());
        }

        drawContour(contour);
        findFingers(contour);
    }

    private void drawContour(MatOfPoint contour) {
        ArrayList<MatOfPoint> con = new ArrayList<>();
        con.add(contour);
        Imgproc.drawContours(mRgba, con, 0, CONTOUR_COLOR_RED, 5, 8, new Mat(), Integer.MAX_VALUE, handSuspect.tl());

    }

    private void findFingers(MatOfPoint contour) {

        fingerTips.clear();

        try {
            for (int i = 0; i < defectsStorage.toList().size(); i = i + 4) {

                Point start = contour.toList().get(defectsStorage.toList().get(i));
                Point end = contour.toList().get(defectsStorage.toList().get(i + 1));
                Point depth = contour.toList().get(defectsStorage.toList().get(i + 2));

                double angle = Math.atan2(cogPt.y - start.y, cogPt.x - start.x);
                double inAngle = Toolkit.angleBetween(start, end, depth, false);
                double length = Toolkit.distanceBetweenPoints(start, depth);

                if (angle > -30 && angle < 160 && Math.abs(inAngle) > 20 && Math.abs(inAngle) < 120 && length > 0.1 * handSuspect.height) {
                    if (start.y < cogPt.y) {
                        fingerTips.add(start);
                    }
                }
            }
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
    }

    private void getDirection() {

        Point avgFing = Toolkit.avgFromPoint(fingerTips);

        Point aboveCogPt = new Point(avgFing.x, cogPt.y);

        int angle = Toolkit.angleBetween(cogPt, aboveCogPt, avgFing, true);

        angle -= 10;

        myGLRenderer.rotateToZ(-angle);
        myGLRenderer.rotateToY(-mySensorListener.getY() * 10);
        myGLRenderer.rotateToX(-mySensorListener.getX() * 5);

        double tempDis;
        cubeSize = 0;
        for (Point p : fingerTips) {
            tempDis = Toolkit.distanceBetweenPoints(cogPt, p);
            if (tempDis > cubeSize) {
                cubeSize = tempDis;
            }
        }
    }


}
