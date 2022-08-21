package com.assignments.finalworks.application;

import com.assignments.finalworks.config.properties.ArchiveProperties;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ArchiveDataForm {

    private final ArchiveProperties archiveProperties;

    public String startDate;
    public String endDate;
    public String archiveFormat;
    public String dateFormat = "yyyy-MM-dd HH:mm:ss";

    Calendar setStartDate;
    Calendar setEndDate;

    // 작업 실행
    public void makeArchiveDataForm(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;

        setOption();

        System.out.println("startDate : " + startDate + "\n" + "endDate : " + endDate);
        System.out.println("Archive Date Format :\n{");
        autoIncrementTimeWrite();
        System.out.println("}");
    }

    public void autoIncrementTimeWrite() {
        if (this.archiveFormat.equals("5m")) {
            everyFiveMinutes();
        } else if (this.archiveFormat.equals("1h")) {
            everyHour();
        } else if (this.archiveFormat.equals("6h")) {
            everySixHours();
        } else {
            everyDay();
        }
    }

    public void everyFiveMinutes() {

        setStartDate = stringToCalendar(startDate);
        setEndDate = stringToCalendar(endDate);

        setStartDate.set(Calendar.SECOND, 00);
        setEndDate.set(Calendar.SECOND, 00);

        int sMin = setStartDate.get(Calendar.MINUTE) % 5;
        int eMin = 0;
        if ((setEndDate.get(Calendar.MINUTE) % 5) != 0) {
            eMin = 5 - (setEndDate.get(Calendar.MINUTE) % 5);
        }

        setStartDate.add(Calendar.MINUTE, sMin * -1);
        setEndDate.add(Calendar.MINUTE, eMin);

        while (setStartDate.compareTo(setEndDate) <= 0) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            System.out.println(sdf.format(setStartDate.getTime()));
            setStartDate.add(Calendar.MINUTE, 5);
        }
    }

    public void everyHour() {

        setStartDate = stringToCalendar(startDate);
        setEndDate = stringToCalendar(endDate);
        setStartDate.set(Calendar.MINUTE, 00);
        setStartDate.set(Calendar.SECOND, 00);
        setEndDate.set(Calendar.MINUTE, 00);
        setEndDate.set(Calendar.SECOND, 00);
        setEndDate.add(Calendar.HOUR, 1);

        while (setStartDate.compareTo(setEndDate) <= 0) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            System.out.println(sdf.format(setStartDate.getTime()));
            setStartDate.add(Calendar.HOUR, 1);
        }
    }

    public void everySixHours() {

        setStartDate = stringToCalendar(startDate);
        setEndDate = stringToCalendar(endDate);
        setStartDate.set(Calendar.MINUTE, 00);
        setStartDate.set(Calendar.SECOND, 00);
        setEndDate.set(Calendar.MINUTE, 00);
        setEndDate.set(Calendar.SECOND, 00);
        int sHour = setStartDate.get(Calendar.HOUR) % 6;
        int eHour = 0;
        if ((setEndDate.get(Calendar.HOUR) % 6) != 0) {
            eHour = 6 - (setEndDate.get(Calendar.HOUR) % 6);
        }

        setStartDate.add(Calendar.HOUR, sHour * -1);
        setEndDate.add(Calendar.HOUR, eHour);

        while (setStartDate.compareTo(setEndDate) <= 0) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            System.out.println(sdf.format(setStartDate.getTime()));
            setStartDate.add(Calendar.HOUR, 6);
        }
    }

    public void everyDay() {

        setStartDate = stringToCalendar(startDate);
        setEndDate = stringToCalendar(endDate);
        setStartDate.set(Calendar.HOUR, 00);
        setStartDate.set(Calendar.MINUTE, 00);
        setStartDate.set(Calendar.SECOND, 00);
        setEndDate.add(Calendar.DATE, 1);
        setEndDate.set(Calendar.HOUR, 00);
        setEndDate.set(Calendar.MINUTE, 00);
        setEndDate.set(Calendar.SECOND, 00);
        while (setStartDate.compareTo(setEndDate) <= 0) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            System.out.println(sdf.format(setStartDate.getTime()));
            setStartDate.add(Calendar.DATE, 1);
        }
    }

    // 옵션 값 세팅
    public void setOption() {
        int fiveMinuteDays = archiveProperties.getFiveMinute();
        int oneHourDays = archiveProperties.getOneHour();
        int sixHoursDays = archiveProperties.getSixHours();
        long intervalRange = getDateInterval();

        if (dayToUnix(fiveMinuteDays) >= intervalRange) {
            this.archiveFormat = "5m";
        } else if (dayToUnix(oneHourDays) >= intervalRange) {
            this.archiveFormat = "1h";
        } else if (dayToUnix(sixHoursDays) >= intervalRange) {
            this.archiveFormat = "6h";
        } else {
            this.archiveFormat = "24h";
        }
    }


    // day(int)을 unix로
    public long dayToUnix(int day) {

        long dayUnixTime = (long) day * 24 * 60 * 60;

        return dayUnixTime;
    }


    // 입력 받은 날짜 차이 계산
    public long getDateInterval() {
        long startUnixDate = dateToUnix(this.startDate, dateFormat);
        long endUnixDate = dateToUnix(this.endDate, dateFormat);

        return endUnixDate - startUnixDate;
    }

    // 날짜 변환 메소드 date to unix
    public long dateToUnix(String date, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Date beforeDate = null;
        try {
            beforeDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long unixTime = beforeDate.getTime() / 1000;
        return unixTime;
    }

    // String 형태를 Calendar 형태로
    public Calendar stringToCalendar(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Date date;
        Calendar cal = Calendar.getInstance();
        try {
            date = formatter.parse(strDate);
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }

    // 날짜 변환 메소드 unix to date
    public String unixToDate(long unix, String dateFormat) {
        Date date = new Date(unix * 1000L);
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        String unixToDate = "";
        try {
            unixToDate = String.valueOf(formatter.parse(String.valueOf(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unixToDate;
    }
}
