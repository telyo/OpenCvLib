package com.telyo.cvlib;

import com.telyo.cvlib.widget.ReactPoint;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class PointMat extends Mat implements ReactPoint {

    List<Point> corners = new ArrayList<>();

    private boolean isNeedPreView = false;

    public boolean isNeedPreView() {
        return isNeedPreView;
    }

    public void setNeedPreView(boolean needPreView) {
        isNeedPreView = needPreView;
    }

    @Override
    public List<Point> getCorners() {
        return corners;
    }
    @Override
    public void setCorners(List<Point> corners) {
        this.corners = corners;
    }
    @Override
    public Point getPoint0() {
        return corners.size() > 0 ? corners.get(0) : null;
    }

    @Override
    public Point getPoint1() {
        return corners.size() > 1 ? corners.get(1) : null;

    }
    @Override
    public Point getPoint2() {
        return corners.size() > 2 ? corners.get(2) : null;

    }

    @Override
    public Point getPoint3() {
        return corners.size() > 3 ? corners.get(3) : null;

    }
}
