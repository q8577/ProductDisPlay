package com.cambricon.productdisplay.activity;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.caffenative.CaffeMobile;
import com.cambricon.productdisplay.task.CNNListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ClassificationActivity extends AppCompatActivity implements CNNListener {
    private static final String LOG_TAG = "ClassificationActivity";
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static String[] IMAGENET_CLASSES;

    private Button test;
    private ImageView ivCaptured;
    private TextView tvLabel;
    private ProgressDialog dialog;
    private Bitmap bmp;
    private CaffeMobile caffeMobile;

    private static Boolean isTest = true;
    private Handler mHandler;
    public static int startIndex = 0;

    File sdcard = Environment.getExternalStorageDirectory();
    String modelDir = sdcard.getAbsolutePath() + "/caffe_mobile/bvlc_reference_caffenet";
    String modelProto = modelDir + "/deploy.prototxt";
    String modelBinary = modelDir + "/bvlc_reference_caffenet.caffemodel";

    String[] imageName = new String[]{"test_image.jpg", "test_image2.jpg", "test_image3.jpg", "test_image4.jpg"};
    File imageFile = new File(sdcard, imageName[0]);

    static {
        System.loadLibrary("caffe-jni");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classification_layout);

        ivCaptured = (ImageView) findViewById(R.id.classification_img);
        tvLabel = (TextView) findViewById(R.id.test_result);

        test = findViewById(R.id.classification_begin);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "test onclick");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(startIndex < imageName.length){
                            Log.d(LOG_TAG, "startIndex=" + startIndex);
                            imageFile = new File(sdcard, imageName[startIndex]);
                            bmp = BitmapFactory.decodeFile(imageFile.getPath());
                            dialog = ProgressDialog.show(ClassificationActivity.this, "Predicting...", "Wait for one sec...", true);
                            CNNTask cnnTask = new CNNTask(ClassificationActivity.this);
                            cnnTask.execute(imageFile.getPath());
                        }else {
                            return;
                        }
                    }
                }).start();
            }
        });
        // TODO: implement a splash screen(?
        caffeMobile = new CaffeMobile();
        caffeMobile.setNumThreads(4);
        caffeMobile.loadModel(modelProto, modelBinary);

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
            Log.i(LOG_TAG, String.format("elapsed wall time: %d ms", SystemClock.uptimeMillis() - startTime));
            listener.onTaskCompleted(integer);
            super.onPostExecute(integer);
        }
    }

    @Override
    public void onTaskCompleted(int result) {
        Log.d(LOG_TAG, "IMAGENET_CLASSES=" + IMAGENET_CLASSES[result]);
        ivCaptured.setImageBitmap(bmp);
        startIndex++;
        tvLabel.setText(IMAGENET_CLASSES[result]);
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
