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
    }

    @Override
    public void onClick(View view) {

    }
}
