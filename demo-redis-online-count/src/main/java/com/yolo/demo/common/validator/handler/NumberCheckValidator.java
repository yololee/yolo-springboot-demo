package com.yolo.demo.common.validator.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.yolo.demo.common.validator.annotation.NumberCheck;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NumberCheckValidator implements ConstraintValidator<NumberCheck, Object> {

    private Boolean  required;
    private int min;
    private int max;
    private int maxScale;

    @Override
    public void initialize(NumberCheck numberCheck) {
        this.required = numberCheck.required();
        this.min = numberCheck.min();
        this.max = numberCheck.max();
        this.maxScale = numberCheck.maxScale();
    }

    @Override
    public boolean isValid(Object target, ConstraintValidatorContext constraintValidatorContext) {
        if (ObjectUtil.isNull(target)  || Convert.toInt(target) <= 0) {
            return !required;
        }

        if (target instanceof Integer){
            int tar = (Integer) target;

            if (!required && (tar > min || tar < max)){
                return Boolean.TRUE;
            }
        }

        if (target instanceof Long){
            long tar = (Long) target;


            if (!required && (tar > min || tar < max)){
                return Boolean.TRUE;
            }
        }

        if (target instanceof Double){
            double tar = (Double) target;

            if (!required && (tar < min || tar > max)){
                return Boolean.FALSE;
            }

            int length = String.valueOf(tar).split("\\.")[1].length();
            if (!required &&  length <= maxScale){
                return Boolean.TRUE;
            }
        }

        return false;
    }
}
