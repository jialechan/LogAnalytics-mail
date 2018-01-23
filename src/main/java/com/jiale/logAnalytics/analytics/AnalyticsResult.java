package com.jiale.logAnalytics.analytics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.*;

public class AnalyticsResult implements Serializable {

    private static Logger log = LoggerFactory.getLogger(AnalyticsResult.class);

    private Map<String, Long> accessResult = initAccessResult();
    private Map<String, Long> callResult = new HashMap<>();
    private Map<String, Long> consumeAvgResult = new HashMap<>();
    private Map<String, Long> consumeTotalResult = new HashMap<>();
    private Map<String, Long> httpStatusResult = new HashMap<>();

    private int totalLineScan = 0;
    private int totalAccess = 0;

    private Map<String, Long> initAccessResult() {
        final Map<String, Long> result = new LinkedHashMap<>();
        DecimalFormat format = new DecimalFormat("00");
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 60; j++) {
                String key = format.format(i) + ":" + format.format(j) + ":00";
                result.put(key, 0L);
            }
        }
        return result;
    }

    public Map<String, Long> getAccessResult() {
        return accessResult;
    }

    public void setAccessResult(Map<String, Long> accessResult) {
        this.accessResult = accessResult;
    }

    public Map<String, Long> getCallResult() {
        return callResult;
    }

    public void setCallResult(Map<String, Long> callResult) {
        this.callResult = callResult;
    }

    public Map<String, Long> getConsumeAvgResult() {
        return consumeAvgResult;
    }

    public void setConsumeAvgResult(Map<String, Long> consumeAvgResult) {
        this.consumeAvgResult = consumeAvgResult;
    }

    public int getTotalAccess() {
        return totalAccess;
    }

    public void setTotalAccess(int totalAccess) {
        this.totalAccess = totalAccess;
    }

    public static Logger getLog() {
        return log;
    }

    public static void setLog(Logger log) {
        AnalyticsResult.log = log;
    }

    public int getTotalLineScan() {
        return totalLineScan;
    }

    public void setTotalLineScan(int totalLineScan) {
        this.totalLineScan = totalLineScan;
    }

    public Map<String, Long> getConsumeTotalResult() {
        return consumeTotalResult;
    }

    public void setConsumeTotalResult(Map<String, Long> consumeTotalResult) {
        this.consumeTotalResult = consumeTotalResult;
    }

    public Map<String, Long> getHttpStatusResult() {
        return httpStatusResult;
    }

    public void setHttpStatusResult(Map<String, Long> httpStatusResult) {
        this.httpStatusResult = httpStatusResult;
    }
}


