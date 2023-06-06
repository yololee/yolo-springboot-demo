package com.yolo.demo.mapstruct;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StringToListString {

    /**
     * 字符串根据逗号转换为字符串集合
     *
     * @param str str
     * @return {@link List}<{@link String}>
     */
    public List<String> stringToListString(String str) {
        return StrUtil.split(str,",");
    }
}
