# springboot整合MapStruct

## 一、介绍

MapStruct是一个Java注释处理器，用于生成类型安全的bean映射类

您要做的就是定义一个映射器接口，该接口声明任何必需的映射方法。在编译期间，MapStruct将生成此接口的实现。此实现使用简单的Java方法调用在源对象和目标对象之间进行映射，即没有反射或类似内容

与手动编写映射代码相比，MapStruct通过生成繁琐且易于出错的代码来节省时间。遵循配置方法上的约定，MapStruct使用合理的默认值，但在配置或实现特殊行为时不加理会

**下载插件**

![image-20230607094648115](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230607094648115.png)

**在参数上，按 ctrl + 鼠标左键 ，能够自动进入参数所在类文件**

![image-20230607094747459](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230607094747459.png)

## 二、快速入门

### 1、pom.xml

**Lombok依赖：（版本最好在1.16.16以上，否则会出现问题）**

```xml
        <!--mapstruct核心-->
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>1.4.2.Final</version>
        </dependency>
        <!--mapstruct编译-->
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct-processor</artifactId>
            <version>1.4.2.Final</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.24</version>
        </dependency>
```

防止整合lombok使用MapStruct转换对象出现找不到属性错误

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <encoding>UTF-8</encoding>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.mapstruct</groupId>
                            <artifactId>mapstruct-processor</artifactId>
                            <version>1.4.2.Final</version>
                        </path>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>1.18.24</version>
                        </path>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok-mapstruct-binding</artifactId>
                            <version>0.2.0</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
```

### 2、实体类

```java
@Data
public class UserDto {
    private String username;
    private int age;
    private boolean young;
    private String address;
    private Date createTime;
    private BigDecimal source;
    private double height;
}

@Data
public class User {
    private String username;
    private int age;
    private boolean young;
    private String address;
    private Date createTime;
    private BigDecimal source;
    private double height;
}
```

### 3、创建映射器

1. 当一个属性与其目标实体对应的名称相同时，它将被隐式映射。
2. 当属性在目标实体中具有不同的名称时，可以通过@Mapping注释指定其名称

```java
package com.example.demo.mapstruct;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConvertMapper {

    UserConvertMapper INSTANCE = Mappers.getMapper(UserConvertMapper.class);

    @Mapping(target = "username", source = "username")
    @Mapping(target = "age", ignore = true) // 忽略id，不进行映射
    User convert(UserDto userDto);
}
```

> 如果不指定@Mapping，默认映射name相同的field
> 如果映射的对象field name不一样，通过 @Mapping 指定
>
> 忽略字段加@Mapping #ignore() = true

### 4、测试

```java
    @Test
    public void quickStart() {
        UserDto dto = new UserDto();
        dto.setUsername("jack");
        dto.setAge(23);
        dto.setYoung(false);

        User user = UserConvertMapper.INSTANCE.convert(dto);
        System.out.println(user);//User(username=jack, age=0, young=false, address=null, createTime=null, source=null, height=0.0)
    }
```

## 三、俩个类之间的转换

### 指定默认值

在@Mapper接口类里面的转换方法上添加@Mapping注解，target() 必须添加，source()可以不添加，则直接使用defaultValue

```java
    @Mapping(target = "address",source = "address",defaultValue = "武汉")
    void dto2Entity(UserDto userDto, @MappingTarget User user);
```

### 使用表达式

目前java是唯一受支持的语言，达式必须以Java表达式的形式给出
注意： 这个属性不能与source()、defaultValue()、defaultExpression()、qualifiedBy()、qualifiedByName()或constant()一起使用

```java
    @Mapping(target = "createTime",expression = "java(new java.util.Date())")
    void dto2Entity(UserDto userDto, @MappingTarget User user);
```

```java
    @Test
    public void test1() {
        UserDto dto = new UserDto();
        dto.setUsername("jack");
        dto.setAge(23);
        dto.setYoung(false);

        User user = new User();
        UserConvertMapper.INSTANCE.dto2Entity(dto,user);

        System.out.println(user);//User(username=jack, age=23, young=false, address=武汉, createTime=Wed Jun 07 10:40:45 CST 2023, source=null, height=0.0)
    }
```

### dateFormat()

如果属性从字符串映射到日期，则该格式字符串可由SimpleDateFormat处理，反之亦然。当映射枚举常量时，将忽略所有其他属性类型。

```java
    @Mapping(target = "address",source = "address",defaultValue = "武汉")
    @Mapping(target = "createTime",source = "createTime",dateFormat = "yyyy-MM-dd")
    void dto2Entity2(UserDto userDto, @MappingTarget User user);
```

### 使用自定义方法

#### 自定义类型转换方法

```java
@Data
public class PersonDto {

    private String phones;
}

@Data
public class Person {

    private List<String> phoneList;
}
```

在进行类型转换的时候直接调用改转换方法

`@Mapper#uses`可以使用多个类

```java
@Mapper(uses = ListUtil.class)
public interface PersonConvertMapper {

    PersonConvertMapper INSTANCE = Mappers.getMapper(PersonConvertMapper.class);

    @Mapping(target = "phoneList",expression = "java(ListUtil.stringToListString(personDto.getPhones(),separator))")
    Person dtoToEntity(PersonDto personDto,String separator);
}

```

```java
    @Test
    public void test1(){
        PersonDto personDto = new PersonDto();
        personDto.setPhones("1,2,3,4");
        Person person = PersonConvertMapper.INSTANCE.dtoToEntity(personDto,",");
        System.out.println(person);
    }
```

#### 使用注解`@namd`

```java
public class ListUtil {
    
    public static List<String> stringToListString(String str,String separator) {
        return Arrays.asList(StrUtil.split(str, separator));
    }

    @Named("listStringToString")
    public static String listStringToString(List<String> strList){
        return StrUtil.join(",",strList);
    }
}

```

```java
    @Mapping(target = "phones",source = "phoneList",qualifiedByName = "listStringToString")
    PersonDto entityToDto(Person person);
```

```java
    @Test
    public void test2(){
        Person person = new Person();
        person.setPhoneList(ListUtil.of("1,2,3"));
        PersonDto personDto = PersonConvertMapper.INSTANCE.entityToDto(person);
        System.out.println(personDto);//PersonDto(phones=1,2,3)
    }
```

### 多个源对象

```java
@Data
public class BasicEntity {

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

}
```

```java
    @Mapping(target = "createTime",source = "entity.createTime",dateFormat = "yyyy-MM-dd")
    User dtoToEntity2(UserDto userDto, BasicEntity entity);
```

```java
    @Test
    public void test13() {
        UserDto dto = new UserDto();
        dto.setUsername("jack");
        dto.setAge(23);
        dto.setYoung(false);
        dto.setCreateTime(new Date(1685548800000L));//2023-06-01


        BasicEntity basicEntity = new BasicEntity();
        basicEntity.setCreateTime(new Date());

        User user = UserConvertMapper.INSTANCE.dtoToEntity2(dto, basicEntity);

        System.out.println(user);//User(username=jack, age=23, young=false, address=null, createTime=2023-06-07, source=null, height=0.0)
    }
```

### 嵌套映射

>  如果field name一样则不需要指定@Mapping

```java
@Data
public class UserDto {
		...
    private PersonDto personDto;
}

@Data
public class User {
		...
    private PersonDto personDto;
}
```

```java
    @Mapping(target = "address",source = "address",defaultValue = "武汉")
    @Mapping(target = "createTime",source = "createTime",dateFormat = "yyyy-MM-dd")
    @Mapping(target = "personDto",source = "personDto")
    User dtoToEntity3(UserDto userDto);
```

```java
    @Test
    public void test14() {
        UserDto dto = new UserDto();
        dto.setUsername("jack");
        dto.setAge(23);
        dto.setYoung(false);
        dto.setCreateTime(new Date(1685548800000L));//2023-06-01

        PersonDto personDto = new PersonDto();
        personDto.setPhones("1,2,3,4");
        dto.setPersonDto(personDto);

        User user = UserConvertMapper.INSTANCE.dtoToEntity3(dto);
        //User(username=jack, age=23, young=false, address=武汉, createTime=2023-06-01, source=null, height=0.0, personDto=PersonDto(phones=1,2,3,4))
        System.out.println(user);
    }
```

### numberFormat()

如果带注释的方法从数字映射到字符串，则使用DecimalFormat将格式字符串作为可处理的格式。反之亦然。对于所有其他元素类型，将被忽略。

```java
@Mapping(target = "age",source = "age", numberFormat = "#0.00")
PersonDTO conver(Person person);
```

## 四、Map映射

mapStruct还支持map集合的转换，可以对map进行隐式转换，支持对Key与value进行隐式转换

例如：日期转字符串，字符串转日期；同样支持@name注解转换形式，或使用qualifiedBy指定一个转换类（类中默认使用相同入参出参来匹配）

```java
@Mapper(componentModel = "spring", uses = {ConverterUtil.class})
public interface MapConvertMapper {

    @MapMapping(keyQualifiedByName = "getValue", valueDateFormat = "dd.MM.yyyy")
    Map<String, String> longDateMapToStringStringMap(Map<Long, Date> source);
}


public class ConverterUtil {
    
    @Named("getValue")
    public static String getValue(Long l){
        return DateUtil.format(new Date(l), "yyyy/MM/dd");
    }
}
```

```java
    @Autowired
    private MapConvertMapper mapConvertMapper;

    @Test
    public void test1(){
        Map<Long, Date> map = new HashMap<>();
        map.put(1685548800000L,new Date());

        Map<String, String> map1 = mapConvertMapper.longDateMapToStringStringMap(map);
        System.out.println(map1);//{2023/06/01=07.06.2023}

    }
```

## 五、集成到 spring

在`@Mapper#componentModel` 中指定依赖注入框架

```java
@Mapper(componentModel = "spring", uses = {ConverterUtil.class})
public interface MapConvertMapper {

    @MapMapping(keyQualifiedByName = "getValue", valueDateFormat = "dd.MM.yyyy")
    Map<String, String> longDateMapToStringStringMap(Map<Long, Date> source);
}

    @Autowired
    private MapConvertMapper mapConvertMapper;

    @Test
    public void test1(){
        Map<Long, Date> map = new HashMap<>();
        map.put(1685548800000L,new Date());

        Map<String, String> map1 = mapConvertMapper.longDateMapToStringStringMap(map);
        System.out.println(map1);//{2023/06/01=07.06.2023}

    }
```

