# springboot-整合shardingjdbc

> 本 demo 主要演示了如何集成 `sharding-jdbc` 实现分库分表操作，ORM 层使用了`Mybatis-Plus`简化开发，童鞋们可以按照自己的喜好替换为 JPA、通用Mapper、JdbcTemplate甚至原生的JDBC都可以

## 一、项目准备

1. 在数据库创建2个数据库，分别为：`test`、`test1`
2. 去数据库执行 `sql/demo.sql` ，创建 `6` 张分片表
3. 找到 `DataSourceShardingConfig` 配置类，修改 `数据源` 的相关配置，位于 `dataSourceMap()` 这个方法
4. 找到测试类 `DemoShardingJdbcApplicationTests` 进行测试

## 二、整合

### 1、pom.xml

```xml
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.4.2</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>

        <dependency>
            <groupId>io.shardingsphere</groupId>
            <artifactId>sharding-jdbc-core</artifactId>
            <version>3.1.0</version>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.4.5</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
```

### 2、自定义雪花算法

```java
package com.yolo.shardingjdbc.config;

import cn.hutool.core.lang.Snowflake;
import io.shardingsphere.core.keygen.KeyGenerator;

/**
 * 自定义雪花算法，替换 DefaultKeyGenerator，避免DefaultKeyGenerator生成的id大几率是偶数
 */
public class CustomSnowflakeKeyGenerator implements KeyGenerator {
    private final Snowflake snowflake;

    public CustomSnowflakeKeyGenerator(Snowflake snowflake) {
        this.snowflake = snowflake;
    }

    @Override
    public Number generateKey() {
        return snowflake.nextId();
    }
}
```

### 3、sharding-jdbc 的数据源配置

```java
package com.yolo.shardingjdbc.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.zaxxer.hikari.HikariDataSource;
import io.shardingsphere.api.config.rule.ShardingRuleConfiguration;
import io.shardingsphere.api.config.rule.TableRuleConfiguration;
import io.shardingsphere.api.config.strategy.InlineShardingStrategyConfiguration;
import io.shardingsphere.api.config.strategy.NoneShardingStrategyConfiguration;
import io.shardingsphere.core.keygen.KeyGenerator;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * sharding-jdbc 的数据源配置
 */
@Configuration
public class DataSourceShardingConfig {
    private static final Snowflake SNOWFLAKE = IdUtil.createSnowflake(1, 1);

    /**
     * 需要手动配置事务管理器
     */
    @Bean
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "dataSource")
    @Primary
    public DataSource dataSource() throws SQLException {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        // 设置分库策略
        shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id", "ds${user_id % 2}"));
        // 设置规则适配的表
        shardingRuleConfig.getBindingTableGroups().add("t_order");
        // 设置分表策略
        shardingRuleConfig.getTableRuleConfigs().add(orderTableRule());
        shardingRuleConfig.setDefaultDataSourceName("ds0");
        shardingRuleConfig.setDefaultTableShardingStrategyConfig(new NoneShardingStrategyConfiguration());

        Properties properties = new Properties();
        properties.setProperty("sql.show", "true");

        return ShardingDataSourceFactory.createDataSource(dataSourceMap(), shardingRuleConfig, new ConcurrentHashMap<>(16), properties);
    }

    private TableRuleConfiguration orderTableRule() {
        TableRuleConfiguration tableRule = new TableRuleConfiguration();
        // 设置逻辑表名
        tableRule.setLogicTable("t_order");
        // ds${0..1}.t_order_${0..2} 也可以写成 ds$->{0..1}.t_order_$->{0..1}
        tableRule.setActualDataNodes("ds${0..1}.t_order_${0..2}");
        tableRule.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("order_id", "t_order_$->{order_id % 3}"));
        tableRule.setKeyGenerator(customKeyGenerator());
        tableRule.setKeyGeneratorColumnName("order_id");
        return tableRule;
    }

    private Map<String, DataSource> dataSourceMap() {
        Map<String, DataSource> dataSourceMap = new HashMap<>(16);

        // 配置第一个数据源
        HikariDataSource ds0 = new HikariDataSource();
        ds0.setDriverClassName("com.mysql.jdbc.Driver");
        ds0.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT%2B8");
        ds0.setUsername("root");
        ds0.setPassword("root");

        // 配置第二个数据源
        HikariDataSource ds1 = new HikariDataSource();
        ds1.setDriverClassName("com.mysql.jdbc.Driver");
        ds1.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test1?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT%2B8");
        ds1.setUsername("root");
        ds1.setPassword("root");

        dataSourceMap.put("ds0", ds0);
        dataSourceMap.put("ds1", ds1);
        return dataSourceMap;
    }

    /**
     * 自定义主键生成器
     */
    private KeyGenerator customKeyGenerator() {
        return new CustomSnowflakeKeyGenerator(SNOWFLAKE);
    }

}
```

### 4、实体类和mapper

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName(value = "t_order")
public class Order {
    /**
     * 主键
     */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 备注
     */
    private String remark;
}
```

```java
@Component
public interface OrderMapper extends BaseMapper<Order> {
}
```

### 5、启动类

```java
@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
@MapperScan("com.yolo.shardingjdbc.mapper")
public class DemoShardingJdbcApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoShardingJdbcApplication.class, args);
    }

}
```

## 三、测试

```java
@Slf4j
@SpringBootTest(classes = DemoShardingJdbcApplication.class)
@RunWith(SpringRunner.class)
public class DemoShardingJdbcApplicationTests {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 测试新增
     */
    @Test
    public void testInsert() {
        for (long i = 1; i < 10; i++) {
            for (long j = 1; j < 20; j++) {
                Order order = Order.builder().userId(i).orderId(j).remark(RandomUtil.randomString(20)).build();
                orderMapper.insert(order);
            }
        }
    }

    /**
     * 测试更新
     */
    @Test
    public void testUpdate() {
        Order update = new Order();
        update.setRemark("修改备注信息");
        orderMapper.update(update, Wrappers.<Order>update().lambda().eq(Order::getOrderId, 2).eq(Order::getUserId, 2));
    }

    /**
     * 测试删除
     */
    @Test
    public void testDelete() {
        orderMapper.delete(new QueryWrapper<>());
    }

    /**
     * 测试查询
     */
    @Test
    public void testSelect() {
        List<Order> orders = orderMapper.selectList(Wrappers.<Order>query().lambda().in(Order::getOrderId, 1, 2));
        log.info("【orders】= {}", JSONUtil.toJsonStr(orders));
    }

}
```

> 测试新增
>
> test库都是user_id为2、4、8的数据，
>
> t_order_0中的order_id为（3、6、9、12、15）
>
> t_order_1中的order_id为（1、4、7、10、13）
>
> t_order_2中的order_id为（2、5、8、11、14）

![image-20230524102706852](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230524102706852.png)

