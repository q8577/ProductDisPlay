package com.cambricon.productdisplay.caffenative;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Created by dell on 18-2-3.
 */

public class CaffeDetection {
    static {
        System.loadLibrary("detecte_jni");
    }
    private static byte[] stringToBytes(String s) {
        return s.getBytes(StandardCharsets.US_ASCII);
    }

    public native void setNumThreads(int numThreads);

    public native void enableLog(boolean enabled);  // currently nonfunctional

    public native int loadModel(String modelPath, String weightsPath);  // required

    private native void setMeanWithMeanFile(String meanFile);

    private native void setMeanWithMeanValues(float[] meanValues);

    public native void setScale(float scale);
    //huangyaling
    public native void detectImage(byte[] data,int width,int height);
    public void detectImage(String imgPath){
        detectImage(stringToBytes(imgPath),0,0);
    }
    //huangyaling

    public void setMean(float[] meanValues) {
        setMeanWithMeanValues(meanValues);
    }

    public void setMean(String meanFile) {
        setMeanWithMeanFile(meanFile);
    }
}
