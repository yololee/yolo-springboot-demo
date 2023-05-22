# springboot-整合es

> 此 demo 主要演示了 Spring Boot 如何集成 `spring-boot-starter-data-elasticsearch` 完成对 ElasticSearch 的高级使用技巧，包括创建索引、配置映射、删除索引、增删改查基本操作、复杂查询、高级查询、聚合查询等

## 一、项目版本

Jdk：1.8

springboot：2.3.12.RELEASE

es：7.14.1

kabana：7.14.1

> [docker 部署es和kabana](https://gitee.com/zhengqingya/docker-compose/blob/master/Linux/elasticsearch/docker-compose-elasticsearch.yml)

## 二、集成

### 1、pom.xml

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
            <version>2.3.12.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
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

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.26</version>
        </dependency>
```

### 2、application.yml

```yml
spring:
  elasticsearch:
    rest:
      uris: http://192.168.10.125:9200
      password: 123456
      username: elastic
```

### 3、application.yml

```yml
spring:
  elasticsearch:
    rest:
      uris: http://127.0.0.1:9200
      password: 123456
      username: elastic

```

### 4、Person实体类

> @Document 注解主要声明索引名、类型名、分片数量和备份数量
>
> @Field 注解主要声明字段对应ES的类型

```java
package com.yolo.elasticsearch.domain;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * 用户实体类
 */
@Document(indexName = "person", shards = 1, replicas = 0)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 名字
     */
    @Field(type = FieldType.Keyword)
    private String name;

    /**
     * 国家
     */
    @Field(type = FieldType.Keyword)
    private String country;

    /**
     * 年龄
     */
    @Field(type = FieldType.Integer)
    private Integer age;

    /**
     * 生日
     */
    @Field(type = FieldType.Date,format = DateFormat.date_time)
    private Date birthday;

    /**
     * 介绍
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String remark;
}
```

### 5、UserDao.java

```java
package com.yolo.elasticsearch.dao;


import com.yolo.elasticsearch.domain.Person;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**

 * 用户持久层
 */
public interface PersonRepository extends ElasticsearchRepository<Person, Long> {

    /**
     * 根据年龄区间查询
     *
     * @param min 最小值
     * @param max 最大值
     * @return 满足条件的用户列表
     */
    List<Person> findByAgeBetween(Integer min, Integer max);
}
```

## 三、测试

```java
package com.yolo.elasticsearch;


import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.yolo.elasticsearch.dao.PersonRepository;
import com.yolo.elasticsearch.domain.Person;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.InternalAvg;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试 Repository 操作ES
 */
@Slf4j
public class PersonRepositoryTest extends DemoElasticsearchApplicationTests {
    @Autowired
    private PersonRepository repo;

    /**
     * 测试新增
     */
    @Test
    public void save() {
        Person person = new Person(1L, "刘备", "蜀国", 18, DateUtil.parse("1990-01-02 03:04:05"), "刘备（161年－223年6月10日），即汉昭烈帝（221年－223年在位），又称先主，字玄德");
        Person save = repo.save(person);
        log.info("【save】= {}", save);
    }

    /**
     * 测试批量新增
     */
    @Test
    public void saveList() {
        List<Person> personList = new ArrayList<>();
        personList.add(new Person(2L, "曹操", "魏国", 20, DateUtil.parse("1988-01-02 03:04:05"), "曹操（155年－220年3月15日），字孟德，一名吉利，小字阿瞒"));
        personList.add(new Person(3L, "孙权", "吴国", 19, DateUtil.parse("1989-01-02 03:04:05"), "孙权（182年－252年5月21日），字仲谋，吴郡富春（今浙江杭州富阳区）人"));
        personList.add(new Person(4L, "诸葛亮", "蜀国", 16, DateUtil.parse("1992-01-02 03:04:05"), "诸葛亮（181年-234年10月8日），字孔明，号卧龙，徐州琅琊阳都（今山东临沂市沂南县）人"));
        Iterable<Person> people = repo.saveAll(personList);
        log.info("【people】= {}", people);
    }

    /**
     * 测试更新
     */
    @Test
    public void update() {
        repo.findById(1L).ifPresent(person -> {
            person.setRemark(person.getRemark() + "\n更新更新更新更新更新");
            Person save = repo.save(person);
            log.info("【save】= {}", save);
        });
    }

    /**
     * 测试删除
     */
    @Test
    public void delete() {
        // 主键删除
        repo.deleteById(1L);

        // 对象删除
        repo.findById(2L).ifPresent(person -> repo.delete(person));

        // 批量删除
        repo.deleteAll(repo.findAll());
    }

    /**
     * 测试普通查询，按生日倒序
     */
    @Test
    public void select() {
        repo.findAll(Sort.by(Sort.Direction.DESC, "birthday")).forEach(person -> log.info("{} 生日: {}", person.getName(), DateUtil.formatDateTime(person.getBirthday())));
    }

    /**
     * 自定义查询，根据年龄范围查询
     */
    @Test
    public void customSelectRangeOfAge() {
        repo.findByAgeBetween(18, 19).forEach(person -> log.info("{} 年龄: {}", person.getName(), person.getAge()));
    }

    /**
     * 高级查询
     */
    @Test
    public void advanceSelect() {
        // QueryBuilders 提供了很多静态方法，可以实现大部分查询条件的封装
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("name", "孙权");
        log.info("【queryBuilder】= {}", queryBuilder.toString());

        repo.search(queryBuilder).forEach(person -> log.info("【person】= {}", person));
    }

    /**
     * 自定义高级查询
     */
    @Test
    public void customAdvanceSelect() {
        // 构造查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本的分词条件
        queryBuilder.withQuery(QueryBuilders.matchQuery("remark", "东汉"));
        // 排序条件
        queryBuilder.withSort(SortBuilders.fieldSort("age").order(SortOrder.DESC));
        // 分页条件
        queryBuilder.withPageable(PageRequest.of(0, 2));
        Page<Person> people = repo.search(queryBuilder.build());
        log.info("【people】总条数 = {}", people.getTotalElements());
        log.info("【people】总页数 = {}", people.getTotalPages());
        people.forEach(person -> log.info("【person】= {}，年龄 = {}", person.getName(), person.getAge()));
    }

    /**
     * 测试聚合，测试平均年龄
     */
    @Test
    public void agg() {
        // 构造查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));

        // 平均年龄
        queryBuilder.addAggregation(AggregationBuilders.avg("avg").field("age"));

        log.info("【queryBuilder】= {}", JSONUtil.toJsonStr(queryBuilder.build()));

        AggregatedPage<Person> people = (AggregatedPage<Person>) repo.search(queryBuilder.build());
        double avgAge = ((InternalAvg) people.getAggregation("avg")).getValue();
        log.info("【avgAge】= {}", avgAge);
    }

    /**
     * 测试高级聚合查询，每个国家的人有几个，每个国家的平均年龄是多少
     */
    @Test
    public void advanceAgg() {
        // 构造查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));

        // 1. 添加一个新的聚合，聚合类型为terms，聚合名称为country，聚合字段为age
        queryBuilder.addAggregation(AggregationBuilders.terms("country").field("country")
            // 2. 在国家聚合桶内进行嵌套聚合，求平均年龄
            .subAggregation(AggregationBuilders.avg("avg").field("age")));

        log.info("【queryBuilder】= {}", JSONUtil.toJsonStr(queryBuilder.build()));

        // 3. 查询
        AggregatedPage<Person> people = (AggregatedPage<Person>) repo.search(queryBuilder.build());

        // 4. 解析
        // 4.1. 从结果中取出名为 country 的那个聚合，因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        StringTerms country = (StringTerms) people.getAggregation("country");
        // 4.2. 获取桶
        List<StringTerms.Bucket> buckets = country.getBuckets();
        for (StringTerms.Bucket bucket : buckets) {
            // 4.3. 获取桶中的key，即国家名称  4.4. 获取桶中的文档数量
            log.info("{} 总共有 {} 人", bucket.getKeyAsString(), bucket.getDocCount());
            // 4.5. 获取子聚合结果：
            InternalAvg avg = (InternalAvg) bucket.getAggregations().asMap().get("avg");
            log.info("平均年龄：{}", avg);
        }
    }

}
```

