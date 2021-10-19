package it.unibo.sca.multiroomaudio.shared;

import it.unibo.sca.multiroomaudio.ui.JavascriptInterface;

public class JavascriptBindings {
    // Javascript non supporta overloading => NON FARE metodi con lo stesso nome 

    @JavascriptInterface
    public String printHello(){
        return "Ciao da Java";
    }

    @JavascriptInterface
    public String print2(String text1, int prova){
        return text1 + " " + prova;
    }

    @JavascriptInterface
    public int print3(int a, int b){
        return a + b;
    }
}
