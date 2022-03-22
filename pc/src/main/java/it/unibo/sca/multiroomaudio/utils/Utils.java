package it.unibo.sca.multiroomaudio.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class Utils {

    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }

    public static LinkedHashMap<String, Double> sortHashMapByValueAsc(Map<String, Double> map) {
        LinkedHashMap<String, Double> result = new LinkedHashMap<>();
        map.entrySet().stream()
                    .sorted((e1, e2) -> -(e2.getValue()).compareTo(e1.getValue()))
                    .forEach((entry) -> result.put(entry.getKey(), entry.getValue()));
        return result;
    }

    public static void getHTTPRequest(String urlString){
        try {
            InputStream response = new URL(urlString).openStream();
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
