package com.jiale.logAnalytics.mail;

import com.jiale.logAnalytics.config.MailConfig;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import java.io.IOException;

import static com.jtool.apiclient.ApiClient.Api;

public class MailSender {

    public static boolean send(final MailConfig mailConfig, final String title, final String content) throws EmailException {

        if(mailConfig.getApiSentMail()) {
            MailPojo mailPojo = new MailPojo();
            mailPojo.setUniqueCode(mailConfig.getUniqueCode());
            mailPojo.setAppName("[Auto]");
            mailPojo.setEmailContent(content);
            mailPojo.setEmailSubject(title);
            try {
                Api().setReadTimeout(1000000).param(mailPojo).restPost(mailConfig.getApiMailGateWay());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            HtmlEmail email = new HtmlEmail();
            email.setHostName(mailConfig.getSenderHost());
            email.setAuthenticator(new DefaultAuthenticator(mailConfig.getSenderUsername(), mailConfig.getSenderPassword()));
            email.setFrom(mailConfig.getEmailFrom());
            email.setSubject(title);
            email.setHtmlMsg(content);
            email.setCharset("utf8");

            for (String receiver : mailConfig.getReceiverList()) {
                email.addTo(receiver);
            }

            email.send();

            return true;
        }

    }

}
