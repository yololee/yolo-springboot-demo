package com.yolo.demo;



import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@SpringBootTest
public class DemoNettySocketioApplicationTests {

    @Test
    public void contextLoads() {
//        List<Long> startAndEndTime = getStartAndEndTime();
        // 获取当前日期

        long startTime = cn.hutool.core.date.DateUtil.beginOfMonth(new Date(1693591200000L)).getTime(); //2023-09-01 02:00:00
        long endTime = cn.hutool.core.date.DateUtil.endOfMonth(new Date(1693591200000L)).getTime(); //2023-09-01 02:00:00
        String startTimeStr = cn.hutool.core.date.DateUtil.format(new Date(startTime), "yyyy-MM-dd");
        String monthStartDay = startTimeStr.substring(8);
        String nowTime = cn.hutool.core.date.DateUtil.format(new Date(1693591200000L) , "yyyy-MM-dd"); //2023-09-01 02:00:00
        String day = nowTime.substring(8);
        if (Convert.toInt(monthStartDay).equals(Convert.toInt(day))){
            Date currentDate = cn.hutool.core.date.DateUtil.date();
            startTime = cn.hutool.core.date.DateUtil.beginOfMonth(cn.hutool.core.date.DateUtil.offsetMonth(currentDate, -1)).getTime();
            endTime = cn.hutool.core.date.DateUtil.endOfMonth(cn.hutool.core.date.DateUtil.offsetMonth(currentDate, -1)).getTime();
        }
        String start = cn.hutool.core.date.DateUtil.format(new Date(startTime), "yyyy-MM-dd HH:mm:ss");
        String end = cn.hutool.core.date.DateUtil.format(new Date(endTime), "yyyy-MM-dd HH:mm:ss");
        System.out.println(start);
        System.out.println(end);
    }

    private List<Long> getStartAndEndTime() {
        List<Long> list = new LinkedList<>();

        //当前月的开始时间
        DateTime dateTime = cn.hutool.core.date.DateUtil.beginOfMonth(new Date(System.currentTimeMillis()));
        long startTime = dateTime.getTime();
        String startTimeStr = cn.hutool.core.date.DateUtil.format(dateTime, "yyyy-MM-dd");
        //截取天数
        String monthStartDay = startTimeStr.substring(8);

        //当前时间
//        String nowTime = cn.hutool.core.date.DateUtil.format(new Date(System.currentTimeMillis()) , "yyyy-MM-dd");
        String nowTime = "2023-09-01";
        String day = nowTime.substring(8);
        long endTime = DateUtil.parse(nowTime,"yyyy-MM-dd").getTime();
        if (Objects.equals(Convert.toInt(monthStartDay), Convert.toInt(day))){
            endTime = startTime;
            System.out.println(DateUtil.format(new Date(endTime),"yyyy-MM-dd"));
            startTime = startTime - 24 * 60 * 60 * 1000;
            System.out.println(DateUtil.format(new Date(startTime),"yyyy-MM-dd"));
        }

        list.add(startTime);
        list.add(endTime);
        return list;
    }

}
