package com.yolo.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 全局配置类
 */
@Component
@ConfigurationProperties(prefix = "yolo")
public class YoloConfig {
    /**
     * 上传路径
     */
    private static String fileUploadPath;

    /**
     * 获取地址开关
     */
    private static boolean addressEnabled;

    public static String getFileUploadPath() {
        return fileUploadPath;
    }

    public void setFileUploadPath(String fileUploadPath) {
        YoloConfig.fileUploadPath = fileUploadPath;
    }

    public static boolean isAddressEnabled() {
        return addressEnabled;
    }

    public void setAddressEnabled(boolean addressEnabled) {
        YoloConfig.addressEnabled = addressEnabled;
    }

    /**
     * 获取导入上传路径
     */
    public static String getImportPath() {
        return getFileUploadPath() + "/import";
    }


    /**
     * 获取上传路径
     */
    public static String getUploadPath() {
        return getFileUploadPath() + "/upload";
    }
}
