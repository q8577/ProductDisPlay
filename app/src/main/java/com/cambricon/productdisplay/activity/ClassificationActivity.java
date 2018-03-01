package com.cambricon.productdisplay.activity;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.caffenative.CaffeMobile;
import com.cambricon.productdisplay.db.ClassificationDB;
import com.cambricon.productdisplay.task.CNNListener;
import com.cambricon.productdisplay.utils.ConvertUtil;
import com.cambricon.productdisplay.utils.StatusBarCompat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ClassificationActivity extends AppCompatActivity implements CNNListener {
    private static final String LOG_TAG = "ClassificationActivity";
    private static String[] IMAGENET_CLASSES;

    private android.support.v7.widget.Toolbar toolbar;

    private Button classification_begin;
    private Button classification_end;
    private ImageView ivCaptured;
    private TextView testResult;
    private TextView loadCaffe;
    private TextView testTime;
    private TextView function_text;
    private TextView textFps;
    private TextView testPro;
    private ProgressDialog dialog;
    private Bitmap bmp;
    private CaffeMobile caffeMobile;

    public static int startIndex = 0;
    public Thread testThread;
    public static boolean isExist=true;
    private double classificationTime;
    private ClassificationDB classificationDB;

    private static float TARGET_WIDTH;
    private static float TARGET_HEIGHT;

    File sdcard = Environment.getExternalStorageDirectory();
    String modelDir = sdcard.getAbsolutePath() + "/caffe_mobile/bvlc_reference_caffenet";
    String modelProto = modelDir + "/deploy.prototxt";
    String modelBinary = modelDir + "/bvlc_reference_caffenet.caffemodel";

    String[] imageName = new String[]{"test_image.jpg", "test_image2.jpg", "test_image3.jpg", "test_image4.jpg"};
    File imageFile = new File(sdcard, imageName[0]);

    static {
        System.loadLibrary("caffe_jni");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimary));
        setContentView(R.layout.classification_layout);
        init();
        setActionBar();
        classification_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                function_text.setVisibility(View.GONE);
                testPro.setText(getString(R.string.classification_begin_guide));
                testResult.setVisibility(View.VISIBLE);
                testTime.setVisibility(View.VISIBLE);
                textFps.setVisibility(View.VISIBLE);
                isExist=true;
                startThread();
                classification_begin.setVisibility(View.GONE);
                classification_end.setVisibility(View.VISIBLE);
            }
        });

        classification_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testPro.setText(getString(R.string.classification_pasue_guide));
                isExist=false;
                classification_begin.setVisibility(View.VISIBLE);
                classification_end.setVisibility(View.GONE);
            }
        });
        // TODO: implement a splash screen(?
        caffeMobile = new CaffeMobile();
        caffeMobile.setNumThreads(4);
        long start_time = System.nanoTime();
        caffeMobile.loadModel(modelProto, modelBinary);
        long end_time = System.nanoTime();
        loadCaffe.append(String.valueOf((end_time - start_time) / 1e6));
        loadCaffe.setText(getResources().getString(R.string.load_model)+String.valueOf((end_time - start_time) / 1e6)+"ms");

        float[] meanValues = {104, 117, 123};
        caffeMobile.setMean(meanValues);

        AssetManager am = this.getAssets();
        try {
            InputStream is = am.open("synset_words.txt");
            Scanner sc = new Scanner(is);
            List<String> lines = new ArrayList<String>();
            while (sc.hasNextLine()) {
                final String temp = sc.nextLine();
                lines.add(temp.substring(temp.indexOf(" ") + 1));
            }
            IMAGENET_CLASSES = lines.toArray(new String[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isExist=false;
    }

    public void startThread() {
        testThread = new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                imageFile = new File(sdcard, imageName[startIndex]);
                bmp = BitmapFactory.decodeFile(imageFile.getPath());
                CNNTask cnnTask = new CNNTask(ClassificationActivity.this);
                if (imageFile.exists()) {
                    cnnTask.execute(imageFile.getPath());
                } else {
                    Toast.makeText(ClassificationActivity.this, "image File is not exists", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(isExist){
            testThread.start();
        }
    }

    private void init() {
        ivCaptured = (ImageView) findViewById(R.id.classification_img);
        testResult = (TextView) findViewById(R.id.test_result);
        testTime=findViewById(R.id.test_time);
        loadCaffe = findViewById(R.id.load_caffe);
        function_text=findViewById(R.id.function_describe);
        textFps=findViewById(R.id.test_fps);
        testPro=findViewById(R.id.test_guide);
        classification_begin = findViewById(R.id.classification_begin);
        classification_end=findViewById(R.id.classification_end);
        this.toolbar = (Toolbar) findViewById(R.id.classification_toolbar);
        classificationDB=new ClassificationDB(getApplicationContext());
        classificationDB.open();
    }

    /**
     * 设置ActionBar
     */
    private void setActionBar() {
        setSupportActionBar(toolbar);
        Drawable toolDrawable= ContextCompat.getDrawable(getApplicationContext(),R.drawable.toolbar_bg);
        toolDrawable.setAlpha(50);
        toolbar.setBackground(toolDrawable);
        /*显示Home图标*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * @param target
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap target) {
        int width = target.getWidth();
        int height = target.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) TARGET_WIDTH) / width;
        float scaleHeight = ((float) TARGET_HEIGHT) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap result = Bitmap.createBitmap(target, 0, 0, width,
                height, matrix, true);
        return result;
    }

    private String getFps(double classificationTime){
        double fps=60*1000/classificationTime;
        Log.d(LOG_TAG,"fps:"+fps);
        return String.valueOf(fps);
    }

    private class CNNTask extends AsyncTask<String, Void, Integer> {
        private CNNListener listener;
        private long startTime;

        public CNNTask(CNNListener listener) {
            this.listener = listener;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            startTime = SystemClock.uptimeMillis();
            return caffeMobile.predictImage(strings[0])[0];
        }

        @Override
        protected void onPostExecute(Integer integer) {
            classificationTime = SystemClock.uptimeMillis() - startTime;
            testTime.setText(getResources().getString(R.string.test_time)+String.valueOf(classificationTime)+"ms");
            listener.onTaskCompleted(integer);
            super.onPostExecute(integer);
        }
    }

    @Override
    public void onTaskCompleted(int result) {
        Log.d(LOG_TAG, "IMAGENET_CLASSES=" + IMAGENET_CLASSES[result]);
        TARGET_WIDTH = ivCaptured.getWidth();
        TARGET_HEIGHT = ivCaptured.getHeight();
        ivCaptured.setImageBitmap(zoomBitmap(bmp));
        classificationDB.addClassification(imageName[startIndex],String.valueOf(classificationTime),getFps(classificationTime),IMAGENET_CLASSES[result]);
        startIndex++;
        testResult.setText(getResources().getString(R.string.test_result)+IMAGENET_CLASSES[result]);
        textFps.setText(getResources().getString(R.string.test_fps)+ ConvertUtil.getFps(getFps(classificationTime))+getResources().getString(R.string.test_fps_units));
        if (startIndex >=imageName.length) {
            startIndex = startIndex % imageName.length;
            Log.d(LOG_TAG, "startIndex=" + startIndex);
        }
        startThread();
    }
}
