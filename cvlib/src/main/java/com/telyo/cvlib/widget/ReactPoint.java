package com.telyo.cvlib.widget;

import org.opencv.core.Point;

import java.util.List;

public interface ReactPoint {
    Point getPoint0();
    Point getPoint1();
    Point getPoint2();
    Point getPoint3();
    List<Point> getCorners();
    void setCorners(List<Point> corners);
}
