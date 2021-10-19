package com.giacomovallorani.multiroomaudio;

import android.app.Activity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class JavaScriptInterface {
    private static final String tag = JavaScriptInterface.class.getCanonicalName();
    private Activity activity;
    private WifiHandler wifiHandler;
    private WebView webView;

    public JavaScriptInterface(Activity activity, WifiHandler wifiHandler, WebView webView) {
        this.activity = activity;
        this.wifiHandler = wifiHandler;
        this.webView = webView;
    }

    @JavascriptInterface
    public void scanWifi(){
        Log.d(tag, "Call scanWifi");
        wifiHandler.startScan(activity);
    }

    public void callJs(String funCall){
        webView.post(() -> {
            // Note that the JS method name to be invoked corresponds to
            // Call the callJS() method of javascript
            webView.loadUrl("javascript:" + funCall);
        });
    }
}
