package com.yolo.log;


import com.yolo.log.mapper.SysOperLogMapper;
import com.yolo.log.pojo.SysOperLog;
import com.yolo.log.util.SpringBeanUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DemoLoginOperationLogApplicationTests {

    @Test
    public void contextLoads() {
        SysOperLog sysOperLog = new SysOperLog();
        sysOperLog.setTitle("测试日志");
        sysOperLog.setBusinessType(0);
        sysOperLog.setMethod("get");

        SpringBeanUtils.getBean(SysOperLogMapper.class).insertOperlog(sysOperLog);
    }

}
