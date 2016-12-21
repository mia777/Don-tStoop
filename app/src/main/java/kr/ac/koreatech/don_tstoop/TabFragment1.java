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

import android.widget.TextView;
import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TabFragment1 extends Fragment {

    private static final String TAG = "TAB1";
    View _chart;
    int day_sum = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_fragment_1,container,false);

        LinearLayout layout = (LinearLayout)v.findViewById(R.id.chart);

        _chart = ChartFactory.getBarChartView(getActivity(), getBarChartDataset (getCurrent()), getRenderer (MAX_Range(getCurrent(),24)), BarChart.Type.DEFAULT);
        layout.addView(_chart);
        ((TextView)v.findViewById(R.id.textView)).setText("총 경고횟수: " + day_sum);

        return v;
    }

    public int[] getCurrent()
    {
        int[] result_arr = new int[24];

        byte data[] = null;
        FileInputStream open;
        String result;
        try{
            open = getContext().openFileInput("test.txt");
            data = new byte[open.available()];
            while(open.read(data)!=-1) {;}
            result = new String(data);
            String[] b = result.split(" ");

            day_sum = 0;
            for(int i=0;i<24;i++)
            {
                result_arr[i] = Integer.parseInt(b[i]);
                day_sum += result_arr[i];

                Log.i(TAG, "RESULT[" + i + "]: " + String.valueOf(result_arr[i]));
                Log.d(TAG, "DAY_SUM: " + day_sum);
            }
            open.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return result_arr;
    }

    public int MAX_Range(int[] arr,int length)
    {
        int max = 0;
        for(int i=0;i<length;i++)
        {
            if(max <= arr[i])
                max = arr[i];
        }
        return max;
    }

    public void setCurrent()
    {
        String fileName = "test.txt";
        try{
            FileOutputStream fos = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            for(int i=0;i<24;i++) {
                fos.write(String.valueOf(i).getBytes());
                fos.write(" ".getBytes());
                Log.i(TAG,String.valueOf(i));
            }
            fos.close();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public XYMultipleSeriesDataset getBarChartDataset(int[] arr) {
        XYMultipleSeriesDataset myData = new XYMultipleSeriesDataset();
        XYSeries dataSeries = new XYSeries("data");

        for(int i=0;i<24;i++)
            dataSeries.add(i,arr[i]);


        myData.addSeries(dataSeries);
        return myData;
    }

    public XYMultipleSeriesRenderer getRenderer(int _length) {
        XYSeriesRenderer renderer = new XYSeriesRenderer();

        renderer.setColor(Color.parseColor("#159aea"));

        XYMultipleSeriesRenderer myRenderer = new XYMultipleSeriesRenderer();
        myRenderer.addSeriesRenderer(renderer);

        myRenderer.setXAxisMin(-1);
        myRenderer.setXAxisMax(24);
        myRenderer.setYAxisMin(0);
        myRenderer.setYAxisMax(_length+1);

        myRenderer.setXLabels(0);
        myRenderer.setChartTitle("일간 통계");
        myRenderer.setChartTitleTextSize(150);
        myRenderer.setXTitle("시간");
        myRenderer.setAxisTitleTextSize(50);
        myRenderer.setYTitle("경고 횟수");
        myRenderer.setLabelsTextSize(50);


        myRenderer.setDisplayChartValues(true);
        myRenderer.setChartValuesTextSize(28);

        myRenderer.setShowGridX(true);
        myRenderer.setGridColor(Color.parseColor("#c9c9c9"));

        myRenderer.setPanEnabled(false, false);
        myRenderer.setPanLimits(new double[] {0, 23.5,0, 0});

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


