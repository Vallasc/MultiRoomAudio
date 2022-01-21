package it.unibo.sca.multiroomaudio.utils;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.lang3.SystemUtils;

public class Desktop {

    public static void browse(URI uri) {

        if (isWindows()) {
            browseWindows(uri);
        } else if (isLinux()) {
            browseLinux(uri);
        } else if (isMac()) {
            browseMac(uri);
        }
    }

    private static void browseWindows(URI uri){
        try {
            Runtime.getRuntime().exec("start " + uri.toASCIIString());
        } catch (IOException e) {}
    }
    private static void browseLinux(URI uri){
        try {
            Runtime.getRuntime().exec("xdg-open " + uri.toASCIIString());
        } catch (IOException e) {}
    }
    private static void browseMac(URI uri){
        try {
            Runtime.getRuntime().exec("open " + uri.toASCIIString());
        } catch (IOException e) {}
    }

    private static boolean isWindows(){
        return SystemUtils.IS_OS_WINDOWS;
    }

    private static boolean isLinux(){
        return SystemUtils.IS_OS_LINUX;
    }

    private static boolean isMac(){
        return SystemUtils.IS_OS_MAC;
    }

    private Desktop() {
    }
}