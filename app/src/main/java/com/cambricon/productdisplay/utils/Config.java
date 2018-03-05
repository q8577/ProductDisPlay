package com.cambricon.productdisplay.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by dell on 18-2-28.
 */

public class Config {
    public static final int ChartPointNum = 10;

    /**
     * file path
     */
    public static File sdcard = Environment.getExternalStorageDirectory();
    public static String modelPath = sdcard.getAbsolutePath() + "/caffe_mobile";

    /**
     * classification source
     */
    public static String modelDir = modelPath + "/bvlc_reference_caffenet";
    public static String modelProto = modelDir + "/deploy.prototxt";
    public static String modelBinary = modelDir + "/bvlc_reference_caffenet.caffemodel";
    public static String[] imageName = new String[]{
         "test_image.jpg", "test_image2.jpg", "test_image3.jpg", "test_image4.jpg"
    };

    /**
     * detection source
     */
    public static String dModelDir = modelPath + "/ResNet50";
    public static String dModelProto = dModelDir + "/test_agnostic.prototxt";
    public static String dModelBinary = dModelDir + "/resnet50_rfcn_final.caffemodel";
    public static String dModelMean = dModelDir + "/imagenet_mean.binaryproto";
    public static String[] dImageArray = new String[]{
         "001763.jpg", "cat.jpg", "300.jpg", "dog2.jpg"
    };

}
