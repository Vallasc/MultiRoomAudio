package it.unibo.sca.multiroomaudio.step_count;

import static android.content.Context.SENSOR_SERVICE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.util.Log;

import it.unibo.sca.multiroomaudio.JavascriptBindings;
import it.unibo.sca.multiroomaudio.WifiHandler;

public class OfflinePhaseManager extends BroadcastReceiver implements SensorEventListener, StepListener {

    private static final String TAG = OfflinePhaseManager.class.getCanonicalName();

    private final WifiHandler wifiHandler;
    private final Context context;
    private final StepDetector stepDetector;

    private final SensorManager sensorManager;
    private final Sensor sensorGravity;

    private int stepCount;


    public OfflinePhaseManager(Context context){

        this.context = context;
        this.wifiHandler = new WifiHandler();

        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //TODO controllare  sensorGravity == null

        // listen to these sensors
        sensorManager.registerListener(this, sensorGravity, SensorManager.SENSOR_DELAY_FASTEST);
        stepCount = 0;

        stepDetector = new StepDetector();
        stepDetector.registerListener(this);
    }


    public void setRoomName(String name){

    }

    private void setReferencePoint(){ //TODO

    }

    public void sendToSLAC(){

    }

    public void clear(){
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        // Walk detected
        if(intent.getAction() == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
            setReferencePoint(); //TODO
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            stepDetector.updateAccel(event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        stepCount++;
        JavascriptBindings.getInstance().setText("Steps: " + stepCount);
    }

    public void onResume(){
        // Register for WiFi scan result
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(this, filter);

        // Register listener for step detector
        if(sensorGravity != null) {
            sensorManager.registerListener(this, sensorGravity, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Log.d(TAG, "No sensor gravity found");
            // TODO implmentare con intervalli di tempo
        }
    }

    public void onPause() {
        context.unregisterReceiver(this);
        if(sensorGravity != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

}
