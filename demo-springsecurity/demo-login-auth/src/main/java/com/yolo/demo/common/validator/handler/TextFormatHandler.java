package com.yolo.demo.common.validator.handler;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.yolo.demo.common.validator.annotation.TextFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 参数校验验证器
 */
public class TextFormatHandler implements ConstraintValidator<TextFormat, String> {

    private Boolean  required;
    private int maxLength;
    private boolean notChinese;
    private Set<String> contains;
    private Set<String> notContains;
    private String startWith;
    private String endsWith;

    // 注解初始化时执行
    @Override
    public void initialize(TextFormat textFormat) {
        this.required = textFormat.required();
        this.maxLength = textFormat.maxLength();
        this.notChinese = textFormat.notChinese();
        this.contains = Arrays.stream(textFormat.contains()).collect(Collectors.toSet());
        this.notContains = Arrays.stream(textFormat.notContains()).collect(Collectors.toSet());
        this.startWith = textFormat.startWith();
        this.endsWith = textFormat.endsWith();
    }

    @Override
    public boolean isValid(String target, ConstraintValidatorContext context) {
        if (StrUtil.isBlank(target)) {
            return !required;
        }

        if (!required && StrUtil.isNotBlank(target) && maxLength != -1 && target.length() < maxLength){
            return Boolean.TRUE;
        }

        if (StrUtil.isNotBlank(target) && notChinese){
            return checkNoeChinese(target);
        }

        if (CollUtil.isNotEmpty(contains) && contains.contains(target)){
           return Boolean.TRUE;
        }

        if (CollUtil.isNotEmpty(notContains) && !notContains.contains(target)){
            return Boolean.TRUE;
        }

        if (StrUtil.isNotBlank(startWith) && target.startsWith(startWith)) {
            return Boolean.TRUE;
        }

        if (StrUtil.isNotBlank(endsWith) && target.endsWith(startWith)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    private Boolean checkNoeChinese(String target) {
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(target);
        return  matcher.find();
    }

}
