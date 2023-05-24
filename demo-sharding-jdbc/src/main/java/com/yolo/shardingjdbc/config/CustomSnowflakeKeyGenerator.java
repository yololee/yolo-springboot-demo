package com.yolo.shardingjdbc.config;

import cn.hutool.core.lang.Snowflake;
import io.shardingsphere.core.keygen.KeyGenerator;

/**
 * 自定义雪花算法，替换 DefaultKeyGenerator，避免DefaultKeyGenerator生成的id大几率是偶数
 */
public class CustomSnowflakeKeyGenerator implements KeyGenerator {
    private final Snowflake snowflake;

    public CustomSnowflakeKeyGenerator(Snowflake snowflake) {
        this.snowflake = snowflake;
    }

    @Override
    public Number generateKey() {
        return snowflake.nextId();
    }
}