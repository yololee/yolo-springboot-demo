package com.example.easy.es;

import cn.easyes.core.conditions.LambdaEsQueryWrapper;
import cn.easyes.core.conditions.LambdaEsUpdateWrapper;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.example.easy.es.domain.Document;
import com.example.easy.es.mapper.DocumentMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

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
