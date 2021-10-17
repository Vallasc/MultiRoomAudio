package main;

import org.cef.CefApp;

import main.ui.UI;

public class Main {
    public static void main(String[] args) {

        CefApp.startup(args);
        new UI("MultiRoomAudio", JavascriptBindings.class);
    }
}