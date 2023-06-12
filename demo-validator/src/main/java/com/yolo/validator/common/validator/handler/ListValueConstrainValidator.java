package com.yolo.validator.common.validator.handler;

import com.yolo.validator.common.validator.annotation.ListValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ListValueConstrainValidator implements ConstraintValidator<ListValue,Integer> {
    
    private final Set<Integer> set = new HashSet<>();

    private Boolean  required;

    /**
     * 初始化
     *
     * @param constraintAnnotation 约束注释
     */
    @Override
    public void initialize(ListValue constraintAnnotation) {
        Arrays.stream(constraintAnnotation.values()).filter(Objects::nonNull).forEach(set::add);
        required = constraintAnnotation.required();
    }


    /**
     * 校验
     *
     * @param value           需要校验的值
     * @param constraintValidatorContext 约束验证器上下文
     * @return boolean
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        // 注解表明为必选项 则不允许为空，否则可以为空
        if (value == null) {
            return !required;
        }

        return set.contains(value);
    }
}