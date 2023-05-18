# springboot-使用mybatis-plus集成多数据源

> 此 demo 主要演示了 Spring Boot 如何集成 Mybatis 的多数据源。可以自己基于AOP实现多数据源，这里基于 Mybatis-Plus 提供的一个优雅的开源的解决方案来实现。

## 一、准备工作

在数据库中准备俩个数据库，分别执行如下建表语句

```sql
DROP TABLE IF EXISTS `multi_user`;
CREATE TABLE `multi_user`(
`id` bigint(64) NOT NULL,
`name` varchar(50) DEFAULT NULL,
`age` int(30) DEFAULT NULL,
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci;
```

## 二、实现

### 1、pom.xml

```xml

```

### 2、application.yml

```yml

```

### 3、用户实体类

```java
@Data
@TableName("multi_user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {
    private static final long serialVersionUID = -1923859222295750467L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;
}
```

### 4、mapper

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

### 5、service相关类

```java
import com.baomidou.mybatisplus.extension.service.IService;
import com.yolo.multi.datasource.domain.User;

/**
 * 数据服务层
 */
public interface UserService extends IService<User> {

    /**
     * 添加 User
     *
     * @param user 用户
     */
    void addUser(User user);
}
```

```java
package com.yolo.multi.datasource.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yolo.multi.datasource.domain.User;
import com.yolo.multi.datasource.mapper.UserMapper;

import com.yolo.multi.datasource.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 数据服务层 实现
 */
@Service
@DS("slave")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 类上 {@code @DS("slave")} 代表默认从库，在方法上写 {@code @DS("master")} 代表默认主库
     *
     * @param user 用户
     */
    @DS("master")
    @Override
    public void addUser(User user) {
        baseMapper.insert(user);
    }
}
```

> 1. @DS: 注解在类上或方法上来切换数据源，方法上的@DS优先级大于类上的@DS
> 2. baseMapper: mapper 对象，即`UserMapper`，可获得CRUD功能
> 3. 默认走从库: `@DS(value = "slave")`在类上，默认走从库，除非在方法在添加`@DS(value = "master")`才走主库

## 三、测试

```java
/**
 * 测试主从数据源
 */
@Slf4j
public class UserServiceImplTest extends DemoMultiDatasourceMybatisApplicationTests {
    @Autowired
    private UserService userService;

    /**
     * 主从库添加
     */
    @Test
    public void addUser() {
        User userMaster = User.builder().name("主库添加").age(20).build();
        userService.addUser(userMaster);

        User userSlave = User.builder().name("从库添加").age(20).build();
        userService.save(userSlave);
    }

    /**
     * 从库查询
     */
    @Test
    public void testListUser() {
        List<User> list = userService.list(new QueryWrapper<>());
        log.info("【list】= {}", JSONUtil.toJsonStr(list));
    }
}
```

![image-20230518163229303](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230518163229303.png)