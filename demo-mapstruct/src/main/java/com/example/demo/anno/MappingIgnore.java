package com.example.demo.anno;

import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * 映射忽略字段
 *
 * @author jujueaoye
 * @date 2023/06/16
 */
@Retention(RetentionPolicy.CLASS)
@Mappings(value = {
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "createTime", ignore = true),
        @Mapping(target = "updateTime", ignore = true),
        @Mapping(target = "deleteFlag", ignore = true),
        @Mapping(target = "deleteTime", ignore = true)
})

public @interface MappingIgnore {
}
