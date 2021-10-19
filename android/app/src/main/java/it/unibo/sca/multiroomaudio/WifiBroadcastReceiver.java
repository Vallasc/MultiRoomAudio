package it.unibo.sca.multiroomaudio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WifiBroadcastReceiver extends BroadcastReceiver {

    private WifiHandler wifiHandler;

    WifiBroadcastReceiver(WifiHandler WifiHandler) {
        this.wifiHandler = WifiHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        wifiHandler.sendListOfAP(context);
    }
}
