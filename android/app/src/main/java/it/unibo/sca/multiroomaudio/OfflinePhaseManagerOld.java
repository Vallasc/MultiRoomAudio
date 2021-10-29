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

public class OfflinePhaseManagerOld extends BroadcastReceiver implements SensorEventListener {

    private static final String TAG = OfflinePhaseManagerOld.class.getCanonicalName();
    private static final int STEPS_THRESHOLD = 2;

    private final WifiHandler wifiHandler;
    private final Context context;

    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;

    private StringBuilder sb = new StringBuilder();

    private int currentStepsDetected;


    public OfflinePhaseManagerOld(Context context){
        this.context = context;
        this.wifiHandler = new WifiHandler(context);

        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        currentStepsDetected = 0;
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
    public void onSensorChanged(SensorEvent sensorEvent) {

        // Step detector sensor
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            int detectSteps = (int) sensorEvent.values[0];
            currentStepsDetected += detectSteps;
            //if( currentStepsDetected % STEPS_THRESHOLD == 0) {
                Log.d(TAG, "Num passi " + currentStepsDetected);
                sb.append("Num passi " + currentStepsDetected + "<br>");
                JavascriptBindings.getInstance().setText(sb.toString());
                //wifiHandler.startScan(context);
            //}
        }
    }

    public void onResume(){
        // Register for WiFi scan result
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(this, filter);

        // Register listener for step detector
        if(stepDetectorSensor != null) {
            sensorManager.registerListener(this, stepDetectorSensor, 0);
        } else {
            Log.d(TAG, "No step detector found");
            // TODO implementare con intervalli di tempo
        }
    }

    public void onPause() {
        context.unregisterReceiver(this);
        if(stepDetectorSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
