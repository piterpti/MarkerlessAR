package pl.piterpti.cba.pl;

import org.opencv.core.Point;
import org.opencv.core.Rect;

/**
 * Created by Piter on 07/12/2016.
 */
public class Toolkit {

    public static Rect findBiggestRect(Rect[] rects) {
        int maxArea = 0;
        int tmpArea = 0;
        Rect biggestRect = null;

        for (Rect rect : rects) {
            tmpArea = rect.width * rect.height;
            if (tmpArea > maxArea) {
                maxArea = tmpArea;
                biggestRect = rect;
            }
        }

        return biggestRect;
    }

    public static Rect [] getBiggestRects(Rect[] rects, int maxRects) {
        if (maxRects > rects.length) {
            maxRects = rects.length;
        }

        Rect[] result = new Rect[maxRects];

        for (int i = 0; i < maxRects; i++) {
            result[i] = findBiggestRect(rects);

            Rect[] tmpRects = new Rect[rects.length - 1];
            int counter = 0;
            for (Rect rect : rects) {
                if (rect != result[i]) {
                    tmpRects[counter++] = rect;
                }
            }
            rects = tmpRects;
        }
        return result;
    }

    public static Point getRectCenter(Rect rect) {
        if (rect != null) {
            return new Point(rect.tl().x + rect.width / 2, rect.tl().y + rect.height / 2);
        } else {
            return null;
        }
    }

    public static Rect makeRectsSomeBigger(Rect rect, int widthPlus, int heightPlus, int resWidht, int resHeight) {
        int x = rect.x;
        int y = rect.y;
        int width = rect.width;
        int height = rect.height;

        int xAfter = x - widthPlus / 2 ;

        if (xAfter < 1) {
            xAfter = 1;
        } else {
            xAfter = x -widthPlus / 2;
        }

        int widthAfter = width + widthPlus;

        if (xAfter + widthAfter >= resWidht) {
            widthAfter = resHeight - 1;
        }


        Rect result = new Rect(xAfter, y, widthAfter, height);


        return result;
    }
}
