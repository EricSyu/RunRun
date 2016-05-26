package homework6.group.runrun;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PaceService extends Service implements SensorEventListener {

    private PaceDB paceDB;

    private SensorManager mSensorManager;

    public static int CURRENT_SETP = 0;
    public static float SENSITIVITY = 10;
    private float mLastValues[] = new float[3 * 2];
    private float mScale[] = new float[2];
    private float mYOffset;
    private static long end = 0;
    private static long start = 0;

    private float mLastDirections[] = new float[3 * 2];
    private float mLastExtremes[][] = { new float[3 * 2], new float[3 * 2] };
    private float mLastDiff[] = new float[3 * 2];
    private int mLastMatch = -1;

    public PaceService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        paceDB = new PaceDB(getApplicationContext());

        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        registerSensor();

        updateNotification();
        updateDataBasePace();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                float vSum = 0;
                for (int i = 0; i < 3; i++) {
                    final float v = mYOffset + event.values[i] * mScale[1];
                    vSum += v;
                }
                int k = 0;
                float v = vSum / 3;

                float direction = (v > mLastValues[k] ? 1
                        : (v < mLastValues[k] ? -1 : 0));
                if (direction == -mLastDirections[k]) {
                    int extType = (direction > 0 ? 0 : 1);

                    mLastExtremes[extType][k] = mLastValues[k];
                    float diff = Math.abs(mLastExtremes[extType][k]
                            - mLastExtremes[1 - extType][k]);

                    if (diff > SENSITIVITY) {
                        boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                        boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                        boolean isNotContra = (mLastMatch != 1 - extType);

                        if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough
                                && isNotContra) {
                            end = System.currentTimeMillis();
                            if (end - start > 500) {

                                updateDataBasePace();
                                updateNotification();

                                mLastMatch = extType;
                                start = end;
                            }
                        } else {
                            mLastMatch = -1;
                        }
                    }
                    mLastDiff[k] = diff;
                }
                mLastDirections[k] = direction;
                mLastValues[k] = v;
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void updateDataBasePace(){
        SimpleDateFormat dateStringFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        Date date=new Date();
        String dataStr = dateStringFormat.format(date);

        PaceData paceData = paceDB.queryDate(dataStr);
        if(paceData != null){
            paceData.setPace(paceData.getPace() + 1);
            paceDB.modify(paceData);
        }
        else{
            PaceData item = new PaceData(dataStr, 0);
            paceDB.insert(item);
        }
    }

    private void updateNotification(){
        SimpleDateFormat dateStringFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        Date date=new Date();
        String dataStr = dateStringFormat.format(date);

        PaceData paceData = paceDB.queryDate(dataStr);
        int pace = 0;
        if(paceData != null){
            pace = paceData.getPace();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.shoe)
                .setContentTitle("今日累計步數")
                .setContentText(pace + " 步")
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(1, notification);
    }

    private  void registerSensor(){
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void unregisterSensor(){
        mSensorManager.unregisterListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterSensor();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
