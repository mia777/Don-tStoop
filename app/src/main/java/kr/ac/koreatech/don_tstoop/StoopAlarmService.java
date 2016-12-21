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

    /* 알림 소리 설정관련 상수 */
    private final int VIBRATE = 0;
    private final int SOUND = 1;
    private final int SILENT = 2;

    /* 허리 숙임 감지에 관련된 상수 및 변수 */
    private final float LIMIT_VALUE = 1.f; // 허리 숙임 정도의 허용 범위
    private final int INIT_TIME = 4000; // 초기화 시간
    private final int DETECTION_TIME = 2000; // 허리 숙임 감지 시간

    private float mReferenceValue; // 허리 숙임 정도의 기준값

    // 감지 시작 시각 및 갱신 되는 시각
    private long mLastTime, mStartTime;
    private boolean mInitFinished = false;  // 초기화 완료 여부

    // 진동 패턴
    private final long[] INIT_VIB_PATTERN = {50, 200, 50, 200};
    private final long[] DETECT_VIB_PATTERN = {100, 200, 100, 200};

    // 초기화 완료 사운드 및 감지 알람 사운드 재생을 위한 미디어 플레이어 객체
    private MediaPlayer mInitPlayer, mAlarmPlayer;
    private Vibrator mVibrator; // 진동 객체

    // 센서 관련 변수
    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor, mProximitySensor;
    private AccelerometerSensorListener mAccelerometerSensorListener;
    private ProximitySensorListener mProximitySensorListener;

    // 현재 시각
    private int mCurrentTime;
    private int mAlarmType =0; // 현재 소리 알람 타입 (소리, 진동, 무음)
    private int[] mResult; // 감지된 횟수

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

        // 알람소리 및 초기화 완료 사운드 재생을 위한 미디어플레이어 객체 생성
        mInitPlayer = new MediaPlayer();
        mAlarmPlayer = new MediaPlayer();

        try {
            // 초기화 완료 안내 사운드 불러옴. res/raw/init.wav
            AssetFileDescriptor afd = this.getResources().openRawResourceFd(R.raw.init);
            mInitPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            mInitPlayer.setLooping(false);  // 반복여부 지정
            mInitPlayer.prepare();    // 실행전 준비
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 현재 설정된 시스템 알람 사운드 받아옴.
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
                .setContentText("자세 검사중...")
                .setSmallIcon(R.mipmap.ic_noti)
                .setContentIntent(pIntent)
                .build();

        startForeground(123, noti);
        //****************************************
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        Toast.makeText(this, "StoopAlarmService 시작", Toast.LENGTH_SHORT).show();

        // 센서매니저에 가속도 센서 및 근접 센서 리스너 등록
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

        // 현재 소리 설정이 무음이 아니면 초기 실행을 알려주는 진동을 한다.
        if(mAlarmType != SILENT)
            mVibrator.vibrate(INIT_VIB_PATTERN, -1);

        // 최초 실행 시각 설정
        mStartTime = System.currentTimeMillis();

        return START_STICKY;
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        Toast.makeText(this, "StoopAlarmService 중지", Toast.LENGTH_SHORT).show();

        // 센서매니저에 등록된 가속도 센서 및 근접 센서 리스너 해제
        mSensorManager.unregisterListener(mAccelerometerSensorListener, mAccelerometerSensor);
        mSensorManager.unregisterListener(mProximitySensorListener, mProximitySensor);

        // 초기화 안내 멘트 및 자세 교정 알림 사운드를 위한 미디어 플레이어 객체 정리
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

        // 1. 서비스가 실행된 4초동안은 아무일도 하지 않는다. (사용자가 핸드폰을 등에 바르게 부착하기 위하여)
        // 2. 4초 ~ 8초동안 사용자 자세를 바탕으로 기준값을 설정한다.
        // 3. 8초~ 이후에는 사용자가 허리를 굽었을 때 알람을 주고 해당 시각 인덱스의 mResult 값을 1증가 시킨다.
        public void onSensorChanged(SensorEvent event) {
            // 가속도 센서값.
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                long currentTime = System.currentTimeMillis(); // 변화되었을 때 시각.
                long detectionTime = (currentTime - mLastTime); // 감지 간격
                long InitTime = (currentTime - mStartTime); // 초기화 간격

                // 현재시각 계산
                // 결과값을 저장하는 배열은 24개의 원소를 가지고 있다.
                // 각 인덱스는 0시부터 23시의 시각을 나타낸다.
                // 허리 굽힘이 감지가 되면, 그때의 시각을 인덱스로 mResult값을 1증가한다.
                SimpleDateFormat dateFormat = new  SimpleDateFormat("HH", java.util.Locale.getDefault());
                Date date = new Date();
                String strDate = dateFormat.format(date);
                mCurrentTime = Integer.parseInt(strDate);
                // Log.d(TAG, "현재시각: "+mCurrentTime);

                if (InitTime < INIT_TIME) {
                }
                else if (InitTime < INIT_TIME * 2) {
                    // 사용자의 초기 허리굽힘 정도를 바탕으로 기준값을 설정한다.
                    mReferenceValue += event.values[2];
                    mReferenceValue /= 2;
                    if(mAlarmType == SOUND)
                        Log.d(TAG, "가속 Z: " + event.values[2]);
                    // Log.d(TAG, "기준값: " + mReferenceValue);
                } else {
                    // 8초가 지나면 초기화가 완료되었다는 것을 사용자에게 안내 메세지와 진동을 통해 알려준다.
                    if(!mInitFinished) {
                        if(mAlarmType != SILENT)
                            mVibrator.vibrate(DETECT_VIB_PATTERN, -1);
                        if(mAlarmType == SOUND)
                            mInitPlayer.start();
                        mInitFinished = true;
                    }
                    // 사용자가 허리를 굽히면 알림을 한다.
                    // 단, 이전 감지와 현재 감지 사이의 시간이 2초 이상일 때 알린다.
                    else if (event.values[2] < (mReferenceValue - LIMIT_VALUE)) {
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

            // 근접 센서의 값이 0보다 크면(far) mResult값을 GraphActivity에 넘겨주며 실행한다.
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

