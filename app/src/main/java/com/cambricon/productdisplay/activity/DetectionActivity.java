package com.cambricon.productdisplay.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.caffenative.CaffeDetection;

import java.io.File;

/**
 * Created by dell on 18-1-30.
 */

public class DetectionActivity extends AppCompatActivity {
    private android.support.v7.widget.Toolbar toolbar;
    private TextView tv;
    private CaffeDetection mCaffeDetection;
    final File modelFile = new File(Environment.getExternalStorageDirectory(), "resnet101_train_agnostic.prototxt");
    final File weightFile = new File(Environment.getExternalStorageDirectory(), "resnet101_rfcn_final.caffemodel");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detection_layout);
        initView();
        setToolbar();
        //initData();
    }

    public void initView() {
        toolbar = findViewById(R.id.detection_toolbar);
        tv = findViewById(R.id.load_detection_res);
    }

    public void initData() {
        long start_time = System.nanoTime();
        mCaffeDetection = new CaffeDetection();
        tv.append(" loading caffe model ....");
        boolean res = mCaffeDetection.loadModel(modelFile.getPath(), weightFile.getPath());
        long end_time = System.nanoTime();
        double difference = (end_time - start_time) / 1e6;
        tv.append(String.valueOf(difference));
    }

    /**
     * toolBar返回按钮
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置toolbar属性
     */
    public void setToolbar() {
        toolbar.setTitle(R.string.gv_text_item2);
        setSupportActionBar(toolbar);
        /*显示Home图标*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
