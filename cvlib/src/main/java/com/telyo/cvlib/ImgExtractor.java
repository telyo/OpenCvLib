package com.telyo.cvlib;

import android.content.Context;

import com.telyo.cvlib.extract.ExtractorImpl;
import com.telyo.cvlib.extract.IExtractor;
import com.telyo.cvlib.imggeter.IImageGetter;
import com.telyo.cvlib.imggeter.ImageGetterImpl;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class ImgExtractor implements ImageGetterImpl.OnMatStreamListener, IExtractor {
    private IImageGetter mImageGetter;
    private IExtractor mExtractor;
    private List<ImageGetterImpl.OnMatStreamListener> mListeners;

    private static ImgExtractor instance;

    public static ImgExtractor getInstance() {
        if (instance == null) {
            synchronized (ImgExtractor.class) {
                if (instance == null) {
                    instance = new ImgExtractor();
                }
            }
        }
        return instance;
    }

    private ImgExtractor() {
        this(new ExtractorImpl(), new ImageGetterImpl(), new ArrayList<ImageGetterImpl.OnMatStreamListener>());
    }

    private ImgExtractor(IExtractor extractor, IImageGetter imageGetter, List<ImageGetterImpl.OnMatStreamListener> listeners) {
        this.mExtractor = extractor;
        this.mImageGetter = imageGetter;
        this.mListeners = listeners;
    }

    public void openCamera(Context context) {
        mImageGetter.openCamera(context);
    }

    @Override
    public void onCancel() {
        for (ImageGetterImpl.OnMatStreamListener listener : mListeners) {
            listener.onCancel();
        }
    }

    @Override
    public void onMat(Mat img) {
        for (ImageGetterImpl.OnMatStreamListener listener : mListeners) {
            listener.onMat(img);
        }
    }

    @Override
    public PointMat doExtract(PointMat mat) {
        return mExtractor.doExtract(mat);
    }

    public IImageGetter getImageGetter() {
        return mImageGetter;
    }

    public void setmExtractor(IExtractor mExtractor) {
        this.mExtractor = mExtractor;
    }

    public IExtractor getExtractor() {
        return mExtractor;
    }

    public void addOnMatStreamListener(ImageGetterImpl.OnMatStreamListener listener) {
        mListeners.add(listener);
    }

    public void removeOnMatStreamListener(ImageGetterImpl.OnMatStreamListener listener) {
        if (mListeners.contains(listener)) {
            mListeners.remove(listener);
        }
    }


}
