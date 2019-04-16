package com.telyo.cvlib.interceptor;

import com.telyo.cvlib.PointMat;

import org.opencv.core.Mat;

import java.util.List;

public class RealInterceptorChain implements Interceptor.Chain {

    private final List<Interceptor> interceptors;
    private final int index;
    private final PointMat mat;
    private final PointMat originMat;

    public RealInterceptorChain(List<Interceptor> interceptors, int index, PointMat mat,PointMat originMat) {
        this.interceptors = interceptors;
        this.index = index;
        this.mat = mat;
        this.originMat = originMat;
    }

    public PointMat getMat() {
        return mat;
    }

    public PointMat getOriginMat() {
        return originMat;
    }

    @Override
    public PointMat proceed(PointMat mat) {
        if (!(mat instanceof Mat) || !(originMat instanceof Mat)){
            throw new IllegalArgumentException(mat.toString() +"or" + originMat.toString() + "is not a Mat");
        }
        RealInterceptorChain next = new RealInterceptorChain(
                interceptors,index + 1, mat,originMat);
        Interceptor interceptor = interceptors.get(index);

        PointMat resultMat = interceptor.intercept(next);

        if (resultMat == null) {
            throw new NullPointerException("interceptor " + interceptor + " returned null");
        }
        return resultMat;
    }
}
