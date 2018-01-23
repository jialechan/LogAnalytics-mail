package com.jiale.logAnalytics.mail;

import com.jiale.logAnalytics.config.MailConfig;
import org.apache.commons.mail.EmailException;

import java.io.IOException;

import static com.jtool.apiclient.ApiClient.Api;

public class MailSender {

    public static boolean send(final MailConfig mailConfig, final String title, final String content) throws IOException {

        MailPojo mailPojo = new MailPojo();
        mailPojo.setUniqueCode(mailConfig.getUniqueCode());
        mailPojo.setAppName("[Auto]");
        mailPojo.setEmailContent(content);
        mailPojo.setEmailSubject(title);

        Api().setReadTimeout(1000000).param(mailPojo).restPost(mailConfig.getApiMailGateWay());

        return true;

    }

}
