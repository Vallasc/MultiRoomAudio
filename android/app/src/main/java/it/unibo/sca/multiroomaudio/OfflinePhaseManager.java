package it.unibo.sca.multiroomaudio;

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

import java.util.ArrayList;
import java.util.List;

import it.unibo.sca.multiroomaudio.shared.dto.Fingerprint;
import it.unibo.sca.multiroomaudio.step_count.StepDetector;
import it.unibo.sca.multiroomaudio.step_count.StepListener;

public class OfflinePhaseManager extends BroadcastReceiver implements SensorEventListener, StepListener {

    private static final String TAG = OfflinePhaseManager.class.getCanonicalName();

    private final WifiHandler wifiHandler;
    private final Context context;
    private final StepDetector stepDetector;

    private final SensorManager sensorManager;
    private final Sensor sensorGravity;

    private int stepCount;

    private String roomName;
    private List<Fingerprint> fingerprints;


    public OfflinePhaseManager(Context context){

        this.context = context;
        this.wifiHandler = new WifiHandler(context);

        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //TODO controllare  sensorGravity == null

        // listen to these sensors
        sensorManager.registerListener(this, sensorGravity, SensorManager.SENSOR_DELAY_FASTEST);

        stepDetector = new StepDetector();
        stepDetector.registerListener(this);
        clear();
    }


    public void setRoomName(String name){
        this.roomName = name;
    }

    public void setReferencePoint(){
        wifiHandler.startScan();
    }

    public void saveRoom(){
        if(roomName != null){
            //TODO
        }
        clear();
    }

    public void clear(){
        stepCount = 0;
        roomName = null;
        fingerprints = new ArrayList<>();
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        // Walk detected
        if(intent.getAction() == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
            Fingerprint fingerprint = wifiHandler.getFingerprint();
            fingerprint.setId(String.valueOf(fingerprints.size()));
            fingerprints.add(fingerprint);
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
            Log.d(TAG, "No accelerometer found");
            // TODO implementare con intervalli di tempo
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
