package pl.piterpti.cba.pl;

import org.opencv.core.Point;
import org.opencv.core.Rect;

/**
 * Created by Piter on 07/12/2016.
 */
public class Toolkit {

    public static Rect findBiggestRect(Rect[] rects) {
        int maxArea = 0;
        int tmpArea;
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
            widthAfter = resWidht - 1;
        }

        int yAfter = y - heightPlus / 2;
        if (yAfter < 1) {
            yAfter = 1;
        } else {
            yAfter = y - heightPlus / 2;
        }

        int heightAfter = height + heightPlus;

        if (yAfter + heightAfter >= resHeight) {
            heightAfter = resHeight;
        }

        Rect result = new Rect(xAfter, yAfter, widthAfter, heightAfter);


        return result;
    }

    public static int angleBetween(Point tip, Point next, Point prev, boolean negativeVal) {
        int angle = ((int)Math.round(
                Math.toDegrees(
                        Math.atan2(next.x - tip.x, next.y - tip.y) -
                                Math.atan2(prev.x - tip.x, prev.y - tip.y)) ));

        if (! negativeVal) {
            angle = Math.abs(angle);
        }

        return angle;
    }

    public static double distanceBetweenPoints(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    public static Point avgFromPoints(Point p1, Point p2) {
        return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) /2);
    }


}
