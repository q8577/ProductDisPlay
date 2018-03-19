package com.cambricon.productdisplay.activity;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.cambricon.productdisplay.utils.Config;
import com.cambricon.productdisplay.utils.ConvertUtil;
import com.cambricon.productdisplay.utils.StatusBarCompat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ClassificationActivity extends AppCompatActivity implements CNNListener, View.OnClickListener {
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
    private Bitmap bmp;
    private CaffeMobile caffeMobile;

    public static int startIndex = 0;
    public Thread testThread;
    public static boolean isExist = true;
    private double classificationTime;
    private ClassificationDB classificationDB;

    private static float TARGET_WIDTH;
    private static float TARGET_HEIGHT;

    private final int START_LOAD_DETECT = 2;
    private final int LOED_DETECT_END = 3;

    File imageFile = new File(Config.sdcard, Config.imageName[0]);

    static {
        System.loadLibrary("caffe_jni");
    }

    private long mEnd_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimary));
        setContentView(R.layout.classification_layout);
        init();
        setActionBar();
        initSynsetWords();
    }

    private void init() {
        ivCaptured = findViewById(R.id.classification_img);
        testResult = findViewById(R.id.test_result);
        testTime = findViewById(R.id.test_time);
        loadCaffe = findViewById(R.id.load_caffe);
        function_text = findViewById(R.id.function_describe);
        textFps = findViewById(R.id.test_fps);
        testPro = findViewById(R.id.test_guide);
        classification_begin = findViewById(R.id.classification_begin);
        classification_end = findViewById(R.id.classification_end);
        this.toolbar = findViewById(R.id.classification_toolbar);

        loadCaffe.setText("");

        classification_begin.setOnClickListener(this);
        classification_end.setOnClickListener(this);

        classificationDB = new ClassificationDB(getApplicationContext());
        classificationDB.open();

        caffeMobile = new CaffeMobile();
    }

    /**
     * 设置ActionBar
     */
    private void setActionBar() {
        String mode = Config.getIsCPUMode(ClassificationActivity.this) ? getString(R.string.cpu_mode) : getString(R.string.ipu_mode);
        toolbar.setTitle(getString(R.string.gv_text_item1) + "--" + mode);
        setSupportActionBar(toolbar);
        Drawable toolDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toolbar_bg);
        toolDrawable.setAlpha(50);
        toolbar.setBackground(toolDrawable);
        /*显示Home图标*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initSynsetWords() {
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

    //加载模型
    private void load() {
        Message msg = new Message();
        msg.what = START_LOAD_DETECT;
        handler.sendMessage(msg);
        Log.e(LOG_TAG, "loadModel: start");

        long start_time = SystemClock.uptimeMillis();

        caffeMobile.setNumThreads(4);
        caffeMobile.loadModel(Config.modelProto, Config.modelBinary, Config.getIsCPUMode(ClassificationActivity.this));
        float[] meanValues = {104, 117, 123};
        caffeMobile.setMean(meanValues);
        mEnd_time = SystemClock.uptimeMillis() - start_time;

        Log.e(LOG_TAG, "loadModel: end");
        Message msg_end = new Message();
        msg_end.what = LOED_DETECT_END;
        handler.sendMessage(msg_end);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_LOAD_DETECT:
                    loadCaffe.setText(R.string.load_data_detection);
                    break;
                case LOED_DETECT_END:
                    loadCaffe.setText(getResources().getString(R.string.detection_load_model) + mEnd_time + "ms");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.classification_begin:
                function_text.setVisibility(View.GONE);
                testPro.setText(getString(R.string.classification_begin_guide));
                testResult.setVisibility(View.VISIBLE);
                testTime.setVisibility(View.VISIBLE);
                textFps.setVisibility(View.VISIBLE);
                //classificationDB.deleteAllClassification();
                startIndex = 0;
                isExist = true;
                startThread();
                classification_begin.setVisibility(View.GONE);
                classification_end.setVisibility(View.VISIBLE);
                break;
            case R.id.classification_end:
                testPro.setText(getString(R.string.classification_pasue_guide));
                isExist = false;
                classification_begin.setVisibility(View.VISIBLE);
                classification_end.setVisibility(View.GONE);
                break;
        }
    }

    public void startThread() {
        testThread = new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                load();
                executeImg();
            }
        });
        if (isExist) {
            testThread.start();
        }
    }

    private void executeImg() {
        imageFile = new File(Config.imagePath, Config.imageName[startIndex]);
        bmp = BitmapFactory.decodeFile(imageFile.getPath());
        CNNTask cnnTask = new CNNTask(ClassificationActivity.this);
        if (imageFile.exists()) {
            cnnTask.execute(imageFile.getPath());
        } else {
            Log.d(LOG_TAG, "file is not exist");
        }
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

    private String getFps(double classificationTime) {
        double fps = 60 * 1000 / classificationTime;
        Log.d(LOG_TAG, "fps:" + fps);
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
            listener.onTaskCompleted(integer);
            super.onPostExecute(integer);
        }

    }
    @Override
    public void onTaskCompleted(int result) {
        if (isExist) {
            Log.d(LOG_TAG, "IMAGENET_CLASSES=" + IMAGENET_CLASSES[result]);
            TARGET_WIDTH = ivCaptured.getWidth();
            TARGET_HEIGHT = ivCaptured.getHeight();
            ivCaptured.setImageBitmap(zoomBitmap(bmp));
            if (Config.getIsCPUMode(ClassificationActivity.this)) {
                Log.e(LOG_TAG, "CPU modeIMAGENET_CLASSES=" + IMAGENET_CLASSES[result]);
                classificationDB.addClassification(Config.imageName[startIndex], String.valueOf((int) classificationTime), getFps(classificationTime), IMAGENET_CLASSES[result]);
            } else {
                Log.e(LOG_TAG, "IPU mode IMAGENET_CLASSES=" + IMAGENET_CLASSES[result]);
                classificationDB.addIPUClassification(Config.imageName[startIndex], String.valueOf((int) classificationTime), getFps(classificationTime), IMAGENET_CLASSES[result]);
            }
            startIndex++;
            testResult.setText(getResources().getString(R.string.test_result) + IMAGENET_CLASSES[result]);
            testTime.setText(getResources().getString(R.string.test_time) + String.valueOf((int) classificationTime) + "ms");
            textFps.setText(getResources().getString(R.string.test_fps) + ConvertUtil.getFps(getFps(classificationTime)) + getResources().getString(R.string.test_fps_units));
        /*if (startIndex >= Config.imageName.length) {
            startIndex = startIndex % Config.imageName.length;
            Log.d(LOG_TAG, "startIndex=" + startIndex);
        }*/
            Log.e(LOG_TAG, "startIndex: " + startIndex);
            if (startIndex < Config.imageName.length) {
                executeImg();
            } else {
                Toast.makeText(this, "检测结束", Toast.LENGTH_SHORT).show();
                testPro.setText(getString(R.string.classification_end_guide));
                isExist = false;
                classification_begin.setVisibility(View.VISIBLE);
                classification_end.setVisibility(View.GONE);
            }
        } else {
            testPro.setText(getString(R.string.classification_end_guide));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isExist = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isExist = false;
    }
}
