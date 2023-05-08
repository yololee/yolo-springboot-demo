# springboot-整合mybatis(注解、XML)

## 一、准备工作

### 1.创建表

```sql
CREATE TABLE `orm_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) NOT NULL COMMENT '用户名',
  `password` varchar(32) NOT NULL COMMENT '加密后的密码',
  `salt` varchar(32) NOT NULL COMMENT '加密使用的盐',
  `email` varchar(32) NOT NULL COMMENT '邮箱',
  `phone_number` varchar(15) NOT NULL COMMENT '手机号码',
  `status` int(2) NOT NULL DEFAULT '1' COMMENT '状态，-1：逻辑删除，0：禁用，1：启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '上次登录时间',
  `last_update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上次更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone_number` (`phone_number`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='Spring Boot Demo Orm 系列示例表';

CREATE TABLE `orm_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';
```

### 2、创建项目

> 项目结构如下

![image-20230508090839049](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230508090839049.png)


#### 添加pom.xml配置信息

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>${mybatis.version}</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>

        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
```

#### application.yml

```yml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT%2B8
    username: root
    password: root
    driver-class-name: com.mysql.jdbc.Driver
    type: com.zaxxer.hikari.HikariDataSource
    initialization-mode: always
    continue-on-error: true
    hikari:
      minimum-idle: 5
      connection-test-query: SELECT 1 FROM DUAL
      maximum-pool-size: 20
      auto-commit: true
      idle-timeout: 30000
      pool-name: SpringBootDemoHikariCP
      max-lifetime: 60000
      connection-timeout: 30000
logging:
  level:
    com.yolo: debug
    com.yolo.mybatis.mapper: trace

#MyBatis配置
mybatis:
  type-aliases-package: com.yolo.mybatis.pojo #别名定义
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl #指定 MyBatis 所用日志的具体实现，未指定时将自动查找
    map-underscore-to-camel-case: true #开启自动驼峰命名规则（camel case）映射
    lazy-loading-enabled: true #开启延时加载开关
    aggressive-lazy-loading: false #将积极加载改为消极加载（即按需加载）,默认值就是false
    lazy-load-trigger-methods: "" #阻挡不相干的操作触发，实现懒加载
    cache-enabled: true #打开全局缓存开关（二级环境），默认值就是true
  mapper-locations: classpath:mybatis/mapper/*.xml  # 加载映射配置文件
  #config-location: classpath:mybatis/mybatis-config.xml   # 加载核心配置文件
```

## 二、注解版

> 用户实体类

```java
/**
 * 用户实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {
    private static final long serialVersionUID = -1840831686851699943L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 加密后的密码
     */
    private String password;

    /**
     * 加密使用的盐
     */
    private String salt;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 状态，-1：逻辑删除，0：禁用，1：启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 上次登录时间
     */
    private Date lastLoginTime;

    /**
     * 上次更新时间
     */
    private Date lastUpdateTime;
}
```

> 用户mapper

```java
package com.yolo.mybatis.mapper;


import com.yolo.mybatis.dto.UserDTO;
import com.yolo.mybatis.pojo.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User Mapper
 */
@Mapper
//实现实体和数据表的映射关系可以在Mapper类上添加@Mapper注解
//但是建议以后直接在SpringBoot启动类中加 @MapperScan("com.yolo.mybatis.mapper")注解，这样会比较方便，不需要对每个Mapper都添加@Mapper注解。
@Repository
public interface UserMapper {

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @Select("SELECT * FROM orm_user")
    List<User> selectAllUser();

    /**
     * 根据id查询用户
     *
     * @param id 主键id
     * @return 当前id的用户，不存在则是 {@code null}
     */
    @Select("SELECT * FROM orm_user WHERE id = #{id}")
    User selectUserById(@Param("id") Long id);


    /**
     * 根据id批量查询
     * @param ids 主键id集合
     * @return 用户列表
     */
    @Select("<script> SELECT * FROM orm_user " +
            " WHERE id in " +
            " <foreach collection='list' item='id' index='index' open='(' separator=',' close=')' >" +
            " #{id}" +
            " </foreach>" +
            " AND status != -1 " +
            " </script>")
    List<User> selectByIds(@Param("list") List<Integer> ids);

    /**
     * 多条件查询
     * @param userDTO 查询条件
     * @return 用户列表
     */
    @Select("<script> SELECT * from orm_user "  +
            "<where>" +
            "<if test= \" item.name != null and item.name != '' \"> name like '%" + "${item.name}" + "%'</if>" +
            "<if test= \" item.phoneNumber != null and item.phoneNumber != ''\"> phoneNumber =#{item.phoneNumber}</if>" +
            " AND status = 1 " +
            "</where>  ORDER BY id  DESC </script>")
    List<User> selectPage(@Param("item") UserDTO userDTO);

    /**
     * 保存用户
     * useGeneratedKeys属性表示使用自增主键
     * keyProperty属性是Java包装类对象的属性名
     * keyColumn属性是mysql表中的字段名
     * @param user 用户
     * @return 成功 - {@code 1} 失败 - {@code 0}
     */
    @Insert("insert into orm_user (name,password,salt,email,phone_number,status,create_time,last_login_time,last_update_time)" +
            " VALUES (#{user.name},#{user.password},#{user.salt},#{user.email},#{user.phoneNumber},#{user.status}," +
            " #{user.createTime},#{user.lastLoginTime},#{user.lastUpdateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id" , keyColumn = "id")
    int saveUser(@Param("user") User user);

    /**
     * 批量插入
     * @param userList
     */
    @Insert({
            "<script>",
            "INSERT INTO orm_user (name,password,salt,email,phone_number,status,create_time,last_login_time,last_update_time)  VALUES",
            "<foreach collection='list' item='item' index='index' separator=','>",
            "( #{item.name},#{item.password},#{item.salt},#{item.email},#{item.phoneNumber},#{item.status}," +
                    "#{item.createTime},#{item.lastLoginTime},#{item.lastUpdateTime})",
            "</foreach>",
            "</script>"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertList(@Param("list")List<User> userList);


    @Update({"<script> UPDATE orm_user SET status =#{status}"
            + " WHERE status !=-1 and id IN "
            + "<foreach  collection = 'ids' item = 'id' index = 'index' open = '(' separator= ',' close = ')' >"
            + "	#{id} "
            + "</foreach>"
            + "</script>"})
    int updateBatchStateById(@Param("ids") List<Integer> ids, @Param("status") Integer status);


    @Update("<script>" +
            "<foreach collection = 'list' item ='item' open='' close='' separator=';'>" +
            " update orm_user set " +
            "<if test= \" item.name != null and item.name != '' \"> name =#{item.name} ,</if>" +
            "<if test= \" item.email != null and item.email != '' \"> email =#{item.email} </if>" +
            " where id=#{item.id} " +
            "</foreach>" +
            "</script>")
    int updateBatch(@Param("list")List<User> userList);


    /**
     * 删除用户
     *
     * @param id 主键id
     * @return 成功 - {@code 1} 失败 - {@code 0}
     */
    @Delete("delete from orm_user where id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 批量删除
     * @param ids 主键id集合
     */
    @Delete({"<script> DELETE FROM orm_user  WHERE id IN "
            + "<foreach  collection = 'ids' item = 'id' index = 'index' open = '(' separator= ',' close = ')' >"
            + "	#{id} "
            + "</foreach>"
            + "</script>"})
    int batchDelete(@Param("ids") List<Integer> ids);


}
```

三、XML版

> 角色实体

```java 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    /**
     * 角色ID
     */
    private Long id;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 显示顺序
     */
    private Integer roleSort;
    /**
     * 角色状态（0正常 1停用）
     */
    private String status;
    /**
     * 备注
     */
    private String remark;
}
```

> 角色XML

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.yolo.mybatis.mapper.RoleMapper">


    <resultMap type="Role" id="roleResult">
        <id     property="id"      column="role_id"      />
        <result property="roleName"    column="role_name"    />
        <result property="roleSort"     column="role_sort"     />
        <result property="status"   column="status"   />
        <result property="remark"    column="remark"    />
    </resultMap>

    <sql id="roleVO">
        select role_id, role_name, role_sort, status, remark from orm_role
    </sql>

    <select id="selectAll" resultMap="roleResult">
        <include refid="roleVO"/>
    </select>

    <select id="selectById" resultMap="roleResult">
        delete orm_role
        <where>
            role_id =#{id}
        </where>
    </select>


    <select id="selectByIds" resultMap="roleResult">
        delete orm_role
        <where>
            role_id in
            <foreach collection="ids" item="id" index="index" open="(" close=")" separator=",">
                <if test="id != null">#{id}</if>
            </foreach>
        </where>
    </select>

    <select id="selectPage" resultMap="roleResult">
        <include refid="roleVO"/>
        <where>
            <if test="roleName != null and roleName != '' ">
                and role_name like concat('%',#{roleName},'%')
            </if>
            <if test="status != null and status != '' ">
                and status =#{item.roleName}
            </if>
        </where>
    </select>

    <delete id="deleteById" parameterType="long">
        delete orm_role where role_id=#{id}
    </delete>

    <delete id="batchDelete">
        delete orm_role
        <where>
            role_id in
            <foreach collection="ids" item="id" index="index" open="(" close=")" separator=",">
                #{id}
            </foreach>
        </where>
    </delete>


    <!--
     * useGeneratedKeys属性表示使用自增主键
     * keyProperty属性是Java包装类对象的属性名
     * keyColumn属性是mysql表中的字段名
    -->
    <insert id="save" parameterType="com.yolo.mybatis.pojo.Role" useGeneratedKeys="true" keyProperty="id" keyColumn="role_id">
        insert into orm_role (
        <if test="roleName != null and roleName != '' ">role_name,</if>
        <if test="status != null and status != '' ">status,</if>
        <if test="remark != null and remark != '' ">remark,</if>
        role_sort
        )values(
        <if test="roleName != null and roleName != ''">#{roleName},</if>
        <if test="status != null and status != ''">#{status},</if>
        <if test="remark != null and remark != ''">#{remark},</if>
        #{roleSort}
        )
    </insert>

    <insert id="insertList" useGeneratedKeys="true" keyProperty="id" keyColumn="role_id" parameterType="List">
        insert into orm_role (role_name,status ,remark,role_sort) values
        <foreach collection="roleList" item="role" separator="," index="index">
            (<if test="role.roleName != null and role.roleName != '' ">#{role.roleName},</if>
            <if test="role.status != null and role.status != '' "> #{role.status}, </if>
            <if test="role.remark != null and role.remark != '' "> #{role.remark}, </if>
            #{role.roleSort})
        </foreach>
    </insert>


    <update id="updateBatchStateById">
        update orm_role 
        <set>
            <if test="status != null and status != '' "> status = #{status}</if>
        </set>
        <where>
            role_id in
            <foreach collection="ids" item="id" index="index" open="(" separator="," close=")">
                #{id}
            </foreach>
        </where>
    </update>

    <update id="updateBatch" parameterType="List">
        <foreach collection="roleList" item="role" separator=";">
            update orm_role
            <set>
                <if test="role.status != null and role.status != '' "> status = #{role.status},</if>
                <if test="role.remark != null and role.remark != '' "> remark = #{role.remark},</if>
            </set>
            <where>
                role_id = #{role.id}
            </where>
        </foreach>
    </update>




</mapper>


```

