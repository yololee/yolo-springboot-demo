package com.yolo.validator.common.validator.handler;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.yolo.validator.common.dto.ApiStatus;
import com.yolo.validator.common.exception.ParamException;
import com.yolo.validator.common.validator.annotation.TextFormat;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 参数校验验证器
 */
public class TextFormatHandler implements ConstraintValidator<TextFormat, Object> {

    private boolean notChinese;
    private String[] contains;
    private String[] notContains;
    private int[] containsInt;
    private int[] notContainsInt;
    private String startWith;
    private String endsWith;
    private String notNeedFill;
    private String message;

    // 注解初始化时执行
    @Override
    public void initialize(TextFormat textFormat) {
        this.notChinese = textFormat.notChinese();
        this.contains = textFormat.contains();
        this.notContains = textFormat.notContains();
        this.startWith = textFormat.startWith();
        this.endsWith = textFormat.endsWith();
        this.message = textFormat.message();
        this.containsInt = textFormat.containsInt();
        this.notContainsInt = textFormat.notContainsInt();
        this.notNeedFill = textFormat.notNeedFill();
    }

    @Override
    public boolean isValid(Object type, ConstraintValidatorContext context) {
        if (type instanceof String) {
            String target = (String) type;
            checkNoeChinese(target);
            checkContainStr(target);
            checkNotContainStr(target);
            checkStartWith(target);
            checkEndsWith(target);
            checkNotNeedFill(target);
        } else if (type instanceof Integer) {
            int target = (Integer) type;
            checkContainInt(target);
            checkNotContainInt(target);
        }else if (type instanceof List){
            List target = (List) type;
            checkContainStrList(target);
            checkContainIntList(target);
        }
        return true;
    }

    private void checkContainIntList(List<Integer> target) {
        if (CollUtil.isNotEmpty(Collections.singletonList(containsInt))){
            List<Integer> list = Arrays.stream(containsInt).boxed().collect(Collectors.toList());
            List<Integer> subtraction = target.stream().filter(v->!list.contains(v)).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(subtraction)){
                throw new ParamException(ApiStatus.PARAM_ERROR.getCode(), message);
            }
        }
    }

    private void checkContainStrList(List<String> target) {
        if (CollUtil.isNotEmpty(Arrays.asList(notContains))){
            List<String> list = Arrays.stream(notContains).collect(Collectors.toList());
            List<String> subtraction = target.stream().filter(v -> !list.contains(v)).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(subtraction)){
                throw new ParamException(ApiStatus.PARAM_ERROR.getCode(), message);
            }
        }

    }

    private void checkNotNeedFill(String target) {
        if (!StringUtils.isEmpty(notNeedFill)) {
            switch (notNeedFill) {
                case "description":
                    //检查描述长度
                    if (target.length() >= 2){
                        throw new ParamException(ApiStatus.PARAM_ERROR.getCode(), message);
                    }
                case "place":
                    if (target.length() >= 50){
                        throw new ParamException(ApiStatus.PARAM_ERROR.getCode(), message);
                    }
                default:
            }
        }
    }

    private void checkNotContainInt(int target) {
        for (int notContainInt : notContainsInt) {
            if (target == notContainInt) {
                throw new ParamException(ApiStatus.PARAM_ERROR.getCode(), message);
            }
        }
    }

    private void checkContainInt(int target) {
        for (int containInt : containsInt) {
            if (target != containInt) {
                throw new ParamException(ApiStatus.PARAM_ERROR.getCode(), message);
            }
        }
    }

    private void checkEndsWith(String target) {
        if (!StringUtils.isEmpty(target)) {
            if (!target.endsWith(endsWith)) {
                throw new ParamException(ApiStatus.PARAM_ERROR.getCode(),message);
            }
        }
    }

    private void checkStartWith(String target) {
        if (!StringUtils.isEmpty(startWith)) {
            if (!target.startsWith(startWith)) {
                throw new ParamException(ApiStatus.PARAM_ERROR.getCode(),message);
            }
        }
    }

    private void checkNotContainStr(String target) {
        for (String notContain : notContains) {
            if (notContain.equals(target)) {
                throw new ParamException(ApiStatus.PARAM_ERROR.getCode(), message);
            }
        }
    }

    private void checkContainStr(String target) {
        for (String contain : contains) {
            if (!contain.equals(target)) {
                throw new ParamException(ApiStatus.PARAM_ERROR.getCode(), message);
            }
        }
    }

    private void checkNoeChinese(String target) {
        if (notChinese) {
            String regEx = "[\\u4e00-\\u9fa5]";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(target);
            boolean b = matcher.find();
            if (b) {
                throw new ParamException(ApiStatus.PARAM_ERROR.getCode(),message);
            }
        }
    }

}
