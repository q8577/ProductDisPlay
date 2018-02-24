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
    private static byte[] stringToBytes(String s) {
        return s.getBytes(StandardCharsets.US_ASCII);
    }

    public native void setNumThreads(int numThreads);

    public native void enableLog(boolean enabled);  // currently nonfunctional

    public native int loadModel(String modelPath, String weightsPath);  // required

    private native void setMeanWithMeanFile(String meanFile);

    private native void setMeanWithMeanValues(float[] meanValues);

    public native void setScale(float scale);

    public native float[] getConfidenceScore(byte[] data, int width, int height);

    public float[] getConfidenceScore(String imgPath) {
        return getConfidenceScore(stringToBytes(imgPath), 0, 0);
    }

    public native int[] predictImage(byte[] data, int width, int height, int k);

    public int[] predictImage(String imgPath, int k) {
        return predictImage(stringToBytes(imgPath), 0, 0, k);
    }

    public int[] predictImage(String imgPath) {
        return predictImage(imgPath, 3);
    }
    //huangyaling
    public native int[] detectImage(byte[] data,int width,int height);
    public int[] detectImage(String imgPath){
        return detectImage(stringToBytes(imgPath),0,0);
    }
    //huangyaling

    public void setMean(float[] meanValues) {
        setMeanWithMeanValues(meanValues);
    }

    public void setMean(String meanFile) {
        setMeanWithMeanFile(meanFile);
    }
}
