package com.yolo.xxl.job.http;

import com.yolo.xxl.job.http.model.XxlJobInfo;
import com.yolo.xxl.job.http.util.XxlJobApiUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class XxlJobGroupTest extends DemoXxlJobHttpApplicationTests{

    @Autowired
    private XxlJobApiUtils xxlJobApiUtils;

    @Test
    public void xxlJobGroupList(){
        String s = xxlJobApiUtils.xxlJobList();
        System.out.println(s);
    }

    @Test
    public void xxlJobAdd(){
        XxlJobInfo info = new XxlJobInfo();
        info.setScheduleConf("0/2 * * * * ?");
        info.setJobDesc("手动添加的任务");
        info.setAuthor("admin");
        info.setExecutorHandler("demoTask");
        String s = xxlJobApiUtils.xxlJobAdd(info);
        System.out.println(s);
    }

    @Test
    public void xxlJobTrigger(){
       xxlJobApiUtils.xxlJobTrigger(5,null);
    }

    @Test
    public void xxlJobStop(){
        xxlJobApiUtils.xxlJobStop(5);
    }

    @Test
    public void xxlJobStart(){
        xxlJobApiUtils.xxlJobStart(5);
    }

    @Test
    public void xxlJobRemove(){
        xxlJobApiUtils.xxlJobRemove(5);
    }



}
