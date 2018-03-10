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
import android.widget.Toast;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.caffenative.CaffeDetection;
import com.cambricon.productdisplay.db.ClassificationDB;
import com.cambricon.productdisplay.db.DetectionDB;
import com.cambricon.productdisplay.task.CNNListener;
import com.cambricon.productdisplay.utils.Config;
import com.cambricon.productdisplay.utils.ConvertUtil;
import com.cambricon.productdisplay.utils.StatusBarCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
public class DetectionActivity2 extends AppCompatActivity implements View.OnClickListener, CNNListener {
    private final String TAG = "DetectionActivity";
    private Toolbar toolbar;
    private TextView testNet;
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
    CaffeDetection caffeDetection;
    private double detectionTime;
    private DetectionDB detectionDB;
    private long loadDTime;

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
        toolbar = findViewById(R.id.classification_toolbar);
        ivCaptured = findViewById(R.id.classification_img);
        testNet = findViewById(R.id.test_result);
        testTime = findViewById(R.id.test_time);
        loadCaffe = findViewById(R.id.load_caffe);
        function_text = findViewById(R.id.function_describe);
        textFps = findViewById(R.id.test_fps);
        testPro = findViewById(R.id.test_guide);
        detection_begin = findViewById(R.id.classification_begin);
        detection_end = findViewById(R.id.classification_end);

        loadCaffe.setText(getResources().getString(R.string.detection_load_model) + String.valueOf(getIntent().getSerializableExtra("loadDTime")) + "ms");
        testPro.setText("图片目标检测结果显示");
        function_text.setText("目标检测：\n\t\t\t\t通过特定的训练模型，不仅仅要识别出来是什么物体，而且还要预测物体的位置，位置用边框标记。");
        testNet.setText(R.string.decete_type);

        detection_begin.setOnClickListener(this);
        detection_end.setOnClickListener(this);

        detectionDB = new DetectionDB(getApplicationContext());
        detectionDB.open();
    }


    /**
     * 设置ActionBar
     */
    private void setActionBar() {
        String mode=Config.getIsCPUMode(DetectionActivity2.this)?getString(R.string.cpu_mode):getString(R.string.ipu_mode);
        toolbar.setTitle(getString(R.string.detection_title)+"--"+mode);
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
                testNet.setVisibility(View.VISIBLE);
                detectionDB.deleteAllClassification();
                index = 0;
                isExist = true;
                Config.isResNet101=false;
                startDetect();
                detection_begin.setVisibility(View.GONE);
                detection_end.setVisibility(View.VISIBLE);
                break;
            case R.id.classification_end:
                testPro.setText(getString(R.string.detection_pasue_guide));
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
                imageFile = new File(Config.imagePath, Config.dImageArray[index]);
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
            CaffeDetection caffeDetection = (CaffeDetection) getIntent().getSerializableExtra("Detection");
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
            detectionDB.addDetection(Config.dImageArray[index], String.valueOf((int) detectionTime), getFps(detectionTime));
            storeImage(resBitmap);
            index++;
            testTime.setText(getResources().getString(R.string.test_time) + String.valueOf(detectionTime) + "ms");
            textFps.setText(getResources().getString(R.string.test_fps) + ConvertUtil.getFps(getFps(detectionTime)) + getResources().getString(R.string.test_fps_units));
            if ((index > Config.dImageArray.length-1) && !Config.isResNet101) {
                loadResNet();
                index = 0;
                Config.isResNet101=true;
            }
            Log.e(TAG, "startIndex: " + index);
            if (index < Config.imageName.length) {
                startDetect();
                if(!Config.isResNet101){
                    testNet.setText(getString(R.string.decete_type)+"ResNet50");
                }else{
                    testNet.setText(getString(R.string.decete_type)+"ResNet101");
                }
            } else {
                Toast.makeText(this, "检测结束", Toast.LENGTH_SHORT).show();
                testPro.setText(getString(R.string.detection_end_guide));
                isExist = false;
                detection_begin.setVisibility(View.VISIBLE);
                detection_end.setVisibility(View.GONE);
            }
        } else {
            testPro.setText(getString(R.string.detection_end_guide));
        }
    }

    protected void loadResNet(){
        loadCaffe.setText(R.string.decete_type_change);
        long startTime = SystemClock.uptimeMillis();
        caffeDetection.setNumThreads(4);
        caffeDetection.loadModel(Config.dModelProto_101, Config.dModelBinary_101);
        caffeDetection.setMean(Config.dModelMean_101);
        loadDTime = SystemClock.uptimeMillis() - startTime;
        loadCaffe.setText(getResources().getString(R.string.detection_load_model)+loadDTime+"ms");
        Log.e("huangyaling","resnet101");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isExist = false;
    }

    public void storeImage(Bitmap bitmap) {
        File file = new File(Config.dImagePath, "detec-" + index + ".jpg");
        Log.e(TAG, "storeImage: " + file.exists());
        if (!file.exists()) {
            file.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



































