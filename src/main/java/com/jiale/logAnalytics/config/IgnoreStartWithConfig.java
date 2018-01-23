package com.jiale.logAnalytics.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix="ignoreStartWith")
public class IgnoreStartWithConfig {

    private List<String> accessIgnoreStartWithList;
    private List<String> callIgnoreStartWithList;
    private List<String> consumeIgnoreStartWithList;

    public List<String> getAccessIgnoreStartWithList() {
        return accessIgnoreStartWithList;
    }

    public void setAccessIgnoreStartWithList(List<String> accessIgnoreStartWithList) {
        this.accessIgnoreStartWithList = accessIgnoreStartWithList;
    }

    public List<String> getCallIgnoreStartWithList() {
        return callIgnoreStartWithList;
    }

    public void setCallIgnoreStartWithList(List<String> callIgnoreStartWithList) {
        this.callIgnoreStartWithList = callIgnoreStartWithList;
    }

    public List<String> getConsumeIgnoreStartWithList() {
        return consumeIgnoreStartWithList;
    }

    public void setConsumeIgnoreStartWithList(List<String> consumeIgnoreStartWithList) {
        this.consumeIgnoreStartWithList = consumeIgnoreStartWithList;
    }
}


