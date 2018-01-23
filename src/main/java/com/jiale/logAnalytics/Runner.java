package com.jiale.logAnalytics;

import com.jiale.logAnalytics.analytics.AnalyticsResult;
import com.jiale.logAnalytics.analytics.Analyzer;
import com.jiale.logAnalytics.chart.ChartMaker;
import com.jiale.logAnalytics.chart.LineChartInfo;
import com.jiale.logAnalytics.chart.PieChartInfo;
import com.jiale.logAnalytics.config.CoreConfig;
import com.jiale.logAnalytics.config.IgnoreStartWithConfig;
import com.jiale.logAnalytics.config.MailConfig;
import com.jiale.logAnalytics.mail.MailSender;
import com.jiale.logAnalytics.util.GetLocalIp;
import com.jiale.logAnalytics.util.MapUtil;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.List;

import static com.jtool.apiclient.ApiClient.Api;

@Repository
public class Runner {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private MailConfig mailConfig;

    @Resource
    private CoreConfig coreConfig;

    @Resource
    private IgnoreStartWithConfig ignoreStartWithConfig;

    @Resource
    private Analyzer analyzer;

    @Value("${dateStr}")
    private String dateStr;

    @PostConstruct
    public void run() throws IOException, ParseException {

        log.info("程序开始运行...");

        //分析日志
        AnalyticsResult analyticsResult = analyticsFile(dateStr);

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("/tmp/" + System.currentTimeMillis() + ".bin"))) {
            objectOutputStream.writeObject(analyticsResult);
        }

        //生成图片
        String totalAccessBase64Data = genTotalAccessBase64Data(analyticsResult);
        String urlCallPicChartBase64Data = genUrlCallBase64Data(analyticsResult);
        String consumeTotalPieChartBase64Data = genConsumeTotalBase64Data(analyticsResult);
        String consumeAvgPieChartBase64Data = genConsumeBase64Data(analyticsResult);
        String httpStatusPieChartBase64Data = genHttpStatusBase64Data(analyticsResult);

        //发送邮件
        final String content = "<html><body>" +
                "<img src='data:image/png;base64, " + totalAccessBase64Data + "' /><br/><br/>" +
                "<img src='data:image/png;base64, " + urlCallPicChartBase64Data + "' /><br/><br/>" +
                "<img src='data:image/png;base64, " + consumeTotalPieChartBase64Data + "' /><br/><br/>" +
                "<img src='data:image/png;base64, " + consumeAvgPieChartBase64Data + "' /><br/><br/>" +
                "<img src='data:image/png;base64, " + httpStatusPieChartBase64Data + "' /><br/><br/>" +
                "邮件发送于: <br/>" + fetchLocalIps() +
                "</body></html>";

        final String title = mailConfig.getMailTitle() + " [" + dateStr + "] (总计" + new DecimalFormat("#,###").format(analyticsResult.getTotalAccess()) + "次)";
        boolean isSend = false;
        for(int i = 0; i < 5; i++) {
            try {
                if(MailSender.send(mailConfig, title, content)) {
                    isSend = true;
                    break;
                }
            } catch (EmailException e) {
                log.error("第" + (i + 1) + "发送邮件失败", e);
            }
        }

        log.info("Done, isSend: " + isSend);
    }

    private String fetchLocalIps() {

        String ipsStr = "";

        try {

            ipsStr = Api().get("https://www.taobao.com/help/getip.php") + "<br/>";

            String[] ips = GetLocalIp.getAllLocalHostIP();

            if(ips != null) {
                for (String ip : ips) {
                    ipsStr += ip + "<br/>";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ipsStr;
    }

    private String genConsumeTotalBase64Data(AnalyticsResult analyticsResult) {
        try {
            analyticsResult.setConsumeTotalResult(MapUtil.sortByValueDesc(analyticsResult.getConsumeTotalResult()));

            PieChartInfo pieChartInfo = new PieChartInfo();
            pieChartInfo.setTitle("Consumed total the top 20");

            return ChartMaker.makeConsumePieChart(analyticsResult.getConsumeTotalResult(), pieChartInfo, false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private String genConsumeBase64Data(AnalyticsResult analyticsResult) {
        try {
            analyticsResult.setConsumeAvgResult(MapUtil.sortByValueDesc(analyticsResult.getConsumeAvgResult()));

            PieChartInfo pieChartInfo = new PieChartInfo();
            pieChartInfo.setTitle("Consumed avg the top 20");

            return ChartMaker.makeConsumePieChart(analyticsResult.getConsumeAvgResult(), pieChartInfo, true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private String genUrlCallBase64Data(AnalyticsResult analyticsResult) {
        try {
            analyticsResult.setCallResult(MapUtil.sortByValueDesc(analyticsResult.getCallResult()));

            PieChartInfo pieChartInfo = new PieChartInfo();
            pieChartInfo.setTitle("Call the top 20");

            return ChartMaker.makeUrlCallPieChart(analyticsResult, pieChartInfo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    private String genTotalAccessBase64Data(AnalyticsResult analyticsResult) {
        try {

            LineChartInfo lineChartInfo = new LineChartInfo();
            lineChartInfo.setTitle("Total number of requests(" + analyticsResult.getTotalAccess() + "times)");

            return ChartMaker.makeAccessCountChart(analyticsResult, lineChartInfo);

        } catch (Exception e) {
            log.error("生成总计请求发生错误", e);
            return "";
        }
    }

    private String genHttpStatusBase64Data(AnalyticsResult analyticsResult) {
        try {
            analyticsResult.setHttpStatusResult(MapUtil.sortByValueDesc(analyticsResult.getHttpStatusResult()));

            PieChartInfo pieChartInfo = new PieChartInfo();
            pieChartInfo.setTitle("Http status code the top 20");

            return ChartMaker.makeConsumePieChartInteger(analyticsResult.getHttpStatusResult(), pieChartInfo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private AnalyticsResult analyticsFile(String dateStr) throws ParseException, IOException {
        List<File> logsFile = new ArrayList<>();
        for (String host : coreConfig.getHostList()) {
            logsFile.add(new File(coreConfig.getLogPath() + host + ".access.log_" + dateStr));
        }
        return analyzer.analytics(logsFile, ignoreStartWithConfig);
    }

}
