package com.cambricon.productdisplay.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.caffenative.CaffeDetection;
import com.cambricon.productdisplay.caffenative.CaffeMobile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class showActivity extends AppCompatActivity {
    private CaffeDetection caffeDetection;
    private static String[] IMAGENET_CLASSES;
    File sdcard = Environment.getExternalStorageDirectory();
    String modelDir = sdcard.getAbsolutePath()+"/caffe_mobile/ResNet50";
    String modelProto = modelDir + "/test_agnostic.prototxt";
    String modelBinary = modelDir + "/resnet50_rfcn_final.caffemodel";
    String modelMean = modelDir + "/imagenet_mean.binaryproto";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadModel();
            }
        }).start();
    }

    private void loadModel() {
        caffeDetection = new CaffeDetection();
        caffeDetection.setNumThreads(4);
        long start_time = System.nanoTime();
        caffeDetection.loadModel(modelProto, modelBinary);
        long end_time = System.nanoTime();
        double paste_time = (end_time-start_time)/ 1e6;

        float[] meanValues = {104, 117, 123};
        caffeDetection.setMean(modelMean);

        startActivity(new Intent(showActivity.this,DetectionActivity2.class));
        finish();
    }


}

























