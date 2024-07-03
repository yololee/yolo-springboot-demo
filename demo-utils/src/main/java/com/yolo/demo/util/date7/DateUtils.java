package com.yolo.demo.util.date7;

import com.sun.jmx.snmp.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期时间工具类
 *
 * @author JourWon
 * @date 2020/12/5
 */
public class DateUtils {

    /**
     * 显示年月日时分秒，例如 2015-08-11 09:51:53.
     */
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 显示年月日时分，例如 2015-08-11 09:51.
     */
    public static final String NO_SECOND_DATETIME_PATTERN = "yyyy-MM-dd HH:mm";

    /**
     * 仅显示年月日，例如 2015-08-11.
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 仅显示时分秒，例如 09:51:53.
     */
    public static final String TIME_PATTERN = "HH:mm:ss";

    /**
     * 显示年月日时分秒(由/分割)，例如 2015/08/11 09:51:53.
     */
    public static final String DATETIME_PATTERN_WITH_SLASH = "yyyy/MM/dd HH:mm:ss";

    /**
     * 显示年月日(由/分割)，例如 2015/08/11.
     */
    public static final String DATE_PATTERN_WITH_SLASH = "yyyy/MM/dd";

    /**
     * 显示年月日时分秒(无符号)，例如 20150811095153.
     */
    public static final String UNSIGNED_DATETIME_PATTERN = "yyyyMMddHHmmss";

    /**
     * 仅显示年月日(无符号)，例如 20150811.
     */
    public static final String UNSIGNED_DATE_PATTERN = "yyyyMMdd";

    /**
     * 仅显示年(无符号)，例如 2015.
     */
    private static final String YEAR_PATTERN = "yyyy";

    /**
     * 仅显示年月，例如 2015-08.
     */
    private static final String MONTH_PATTERN = "yyyy-MM";

    /**
     * 仅显示年月(无符号)，例如 201508.
     */
    private static final String UNSIGNED_MONTH_PATTERN = "yyyyMM";

    /**
     * 一天的开始时间，仅显示时分秒
     */
    private static final String START_TIME = "00:00:00";

    /**
     * 一天的结束时间，仅显示时分秒
     */
    private static final String END_TIME = "23:59:59";

    /**
     * 每天的毫秒数.
     */
    public static final long MILLISECONDS_PER_DAY = 86400000L;

    /**
     * 每小时毫秒数.
     */
    public static final long MILLISECONDS_PER_HOUR = 3600000L;

    /**
     * 每分钟毫秒数.
     */
    public static final long MILLISECONDS_PER_MINU = 60000L;

    /**
     * 每秒的毫秒数.
     */
    public static final long MILLISECONDS_PER_SECONDS = 1000L;

    /**
     * 每分钟秒数.
     */
    public static final long SECONDS_PER_MINUTE = 60L;

    /**
     * 每小时秒数.
     */
    public static final long SECONDS_PER_HOUR = 3600L;

    /**
     * 每天秒数.
     */
    public static final long SECONDS_PER_DAY = 86400L;

    /**
     * 每周秒数.
     */
    public static final long SECONDS_PER_WEEK = 604800L;

    /**
     * 每个月秒数，默认每月30天.
     */
    public static final long SECONDS_PER_MONTH = 2592000L;

    /**
     * 每年秒数，默认每年365天.
     */
    public static final long SECONDS_PER_YEAR = 31536000L;

    /**
     * 每周的天数.
     */
    public static final long DAYS_PER_WEEK = 7L;

    /**
     * 春天;
     */
    public static final Integer SPRING = 1;

    /**
     * 夏天;
     */
    public static final Integer SUMMER = 2;

    /**
     * 秋天;
     */
    public static final Integer AUTUMN = 3;

    /**
     * 冬天;
     */
    public static final Integer WINTER = 4;

    /**
     * 星期日;
     */
    public static final String SUNDAY = "星期日";

    /**
     * 星期一;
     */
    public static final String MONDAY = "星期一";

    /**
     * 星期二;
     */
    public static final String TUESDAY = "星期二";

    /**
     * 星期三;
     */
    public static final String WEDNESDAY = "星期三";

    /**
     * 星期四;
     */
    public static final String THURSDAY = "星期四";

    /**
     * 星期五;
     */
    public static final String FRIDAY = "星期五";

    /**
     * 星期六;
     */
    public static final String SATURDAY = "星期六";

    /**
     * 获取当前日期和时间字符串.
     *
     * @return String 日期时间字符串，例如 2015-08-11 09:51:53
     */
    public static String getDateTimeStr() {
        return format(new Date(), DATETIME_PATTERN);
    }

    /**
     * 获取当前日期字符串.
     *
     * @return String 日期字符串，例如2015-08-11
     */
    public static String getDateStr() {
        return format(new Date(), DATE_PATTERN);
    }

    /**
     * 获取当前时间字符串.
     *
     * @return String 时间字符串，例如 09:51:53
     */
    public static String getTimeStr() {
        return format(new Date(), TIME_PATTERN);
    }

    /**
     * 获取当前年份字符串.
     *
     * @return String 当前年份字符串，例如 2015
     */
    public static String getYearStr() {
        return format(new Date(), YEAR_PATTERN);
    }

    /**
     * 获取当前月份字符串.
     *
     * @return String 当前月份字符串，例如 08
     */
    public static String getMonthStr() {
        return format(new Date(), "MM");
    }

    /**
     * 获取当前天数字符串.
     *
     * @return String 当前天数字符串，例如 11
     */
    public static String getDayStr() {
        return format(new Date(), "dd");
    }

    /**
     * 获取当前星期字符串.
     *
     * @return String 当前星期字符串，例如 星期二
     */
    public static String getDayOfWeekStr() {
        return format(new Date(), "E");
    }

    /**
     * 获取指定日期是星期几
     *
     * @param date 日期
     * @return String 星期几
     */
    public static String getDayOfWeekStr(Date date) {
        String[] weekOfDays = {SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int num = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekOfDays[num];
    }

    /**
     * 获取当前小时字符串.
     *
     * @return String 当前小时字符串，例如09
     */
    public static String getHourStr() {
        return format(new Date(), "HH");
    }

    /**
     * 获取当前分钟字符串.
     *
     * @return String 当前分钟字符串，例如51
     */
    public static String getMinuteStr() {
        return format(new Date(), "mm");
    }

    /**
     * 获取当前秒钟字符串.
     *
     * @return String 当前秒钟字符串，例如53
     */
    public static String getSecondStr() {
        return format(new Date(), "ss");
    }

    /**
     * 获取日期时间字符串
     *
     * @param date    需要转化的日期时间
     * @param pattern 时间格式
     * @return String 日期时间字符串，例如 2015-08-11 09:51:53
     */
    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 时间戳转换为日期时间字符串
     *
     * @param timestamp 时间戳
     * @param pattern   日期格式 例如DATETIME_PATTERN
     * @return String 日期时间字符串，例如 2015-08-11 09:51:53
     */
    public static String getDateTimeStr(long timestamp, String pattern) {
        return new SimpleDateFormat(pattern).format(timestamp);
    }

    /**
     * 日期字符串转换为日期(java.util.Date)
     *
     * @param dateStr 日期字符串
     * @param pattern 日期格式 例如DATETIME_PATTERN
     * @return Date 日期
     */
    public static Date parse(String dateStr, String pattern) {
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
        dateFormat.setLenient(false);
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取指定日期num年数之后的日期.
     *
     * @param num 间隔年数(负数表示之前)
     * @return Date 日期
     */
    public static Date addYears(Date date, int num) {
        return add(date, num, Calendar.YEAR);
    }

    /**
     * 获取当前日期指定年数之后的日期.
     *
     * @param num 间隔年数(负数表示之前)
     * @return Date 日期
     */
    public static Date addYears(int num) {
        return add(new Date(), num, Calendar.YEAR);
    }

    /**
     * 获取当前日期num月数之后的日期.
     *
     * @param num 间隔月数(负数表示之前)
     * @return Date 日期
     */
    public static Date addMonths(Date date, int num) {
        return add(date, num, Calendar.MONTH);
    }

    /**
     * 获取当前日期指定月数之后的日期.
     *
     * @param num 间隔月数(负数表示之前)
     * @return Date 日期
     */
    public static Date addMonths(int num) {
        return add(new Date(), num, Calendar.MONTH);
    }

    /**
     * 获取指定日期num周数之后的日期.
     *
     * @param date 日期
     * @param num  周数(负数表示之前)
     * @return Date 新的日期
     */
    public static Date addWeeks(Date date, int num) {
        return add(date, num, Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获取当前日期指定周数之后的日期.
     *
     * @param num 周数(负数表示之前)
     * @return Date 新的日期
     */
    public static Date addWeeks(int num) {
        return add(new Date(), num, Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获取指定日期num天数之后的日期.
     *
     * @param date 日期
     * @param num  天数(负数表示之前)
     * @return Date 新的日期
     */
    public static Date addDays(Date date, int num) {
        return add(date, num, Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前日期指定天数之后的日期.
     *
     * @param num 天数(负数表示之前)
     * @return Date 新的日期
     */
    public static Date addDays(int num) {
        return add(new Date(), num, Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取指定日期num小时之后的日期.
     *
     * @param date 日期
     * @param num  小时数(负数表示之前)
     * @return Date 新的日期
     */
    public static Date addHours(Date date, int num) {
        return add(date, num, Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前日期指定小时之后的日期.
     *
     * @param num 小时数(负数表示之前)
     * @return Date 新的日期
     */
    public static Date addHours(int num) {
        return add(new Date(), num, Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取指定日期num分钟之后的日期.
     *
     * @param date 日期
     * @param num  分钟数(负数表示之前)
     * @return Date 新的日期
     */
    public static Date addMinutes(Date date, int num) {
        return add(date, num, Calendar.MINUTE);
    }

    /**
     * 获取当前日期指定分钟之后的日期.
     *
     * @param num 分钟数(负数表示之前)
     * @return Date 新的日期
     */
    public static Date addMinutes(int num) {
        return add(new Date(), num, Calendar.MINUTE);
    }

    /**
     * 获取指定日期num秒钟之后的日期.
     *
     * @param date 日期
     * @param num  秒钟数(负数表示之前)
     * @return Date 新的日期
     */
    public static Date addSeconds(Date date, int num) {
        return add(date, num, Calendar.SECOND);
    }

    /**
     * 获取当前日期指定秒钟之后的日期.
     *
     * @param num 秒钟数(负数表示之前)
     * @return Date 新的日期
     */
    public static Date addSeconds(int num) {
        return add(new Date(), num, Calendar.SECOND);
    }

    /**
     * 获取指定日期num毫秒之后的日期.
     *
     * @param date 日期
     * @param num  毫秒数(负数表示之前)
     * @return Date 新的日期
     */
    public static Date addMilliSeconds(Date date, int num) {
        return add(date, num, Calendar.MILLISECOND);
    }

    /**
     * 获取当前日期指定毫秒之后的日期.
     *
     * @param num 毫秒数(负数表示之前)
     * @return Date 新的日期
     */
    public static Date addMilliSeconds(int num) {
        return add(new Date(), num, Calendar.MILLISECOND);
    }

    /**
     * 获取当前日期指定数量日期时间单位之后的日期.
     *
     * @param date 日期
     * @param num  数量
     * @param unit 日期时间单位
     * @return Date 新的日期
     */
    public static Date add(Date date, int num, int unit) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(unit, num);
        return calendar.getTime();
    }

    /**
     * 计算两个日期之间相隔年数.
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return int 相隔年数，向下取整
     */
    public static int getYearsBetween(Date startDate, Date endDate) {
        return getMonthsBetween(startDate, endDate) / 12;
    }

    /**
     * 计算两个日期之间相隔月数.
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return int 相隔月数，向下取整
     */
    public static int getMonthsBetween(Date startDate, Date endDate) {
        int months;
        int flag = 0;

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);

        if (endCalendar.equals(startCalendar)) {
            return 0;
        }

        if (startCalendar.after(endCalendar)) {
            Calendar temp = startCalendar;
            startCalendar = endCalendar;
            endCalendar = temp;
        }
        if (endCalendar.get(Calendar.DAY_OF_MONTH) < startCalendar.get(Calendar.DAY_OF_MONTH)) {
            flag = 1;
        }

        if (endCalendar.get(Calendar.YEAR) > startCalendar.get(Calendar.YEAR)) {
            months = ((endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR))
                    * 12 + endCalendar.get(Calendar.MONTH) - flag)
                    - startCalendar.get(Calendar.MONTH);
        } else {
            months = endCalendar.get(Calendar.MONTH)
                    - startCalendar.get(Calendar.MONTH) - flag;
        }

        return months;
    }

    /**
     * 计算两个日期之间相隔周数.
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return long 相隔周数，向下取整
     */
    public static long getWeeksBetween(Date startDate, Date endDate) {
        return getDaysBetween(startDate, endDate) / DAYS_PER_WEEK;
    }

    /**
     * 计算两个日期之间相隔天数.
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return long 相隔天数，向下取整
     */
    public static long getDaysBetween(Date startDate, Date endDate) {
        return (endDate.getTime() - startDate.getTime()) / MILLISECONDS_PER_DAY;
    }

    /**
     * 计算两个日期之间相隔小时数.
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return long 相隔小时数，向下取整
     */
    public static long getHoursBetween(Date startDate, Date endDate) {
        return (endDate.getTime() - startDate.getTime()) / MILLISECONDS_PER_HOUR;
    }

    /**
     * 计算两个日期之间相隔分钟数.
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return long 相隔分钟数，向下取整
     */
    public static long getMinutesBetween(Date startDate, Date endDate) {
        return (endDate.getTime() - startDate.getTime()) / MILLISECONDS_PER_MINU;
    }

    /**
     * 计算两个日期之间相隔秒数.
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return long 相隔秒数，向下取整
     */
    public static long getSecondsBetween(Date startDate, Date endDate) {
        return (endDate.getTime() - startDate.getTime()) / MILLISECONDS_PER_SECONDS;
    }

    /**
     * 获取当前季度.
     * 注意:3～5月为春季 1，6～8月为夏季 2，9～11月为秋季 3，12～2月为冬季 4
     *
     * @return int 当前季度数
     */
    public static int getCurrentSeason() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int season = 0;
        if (month >= 3 && month <= 5) {
            season = SPRING;
        } else if (month >= 6 && month <= 8) {
            season = SUMMER;
        } else if (month >= 9 && month <= 11) {
            season = AUTUMN;
        } else if (month == 12 || month >= 1 && month <= 2) {
            season = WINTER;
        }
        return season;
    }

    /**
     * 获取当前日期与之前日期的时间间隔.
     *
     * @param date 之前的日期
     * @return String 例如 16分钟前、2小时前、3天前、4月前、5年前等
     */
    public static String getIntervalByDate(Date date) {
        long secondsBetween = getSecondsBetween(date, new Date());
        return getIntervalBySeconds(secondsBetween);
    }

    /**
     * 将以秒为单位的时间转换为其他单位.
     *
     * @param seconds 秒数
     * @return String 例如 16分钟前、2小时前、3天前、4月前、5年前等
     */
    public static String getIntervalBySeconds(long seconds) {
        StringBuffer buffer = new StringBuffer();
        if (seconds < SECONDS_PER_MINUTE) {
            buffer.append(seconds).append("秒前");
        } else if (seconds < SECONDS_PER_HOUR) {
            buffer.append((long) Math.floor(seconds / SECONDS_PER_MINUTE)).append("分钟前");
        } else if (seconds < SECONDS_PER_DAY) {
            buffer.append((long) Math.floor(seconds / SECONDS_PER_HOUR)).append("小时前");
        } else if (seconds < SECONDS_PER_WEEK) {
            buffer.append((long) Math.floor(seconds / SECONDS_PER_DAY)).append("天前");
        } else if (seconds < SECONDS_PER_MONTH) {
            buffer.append((long) Math.floor(seconds / SECONDS_PER_WEEK)).append("周前");
        } else if (seconds < SECONDS_PER_YEAR) {
            buffer.append((long) Math.floor(seconds / SECONDS_PER_MONTH)).append("月前");
        } else {
            buffer.append((long) Math.floor(seconds / SECONDS_PER_YEAR)).append("年前");
        }
        return buffer.toString();
    }

    /**
     * 将 Date 日期转化为 Calendar 类型日期.
     *
     * @param date 指定日期
     * @return Calendar Calendar对象
     */
    public static Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 得到UTC时间，类型为字符串，格式为"yyyy-MM-dd HH:mm"
     * 如果获取失败，返回null
     *
     * @return
     */
    public static String getUTCTimeStr() {
        StringBuffer UTCTimeBuffer = new StringBuffer();
        // 1、取得本地时间：
        Calendar cal = Calendar.getInstance();
        // 2、取得时间偏移量：
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        UTCTimeBuffer.append(year).append("-").append(month).append("-").append(day);
        UTCTimeBuffer.append(" ").append(hour).append(":").append(minute);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(NO_SECOND_DATETIME_PATTERN);
            sdf.parse(UTCTimeBuffer.toString());
            return UTCTimeBuffer.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将Timestamp转换为yyyy-MM-dd HH:mm:ss格式字符串
     *
     * @param timestamp
     * @return
     */
    public static String timestampToStr(Timestamp timestamp) {
        return timestamp.toString().substring(0, 19);
    }

    /**
     * 比较传进来的日期是否大于当前日期，如果传进来的日期大于当前日期则返回true，否则返回false
     *
     * @param dateStr 日期字符串
     * @param pattern 日期格式
     * @return boolean
     */
    public static boolean compareNowDate(String dateStr, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            Date date = simpleDateFormat.parse(dateStr);
            return date.after(new Date());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 如果endDateStr>startDateStr，返回true，否则返回false
     *
     * @param startDateStr 开始日期字符串
     * @param endDateStr   结束日期字符串
     * @param pattern      日期格式
     * @return boolean
     */
    public static boolean compareDate(String startDateStr, String endDateStr, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            Date startDate = simpleDateFormat.parse(startDateStr);
            Date endDate = simpleDateFormat.parse(endDateStr);
            return endDate.after(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 如果startDate>endDate，返回true，否则返回false
     *
     * @param startDate 开始日期字符串
     * @param endDate   结束日期字符串
     * @return boolean
     */
    public static boolean compareDate(Date startDate, Date endDate) {
        return endDate.after(startDate);
    }

    /**
     * 判断日期是否合法
     *
     * @param dateStr yyyy-MM-dd HH:mm:ss格式日期字符串
     * @return
     */
    public static boolean isValidDate(String dateStr, String pattern) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }


    /**
     * 判断日期是否合法
     *
     * @param dateStr yyyy-MM-dd HH:mm:ss格式日期字符串
     * @return
     */
    public static boolean isValidDate(String dateStr) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期
        SimpleDateFormat format = new SimpleDateFormat(DATETIME_PATTERN);
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 判断日期是否为月底最后一天
     *
     * @param date 日期
     * @return boolean true:是  false:否
     */
    public static boolean isLastDayofMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) + 1));
        if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            return true;
        }
        return false;
    }

    /**
     * 获取本年第一天的日期字符串
     *
     * @return String 格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getYearStartTimeStr() {
        return getDateTimeStr(getStartDayOfYear(new Date()));
    }

    /**
     * 获取指定日期当年第一天的日期字符串
     *
     * @param date
     * @return String 格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getYearStartTimeStr(Date date) {
        return getDateTimeStr(getStartDayOfYear(date));
    }

    /**
     * 获取本年最后一天的日期字符串
     *
     * @return String 格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getYearEndTimeStr() {
        return getDateTimeStr(getEndDayOfYear(new Date()));
    }

    /**
     * 获取指定日期当年最后一天的日期字符串
     *
     * @param date 指定日期
     * @return String 格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getYearEndTimeStr(Date date) {
        return getDateTimeStr(getEndDayOfYear(date));
    }

    /**
     * 获取本月第一天的日期字符串
     * 格式：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getMonthStartTimeStr() {
        return getDateTimeStr(getStartDayOfMonth(new Date()));
    }

    /**
     * 获取指定日期当月第一天的日期字符串
     *
     * @param date 指定日期
     * @return String 格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getMonthStartTimeStr(Date date) {
        return getDateTimeStr(getStartDayOfMonth(date));
    }

    /**
     * 获取本月最后一天的日期字符串
     *
     * @return String 格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getMonthEndTimeStr() {
        return getDateTimeStr(getEndDayOfMonth(new Date()));
    }

    /**
     * 获取指定日期当月最后一天的日期字符串
     *
     * @param date 指定日期
     * @return String 格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getMonthEndTimeStr(Date date) {
        return getDateTimeStr(getEndDayOfMonth(date));
    }

    /**
     * 获取本周第一天的日期字符串
     *
     * @return String 格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getWeekStartTimeStr() {
        return getDateTimeStr(getStartDayOfWeek(new Date()));
    }

    /**
     * 获取指定日期当周第一天的日期字符串
     *
     * @param date 指定日期
     * @return String 格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getWeekStartTimeStr(Date date) {
        return getDateTimeStr(getStartDayOfWeek(date));
    }

    /**
     * 获取本周最后一天的日期字符串
     *
     * @return String 格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getWeekEndTimeStr() {
        return getDateTimeStr(getEndDayOfWeek(new Date()));
    }

    /**
     * 获取指定日期当周最后一天的日期字符串
     *
     * @param date 指定日期
     * @return String 格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getWeekEndTimeStr(Date date) {
        return getDateTimeStr(getEndDayOfWeek(date));
    }

    /**
     * 获取今天的开始时间字符串
     *
     * @return String 格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getTodayStartTimeStr() {
        return getDateTimeStr(getTodayStartTime(new Date()));
    }

    /**
     * 获取指定日期的开始时间字符串
     *
     * @param date 指定日期
     * @return String 格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getTodayStartTimeStr(Date date) {
        return getDateTimeStr(getTodayStartTime(date));
    }

    /**
     * 获取今天的结束时间字符串
     *
     * @return String 格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getTodayEndTimeStr() {
        return getDateTimeStr(getTodayEndTime(new Date()));
    }

    /**
     * 获取指定日期的结束时间字符串
     *
     * @param date 指定日期
     * @return String 格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getTodayEndTimeStr(Date date) {
        return getDateTimeStr(getTodayEndTime(date));
    }

    /**
     * 获得指定日期所在日的开始时间字符串
     *
     * @param date 指定日期
     * @return String 例如：2020-12-06 00:00:00
     */
    public static String getDateStartTimeStr(Date date) {
        String result = format(date, DATE_PATTERN);
        return result.concat(" ").concat(START_TIME);
    }

    /**
     * 获得指定日期所在日的结束时间字符串
     *
     * @param date 指定日期
     * @return String 例如：2020-12-06 23:59:59
     */
    public static String getDateEndTimeStr(Date date) {
        String result = format(date, DATE_PATTERN);
        return result.concat(" ").concat(END_TIME);
    }

    /**
     * 根据日历返回日期时间字符串
     *
     * @param calendar 日历
     * @return String 日期时间字符串
     */
    public static String getDateTimeStr(Calendar calendar) {
        StringBuffer buf = new StringBuffer("");

        buf.append(calendar.get(Calendar.YEAR));
        buf.append("-");
        buf.append(calendar.get(Calendar.MONTH) + 1 > 9 ? calendar.get(Calendar.MONTH) + 1 + ""
                : "0" + (calendar.get(Calendar.MONTH) + 1));
        buf.append("-");
        buf.append(calendar.get(Calendar.DAY_OF_MONTH) > 9 ? calendar.get(Calendar.DAY_OF_MONTH) + ""
                : "0" + calendar.get(Calendar.DAY_OF_MONTH));
        buf.append(" ");
        buf.append(calendar.get(Calendar.HOUR_OF_DAY) > 9 ? calendar.get(Calendar.HOUR_OF_DAY) + ""
                : "0" + calendar.get(Calendar.HOUR_OF_DAY));
        buf.append(":");
        buf.append(calendar.get(Calendar.MINUTE) > 9 ? calendar.get(Calendar.MINUTE) + ""
                : "0" + calendar.get(Calendar.MINUTE));
        buf.append(":");
        buf.append(calendar.get(Calendar.SECOND) > 9 ? calendar.get(Calendar.SECOND) + ""
                : "0" + calendar.get(Calendar.SECOND));
        return buf.toString();
    }

    /**
     * 获取今年的第一天
     *
     * @return Calendar 日历
     */
    public static Calendar getStartDayOfYear(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    /**
     * 获取今年的最后一天
     *
     * @return Calendar 日历
     */
    public static Calendar getEndDayOfYear(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(date);
        int i = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
        calendar.set(Calendar.DAY_OF_YEAR, i);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar;
    }

    /**
     * 获取本月的第一天
     *
     * @return Calendar 日历
     */
    public static Calendar getStartDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    /**
     * 获取本月的最后一天
     *
     * @return Calendar 日历
     */
    public static Calendar getEndDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(date);
        int i = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, i);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar;
    }

    /**
     * 获取本周的第一天，一个星期的第一天是星期一，最后一天是星期天
     *
     * @return Calendar 日历
     */
    public static Calendar getStartDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    /**
     * 获取本周的最后一天，一个星期的第一天是星期一，最后一天是星期天
     *
     * @return Calendar 日历
     */
    public static Calendar getEndDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar;
    }

    /**
     * 获取今天开始时间
     *
     * @return
     */
    public static Calendar getTodayStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * 获取今天结束时间
     *
     * @return
     */
    public static Calendar getTodayEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar;
    }

    /**
     * 根据日期范围，获取按周期划分的日期区间
     *
     * @param startDateStr 开始日期（格式：2020-11-29）
     * @param endDateStr   结束日期（格式：2020-12-02）
     * @param pattern      日期格式（支持：DATE_PATTERN，MONTH_PATTERN，YEAR_PATTERN）
     * @return List<String> 区间集合 例如：[2020-11-29,2020-11-30,2020-12-01,2020-12-02]
     */
    public static List<String> getDateStrList(String startDateStr, String endDateStr, String pattern) {
        Date start = parse(startDateStr, pattern);
        Date end = parse(endDateStr, pattern);
        return getDateStrList(start, end, pattern);
    }

    /**
     * 根据日期范围，获取按周期划分的日期区间
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param pattern   日期格式（支持：DATE_PATTERN，MONTH_PATTERN，YEAR_PATTERN）
     * @return List<String> 区间集合 例如：[2020-11-29,2020-11-30,2020-12-01,2020-12-02]
     */
    public static List<String> getDateStrList(Date startDate, Date endDate, String pattern) {
        List<String> result = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        if (DATE_PATTERN.equals(pattern)) {
            while (startDate.before(endDate) || startDate.equals(endDate)) {
                result.add(new SimpleDateFormat(DATE_PATTERN).format(calendar.getTimeInMillis()));
                calendar.add(Calendar.DATE, 1);
                startDate = calendar.getTime();
            }
        } else if (MONTH_PATTERN.equals(pattern)) {
            while (startDate.before(endDate) || startDate.equals(endDate)) {
                result.add(new SimpleDateFormat(MONTH_PATTERN).format(calendar.getTimeInMillis()));
                calendar.add(Calendar.MONTH, 1);
                startDate = calendar.getTime();
            }
        } else if (YEAR_PATTERN.equals(pattern)) {
            while (startDate.before(endDate) || startDate.equals(endDate)) {
                result.add(new SimpleDateFormat(YEAR_PATTERN).format(calendar.getTimeInMillis()));
                calendar.add(Calendar.YEAR, 1);
                startDate = calendar.getTime();
            }
        }
        return result;
    }

    /**
     * 获取当前日期前后num天的集合
     *
     * @param num 天数（正数：之后；负数：之前）
     * @return List<String> 前/后日期的集合（包含指定日期）
     */
    public static List<String> getDateStrList(int num) {
        return getDateStrList(new Date(), num, DATE_PATTERN);
    }

    /**
     * 获取指定日期前后num天的集合
     *
     * @param date 指定日期
     * @param num  天数（正数：之后；负数：之前）
     * @return List<String> 前/后日期的集合（包含指定日期）
     */
    public static List<String> getDateStrList(Date date, int num) {
        return getDateStrList(date, num, DATE_PATTERN);
    }

    /**
     * 获取指定日期前后num天的集合，带日期格式参数
     *
     * @param date    指定日期
     * @param num     天数（正数：之后；负数：之前）
     * @param pattern 日期格式
     * @return List<String> 前/后日期的集合（包含指定日期）  例如：[2020-11-29,2020-11-30,2020-12-01]
     */
    public static List<String> getDateStrList(Date date, int num, String pattern) {
        List<String> result = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        boolean flag = false;
        if (num < 0) {
            num = Math.abs(num);
            flag = true;
        }
        for (int i = 0; i < num; i++) {
            result.add(new SimpleDateFormat(pattern).format(c.getTimeInMillis()));
            c.add(Calendar.DATE, flag ? -1 : 1);
        }
        if (flag) {
            Collections.reverse(result);
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println("=======================");
        System.out.println(getYearStr());
        System.out.println(getMonthStr());
        System.out.println(getDayStr());
        System.out.println(getHourStr());
        System.out.println(getMinuteStr());
        System.out.println(getSecondStr());

        System.out.println(getDateTimeStr());
        System.out.println(getDateStr());
        System.out.println(getTimeStr());
        System.out.println(getDayOfWeekStr());
        System.out.println(getDayOfWeekStr(parse("2020-12-05", DATE_PATTERN)));
        System.out.println(getDateTimeStr(System.currentTimeMillis(), DATETIME_PATTERN));

        System.out.println("=======================");
        System.out.println(parse("2020-12-31", DATE_PATTERN));

        System.out.println("=======================");
        System.out.println(addYears(1));
        System.out.println(addYears(new Date(), -1));
        System.out.println(addMonths(1));
        System.out.println(addMonths(new Date(), -1));
        System.out.println(addWeeks(1));
        System.out.println(addWeeks(new Date(), -1));
        System.out.println(addDays(1));
        System.out.println(addDays(new Date(), -1));
        System.out.println(addHours(1));
        System.out.println(addHours(new Date(), -1));
        System.out.println(addMinutes(1));
        System.out.println(addMinutes(new Date(), -1));
        System.out.println(addSeconds(1));
        System.out.println(addSeconds(new Date(), -1));
        System.out.println(addMilliSeconds(1));
        System.out.println(addMilliSeconds(new Date(), -1));

        System.out.println("=======================");
        System.out.println(getYearsBetween(parse("2020-01-30", DATE_PATTERN), parse("2021-01-29", DATE_PATTERN)));
        System.out.println(getMonthsBetween(parse("2020-01-30", DATE_PATTERN), parse("2021-01-29", DATE_PATTERN)));
        System.out.println(getWeeksBetween(parse("2020-01-30", DATE_PATTERN), parse("2020-02-06", DATE_PATTERN)));
        System.out.println(getDaysBetween(parse("2020-01-30", DATE_PATTERN), parse("2020-02-06", DATE_PATTERN)));
        System.out.println(getHoursBetween(parse("2020-01-30", DATE_PATTERN), parse("2020-02-06", DATE_PATTERN)));
        System.out.println(getMinutesBetween(parse("2020-01-30", DATE_PATTERN), parse("2020-02-06", DATE_PATTERN)));
        System.out.println(getSecondsBetween(parse("2020-12-06 19:47:00", DATETIME_PATTERN), parse("2020-12-06 19:47:50", DATETIME_PATTERN)));

        System.out.println("=======================");
        System.out.println(getCurrentSeason());

        System.out.println("=======================");
        System.out.println(getIntervalByDate(parse("2020-12-06 19:30:00", DATETIME_PATTERN)));
        System.out.println(getIntervalBySeconds(604800L));

        System.out.println("=======================");
        System.out.println(getCalendar(new Date()));
        System.out.println(getUTCTimeStr());

        System.out.println("=======================");
        System.out.println(timestampToStr(new Timestamp(System.currentTimeMillis())));

        System.out.println("=======================");
        System.out.println(compareNowDate("2020-12-07", DATE_PATTERN));
        System.out.println(compareDate(parse("2020-12-05", DATE_PATTERN), new Date()));
        System.out.println(compareDate("2020-12-05", "2020-12-06", DATE_PATTERN));

        System.out.println("=======================");
        System.out.println(isValidDate("2020-02-29 23:59:00"));
        System.out.println(isLastDayofMonth(parse("2020-11-01 00:00:00", DATETIME_PATTERN)));

        System.out.println("=======================");
        System.out.println(getYearStartTimeStr());
        System.out.println(getYearStartTimeStr(parse("2019-12-06", DATE_PATTERN)));
        System.out.println(getYearEndTimeStr());
        System.out.println(getYearEndTimeStr(parse("2019-12-06", DATE_PATTERN)));
        System.out.println(getMonthStartTimeStr());
        System.out.println(getMonthStartTimeStr(parse("2019-12-06", DATE_PATTERN)));
        System.out.println(getMonthEndTimeStr());
        System.out.println(getMonthEndTimeStr(parse("2019-12-06", DATE_PATTERN)));
        System.out.println(getWeekStartTimeStr());
        System.out.println(getWeekStartTimeStr(parse("2019-12-06", DATE_PATTERN)));
        System.out.println(getWeekEndTimeStr());
        System.out.println(getWeekEndTimeStr(parse("2019-12-06", DATE_PATTERN)));
        System.out.println(getTodayStartTimeStr());
        System.out.println(getTodayStartTimeStr(parse("2019-12-06", DATE_PATTERN)));
        System.out.println(getTodayEndTimeStr());
        System.out.println(getTodayEndTimeStr(parse("2019-12-06", DATE_PATTERN)));
        System.out.println(getDateStartTimeStr(parse("2020-11-01 00:00:00", DATETIME_PATTERN)));
        System.out.println(getDateEndTimeStr(parse("2020-11-01 00:00:00", DATETIME_PATTERN)));

        System.out.println("=======================");
        List<String> strList1 = getDateStrList(3);
        for (String s : strList1) {
            System.out.println(s);
        }

        System.out.println("=======================");
        List<String> dayList = getDateStrList(parse("2020-11-29", DATE_PATTERN), 3);
        for (String s : dayList) {
            System.out.println(s);
        }

        System.out.println("=======================");
        List<String> dateList = getDateStrList("2020-11-29", "2020-12-06", DATE_PATTERN);
        for (String s : dateList) {
            System.out.println(s);
        }

        System.out.println("=======================");
        List<String> strList = getDateStrList(parse("2020-11-29", DATE_PATTERN), parse("2020-12-06", DATE_PATTERN), DATE_PATTERN);
        for (String s : strList) {
            System.out.println(s);
        }
    }

}
