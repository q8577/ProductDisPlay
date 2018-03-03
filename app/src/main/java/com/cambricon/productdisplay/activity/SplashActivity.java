package com.cambricon.productdisplay.activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.caffenative.CaffeDetection;

import java.io.File;

/**
 * Created by dell on 18-1-31.
 */

public class SplashActivity extends AppCompatActivity {
    private CaffeDetection caffeDetection;
    private static String[] IMAGENET_CLASSES;
    File sdcard = Environment.getExternalStorageDirectory();
    String modelDir = sdcard.getAbsolutePath()+"/caffe_mobile/ResNet50";
    String modelProto = modelDir + "/test_agnostic.prototxt";
    String modelBinary = modelDir + "/resnet50_rfcn_final.caffemodel";
    String modelMean = modelDir + "/imagenet_mean.binaryproto";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                finish();
            }
        },1500);
        //逻辑需要重新做

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                loadDectionModel();
            }
        }).start();*/
    }



    private void loadDectionModel() {
        caffeDetection = new CaffeDetection();
        caffeDetection.setNumThreads(4);
        long start_time = System.nanoTime();
        caffeDetection.loadModel(modelProto, modelBinary);
        long end_time = System.nanoTime();
        double paste_time = (end_time-start_time)/ 1e6;

        float[] meanValues = {104, 117, 123};
        caffeDetection.setMean(modelMean);
        Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
        intent.putExtra("caffeDetection",caffeDetection);
        startActivity(intent);
        finish();
    }


}
