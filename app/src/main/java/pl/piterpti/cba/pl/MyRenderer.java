package pl.piterpti.cba.pl;

import android.content.Context;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.renderer.RajawaliRenderer;

/**
 * Created by Piter on 10/12/2016.
 */
public class MyRenderer extends RajawaliRenderer{

    private DirectionalLight directionalLight;

    private Object3D currentObj;

    private Object3D[] objects;

    private double rotationX = 0f;
    private double rotationY = 0f;
    private double rotationZ = 0f;

    private int currentModel = 0;

    public void onTouchEvent(MotionEvent event){
    }

    public void onOffsetsChanged(float x, float y, float z, float w, int i, int j){
    }

    public Context context;

    public MyRenderer(Context context) {
        super(context);
        this.context = context;
        setFrameRate(10);
    }

    public void initScene(){

        directionalLight = new DirectionalLight(1f, .2f, -1.0f);
        directionalLight.setColor(1.0f, 1.0f, 1.0f);
        directionalLight.setPower(2);
        getCurrentScene().addLight(directionalLight);

        objects = new Object3D[3];



        Obj3D obj3D= new Obj3D(mContext, mTextureManager, R.raw.camaro_obj, R.drawable.camaro);
        objects[0] = obj3D.getObject3D();

        obj3D= new Obj3D(mContext, mTextureManager, R.raw.cube_obj, R.drawable.camaro);
        objects[1] = obj3D.getObject3D();

        obj3D= new Obj3D(mContext, mTextureManager, R.raw.superman_obj, R.drawable.superman);
        objects[2] = obj3D.getObject3D();

        currentObj = objects[currentModel];

        currentObj.setPosition(0, 0, 0);
        currentObj.setScale(0.3, 0.3, 0.3);


        getCurrentScene().addChild(currentObj);

        getCurrentCamera().setZ(4.2);
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        if (currentObj != null) {
            currentObj.setRotation(rotationX, rotationY, rotationZ);
        }
    }

    public void setPos(double x, double y) {
        if (currentObj != null) {
            currentObj.setPosition(x, y, currentObj.getZ());
        }
    }

    public void rotateToZ(float rotZ) {
        rotationZ = rotZ;
    }

    public void rotateToX(float rotX) {
        rotationX = rotX;
    }

    public void rotateToY(float rotY) {
        rotationY = rotY;
    }

    public void setRendering(boolean status) {
        if (currentObj != null) {
            currentObj.setVisible(status);
        }
    }

    public void nextObj() {
        currentModel++;
        if (currentModel >= objects.length) {
            currentModel = 0;
            getCurrentScene().removeChild(objects[objects.length - 1]);
        } else {
            getCurrentScene().removeChild(objects[currentModel - 1]);
        }

        currentObj = objects[currentModel];
        currentObj.setPosition(0, 0, 0);
        currentObj.setScale(0.3, 0.3, 0.3);
        getCurrentScene().addChild(currentObj);
    }
}
