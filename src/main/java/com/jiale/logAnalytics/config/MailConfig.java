package com.jiale.logAnalytics.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="mail")
public class MailConfig {

    private String uniqueCode;
    private String apiMailGateWay;
    private String mailTitle;

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getApiMailGateWay() {
        return apiMailGateWay;
    }

    public void setApiMailGateWay(String apiMailGateWay) {
        this.apiMailGateWay = apiMailGateWay;
    }

    public String getMailTitle() {
        return mailTitle;
    }

    public void setMailTitle(String mailTitle) {
        this.mailTitle = mailTitle;
    }

}
