package it.unibo.sca.multiroomaudio.shared;

import android.webkit.JavascriptInterface;

import it.unibo.sca.multiroomaudio.WifiHandler;

public class JavascriptBindings {
    // Javascript non supporta overloading => NON FARE metodi con lo stesso nome 

    public JavascriptBindings(WifiHandler wifiHandler){

    }
    @JavascriptInterface
    public boolean saveReferencePoint(){
        System.out.println("Save ref");
        return true;
    }

}
