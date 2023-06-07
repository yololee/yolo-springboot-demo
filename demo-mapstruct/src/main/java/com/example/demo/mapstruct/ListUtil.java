package com.example.demo.mapstruct;

import cn.hutool.core.util.StrUtil;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.List;

public class ListUtil {

    public static List<String> stringToListString(String str,String separator) {
        return Arrays.asList(StrUtil.split(str, separator));
    }

    @Named("listStringToString")
    public static String listStringToString(List<String> strList){
        return StrUtil.join(",",strList);
    }
}
