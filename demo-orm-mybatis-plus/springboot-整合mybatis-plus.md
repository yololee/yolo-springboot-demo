# springboot-整合mybatis-plus

## 一、项目准备

> 项目结构

![image-20230512104253271](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230512104253271.png)

### 1、添加依赖

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!--MySQL 5.1.47-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>
        <!--druid 数据库连接池-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.24</version>
        </dependency>
        <!-- mybatis plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.4.2</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>


        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>


        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
```

>  mapper：这里要使用mybatis-plus，需要在mapper继承BaseMapper

### 2、mybatis-plus分页配置

```java
@Configuration   //组件，添加到容器
@MapperScan("com.yolo.mybatis.plus.mapper")  //开启mapper接口扫描
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

}
```

## 二、CURD

### 1、新增

```java
    @Test
    public void insertTest(){
        Company company = new Company();
        company.setName("阿里");
        company.setContact("张三");
        company.setContactType("17683720001");
        company.setCreateTime(LocalDateTime.now());
        company.setRemoved(0);
        companyMapper.insert(company);
    }
```

![image-20230512110002126](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230512110002126.png)

### 2、更新

```java
    int updateById(@Param("et") T entity);

    int update(@Param("et") T entity, @Param("ew") Wrapper<T> updateWrapper);
```

```java
    @Test
    public void updateTest1(){
        Company company = new Company();
        company.setId(1656854581440679937L);
        company.setName("阿里");
        company.setContact("张三111");
        company.setContactType("17683723698");
        company.setUpdateTime(LocalDateTime.now());
        company.setRemoved(0);
        companyMapper.updateById(company);
    }

    @Test
    public void updateTest2(){
        //第一种
        Company company = new Company();
        company.setContact("张三222"); //需要更新的字段
        //queryWrapper对象，用于设置条件
        QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",1656854581440679937L);//设置查询条件
        companyMapper.update(company,queryWrapper);
    }

		//推荐第二种
    @Test
    public void updateTest3(){
        //第二种
        //UpdateWrapper更新操作
        UpdateWrapper<Company> warp = new UpdateWrapper<>();
        //通过set设置需要修改的内容，eq设置条件
        warp.set("name","阿里111").set("contact","zhansgan3333").eq("id",1656854581440679937L);
        companyMapper.update(null,warp);
    }
```

![image-20230512110447067](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230512110447067.png)

### 3、查询

```java 
    T selectById(Serializable id);

    List<T> selectBatchIds(@Param("coll") Collection<? extends Serializable> idList);

    List<T> selectByMap(@Param("cm") Map<String, Object> columnMap);

    T selectOne(@Param("ew") Wrapper<T> queryWrapper);

    Integer selectCount(@Param("ew") Wrapper<T> queryWrapper);

    List<T> selectList(@Param("ew") Wrapper<T> queryWrapper);
```

```java
    @Test
    public void selectTest(){
        //根据id查询
        Company company = companyMapper.selectById(6);
        System.out.println(company);

        //根据id集合查询
        List<Company> companyList = companyMapper.selectBatchIds(ListUtil.of(1656854581440679937L, 1656856229496020993L));
        System.out.println(companyList);

        //根据条件查询一个
        QueryWrapper<Company> query = new QueryWrapper<>();
        query.eq("name","华为");
        Company company1 = companyMapper.selectOne(query);
        System.out.println(company1);

        //根据map查询
        Map<String, Object> map = new HashMap<>();
        map.put("contact","张三");
        List<Company> companyList1 = companyMapper.selectByMap(map);
        System.out.println(companyList1);

        //根据条件查询个数
        QueryWrapper<Company> query1 = new QueryWrapper<>();
        query1.eq("contact","张三");
        Integer integer = companyMapper.selectCount(query1);
        System.out.println(integer);
        //根据条件查询多个
        List<Company> companyList2 = companyMapper.selectList(query1);
        System.out.println(companyList2);
    }
```

### 4、分页查询

![image-20230512111319013](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230512111319013.png)

### 5、删除

```java
    int deleteById(Serializable id);

    int deleteByMap(@Param("cm") Map<String, Object> columnMap);

    int delete(@Param("ew") Wrapper<T> queryWrapper);

    int deleteBatchIds(@Param("coll") Collection<? extends Serializable> idList);
```

## 三、逻辑删除

**步骤一**

```yml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: removed # 全局逻辑删除的实体字段名(since 3.3.0,配置后可以忽略不配置步骤2)
      logic-delete-value: -1 # 逻辑已删除值(默认为 -1)
      logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
```

**步骤二：实体类字段上加上`@TableLogic`注解**

**测试**

```java
    @Test
    public void deleteTest(){
        int i = companyMapper.deleteById(1656856229496020993L);
        System.out.println(i);
    }
```

![image-20230512112137391](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230512112137391.png)

## 四、设置自动填充字段

**第一步：给需要进行自动填充的字段加上注解 ：`@TableField()`**

```java
1. 默认不做处理:
# DEFAULT

2. 插入时填充字段:
# INSERT

3. 更新时填充字段:
# UPDATE

4. 插入和更新时都填充字段:
# INSERT_UPDATE
```

**第二步：实现 `MetaObjectHandler` 接口，并将其注入 Spring**

```java
    @Component
    @Slf4j
    public static class MyMetaObjectHandler implements MetaObjectHandler {

        @Override
        public void insertFill(MetaObject metaObject) {
            log.info("start insert fill ....");
            this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐使用)
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            log.info("start update fill ....");
            this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐)
        }
    }
```

**第三步：实体类字段添加注解**

```java
    /**
    * 创建时间
    */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
    * 修改时间
    */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
```

**注意**

- 填充原理是直接给entity的属性设置值
- 注解则是指定该属性在对应情况下必有值,如果无值则入库会是null
- MetaObjectHandler提供的默认方法的策略均为:如果属性有值则不覆盖,如果填充值为null则不填充
- 字段必须声明TableField注解,属性fill选择对应策略,该声明告知Mybatis-Plus需要预留注入SQL字段
- 填充处理器MyMetaObjectHandler在 Spring Boot 中需要声明@Component或@Bean注入
- update(T t,Wrapper updateWrapper)时t不能为空,否则自动填充失效

## 五、设置表名映射

**一：单独设置**

```java
@Data
@TableName("tb_user")
public class User {
    private int id;
    private String  name;
}
```

**二：全局设置**

```yml
mybatis-plus:
  #全局配置
  global-config:
    #数据库配置
    db-config:
      #表名前缀为tb_，表名为前缀拼接类名（小写）
      table-prefix: tb_
```

## 六、设置字段映射

```java
@Data
public class User {
    // @TableField(value = "userId")
    @TableField("userId")
    private int id;
    private String  name;
}
```

## 七、字段名和列名的驼峰映射

数据库中的字段名 ：user_name

实体类中的变量名 ：userName

mp默认开启驼峰映射，如果需要关闭 ：

```yml
mybatis-plus:
  configuration:
    #类属性与表字段的驼峰映射，mybatiplus默认true开启，mybatis需要手动配置，且config-location和configuration不能同时出现
    map-underscore-to-camel-case: true
```

## 八、设置主键生成策略

默认情况下主键的生成策略使用mp提供的雪花算法生成的自增id

**一：单独设置**

如果需要使用别的策略，在字段上加 `@TableId` ，它通过type属性指定主键生成策略，type的值为枚举类。最后两个策略只有当插入对象的主键位为时才会自动填充

| **属性值**         | **作用**                                               |
| ------------------ | ------------------------------------------------------ |
| IdType.NONE        | 默认，未设置逐渐策略，使用全局策略，默认全局为雪花算法 |
| IdType.AUTO        | 使用数据库的自动增长策略，每次加一                     |
| IdType.INPUT       | 需要手动设置主键                                       |
| IdType…ASSIGN_UUID | 使用UUID生成随机主键                                   |
| IdType.ASSIGN_ID   | mp自带策略，数字类型使用（19位）                       |

## 九、开启日志

```YML
mybatis-plus:
  configuration:
    # 用来打印sql日志
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

## 十、设置扫描Mapper文件位置

```yml
mybatis-plus:
  #mybatis配置文件
  #config-location: classpath:mybatis-config.xml
  # mapper映射位置
  mapper-locations: classpath:/mapper/**Mapper.xml
```

