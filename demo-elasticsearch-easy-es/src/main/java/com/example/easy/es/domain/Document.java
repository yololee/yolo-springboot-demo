package com.example.easy.es.domain;

import cn.easyes.annotation.HighLight;
import cn.easyes.annotation.IndexField;
import cn.easyes.annotation.IndexId;
import cn.easyes.annotation.IndexName;
import cn.easyes.annotation.rely.Analyzer;
import cn.easyes.annotation.rely.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ES 数据模型
 */
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
