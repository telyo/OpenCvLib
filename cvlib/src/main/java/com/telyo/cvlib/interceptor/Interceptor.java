package com.telyo.cvlib.interceptor;

import com.telyo.cvlib.PointMat;

public interface Interceptor {
    PointMat intercept(Chain chain);

    interface Chain {
        PointMat proceed(PointMat mat)throws IllegalArgumentException;
    }
}
