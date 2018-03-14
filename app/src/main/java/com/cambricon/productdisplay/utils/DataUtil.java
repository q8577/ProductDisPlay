package com.cambricon.productdisplay.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.cambricon.productdisplay.R;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * Created by gzb on 18-3-6.
 */

public class DataUtil {

    public static XYMultipleSeriesRenderer getRefender(Context context) {
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
        //seriesRenderer.setYTitle(getActivity().getResources().getString(R.string.classification_chart_y));//设置y轴标题
        seriesRenderer.setYLabelsColor(0,Color.BLACK);
        seriesRenderer.setXLabelsColor(Color.BLACK);
        //seriesRenderer.setXTitle(getActivity().getResources().getString(R.string.classification_chart_x));//设置x轴标题
        //颜色设置
        //seriesRenderer.setApplyBackgroundColor(true);//是应用设置的背景颜色
        seriesRenderer.setLabelsColor(0xFF85848D);//设置标签颜色
        //seriesRenderer.setBackgroundColor(Color.argb(100, 255, 231, 224));//设置图表的背景颜色
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
        //seriesRenderer.setChartTitle(getContext().getResources().getString(R.string.classification_chart_title));

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
        xySeriesRenderer2.setPointStyle(PointStyle.TRIANGLE);//坐标点的显示风格
        xySeriesRenderer2.setFillPoints(true);
        xySeriesRenderer2.setPointStrokeWidth(3);//坐标点的大小
        xySeriesRenderer2.setColor(R.color.main_line);//表示该组数据的图或线的颜色
        xySeriesRenderer2.setDisplayChartValues(true);//设置是否显示坐标点的y轴坐标值
        xySeriesRenderer2.setChartValuesTextSize(25);//设置显示的坐标点值的字体大小
        xySeriesRenderer2.setDisplayChartValuesDistance(30);

        /*某一组数据的描绘器，描绘该组数据的个性化显示效果，主要是字体跟颜色的效果*/
        XYSeriesRenderer xySeriesRenderer3 = new XYSeriesRenderer();
        xySeriesRenderer3.setAnnotationsColor(Color.BLUE);//设置注释（注释可以着重标注某一坐标）的颜色
        xySeriesRenderer3.setAnnotationsTextAlign(Paint.Align.CENTER);//设置注释的位置
        xySeriesRenderer3.setAnnotationsTextSize(20);//设置注释文字的大小
        xySeriesRenderer3.setPointStyle(PointStyle.DIAMOND);//坐标点的显示风格
        xySeriesRenderer3.setFillPoints(true);
        xySeriesRenderer3.setPointStrokeWidth(3);//坐标点的大小
        xySeriesRenderer3.setColor(Color.BLUE);//表示该组数据的图或线的颜色
        xySeriesRenderer3.setDisplayChartValues(true);//设置是否显示坐标点的y轴坐标值
        xySeriesRenderer3.setChartValuesTextSize(25);//设置显示的坐标点值的字体大小
        xySeriesRenderer3.setDisplayChartValuesDistance(30);

        /*某一组数据的描绘器，描绘该组数据的个性化显示效果，主要是字体跟颜色的效果*/
        XYSeriesRenderer xySeriesRenderer4 = new XYSeriesRenderer();
        xySeriesRenderer4.setAnnotationsColor(Color.GREEN);//设置注释（注释可以着重标注某一坐标）的颜色
        xySeriesRenderer4.setAnnotationsTextAlign(Paint.Align.CENTER);//设置注释的位置
        xySeriesRenderer4.setAnnotationsTextSize(20);//设置注释文字的大小
        xySeriesRenderer4.setPointStyle(PointStyle.SQUARE);//坐标点的显示风格
        xySeriesRenderer4.setFillPoints(true);
        xySeriesRenderer4.setPointStrokeWidth(3);//坐标点的大小
        xySeriesRenderer4.setColor(Color.GREEN);//表示该组数据的图或线的颜色
        xySeriesRenderer4.setDisplayChartValues(true);//设置是否显示坐标点的y轴坐标值
        xySeriesRenderer4.setChartValuesTextSize(25);//设置显示的坐标点值的字体大小
        xySeriesRenderer4.setDisplayChartValuesDistance(30);

        seriesRenderer.addSeriesRenderer(xySeriesRenderer1);
        seriesRenderer.addSeriesRenderer(xySeriesRenderer2);
        seriesRenderer.addSeriesRenderer(xySeriesRenderer3);
        seriesRenderer.addSeriesRenderer(xySeriesRenderer4);

        return seriesRenderer;
    }

    public static XYMultipleSeriesDataset getDataSet(Context context,int[] points,int[] repoints,int[] ipu_points, int[] ipu_repoints) {
        XYMultipleSeriesDataset seriesDataset=new XYMultipleSeriesDataset();
        XYSeries xySeries1=new XYSeries("CPU模式网络ResNet50数据");
        XYSeries xySeries2 = new XYSeries("CPU模式网络ResNet50数据");
        XYSeries xySeries3 = new XYSeries("IPU模式网络ResNet101数据");
        XYSeries xySeries4 = new XYSeries("IPU模式网络ResNet101数据");

        for(int i=1;i<=Config.ChartPointNum;i++){
            if(points[i-1]!=0){
                xySeries1.add(i,points[i-1]);
            }
        }
        for(int i=1;i<=Config.ChartPointNum;i++){
            if(repoints[i-1]!=0){
                xySeries2.add(i,repoints[i-1]);
            }

        }
        for(int i=1;i<=Config.ChartPointNum;i++){
            if(ipu_points[i-1]!=0){
                xySeries3.add(i,ipu_points[i-1]);
            }
        }
        for(int i=1;i<=Config.ChartPointNum;i++){
            if(ipu_repoints[i-1]!=0){
                xySeries4.add(i,ipu_repoints[i-1]);
            }
        }


        seriesDataset.addSeries(xySeries1);
        seriesDataset.addSeries(xySeries2);
        seriesDataset.addSeries(xySeries3);
        seriesDataset.addSeries(xySeries4);


        return seriesDataset;
    }

}
