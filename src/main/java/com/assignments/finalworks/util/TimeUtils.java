package com.assignments.finalworks.util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {


    public static String extractDateFormat(String str, String regPattern) {
        String result = "";

        Matcher matcher;

        if (!str.isEmpty()) {
//			String patternStr = "(\\d{2,4})-(\\d{2})-(\\d{2})_(\\d{6})"; // 숫자(2~4개)-숫자(2개)-숫자(2개)_숫자(6개)

            int flags = Pattern.MULTILINE | Pattern.CASE_INSENSITIVE;
            Pattern pattern = Pattern.compile(regPattern, flags);
            matcher = pattern.matcher(str);

            if (matcher.find()) {
                result = matcher.group();
            }
        }
        return result;
    }

    public static String unixTimeToDateForm(Long unixTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        Date date = new Date();
        date.setTime(unixTime * 1000);

        String Datetime = sdf.format(date);
        return Datetime;
    }
}
