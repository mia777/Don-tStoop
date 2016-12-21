package kr.ac.koreatech.don_tstoop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TabFragment3 extends Fragment {
    View _chart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_fragment_3, container, false);

        LinearLayout layout = (LinearLayout) v.findViewById(R.id.chart3);

        _chart = ChartFactory.getBarChartView(getActivity(), getBarChartDataset(getCurrent()), getRenderer(MAX_Range(getCurrent(), 31)), BarChart.Type.DEFAULT);
        layout.addView(_chart);

        return v;
    }

    public int[] getCurrent() {
        int[] result_arr = new int[31];

        byte data[] = null;
        FileInputStream open;
        String result;
        try {
            open = getContext().openFileInput("test2.txt");
            data = new byte[open.available()];
            while (open.read(data) != -1) {
                ;
            }
            result = new String(data);
            String[] b = result.split(" ");
            for (int i = 0; i < 31; i++) {
                result_arr[i] = Integer.parseInt(b[i]);
                Log.i("ddd", String.valueOf(result_arr[i]));
            }
            open.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result_arr;
    }

    public int MAX_Range(int[] arr, int length) {
        int max = 0;
        for (int i = 0; i < length; i++) {
            if (max <= arr[i])
                max = arr[i];
        }
        return max;
    }

    public void setCurrent() {
        String fileName = "test.txt";
        String msg = "hello world";
        try {
            FileOutputStream fos = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            for (int i = 0; i < 31; i++) {
                fos.write(String.valueOf(i).getBytes());
                fos.write(" ".getBytes());
                Log.i("tag", String.valueOf(i));
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public XYMultipleSeriesDataset getBarChartDataset(int[] arr) {
        XYMultipleSeriesDataset myData = new XYMultipleSeriesDataset();
        XYSeries dataSeries = new XYSeries("data");

        for (int i = 0; i < 31; i++)
            dataSeries.add(i, arr[i]);


        myData.addSeries(dataSeries);
        return myData;
    }

    public XYMultipleSeriesRenderer getRenderer(int _length) {
        XYSeriesRenderer renderer = new XYSeriesRenderer();

        renderer.setColor(Color.parseColor("#159aea"));

        XYMultipleSeriesRenderer myRenderer = new XYMultipleSeriesRenderer();
        myRenderer.addSeriesRenderer(renderer);

        myRenderer.setXAxisMin(-1);
        myRenderer.setXAxisMax(31);
        myRenderer.setYAxisMin(0);
        myRenderer.setYAxisMax(_length + 1);

        myRenderer.setXLabels(0);
        myRenderer.setChartTitle("월간 통계");
        myRenderer.setChartTitleTextSize(150);
        myRenderer.setXTitle("일수");
        myRenderer.setAxisTitleTextSize(50);
        myRenderer.setYTitle("경고 횟수");
        myRenderer.setLabelsTextSize(50);


        myRenderer.setDisplayChartValues(true);
        myRenderer.setChartValuesTextSize(28);

        myRenderer.setShowGridX(true);
        myRenderer.setGridColor(Color.parseColor("#c9c9c9"));

        myRenderer.setPanEnabled(false, false);
        myRenderer.setPanLimits(new double[]{0, 30.5, 0, 0});

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
}