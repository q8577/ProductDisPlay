package com.cambricon.productdisplay.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.caffenative.FaceDetection;
import com.cambricon.productdisplay.task.CNNListener;
import com.cambricon.productdisplay.utils.Config;

import java.io.File;

/**
 * Created by cambricon on 18-3-12.
 */

public class FaceDetectorActivity extends AppCompatActivity implements View.OnClickListener,
        CNNListener {


    private static final String TAG = FaceDetection.class.getSimpleName();

    private Button mBtn_face_detector_begin;
    private Button mBtn_face_detector_end;
    private ImageView mIv_face_detector;
    private Toolbar mToolbar;
    private TextView mTv_face_detect_time;
    private TextView mTv_face_detect_guide;
    private TextView mTv_face_detector_function;

    private double detectionTime;

    public Thread mTestThread;
    private boolean isExist = true;
    private File mImgFile;
    private Bitmap mBitmap;
    private Bitmap mResultFace;
    private TextView mTv_face_detect_load_time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detector);
        initUI();
        setActionBar();
    }

    /**
     * 装载UI
     */

    private void initUI() {
        mToolbar = findViewById(R.id.toolbar_face_detector);
        mIv_face_detector = findViewById(R.id.iv_face_detector_img);
        mTv_face_detect_guide = findViewById(R.id.tv_guide_face_detector);
        mTv_face_detect_time = findViewById(R.id.tv_face_detector_time);
        mTv_face_detector_function = findViewById(R.id.tv_face_detector_function_describe);
        mTv_face_detect_load_time = findViewById(R.id.tv_load_face_detector);
        mBtn_face_detector_begin = findViewById(R.id.btn_face_detector_begin);
        mBtn_face_detector_end = findViewById(R.id.btn_face_detector_end);

        mTv_face_detect_load_time.setText(getResources().getString(R.string.detection_load_model) +
                String.valueOf(getIntent().getSerializableExtra("loadDTime")) + "ms");
        mTv_face_detect_guide.setText("图片目标检测结果显示");
        mTv_face_detector_function.setText(R.string.face_detector_function);

        mBtn_face_detector_begin.setOnClickListener(this);
        mBtn_face_detector_end.setOnClickListener(this);
    }

    /**
     * 设置ActionBar样式
     */
    private void setActionBar() {
        mToolbar.setTitle(R.string.face_detection_title_cpu);
        Drawable toolDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toolbar_bg);
        toolDrawable.setAlpha(50);
        mToolbar.setBackground(toolDrawable);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_face_detector_begin:
                mTv_face_detector_function.setVisibility(View.GONE);
                mTv_face_detect_guide.setText(getString(R.string.face_detection_begin_guide));
                mTv_face_detect_time.setVisibility(View.VISIBLE);
                isExist = true;
                startFaceDetector();
                mBtn_face_detector_begin.setVisibility(View.GONE);
                mBtn_face_detector_end.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_face_detector_end:
                mTv_face_detect_guide.setText(getString(R.string.face_detection_pasue_guide));
                isExist = false;
                mBtn_face_detector_begin.setVisibility(View.VISIBLE);
                mBtn_face_detector_end.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    /**
     * 启动人脸检测
     */
    private void startFaceDetector() {
        mTestThread = new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                mImgFile = new File(Config.faceModelImg);
                mBitmap = BitmapFactory.decodeFile(mImgFile.getPath());
                MTCNNTask mtcnnTask = new MTCNNTask(FaceDetectorActivity.this);
                if (mImgFile.exists()) {
                    mtcnnTask.execute();
                } else {
                    Log.e(TAG, "File is not exist");
                }
            }
        });

        if (isExist) {
            mTestThread.start();
        }
    }

    private class MTCNNTask extends AsyncTask<Void, Void, Void> {

        private CNNListener listener;
        private long startTime;

        public MTCNNTask(CNNListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            startTime = SystemClock.uptimeMillis();
            int w = mBitmap.getWidth();//图像宽度
            int h = mBitmap.getHeight();//图像高度
            int[] pix = new int[w * h];//设置一个int数组用来存放Bitmap图像从而传入JNI
            mBitmap.getPixels(pix, 0, w, 0, 0, w, h);
            Log.e(TAG, "start detect in JNI");
            int[] result = FaceDetection.doFaceDetector(Config.faceModelDir, w, h, pix);
            mResultFace = Bitmap.createBitmap(w, h, mBitmap.getConfig());
            mResultFace.setPixels(result, 0, w, 0, 0, w, h);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            detectionTime = SystemClock.uptimeMillis() - startTime;
            listener.onTaskCompleted(0);
            super.onPostExecute(aVoid);
        }
    }

    @Override
    public void onTaskCompleted(int result) {
        if (isExist) {
            mIv_face_detector.setScaleType(ImageView.ScaleType.FIT_XY);
            mIv_face_detector.setImageBitmap(mResultFace);
            mTv_face_detect_time.setText(getResources().getString(R.string.test_time) +
                    String.valueOf(detectionTime) + "ms");
            Toast.makeText(this, "检测结束", Toast.LENGTH_LONG).show();
            mTv_face_detect_guide.setText(getString(R.string.face_detection_end_guide));
            isExist = false;
            mBtn_face_detector_begin.setVisibility(View.VISIBLE);
            mBtn_face_detector_end.setVisibility(View.GONE);
        }
    }

    private String getFps(double classificationTime) {
        double fps = 60 * 1000 / classificationTime;
        Log.d(TAG, "fps:" + fps);
        return String.valueOf(fps);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isExist = false;
    }
}
