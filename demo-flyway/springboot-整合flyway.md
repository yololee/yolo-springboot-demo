# springboot-整合flyway

## 一、Flyway简介

### 1、介绍

​	Flyway是一款开源的数据库版本管理工具，他可以很方便的在命令行中使用，或者在java应用程序中引入，用于管理我们的数据库版本。

​	Flyway是一款数据库迁移（migration）工具。简单点说，就是在你部署应用的时候，帮你执行数据库脚本的工具。Flyway支持SQL和Java两种类型的脚本，你可以将脚本打包到应用程序中，在应用程序启动时，由Flyway来管理这些脚本的执行，这些脚本被Flyway称之为migration

### 2、工作流程

1. 项目启动，应用程序完成数据库连接池的建立之后，Flyway自动运行
2. 初次使用时，Flyway会创建一个**flyway_schema_history** 表，用于记录sql执行记录
3. Flyway会扫描项目指定路径下(默认是 **classpath:db/migration** )的所有sql脚本，与 **flyway_schema_history** 表脚本记录进行比对。如果数据库记录执行过的脚本记录，与项目中的sql脚本不一致，Flyway会报错并停止项目执行
4. 如果校验通过，则根据表中的sql记录最大版本号，忽略所有版本号不大于该版本的脚本。再按照版本号从小到大，逐个执行其余脚本

## 二、集成Flyway

### 1、pom.xml

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
            <version>5.1.34</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.1.2</version>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
            <version>5.2.4</version>
        </dependency>
```

### 2、application.yml

```yml
spring:
  # 数据库连接配置
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/flyway-demo?characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
  flyway:
    # 是否启用flyway
    enabled: true
    # 编码格式，默认UTF-8
    encoding: UTF-8
    # 迁移sql脚本文件存放路径，默认db/migration
    locations: classpath:db/migration
    # 迁移sql脚本文件名称的前缀，默认V
    sql-migration-prefix: V
    # 迁移sql脚本文件名称的分隔符，默认2个下划线__
    sql-migration-separator: __
    # 迁移sql脚本文件名称的后缀
    sql-migration-suffixes: .sql
    # 执行迁移时是否自动调用验证   当你的 版本不符合逻辑 比如 你先执行了 DML 而没有 对应的DDL 会抛出异常
    validate-on-migrate: true
    # 如果没有 flyway_schema_history 这个 metadata 表， 在执行 flyway migrate 命令之前, 必须先执行 flyway baseline 命令
    # 设置为 true 后 flyway 将在需要 baseline 的时候, 自动执行一次 baseline
    baseline-on-migrate: true
    # metadata 版本控制信息表 默认 flyway_schema_history
    table: flyway_schema_history
    # 指定 baseline 的版本号,默认值为 1, 低于该版本号的 SQL 文件, migrate 时会被忽略
    baseline-version: 1
```

> ==创建脚本所在文件夹==
>
> 根据上面配置文件中的脚本存放路径，我们需要在resource目录下建立文件夹 db/migration

### 3、<font color = red>sql脚本命名规范</font>

> 对于Flyway，对数据库的所有更改都称为变迁(migrations)

1. 版本变迁(Versioned Migrations): 每个版本执行一次，包含有版本、描述和校验和；常用于创建，修改，删除表；插入，修改数据等
2. 撤销变迁(Undo Migrations): 版本变迁(Versioned Migrations)的反操作
3. 可重复变迁(Repeatable Migrations): 可以执行多次，包含描述和校验和（没有版本）；主要用于视图，存储过程，函数等

![image-20230518100139006](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230518100139006.png)

- 前缀: V 代表版本变迁(Versioned Migrations), U 代表撤销变迁(Undo Migrations)， R 代表可重复变迁(Repeatable Migrations)
- 版本号: 唯一的版本号，比如V1.0.1
- 分隔符: __ (两个下划线)
- 描述信息: 描述信息
- 后缀: .sql

> <b>sql的执行顺序</b>

Flyway是采用了**采用左对齐原则, 缺位用 0 代替**，根据版本好来判断那个sql先执行

```java
1.0.1.1 比 1.0.1 版本高。
1.0.10 比 1.0.9.4 版本高。
1.0.10 和 1.0.010 版本号一样高, 每个版本号部分的前导 0 会被忽略
```

仅需要被执行一次的SQL命名以大写的"V"开头，V+版本号(版本号的数字间以”.“或”_“分隔开)+双下划线(用来分隔版本号和描述)+文件描述+后缀名。例如：  `V20201100__create_user.sql`、`V2.1.5__create_user_ddl.sql`、`V4.1_2__add_user_dml.sql `

可重复运行的SQL，则以大写的“R”开头，后面再以两个下划线分割，其后跟文件名称，最后以.sql结尾。（不推荐使用）比如：` R__truncate_user_dml.sql `

==其中，V开头的SQL执行优先级要比R开头的SQL优先级高==

### 4、测试

> V1__create_user.sql

```sql
CREATE TABLE IF NOT EXISTS `user`(
    `USER_ID`      INT          NOT NULL AUTO_INCREMENT,
    `USER_NAME`    VARCHAR(100) NOT NULL COMMENT '用户姓名',
    `AGE`          INT          NOT NULL COMMENT '年龄',
    `CREATED_TIME` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `CREATED_BY`   varchar(100) NOT NULL DEFAULT 'UNKNOWN',
    `UPDATED_TIME` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `UPDATED_BY`   varchar(100) NOT NULL DEFAULT 'UNKNOWN',
    PRIMARY KEY (`USER_ID`)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;
```

> V2__add_user.sql

```sql 
insert into `user`(user_name,age) values('lisi',33);
```

> V3__add_user.sql

```sql
insert into `user`(user_name,age) values('lisi2222',33);
```

> R__add_unknown_user.sql

```sql
insert into `user`(user_name,age) values('unknown',33);
```

启动测试

查看mysql中Flyway的版本控制信息表

表`flyway_schema_history`是项目中自己配置的表名，作用是Flyway的版本控制信息

表`user`是自己创建的

![image-20230518102113380](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230518102113380.png)

> 表`flyway_schema_history`

![image-20230518102125211](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230518102125211.png)

> 表`user`

![image-20230518102133822](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230518102133822.png)

这里我们修改`V3__add_user.sql`文件他就会出现错误

[ERROR] Migration checksum mismatch for migration version 2

如果我们修改`R__add_unknown_user.sql`然后再次执行，该脚本会再次执行，并且flyway的历史记录表中也会增加本次执行的记录

## 三、Flyway配置清单

```java 
flyway.baseline-description对执行迁移时基准版本的描述.
flyway.baseline-on-migrate当迁移时发现目标schema非空，而且带有没有元数据的表时，是否自动执行基准迁移，默认false.
flyway.baseline-version开始执行基准迁移时对现有的schema的版本打标签，默认值为1.
flyway.check-location检查迁移脚本的位置是否存在，默认false.
flyway.clean-on-validation-error当发现校验错误时是否自动调用clean，默认false.
flyway.enabled是否开启flywary，默认true.
flyway.encoding设置迁移时的编码，默认UTF-8.
flyway.ignore-failed-future-migration当读取元数据表时是否忽略错误的迁移，默认false.
flyway.init-sqls当初始化好连接时要执行的SQL.
flyway.locations迁移脚本的位置，默认db/migration.
flyway.out-of-order是否允许无序的迁移，默认false.
flyway.password目标数据库的密码.
flyway.placeholder-prefix设置每个placeholder的前缀，默认${.
flyway.placeholder-replacementplaceholders是否要被替换，默认true.
flyway.placeholder-suffix设置每个placeholder的后缀，默认}.
flyway.placeholders.[placeholder name]设置placeholder的value
flyway.schemas设定需要flywary迁移的schema，大小写敏感，默认为连接默认的schema.
flyway.sql-migration-prefix迁移文件的前缀，默认为V.
flyway.sql-migration-separator迁移脚本的文件名分隔符，默认__
flyway.sql-migration-suffix迁移脚本的后缀，默认为.sql
flyway.tableflyway使用的元数据表名，默认为schema_version
flyway.target迁移时使用的目标版本，默认为latest version
flyway.url迁移时使用的JDBC URL，如果没有指定的话，将使用配置的主数据源
flyway.user迁移数据库的用户名
flyway.validate-on-migrate迁移时是否校验，默认为true
```

## 四、maven插件的使用

上面的操作，每次我们想要migration都需要运行整个springboot项目，并且只能执行migrate一种命令，其实flyway还是有很多其它命令的，maven插件给了我们不需要启动项目就能执行flyway各种命令的机会。

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.flywaydb</groupId>
                <artifactId>flyway-maven-plugin</artifactId>
                <version>5.2.4</version>
                <configuration>
                    <url>jdbc:mysql://localhost:3306/flyway-demo?characterEncoding=utf-8&amp;useSSL=false&amp;serverTimezone=Asia/Shanghai
                    </url>
                    <user>root</user>
                    <password>root</password>
                    <driver>com.mysql.jdbc.Driver</driver>
                </configuration>
            </plugin>
        </plugins>
    </build>
```


### migrate

Migrate是指把数据库Schema迁移到最新版本，是Flyway工作流的核心功能，Flyway在Migrate时会检查Metadata(元数据)表，如果不存在会创建Metadata表，Metadata表主要用于记录版本变更历史以及Checksum之类的

### baseline

Baseline针对已经存在Schema结构的数据库的一种解决方案，即实现在非空数据库中新建Metadata表，并把Migrations应用到该数据库。

Baseline可以应用到特定的版本，这样在已有表结构的数据库中也可以实现添加Metadata表，从而利用Flyway进行新Migrations的管理了

### clean（慎用）

Clean相对比较容易理解，清除掉对应数据库Schema中所有的对象，包括表结构，视图，存储过程等，clean操作在dev 和 test阶段很好用，但在生产环境务必禁用

### info

Info用于打印所有Migrations的详细和状态信息，其实也是通过Metadata表和Migrations完成的，Info能够帮助快速定位当前的数据库版本，以及查看执行成功和失败的Migrations。下图很好地示意了Info打印出来的信息

### repair

repair操作能够修复Metadata表，该操作在Metadata表出现错误时是非常有用的

### validate

Validate是指验证已经Apply的Migrations是否有变更，Flyway是默认是开启验证的。

Validate原理是对比Metadata表与本地Migrations的Checksum值，如果值相同则验证通过，否则验证失败，从而可以防止对已经Apply到数据库的本地Migrations的无意修改

### undo

撤销操作，社区版不支持

## 五、flyway知识补充

- flyway执行migrate必须在空白的数据库上进行，否则报错
- 对于已经有数据的数据库，必须先baseline，然后才能migrate
- clean操作是删除数据库的所有内容，包括baseline之前的内容
- 尽量不要修改已经执行过的SQL，即便是R开头的可反复执行的SQL，它们会不利于数据迁移
- 当需要做数据迁移的时候，更换一个新的空白数据库，执行下migrate命令，所有的数据库更改都可以一步到位地迁移过去