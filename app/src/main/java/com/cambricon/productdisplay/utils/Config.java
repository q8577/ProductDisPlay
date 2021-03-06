package com.cambricon.productdisplay.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.cambricon.productdisplay.R;

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
    public static String imagePath = modelPath + "/re/test";
    public static String[] imageName = new String[]{
            "300.jpg", "400.jpg", "500.jpg", "600.jpg", "700.jpg",
            "301.jpg", "401.jpg", "501.jpg", "601.jpg", "701.jpg",
            "302.jpg", "402.jpg", "502.jpg", "602.jpg", "702.jpg",
            "303.jpg", "403.jpg", "503.jpg", "603.jpg", "703.jpg",
            "304.jpg", "404.jpg", "504.jpg", "604.jpg", "704.jpg",
            "300.jpg", "400.jpg", "500.jpg", "600.jpg", "700.jpg",
            "301.jpg", "401.jpg", "501.jpg", "601.jpg", "701.jpg",
            "302.jpg", "402.jpg", "502.jpg", "602.jpg", "702.jpg",
            "303.jpg", "403.jpg", "503.jpg", "603.jpg", "703.jpg",
            "304.jpg", "404.jpg", "504.jpg", "604.jpg", "704.jpg",
            "300.jpg", "400.jpg", "500.jpg", "600.jpg", "700.jpg",
            "301.jpg", "401.jpg", "501.jpg", "601.jpg", "701.jpg",
            "302.jpg", "402.jpg", "502.jpg", "602.jpg", "702.jpg",
            "303.jpg", "403.jpg", "503.jpg", "603.jpg", "703.jpg",
            "304.jpg", "404.jpg", "504.jpg", "604.jpg", "704.jpg",
            "300.jpg", "400.jpg", "500.jpg", "600.jpg", "700.jpg",
            "301.jpg", "401.jpg", "501.jpg", "601.jpg", "701.jpg",
            "302.jpg", "402.jpg", "502.jpg", "602.jpg", "702.jpg",
            "303.jpg", "403.jpg", "503.jpg", "603.jpg", "703.jpg",
            "304.jpg", "404.jpg", "504.jpg", "604.jpg", "704.jpg",
    };


    /**
     * detection source-ResNet50
     */
    public static boolean isResNet50 = false;
    public static boolean isResNet101 = false;

    public static String dModelDir = modelPath + "/ResNet50";
    public static String dModelProto = dModelDir + "/test_agnostic.prototxt";
    public static String dModelBinary = dModelDir + "/resnet50_rfcn_final.caffemodel";
    public static String dModelMean = dModelDir + "/imagenet_mean.binaryproto";


    public static String dImagePath = modelPath + "/re/detec";
    public static String dModelDir_101 = modelPath + "/ResNet101";
    public static String dModelProto_101 = dModelDir_101 + "/test_agnostic.prototxt";
    public static String dModelBinary_101 = dModelDir_101 + "/resnet101_rfcn_final.caffemodel";
    public static String dModelMean_101 = dModelDir_101 + "/resnet101_imagenet_mean.binaryproto";

    public static String[] dImageArray = new String[]{
            "300.jpg", "301.jpg", "302.jpg", "303.jpg", "304.jpg",
            "305.jpg", "306.jpg", "307.jpg", "308.jpg", "309.jpg",
            "310.jpg", "311.jpg", "312.jpg", "313.jpg", "314.jpg",
            "315.jpg", "316.jpg", "317.jpg", "318.jpg", "319.jpg",
    };


    public static boolean getIsCPUMode(Context context) {
        SharedPreferences mSharedPreferences = context.getSharedPreferences("Cambricon_mode", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Config.isCPUMode = mSharedPreferences.getBoolean(String.valueOf(R.string.cpu_mode), true);
        Log.w("huangyaling", "isCPUMode=" + Config.isCPUMode);
        return isCPUMode;
    }

    /**
     * test mode
     */
    public static boolean isCPUMode = true;

    /**
     * Face Detector source
     */
    public static String faceDetectDir = Config.modelPath + "/face_detector";
    public static String faceModelDir = faceDetectDir + "/model";
    public static String faceImgDir = faceDetectDir + "/img";
    public static String faceDetectedImgDir = faceDetectDir + "/detected";

    public static String[] faceModelArray = new String[]{
            "det1.caffemodel", "det1.prototxt", "det2.caffemodel", "det2.prototxt",
            "det3.caffemodel", "det3.prototxt", "det4.caffemodel", "det4.prototxt"
    };

    public static String[] faceImgArray = new String[]{
            "test0.jpg", "test1.jpg", "test2.jpg", "test3.jpg", "test4.jpg",
            "test5.jpg", "test6.jpg", "test7.jpg", "test8.jpg", "test9.jpg",
            "test10.jpg", "test11.jpg", "test12.jpg", "test13.jpg", "test14.jpg",
            "test15.jpg", "test16.jpg", "test17.jpg", "test18.jpg", "test19.jpg", "test20.jpg"
    };


    public static long loadClassifyTime = 0;
    public static long loadDetecteTime = 0;

}
