package com.cambricon.productdisplay.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.adapter.FaceDetectorAdapter;
import com.cambricon.productdisplay.bean.FaceDetectionImage;
import com.cambricon.productdisplay.db.FaceDetectDB;
import com.cambricon.productdisplay.utils.Config;
import com.cambricon.productdisplay.utils.ConvertUtil;
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
 * Created by cambricon on 18-3-13.
 */

public class FaceDetectorData extends Fragment implements View.OnClickListener {

    private static final String TAG = FaceDetectorData.class.getSimpleName();

    private View mView;
    private String mContent;
    private Context mContext;
    private LinearLayout mLinearLayout;

    private Button mBtn_ipu_result;
    private Button mBtn_result;
    private TextView mTv_ipu_avg_fps;
    private TextView mTv_avg_fps;
    private TextView mTv_ipu_avg_time;
    private TextView mTv_avg_time;
    private GraphicalView mGraphicalView;

    private ArrayList<FaceDetectionImage> mAllTicketsList;
    private ArrayList<FaceDetectionImage> mAllIPUTicketsList;

    private UltraViewPager ultraViewPager_dialog;
    private UltraViewPager ultraViewPager_ipuDialog;

    private PagerAdapter adapter_dialog;

    private FaceDetectDB mFaceDetectDB;

    //cpu param
    private int[] points;
    private double[] avgTimes;
    private static double avgTimeValue = 0.00;
    private static int avgFpsValue;
    //ipu param
    private int[] ipu_points;
    private double[] avgIPUTimes;
    private static double avgIPUTimeValue = 0.00;
    private static int avgIPUFpsValue;

    private int max = 0;
    private int min = 10000;

    public FaceDetectorData() {
    }

    @SuppressLint("ValidFragment")
    public FaceDetectorData(Context context, String content) {
        mContext = context;
        mContent = content;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_face_detector_data, container, false);
        initUI();
        return mView;
    }

    private void initUI() {
        mLinearLayout = mView.findViewById(R.id.layout_face_data_chart_line);
        mTv_avg_fps = mView.findViewById(R.id.tv_face_data_avg_fps);
        mTv_ipu_avg_fps = mView.findViewById(R.id.tv_face_data_avg_ipu_fps);
        mTv_avg_time = mView.findViewById(R.id.tv_face_data_avg_time);
        mTv_ipu_avg_time = mView.findViewById(R.id.tv_face_data_avg_ipu_time);
        mBtn_result = mView.findViewById(R.id.btn_face_data_all_result);
        mBtn_ipu_result = mView.findViewById(R.id.btn_face_data_all_ipu_result);

        mBtn_result.setOnClickListener(this);
        mBtn_ipu_result.setOnClickListener(this);

        mFaceDetectDB = new FaceDetectDB(getContext());
        mFaceDetectDB.open();
    }

    private void getData() {

        double allTime = 0.00;
        int allFps = 0;

        double allIPUTime = 0.00;
        int allIPUFps = 0;

        mAllTicketsList = new ArrayList<>();
        mAllTicketsList = mFaceDetectDB.fetchAll();
        points = new int[Config.ChartPointNum];

        mAllIPUTicketsList = new ArrayList<>();
        mAllIPUTicketsList = mFaceDetectDB.fetchIPUAll();
        ipu_points = new int[Config.ChartPointNum];


        if (mAllIPUTicketsList.size() != 0) {
            avgIPUTimes = new double[mAllIPUTicketsList.size()];
            int ipuCount = 0;
            if (Config.ChartPointNum > mAllIPUTicketsList.size()) {
                ipuCount = mAllIPUTicketsList.size();
            } else {
                ipuCount = Config.ChartPointNum;
            }

            for (int j = 0; j < ipuCount; j++) {
                ipu_points[j] = ConvertUtil.getFps(mAllIPUTicketsList.get(mAllIPUTicketsList.size() - j - 1).getFps()) + 100;
                avgIPUTimes[j] = ConvertUtil.convert2Double(mAllIPUTicketsList.get(mAllIPUTicketsList.size() - j - 1).getTime());
                allIPUTime = allTime + avgIPUTimes[j];
                allIPUFps = allFps + ipu_points[j];
            }
            avgIPUTimeValue = allIPUTime / ipuCount;
            avgIPUFpsValue = allIPUFps / ipuCount;
            mTv_ipu_avg_fps.setText(R.string.classification_avg_time);
            mTv_ipu_avg_time.setText(R.string.classification_single_time);
            mTv_ipu_avg_fps.append(String.valueOf(avgIPUFpsValue) + "张/分钟");
            mTv_ipu_avg_time.append(String.valueOf((int) avgIPUTimeValue) + "ms");
        }

        if (mAllTicketsList.size() != 0) {
            avgTimes = new double[mAllTicketsList.size()];
            int dataSum;
            if (Config.ChartPointNum > mAllTicketsList.size()) {
                dataSum = mAllTicketsList.size();
            } else {
                dataSum = Config.ChartPointNum;
            }
            for (int i = 0; i < dataSum; i++) {

                points[i] = ConvertUtil.getFps(mAllTicketsList.get(mAllTicketsList.size() - i - 1).getFps());
                if (points[i] > max) {
                    max = points[i];
                }
                if (points[i] < min) {
                    min = points[i];
                }
                avgTimes[i] = ConvertUtil.convert2Double(mAllTicketsList.get(mAllTicketsList.size() - i - 1).getTime());
                allTime = allTime + avgTimes[i];
                allFps = allFps + points[i];
            }
            avgTimeValue = allTime / dataSum;
            avgFpsValue = allFps / dataSum;

            mTv_avg_fps.setText(R.string.classification_avg_time);
            mTv_avg_time.setText(R.string.classification_single_time);
            mTv_avg_fps.append(String.valueOf(avgFpsValue) + "张/分钟");
            mTv_avg_time.append(String.valueOf((int) avgTimeValue) + "ms");
        }
    }

    @Override
    public void onResume() {
        getData();
        showChart();
        super.onResume();
    }

    private XYMultipleSeriesDataset getDataSet() {
        XYMultipleSeriesDataset seriesDataset = new XYMultipleSeriesDataset();
        XYSeries xySeries1 = new XYSeries(getContext().getResources().getString(R.string.face_detection_chart_desc));
        XYSeries xySeries2 = new XYSeries(getContext().getResources().getString(R.string.face_detection_chart_ipu_desc));
        for (int i = 1; i <= Config.ChartPointNum; i++) {
            if (points[i - 1] != 0) {
                xySeries1.add(i, points[i - 1]);
            }
            if (ipu_points[i - 1] != 0) {
                xySeries2.add(i, ipu_points[i - 1]);
            }
        }
        seriesDataset.addSeries(xySeries1);
        seriesDataset.addSeries(xySeries2);

        return seriesDataset;
    }

    private void showChart() {
        if (mAllTicketsList.size() > 0 || mAllIPUTicketsList.size() > 0) {
            XYMultipleSeriesDataset mDataSet = getDataSet();
            XYMultipleSeriesRenderer mRefender = getRefender();
            mGraphicalView = ChartFactory.getLineChartView(getContext(), mDataSet, mRefender);
            mLinearLayout.removeAllViews();
            mLinearLayout.addView(mGraphicalView);
        } else {
            mLinearLayout.removeAllViews();
            TextView nullDate = new TextView(getContext());
            nullDate.setText("暂无功能测评数据");
            nullDate.setGravity(Gravity.CENTER_HORIZONTAL);
            nullDate.setPadding(0, 300, 0, 0);
            mLinearLayout.addView(nullDate);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_face_data_all_result:
                ResultDialog dialog = new ResultDialog(getContext());
                View contentView1 = LayoutInflater.from(getContext()).inflate(R.layout.result_dialog, null);
                ultraViewPager_dialog = contentView1.findViewById(R.id.ultra_viewpager_dialog);
                ultraViewPager_dialog.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
                adapter_dialog = new FaceDetectorAdapter(true, mAllTicketsList);
                ultraViewPager_dialog.setAdapter(adapter_dialog);
                ultraViewPager_dialog.setMultiScreen(0.6f);
                ultraViewPager_dialog.setItemRatio(1.0f);
                ultraViewPager_dialog.setAutoMeasureHeight(true);
                ultraViewPager_dialog.setPageTransformer(false, new UltraDepthScaleTransformer());
                dialog.setContentView(contentView1);
                dialog.setTitle("测试结果");
                dialog.setCanceledOnTouchOutside(true);
                if (mAllTicketsList.size() > 0) {
                    dialog.show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.face_detection_dialog_null), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_face_data_all_ipu_result:
                dialog = new ResultDialog(getContext());
                View contentView2 = LayoutInflater.from(getContext()).inflate(
                        R.layout.result_dialog, null);
                ultraViewPager_ipuDialog = contentView2.findViewById(R.id.ultra_viewpager_dialog);
                ultraViewPager_ipuDialog.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
                adapter_dialog = new FaceDetectorAdapter(true, mAllTicketsList);
                ultraViewPager_ipuDialog.setAdapter(adapter_dialog);
                ultraViewPager_ipuDialog.setMultiScreen(0.6f);
                ultraViewPager_ipuDialog.setItemRatio(1.0f);
                ultraViewPager_ipuDialog.setAutoMeasureHeight(true);
                ultraViewPager_ipuDialog.setPageTransformer(false, new UltraDepthScaleTransformer());
                dialog.setContentView(contentView2);
                dialog.setTitle("测试结果");
                dialog.setCanceledOnTouchOutside(true);
                if (mAllIPUTicketsList.size() > 0) {
                    dialog.show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.face_detection_dialog_null), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private XYMultipleSeriesRenderer getRefender() {
        /*描绘器，设置图表整体效果，比如x,y轴效果，缩放比例，颜色设置*/
        XYMultipleSeriesRenderer seriesRenderer = new XYMultipleSeriesRenderer();

        seriesRenderer.setChartTitleTextSize(50);//设置图表标题的字体大小(图的最上面文字)
        seriesRenderer.setMargins(new int[]{20, 40, 40, 40});//设置外边距，顺序为：上左下右
        //坐标轴设置
        seriesRenderer.setAxisTitleTextSize(30);//设置坐标轴标题字体的大小
        seriesRenderer.setYAxisMin(0);//设置y轴的起始值
        seriesRenderer.setYAxisMax(300);//设置y轴的最大值
        seriesRenderer.setYLabels(10);//设置y轴显示点数
        seriesRenderer.setXAxisMin(0.5);//设置x轴起始值
        seriesRenderer.setXAxisMax(10.5);//设置x轴最大值
        seriesRenderer.setYLabelsColor(0, Color.BLACK);
        seriesRenderer.setXLabelsColor(Color.BLACK);
        //颜色设置
        seriesRenderer.setLabelsColor(0xFF85848D);//设置标签颜色
        seriesRenderer.setBackgroundColor(R.color.gridview_bg);//设置图表的背景颜色
        //缩放设置
        seriesRenderer.setZoomButtonsVisible(false);//设置缩放按钮是否可见
        seriesRenderer.setZoomEnabled(false); //图表是否可以缩放设置
        seriesRenderer.setZoomInLimitX(7);
        //图表移动设置
        seriesRenderer.setPanEnabled(false);//图表是否可以移动

        //坐标轴标签设置
        seriesRenderer.setLabelsTextSize(25);//设置标签字体大小
        seriesRenderer.setXLabelsAlign(Paint.Align.CENTER);
        seriesRenderer.setYLabelsAlign(Paint.Align.LEFT);
        seriesRenderer.setXLabels(0);//显示的x轴标签的个数
        for (int i = 1; i <= Config.ChartPointNum; i++) {
            seriesRenderer.addXTextLabel(i, String.valueOf(i));
        }
        seriesRenderer.setPointSize(5);//设置坐标点大小
        seriesRenderer.setMarginsColor(Color.WHITE);//设置外边距空间的颜色
        seriesRenderer.setClickEnabled(false);

        /*某一组数据的描绘器，描绘该组数据的个性化显示效果，主要是字体跟颜色的效果*/
        XYSeriesRenderer xySeriesRenderer1 = new XYSeriesRenderer();
        xySeriesRenderer1.setAnnotationsColor(0xFFFF0000);//设置注释（注释可以着重标注某一坐标）的颜色
        xySeriesRenderer1.setAnnotationsTextAlign(Paint.Align.CENTER);//设置注释的位置
        xySeriesRenderer1.setAnnotationsTextSize(20);//设置注释文字的大小
        xySeriesRenderer1.setPointStyle(PointStyle.CIRCLE);//坐标点的显示风格
        xySeriesRenderer1.setFillPoints(true);
        xySeriesRenderer1.setPointStrokeWidth(3);//坐标点的大小
        xySeriesRenderer1.setColor(0xFFF46C48);//表示该组数据的图或线的颜色
        xySeriesRenderer1.setDisplayChartValues(true);//设置是否显示坐标点的y轴坐标值
        xySeriesRenderer1.setChartValuesTextSize(25);//设置显示的坐标点值的字体大小
        xySeriesRenderer1.setDisplayChartValuesDistance(30);

         /*某一组数据的描绘器，描绘该组数据的个性化显示效果，主要是字体跟颜色的效果*/
        XYSeriesRenderer xySeriesRenderer2 = new XYSeriesRenderer();
        xySeriesRenderer2.setAnnotationsColor(R.color.main_line);//设置注释（注释可以着重标注某一坐标）的颜色
        xySeriesRenderer2.setAnnotationsTextAlign(Paint.Align.CENTER);//设置注释的位置
        xySeriesRenderer2.setAnnotationsTextSize(20);//设置注释文字的大小
        xySeriesRenderer2.setPointStyle(PointStyle.CIRCLE);//坐标点的显示风格
        xySeriesRenderer2.setFillPoints(true);
        xySeriesRenderer2.setPointStrokeWidth(3);//坐标点的大小
        xySeriesRenderer2.setColor(R.color.main_line);//表示该组数据的图或线的颜色
        xySeriesRenderer2.setDisplayChartValues(true);//设置是否显示坐标点的y轴坐标值
        xySeriesRenderer2.setChartValuesTextSize(25);//设置显示的坐标点值的字体大小
        xySeriesRenderer2.setDisplayChartValuesDistance(30);

        seriesRenderer.addSeriesRenderer(xySeriesRenderer1);
        seriesRenderer.addSeriesRenderer(xySeriesRenderer2);
        return seriesRenderer;
    }
}
