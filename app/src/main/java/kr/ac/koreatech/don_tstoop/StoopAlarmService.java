package kr.ac.koreatech.don_tstoop;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StoopAlarmService extends Service {

    private static final String TAG = "StoopAlarmService";
    private final int VIBRATE = 0;
    private final int SOUND = 1;
    private final int SILENT = 2;
    private final float LIMIT_VALUE = 1.f;
    private final int INIT_TIME = 4000;
    private final int DETECTION_TIME = 2000;
    private final long[] INIT_VIB_PATTERN = {50, 200, 50, 200};
    private final long[] DETECT_VIB_PATTERN = {100, 200, 100, 200};

    private long mLastTime, mStartTime;
    private float mReferenceValue;
    private boolean mInitFinished = false;
    private MediaPlayer mInitPlayer, mAlarmPlayer;

    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor, mProximitySensor;
    private AccelerometerSensorListener mAccelerometerSensorListener;
    private ProximitySensorListener mProximitySensorListener;
    private Vibrator mVibrator;

    private int mCurrentTime;
    private int mAlarmType =0;
    private int[] mResult;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");

        // 가속도 센서, 근접 센서 관련 객체 생성
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mAccelerometerSensorListener = new AccelerometerSensorListener();
        mProximitySensorListener = new ProximitySensorListener();

        // 결과 배열 객체 생성
        mResult = new int[24];
        mInitPlayer = new MediaPlayer();
        mAlarmPlayer = new MediaPlayer();

        try {
            AssetFileDescriptor afd = this.getResources().openRawResourceFd(R.raw.init);
            mInitPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            mInitPlayer.setLooping(false);  // 반복여부 지정
            mInitPlayer.prepare();    // 실행전 준비
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
            mAlarmPlayer.setDataSource(this, alert);

            mAlarmPlayer.setLooping(false);  // 반복여부 지정
            mAlarmPlayer.prepare();    // 실행전 준비
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 진동 객체 생성
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //***************************************
        // Service를 Foreground로 실행하기 위한 과정
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification noti = new Notification.Builder(this)
                .setContentTitle("Don-tStoop")
                .setContentText("자세검사중...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .build();

        startForeground(123, noti);
        //****************************************
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        Toast.makeText(this, "StoopAlarmService 시작", Toast.LENGTH_SHORT).show();

        mSensorManager.registerListener(mAccelerometerSensorListener, mAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mProximitySensorListener, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Main_Activity로 부터 진동, 소리, 무음 설정을 받아온다.
        try {
            mAlarmType = intent.getIntExtra("STATE", SOUND);
        } catch(Exception e) {
            Log.d(TAG, "설정값을 받아오는데 오류가 생김.");
            mAlarmType = VIBRATE;
            e.printStackTrace();
        }

        if(mAlarmType != SILENT)
            mVibrator.vibrate(INIT_VIB_PATTERN, -1);

        mStartTime = System.currentTimeMillis();

        return START_STICKY;
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        Toast.makeText(this, "StoopAlarmService 중지", Toast.LENGTH_SHORT).show();
        mSensorManager.unregisterListener(mAccelerometerSensorListener, mAccelerometerSensor);
        mSensorManager.unregisterListener(mProximitySensorListener, mProximitySensor);

        if(mInitPlayer.isPlaying())
            mInitPlayer.stop();
        mInitPlayer.reset();
        mInitPlayer.release();
        mInitPlayer = null;

        if(mAlarmPlayer.isPlaying())
            mAlarmPlayer.stop();
        mAlarmPlayer.reset();
        mAlarmPlayer.release();
        mAlarmPlayer = null;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class AccelerometerSensorListener implements SensorEventListener {

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                long currentTime = System.currentTimeMillis();
                long detectionTime = (currentTime - mLastTime);
                long InitTime = (currentTime - mStartTime);

                // 현재시각 계산
                SimpleDateFormat dateFormat = new  SimpleDateFormat("HH", java.util.Locale.getDefault());
                Date date = new Date();
                String strDate = dateFormat.format(date);
                mCurrentTime = Integer.parseInt(strDate);
                // Log.d(TAG, "현재시각: "+mCurrentTime);

                if (InitTime < INIT_TIME) {
                }
                else if (InitTime < INIT_TIME * 2) {
                    mReferenceValue += event.values[2];
                    mReferenceValue /= 2;
                    if(mAlarmType == SOUND)
                        Log.d(TAG, "가속 Z: " + event.values[2]);
                    // Log.d(TAG, "기준값: " + mReferenceValue);
                } else {
                    if(!mInitFinished) {
                        if(mAlarmType != SILENT)
                            mVibrator.vibrate(DETECT_VIB_PATTERN, -1);
                        if(mAlarmType == SOUND)
                            mInitPlayer.start();
                        mInitFinished = true;
                    }
                    if (event.values[2] < (mReferenceValue - LIMIT_VALUE)) {
                        if (detectionTime > DETECTION_TIME) {
                            if (mAlarmPlayer.isPlaying()) {
                                try {
                                    mAlarmPlayer.stop();
                                    mAlarmPlayer.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            mLastTime = currentTime;
                            if (mAlarmType != SILENT)
                                mVibrator.vibrate(500);
                            if (mAlarmType == SOUND)
                                mAlarmPlayer.start();

                            mResult[mCurrentTime]++;
                            //Log.d(TAG, "가속 Z: " + event.values[2]);
                            //Log.d(TAG, "기준: " + (mReferenceValue - LIMIT_VALUE));
                            Log.d(TAG, "시각: " + mCurrentTime + " 횟수: " + mResult[mCurrentTime]);
                        }
                    }
                }
            }
        }
    }

    private class ProximitySensorListener implements SensorEventListener {

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
                Log.d(TAG, "근접: "+event.values[0]);
                if(event.values[0] > 0.0) {
                    Intent intent = new Intent(StoopAlarmService.this, GraphActivity.class);
                    intent.putExtra("TODAY_RESULT",mResult);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    stopSelf();
                }
            }
        }
    }
}

