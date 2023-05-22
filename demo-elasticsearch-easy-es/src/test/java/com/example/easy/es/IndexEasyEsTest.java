package com.example.easy.es;

import cn.easyes.annotation.rely.Analyzer;
import cn.easyes.annotation.rely.FieldType;
import cn.easyes.core.conditions.LambdaEsIndexWrapper;
import com.example.easy.es.domain.Document;
import com.example.easy.es.mapper.DocumentMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class IndexEasyEsTest extends DemoElasticsearchEasyEsApplicationTests{

    @Autowired
    private DocumentMapper documentMapper;

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
}
