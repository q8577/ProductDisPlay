package com.cambricon.productdisplay.activity;

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

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.caffenative.CaffeDetection;
import com.cambricon.productdisplay.db.ClassificationDB;
import com.cambricon.productdisplay.db.DetectionDB;
import com.cambricon.productdisplay.task.CNNListener;
import com.cambricon.productdisplay.utils.Config;
import com.cambricon.productdisplay.utils.ConvertUtil;
import com.cambricon.productdisplay.utils.StatusBarCompat;

import java.io.File;

/**
 *
 */
public class DetectionActivity2 extends AppCompatActivity implements View.OnClickListener, CNNListener {
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

    private double detectTime;

    private ImageView ivCaptured;

    private boolean isExist = true;

    public Thread testThread;

    private Bitmap resBitmap;

    File imageFile;
    private Bitmap bitmap;

    public static int index = 0;

    public String filePath;

    CaffeDetection caffeDetection;

    private double detectionTime;

    private DetectionDB detectionDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimary));
        setContentView(R.layout.classification_layout);
        init();
        setActionBar();

    }

    private void init() {


        caffeDetection = (CaffeDetection) getIntent().getSerializableExtra("Detection");

        filePath = Environment.getExternalStorageDirectory() + "/caffe_mobile/ResNet50";

        toolbar = findViewById(R.id.classification_toolbar);
        ivCaptured = (ImageView) findViewById(R.id.classification_img);
        testResult = (TextView) findViewById(R.id.test_result);
        testTime = findViewById(R.id.test_time);
        loadCaffe = findViewById(R.id.load_caffe);
        function_text = findViewById(R.id.function_describe);
        textFps = findViewById(R.id.test_fps);
        testPro = findViewById(R.id.test_guide);
        detection_begin = findViewById(R.id.classification_begin);
        detection_end = findViewById(R.id.classification_end);

//        testTime.setText(R.string.detection_load_model+String.valueOf(getIntent().getSerializableExtra("loadDTime")));
        loadCaffe.setText(getResources().getString(R.string.detection_load_model)+String.valueOf(getIntent().getSerializableExtra("loadDTime"))+"ms");

        testPro.setText("图片目标检测结果显示");
        function_text.setText("目标检测：\n\t\t\t\t通过特定的训练模型，不仅仅要识别出来是什么物体，而且还要预测物体的位置，位置用边框标记。");


        detection_begin.setOnClickListener(this);
        detection_end.setOnClickListener(this);

        detectionDB = new DetectionDB(getApplicationContext());
        detectionDB.open();
//        detectionDB.deleteTable();

       /* detectionDB.addDetection(Config.dImageArray[0], "3", "3");
        detectionDB.addDetection(Config.dImageArray[1], "3", "3");
        detectionDB.addDetection(Config.dImageArray[2], "3", "3");
        detectionDB.addDetection(Config.dImageArray[3], "3", "3");*/

    }


    /**
     * 设置ActionBar
     */
    private void setActionBar() {
        toolbar.setTitle(R.string.detection_title_cpu);
        Drawable toolDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toolbar_bg);
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
        switch (v.getId()) {
            case R.id.classification_begin:
                function_text.setVisibility(View.GONE);
                testPro.setText(getString(R.string.detection_begin_guide));
                testTime.setVisibility(View.VISIBLE);
                textFps.setVisibility(View.VISIBLE);
                isExist = true;
                startDetect();
                detection_begin.setVisibility(View.GONE);
                detection_end.setVisibility(View.VISIBLE);
                break;
            case R.id.classification_end:
                testPro.setText(getString(R.string.detection_pasue_guide));
               /* try {
                    testThread.sleep(1000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                isExist = false;
                detection_begin.setVisibility(View.VISIBLE);
                detection_end.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void startDetect() {
        /**
         * 线程
         */
        testThread = new Thread(new Runnable() {
            @Override
            public synchronized void run() {

                imageFile = new File(filePath, Config.dImageArray[index]);
                bitmap = BitmapFactory.decodeFile(imageFile.getPath());
                CNNTask cnnTask = new CNNTask(DetectionActivity2.this);
                if (imageFile.exists()) {
                    cnnTask.execute();
                } else {
                    Log.e(TAG, "file is not exist");
                }

            }
        });

        if (isExist) {
            testThread.start();
        }
    }


    private class CNNTask extends AsyncTask<Void, Void, Void> {
        private CNNListener listener;
        private long startTime;

        public CNNTask(CNNListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            startTime = SystemClock.uptimeMillis();
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            int[] pixels = new int[w * h];
            bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, w, h);
            Log.e(TAG, "run: ");
            CaffeDetection caffeDetection = (CaffeDetection) getIntent().getSerializableExtra("Detection");
            Log.e(TAG, "run: " + caffeDetection);
            int[] resultInt = caffeDetection.grayPoc(pixels, w, h);
            resBitmap = Bitmap.createBitmap(resultInt, w, h, bitmap.getConfig());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            detectionTime = SystemClock.uptimeMillis() - startTime;
            listener.onTaskCompleted(0);
            super.onPostExecute(aVoid);
        }

    }

    private String getFps(double classificationTime) {
        double fps = 60 * 1000 / classificationTime;
        Log.d(TAG, "fps:" + fps);
        return String.valueOf(fps);
    }

    @Override
    public void onTaskCompleted(int result) {
        if (isExist) {
            ivCaptured.setScaleType(ImageView.ScaleType.FIT_XY);
            ivCaptured.setImageBitmap(resBitmap);
            detectionDB.addDetection(Config.dImageArray[index], String.valueOf((int)detectionTime), getFps(detectionTime));
            index++;
            testTime.setText(getResources().getString(R.string.test_time) + String.valueOf(detectionTime) + "ms");
            textFps.setText(getResources().getString(R.string.test_fps) + ConvertUtil.getFps(getFps(detectionTime)) + getResources().getString(R.string.test_fps_units));
            if (index >= Config.dImageArray.length) {
                index = index % Config.dImageArray.length;
            }
            startDetect();
        }else{
            testPro.setText(getString(R.string.detection_end_guide));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
        /**
         * 结束线程
         *
         */
    }
}



































