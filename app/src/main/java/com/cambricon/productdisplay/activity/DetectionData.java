package com.cambricon.productdisplay.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.tv.TvView;
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
import java.util.concurrent.CopyOnWriteArrayList;

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
    private int[] rePoints;
    private double[] avgTimes;
    private static double avgTimeValue = 0.00;
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

    private int[] ipu_points;
    private int[] ipu_rePoints;
    private ArrayList<DetectionImage> ipu_allTickets;

    private TextView fps_ipu;
    private TextView time_ipu;
    private Button ipu_result;

    public DetectionData() {

    }

    @SuppressLint("ValidFragment")
    public DetectionData(Context contexts, String content) {
        this.context = contexts;
        this.content = content;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.detectiondata_layout, null);
        init();
        setListener();
        return view;
    }

    private void init() {
        linearLayout = view.findViewById(R.id.chart_line);

        fps_tv = view.findViewById(R.id.avg_fps);
        time_tv = view.findViewById(R.id.avg_time);
        result_btn = view.findViewById(R.id.all_result);

        fps_ipu = view.findViewById(R.id.avg_ipu_fps);
        time_ipu = view.findViewById(R.id.avg_ipu_time);
        ipu_result = view.findViewById(R.id.all_ipu_result);

        detectionDB = new DetectionDB(getContext());
        detectionDB.open();

    }

    private void getData() {
        Log.e(TAG, "getData: " + "DetectionData");

        double allTime = 0.0;
        int allFps = 0;
        allTicketsList = new ArrayList<>();
        allTicketsList = detectionDB.fetchAll();

        ipu_allTickets = new ArrayList<>();
        ipu_allTickets = detectionDB.fetchIPUAll();

        //计算平均数
        int avg50 = 0;
        int avg101 = 0;
        int ipu_avg50 = 0;
        int ipu_avg101 = 0;
        int time50 = 0;
        int time101 = 0;
        int ipu_time50 = 0;
        int ipu_time101 = 0;

        Log.e(TAG, "getData:cpu " + allTicketsList.size());
        Log.e(TAG, "getData:ipu " + ipu_allTickets.size());
        //数据库图片属性输出
        for (DetectionImage image : allTicketsList) {
            Log.e(TAG, "getData: " + image.toString());
        }

        points = new int[Config.ChartPointNum];
        rePoints = new int[Config.ChartPointNum];

        if (allTicketsList.size() != 0) {
            int x = 0;
            int y = 0;
            for (int i = allTicketsList.size() - 1; i >= 0; i--) {
                if (allTicketsList.get(i).getNetType().equals("ResNet50") && x <= points.length) {
                    points[x] = ConvertUtil.getFps(allTicketsList.get(i).getFps());
                    avg50+=points[x];
                    time50+=Integer.valueOf(allTicketsList.get(i).getTime());
                    x++;
                } else if (y <= rePoints.length) {
                    rePoints[y] = ConvertUtil.getFps(allTicketsList.get(i).getFps());
                    avg101+=rePoints[y];
                    time101+=Integer.valueOf(allTicketsList.get(i).getTime());
                    y++;
                }
            }
            if(x>0){
                avg50/=x;
                time50/=x;
            }
            if(y>0){
                avg101/=y;
                time101/=y;
            }

            //数据库图片属性输出
            for (DetectionImage image : ipu_allTickets) {
                Log.e(TAG, "getData: " + image.toString());
            }

        }


        ipu_points = new int[Config.ChartPointNum];
        ipu_rePoints = new int[Config.ChartPointNum];

        if (ipu_allTickets.size() != 0) {
            int a = 0;
            int b = 0;
            for (int i = ipu_allTickets.size() - 1; i >= 0; i--) {
                if (ipu_allTickets.get(i).getNetType().equals("ResNet50") && a <= ipu_points.length) {
                    ipu_points[a] = ConvertUtil.getFps(ipu_allTickets.get(i).getFps())+100;
                    ipu_avg50+=ipu_points[a];
                    ipu_time50+=Integer.valueOf(ipu_allTickets.get(i).getTime());
                    a++;
                } else if (b <= rePoints.length) {
                    ipu_rePoints[b] = ConvertUtil.getFps(ipu_allTickets.get(i).getFps())+100;
                    ipu_avg101+=ipu_rePoints[b];
                    ipu_time101+=Integer.valueOf(ipu_allTickets.get(i).getTime());
                    b++;
                }
            }
            if(a>0){
                ipu_avg50/=a;
                ipu_time50/=a;
            }
            if(b>0){
                ipu_avg101/=b;
                ipu_time101/=b;
            }

        }


        fps_tv.setText("平均速率：\n");
        time_tv.setText("平均时间：\n");
        fps_tv.append(String.valueOf(avg50)+"/"+String.valueOf(avg101)+"(张/分钟)");
        time_tv.append(String.valueOf(time50)+"/"+String.valueOf(time101)+"(ms)");
        fps_ipu.setText("平均速率：\n");
        time_ipu.setText("平均时间：\n");
        fps_ipu.append(String.valueOf(ipu_avg50)+"/"+String.valueOf(ipu_avg101)+"(张/分钟)");
        time_ipu.append(String.valueOf(ipu_time50)+"/"+String.valueOf(ipu_time101)+"(ms)");


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

    private void setListener() {
        result_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResultDialog dialog = new ResultDialog(getContext());
                View contentView1 = LayoutInflater.from(getContext()).inflate(R.layout.result_dialog, null);
                ultraViewPager_dialog = contentView1.findViewById(R.id.ultra_viewpager_dialog);
                ultraViewPager_dialog.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
                adapter_dialog = new DetectPagerAdapter(true, allTicketsList);
                ultraViewPager_dialog.setAdapter(adapter_dialog);
                ultraViewPager_dialog.setMultiScreen(0.6f);
                ultraViewPager_dialog.setItemRatio(1.0f);
                ultraViewPager_dialog.setAutoMeasureHeight(true);
                ultraViewPager_dialog.setPageTransformer(false, new UltraDepthScaleTransformer());
                dialog.setContentView(contentView1);
                dialog.setTitle("测试结果");
                dialog.setCanceledOnTouchOutside(true);
                if (allTicketsList.size() > 0) {
                    dialog.show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.classification_dialog_null), Toast.LENGTH_SHORT).show();
                }
            }
        });
        ipu_result.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ResultDialog dialog = new ResultDialog(getContext());
                View contentView1 = LayoutInflater.from(getContext()).inflate(R.layout.result_dialog, null);
                ultraViewPager_dialog = contentView1.findViewById(R.id.ultra_viewpager_dialog);
                ultraViewPager_dialog.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
                adapter_dialog = new DetectPagerAdapter(true, ipu_allTickets);
                ultraViewPager_dialog.setAdapter(adapter_dialog);
                ultraViewPager_dialog.setMultiScreen(0.6f);
                ultraViewPager_dialog.setItemRatio(1.0f);
                ultraViewPager_dialog.setAutoMeasureHeight(true);
                ultraViewPager_dialog.setPageTransformer(false, new UltraDepthScaleTransformer());
                dialog.setContentView(contentView1);
                dialog.setTitle("测试结果");
                dialog.setCanceledOnTouchOutside(true);
                if (allTicketsList.size() > 0) {
                    dialog.show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.classification_dialog_null), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showChart() {
        if (allTicketsList.size() > 0) {
            XYMultipleSeriesDataset mDataSet = DataUtil.getDataSet(getContext(), points, rePoints, ipu_points, ipu_rePoints);
            XYMultipleSeriesRenderer mRefender = DataUtil.getRefender(getContext());
            graphicalView = ChartFactory.getLineChartView(getContext(), mDataSet, mRefender);
            linearLayout.removeAllViews();
            linearLayout.addView(graphicalView);
        } else {
            linearLayout.removeAllViews();
            TextView nullDate = new TextView(getContext());
            nullDate.setText("暂无检测测评数据");
            nullDate.setGravity(Gravity.CENTER_HORIZONTAL);
            nullDate.setPadding(0, 300, 0, 0);
            linearLayout.addView(nullDate);
        }
    }

}


























































