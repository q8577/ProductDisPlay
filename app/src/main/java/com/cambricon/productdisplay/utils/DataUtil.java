package com.cambricon.productdisplay.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

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

    public static XYMultipleSeriesRenderer getRefender(Context context,int max, int min) {
        /*描绘器，设置图表整体效果，比如x,y轴效果，缩放比例，颜色设置*/
        XYMultipleSeriesRenderer seriesRenderer=new XYMultipleSeriesRenderer();

        seriesRenderer.setChartTitleTextSize(50);//设置图表标题的字体大小(图的最上面文字)
        seriesRenderer.setMargins(new int[] { 60, 40, 40, 40 });//设置外边距，顺序为：上左下右
        //坐标轴设置
        seriesRenderer.setAxisTitleTextSize(30);//设置坐标轴标题字体的大小
        seriesRenderer.setYAxisMin(min-15);//设置y轴的起始值
        seriesRenderer.setYAxisMax(max+10);//设置y轴的最大值
        seriesRenderer.setYLabels(10);//设置y轴显示点数
        seriesRenderer.setXAxisMin(0.5);//设置x轴起始值
        seriesRenderer.setXAxisMax(10.5);//设置x轴最大值
        seriesRenderer.setYTitle(context.getResources().getString(R.string.classification_chart_y));//设置y轴标题
        //seriesRenderer.setXTitle(getActivity().getResources().getString(R.string.classification_chart_x));//设置x轴标题
        //颜色设置
        seriesRenderer.setApplyBackgroundColor(true);//是应用设置的背景颜色
        seriesRenderer.setLabelsColor(0xFF85848D);//设置标签颜色
        seriesRenderer.setBackgroundColor(Color.argb(100, 255, 231, 224));//设置图表的背景颜色
        //缩放设置
        seriesRenderer.setZoomButtonsVisible(false);//设置缩放按钮是否可见
        seriesRenderer.setZoomEnabled(false); //图表是否可以缩放设置
        seriesRenderer.setZoomInLimitX(7);
        //图表移动设置
        seriesRenderer.setPanEnabled(false);//图表是否可以移动

        //坐标轴标签设置
        seriesRenderer.setLabelsTextSize(20);//设置标签字体大小
        seriesRenderer.setXLabelsAlign(Paint.Align.CENTER);
        seriesRenderer.setYLabelsAlign(Paint.Align.LEFT);
        seriesRenderer.setXLabels(0);//显示的x轴标签的个数
        for(int i=1;i<=Config.ChartPointNum;i++){
            seriesRenderer.addXTextLabel(i, String.valueOf(i));
        }
        seriesRenderer.setPointSize(5);//设置坐标点大小

        seriesRenderer.setMarginsColor(Color.WHITE);//设置外边距空间的颜色
        seriesRenderer.setClickEnabled(false);
        seriesRenderer.setChartTitle(context.getResources().getString(R.string.classification_chart_title));

        /*某一组数据的描绘器，描绘该组数据的个性化显示效果，主要是字体跟颜色的效果*/
        XYSeriesRenderer xySeriesRenderer1=new XYSeriesRenderer();
        xySeriesRenderer1.setAnnotationsColor(0xFFFF0000);//设置注释（注释可以着重标注某一坐标）的颜色
        xySeriesRenderer1.setAnnotationsTextAlign(Paint.Align.CENTER);//设置注释的位置
        xySeriesRenderer1.setAnnotationsTextSize(12);//设置注释文字的大小
        xySeriesRenderer1.setPointStyle(PointStyle.CIRCLE);//坐标点的显示风格
        xySeriesRenderer1.setFillPoints(true);
        xySeriesRenderer1.setPointStrokeWidth(3);//坐标点的大小
        xySeriesRenderer1.setColor(0xFFF46C48);//表示该组数据的图或线的颜色
        xySeriesRenderer1.setDisplayChartValues(true);//设置是否显示坐标点的y轴坐标值
        xySeriesRenderer1.setChartValuesTextSize(25);//设置显示的坐标点值的字体大小
        xySeriesRenderer1.setDisplayChartValuesDistance(30);

        seriesRenderer.addSeriesRenderer(xySeriesRenderer1);
        return seriesRenderer;
    }

    public static XYMultipleSeriesDataset getDataSet(Context context,int[] points) {
        XYMultipleSeriesDataset seriesDataset=new XYMultipleSeriesDataset();
        XYSeries xySeries1=new XYSeries(context.getResources().getString(R.string.classification_chart_desc));
        for(int i=1;i<=Config.ChartPointNum;i++){
            if(points[i-1]!=0){
                xySeries1.add(i,points[i-1]);
            }
        }
        seriesDataset.addSeries(xySeries1);

        return seriesDataset;
    }

}
