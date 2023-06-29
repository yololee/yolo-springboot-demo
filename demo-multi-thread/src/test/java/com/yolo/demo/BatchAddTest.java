package com.yolo.demo;

import cn.hutool.core.collection.ListUtil;
import com.yolo.demo.entity.Company;
import com.yolo.demo.service.CompanyService;
import com.yolo.demo.task.CompanyRunnable;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class BatchAddTest extends DemoMultiThreadApplicationTests{


    @Autowired
    private CompanyService companyService;

    /**
     * 原始批量插入
     */
    @Test
    public void batchInsert() {
        long start = System.currentTimeMillis();
        List<Company> companies = IntStream.range(0, 1000).mapToObj(s -> Company.builder().name("华为").contact("lisi" + s).contactType("phone").build()).collect(Collectors.toList());
        companyService.saveBatch(companies);
        long end = System.currentTimeMillis();
        log.info("一次性插入一万条耗时{}毫秒：" , end - start );
    }


    @Resource()
    @Qualifier("customAsyncThreadPool")
    private Executor executor;

    @Autowired
    private CompanyRunnable companyRunnable;

    /**
     * 使用线程池批量插入
     */
    @Test
    public void batchInsert2() {
        long start = System.currentTimeMillis();
        List<Company> companies = IntStream.range(0, 10000).mapToObj(s -> Company.builder().name("华为").contact("lisi" + s).contactType("phone").build()).collect(Collectors.toList());
        List<List<Company>> split = ListUtil.split(companies, 100);
        try {
            for (List<Company> companyList : split) {
                //创建一个类,里面执行具体的逻辑
                executor.execute(() -> companyRunnable.batchSave(companyList));
//                executor.execute(() -> companyService.saveBatch(companyList));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("批量插入失败");
        }
        long end = System.currentTimeMillis();
        log.info("一次性插入一万条耗时{}毫秒：" , end - start );
    }


    /**
     * 使用stream配合线程池批量插入
     */
    @Test
    public void batchInsert4() {
        long start = System.currentTimeMillis();
        List<Company> companies = IntStream.range(0, 10000).mapToObj(s -> Company.builder().name("华为").contact("lisi" + s).contactType("phone").build()).collect(Collectors.toList());
        List<List<Company>> split = ListUtil.split(companies, 100);
        asyncCallable1(split);
        long end = System.currentTimeMillis();
        log.info("一次性插入一万条耗时{}毫秒：" , end - start );
    }

    public <P> void asyncCallable1(List<P> list) {
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(i -> CompletableFuture.runAsync(() -> {
                try {
                    companyRunnable.batchSave((List<Company>) i);
                } catch (Exception e) {
                    log.error("Exception:" + e);
                }
            }, executor));
        }
    }
}
