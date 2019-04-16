package com.telyo.cvlib.utils;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class LibUtils {
    public static void rotate90(Mat img, boolean isFrontCamera) {
        Core.rotate(img, img, isFrontCamera ? Core.ROTATE_90_COUNTERCLOCKWISE : Core.ROTATE_90_CLOCKWISE);
        if (isFrontCamera){
            Core.flip(img, img, 1);
        }
    }
}
