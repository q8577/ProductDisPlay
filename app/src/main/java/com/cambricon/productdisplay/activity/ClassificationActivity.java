package com.cambricon.productdisplay.activity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.caffenative.CaffeClassification;
import com.cambricon.productdisplay.db.ClassificationDB;
import com.cambricon.productdisplay.view.ChartService;

import org.achartengine.GraphicalView;

import java.io.File;
import java.util.Arrays;
import java.util.Timer;

import static com.cambricon.productdisplay.db.DetectionDB.LOG_TAG;

/**
 * Created by huangyaling on 18-1-30.
 */




public class ClassificationActivity extends AppCompatActivity {
    private final int CLASSIFICATION_NUM=4;
    private android.support.v7.widget.Toolbar toolbar;
    private ImageView classification_img;
    private TextView load_caffe;
    private Button btn_begin;
    private Button btn_end;
    private Boolean isTest=true;
    private TextView test_result;
    private Boolean isLoadRes;


    //fps折线图
    private LinearLayout chartView;
    private GraphicalView graphicalView;
    private ChartService chartService;

    private CaffeClassification caffeClassification;
    private File sdCard= Environment.getExternalStorageDirectory();

    String[] imageName=new String[]{"test_image.jpg","test_image2.jpg","test_image3.jpg","test_image4.jpg"};
    File imageFile=new File(sdCard,imageName[0]);
    final File modelFile = new File(Environment.getExternalStorageDirectory(), "net.protobin");
    final File weightFile = new File(Environment.getExternalStorageDirectory(), "weight.caffemodel");
    final int REQUST_CODE=001;

    private Handler myHandler;
    private Bitmap bmp;

    private int temp=0;
    private Timer timer;

    private ClassificationDB classificationDB;
    protected class Cate implements Comparable<Cate> {
        public final int    idx;
        public final float  prob;

        public Cate(int idx, float prob) {
            this.idx = idx;
            this.prob = prob;
        }

        @Override
        public int compareTo(Cate other) {
            // need descending sort order
            return Float.compare(other.prob, this.prob);
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.classification_layout);
        initView();
        setToolbar();
        initData();
        setListener();
        myHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //load_caffe.append((String)msg.obj);
                test_result.setText((String)msg.obj);
                classification_img.setImageBitmap(bmp);
               // chartService.updateChart(temp,Math.random() * 10);
               // temp++;

            }
        };
    }


    public void initView(){
        toolbar=findViewById(R.id.classification_toolbar);
        chartView=findViewById(R.id.line_chart_view);
        classification_img=findViewById(R.id.classification_img);
        load_caffe=findViewById(R.id.load_caffe);
        btn_begin=findViewById(R.id.classification_begin);
        btn_end=findViewById(R.id.classification_end);
        test_result=findViewById(R.id.test_result);
    }

    public void initData(){
        //fps折线图
        chartService=new ChartService(this);
        chartService.setXYMultipleSeriesDataset("testing");
        chartService.setXYMultipleSeriesRenderer(60, 10, "testing", "时间:s", "fps:千张/秒",
                Color.WHITE, Color.WHITE, Color.BLUE, Color.WHITE);
        graphicalView=chartService.getGraphicalView();
        chartView.addView(graphicalView);

        long start_time = System.nanoTime();
        caffeClassification=new CaffeClassification();
        load_caffe.append("Loading caffe model...");
        load_caffe.setMovementMethod(new ScrollingMovementMethod());
        isLoadRes = caffeClassification.loadModel(modelFile.getPath(), weightFile.getPath());
        long end_time = System.nanoTime();
        double difference = (end_time - start_time)/1e6;
        Log.d(LOG_TAG, "onCreate: loadmodel:" + isLoadRes);
        load_caffe.append(String.valueOf(difference) + "ms\n");
        bmp = BitmapFactory.decodeFile(imageFile.getPath());
        classification_img.setImageBitmap(bmp);

        classificationDB=new ClassificationDB(this);
        classificationDB.open();

    }


    public void setListener(){
        btn_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isLoadRes){
                    Toast.makeText(ClassificationActivity.this,getString(R.string.classificaton_res_toast),Toast.LENGTH_SHORT).show();
                    return;
                }
                btn_begin.setVisibility(View.GONE);
                btn_end.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(LOG_TAG,"isTest="+isTest);
                        while(isTest){
                            if(CLASSIFICATION_NUM!=imageName.length){
                                return;
                            }
                            for(int j=0;j<CLASSIFICATION_NUM;j++){
                                if(!isTest){
                                    return;
                                }
                                Message msg = myHandler.obtainMessage();
                                imageFile=new File(sdCard,imageName[j]);
                                bmp = BitmapFactory.decodeFile(imageFile.getPath());
                                long start_time = System.nanoTime();
                                float mean[] = {81.3f, 107.3f, 105.3f};
                                float[] result = caffeClassification.predictImage(imageFile.getPath(), mean);
                                long end_time = System.nanoTime();
                                if (null != result) {
                                    double difference = (end_time - start_time) / 1e6;
                                    // Top 10
                                    int topN = 10;
                                    Cate[] cates = new Cate[result.length];
                                    for (int i = 0; i < result.length; i++) {
                                        cates[i] = new Cate(i, result[i]);
                                    }
                                    Arrays.sort(cates);
                                    msg.obj = "Top" + topN + " Results (" + String.valueOf(difference) + "ms):\n";
                                    for (int i = 0; i < topN; i++) {
                                        msg.obj += "output[" + cates[i].idx + "]\t=" + cates[i].prob + "\n";
                                    }
                                    String fps=getFps(difference);
                                    classificationDB.addClassification(imageName[j],String.valueOf(difference),fps,(String)msg.obj);
                                } else {
                                    msg.obj = "output=null (some error happens)";
                                }
                                myHandler.sendMessage(msg);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }).start();
            }
        });

        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_begin.setVisibility(View.VISIBLE);
                btn_end.setVisibility(View.GONE);
                isTest=false;
                if(timer!=null){
                    timer.cancel();
                }
            }
        });
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

    /**
     * 计fps值取整
     * @param time
     * @return
     */
    public String getFps(double time){
        double resultFps=1000/time;

        String[] args=String.valueOf(resultFps).split(".");
        Log.d("huangyaling","time="+time+";resultfps="+resultFps);
        return String.valueOf((int)resultFps);
    }

    @Override
    protected void onDestroy() {
        if(classificationDB!=null){
            classificationDB.close();
        }
        super.onDestroy();
    }
}
