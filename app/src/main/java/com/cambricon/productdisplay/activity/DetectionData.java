package com.cambricon.productdisplay.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.adapter.DetectPagerAdapter;
import com.cambricon.productdisplay.adapter.UltraPagerAdapter;
import com.cambricon.productdisplay.bean.ClassificationImage;
import com.cambricon.productdisplay.bean.DetectionImage;
import com.cambricon.productdisplay.db.ClassificationDB;
import com.cambricon.productdisplay.db.DetectionDB;
import com.cambricon.productdisplay.utils.Config;
import com.cambricon.productdisplay.utils.ConvertUtil;
import com.cambricon.productdisplay.utils.DataUtil;
import com.cambricon.productdisplay.view.ResultDialog;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraDepthScaleTransformer;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;

/**
 * Created by dell on 18-2-8.
 */

public class DetectionData extends Fragment {
    private final String TAG = "DetectionData";

    private View view;
    private Context context;
    private String content;
    private LinearLayout linearLayout;
    private GraphicalView graphicalView;
    private int[] points;
    private double[] avgTimes;
    private static double avgTimeValue=0.00;
    private static int avgFpsValue;
    private TextView fps_tv;
    private TextView time_tv;
    private Button result_btn;
    private DetectionDB detectionDB;
    private UltraViewPager ultraViewPager_dialog;
    private PagerAdapter adapter_dialog;
    private ArrayList<DetectionImage> allTicketsList;
    private int max = 0;
    private int min = 10000;

    public DetectionData(){

    }

    @SuppressLint("ValidFragment")
    public DetectionData(Context contexts, String content) {
        this.context = contexts;
        this.content = content;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.classification_data_layout, null);
        init();
        setListener();
        return view;
    }
    private void init(){
        linearLayout=view.findViewById(R.id.chart_line);

        fps_tv=view.findViewById(R.id.avg_fps);
        time_tv=view.findViewById(R.id.avg_time);
        result_btn=view.findViewById(R.id.all_result);

        detectionDB=new DetectionDB(getContext());
        detectionDB.open();

    }
    private void getData(){

        double allTime = 0.0;
        int allFps = 0;
        allTicketsList = new ArrayList<>();
        allTicketsList = detectionDB.fetchAll();

        for(DetectionImage image : allTicketsList){
            Log.e(TAG, "getData: "+image.toString());
        }

        points=new int[Config.ChartPointNum];
        if(allTicketsList.size()!=0){
            avgTimes=new double[allTicketsList.size()];
            int dataSum;
            if (Config.ChartPointNum > allTicketsList.size()) {
                dataSum = allTicketsList.size();
            } else {
                dataSum = Config.ChartPointNum;
            }

            for(int i=0;i<dataSum;i++){
                points[i]= ConvertUtil.getFps(allTicketsList.get(i).getFps());
                if(points[i]>max){
                    max = points[i];
                }
                if(points[i]<min){
                    min = points[i];
                }
                avgTimes[i]=ConvertUtil.convert2Double(allTicketsList.get(i).getTime());
                allTime=allTime+avgTimes[i];
                allFps=allFps+points[i];
            }
            avgTimeValue=allTime/dataSum;
            avgFpsValue=(int) allFps/dataSum;
            fps_tv.setText("平均FPS值：");
            time_tv.setText("单张图片平均时间：");
            fps_tv.append(String.valueOf(avgFpsValue)+"张/分钟");
            time_tv.append(String.valueOf((int) avgTimeValue)+"ms");
        }


    }

    @Override
    public void onResume() {

        super.onResume();
        getData();
        Log.e(TAG, "onResume: ");
        showChart();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setListener(){
        result_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResultDialog dialog = new ResultDialog(getContext());
                View contentView1 = LayoutInflater.from(getContext()).inflate(R.layout.result_dialog,null);
                ultraViewPager_dialog = contentView1.findViewById(R.id.ultra_viewpager_dialog);
                ultraViewPager_dialog.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
                adapter_dialog = new DetectPagerAdapter(true,allTicketsList);
                ultraViewPager_dialog.setAdapter(adapter_dialog);
                ultraViewPager_dialog.setMultiScreen(0.6f);
                ultraViewPager_dialog.setItemRatio(1.0f);
                ultraViewPager_dialog.setAutoMeasureHeight(true);
                ultraViewPager_dialog.setPageTransformer(false, new UltraDepthScaleTransformer());
                dialog.setContentView(contentView1);
                dialog.setTitle("测试结果");
                dialog.setCanceledOnTouchOutside(true);
                if(allTicketsList.size()>0){
                    dialog.show();
                }else{
                    Toast.makeText(getContext(),getString(R.string.classification_dialog_null),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showChart() {
        if(allTicketsList.size()>0){
            XYMultipleSeriesDataset mDataSet = DataUtil.getDataSet(getContext(),points);
            XYMultipleSeriesRenderer mRefender= DataUtil.getRefender(getContext(),max,min);
            graphicalView= ChartFactory.getLineChartView(getContext(), mDataSet, mRefender);
            linearLayout.removeAllViews();
            linearLayout.addView(graphicalView);
        }else{
            linearLayout.removeAllViews();
            TextView nullDate = new TextView(getContext());
            nullDate.setText("暂无检测测评数据");
            nullDate.setGravity(Gravity.CENTER_HORIZONTAL);
            nullDate.setPadding(0,300,0,0);
            linearLayout.addView(nullDate);
        }
    }

}


























































