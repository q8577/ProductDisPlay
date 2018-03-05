package com.cambricon.productdisplay.activity;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.caffenative.CaffeDetection;
import com.cambricon.productdisplay.utils.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 18-1-31.
 */

public class SplashActivity extends AppCompatActivity {
    private CaffeDetection caffeDetection;
    private long loadDTime;
    private TextView load_data;
    //...

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        load_data = findViewById(R.id.load_data);
        verifyPermission();
//        load();
        //逻辑需要重新做
//        loadModel();
    }

    public void verifyPermission(){
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            loadModel();
        }
    }

    private void load(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                finish();
            }
        },1500);
    }


    private void loadModel(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //
                loadDectionModel();
                //...
                //object
                forward();

            }
        }).start();

    }


    private void loadDectionModel() {
        load_data.setText(R.string.load_data_detection);
        long startTime = SystemClock.uptimeMillis();
        caffeDetection = new CaffeDetection();
        caffeDetection.setNumThreads(4);
        long start_time = System.nanoTime();
        caffeDetection.loadModel(Config.dModelProto, Config.dModelBinary);
        long end_time = System.nanoTime();
        double paste_time = (end_time-start_time)/ 1e6;

        float[] meanValues = {104, 117, 123};
        caffeDetection.setMean(Config.dModelMean);
        loadDTime = SystemClock.uptimeMillis()-startTime;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    loadModel();
                }else{
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private void forward(){
        Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
        //detect
        intent.putExtra("caffeDetection",caffeDetection);
        intent.putExtra("loadDTime",loadDTime);
        //...
        startActivity(intent);
        finish();
    }

}


























