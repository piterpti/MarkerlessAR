package pl.piterpti.cba.pl;

import android.util.Log;

import org.rajawali3d.ATransformable3D;
import org.rajawali3d.Object3D;

/**
 * Created by Piter on 12/12/2016.
 */
public class MyObject3D {

    private Object3D object3D;
    private int rotationMethod = 0;

    private double startRotX = 0;
    private double startRotY = 0;
    private double startRotZ = 0;

    public MyObject3D(Object3D aObject3D){
        object3D = aObject3D;
    }

    public void setRotationMethod(int method) {
        rotationMethod = method;
    }

    public void setRotation(double x, double y, double z) {
        x += startRotX;
        y += startRotY;
        z += startRotZ;

        if (rotationMethod == 0) {
            object3D.setRotation(x, y, z);
        } else if (rotationMethod == 1) {
            object3D.setRotation(0, -x, -z);
        } else if (rotationMethod == 2) {
            object3D.setRotation(0, x, z);
        } else {
            object3D.setRotation(x, y, z);
        }
    }

    public void setPosition(double x, double y, double z){
        object3D.setPosition(x, y, z);
    }

    public void setScale(double x, double y, double z) {
        object3D.setScale(x, y, z);
    }

    public Object3D getObject3D() {
        return object3D;
    }

    public void setStartRotX(double startRotX) {
        this.startRotX = startRotX;
    }

    public void setStartRotY(double startRotY) {
        this.startRotY = startRotY;
    }

    public void setStartRotZ(double startRotZ) {
        this.startRotZ = startRotZ;
    }
}
