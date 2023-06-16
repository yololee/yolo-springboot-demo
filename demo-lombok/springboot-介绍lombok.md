# springboot-介绍lombok

## 一、前言

### 1、介绍

Lombok是一款Java开发插件，使得Java开发者可以通过其定义的一些注解来消除业务工程中冗长和繁琐的代码，尤其对于简单的Java模型对象（POJO）。

在开发环境中使用Lombok插件后，Java开发人员可以节省出重复构建，诸如hashCode和equals这样的方法以及各种业务对象模型的accessor和ToString等方法的大量时间。对于这些方法，它能够在编译源代码期间自动帮我们生成这些方法，并没有如反射那样降低程序的性能

### 2、引入依赖

```xml
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.26</version>
        </dependency>
```

## 二、常用注解

### @AllArgsConstructor和@NoArgsConstructor

@AllArgsConstructor：构建有参构造函数

@NoArgsConstructor：构建无参构造函数

```java
@AllArgsConstructor
@NoArgsConstructor
class Parent {
    private Integer id;
}

@AllArgsConstructor
public class Demo extends Parent{
    private String name;
    private int age;
}
```

![image-20230616151409514](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230616151409514.png)

### @RequiredArgsConstructor	

@RequiredArgsConstructor注解则会将类中所有带有`@NonNull注解 / org.jetbrains.annotations.NotNull注解`的或者带有`final修饰的成员变量`生成对应的构造方法

```java
@RequiredArgsConstructor
public class Demo1 {
    @NonNull
    private final int finalVal;
    @NonNull
    private String name;
    @NonNull
    private int age;
}
```

编译后的结果

> 解释：该注解会识别@NonNull字段和final修饰得字段，然后以该字段为元素生成一个构造函数
>
> 如果所有字段都没有@NonNull注解，那效果同@NoArgsConstructor 

```java
package com.yolo.demo.domain;

import lombok.NonNull;

public class Demo1 {
    private final @NonNull int finalVal;
    private @NonNull String name;
    private @NonNull int age;

    public Demo1(@NonNull int finalVal, @NonNull String name, @NonNull int age) {
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        } else {
            this.finalVal = finalVal;
            this.name = name;
            this.age = age;
        }
    }
}
```

### @Getter和@Setter

这一对注解从名字上就很好理解，用在成员变量上面或者类上面，相当于为成员变量生成对应的get和set方法，**同时还可以为生成的方法指定访问修饰符**，当然，默认为public

> 这两个注解直接用在类上，可以为此类里的所有非静态成员变量生成对应的get和set方法。**如果是final变量，那就只会有get方法**

```java
package com.yolo.demo.domain;

import lombok.Getter;
import lombok.Setter;

// 如果指定在类上,所有字段都会生成get/set方法
// 指定在字段上, 只有标注的字段才会生成get/set方法
@Getter
@Setter
public class Demo2 {
    private String name;
    private int age;
}

```

![image-20230616152324447](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230616152324447.png)

### @ToString和@EqualsAndHashCode

这两个注解也比较好理解，就是生成toString，equals和hashcode方法，同时后者还会生成一个canEqual方法，用于判断某个对象是否是当前类的实例。**生成方法时只会使用类中的非静态成员变量**

![image-20230616152504964](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230616152504964.png)

> 有些关键的属性，可以控制toString的输出，我们可以了解一下

```java
//@EqualsAndHashCode也有类似的下面的属性,
@ToString(
        includeFieldNames = true, //是否使用字段名
        exclude = {"name"}, //排除某些字段
        of = {"age"}, //只使用某些字段
        callSuper = true //是否让父类字段也参与 默认false
)
```

### @Data

相当于注解集合。效果等同于 **@Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor** 效果同和这5个注解的效果

> 需要注意的是，这里不包括@NoArgsConstructor和@AllArgsConstructor
>
> - 所以, 一般使用@Data时,要配合这两个注解一起使用

```java
@Data
public class Demo4 {
    private String name;
    private int age;
}
```

![image-20230616153248400](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230616153248400.png)

### @Builder

@Builder提供了一种比较推崇的构建值对象的方式; 非常推荐的一种构建值对象的方式

缺点就是父类的属性不能产于builder

> 标注@Builder的类, 会在类内部生成一个内部类,用于生成值对象

```java
@Builder
public class Demo5 {
    private final int finalVal = 10;
    private String name;
    private int age;
}
```

编译之后生成

```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.yolo.demo.domain;

public class Demo5 {
    private final int finalVal = 10;
    private String name;
    private int age;

    Demo5(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public static Demo5Builder builder() {
        return new Demo5Builder();
    }

    public static class Demo5Builder {
        private String name;
        private int age;

        Demo5Builder() {
        }

        public Demo5Builder name(String name) {
            this.name = name;
            return this;
        }

        public Demo5Builder age(int age) {
            this.age = age;
            return this;
        }

        public Demo5 build() {
            return new Demo5(this.name, this.age);
        }

        public String toString() {
            return "Demo5.Demo5Builder(name=" + this.name + ", age=" + this.age + ")";
        }
    }
}

```

使用方式

```java
public class Main {
    public static void main(String[] args) {
        Demo5 demo = Demo5.builder().name("zss").age(20).build();
        System.out.println(demo);
    }
}
```

> 一般我们给POJO类, 标注的Lombok注解, 百分之90就是这4个 : @Data, @NoArgsConstructor, @AllArgsConstructor, @Builder

### @Accessors

@Accessors 一个为getter和setter方法设计的更流畅的注解
这个注解要搭配@Getter与@Setter使用，用来修改默认的setter与getter方法的形式。

> **@Accessors属性详解**
>
> - fluent 属性 : 链式的形式 这个特别好用，方法连缀越来越方便了
> - chain 属性 : 流式的形式（若无显示指定chain的值，也会把chain设置为true）
> - prefix 属性 : 生成指定前缀的属性的getter与setter方法，并且生成的getter与setter方法时会去除前缀

测试不使用@Accessors时

![image-20230616154205755](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230616154205755.png)

**fluent属性**

默认为false，当该值为 true 时，对应字段的 getter 方法前面就没有 get，setter 方法就不会有 set。

![image-20230616154330887](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230616154330887.png)

使用

```java
public class Main2 {
    public static void main(String[] args) {
        Demo6 demo = new Demo6();
        // setter方法; 这里包含了chain=true的功能,可以链式设置值
        demo.xxName("lucky").yyAge(20);
        // getter方法
        System.out.println(demo.xxName() + "," + demo.yyAge());
        System.out.println("demo = " + demo);
    }
}
```

**chain属性**

不写默认为false，当该值为 true 时，**对应字段的 setter 方法调用后，会返回当前对象, 进行链式设置值**

![image-20230616154714086](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230616154714086.png)

**prefix属性**

该属性是一个字符串数组，当该数组有值时，表示忽略字段中对应的前缀，生成对应的 getter 和 setter 方法。

如果，我们把它的前缀加到 @Accessors 的属性值中，则可以像没有前缀那样，去调用字段的 getter和 setter 方法

![image-20230616154920846](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230616154920846.png)

## 三、其他注解

### @SneakyThrows

**这个注解用在方法上**，可以将方法中的代码用try-catch语句包裹起来，捕获异常并在catch中用Lombok.sneakyThrow(e)把异常抛出，可以使用@SneakyThrows(Exception.class)的形式指定抛出哪种异常

```java
public class Demo7 {

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        Integer data = getData(list);
        System.out.println(data);
    }

    @SneakyThrows(IndexOutOfBoundsException.class)
    public static Integer getData(List<Integer> list) {
        return list.get(2);
    }
}
```

编译结果

```java
public class Demo7 {
    public Demo7() {
    }

    public static void main(String[] args) {
        List<Integer> list = new ArrayList();
        list.add(1);
        list.add(2);
        Integer data = getData(list);
        System.out.println(data);
    }

    public static Integer getData(List<Integer> list) {
        try {
            return (Integer)list.get(2);
        } catch (IndexOutOfBoundsException var2) {
            throw var2;
        }
    }
}
```

### @Value

> @Value注解和@Data类似，区别在于它会把所有成员变量默认定义为private final修饰，并且不会生成set方法

### @Cleanup

> @Cleanup能够自动释放资源
>
> **这个注解用在`局部变量`上，可以保证此变量代表的资源会被自动关闭，默认是调用资源的close()方法**

如果该资源有其它关闭方法，可使用`@Cleanup(“methodName”)`来指定要调用的方法，就用输入输出流来举个例子吧：

```java
public class Demo8 {

    @SneakyThrows(Exception.class)
    public static void main(String[] args) {
        @Cleanup InputStream in = Files.newInputStream(Paths.get(args[0]));
        @Cleanup OutputStream out = Files.newOutputStream(Paths.get(args[1]));
        byte[] b = new byte[1024];
        while (true) {
            int r = in.read(b);
            if (r == -1) {
                break;
            }
            out.write(b, 0, r);
        }
    }
}
```

编译结果

```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.yolo.demo.domain;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class Demo8 {
    public Demo8() {
    }

    public static void main(String[] args) {
        try {
            InputStream in = Files.newInputStream(Paths.get(args[0]));

            try {
                OutputStream out = Files.newOutputStream(Paths.get(args[1]));

                try {
                    byte[] b = new byte[1024];

                    while(true) {
                        int r = in.read(b);
                        if (r == -1) {
                            return;
                        }

                        out.write(b, 0, r);
                    }
                } finally {
                    if (Collections.singletonList(out).get(0) != null) {
                        out.close();
                    }

                }
            } finally {
                if (Collections.singletonList(in).get(0) != null) {
                    in.close();
                }

            }
        } catch (Exception var15) {
            throw var15;
        }
    }
}
```

### @NotNull

**这个注解可以用在成员方法或者构造方法的参数上**，会自动产生一个关于此参数的非空检查，如果参数为空，则抛出一个空指针异常

```java
//成员方法参数加上@NonNull注解
public String getName(@NonNull Person p){
    return p.getName();
}
```

编译结果

```java
public String getName(@NonNull Person p){
    if(p == null){
        throw new NullPointerException("person");
    }
    return p.getName();
}
```

### @Synchronized

这个注解用在类方法或者实例方法上，效果和synchronized关键字相同，区别在于锁对象不同，对于类方法和实例方法，synchronized关键字的锁对象分别是类的class对象和this对象

```java
public class Demo9 {
    private Object obj;

    @Synchronized
    public static void hello() {
        System.out.println("world");
    }

    @Synchronized
    public int answerToLife() {
        return 42;
    }

    @Synchronized("obj")
    public void foo() {
        System.out.println("bar");
    }
}
```

编译结果

```java
public class Demo9 {
    private static final Object $LOCK = new Object[0];
    private final Object $lock = new Object[0];
    private Object obj;

    public Demo9() {
    }

    public static void hello() {
        synchronized($LOCK) {
            System.out.println("world");
        }
    }

    public int answerToLife() {
        synchronized(this.$lock) {
            return 42;
        }
    }

    public void foo() {
        synchronized(this.obj) {
            System.out.println("bar");
        }
    }
}
```

### @Slf4j注解

```java
@Slf4j
public class LombokSlf4jTest {

    // @Slf4j会自动生成下面这个Logger对象
    // private static final Logger log = LoggerFactory.getLogger(LombokSlf4jTest.class);
    public static void main(String[] args) {
        log.error("error信息");
        log.warn("warn信息");
        log.info("info信息");
        log.debug("debug信息");
        log.trace("trace信息");
    }
}
```

### @Delegate

> 被@Delegate注释的属性，会把这个属性类型的**公有非静态方法**合到当前类

```java
package com.yolo.demo.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

@Getter
@Setter
public class Demo10 {
    @Delegate
    private Person person;
}
```

```java
public class Person {

    public void personMsg() {
        System.out.println("Person.personMsg");
    }

    public String printName(String name) {
        return name;
    }

    private Integer printAge(Integer age) {
        return age;
    }

    public static void printOther() {
        System.out.println("Person.printOther");
    }
}
```

![image-20230616162025628](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230616162025628.png)

### @Singular

使用 @Singular 注解一个集合字段（如果没有指定 value 属性值，那么集合字段名需要是复数形式），会生成添加元素方法向集合添加单个元素

> 只能配合@Builder注解使用, 该注解作用于字段和参数上, 一般用在集合属性和集合参数

```java
@Builder
@Data
public class Demo11 {
    private String name;

    //不设置value值，默认是nums的单数(num)；如果nums(只能是复数)随便起名，就会编译错误
    @Singular("num")
    private List<Integer> nums;
}
```

使用

```java
public class Main3 {
    public static void main(String[] args) {
        Demo11 demo = Demo11.builder().name("lucky")
                .num(1).num(2).num(3)
                .build();
        System.out.println("demo = " + demo);
    }
}
```

控制台输出: demo = Demo(name=lucky, nums=[1, 2, 3])

