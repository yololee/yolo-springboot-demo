package com.yolo.file.config;

import com.yolo.file.common.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源映射
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer {

    /**
     * Spring Boot 访问静态资源的位置(优先级按以下顺序)
     * classpath默认就是resources,所以classpath:/static/ 就是resources/static/
     * classpath:/META‐INF/resources/
     * classpath:/resources/
     * classpath:/static/
     * classpath:/public/
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler(Constants.RESOURCE_PREFIX + "/**")   //指的是对外暴露的访问路径
                .addResourceLocations("file:" + YoloConfig.getUploadPath()); //指的是内部文件放置的目录
    }
}
