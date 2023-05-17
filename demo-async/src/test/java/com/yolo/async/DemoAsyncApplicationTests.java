package com.yolo.async;

import com.yolo.async.demo.AsyncTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = DemoAsyncApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class DemoAsyncApplicationTests {

    @Autowired
    private AsyncTask asyncTask;

    @Test
    public void contextLoads() throws InterruptedException {
        long t1 = System.currentTimeMillis();
        asyncTask.doTask1("task1");
        asyncTask.doTask2("task2");
        asyncTask.doTask3("task3");
        Thread.sleep(1000);
        long t2 = System.currentTimeMillis();
        log.info("main cost {} ms", t2-t1);

    }

}
