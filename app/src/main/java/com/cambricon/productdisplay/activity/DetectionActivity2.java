package com.cambricon.productdisplay.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.caffenative.CaffeDetection;
import com.cambricon.productdisplay.task.CNNListener;
import com.cambricon.productdisplay.utils.StatusBarCompat;

import java.io.File;

public class DetectionActivity2 extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = "DetectionActivity";
    private Toolbar toolbar;
    private TextView testResult;
    private TextView loadCaffe;
    private TextView testTime;
    private TextView function_text;
    private TextView textFps;
    private TextView testPro;
    private Button detection_begin;
    private Button detection_end;

    private ImageView ivCaptured;

    private boolean isExist = true;

    public Thread testThread;

    private Bitmap resBitmap;

    String[] imageArray = new String[]{
            "001763.jpg","cat.jpg","300.jpg","dog2.jpg"
    };

    File imageFile;
    private Bitmap bitmap;

    public static int index = 0;

    public String filePath;

    CaffeDetection caffeDetection;

    private double detectionTime;

    private final int UPDATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimary));
        setContentView(R.layout.classification_layout);
        init();
        setActionBar();

    }

    private void init(){
        caffeDetection = (CaffeDetection) getIntent().getSerializableExtra("Detection");

        filePath = Environment.getExternalStorageDirectory()+ "/caffe_mobile/ResNet50";

        toolbar = findViewById(R.id.classification_toolbar);
        ivCaptured = (ImageView) findViewById(R.id.classification_img);
        testResult = (TextView) findViewById(R.id.test_result);
        testTime=findViewById(R.id.test_time);
        loadCaffe = findViewById(R.id.load_caffe);
        function_text=findViewById(R.id.function_describe);
        textFps=findViewById(R.id.test_fps);
        testPro=findViewById(R.id.test_guide);
        detection_begin = findViewById(R.id.classification_begin);
        detection_end=findViewById(R.id.classification_end);

        testPro.setText("图片目标检测结果显示");
        function_text.setText("目标检测：\n\t\t\t\t通过特定的训练模型，不仅仅要识别出来是什么物体，而且还要预测物体的位置，位置用边框标记。");


        detection_begin.setOnClickListener(this);
        detection_end.setOnClickListener(this);

    }



    /**
     * 设置ActionBar
     */
    private void setActionBar() {
        toolbar.setTitle(R.string.Detection_toolbar_title);
        Drawable toolDrawable= ContextCompat.getDrawable(getApplicationContext(),R.drawable.toolbar_bg);
        toolDrawable.setAlpha(50);
        toolbar.setBackground(toolDrawable);
        setSupportActionBar(toolbar);
        /*显示Home图标*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.classification_begin:
                function_text.setVisibility(View.GONE);
                testPro.setText(getString(R.string.detection_begin_guide));
                testTime.setVisibility(View.VISIBLE);
                textFps.setVisibility(View.VISIBLE);
                isExist=true;
                startDetect();
                detection_begin.setVisibility(View.GONE);
                detection_end.setVisibility(View.VISIBLE);
                break;
            case R.id.classification_end:
                testPro.setText(getString(R.string.detection_pasue_guide));
                isExist=false;
                detection_begin.setVisibility(View.VISIBLE);
                detection_end.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void startDetect() {
        testThread = new Thread(new Runnable() {
            @Override
            public void run() {
                imageFile = new File(filePath, imageArray[index]);
                bitmap = BitmapFactory.decodeFile(imageFile.getPath());

                Log.e(TAG, "run: "+bitmap);
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();
                int [] pixels = new int[w*h];
                bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,w,h);
                Log.e(TAG, "run: ");
                CaffeDetection caffeDetection = (CaffeDetection) getIntent().getSerializableExtra("Detection");
                Log.e(TAG, "run: "+caffeDetection);
                int[] resultInt = caffeDetection.grayPoc(pixels,w,h);
                resBitmap = Bitmap.createBitmap(resultInt,w,h,bitmap.getConfig());

                Message msg = new Message();
                msg.what = UPDATE;
                handler.sendMessage(msg);


            }
        });

        if(isExist){
            testThread.start();
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE:
                    ivCaptured.setScaleType(ImageView.ScaleType.FIT_XY);
                    ivCaptured.setImageBitmap(resBitmap);
                    index++;
                    if(index<imageArray.length){
                        startDetect();
                    }
                    break;
                default:
                    break;
            }
        }
    };


}



































