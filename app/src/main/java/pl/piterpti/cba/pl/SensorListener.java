package pl.piterpti.cba.pl;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by Piter on 26/11/2016.
 */
public class SensorListener implements SensorEventListener {

    // -x - right || x - left
    // -y - forward || y - backwards
    // -z || z

    private float x = 0f;
    private float z = 0f;
    private float y = 0f;

    @Override
    public void onSensorChanged(SensorEvent event) {
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public String toString() {
        return "SensorListener{" +
                "x=" + x +
                ", z=" + z +
                ", y=" + y +
                '}';
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }
}