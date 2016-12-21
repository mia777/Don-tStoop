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
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "Main";

    /* 알림 소리 설정관련 상수 */
    private final int VIBRATE = 0;
    private final int SOUND = 1;
    private final int SILENT = 2;

    // 현재 알람 설정
    private int mAlarmType = SOUND;

    // 근접센서 사용에 관련된 변수
    private SensorManager mSensorManager;
    private Sensor mProximitySensor;

    // 설명 이미지
    private ImageView mIntroImg;
    private int[] mResult;  // GraphActivity에 전달할 값, 여기서는 무의미한 값을 보내므로 0으로 초기화

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 설명 이미지 가져오기
        mIntroImg = (ImageView)findViewById(R.id.black);

        // 근접 센서 관련 객체 생성
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // 결과 배열 객체 생성 및 초기화
        // 무의미한 값이므로 0으로 초기화
        mResult = new int[24];
        for(int i = 0; i < 24; i++) {
            mResult[i] = 0;
        }

        // firstRun.txt파일은 어플 최초 실행시 생성됨.
        // 그러므로 firstRun.txt파일이 없다는 것은 어플을 최초 실행한 것이므로
        // 설명 이미지를 표시함.
        try{
            openFileInput("firstRun.txt");
        }catch (IOException e){
            e.printStackTrace();
            mIntroImg.setVisibility(View.VISIBLE);
        }
    }

    // 설명 이미지를 터치하면 설명이미지가 사라짐.
    @Override
    public boolean onTouchEvent(MotionEvent event){
        super.onTouchEvent(event);
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            // 이후 어플을 최초 실행했다는 것을 알리기 위해 'firstRun.txt'파일을 생성
            try {
                FileOutputStream fosStream = openFileOutput("firstRun.txt", Context.MODE_PRIVATE);
                fosStream.write("First run.".getBytes());
                fosStream.close();
            } catch (IOException firstRunE) {
                firstRunE.printStackTrace();
            }
            mIntroImg.setVisibility(View.INVISIBLE);
        }
        return true;
    }

    // 옵션메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // 옵션 아이템 선택 핸들러 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.vibe:
                Log.d(TAG, "Viberate");
                mAlarmType = VIBRATE;
                Toast.makeText(this, "진동",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nm:
                Log.d(TAG, "Sound");
                mAlarmType = SOUND;
                Toast.makeText(this, "소리",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.sl:
                Log.d(TAG, "Silent");
                mAlarmType = SILENT;
                Toast.makeText(this, "무음",Toast.LENGTH_SHORT).show();
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

    // 근접 센서값이 0.0(근접)이면 StoopAlarmService를 실행
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] == 0.0) {
                if (!isAliveService(this)) {
                    Intent intent = new Intent(this, StoopAlarmService.class);
                    intent.putExtra("STATE", mAlarmType); // 현재 알람 설정값을 넘겨줌.
                    startService(intent);
                }
            }
        }
    }

    private Boolean isAliveService(Context context) {
        // ActivityManager 객체를 이용해 현재 시스템에서 돌고있는 서비스들의 정보를 가져온다.
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        // 현재 시스템에서 돌고있는 서비스들 중에 SttopAlarmService가 있다면 true를 반환한다.
        for (ActivityManager.RunningServiceInfo rsi : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (StoopAlarmService.class.getName().equals(rsi.service.getClassName())) {
                return true;
            }
        }
        // 그렇지 않다면 false를 반환한다.
        return false;
    }

    // 밀어서 통계보기 버튼의 핸들러
    public void onclick(View v) {
        Intent intent = new Intent(this, GraphActivity.class);
        intent.putExtra("TODAY_RESULT",mResult);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
    }
}