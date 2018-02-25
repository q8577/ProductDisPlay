package com.cambricon.productdisplay.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.caffenative.CaffeDetection;

import java.io.File;

/**
 * Created by dell on 18-2-24.
 */

public class TestActivity extends AppCompatActivity {
    private CaffeDetection caffeDetection;
    private String TAG = "TestActivity";
    File sdcard = Environment.getExternalStorageDirectory();
    String modelDirtest = sdcard.getAbsolutePath() + "/caffe_mobile/bvlc_reference_caffenet";
    String modelPrototest = modelDirtest + "/deploy.prototxt";
    String modelBinarytest = modelDirtest + "/bvlc_reference_caffenet.caffemodel";

    String modelDir = sdcard.getAbsolutePath() + "/caffe_mobile/ResNet50";
    String modelProto = modelDir + "/test_agnostic.prototxt";
    String modelBinary = modelDir + "/resnet50_rfcn_final.caffemodel";

    File imageFile = new File(modelDirtest, "001763.jpg");

    private ImageView detecte_iv;
    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        detecte_iv = findViewById(R.id.detecte_image);
        Log.d(TAG, "load begin");
        caffeDetection = new CaffeDetection();
        Log.d(TAG, "setNumThreads begin");
        caffeDetection.setNumThreads(4);
        Log.d(TAG, "setNumThreads end");
        caffeDetection.loadModel(modelPrototest, modelBinarytest);
        //caffeDetection.loadModel(modelProto, modelBinary);
        Log.d(TAG, "load success");

        float[] meanValues = {104, 117, 123};
        caffeDetection.setMean(meanValues);
        Log.d(TAG, "set mean success");
        //caffeDetection.detectImage(imageFile.getPath());
        Log.d(TAG, "detectImage success");
    }
}
