package com.telyo.cvlib.extract;

import com.telyo.cvlib.PointMat;
import com.telyo.cvlib.interceptor.ExtractInterceptor;
import com.telyo.cvlib.interceptor.Interceptor;
import com.telyo.cvlib.interceptor.LineRectInterceptor;
import com.telyo.cvlib.interceptor.PreExtractInterceptor;
import com.telyo.cvlib.interceptor.RealInterceptorChain;

import java.util.ArrayList;
import java.util.List;

public class ExtractorImpl implements IExtractor {

    @Override
    public PointMat doExtract(PointMat mat) {
        List<Interceptor> interceptors = new ArrayList<>();
        PointMat originMat = new PointMat();
        mat.copyTo(originMat);
        originMat.setNeedPreView(mat.isNeedPreView());
        interceptors.add(new PreExtractInterceptor());
        interceptors.add(new LineRectInterceptor());
        interceptors.add(new ExtractInterceptor());
        Interceptor.Chain chain = new RealInterceptorChain(interceptors,0,mat,originMat);
        return chain.proceed(mat);
    }
}
