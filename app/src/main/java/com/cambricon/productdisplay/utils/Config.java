package com.cambricon.productdisplay.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

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
    public static String imagePath = modelPath+"/re/test";
    public static String[] imageName = new String[]{
            "300.jpg","400.jpg","500.jpg","600.jpg","700.jpg",
            "301.jpg","401.jpg","501.jpg","601.jpg","701.jpg",
//            "302.jpg","402.jpg","502.jpg","602.jpg","702.jpg",
//            "303.jpg","403.jpg","503.jpg","603.jpg","703.jpg",
//            "304.jpg","404.jpg","504.jpg","604.jpg","704.jpg",
    };


    /**
     * detection source-ResNet50
     */
    public static String dModelDir = modelPath + "/ResNet50";
    public static String dModelProto = dModelDir + "/test_agnostic.prototxt";
    public static String dModelBinary = dModelDir + "/resnet50_rfcn_final.caffemodel";
    public static String dModelMean = dModelDir + "/imagenet_mean.binaryproto";
    public static String dImagePath = modelPath+"/re/detec";


    public static String dModelDir_101 = modelPath + "/ResNet101";
    public static String dModelProto_101 = dModelDir_101 + "/test_agnostic.prototxt";
    public static String dModelBinary_101 = dModelDir_101 + "/resnet101_rfcn_final.caffemodel";
    public static String dModelMean_101 = dModelDir_101 + "/resnet101_imagenet_mean.binaryproto";

    public static boolean isResNet101=false;
    public static boolean isResNet50=false;


    public static String[] dImageArray = new String[]{
            "300.jpg","301.jpg","302.jpg","303.jpg","304.jpg",
            "305.jpg","306.jpg","307.jpg","308.jpg","309.jpg",
            /*"310.jpg","311.jpg","312.jpg","313.jpg","314.jpg",
            "315.jpg","316.jpg","317.jpg","318.jpg","319.jpg",*/
    };

    public static boolean getIsCPUMode(Context context) {
        SharedPreferences mSharedPreferences= context.getSharedPreferences("Cambricon_mode", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Config.isCPUMode=mSharedPreferences.getBoolean("CPU_mode", true);
        Log.d("huangyaling","isCPUMode="+Config.isCPUMode);
        return isCPUMode;
    }

    /**
     * test mode
     */
    public static boolean isCPUMode=true;

}
