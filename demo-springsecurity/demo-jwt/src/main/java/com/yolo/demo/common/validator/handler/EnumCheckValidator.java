package com.yolo.demo.common.validator.handler;


import com.yolo.demo.common.validator.annotation.EnumCheck;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;


@Slf4j
public class EnumCheckValidator implements ConstraintValidator<EnumCheck, Object> {


    private EnumCheck enumCheck;

    @Override
    public void initialize(EnumCheck enumCheck) {
        this.enumCheck = enumCheck;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        // 注解表明为必选项 则不允许为空，否则可以为空
        if (value == null) {
            return !this.enumCheck.required();
        }

        Boolean result = Boolean.FALSE;
        Class<?> valueClass = value.getClass();
        try {
            //通过反射执行枚举类中validation方法
            Method method = this.enumCheck.enumClass().getMethod(this.enumCheck.enumMethod(), valueClass);
            result = (Boolean) method.invoke(null, value);
            if (result == null) {
                return false;
            }
        } catch (Exception e) {
            log.error("custom EnumCheckValidator error", e);
        }
        return result;
    }
}

