# springboot-整合mybatis使用PageHelper

## 一、准备工作

## 1、创建表

```sql
CREATE TABLE `orm_role` (
`role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
`role_name` varchar(30) NOT NULL COMMENT '角色名称',
`role_sort` int(4) NOT NULL COMMENT '显示顺序',
`status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
`remark` varchar(500) DEFAULT NULL COMMENT '备注',
PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

INSERT INTO `test`.`orm_role` (`role_id`, `role_name`, `role_sort`, `status`, `remark`) VALUES (6, '管理员', 1, '0', 'test');
INSERT INTO `test`.`orm_role` (`role_id`, `role_name`, `role_sort`, `status`, `remark`) VALUES (9, '运维', 2, '0', 'test2222');
INSERT INTO `test`.`orm_role` (`role_id`, `role_name`, `role_sort`, `status`, `remark`) VALUES (10, '运维管理员', 2, '0', 'test1111');
```

## 2、环境搭建

### pom.xml

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
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

        <!-- SpringBoot/MyBatis使用PageHelper分页控件 -->
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper-spring-boot-starter</artifactId>
            <version>1.2.13</version>
        </dependency>

        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
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
```



### application.yml

```yml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT%2B8&&allowMultiQueries=true
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
  type-aliases-package: com.yolo.mybatis.page.pojo #别名定义
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl #指定 MyBatis 所用日志的具体实现，未指定时将自动查找
    map-underscore-to-camel-case: true #开启自动驼峰命名规则（camel case）映射
    lazy-loading-enabled: true #开启延时加载开关
    aggressive-lazy-loading: false #将积极加载改为消极加载（即按需加载）,默认值就是false
    lazy-load-trigger-methods: "" #阻挡不相干的操作触发，实现懒加载
    cache-enabled: true #打开全局缓存开关（二级环境），默认值就是true
  mapper-locations: classpath:mybatis/*.xml  # 加载映射配置文件
  #config-location: classpath:mybatis/mybatis-config.xml   # 加载核心配置文件

# PageHelper分页插件
pagehelper:
  helperDialect: mysql
  supportMethodsArguments: true
  params: count=countSql
```

## 三、集成pagehelper插件

### 相关类

> PageUtils

```
package com.yolo.mybatis.page.page;

import cn.hutool.core.convert.Convert;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yolo.mybatis.page.util.ServletUtils;
import com.yolo.mybatis.page.util.SqlUtil;

import java.util.List;


/**
 * 分页工具类
 */
public class PageUtils extends PageHelper {

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 分页参数合理化
     */
    public static final String REASONABLE = "reasonable";


    /**
     * 设置请求分页数据
     */
    public static void startPage() {
        PageDomain pageDomain = getPageDomain();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        Boolean reasonable = pageDomain.getReasonable();
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
    }

    /**
     * 清理分页的线程变量
     */
    public static void clearPage() {
        PageHelper.clearPage();
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 封装分页对象
     */
    private static PageDomain getPageDomain(){
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(Convert.toInt(ServletUtils.getParameter(PAGE_NUM),1));
        pageDomain.setPageSize(Convert.toInt(ServletUtils.getParameter(PAGE_SIZE), 10));
        pageDomain.setOrderByColumn(ServletUtils.getParameter(ORDER_BY_COLUMN));
        pageDomain.setIsAsc(ServletUtils.getParameter(IS_ASC));
        pageDomain.setReasonable(ServletUtils.getParameterToBool(REASONABLE));
        return pageDomain;
    }

}

```

> PageDomain

```java
package com.yolo.mybatis.page.page;


import com.yolo.mybatis.page.util.StringUtils;

/**
 * 分页数据
 */
public class PageDomain {
    /**
     * 当前记录起始索引
     */
    private Integer pageNum;

    /**
     * 每页显示记录数
     */
    private Integer pageSize;

    /**
     * 排序列
     */
    private String orderByColumn;

    /**
     * 排序的方向desc或者asc
     */
    private String isAsc = "asc";

    /**
     * 分页参数合理化
     */
    private Boolean reasonable = true;

    public String getOrderBy() {
        if (StringUtils.isEmpty(orderByColumn)) {
            return "";
        }
        return StringUtils.toUnderScoreCase(orderByColumn) + " " + isAsc;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderByColumn() {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn) {
        this.orderByColumn = orderByColumn;
    }

    public String getIsAsc() {
        return isAsc;
    }

    public void setIsAsc(String isAsc) {
        this.isAsc = isAsc;
    }

    public Boolean getReasonable() {
        if (StringUtils.isNull(reasonable)) {
            return Boolean.TRUE;
        }
        return reasonable;
    }

    public void setReasonable(Boolean reasonable) {
        this.reasonable = reasonable;
    }
}

```

> TableDataInfo

```
package com.yolo.mybatis.page.page;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 表格分页数据对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TableDataInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<?> rows;

    /**
     * 分页
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public TableDataInfo(List<?> list, int total) {
        this.rows = list;
        this.total = total;
    }
}
```

### 测试

```java
@RestController
@RequestMapping("/test")
public class TestRoleController extends BaseController{

    @Autowired
    private RoleMapper roleMapper;

    @GetMapping("/page")
    public TableDataInfo page(RoleDTO roleDTO){
        PageUtils.startPage();

        List<Role> roleList = roleMapper.selectPage(roleDTO);

        return PageUtils.getDataTable(roleList);
    }

}
```

![image-20230508105105752](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230508105105752.png)

> [Gitee项目地址](https://gitee.com/huanglei1111/yolo-springboot-demo/tree/master/demo-orm-mybatis-page)

