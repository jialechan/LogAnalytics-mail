package com.jiale.logAnalytics.analytics;

import com.jiale.logAnalytics.config.IgnoreStartWithConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

@Service
public class Analyzer {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public AnalyticsResult analytics(List<File> logFiles, IgnoreStartWithConfig ignoreStartWithConfig) {

        AnalyticsResult analyticsResult = new AnalyticsResult();

        for(File logFile : logFiles) {
            log.debug("开始分析文件: {}", logFile.getAbsolutePath());
            long beginTime = System.currentTimeMillis();
            long count = 0;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)))) {
                String line;
                while ((line = br.readLine()) != null) {
                    long currentTime = System.currentTimeMillis();
                    count++;
                    if(currentTime - beginTime > 10000) {
                        log.debug("已经分析了{}行...", count);
                        beginTime = currentTime;
                    }
                    analyticsLine(analyticsResult, line, ignoreStartWithConfig);
                }
            } catch (Exception e) {
                log.error("分析日志文件出错", e);
            }
        }

        for(String path : analyticsResult.getConsumeTotalResult().keySet()) {

            Long consumeTotal = analyticsResult.getConsumeTotalResult().get(path);
            Long callTotal = analyticsResult.getCallResult().get(path);

            analyticsResult.getConsumeAvgResult().put(path, Math.round(consumeTotal.doubleValue() / callTotal.doubleValue()));
        }

        log.info("扫描行数(没经过过滤关键字的): {}", analyticsResult.getTotalLineScan());

        return analyticsResult;
    }

    private void analyticsLine(AnalyticsResult analyticsResult, String line, IgnoreStartWithConfig ignoreStartWithConfig) {
        analyticsResult.setTotalLineScan(analyticsResult.getTotalLineScan() + 1);
        Optional<List<LineInfo>> lineInfoListOpt = LineInfoParser.parse(line);
        if(lineInfoListOpt.isPresent()) {

            for(LineInfo lineInfo : lineInfoListOpt.get()) {
                if (isNotInKeyWord(lineInfo.getUri(), ignoreStartWithConfig.getAccessIgnoreStartWithList())) {
                    analyticsResult.setTotalAccess(analyticsResult.getTotalAccess() + 1);
                    addAccessCount(analyticsResult, lineInfo);
                    addHttpStatusCount(analyticsResult, lineInfo);
                }
                if (isNotInKeyWord(lineInfo.getUri(), ignoreStartWithConfig.getCallIgnoreStartWithList())) {
                    addCallCount(analyticsResult, lineInfo);
                }
                if (isNotInKeyWord(lineInfo.getUri(), ignoreStartWithConfig.getConsumeIgnoreStartWithList())) {
                    addConsumeTotalCount(analyticsResult, lineInfo);
                }
            }
        }

    }

    private boolean isNotInKeyWord(String key, List<String> shouldNotStartWithList) {
        for(String shouldNotStartWith : shouldNotStartWithList) {
            if(key.startsWith(shouldNotStartWith)) {
                return false;
            }
        }
        return true;
    }

    private void addConsumeTotalCount(AnalyticsResult analyticsResult, LineInfo lineInfo) {
        if(lineInfo.getRequestTime() != null) {
            long consumeCount = analyticsResult.getConsumeTotalResult().get(lineInfo.getUri()) == null ?
                    0L : analyticsResult.getConsumeTotalResult().get(lineInfo.getUri());
            analyticsResult.getConsumeTotalResult().put(lineInfo.getUri(), consumeCount + lineInfo.getRequestTime());
        }
    }

    private void addCallCount(AnalyticsResult analyticsResult, LineInfo lineInfo) {
        long callCount = analyticsResult.getCallResult().get(lineInfo.getUri()) == null ?
                0L : analyticsResult.getCallResult().get(lineInfo.getUri());
        analyticsResult.getCallResult().put(lineInfo.getUri(), callCount + 1);
    }

    private void addAccessCount(AnalyticsResult analyticsResult, LineInfo lineInfo) {
        long accessCount = analyticsResult.getAccessResult().get(lineInfo.getTime()) == null ?
                0L : analyticsResult.getAccessResult().get(lineInfo.getTime());
        analyticsResult.getAccessResult().put(lineInfo.getTime(), accessCount + 1);
    }

    private void addHttpStatusCount(AnalyticsResult analyticsResult, LineInfo lineInfo) {
        long count = analyticsResult.getHttpStatusResult().get(lineInfo.getStat()) == null ?
                0L : analyticsResult.getHttpStatusResult().get(lineInfo.getStat());
        analyticsResult.getHttpStatusResult().put(lineInfo.getStat(), count + 1);
    }

}
