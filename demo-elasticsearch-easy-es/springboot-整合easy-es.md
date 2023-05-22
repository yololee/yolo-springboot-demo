# springboot-整合easy-es

## 一、项目版本

Jdk：1.8

springboot：2.3.12.RELEASE

es：7.14.1

kabana：7.14.1

> [docker 部署es和kabana](https://gitee.com/zhengqingya/docker-compose/blob/master/Linux/elasticsearch/docker-compose-elasticsearch.yml)

## 二、整合

### 1、pom.xml

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
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

        <!-- Easy-Es -->
        <!-- https://mvnrepository.com/artifact/cn.easy-es/easy-es-boot-starter -->
        <dependency>
            <groupId>cn.easy-es</groupId>
            <artifactId>easy-es-boot-starter</artifactId>
            <version>1.1.0</version>
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

        <!-- guava -->
        <!-- https://mvnrepository.com/artifact/com.google.guava/guava -->
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>31.1-jre</version>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.4.5</version>
            <scope>test</scope>
        </dependency>
```

### 2、application.yml

```yml
easy-es:
  enable: true # 默认为true,若为false时,则认为不启用本框架
  address: 127.0.0.1:9200  # 填你的es连接地址
  username: elastic
  password: 123456
```

### 3、启动类

启动类中添加 @EsMapperScan 注解，扫描 Mapper 文件夹

```java
@SpringBootApplication
@EsMapperScan("com.example.easy.es.mapper")
public class DemoElasticsearchEasyEsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoElasticsearchEasyEsApplication.class, args);
    }
```

### 4、实体类和mapper

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IndexName("document")
public class Document {
    /**
     * es中的唯一id
     */
    @IndexId
    private String id;

    /**
     * 文档标题
     */
    private String title;
    /**
     * 文档内容
     */
    // 高亮查询
    @HighLight(preTag = "<em>", postTag = "</em>")
    // 分词查询
    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_MAX_WORD, searchAnalyzer = Analyzer.IK_MAX_WORD)
    private String content;

}
```

```java
@Repository
public interface DocumentMapper extends BaseEsMapper<Document> {

}
```

## 三、索引CRUD

首先说一下索引的托管模式，EE这里有三种托管模式

1. 自动托管之平滑模式(默认)：在此模式下,索引的创建更新数据迁移等全生命周期用户均不需要任何操作即可完成
2. 自动托管之非平滑模式：在此模式下,索引额创建及更新由EE全自动异步完成,但不处理数据迁移工作
3. 手动模式：在此模式下,索引的所有维护工作EE框架均不介入,由用户自行处理,EE提供了开箱即用的索引CRUD相关API

> 前置条件

索引CRUD相关的API都属于手动挡范畴,因此我们执行下述所有API前必须先配置开启手动挡,以免和自动挡冲突

```yml
easy-es:
  global-config:
    process_index_mode: manul # 手动挡模式
```

#### 创建索引

```java
    @Test
    void createIndex01(){
        // 绝大多数场景推荐使用
        documentMapper.createIndex();
    }

    @Test
    void createIndex02(){
        // 适用于定时任务按日期创建索引场景
        String indexName = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        documentMapper.createIndex(indexName);
    }

    @Test
    void createIndex03() {
        // 复杂场景使用
        LambdaEsIndexWrapper<Document> wrapper = new LambdaEsIndexWrapper<>();
        // 此处简单起见 索引名称须保持和实体类名称一致,字母小写 后面章节会教大家更如何灵活配置和使用索引
        wrapper.indexName(Document.class.getSimpleName().toLowerCase());

        // 此处将文章标题映射为keyword类型(不支持分词),文档内容映射为text类型(支持分词查询)
        wrapper.mapping(Document::getTitle, FieldType.KEYWORD, 2.0f)
                .mapping(Document::getContent, FieldType.TEXT, Analyzer.IK_SMART, Analyzer.IK_MAX_WORD);

        // 设置分片及副本信息,可缺省
        wrapper.settings(3, 2);
        // 创建索引
        boolean isOk = documentMapper.createIndex(wrapper);

    }
```

#### 查询索引

```java
    @Test
    public void testExistsIndex() {
        // 测试是否存在指定名称的索引
        String indexName = Document.class.getSimpleName().toLowerCase();
        boolean existsIndex = documentMapper.existsIndex(indexName);
        Assertions.assertTrue(existsIndex);
    }

    @Test
    public void testGetIndex() {
        GetIndexResponse indexResponse = documentMapper.getIndex();
        // 这里打印下索引结构信息 其它分片等信息皆可从indexResponse中取
        indexResponse.getMappings().forEach((k, v) -> System.out.println(v.getSourceAsMap()));
    }

```

#### 更新索引

```java
    /**
     * 更新索引
     */
    @Test
    public void testUpdateIndex() {
        // 测试更新索引
        LambdaEsIndexWrapper<Document> wrapper = new LambdaEsIndexWrapper<>();
        // 指定要更新哪个索引
        String indexName = Document.class.getSimpleName().toLowerCase();
        wrapper.indexName(indexName);
        wrapper.mapping(Document::getTitle, FieldType.KEYWORD);
        wrapper.mapping(Document::getContent, FieldType.TEXT, Analyzer.IK_SMART, Analyzer.IK_MAX_WORD);
        wrapper.mapping(Document::getInfo, FieldType.TEXT, Analyzer.IK_SMART, Analyzer.IK_MAX_WORD);
        boolean isOk = documentMapper.updateIndex(wrapper);
        Assertions.assertTrue(isOk);
    }
```

#### 删除索引

```java
    @Test
    public void testDeleteIndex() {
        // 指定要删除哪个索引
        String indexName = Document.class.getSimpleName().toLowerCase();
        boolean isOk = documentMapper.deleteIndex(indexName);
        Assertions.assertTrue(isOk);
    }
```

## 四、数据CRUD

```java
@Slf4j
public class EasyEsTest extends DemoElasticsearchEasyEsApplicationTests{


    @Autowired
    private DocumentMapper documentMapper;

    @Test
    public void insert(){
        Integer insert = documentMapper.insert(Document.builder().title("测试").content("国庆节快乐").build());
        log.info("插入成功{}",insert);
    }

    @Test
    public void insertRandom(){
        List<Document> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(
                    Document.builder()
                            .id(RandomUtil.randomNumbers(10))
                            .title("yolo:" + RandomUtil.randomString("奶茶店里的小帅", 5))
                            .content(RandomUtil.randomString("奶茶店里的小帅很好看哦，希望你多去喝一杯", 20))
                            .build()
            );
        }

        documentMapper.insertBatch(list);
    }


    @Test
    public void search(){
        List<Document> documentList = this.documentMapper.selectList(
                new LambdaEsQueryWrapper<Document>().eq(Document::getTitle, "测试")
        );
        log.info(JSONUtil.toJsonStr(documentList));
    }

    @Test
    public void searchKeyword(){
        List<Document> documentList = this.documentMapper.selectList(
                new LambdaEsQueryWrapper<Document>().match(Document::getContent, "小帅")
        );
        log.info(JSONUtil.toJsonStr(documentList));
    }

    @Test
    public void update(){
        documentMapper.update(
                Document.builder()
                        .title("测试111")
                        .content(RandomUtil.randomString(6))
                        .build(),
                new LambdaEsUpdateWrapper<Document>()
                        .eq(Document::getTitle, "测试")
        );
    }

    @Test
    public void delete() {
        documentMapper.delete(new LambdaEsQueryWrapper<Document>().eq(Document::getTitle, "测试111"));
    }
}
```

> [Eays-ES官方文档](https://www.easy-es.cn/)

