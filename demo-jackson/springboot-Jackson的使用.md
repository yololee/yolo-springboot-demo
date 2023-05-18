# springboot-Jackson的使用

## 一、全局时间配置

未配置之前

```java
    @GetMapping("/jackson/type1/res")
    public Model res() {
        return Model.builder().id(1).age(12).name("zhangsan").createTime(new Date()).build();
    }
```

![image-20230518151131046](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230518151131046.png)

**配置全局时间**

```properties
spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
```

![image-20230518151240516](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230518151240516.png)

## 二、使用`@JsonFormat`为某个属性设置序列化方式

```java
@Data
@Builder
public class Model {
    private Integer id;
    private int age;
    private String name;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private Date createTime;
}
```

```java
    @GetMapping("/jackson/type1/res2")
    public Model res2() {
        return Model.builder().id(1).age(12).name("zhangsan").createTime(new Date()).build();
    }
```

![image-20230518151519571](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230518151519571.png)

## 三、使用`@JsonPropertyOrder`调整属性的序列化顺序

```java
@Data
@Builder
@JsonPropertyOrder(value={"name", "age"})
public class Model {
    private Integer id;
    private int age;
    private String name;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private Date createTime;
}
```

![image-20230518151843773](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230518151843773.png)

## 四、使用`@JsonProperty`修改属性名称

```java
@Data
@Builder
@JsonPropertyOrder(value={"name", "age"})
public class Model {
    private Integer id;
    private int age;
    @JsonProperty("myName")
    private String name;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private Date createTime;
}
```

![image-20230518152003825](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230518152003825.png)

## 五、使用`@JsonInclude`使属性值为null不参与序列化

```java
@Data
@Builder
@JsonPropertyOrder(value={"name", "age"})
public class Model {
    @JsonInclude(value= JsonInclude.Include.NON_NULL)
    private Integer id;
    private int age;
    @JsonProperty("myName")
    private String name;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private Date createTime;
}
```

```java
    @GetMapping("/jackson/type1/res3")
    public Model res3() {
        return Model.builder().age(12).name("zhangsan").createTime(new Date()).build();
    }
```

![image-20230518152225978](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230518152225978.png)

## 六、使用`@JsonIgnore`使某个属性不参与序列化

```java
@Data
@Builder
@JsonPropertyOrder(value={"name", "age"})
public class Model {
    @JsonInclude(value= JsonInclude.Include.NON_NULL)
    private Integer id;
    @JsonIgnore
    private int age;
    @JsonProperty("myName")
    private String name;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private Date createTime;
}
```

![image-20230518152505843](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230518152505843.png)

