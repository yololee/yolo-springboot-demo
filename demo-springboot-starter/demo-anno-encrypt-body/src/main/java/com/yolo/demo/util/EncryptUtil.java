package com.yolo.demo.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class EncryptUtil {
    public final static String AES = "AES";

    /**
     * 设置为CBC加密，默认情况下ECB比CBC更高效
     */
    private final static String CBC = "/CBC/PKCS5Padding";


    public static void main(String[] args) {
        /**
         * 偏移量，AES 128位数据块对应偏移量为16位，任意值即可
         */
        String offset = "5LiN6KaB56C06Kej";

        String input = "123";
        String encryptAes = encryptBySymmetry(input, offset, EncryptUtil.AES);
        System.out.println("AES加密:" + encryptAes);
        String aes = decryptBySymmetry(encryptAes, offset, EncryptUtil.AES);
        System.out.println("AES解密:" + aes);
    }

    /**
     * 对称加密
     *
     * @param input     : 密文
     * @param offset    : 偏移量
     * @param algorithm : 类型：DES、AES
     * @return
     */
    public static String encryptBySymmetry(String input, String offset, String algorithm) {
        try {
            // 根据加密类型判断key字节数
            checkAlgorithmAndKey(offset, algorithm);

            // CBC模式
            String transformation = algorithm + CBC;
            // 获取加密对象
            Cipher cipher = Cipher.getInstance(transformation);
            // 创建加密规则
            // 第一个参数key的字节
            // 第二个参数表示加密算法
            SecretKeySpec sks = new SecretKeySpec(offset.getBytes(), algorithm);

            // 使用CBC模式
            IvParameterSpec iv = new IvParameterSpec(offset.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, sks, iv);


            // 加密
            byte[] bytes = cipher.doFinal(input.getBytes());

            // 输出加密后的数据
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("加密失败！");
        }
    }

    /**
     * 对称解密
     *
     * @param input     : 密文
     * @param offset    : 偏移量
     * @param algorithm : 类型：DES、AES
     * @return
     */
    public static String decryptBySymmetry(String input, String offset, String algorithm) {
        try {
            // 根据加密类型判断key字节数
            checkAlgorithmAndKey(offset, algorithm);

            // CBC模式
            String transformation = algorithm + CBC;

            // 1,获取Cipher对象
            Cipher cipher = Cipher.getInstance(transformation);
            // 指定密钥规则
            SecretKeySpec sks = new SecretKeySpec(offset.getBytes(), algorithm);
            // 默认采用ECB加密：同样的原文生成同样的密文
            // CBC加密：同样的原文生成的密文不一样

            // 使用CBC模式
            IvParameterSpec iv = new IvParameterSpec(offset.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, sks, iv);

            // 3. 解密，上面使用的base64编码，下面直接用密文
            byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(input));
            //  因为是明文，所以直接返回
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("解密失败！");
        }
    }


    private static void checkAlgorithmAndKey(String key, String algorithm) {
        // 根据加密类型判断key字节数
        int length = key.getBytes().length;
        boolean typeEnable = false;
        if (AES.equals(algorithm)) {
            typeEnable = length == 16;
        } else {
            throw new RuntimeException("加密类型不存在");
        }
        if (!typeEnable) {
            throw new RuntimeException("加密Key错误");
        }
    }
}
