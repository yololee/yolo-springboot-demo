package com.yolo.demo.handler;


import com.yolo.demo.annotation.DecryptRequest;
import com.yolo.demo.annotation.EncryptResponse;
import org.springframework.core.MethodParameter;

import java.util.Objects;


public class EncryptionHandler {

    public static String IGNORE_HEADER_NAME = "ignoreParam";
    public static String ENCRYPT_KEY = "encrypt";

    /**
     * 判断是否对响应参数进行加密
     *
     * @param returnType
     * @return
     */
    public static boolean checkEncrypt(MethodParameter returnType) {
        boolean flag = false;
        EncryptResponse classPresent = returnType.getContainingClass().getAnnotation(EncryptResponse.class);
        if (classPresent != null) {
            // 类上标注的是否需要解密
            flag = classPresent.value();

        }
        EncryptResponse methodPresent = Objects.requireNonNull(returnType.getMethod()).getAnnotation(EncryptResponse.class);
        if (methodPresent != null) {
            // 方法上标注的是否需要解密
            flag = methodPresent.value();
        }
        return flag;
    }

    /**
     * 判断是否对请求参数解密
     *
     * @param returnType
     * @return
     */
    public static boolean checkDecrypt(MethodParameter returnType) {
        try {
            boolean flag = false;
            DecryptRequest classPresent = returnType.getContainingClass().getAnnotation(DecryptRequest.class);
            if (classPresent != null) {
                //类上标注的是否需要解密
                flag = classPresent.value();
            }

            // 判断方法上是否有加密注解
            DecryptRequest methodPresent = Objects.requireNonNull(returnType.getMethod()).getAnnotation(DecryptRequest.class);
            if (methodPresent != null) {
                //方法上标注的是否需要解密
                flag = methodPresent.value();
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
