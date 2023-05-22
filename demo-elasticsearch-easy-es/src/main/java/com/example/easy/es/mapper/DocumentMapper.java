package com.example.easy.es.mapper;

import cn.easyes.core.conditions.interfaces.BaseEsMapper;
import com.example.easy.es.domain.Document;
import org.springframework.stereotype.Repository;


/**
 * ES mapper
 */
@Repository
public interface DocumentMapper extends BaseEsMapper<Document> {

}
