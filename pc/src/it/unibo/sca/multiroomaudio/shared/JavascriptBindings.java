package it.unibo.sca.multiroomaudio.shared;

import it.unibo.sca.multiroomaudio.ui.JavascriptInterface;

public class JavascriptBindings {
    // Javascript non supporta overloading => NON FARE metodi con lo stesso nome 

    @JavascriptInterface
    public boolean saveReferencePoint(){
        System.out.println("Save ref");
        return true;
    }

}
