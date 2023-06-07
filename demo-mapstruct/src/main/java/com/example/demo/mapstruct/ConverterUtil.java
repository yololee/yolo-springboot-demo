package com.example.demo.mapstruct;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import org.mapstruct.Named;

import java.util.Date;

public class ConverterUtil {

    @Named("getValue")
    public static String getValue(Long l){
        return DateUtil.format(new Date(l), "yyyy/MM/dd");
    }
}
