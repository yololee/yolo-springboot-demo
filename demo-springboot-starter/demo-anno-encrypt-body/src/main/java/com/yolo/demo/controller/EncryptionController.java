package com.yolo.demo.controller;


import com.alibaba.fastjson.JSON;
import com.yolo.demo.util.EncryptUtil;
import com.yolo.demo.util.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/encrypt")
@Slf4j
public class EncryptionController {


    /**
     * 将明文数据加密为密文，并且返回加密时的key
     * 其实就是模拟前端请求时的加密数据
     *
     * @param text 明文数据
     * @return
     */
    @GetMapping("/jia")
    public String jia(String text) {
        String key = UUIDUtils.getUuId().toUpperCase();
        String offset = key.substring(10, 26);
        String encrypt = EncryptUtil.encryptBySymmetry(text, offset, EncryptUtil.AES);
        // 写入header
        Map<String, String> map = new HashMap<>(2);
        // 模拟请求时的body，key为encrypt，value为实际请求参数的加密结果
        map.put("encrypt", encrypt);
        String body = JSON.toJSONString(map);

        Map<String, String> map2 = new HashMap<>(4);
        map2.put("key", key);
        map2.put("body", body);
        return JSON.toJSONString(map2);
    }

    /**
     * 通过key和加密后的密文，解密为明文
     * 前端请求时，发送的数据已经被加密了；并且加密的偏移量为key(32字符)的10-26个字符共16个字符
     *
     * @param text
     * @return
     */
    @GetMapping("/jie")
    public String jie(String key, String text) {
        String offset = key.substring(10, 26);
        return EncryptUtil.decryptBySymmetry(text, offset, EncryptUtil.AES);
    }
}
