package com.jiale.logAnalytics.analytics;

import com.alibaba.fastjson.JSON;

public class LineInfo {

    private String time;
    private String stat;
    private Integer requestTime;
    private String uri;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public Integer getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Integer requestTime) {
        this.requestTime = requestTime;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}