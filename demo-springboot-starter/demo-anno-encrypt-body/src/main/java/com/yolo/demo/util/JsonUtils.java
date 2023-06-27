package com.yolo.demo.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * json工具类
 * 1、使用@JsonPropertyOrder调整属性的序列化顺序
 * 2、使用@JsonFormat为某个属性设置序列化方式(@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss"))
 * 3、使用@JsonProperty修改属性名称
 * 4、使用@JsonInclude使属性值为null不参与序列化(@JsonInclude(value= JsonInclude.Include.NON_NULL))
 * 5、使用@JsonIgnore使某个属性不参与序列化
 */
@Slf4j
public class JsonUtils {
    private JsonUtils() {
        throw new IllegalStateException("JsonUtil class");
    }

    private static final ObjectMapper MAPPER;

    static {
        //创建ObjectMapper对象
        MAPPER = new ObjectMapper();

        //configure方法 配置一些需要的参数
        // 转换为格式化的json 显示出来的格式美化
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);

        //序列化的时候序列对象的那些属性
        //JsonInclude.Include.NON_DEFAULT 属性为默认值不序列化
        //JsonInclude.Include.ALWAYS      所有属性
        //JsonInclude.Include.NON_EMPTY   属性为 空（“”） 或者为 NULL 都不序列化
        //JsonInclude.Include.NON_NULL    属性为NULL 不序列化
        MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);


        //反序列化时,遇到未知属性会不会报错
        //true - 遇到没有的属性就报错 false - 没有的属性不会管，不会报错
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //如果是空对象的时候,不抛异常
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // 忽略 transient 修饰的属性
        MAPPER.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);

        //修改序列化后日期格式
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        //处理不同的时区偏移格式
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MAPPER.registerModule(new JavaTimeModule());
    }


    /**
     * 对象转json
     *
     * @param obj 对象
     * @return {@link String}
     */
    public static String objectToJson(Object obj){
        try {
            if (Objects.nonNull(obj)){
                return MAPPER.writeValueAsString(obj);
            }
        } catch (IOException e) {
            log.error("将[{}]转换为JSON时，出现异常", obj.getClass().getName(), e);
            throw new RuntimeException(e);
        }
        return null;
    }


    /**
     * json转对象(这个对象需要有无参构造方法)
     *
     * @param json        json
     * @param resultClazz 结果clazz
     * @return {@link T}
     */
    public static <T> T jsonToObject(String json, Class<T> resultClazz) {
        try {
            if (StringUtils.hasText(json)){
                return MAPPER.readValue(json, resultClazz);
            }
        } catch (IOException e) {
            log.error("JSON数据：[{}]转换对象失败", json, e);
            throw new RuntimeException(e);
        }
        return null;
    }


    /**
     * json转List
     *
     * @param json        json
     * @param resultClazz 结果clazz
     * @return {@link List}<{@link T}>
     */
    public static <T> List<T> jsonToList(String json, Class<T> resultClazz) {
        try {
            if (StringUtils.hasText(json)){
                CollectionType listType = MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, resultClazz);
                return MAPPER.readValue(json, listType);
            }
        } catch (IOException e) {
            log.error("JSON数据：[{}]转换对象失败", json, e);
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * json转Set
     *
     * @param json        json
     * @param resultClazz 结果clazz
     * @return {@link Set}<{@link T}>
     */
    public static <T> Set<T> jsonToSet(String json, Class<T> resultClazz) {
        try {
            if (StringUtils.hasText(json)){
                CollectionType setType = MAPPER.getTypeFactory().constructCollectionType(HashSet.class, resultClazz);
                return MAPPER.readValue(json, setType);
            }
        } catch (IOException e) {
            log.error("JSON数据：[{}]转换对象失败", json, e);
            throw new RuntimeException(e);
        }
        return null;
    }


    /**
     * json转Arr
     *
     * @param json        json
     * @param resultClazz 结果clazz
     * @return {@link T[]}
     */
    public static <T> T[] jsonToArray(String json, Class<T> resultClazz) {
        try {
            if (StringUtils.hasText(json)){
                ArrayType arrayType = MAPPER.getTypeFactory().constructArrayType(resultClazz);
                return MAPPER.readValue(json, arrayType);
            }
        } catch (IOException e) {
            log.error("JSON数据：[{}]转换对象失败", json, e);
            throw new RuntimeException(e);
        }
        return null;
    }


    /**
     * json转HashMap
     *
     * @param json  json
     * @param clazz clazz
     * @return {@link Map}<{@link T}, {@link U}>
     */
    public static <T, U> Map<T, U> jsonToHashMap(String json, Class<U> clazz) {
        try {
            if (StringUtils.hasText(json)){
                MapType mapType = MAPPER.getTypeFactory().constructMapType(HashMap.class, String.class, clazz);
                return MAPPER.readValue(json, mapType);
            }
        } catch (IOException e) {
            log.error("JSON数据：[{}]转换对象失败", json, e);
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * bean转map
     *
     * @param obj obj
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public static Map<String, Object> beanToMap(Object obj) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            if (Objects.nonNull(obj)) {
                Field[] fields = obj.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    map.put(field.getName(), field.get(obj));
                }
            }
        } catch (Exception e) {
            log.error("对象：[{}]转换Map失败", obj, e);
            throw new RuntimeException(e);
        }
        return map;
    }

    /**
     * map转bean
     *
     * @param map      map
     * @param beanType bean类型
     * @return {@link T}
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanType) {
        return !CollectionUtils.isEmpty(map) ? MAPPER.convertValue(map, beanType) : null;
    }

}