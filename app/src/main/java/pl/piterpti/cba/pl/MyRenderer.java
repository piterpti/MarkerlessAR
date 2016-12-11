package pl.piterpti.cba.pl;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.renderer.RajawaliRenderer;

/**
 * Created by Piter on 10/12/2016.
 */
public class MyRenderer extends RajawaliRenderer{

    private DirectionalLight directionalLight;

    private Object3D currentObj;

    private double rotationX = 0f;
    private double rotationY = 0f;
    private double rotationZ = 0f;

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


        Obj3D camaro = new Obj3D(mContext, mTextureManager, R.raw.camaro_obj, R.drawable.camaro);

        currentObj = camaro.getObject3D();

        currentObj.setPosition(0, 0, 0);
        currentObj.setScale(0.3, 0.3, 0.3);

        getCurrentScene().addChild(currentObj);

        getCurrentCamera().setZ(4.2);
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        currentObj.setRotation(rotationX, rotationY, rotationZ);
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
}
