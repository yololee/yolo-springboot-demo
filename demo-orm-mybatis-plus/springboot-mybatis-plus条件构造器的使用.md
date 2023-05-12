# springboot-mybatis-plus中条件构造器的使用

## 一、方法介绍

> 注意事项：主动调用`or`表示紧接着下一个**方法**不是用`and`连接!(不调用`or`则默认为使用`and`连接)

| 方法名      | 中文意思                                                 | 使用                                                         |
| ----------- | -------------------------------------------------------- | ------------------------------------------------------------ |
| allEq       | 全部eq(或个别isNull)                                     |                                                              |
| eq          | 等于 ==                                                  | eq("name", "老王")`--->`name = '老王'                        |
| ne          | 不等于 <>                                                | ne("name", "老王")`--->`name <> '老王'                       |
| gt          | 大于 >                                                   | gt("age", 18)--->age > 18                                    |
| ge          | 大于等于 >=                                              | ge("age", 18)--->age >= 18                                   |
| lt          | 小于 <                                                   | lt("age", 18)--->age < 18                                    |
| le          | 小于等于 <=                                              | le("age", 18)`--->`age <= 18                                 |
| between     | BETWEEN 值1 AND 值2                                      | between("age", 18, 30)`--->`age between 18 and 30            |
| notBetween  | NOT BETWEEN 值1 AND 值2                                  | notBetween("age", 18, 30)`--->`age not between 18 and 30     |
| like        | LIKE '%值%'                                              | like("name", "王")`--->`name like '%王%'                     |
| notLike     | NOT LIKE '%值%'                                          | NOT LIKE '%值%'                                              |
| likeLeft    | LIKE '%值'                                               | likeLeft("name", "王")`--->`name like '%王'                  |
| likeRight   | LIKE '值%'                                               | likeRight("name", "王")`--->`name like '王%'                 |
| isNull      | 字段 IS NULL                                             | isNull("name")`--->`name is null                             |
| isNotNull   | 字段 IS NOT NULL                                         | isNotNull("name")`--->`name is not null                      |
| in          | 字段 IN (value.get(0), value.get(1), ...)，              | in("age",{1,2,3})`--->`age in (1,2,3)                        |
|             |                                                          | in("age", 1, 2, 3)`--->`age in (1,2,3)                       |
| notIn       | 字段 NOT IN (value.get(0), value.get(1), ...)            | notIn("age",{1,2,3})`--->`age not in (1,2,3)                 |
|             |                                                          | notIn("age", 1, 2, 3)`--->`age not in (1,2,3)                |
| inSql       | 字段 IN ( sql语句 )                                      | inSql("age", "1,2,3,4,5,6")`--->`age in (1,2,3,4,5,6)        |
|             |                                                          | inSql("id", "select id from table where id < 3")`--->`id in (select id from table where id < 3) |
| notInSql    | 字段 NOT IN ( sql语句 )                                  | notInSql("age", "1,2,3,4,5,6")`--->`age not in (1,2,3,4,5,6) |
|             |                                                          | notInSql("id", "select id from table where id < 3")`--->`id not in (select id from table where id < 3) |
| groupBy     | 分组：GROUP BY 字段, ...                                 | groupBy("id", "name")`--->`group by id,name                  |
| orderByAsc  | 升序：ORDER BY 字段, ... ASC                             | orderByAsc("id", "name")`--->`order by id ASC,name ASC       |
| orderByDesc | 降序：ORDER BY 字段, ... DESC                            | orderByDesc("id", "name")`--->`order by id DESC,name DESC    |
| orderBy     | 排序：ORDER BY 字段,                                     | orderBy(true, true, "id", "name")`--->`order by id ASC,name ASC |
| having      | HAVING ( sql语句 )                                       | having("sum(age) > 10")`--->`having sum(age) > 10            |
| func        | func 方法(主要方便在出现if...else下调用不同方法能不断链) | func(i -> if(true) {i.eq("id", 1)} else {i.ne("id", 1)})     |
| or          | 拼接 OR                                                  | eq("id",1).or().eq("name","老王")`--->`id = 1 or name = '老王' |
|             | OR 嵌套                                                  | or(i -> i.eq("name", "李白").ne("status", "活着"))`--->`or (name = '李白' and status <> '活着') |
| and         | AND 嵌套                                                 | and(i -> i.eq("name", "李白").ne("status", "活着"))`--->`and (name = '李白' and status <> '活着') |



## 二、条件构造器

### 继承体系
![image-20230512141934548](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230512141934548.png)

- AbstractWrapper： 用于查询条件封装，生成 sql 的 where 条件
- QueryWrapper： Entity 对象封装操作类，不是用lambda语法
- UpdateWrapper： Update 条件封装，用于Entity对象更新操作
- AbstractLambdaWrapper： Lambda 语法使用 Wrapper统一处理解析 lambda 获取 column
- LambdaQueryWrapper：看名称也能明白就是用于Lambda语法使用的查询Wrapper
- LambdaUpdateWrapper： Lambda 更新封装Wrapper

### UpdateWrapper更新

- set(String column, Object val)
- set(boolean condition, String column, Object val)

```java
// 需求：将id=1的员工name改为xiaolin
	@Test
    public void testUpdate2(){
        UpdateWrapper<Employee> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", 1L);
        // 相当于sql语句中的set name = xiaolin
        wrapper.set("name", "xiaolin");
        employeeMapper.update(null, wrapper);

    }
```

### LambdaUpdateWrapper更新

```java
   // 需求：将id=1的用户name改为xiaolin 
   @Test
    public void testUpdate4(){
        LambdaUpdateWrapper<Employee> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Employee::getId, 1L);
        wrapper.set(Employee::getName, "xiaolin");
        employeeMapper.update(null, wrapper);
    }
```

### QueryWrapper查询

```java
  // 需求：查询name=xiaolin， age=18的用户
    @Test
    public void testQuery2(){
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.eq("name", "xiaolin").eq("age", 18);
        System.out.println(employeeMapper.selectList(wrapper));
    }
```

### LambdaQueryWrapper查询

```java
  //需求：查询name=xiaolin， age=18的用户
    @Test
    public void testQuery3(){
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getName, "xiaolin").eq(Employee::getAge, 18);
        System.out.println(employeeMapper.selectList(wrapper));

    }
```

### 高级查询

#### 列投影

- select(String... sqlSelect) ：参数是指定查询后返回的列
- select(Predicate<TableFieldInfo> predicate)：参数是Predicate 函数，满足指定判定逻辑列才返回
- select(Class<T> entityClass, Predicate<TableFieldInfo> predicate)：参数1是通过实体属性映射表中列，参数2是Predicate 函数， 满足指定判定逻辑列才返回

```java
  // 需求：查询所有员工， 返回员工name， age列
    @Test
    public void testQuery4(){
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.select("name", "age");
        employeeMapper.selectList(wrapper);

    }

  // 需求：查询所有员工， 返回员工以a字母开头的列
    @Test
    public void testQuery4(){
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.select(Employee.class, tableFieldInfo->tableFieldInfo.getProperty().startsWith("a"));
        employeeMapper.selectList(wrapper);

    }
```

#### allEq:全部eq(或个别isNull)

```java
allEq(Map<R, V> params)
allEq(Map<R, V> params, boolean null2IsNull)
allEq(boolean condition, Map<R, V> params, boolean null2IsNull)
```

> 个别参数说明:
>
> `params` : `key`为数据库字段名,`value`为字段值
> `null2IsNull` : 为`true`则在`map`的`value`为`null`时调用 [isNull](https://baomidou.com/pages/10c804/#isnull) 方法,为`false`时则忽略`value`为`null`的

```java
        Map<String,Object> map = new HashMap<>();
        map.put("name","迈异");
        map.put("contact","张三");

        QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
        queryWrapper.allEq(map);
        //SELECT id,name,contact,contactType,createTime,updateTime,deleteTime,removed FROM company WHERE removed=0 AND (contact = ? AND name = ?)
        companyMapper.selectList(queryWrapper);

        map.put("createTime",null);
        QueryWrapper<Company> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.allEq(map,true);
        //SELECT id,name,contact,contactType,createTime,updateTime,deleteTime,removed FROM company WHERE removed=0 AND (createTime IS NULL AND contact = ? AND name = ?)
        companyMapper.selectList(queryWrapper1);
```

#### or

```java
  // 需求： 查询age = 18 或者 name=xiaolin 或者 id =1 的用户
    @Test
    public void testQuery24(){
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.eq("age", 18)
                .or()
                .eq("name", "xiaolin")
                .or()
                .eq("id", 1L);
        employeeMapper.selectList(wrapper);
    }

  // 需求：查询name含有lin字样的，或者 年龄在18到30之间的用户
    @Test
    public void testQuery25(){
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.like("name", "lin")
                .or(wr -> wr.le("age", 30).ge("age", 18));
        employeeMapper.selectList(wrapper);
    }
```

#### groupBy

```java
   // 需求： 以部门id进行分组查询，查每个部门员工个数
    @Test
    public void testQuery22(){
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.groupBy("dept_id");
        wrapper.select("dept_id", "count(id) count");
        employeeMapper.selectMaps(wrapper);
    }
```

#### having

```java
  // 需求： 以部门id进行分组查询，查每个部门员工个数， 将大于3人的部门过滤出来
    @Test
    public void testQuery23(){
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.groupBy("dept_id")
                .select("dept_id", "count(id) count")
                //.having("count > {0}", 3)
                .having("count >3");
        employeeMapper.selectMaps(wrapper);
    }
```