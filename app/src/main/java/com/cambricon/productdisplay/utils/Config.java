package com.cambricon.productdisplay.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by dell on 18-2-28.
 */

public class Config {
    public static final int ChartPointNum = 10;
    /**
     * classification source
     */
    public static File sdcard = Environment.getExternalStorageDirectory();
    public static String modelDir = sdcard.getAbsolutePath() + "/caffe_mobile/bvlc_reference_caffenet";
    public static String modelProto = modelDir + "/deploy.prototxt";
    public static String modelBinary = modelDir + "/bvlc_reference_caffenet.caffemodel";
    public static String[] imageName = new String[]{"test_image.jpg", "test_image2.jpg", "test_image3.jpg", "test_image4.jpg"};


}
