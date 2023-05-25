package com.yolo.validator.common.validator.handler;

import cn.hutool.core.util.ObjectUtil;
import com.yolo.validator.common.validator.annotation.VerifyIntegerCollectionDataValid;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName VerifyIntegerCollectionDataValidator
 * @Description 自定义校验类，校验List<Integer> 数据是否合法
 * @Author hl
 * @Date 2022/11/8 10:27
 * @Version 1.0
 */
public class VerifyIntegerCollectionDataValidator implements ConstraintValidator<VerifyIntegerCollectionDataValid, List<Integer>> {

    private List<Integer> list = new ArrayList<>();

    @Override
    public void initialize(VerifyIntegerCollectionDataValid verifyIntegerCollectionDataValid) {
        // 获取注解中的值
        int[] values= verifyIntegerCollectionDataValid.values();
        // 赋值给全局变量
        list = Arrays.stream(values).boxed().collect(Collectors.toList());
    }

    @Override
    public boolean isValid(List<Integer> integers, ConstraintValidatorContext constraintValidatorContext) {
        if (ObjectUtil.isNull(integers)){
            return true;
        }

        Integer level = integers.stream().filter(Objects::nonNull).filter(s -> !list.contains(s)).findAny().orElse(null);
        return ObjectUtil.isNull(level);
    }
}
