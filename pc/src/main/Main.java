package main;

import org.cef.CefApp;

public class Main {
    public static void main(String[] args) {

        CefApp.startup(args);
        new UI();
    }
}