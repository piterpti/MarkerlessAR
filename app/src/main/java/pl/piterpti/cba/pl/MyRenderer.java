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

        objects = new Object3D[1];

//        Object3D object3D = loadModel(R.raw.camaro_obj, R.drawable.camaro);
//        object3D.setScale(0.3, 0.3, 0.3);
//        objects[0] = object3D;

        Object3D object3D = loadModel(R.raw.deer_obj, R.drawable.deer);
        object3D.setScale(0.05, 0.05, 0.05);
        objects[0] = object3D;
//
//        Object3D object3D = loadModel(R.raw.superman_obj, R.drawable.superman);
//        object3D.setScale(0.3, 0.3, 0.3);
//        objects[0] = object3D;

        currentObj = objects[currentModel];

        currentObj.setPosition(0, 0, 0);

        getCurrentScene().addChild(currentObj);

        getCurrentCamera().setZ(4.2);
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        if (currentObj != null) {
            currentObj.setRotation(rotationX, rotationY, rotationZ - 70);
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
            getCurrentScene().clearChildren();
        } else {
            getCurrentScene().removeChild(objects[currentModel - 1]);
        }

        currentObj = objects[currentModel];
        currentObj.setPosition(0, 0, 0);
        getCurrentScene().addChild(currentObj);
    }

    public Object3D loadModel(int objRaw, int texRaw) {

        Object3D object3D = null;

        LoaderOBJ parser = new LoaderOBJ(context.getResources(), mTextureManager, objRaw);
        try {
            parser.parse();
            object3D = parser.getParsedObject();

            if (texRaw != -1) {
                Material material = new Material();
                material.enableLighting(true);
                material.setDiffuseMethod(new DiffuseMethod.Lambert());
                material.setColor(0);

                Texture tex = new Texture("name", texRaw);
                material.addTexture(mTextureManager.addTexture(tex));
                object3D.setMaterial(material);
            }

        } catch (ParsingException e) {
            Log.w("Excetpion", "Parsing exception: " + e.toString());
        } catch (ATexture.TextureException e2) {
            Log.w("Excetpion", "TEXTURE ERROR: " + e2.toString());
        }

        return object3D;
    }
}
