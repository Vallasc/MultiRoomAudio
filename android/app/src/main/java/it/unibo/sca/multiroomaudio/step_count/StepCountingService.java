package it.unibo.sca.multiroomaudio.step_count;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class StepCountingService extends Service implements SensorEventListener {

    private static final String TAG = StepCountingService.class.getCanonicalName();
    private static final int STEPS_THRESHOLD = 2;
    public static final String ACTION_DETECTED_STEPS = "com.unibo.sca.StepCountingService";

    // For Intent build
    // Int Type
    private static final String DETECTED_STEPS = "DetectedSteps";

    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;

    private int currentStepsDetected;
    private boolean serviceStopped;
    private Intent intent;

    // Service is being created
    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(ACTION_DETECTED_STEPS);
    }

    // startService() starts service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if(stepDetectorSensor != null) {
            sensorManager.registerListener(this, stepDetectorSensor, 0);

            currentStepsDetected = 0;

            //stepCounter = 0;
            //newStepCounter = 0;

            serviceStopped = false;

            // Existing callbacks to the handler are removed
            //handler.removeCallbacks(updateBroadcastData);
            // handler call
            //handler.post(updateBroadcastData);
        } else {
            serviceStopped = true;
            Log.d(TAG, "No step detector found");
        }

        return START_STICKY;
    }

    // Binding the service
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Service is being destroyed when not in use
    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceStopped = true;
    }

    // system is running low on memory,actively running processes should reduce their memory usage.
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        // Step detector sensor
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            int detectSteps = (int) event.values[0];
            currentStepsDetected += detectSteps;
            if( currentStepsDetected % STEPS_THRESHOLD == 0) {
                broadcastSensorValue();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //Update data by broadcasting
    /*
    private Runnable updateBroadcastData = new Runnable() {
        public void run() {
            if (!serviceStopped) {
                // Broadcast data to the Activity
                broadcastSensorValue();
                handler.postDelayed(this, 1000);
            }
        }
    };*/

    // Broadcast data through intent
    private void broadcastSensorValue() {
        Log.d(TAG, "Send broadcast steps");
        // add step detector to intent.
        intent.putExtra(DETECTED_STEPS, currentStepsDetected);
        // sendBroadcast sends a message to whoever is registered to receive it.
        sendBroadcast(intent);
    }
}