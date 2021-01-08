package com.bestarmedia.migration.misc;


import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

public class DateUtil {

    public static String getDateTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public static String getDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    /**
     * 获取当前时间到当天零点的秒数
     * 例：2018-10-30 01:00:00 -> 3600
     *
     * @return 秒数
     */
    public static int getCurrentDaySeconds() {
        long now = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return (int) ((now - (format.parse(format.format(now)).getTime())) / 1000);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取当时时间整分到当天零点的秒数
     *
     * @return int
     */
    public static int getCurrentTimestampAtMinute() {
        LocalTime localDateTime = LocalTime.now();
        return localDateTime.getHour() * 3600 + localDateTime.getMinute() * 60;
    }

    /**
     * 获取当天零点的时间戳
     *
     * @return int
     */
    public static int getCurrentDayTimestamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return (int) (format.parse(format.format(System.currentTimeMillis())).getTime() / 1000);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 根据时间戳获取日期，格式 yyyy-MM-dd
     *
     * @param timestamp 时间戳
     * @return String
     */
    public static String getCurrentDate(int timestamp) {
        long current = (long) timestamp * 1000;
        if (timestamp <= 0) {
            current = System.currentTimeMillis();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(current);
    }

    /**
     * 根据时间戳获取日期，格式 yyyy-MM-dd
     *
     * @param timestamp 时间戳
     * @return String
     */
    public static Date getCurrentDateTime(int timestamp) {
        if (timestamp <= 0) {
            return null;
        }
        return new Date((long) timestamp * 1000);
    }

    /**
     * 根据时间获取时间戳，格式 yyyy-MM-dd
     *
     * @param date 日期
     * @return int
     */
    public static int getTimestamp(String date) {
        int timestamp = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            timestamp = (int) (format.parse(date, new ParsePosition(0)).getTime() / 1000);
        } catch (Exception e) {
        }

        return timestamp;
    }

    /**
     * 根据时间获取时间戳，格式 yyyy-MM-dd HH:mm:ss
     *
     * @param date 日期
     * @return int
     */
    public static long getTimestampByDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(date, new ParsePosition(0)).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当前指定时间到当天零点的秒数
     * 例：01:00 -> 3600
     *
     * @param time 时间，注意格式为 00:00
     * @return 秒数
     */
    public static int getCurrentDaySecondsByTime(String time) {
        int seconds = 0;
        if (time != null && !time.isEmpty()) {
            String[] times = time.split(":");
            seconds += Integer.parseInt(times[0]) * 3600;
            seconds += Integer.parseInt(times[1]) * 60;
        }
        return seconds;
    }

    /**
     * 获取当前指定时间到当天零点的秒数
     * 例：01:00:00 -> 3600
     *
     * @param time 时间，注意格式为 00:00:00
     * @return 秒数
     */
    public static int getCurrentDaySecondsByTime2(String time) {
        int seconds = 0;
        if (time != null && !time.isEmpty()) {
            String[] times = time.split(":");
            seconds += Integer.parseInt(times[0]) * 3600
                    + Integer.parseInt(times[1]) * 60
                    + Integer.parseInt(times[2]);
        }
        return seconds;
    }

    /**
     * 字符串转日期：格式 yyyy-MM-dd
     *
     * @return 时间
     */
    public static Date string2Date(String strDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串转日期：格式 yyyy-MM-dd
     *
     * @return 时间
     */
    public static Date string2DateTime(String strDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
