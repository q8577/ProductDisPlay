package com.cambricon.productdisplay.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.adapter.MoreFunctionRecylerAdaper;
import com.cambricon.productdisplay.utils.Config;

import java.util.ArrayList;

/**
 * Created by dell on 18-3-15.
 */

public class MoreFunctionsAct extends AppCompatActivity {
    private android.support.v7.widget.Toolbar toolbar;
    private ArrayList<String> mData;
    private ArrayList<Integer> mDraw;
    private RecyclerView recyclerView;
    private MoreFunctionRecylerAdaper adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_functions);
        initdata();
        initView();
        setActionBar();
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        adapter = new MoreFunctionRecylerAdaper(this, mData,mDraw);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.scheduleLayoutAnimation();
    }

    private void initdata() {
        mData = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            mData.add("item" + i);
        }
        mDraw=new ArrayList<>();
        mDraw.add(R.mipmap.logo);
        mDraw.add(R.mipmap.logo);
        mDraw.add(R.mipmap.logo);
        mDraw.add(R.mipmap.logo);
        mDraw.add(R.mipmap.logo);
        mDraw.add(R.mipmap.logo);
        mDraw.add(R.mipmap.logo);
    }

    /**
     * 设置ActionBar
     */
    private void setActionBar() {
        toolbar.setTitle(getString(R.string.more_function_title));
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

    private void initView() {
        recyclerView = findViewById(R.id.recycler_view);
        this.toolbar = findViewById(R.id.more_toolbar);
    }

}
