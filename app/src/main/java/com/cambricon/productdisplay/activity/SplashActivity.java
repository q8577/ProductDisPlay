package com.cambricon.productdisplay.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
    private final int PERMISSION = 1;
    private final int START_LOAD_DETECT = 2;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PERMISSION:
                    loadModel();
                    break;
                case START_LOAD_DETECT:
                    load_data.setText(R.string.load_data_detection);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        load_data = findViewById(R.id.load_data);
        verifyPermission();
    }

    public void verifyPermission() {
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            loadModel();
        }
    }

    private void load() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }
        }, 1500);
    }


    private void loadModel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadDectionModel();
                forward();

            }
        }).start();

    }


    private void loadDectionModel() {
        Message msg = new Message();
        msg.what = START_LOAD_DETECT;
        handler.sendMessage(msg);

        long startTime = SystemClock.uptimeMillis();
        caffeDetection = new CaffeDetection();
        caffeDetection.setNumThreads(4);
        Log.e("load", "loadDectionModel: " + Config.dModelProto);
        Log.e("load", "loadDectionModel: " + Config.dModelBinary);
        caffeDetection.loadModel(Config.dModelProto, Config.dModelBinary);
        caffeDetection.setMean(Config.dModelMean);
        loadDTime = SystemClock.uptimeMillis() - startTime;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Message msg = new Message();
                    msg.what = PERMISSION;
                    handler.sendMessage(msg);

                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private void forward() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        intent.putExtra("caffeDetection", caffeDetection);
        intent.putExtra("loadDTime", loadDTime);
        intent.putExtra("netType","ResNet50");
        startActivity(intent);
        finish();
    }

}


























