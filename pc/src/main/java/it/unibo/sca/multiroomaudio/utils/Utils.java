package it.unibo.sca.multiroomaudio.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import it.unibo.sca.multiroomaudio.shared.model.ScanResult;

public class Utils {

    /**
     * Sleep thread
     * @param milliseconds milliseconds of sleep duration
     */
    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }

    /**
     * Sort hashmap by values in ascending order
     */
    public static LinkedHashMap<String, Double> sortHashMapByValueAsc(Map<String, Double> map) {
        LinkedHashMap<String, Double> result = new LinkedHashMap<>();
        map.entrySet().stream()
                    .sorted((e1, e2) -> -(e2.getValue()).compareTo(e1.getValue()))
                    .forEach((entry) -> result.put(entry.getKey(), entry.getValue()));
        return result;
    }

    /**
     * Do a GET HTTP request
     * @param urlString url of the request
     */
    public static void getHTTPRequest(String urlString){
        try {
            InputStream response = new URL(urlString).openStream();
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compute mean of signal power
     * @param list list of scans
     * @return mean power
     */
    static private double mean(List<ScanResult> list){
        double mean = 0;
        for(var scan : list)
            mean += scan.getSignal();
        mean = mean / list.size();
        return mean;
    }

    /**
     * Compute standard deviation of signal power
     * @param list list of scans
     * @param mean mean signal power
     * @return standard deviation
     */
    static private double stddev(List<ScanResult> list, double mean){
        double der = 0;
        for(var scan : list){
            der += Math.pow(scan.getSignal() - mean, 2);
        }
        double stddev = Math.sqrt(der/list.size());
        if(stddev == 0)
            stddev = 0.005;
        return stddev;
    }

    /**
     * Aggregate fingerprints computing a mean of signals powers
     * @param scans List of scans
     * @return Aggregated list of scans
     */
    static public List<ScanResult> computeMeanFingeprint(List<ScanResult> scans){
        Map<String, List<ScanResult>> signals = new HashMap<>(); // List of all the signals strength for the same ap in the same scan
        for(var scanResult : scans){
            List<ScanResult> listSignals = signals.get(scanResult.getBSSID());
            if(listSignals == null){
                listSignals = new ArrayList<>();
                signals.put(scanResult.getBSSID(), listSignals);
            }
            listSignals.add(scanResult);
        }
        List<ScanResult> outResults = new ArrayList<>();
        for(var entry : signals.entrySet()){
            // Calc mean signal
            double mean = mean(entry.getValue());
            if(mean > GlobalState.getInstance().getCutPower()){
                double stddev = stddev(entry.getValue(), mean);
                ScanResult result = entry.getValue().get(0);
                result = result.cloneWith(mean, stddev);
                outResults.add(result);
            }
        }
        return outResults;
    }
}
