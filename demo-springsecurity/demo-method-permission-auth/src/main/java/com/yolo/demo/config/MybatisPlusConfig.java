package com.yolo.demo.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Supplier;

@Configuration   //组件，添加到容器
@MapperScan("com.yolo.demo.mapper")  //开启mapper接口扫描
@EnableTransactionManagement
public class MybatisPlusConfig {
    /**
     * 3.4.0之后提供的拦截器的配置方式
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }


    @Component
    @Slf4j
    public static class MyMetaObjectHandler implements MetaObjectHandler {

        @Override
        public void insertFill(MetaObject metaObject) {
            this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐使用)

            // 日志输出 ================================================================================================
            LocalDateTime createTime = (LocalDateTime) this.getFieldValByName("createTime", metaObject);
            log.info(String.valueOf(createTime));
            if (createTime != null) {
                // 格式化为yyyy-MM-dd HH:mm:ss
                String format = createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                log.info("MyBatisPlus自动填充处理 - createTime:{} ", format);
            }
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐)
            // 日志输出 ================================================================================================
            LocalDateTime updateTime = (LocalDateTime) this.getFieldValByName("updateTime", metaObject);
            if (updateTime != null) {
                // 格式化为yyyy-MM-dd HH:mm:ss
                String format = updateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                log.info("MyBatisPlus自动填充处理 - updateTime:{} ", format);
            }
        }

        @Override
        public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
            Object obj = fieldVal.get();
            if (Objects.nonNull(obj)) {
                metaObject.setValue(fieldName, obj);
            }
            return this;
        }
    }

}
