package kr.ac.koreatech.don_tstoop;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class GraphActivity extends AppCompatActivity {

    private static final String TAG = "Graph";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    SimpleDateFormat df = new SimpleDateFormat("dd", Locale.KOREA);
    int today = Integer.parseInt(df.format(new Date()));
    private int[] mDayResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // Adding Toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initializing the TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("일간"));
        tabLayout.addTab(tabLayout.newTab().setText("주간"));
        tabLayout.addTab(tabLayout.newTab().setText("월간"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Initializing ViewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        // Creating TabPagerAdapter adapter
        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        try {
            Intent intent = getIntent();
            this.mDayResult = intent.getIntArrayExtra("TODAY_RESULT");
        } catch(Exception e) {
            Toast.makeText(this, "방금 결과를 받아오는데 실패했습니다.",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        }

        for(int i = 0; i < 24; i++) {
            Log.d(TAG, "" + this.mDayResult[i]);
        }

        // Set TabSelectedListener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        int[] temp_arr = new int[31];
        int temp_sum=0;
        int day_sum=0,week_sum=0,month_sum=0;
        int temp_minus = today - 7;
        if(temp_minus < 0) temp_minus = 0;

        for(int i=0;i<24;i++)
            day_sum += mDayResult[i];
        for(int i=temp_minus;i<today;i++)
            week_sum += temp_arr[i];
        for(int i=0;i<31;i++)
            month_sum += temp_arr[i];

        for(int i=0;i<31;i++)
            temp_arr[i] = 0;
        for(int i=0;i<24;i++)
            temp_sum += mDayResult[i];

        temp_arr[today-1] = temp_sum;
        setChekDay(getChekDay());
        setCurrent(getCurrent(), mDayResult);
        setMonth(getMonth(), temp_arr);

        Log.d(TAG, "DAY"+day_sum);
        Log.d(TAG, "WEEK"+week_sum);
        Log.d(TAG, "MONTH"+month_sum);
    }

    public int[] getMonth()
    {
        int[] result_arr = new int[31];

        byte data[] = null;
        FileInputStream open;
        String result;
        try{
            open = openFileInput("test2.txt");
            data = new byte[open.available()];
            while(open.read(data)!=-1) {;}
            result = new String(data);
            String[] b = result.split(" ");
            for(int i=0;i<31;i++)
            {
                result_arr[i] = Integer.parseInt(b[i]);
                Log.i("ddd", String.valueOf(result_arr[i]));
            }
            open.close();
        }
        catch(Exception e)
        {
            for(int i=0;i<31;i++)
                result_arr[i] = 0;

            /**TEST용 DUMMY DATA **/
            /*
            result_arr[0] = 12;
            result_arr[1] = 51;
            result_arr[2] = 34;
            result_arr[3] = 62;
            result_arr[4] = 52;
            result_arr[5] = 21;
            result_arr[6] = 45;
            result_arr[7] = 17;
            result_arr[8] = 64;
            result_arr[9] = 72;
            result_arr[10] = 21;
            result_arr[11] = 59;
            result_arr[12] = 63;
            result_arr[13] = 12;
            result_arr[14] = 24;
            result_arr[15] = 62;
            result_arr[16] = 91;
            result_arr[17] = 74;
            result_arr[18] = 45;
            result_arr[19] = 26;
            */
            /*********************/
            e.printStackTrace();
        }
        return result_arr;
    }
    public int[] getCurrent()
    {
        int[] result_arr = new int[24];

        byte data[] = null;
        FileInputStream open;
        String result;
        try{
            open = openFileInput("test.txt");
            data = new byte[open.available()];
            while(open.read(data)!=-1) {;}
            result = new String(data);
            String[] b = result.split(" ");
            for(int i=0;i<24;i++)
            {
                result_arr[i] = Integer.parseInt(b[i]);
            }
            open.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            for(int i=0;i<24;i++)
                result_arr[i] = 0;
        }
        return result_arr;
    }
    public void setCurrent(int[] arr,int[] input_arr)
    {
        String fileName = "test.txt";
        try{
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            for(int i=0;i<24;i++) {
                fos.write(String.valueOf(arr[i] + input_arr[i]).getBytes());
                fos.write(" ".getBytes());
            }
            fos.close();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    public void setMonth(int[] arr,int[] input_arr)
    {
        String fileName = "test2.txt";
        try{
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);

            for(int i=0;i<31;i++) {
                fos.write(String.valueOf(arr[i] + input_arr[i]).getBytes());
                fos.write(" ".getBytes());
            }
            fos.close();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public int getChekDay()
    {
        int result_int=0;

        byte data[] = null;
        FileInputStream open;
        String result;
        try{
            open = openFileInput("checkday.txt");
            data = new byte[open.available()];
            while(open.read(data)!=-1) {;}
            result = new String(data);
            String[] b = result.split(" ");
            result_int = Integer.parseInt(b[0]);
            open.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return result_int;
    }
    public void setChekDay(int input_result)
    {
        String fileName = "checkday.txt";
        if(input_result != today)
        {
            int[] input_arr = new int[24];
            for(int i=0;i<24;i++)
                input_arr[i] = 0;
            setCurrent(input_arr,input_arr);
        }
        try{
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(String.valueOf(today).getBytes());
            fos.write(" ".getBytes());

            fos.close();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

}


