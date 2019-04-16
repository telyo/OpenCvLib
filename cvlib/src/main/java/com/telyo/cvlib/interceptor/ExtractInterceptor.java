package com.telyo.cvlib.interceptor;

import com.telyo.cvlib.PointMat;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import static com.telyo.cvlib.utils.CalculationUtils.getSpacePointToPoint;
import static com.telyo.cvlib.utils.CalculationUtils.sortCorners;

public class ExtractInterceptor implements Interceptor {
    @Override
    public PointMat intercept(Chain chain) {
        RealInterceptorChain realChain = (RealInterceptorChain) chain;
        PointMat src = realChain.getMat();
        List<Point> corners = src.getCorners();
        sortCorners(corners);
        // 计算目标图像的尺寸
        Point p0 = corners.get(0);
        Point p1 = corners.get(1);
        Point p2 = corners.get(2);
        Point p3 = corners.get(3);
        double space0 = getSpacePointToPoint(p0, p1);
        double space1 = getSpacePointToPoint(p1, p2);
        double space2 = getSpacePointToPoint(p2, p3);
        double space3 = getSpacePointToPoint(p3, p0);

        double imgWidth = space1 > space3 ? space1 : space3;
        double imgHeight = space0 > space2 ? space0 : space2;
        // 如果提取出的图片宽小于高，则旋转90度
        if (imgWidth > imgHeight) {
            double temp = imgWidth;
            imgWidth = imgHeight;
            imgHeight = temp * 1.41;//输出A4纸宽高比
            Point tempPoint = p0.clone();
            p0 = p1.clone();
            p1 = p2.clone();
            p2 = p3.clone();
            p3 = tempPoint.clone();
        } else {
            imgHeight = imgWidth * 1.41;
        }

        Mat quad = Mat.zeros((int) imgHeight * 2, (int) imgWidth * 2, CvType.CV_8UC3);
        PointMat result = new PointMat();
        MatOfPoint2f cornerMat = new MatOfPoint2f(p0, p1, p2, p3);
        MatOfPoint2f quadMat = new MatOfPoint2f(new Point(imgWidth * 0, imgHeight * 2.0),
                new Point(imgWidth * 0, imgHeight * 0),
                new Point(imgWidth * 2.0, imgHeight * 0),
                new Point(imgWidth * 2.0, imgHeight * 2.0));
// 提取图像
        Mat transmtx = Imgproc.getPerspectiveTransform(cornerMat, quadMat);
        Imgproc.warpPerspective(src, quad, transmtx, quad.size());
        quad.copyTo(result);
        return result;
    }
}
