package com.cambricon.productdisplay.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.db.CommDB;
import com.cambricon.productdisplay.utils.StatusBarCompat;

/**
 * Created by dell on 18-2-3.
 */

public class HomeActivity extends AppCompatActivity {
    private android.support.v7.widget.Toolbar toolbar;
    /*创建一个Drawerlayout和Toolbar联动的开关*/
    private ActionBarDrawerToggle toggle;

    private DrawerLayout drawerLayout;
    private RadioGroup radioGroup;
    private LinearLayout relMenu;
    private RadioButton testbtn;
    private RadioButton databtn;
    private RadioButton newsbtn;

    private Drawable test_on;
    private Drawable test_off;
    private Drawable data_on;
    private Drawable data_off;
    private Drawable news_on;
    private Drawable news_off;

    private TestFragment testFragment;
    private DataFragment dataFragment;
    private NewsFragment newsFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    final int PERMISSION_REQUST_CODE = 0x001;

    private CommDB commDB;
    private Boolean isExit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_line));
        setContentView(R.layout.home_layout);
        initView();
        initFragment();
        initRadioBtn();
        setActionBar();
        setListener();
        setDrawerToggle();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUST_CODE);
        } else {

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUST_CODE) {

        }
    }

    private void setDrawerToggle() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.addDrawerListener(toggle);
        /*同步drawerlayout的状态*/
        toggle.syncState();
    }

    private void initView() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.mydrawer);
        radioGroup = findViewById(R.id.rel_navigate);
        testbtn=findViewById(R.id.tab_test);
        databtn=findViewById(R.id.tab_data);
        newsbtn=findViewById(R.id.tab_adv);
        commDB=new CommDB(this);
        commDB.open();

    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        testFragment = new TestFragment();
        fragmentTransaction.add(R.id.main_content, testFragment);
        fragmentTransaction.commit();
    }

    private void initRadioBtn(){
        test_on=getResources().getDrawable(R.mipmap.test_on);
        test_off=getResources().getDrawable(R.mipmap.test_off);
        data_on=getResources().getDrawable(R.mipmap.data_on);
        data_off=getResources().getDrawable(R.mipmap.data_off);
        news_on=getResources().getDrawable(R.mipmap.news_on);
        news_off=getResources().getDrawable(R.mipmap.news_off);
        test_on.setBounds(1,1,test_on.getIntrinsicWidth(),test_on.getIntrinsicHeight());
        test_off.setBounds(1,1,test_off.getIntrinsicWidth(),test_off.getIntrinsicHeight());
        data_on.setBounds(1,1,data_on.getIntrinsicWidth(),data_on.getIntrinsicHeight());
        data_off.setBounds(1,1,data_off.getIntrinsicWidth(),data_off.getIntrinsicHeight());
        news_on.setBounds(1,1,news_on.getIntrinsicWidth(),news_on.getIntrinsicHeight());
        news_off.setBounds(1,1,news_off.getIntrinsicWidth(),news_off.getIntrinsicHeight());


    }

    private void hideAll(FragmentTransaction transaction) {
        if (transaction == null) {
            return;
        }
        if (testFragment != null) {
            transaction.hide(testFragment);
        }

        if (dataFragment != null) {
            transaction.hide(dataFragment);
        }

        if (newsFragment != null) {
            transaction.hide(newsFragment);
        }
    }

    private void setListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                Log.d("huangyaling", "checkId:" + checkId);
                switch (checkId) {
                    case R.id.tab_test:
                        databtn.setCompoundDrawables(null,data_off,null,null);
                        testbtn.setCompoundDrawables(null,test_on,null,null);
                        newsbtn.setCompoundDrawables(null,news_off,null,null);
                        databtn.setTextColor(getResources().getColor(R.color.home_text_color));
                        testbtn.setTextColor(getResources().getColor(R.color.main_line));
                        newsbtn.setTextColor(getResources().getColor(R.color.home_text_color));

                        FragmentTransaction testTransaction = fragmentManager.beginTransaction();
                        hideAll(testTransaction);
                        if (testFragment == null) {
                            testFragment = new TestFragment();
                            testTransaction.add(R.id.main_content, testFragment);
                        } else {
                            testTransaction.show(testFragment);
                        }
                        testTransaction.commit();
                        break;
                    case R.id.tab_data:
                        databtn.setCompoundDrawables(null,data_on,null,null);
                        testbtn.setCompoundDrawables(null,test_off,null,null);
                        newsbtn.setCompoundDrawables(null,news_off,null,null);
                        databtn.setTextColor(getResources().getColor(R.color.main_line));
                        testbtn.setTextColor(getResources().getColor(R.color.home_text_color));
                        newsbtn.setTextColor(getResources().getColor(R.color.home_text_color));

                        FragmentTransaction dataTransaction = fragmentManager.beginTransaction();
                        hideAll(dataTransaction);
                        if (dataFragment == null) {
                            dataFragment = new DataFragment();
                            dataTransaction.add(R.id.main_content, dataFragment);
                        } else {
                            dataTransaction.show(dataFragment);
                        }
                        dataTransaction.commit();
                        break;
                    case R.id.tab_adv:
                        databtn.setCompoundDrawables(null,data_off,null,null);
                        testbtn.setCompoundDrawables(null,test_off,null,null);
                        newsbtn.setCompoundDrawables(null,news_on,null,null);
                        databtn.setTextColor(getResources().getColor(R.color.home_text_color));
                        testbtn.setTextColor(getResources().getColor(R.color.home_text_color));
                        newsbtn.setTextColor(getResources().getColor(R.color.main_line));
                        FragmentTransaction newsTransaction = fragmentManager.beginTransaction();
                        hideAll(newsTransaction);
                        if (newsFragment == null) {
                            newsFragment = new NewsFragment();
                            newsTransaction.add(R.id.main_content, newsFragment);
                        } else {
                            newsTransaction.show(newsFragment);
                        }
                        newsTransaction.commit();
                        break;
                }
            }
        });
    }

    /**
     * 设置ActionBar
     */
    private void setActionBar() {
        setSupportActionBar(toolbar);
        /*显示Home图标*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        if(commDB!=null){
            commDB.close();
        }
        super.onDestroy();
        System.exit(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                if(isExit==false){
                    isExit = true;
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    mHandler.sendEmptyMessageDelayed(0, 2000);
                }else{
                    finish();
                }
                break;
            default:
                break;
        }
        return false;
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }

    };
}
