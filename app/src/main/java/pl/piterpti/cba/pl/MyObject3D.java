package pl.piterpti.cba.pl;

import org.rajawali3d.ATransformable3D;
import org.rajawali3d.Object3D;

/**
 * Created by Piter on 12/12/2016.
 */
public class MyObject3D extends Object3D {

    public MyObject3D() {
        super();
    }

    public void setRot(double x1, double x2, double x3) {
        setRotation(x1, x2, x3);
    }
}
