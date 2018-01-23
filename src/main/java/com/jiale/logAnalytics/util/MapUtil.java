package com.jiale.logAnalytics.util;

import java.util.*;

public class MapUtil {

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueAes(Map<K, V> map) {
        return sortByValue(map, true);
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDesc(Map<K, V> map) {
        return sortByValue(map, false);
    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, final boolean isAes) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                if(isAes) {
                    return (o1.getValue()).compareTo(o2.getValue());
                } else {
                    return (o2.getValue()).compareTo(o1.getValue());
                }
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static Map<String, Long> filterFirst20Long(Map<String, Long> sored) {
        Map<String, Long> result = new LinkedHashMap<>();

        long i = 0;
        long other = 0;
        for(Map.Entry<String, Long> entry : sored.entrySet()) {
            if(i < 20) {
                result.put(entry.getKey(), entry.getValue());
            } else {
                other += entry.getValue();
            }
            i++;
        }

        if(other != 0) {
            result.put("Other", other);
        }

        return result;
    }

    public static Map<String, Double> filterFirst20Double(Map<String, Double> sored) {
        Map<String, Double> result = new LinkedHashMap<>();

        int i = 0;
        double other = 0.0;
        for(Map.Entry<String, Double> entry : sored.entrySet()) {
            if(i < 20) {
                result.put(entry.getKey(), entry.getValue());
            } else {
                other += entry.getValue();
            }
            i++;
        }

        if(other > 0.0) {
            result.put("Ohter", other);
        }

        return result;
    }
}