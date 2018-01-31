package com.cambricon.productdisplay.activity;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.view.ChartService;

import org.achartengine.GraphicalView;

/**
 * Created by huangyaling on 18-1-30.
 */

public class ClassificationActivity extends AppCompatActivity {
    private android.support.v7.widget.Toolbar toolbar;

    //fps折线图
    private LinearLayout chartView;
    private GraphicalView graphicalView;
    private ChartService chartService;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.classification_layout);
        initView();
        setToolbar();
        initData();
    }


    public void initView(){
        toolbar=findViewById(R.id.classification_toolbar);
        chartView=findViewById(R.id.line_chart_view);
    }

    public void initData(){
        //fps折线图
        chartService=new ChartService(this);
        chartService.setXYMultipleSeriesDataset("测试");
        chartService.setXYMultipleSeriesRenderer(60, 10, "测试", "时间:s", "fps:千张/秒",
                Color.WHITE, Color.WHITE, Color.BLUE, Color.WHITE);
        graphicalView=chartService.getGraphicalView();
        chartView.addView(graphicalView);

    }

    /**
     * toolBar返回按钮
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置toolbar属性
     */
    public void setToolbar(){
       toolbar.setTitle(R.string.gv_text_item1);
        toolbar.setDrawingCacheBackgroundColor(getResources().getColor(R.color.test_background));
        setSupportActionBar(toolbar);
        /*显示Home图标*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
