package it.unibo.sca.multiroomaudio;

import org.cef.CefApp;

import it.unibo.sca.multiroomaudio.shared.JavascriptBindings;
import it.unibo.sca.multiroomaudio.ui.UI;

public class Main {
    public static void main(String[] args) {

        CefApp.startup(args);
        new UI("MultiRoomAudio", new JavascriptBindings(), "JSInterface");
    }
}