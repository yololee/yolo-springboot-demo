# springboot整合MapStructPlus

## 一、介绍

Mapstruct(https://mapstruct.org/) 是一个代码生成器，通过定义类转换的接口，自动实现属性转换的具体逻辑。主要为了简化 Java 类型之间转换的实现

Mapstruct Plus 是 Mapstruct 的增强工具，在 Mapstruct 的基础上，实现了自动生成 Mapper 接口的功能，并强化了部分功能，使 Java 类型转换更加便捷、优雅

和 Mapstruct 一样，本质上都是一个基于 JSR 269 的 Java 注释处理器，因此可以由 Maven、Gradle、Ant 等来构建触发

Mapstruct Plus 内嵌 Mapstruct，和 Mapstruct 完全兼容，如果之前已经使用 Mapstruct，可以无缝替换依赖

## 二、整合MapStructPlus

### pom.xml

```xml
        <dependency>
            <groupId>io.github.linpeilie</groupId>
            <artifactId>mapstruct-plus-spring-boot-starter</artifactId>
            <version>1.2.2</version>
        </dependency>
```

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
                            <groupId>io.github.linpeilie</groupId>
                            <artifactId>mapstruct-plus-processor</artifactId>
                            <version>1.2.2</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
```

### 转换对象

> userDto

```java
package com.yolo.demo.dto;

import com.yolo.demo.entity.User;
import io.github.linpeilie.annotations.AutoMapper;


@AutoMapper(target = User.class)
public class UserDto {
    private String username;
    private int age;
    private boolean young;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isYoung() {
        return young;
    }

    public void setYoung(boolean young) {
        this.young = young;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "username='" + username + '\'' +
                ", age=" + age +
                ", young=" + young +
                '}';
    }
}
```

> user

```java
public class User {
    private String username;
    private int age;
    private boolean young;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isYoung() {
        return young;
    }

    public void setYoung(boolean young) {
        this.young = young;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", age=" + age +
                ", young=" + young +
                '}';
    }
}
```

### 测试

```java
package com.yolo.demo;

import com.yolo.demo.dto.UserDto;
import com.yolo.demo.entity.User;
import io.github.linpeilie.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = DemoMapstructPlusApplication.class)
@RunWith(SpringRunner.class)
public class DemoMapstructPlusApplicationTests {


    @Autowired
    private Converter converter;

    @Test
    public void quickStart() {

        User user = new User();
        user.setUsername("jack");
        user.setAge(23);
        user.setYoung(false);

        UserDto userDto = converter.convert(user, UserDto.class);
        System.out.println(userDto);    // UserDto{username='jack', age=23, young=false}

        assert user.getUsername().equals(userDto.getUsername());
        assert user.getAge() == userDto.getAge();
        assert user.isYoung() == userDto.isYoung();

        User newUser = converter.convert(userDto, User.class);

        System.out.println(newUser);    // User{username='jack', age=23, young=false}
    }

}
```

![image-20230606145127473](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230606145127473.png)

## 三、整合Lombok

### lombok 1.18.16 之前

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
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>${lombok.version}</version>
                    </path>
                    <path>
                        <groupId>io.github.linpeilie</groupId>
                        <artifactId>mapstruct-plus-processor</artifactId>
                        <version>${mapstruct-plus.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### lombok 1.18.16 之后

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
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>${lombok.version}</version>
                    </path>
                    <path>
                        <groupId>io.github.linpeilie</groupId>
                        <artifactId>mapstruct-plus-processor</artifactId>
                        <version>${mapstruct-plus.version}</version>
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

## 四、两个类之间的转换

### 简单转换

**要实现两个类之间的转换，只需要在其中一个类上增加注解 `@AutoMapper` ，配置 `target` 属性，指定目标类即可**

```java
@AutoMapper(target = CarDto.class)
public class Car {
    // ...
}
```

该例子表示，会生成 `Car` 转换为 `CarDto` 的接口 `CarToCarDtoMapper` 及实现类 `CarToCarDtoMapperImpl`。在生成的转换代码中，源类型（`Car`）的所有可读属性将被复制到目标属性类型（`CarDto`）的相应属性中。

当一个属性与它的目标实体对应物具有相同的名称时，将会被隐式映射。

除此之外，MapstructPlus 会根据当前的默认规则，生成 `CarDto` 转换为 `Car` 的接口 `CarDtoToCarMapper` 及实现类 `CarDtoToCarMapperImpl`。如果不想生成该转换逻辑的话，可以通过注解的 `reverseConvertGenerate` 属性来配置

![image-20230606150801207](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230606150801207.png)

### 内嵌自定义类转换

```java
@Data
@AutoMapper(target = Car.class)
public class CarDto {
    private SeatConfigurationDto seatConfiguration;

    private String name;
}

@Data
@AutoMapper(target = SeatConfigurationDto.class)
public class SeatConfigurationDto {

    private Integer count;

}
```

```java
@Data
public class Car {
    private SeatConfiguration seatConfiguration;

    private String name;
}

@Data
public class SeatConfiguration {
    public Integer count;
}
```

```java
    @Autowired
    private Converter converter;

    @Test
    public void test1(){
        SeatConfigurationDto seatConfigurationDto = new SeatConfigurationDto();
        seatConfigurationDto.setCount(20);
        log.info("SeatConfigurationDto：{}",seatConfigurationDto);
        CarDto carDto = new CarDto();
        carDto.setName("大巴");
        carDto.setSeatConfiguration(seatConfigurationDto);
        log.info("CarDto：{}",carDto);

        Car car = converter.convert(carDto, Car.class);
        log.info("Car：{}",car);
    }
```

![image-20230606153221171](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230606153221171.png)

### 自定义类型转换器

当不同类型的属性，想要按照自定义的规则进行转换时，可以有两种办法：

1. 通过 `@AutoMapping` 中配置的 `expression` 表达式配置
2. 自定义一个类型转换器，通过 `@AutoMapper` 的 `uses` 属性来引入

```java
@Data
@AutoMapper(target = User.class,uses = StringToListString.class,reverseConvertGenerate = false)
public class UserDto {
    private String username;
    private int age;
    private boolean young;

    @AutoMapping(target = "educationList")
    private String educations;
}


@Data
public class User {
    private String username;
    private int age;
    private boolean young;

    private List<String> educationList;

}


@Component
public class StringToListString {

    /**
     * 字符串根据逗号转换为字符串集合
     *
     * @param str str
     * @return {@link List}<{@link String}>
     */
    public List<String> stringToListString(String str) {
        return StrUtil.split(str,",");
    }
}
```

```java
    @Autowired
    private Converter converter;

    @Test
    public void test1(){
        UserDto userDto = new UserDto();
        userDto.setEducations("1,2,3");

        log.info("userDto：{}",userDto);

        User user = converter.convert(userDto, User.class);
        log.info("Car：{}",user);
    }
```

![image-20230606163137964](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230606163137964.png)

### 自定义属性转换

#### 不同属性名称映射

`AutoMapping` 注解中，提供了 `target` 属性，可以配置当前属性与目标类中 `target` 属性之间映射。

例如，`Car` 转换为 `CatDto` 时，`seatConfiguration` 属性与 `seat` 属性相对应

```java
@AutoMapper(target = CarDto.class)
@Data
public class Car {
    @AutoMapping(target = "seat")
    private SeatConfiguration seatConfiguration;
}

```

`@AutoMapping` 注解中还提供 `source` 方法，该配置默认取当前属性的名称，之所以可以配置，是为了适应一种场景，当前类的某个属性，其内部的属性，转换为目标中的属性字段，则可以通过当前属性来配置

```java
@Data
@AutoMapper(target = GoodsVo.class, reverseConvertGenerate = false)
public class Goods {

    @AutoMapping(source = "seat.count", target = "price")
    private SeatConfiguration seat;

}

@Data
public class GoodsVo {

    private Integer price;

}
```

```java
    @Test
    public void test2(){
        SeatConfiguration seatConfiguration = new SeatConfiguration();
        seatConfiguration.setCount(30);

        Goods goods = new Goods();
        goods.setSeat(seatConfiguration);

        GoodsVo convert = converter.convert(goods, GoodsVo.class);
        System.out.println(convert);
    }
```

#### 指定时间格式转换

当时间类型（例如：`Date`、`LocalDateTime`、`LocalDate` 等等）需要和 `String` 通过指定时间格式进行转换时，可以通过 `@AutoMapping` 中的 `dateFormat` 来配置

```java
@Data
@AutoMapper(target = OrderEntity.class)
public class Order {

    @AutoMapping(dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    @AutoMapping(dateFormat = "yyyy_MM_dd HH:mm:ss")
    private Date createTime;

    @AutoMapping(target = "orderDate", dateFormat = "yyyy-MM-dd")
    private String date;

}

@Data
@AutoMapper(target = Order.class)
public class OrderEntity {

    @AutoMapping(dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String orderTime;

    @AutoMapping(dateFormat = "yyyy_MM_dd HH:mm:ss")
    private String createTime;

    @AutoMapping(target = "date", dateFormat = "yyyy-MM-dd")
    private LocalDate orderDate;

}
```

```java
    @Test
    public void test3(){
        Order order = new Order();
        order.setOrderTime(LocalDateTime.now());
        order.setCreateTime(new Date());
        order.setDate("2022-11-11");
        System.out.println(order);

        OrderEntity convert = converter.convert(order, OrderEntity.class);
        System.out.println(convert);
    }
```

![image-20230606164934792](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230606164934792.png)

#### 指定数字格式转换

当数字类型（例如：`int`/`Integer` 等数字基本类型及包装类、`BigDecimal`）和 `String` 之间的转换需要指定数字格式，可以通过 `@AutoMapping` 的 `numberFormat` 来配置

```java
@Data
@AutoMapper(target = OrderEntity.class)
public class Order {

    @AutoMapping(numberFormat = "0.00")
    private BigDecimal orderPrice;

    @AutoMapping(numberFormat = "0.00")
    private Integer goodsNum;
    
}

@Data
@AutoMapper(target = Order.class)
public class OrderEntity {

    @AutoMapping(numberFormat = "0.00")
    private String orderPrice;

    @AutoMapping(numberFormat = "0.00")
    private String goodsNum;
    
}
```

```java
    @Test
    public void test4() {
        Order order = new Order();
        order.setOrderPrice(new BigDecimal("1.54766536"));
        order.setGoodsNum(2);
        System.out.println(order);

        OrderEntity orderEntity = converter.convert(order, OrderEntity.class);
        System.out.println(orderEntity);

        Order order1 = converter.convert(orderEntity, Order.class);
        System.out.println(order1);
    }
```

![image-20230606165658054](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230606165658054.png)

#### 忽略指定属性的转换

当在进行转换时，需要忽略指定属性的转换，可以通过 `@AutoMapping` 的 `ignore` 来配置

```java
@AutoMapper(target = CarDto.class)
@Data
public class Car {

    @AutoMapping(target = "wheels", ignore = true)
    private Wheels wheels;
    
} 
```

#### 属性转换时的默认值

`@AutoMapping` 中的 `defaultValue` 可以指定在转换属性时，当属性为 `null` 时，转换到目标类中的默认值

```java
@Data
@AutoMapper(target = DefaultVo.class)
public class DefaultDto {

    @AutoMapping(defaultValue = "18")
    private Integer i;

    @AutoMapping(defaultValue = "1.32")
    private Double d;

    @AutoMapping(defaultValue = "true")
    private Boolean b;

}
```

## 五、Map 转对象

**当想要自动生成 `Map<String, Object>` 转为目标类的接口及实现类时，只需要在目标类上添加 `@AutoMapMapper` 注解**

```java
@AutoMapMapper
@Data
public class MapModelA {

    private String str;
    private int i1;
    private Long l2;
    private MapModelB mapModelB;

}

@AutoMapMapper
@Data
public class MapModelB {

    private Date date;

}

```

```java
		@Test
    public void test() {
        Map<String, Object> mapModel1 = new HashMap<>();
        mapModel1.put("str", "1jkf1ijkj3f");
        mapModel1.put("i1", 111);
        mapModel1.put("l2", 11231);

        Map<String, Object> mapModel2 = new HashMap<>();
        mapModel2.put("date", DateUtil.parse("2023-02-23 01:03:23"));

        mapModel1.put("mapModelB", mapModel2);

        final MapModelA mapModelA = converter.convert(mapModel1, MapModelA.class);
        System.out.println(mapModelA);  // MapModelA(str=1jkf1ijkj3f, i1=111, l2=11231, mapModelB=MapModelB(date=2023-02-23 01:03:23))
    }
```



