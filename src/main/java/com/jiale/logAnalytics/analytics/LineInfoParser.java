package com.jiale.logAnalytics.analytics;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineInfoParser {

    private static Pattern pattern = Pattern.compile("T\\[(.*?)T(.*?):(.*?):(.*?)\\+(.*?)\\] STAT\\[(.*?)\\] REQ_T\\[(.*?)\\] URL\\[[GETPOST\\s]+(.*?) HTTP.*?\\]");

    public static Optional<List<LineInfo>> parse(String line) {

        try {
            List<LineInfo> result = new ArrayList<>();

            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                LineInfo lineInfo = new LineInfo();

                lineInfo.setTime(matcher.group(2) + ":" + matcher.group(3) + ":00");
                lineInfo.setStat(matcher.group(6));
                String uri = matcher.group(8);
                if (uri.contains("?")) {
                    uri = uri.substring(0, uri.indexOf("?"));
                }
                if (uri.contains(";jsessionid=")) {
                    uri = uri.substring(0, uri.indexOf(";jsessionid="));
                }
                if(uri.startsWith("http://")) {
                    uri = uri.replaceAll("(http://.*?)/", "/");
                }
                lineInfo.setUri(uri);

                final String reqStr = matcher.group(7);
                if (!"-".equals(reqStr)) {
                    if(reqStr.contains(",")) {
                        String[] reqStrArray = reqStr.split(", ");
                        for(String reqStrItem : reqStrArray) {

                            LineInfo tempLineInfo = new LineInfo();

                            BeanUtils.copyProperties(lineInfo, tempLineInfo);

                            if (!"-".equals(reqStrItem)) {
                                Double requestTime = Double.parseDouble(reqStrItem) * 1000;
                                tempLineInfo.setRequestTime(requestTime.intValue());
                            }

                            result.add(tempLineInfo);
                        }
                    } else {
                        Double requestTime = Double.parseDouble(reqStr) * 1000;
                        lineInfo.setRequestTime(requestTime.intValue());

                        result.add(lineInfo);
                    }
                } else {
                    result.add(lineInfo);
                }

            }

            return Optional.of(result);
        } catch (Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

}