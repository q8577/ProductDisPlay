package com.cambricon.productdisplay.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.caffenative.CaffeDetection;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by dell on 18-2-24.
 */

public class TestActivity extends AppCompatActivity {
    private CaffeDetection caffeDetection;
    private String TAG = "TestActivity";
    File sdcard = Environment.getExternalStorageDirectory();

    String modelDir = sdcard.getAbsolutePath() + "/caffe_mobile/ResNet50";
//    String modelDir = sdcard.getAbsolutePath() + "/caffe_mobile/ResNet-101";

    String modelProto = modelDir + "/test_agnostic.prototxt";

    String modelBinary = modelDir + "/resnet50_rfcn_final.caffemodel";
//    String modelBinary = modelDir + "/resnet101_rfcn_final.caffemodel";

    String modelMean = modelDir + "/imagenet_mean.binaryproto";
//    String modelMean = modelDir + "/resnet101_imagenet_mean.binaryproto";


    File imageFile = new File(modelDir, "001763.jpg");
    //    File imageFile = new File(sdcard.getAbsolutePath()+"/caffe_mobile/","cat.jpg");
    //    File imageFile = new File(sdcard.getAbsolutePath()+"/caffe_mobile/","dog2.jpg");
    //    File imageFile = new File(sdcard.getAbsolutePath()+"/caffe_mobile/re/test/","304.jpg");

    private ImageView detecte_iv;
    private ImageView detecte_result;
    private Button detecte_begin;
    private Bitmap bitmap;
    private Bitmap result;
    byte[] a;
    private int resultWidth;
    private int resultHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        init();
        Log.d(TAG, "load begin");
        caffeDetection = new CaffeDetection();
        Log.d(TAG, "setNumThreads begin");
        caffeDetection.setNumThreads(4);
        Log.d(TAG, "setNumThreads end");
        //caffeDetection.loadModel(modelPrototest, modelBinarytest);
        caffeDetection.loadModel(modelProto, modelBinary);
        Log.d(TAG, "load success");

        float[] meanValues = {104, 117, 123};
        //caffeDetection.setMean(meanValues);
        caffeDetection.setMean(modelMean);
        setListener();
    }

    private void init(){
        detecte_iv = findViewById(R.id.detecte_image);
        detecte_begin=findViewById(R.id.detecte_begin);
        detecte_result=findViewById(R.id.detecte_result);

        Button testbutton = findViewById(R.id.test);
        testbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detect();
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    detecte_iv.setImageBitmap(bytes2Bmp(a));
                    Log.d("huangyaling","bmp="+bytes2Bmp(a));
                    break;
            }
        }
    };

    /**
     * 将二进制数组转换成bitmap
     * @param data
     * @return
     */
    private Bitmap bytes2Bmp(byte[] data){
        YuvImage yuvimage=new YuvImage(data, ImageFormat.NV21, resultWidth,resultHeight, null); //20、20分别是图的宽度与高度
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0,resultWidth, resultHeight), 80, baos);//80--JPG图片的质量[0-100],100最高
        //yuvimage.compressToJpeg(new Rect(0, 0,800, 600), 80, baos);//80--JPG图片的质量[0-100],100最高
        byte[] jdata = baos.toByteArray();
        Bitmap bytes2Bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length);
        return bytes2Bmp;
    }

    private void setListener(){
        detecte_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = 1;
                        a=caffeDetection.detectImage(imageFile.getPath());
                        resultWidth=caffeDetection.getWidth();
                        resultHeight=caffeDetection.getHeight();
                        Log.d("huangyaling","resultWidth="+resultWidth+";resultHeight"+resultHeight);
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });
    }

    public void detect(){
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Log.e(TAG, "test: "+w+","+h);
        int[] pixels = new int[w*h];
        bitmap.getPixels(pixels,0,w,0,0,w,h);

        Log.e(TAG, "test: "+pixels);


        int[] resultInt = caffeDetection.grayPoc(pixels,w,h);

        Bitmap resBitmap = Bitmap.createBitmap(resultInt,w,h,bitmap.getConfig());
        detecte_result.setImageBitmap(resBitmap);

    }
}
