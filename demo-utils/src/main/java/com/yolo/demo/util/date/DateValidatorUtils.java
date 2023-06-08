package com.yolo.demo.util.date;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;


/**
 * 校验时间格式工具类
 *
 * @author jujueaoye
 * @date 2023/06/08
 */
public class DateValidatorUtils {

    /**
     * 这里的年必须为uuuu
     */
    private static final DateTimeFormatter UUUUM_MDDH_HMMSS = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss", Locale.CHINA);
    private static final DateTimeFormatter UUUUM_MDD = DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.CHINA);


    /**
     * 校验格式为 yyyy-MM-dd HH:mm:ss 字符串的时间格式是否合法
     *
     * @param dateStr str日期
     * @return boolean
     */
    public static boolean isValidYyyyMMddHHmmss(String dateStr) {
        try {
            //  ResolverStyle.STRICT (解析模式) 一共有三种
            //  STRICT：严格模式，日期、时间必须完全正确
            //  SMART：智能模式，针对日可以自动调整。月的范围在 1 到 12，日的范围在 1 到 31。比如输入是2月30号，当年2月只有28天，返回的日期就是2月28日
            //  LENIENT：宽松模式，主要针对月和日，会自动后延

            DateTimeFormatter dateTimeFormatter = UUUUM_MDDH_HMMSS.withResolverStyle(ResolverStyle.STRICT);
            LocalDate.parse(dateStr, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }


    /**
     * 校验格式为 yyyy-MM-dd 字符串的时间格式是否合法
     *
     * @param dateStr str日期
     * @return boolean
     */
    public static boolean isValidYyyyMMdd(String dateStr) {
        try {
            DateTimeFormatter dateTimeFormatter = UUUUM_MDD.withResolverStyle(ResolverStyle.STRICT);
            LocalDate.parse(dateStr, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        boolean result = DateValidatorUtils.isValidYyyyMMddHHmmss("2022-11-11 12:13:14");
        System.out.println(result);
    }

}
