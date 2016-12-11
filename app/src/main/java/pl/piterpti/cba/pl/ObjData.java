package pl.piterpti.cba.pl;

/**
 * Created by Piter on 11/12/2016.
 */
public class ObjData {

    private int rawVal = -1;
    private int drawableVal = -1;

    private int startRotX = 0;
    private int startRotY = 0;
    private int startRotZ = 0;

    public ObjData(int raw, int draw) {
        rawVal = raw;
        drawableVal = draw;
    }

    public int getRawVal() {
        return rawVal;
    }

    public void setRawVal(int rawVal) {
        this.rawVal = rawVal;
    }

    public int getDrawableVal() {
        return drawableVal;
    }

    public void setDrawableVal(int drawableVal) {
        this.drawableVal = drawableVal;
    }

    public int getStartRotX() {
        return startRotX;
    }

    public void setStartRotX(int startRotX) {
        this.startRotX = startRotX;
    }

    public int getStartRotY() {
        return startRotY;
    }

    public void setStartRotY(int startRotY) {
        this.startRotY = startRotY;
    }

    public int getStartRotZ() {
        return startRotZ;
    }

    public void setStartRotZ(int startRotZ) {
        this.startRotZ = startRotZ;
    }
}
