package com.yolo.demo.task;

import com.yolo.demo.entity.Company;
import com.yolo.demo.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
public class BatchSaveThread {

    @Autowired
    private CompanyService companyService;


    @Async(value = "customAsyncThreadPool")
    public void asyncBatchSave(int i, CountDownLatch countDownLatch, List<Company> list) {
        try {
            companyService.saveBatch(list);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //latch.countDown()放入进finally中，这样即使报错，直接记录跨过，也不会影响主线程的执行
            countDownLatch.countDown();
        }
    }





}
