package it.unibo.sca.multiroomaudio;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class JavascriptBindings {
    private static final String TAG = JavascriptBindings.class.getCanonicalName();
    private static JavascriptBindings obj= new JavascriptBindings();

    private OfflinePhaseManager offlinePhaseManager;
    private WebView view;

    private JavascriptBindings(){}

    public static JavascriptBindings getInstance(){
        return obj;
    }

    void setOfflinePhaseManager(OfflinePhaseManager offlinePhaseManager){
        this.offlinePhaseManager = offlinePhaseManager;
    }

    void setWebView(WebView view){
        this.view = view;
    }


    @JavascriptInterface
    public void setRoomName(String name){
        offlinePhaseManager.setRoomName(name);
    }

    @JavascriptInterface
    public void saveReferencePoint(){
        offlinePhaseManager.setReferencePoint();
    }

    @JavascriptInterface
    public void saveRoom(){
        System.out.println("Save ref");
    }

    public void setReferencePointList(String text){
        callJavaScript("setText", text);
    }

    public void setText(String text){
        callJavaScript("setText", text);
    }

    private void callJavaScript(String methodName, Object...params){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("javascript:try{");
        stringBuilder.append(methodName);
        stringBuilder.append("(");
        String separator = "";
        for (Object param : params) {
            stringBuilder.append(separator);
            separator = ",";
            if(param instanceof String){
                stringBuilder.append("'");
            }
            stringBuilder.append(param.toString().replace("'", "\\'"));
            if(param instanceof String){
                stringBuilder.append("'");
            }

        }
        stringBuilder.append(")}catch(error){console.error(error.message);}");
        final String call = stringBuilder.toString();
        Log.i(TAG, "callJavaScript: call="+call);

        view.loadUrl(call);
    }

}
