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
    private CaffeDetection caffeDetection;
    private double detectionTime;
    private DetectionDB detectionDB;
    private long loadDTime;
    private final int START_LOAD_DETECT = 2;
    private final int LOED_DETECT_END = 3;
    private final int LOED_DETECT_101 = 4;
    private final int LOED_DETECT_101_END = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimary));
        setContentView(R.layout.classification_layout);
        init();
        setActionBar();

    }

    private void init() {
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

        loadCaffe.setText("");
        testNet.setText(getString(R.string.decete_type)+String.valueOf(getIntent().getSerializableExtra("netType")));
        testPro.setText("图片目标检测结果显示");
        function_text.setText("目标检测：\n\t\t\t\t通过特定的训练模型，不仅仅要识别出来是什么物体，而且还要预测物体的位置，位置用边框标记。");
        testNet.setText(R.string.decete_type);

        detection_begin.setOnClickListener(this);
        detection_end.setOnClickListener(this);

        detectionDB = new DetectionDB(getApplicationContext());
        detectionDB.open();

        caffeDetection = new CaffeDetection();
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
//                detectionDB.deleteAllClassification();
                index = 0;
                Config.isResNet101=false;
                isExist = true;

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



    public void loadModel(){
        Message msg = new Message();
        msg.what = START_LOAD_DETECT;
        handler.sendMessage(msg);
        Log.e(TAG, "loadModel: start");

        long startTime = SystemClock.uptimeMillis();

        caffeDetection.setNumThreads(4);
        Log.e(TAG, "loadModel: "+Config.getIsCPUMode(DetectionActivity2.this));
        caffeDetection.loadModel(Config.dModelProto, Config.dModelBinary,Config.getIsCPUMode(DetectionActivity2.this));
        caffeDetection.setMean(Config.dModelMean);

        loadDTime = SystemClock.uptimeMillis() - startTime;

        Config.isResNet101 = false;
        Config.isResNet50 = true;

        Log.e(TAG, "loadModel: end");
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
                    testNet.setText(getString(R.string.decete_type)+"ResNet50");
                    break;
                case LOED_DETECT_END:
                    loadCaffe.setText(getResources().getString(R.string.detection_load_model) + loadDTime + "ms");
                    break;
                default:
                    break;
            }
        }
    };

    private void startDetect() {
        testThread = new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                loadModel();
                executeImg();
            }
        });
        if (isExist) {
            testThread.start();
        }
    }

    public void executeImg(){
        imageFile = new File(Config.imagePath, Config.dImageArray[index]);
        bitmap = BitmapFactory.decodeFile(imageFile.getPath());
        CNNTask cnnTask = new CNNTask(DetectionActivity2.this);
        if (imageFile.exists()) {
            cnnTask.execute();
        } else {
            Log.e(TAG, "file is not exist");
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
            Log.e(TAG, "doInBackground: "+pixels.toString()+w+h);
            bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, w, h);
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
            String netType;
            if(index>(Config.dImageArray.length/2)-1){
                netType = "ResNet101";
            }else{
                netType = "ResNet50";
            }

            Log.e(TAG, "onTaskCompleted: "+index);
            Log.e(TAG, "onTaskCompleted: "+index+":"+netType);

            if(Config.getIsCPUMode(DetectionActivity2.this)){
                detectionDB.addDetection(Config.dImageArray[index], String.valueOf((int) detectionTime), getFps(detectionTime),netType);
            }else{
                detectionDB.addIPUClassification(Config.dImageArray[index],String.valueOf((int)detectionTime),getFps(detectionTime),netType);
            }


            storeImage(resBitmap);


            testTime.setText(getResources().getString(R.string.test_time) + String.valueOf(detectionTime) + "ms");
            textFps.setText(getResources().getString(R.string.test_fps) + ConvertUtil.getFps(getFps(detectionTime)) + getResources().getString(R.string.test_fps_units));

            if ((index > (Config.dImageArray.length/2)-1) && !Config.isResNet101) {
                loadResNet();
//                index = 0;
                /*Config.isResNet101=true;
                Config.isResNet50=false;*/
            }
            Log.e(TAG, "startIndex: " + index);
            if (index < Config.dImageArray.length-1) {
                executeImg();
            } else {
                Toast.makeText(this, "检测结束", Toast.LENGTH_SHORT).show();
                testPro.setText(getString(R.string.detection_end_guide));
                isExist = false;
                detection_begin.setVisibility(View.VISIBLE);
                detection_end.setVisibility(View.GONE);
            }
            index++;

        } else {
            testPro.setText(getString(R.string.detection_end_guide));
        }
    }

    protected void loadResNet(){
        long startTime = SystemClock.uptimeMillis();
        caffeDetection.setNumThreads(4);
        caffeDetection.loadModel(Config.dModelProto_101, Config.dModelBinary_101,Config.getIsCPUMode(DetectionActivity2.this));
        caffeDetection.setMean(Config.dModelMean_101);

        Config.isResNet50 = false;
        Config.isResNet101 = true;
        Log.e(TAG, "loadResNet: "+"转换模型");
        loadDTime = SystemClock.uptimeMillis() - startTime;
        loadCaffe.setText(getResources().getString(R.string.detection_change_model)+loadDTime+"ms");
        testNet.setText(getString(R.string.decete_type)+"ResNet101");
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

    @Override
    protected void onPause() {
        super.onPause();

    }
}



































