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

    private MyObject3D currentObj;

    private MyObject3D[] objects;

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
        objects = new MyObject3D[3];
    }

    public void initScene(){

        directionalLight = new DirectionalLight(1f, .2f, -1.0f);
        directionalLight.setColor(1.0f, 1.0f, 1.0f);
        directionalLight.setPower(2);
        getCurrentScene().addLight(directionalLight);

        MyObject3D object3D = loadModel(R.raw.camaro_obj, R.drawable.camaro);
        object3D.setScale(0.3, 0.3, 0.3);
        objects[0] = object3D;

        object3D = loadModel(R.raw.deer_obj, R.drawable.deer);
        object3D.setScale(0.05, 0.05, 0.05);
        object3D.setRotationMethod(1);
        object3D.setStartRotZ(-70);
        objects[1] = object3D;

        object3D = loadModel(R.raw.superman_obj, R.drawable.superman);
        object3D.setScale(0.3, 0.3, 0.3);
        object3D.setRotationMethod(2);
        object3D.setStartRotZ(-70);
        objects[2] = object3D;

        currentObj = objects[currentModel];
        currentObj.setPosition(0, 0, 0);


        getCurrentScene().addChild(currentObj.getObject3D());

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
            currentObj.setPosition(x, y, currentObj.getObject3D().getZ());
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

    public void rotateToXYZ(float rotX, float rotY, float rotZ) {
        rotationX = rotX;
        rotationY = rotY;
        rotationZ = rotZ;
    }

    public void setRendering(boolean status) {
        if (currentObj != null) {
            currentObj.getObject3D().setVisible(status);
        }
    }

    public void nextObj() {
        if (currentObj != null) {
            currentModel++;
            if (currentModel >= objects.length) {
                currentModel = 0;
                getCurrentScene().removeChild(objects[objects.length - 1].getObject3D());
            } else {
                getCurrentScene().removeChild(objects[currentModel - 1].getObject3D());
            }
            getCurrentScene().clearChildren();

            currentObj = objects[currentModel];
            currentObj.setPosition(0, 0, 0);
            getCurrentScene().addChild(currentObj.getObject3D());
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void prevObj() {
        if (currentObj != null) {
            currentModel--;
            if (currentModel < 0) {
                currentModel = objects.length - 1;
            }

            getCurrentScene().clearChildren();

            currentObj = objects[currentModel];
            currentObj.setPosition(0, 0, 0);
            getCurrentScene().addChild(currentObj.getObject3D());
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public MyObject3D loadModel(int objRaw, int texRaw) {

        MyObject3D object3D = null;

        LoaderOBJ parser = new LoaderOBJ(context.getResources(), mTextureManager, objRaw);
        try {
            parser.parse();
            Object3D obj3D = parser.getParsedObject();

            if (texRaw != -1) {
                Material material = new Material();
                material.enableLighting(true);
                material.setDiffuseMethod(new DiffuseMethod.Lambert());
                material.setColor(0);

                Texture tex = new Texture("name", texRaw);
                material.addTexture(mTextureManager.addTexture(tex));
                obj3D.setMaterial(material);
            }

            object3D = new MyObject3D(obj3D);

        } catch (ParsingException e) {
            Log.w("Excetpion", "Parsing exception: " + e.toString());
        } catch (ATexture.TextureException e2) {
            Log.w("Excetpion", "TEXTURE ERROR: " + e2.toString());
        }

        return object3D;
    }
}
