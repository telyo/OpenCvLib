package com.telyo.cvlib.interceptor;

import com.telyo.cvlib.PointMat;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class PreExtractInterceptor implements Interceptor {
    private static final int MIN_THRESHOLDL = 20;
    private static final int MAX_THRESHOLDL = 60;
    @Override
    public PointMat intercept(Chain chain) {
        RealInterceptorChain realChain = (RealInterceptorChain) chain;
        PointMat img = realChain.getMat();
        // 彩色转灰度
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);//step1
//         高斯滤波，降噪
        Imgproc.GaussianBlur(img, img, new Size(3, 3), 2, 2);//step2
        // Canny边缘检测
        Imgproc.Canny(img, img, MIN_THRESHOLDL, MAX_THRESHOLDL, 3, false);//step3
        // 膨胀，连接边缘
        Imgproc.dilate(img, img, new Mat(), new Point(-1, -1), 3, 1, new Scalar(1));//step4

        return realChain.proceed(img);
    }
}
