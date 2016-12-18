package kr.ac.koreatech.don_tstoop;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;


import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * Created by yimyongmin on 2016. 12. 19..
 */
public class GraphActivity extends AppCompatActivity {


    private View chart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        LinearLayout layout = (LinearLayout) findViewById (R.id.chart);
        chart = ChartFactory.getBarChartView (this, getBarChartDataset (), getRenderer (), BarChart.Type.DEFAULT);
        layout.addView (chart);

    }
    public int[] getCurrent()
    {
        int[] arr = new int[24];

        return arr;
    }
    public XYMultipleSeriesDataset getBarChartDataset() {
        XYMultipleSeriesDataset myData = new XYMultipleSeriesDataset();
        XYSeries dataSeries = new XYSeries("data");

        dataSeries.add(1, 7);
        dataSeries.add(2, 0);
        dataSeries.add(3, 7);
        dataSeries.add(4, 0);
        dataSeries.add(5, 7);
        dataSeries.add(7, 8);
        dataSeries.add(8, 9);
        dataSeries.add(9, 10);
        dataSeries.add(10, 7);
        dataSeries.add(11, 8);
        dataSeries.add(12, 9);
        dataSeries.add(13, 7);
        dataSeries.add(14, 9);
        dataSeries.add(15, 7);

        /*
        dataSeries.add(1, 7);
        dataSeries.add(2, 8);
        dataSeries.add(3, 9);
        dataSeries.add(4, 8);
        dataSeries.add(5, 7);
        dataSeries.add(6, 6);
        dataSeries.add(7, 7.1);
        dataSeries.add(8, 8);
        dataSeries.add(9, 5);
        dataSeries.add(10, 5);
        dataSeries.add(11, 5);
        dataSeries.add(12, 5);
        dataSeries.add(13, 5);
        dataSeries.add(14, 5);
        dataSeries.add(15, 5);
        */
        myData.addSeries(dataSeries);
        return myData;
    }

    public XYMultipleSeriesRenderer getRenderer() {
        XYSeriesRenderer renderer = new XYSeriesRenderer();

        renderer.setColor(Color.parseColor("#159aea"));

        XYMultipleSeriesRenderer myRenderer = new XYMultipleSeriesRenderer();
        myRenderer.addSeriesRenderer(renderer);

        myRenderer.setXAxisMin(0);
        myRenderer.setXAxisMax(10);
        myRenderer.setYAxisMin(0);
        myRenderer.setYAxisMax(20);

        myRenderer.setXLabels(0);
        myRenderer.setChartTitle("일일 통계");
        myRenderer.setChartTitleTextSize(150);
        myRenderer.setXTitle("시간");
        myRenderer.setAxisTitleTextSize(50);
        myRenderer.setYTitle("경고 횟수");
        myRenderer.setLabelsTextSize(50);


        myRenderer.setDisplayChartValues(true);
        myRenderer.setChartValuesTextSize(28);

        myRenderer.setShowGridX(true);
        myRenderer.setGridColor(Color.parseColor("#c9c9c9"));

        myRenderer.setPanEnabled(true, false);
        myRenderer.setPanLimits(new double[] {0, 24, 0, 0});

        myRenderer.setShowLegend(true);


        myRenderer.setXLabels(10);
        myRenderer.setYLabels(20);
        myRenderer.setLabelsTextSize(20);
        myRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        myRenderer.setShowAxes(false);

        myRenderer.setBarSpacing(0.5);
        myRenderer.setZoomEnabled(false, false);
        int margin[] = {20, 50, 50, 30};
        myRenderer.setMargins(margin);
        myRenderer.setMarginsColor(Color.parseColor("#FFFFFF"));

        return myRenderer;
    }




  /*  private void createChart() {
        int[] x = { 0, 1, 2, 3, 4, 5,6};
        int[] height = { 1, 2, 3, 4, 5, 6,7};

        XYSeries expenseSeries = new XYSeries("Height");

        for (int i = 0; i < x.length; i++) {
            expenseSeries.add(i, height[i]);
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(expenseSeries);


        XYSeriesRenderer heightRenderer = new XYSeriesRenderer();
        heightRenderer.setColor(Color.GREEN);
        heightRenderer.setFillPoints(true);
        heightRenderer.setDisplayChartValues(true);
        heightRenderer.setChartValuesTextSize(50);
        heightRenderer.setChartValuesTextAlign(Paint.Align.RIGHT);

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setXLabels(0);
        renderer.setChartTitle("Comparing height chart ");
        renderer.setXTitle("Friends Name");
        renderer.setYTitle("Unit in centimeter");
        renderer.setBarSpacing(0.5f);

        renderer.setChartTitleTextSize(50);
        renderer.setAxisTitleTextSize(50);
        renderer.setLabelsTextSize(50);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(false, false);
        renderer.setClickEnabled(false);


        renderer.setFitLegend(true);
        renderer.setBackgroundColor(Color.TRANSPARENT);
        renderer.setMarginsColor(getResources().getColor(android.R.color.transparent));
        renderer.setApplyBackgroundColor(true);


        renderer.setMargins(new int[] { 50, 50, 50, 50 });

        for (int i = 0; i < x.length; i++) {
            renderer.addXTextLabel(i, friends[i]);
            renderer.setXLabelsPadding(10);
            //renderer.setYLabelsPadding(10);
        }

        renderer.addSeriesRenderer(heightRenderer);


        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart);
        chartContainer.removeAllViews();
        chart = ChartFactory.getBarChartView(MainActivity.this, dataset, renderer, BarChart.Type.DEFAULT);
        chartContainer.addView(chart);

    }*/

}
