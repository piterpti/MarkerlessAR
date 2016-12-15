package pl.piterpti.cba.pl;

import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.*;

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

    public static Point getRectCenter(Rect rect) {
        if (rect != null) {
            return new Point(rect.tl().x + rect.width / 2, rect.tl().y + rect.height / 2);
        } else {
            return null;
        }
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

    public static Point avgFromPoint(ArrayList<Point> points) {

        double x = 0;
        double y = 0;
        for (Point p : points) {
            x += p.x;
            y += p.y;
        }

        x /= points.size();
        y /= points.size();

        return new Point(x, y);
    }
}
