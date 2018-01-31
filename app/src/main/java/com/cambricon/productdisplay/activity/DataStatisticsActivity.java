package com.cambricon.productdisplay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.cambricon.productdisplay.R;

/**
 * Created by dell on 18-1-30.
 */

public class DataStatisticsActivity extends AppCompatActivity implements View.OnClickListener{

    private android.support.v7.widget.Toolbar toolbar;
    private LinearLayout tab_test;
    private LinearLayout tab_data;
    private LinearLayout tab_adv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_statistics_layout);
        initView();
        setToolbar();
    }

    public void initView(){
        toolbar=findViewById(R.id.data_toolbar);

        tab_test = findViewById(R.id.tab_test);
        tab_data = findViewById(R.id.tab_data);
        tab_adv = findViewById(R.id.tab_adv);

        tab_adv.setOnClickListener(this);
        tab_test.setOnClickListener(this);
        tab_data.setOnClickListener(this);

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
        toolbar.setTitle(R.string.home_tab_db);
        setSupportActionBar(toolbar);
        /*显示Home图标*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_test:
                startActivity(new Intent(DataStatisticsActivity.this,MainActivity.class));
                break;
            case R.id.tab_data:
                //startActivity(new Intent(DataStatisticsActivity.this,DataStatisticsActivity.class));
                break;
            case R.id.tab_adv:
                Log.d("huangyaling","tab_adv");
                startActivity(new Intent(DataStatisticsActivity.this,AdvertiseActivity.class));
                break;
        }
    }
}
