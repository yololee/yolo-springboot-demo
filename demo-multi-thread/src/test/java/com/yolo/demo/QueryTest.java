package com.yolo.demo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import com.yolo.demo.entity.Company;
import com.yolo.demo.service.CompanyService;
import com.yolo.demo.task.QueryTask;
import com.yolo.demo.task.QueryTaskLoader;
import com.yolo.demo.util.ThreadUtil;
import com.yolo.demo.vo.CompanyVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
public class QueryTest extends DemoMultiThreadApplicationTests{

    @Autowired
    private CompanyService companyService;

    /**
     * 普通查询
     */
    @Test
    public void query1(){
        long start = System.currentTimeMillis();
        List<Company> list = companyService.list();
        List<CompanyVO> companyVOS = new ArrayList<>();
        for (Company company : list) {
            CompanyVO vo = new CompanyVO();
            BeanUtil.copyProperties(company,vo);
            companyVOS.add(vo);
        }
        log.info("查询出来的条数：{}" ,companyVOS.size() );
        long end = System.currentTimeMillis();
        log.info("查询耗时{}毫秒：" , end - start );
    }


    @Test
    public void query2() throws Exception {
        long start = System.currentTimeMillis();
        List<Company> list = companyService.list();

        List<QueryTask> tasks = new ArrayList<>();
        for (Company company : list) {
            QueryTask queryTask = new QueryTask();
            queryTask.setCompany(company);
            tasks.add(queryTask);
        }

        List<CompanyVO> voList = ThreadUtil.executeCompletionService(tasks);

        log.info("查询出来的条数：{}" ,voList.size() );
        long end = System.currentTimeMillis();
        log.info("查询耗时{}毫秒：" , end - start );
    }


    @Autowired
    private QueryTaskLoader<CompanyVO,Company> queryTaskLoader;

    @Resource()
    @Qualifier("customAsyncThreadPool")
    private Executor executor;

    @Test
    public void query3() throws Exception {
        long start = System.currentTimeMillis();
        List<Company> list = companyService.list();
        List<CompanyVO> voList = asyncCallable(list, queryTaskLoader);
        log.info("查询出来的条数：{}" ,voList.size() );
        long end = System.currentTimeMillis();
        log.info("查询耗时{}毫秒：" , end - start );
    }


    public <R, P> List<R> asyncCallable(List<P> list, QueryTaskLoader<R, P> loader) {
        if (CollectionUtils.isEmpty(list)) {
            return ListUtil.empty();
        }

        return list.stream().map(i -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return loader.load(i);
                    } catch (Exception e) {
                        log.error("Exception:" + e);
                    }
                    return null;
                }, executor)).map(CompletableFuture::join).filter(Objects::nonNull).collect(Collectors.toList());
    }




}
