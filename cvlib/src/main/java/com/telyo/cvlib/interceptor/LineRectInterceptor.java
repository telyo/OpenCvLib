package com.telyo.cvlib.interceptor;

import com.telyo.cvlib.PointMat;

import org.opencv.core.CvType;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static com.telyo.cvlib.utils.CalculationUtils.computeIntersect;
import static com.telyo.cvlib.utils.CalculationUtils.findLargestSquare;
import static com.telyo.cvlib.utils.CalculationUtils.getAngle;
import static com.telyo.cvlib.utils.CalculationUtils.getMatOfPoints;
import static com.telyo.cvlib.utils.CalculationUtils.getSpacePointToPoint;

public class LineRectInterceptor implements Interceptor {
    private static final int MIN_AREA = 8000;

    @Override
    public PointMat intercept(Chain chain) {
        RealInterceptorChain realChain = (RealInterceptorChain) chain;

        PointMat src = realChain.getMat();
        if (src.getCorners().size() == 4){
            realChain.getOriginMat().setCorners(src.getCorners());
            return realChain.proceed(realChain.getOriginMat());
        }
        List<MatOfPoint> contours = getMatOfPoints(src);
        // 找出轮廓对应凸包的四边形拟合
        List<MatOfPoint> squares = new ArrayList<>();
        List<MatOfPoint> hulls = new ArrayList<>();
        MatOfInt hull = new MatOfInt();//集点的凸壳
        MatOfPoint2f approx = new MatOfPoint2f();
        approx.convertTo(approx, CvType.CV_32F);
        for (MatOfPoint contour : contours) {//step5
            // 边框的凸包
            Imgproc.convexHull(contour, hull);
            // 用凸包计算出新的轮廓点
            Point[] contourPoints = contour.toArray();
            int[] indices = hull.toArray();
            List<Point> newPoints = new ArrayList<>();//新的轮廓点
            for (int index : indices) {
                newPoints.add(contourPoints[index]);
            }
            MatOfPoint2f contourHull = new MatOfPoint2f();//新的轮廓点
            contourHull.fromList(newPoints);
            // 多边形拟合凸包边框(此时的拟合的精度较低)
            Imgproc.approxPolyDP(contourHull, approx, Imgproc.arcLength(contourHull, true) * 0.02, true);
            // 筛选出面积大于某一阈值的，且四边形的各个角度都接近直角的凸四边形
            MatOfPoint approxf1 = new MatOfPoint();
            approx.convertTo(approxf1, CvType.CV_32S);
            if (approx.rows() == 4 && Math.abs(Imgproc.contourArea(approx)) > MIN_AREA
                    && Imgproc.isContourConvex(approxf1)) {
                double maxCosine = 0;
                for (int j = 2; j < 5; j++) {
                    double cosine = Math.abs(getAngle(approxf1.toArray()[j % 4], approxf1.toArray()[j - 2], approxf1.toArray()[j - 1]));
                    maxCosine = Math.max(maxCosine, cosine);
                }
                // 角度大概72度
                if (maxCosine < 0.3) {
                    MatOfPoint tmp = new MatOfPoint();
                    contourHull.convertTo(tmp, CvType.CV_32S);
                    squares.add(approxf1);
                    hulls.add(tmp);
                }
            }
        }
        // 找出外接矩形最大的四边形
        int index = findLargestSquare(squares);
        List<Point> corners = new ArrayList<>();

        if (index < 0) {
            Point tl = new Point(realChain.getMat().width() * 0.2, realChain.getMat().height() * 0.2);
            Point tr = new Point(realChain.getMat().width() * 0.8, realChain.getMat().height() * 0.2);
            Point bl = new Point(realChain.getMat().width() * 0.2, realChain.getMat().height() * 0.8);
            Point br = new Point(realChain.getMat().width() * 0.8, realChain.getMat().height() * 0.8);
            corners.add(bl);
            corners.add(tl);
            corners.add(tr);
            corners.add(br);
        } else {
            MatOfPoint largest_square = squares.get(index);
            // Canny边缘检测
            // 找到这个最大的四边形对应的凸边框，再次进行多边形拟合，此次精度较高，拟合的结果可能是大于4条边的多边形
            MatOfPoint contourHull = hulls.get(index);
            MatOfPoint2f tmp = new MatOfPoint2f();
            contourHull.convertTo(tmp, CvType.CV_32F);
            Imgproc.approxPolyDP(tmp, approx, 3, true);
            List<Point> newPointList = new ArrayList<>();
            double maxL = Imgproc.arcLength(approx, true) * 0.02;
            // 找到高精度拟合时得到的顶点中 距离小于低精度拟合得到的四个顶点maxL的顶点，排除部分顶点的干扰
            for (Point p : approx.toArray()) {
                if (!(getSpacePointToPoint(p, largest_square.toList().get(0)) > maxL &&
                        getSpacePointToPoint(p, largest_square.toList().get(1)) > maxL &&
                        getSpacePointToPoint(p, largest_square.toList().get(2)) > maxL &&
                        getSpacePointToPoint(p, largest_square.toList().get(3)) > maxL)) {
                    newPointList.add(p);
                }
            }
            // 找到剩余顶点连线中，边长大于 2 * maxL的四条边作为四边形物体的四条边
            List<double[]> lines = new ArrayList<>();
            for (int i = 0; i < newPointList.size(); i++) {
                Point p1 = newPointList.get(i);
                Point p2 = newPointList.get((i + 1) % newPointList.size());
                if (getSpacePointToPoint(p1, p2) > 2 * maxL) {
                    lines.add(new double[]{p1.x, p1.y, p2.x, p2.y});
                }
            }
            // 计算出这四条边中 相邻两条边的交点，即物体的四个顶点
            for (int i = 0; i < lines.size(); i++) {
                Point corner = computeIntersect(lines.get(i), lines.get((i + 1) % lines.size()));
                corners.add(corner);
            }
        }
        realChain.getOriginMat().setCorners(corners);
        if (realChain.getOriginMat().isNeedPreView()) {
            for (int i = 0; i < corners.size(); i++) {
                Imgproc.line(realChain.getOriginMat(), corners.get(i), corners.get((i + 1) % corners.size()), new Scalar(0, 0, 255), 5);
            }
            return realChain.getOriginMat();
        }
        return realChain.proceed(realChain.getOriginMat());
    }

}
