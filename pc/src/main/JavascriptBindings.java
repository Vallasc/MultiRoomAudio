package main;

import main.ui.JavascriptInterface;

public class JavascriptBindings {
    // Javascript non supporta overloading => NON FARE metodi con lo stesso nome 

    @JavascriptInterface
    public String print(){
        return "Ciao da vallasc";
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
