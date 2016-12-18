package kr.ac.koreatech.don_tstoop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.app.ActivityManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private final int VIBRATE = 0;
    private final int SOUND = 1;
    private final int SILENT = 2;

    private Button mBtnGraph;
    private TextView mTvSensor;
    private SensorManager mSensorManager;
    private Sensor mProximitySensor;
    private ImageView iv;
    private FileInputStream fis;

    private int mAlarmType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnGraph = (Button) findViewById(R.id.button);
        iv = (ImageView)findViewById(R.id.black);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        try{
            fis = openFileInput("current.txt");
        }catch (IOException e){
            e.printStackTrace();
            iv.setVisibility(View.VISIBLE);
        }



    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        super.onTouchEvent(event);
        if(event.getAction()==MotionEvent.ACTION_DOWN)
        iv.setVisibility(View.INVISIBLE);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.vibe:
                Log.v("ActionBar", "Viberate");
                mAlarmType = VIBRATE;
                return true;
            case R.id.nm:
                Log.v("ActionBar", "Sound");
                mAlarmType = SOUND;
                return true;
            case R.id.sl:
                Log.v("ActionBar", "Silent");
                mAlarmType = SILENT;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {

            if (event.values[0] == 0.0) {
                if (!isAliveService(this)) {
                    Intent intent = new Intent(this, StoopAlarmService.class);
                    intent.putExtra("STATE", mAlarmType);
                    startService(intent);
                }
            }
        }
    }

    private Boolean isAliveService(Context context) {
        // ActivityManager 객체를 이용해 현재 시스템에서 돌고있는 서비스들의 정보를 가져온다.
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        // 현재 시스템에서 돌고있는 서비스들 중에 MusicService가 있다면 true를 반환한다.
        for (ActivityManager.RunningServiceInfo rsi : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (StoopAlarmService.class.getName().equals(rsi.service.getClassName())) {
                return true;
            }
        }
        // 그렇지 않다면 false를 반환한다.
        return false;
    }

    public void onclick(View v) {

        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
    }
}