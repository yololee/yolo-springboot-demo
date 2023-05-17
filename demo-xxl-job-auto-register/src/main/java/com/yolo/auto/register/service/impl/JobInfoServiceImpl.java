package com.yolo.auto.register.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yolo.auto.register.service.JobInfoService;
import com.yolo.auto.register.service.JobLoginService;
import com.yolo.auto.register.model.XxlJobInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class JobInfoServiceImpl implements JobInfoService {

    private static final Logger logger = LoggerFactory.getLogger(JobGroupServiceImpl.class);

    @Value("${xxl.job.admin.address}")
    private String adminAddress;

    private final static String JOB_INFO_URI = "/jobinfo";

    @Autowired
    private JobLoginService jobLoginService;

    @Override
    public List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler) {

//        logger.info("原生配置xxl-job调度中心地址"+adminAddress);

        HttpResponse response = HttpUtil.createPost(adminAddress+JOB_INFO_URI+"/pageList")
                .form("jobGroup", jobGroupId)
                .form("executorHandler", executorHandler)
                .form("triggerStatus", -1)
                .cookie(jobLoginService.getCookie())
                .execute();

        JSONArray array = JSONUtil.parse(response.body()).getByPath("data", JSONArray.class);
        List<XxlJobInfo> list = array.stream()
                .map(o -> JSONUtil.toBean((JSONObject) o, XxlJobInfo.class))
                .collect(Collectors.toList());

//        logger.info("获取(执行器id和executorHandler)任务列表" + JSONUtil.toJsonStr(list));

        return list;
    }

    @Override
    public Integer addJobInfo(XxlJobInfo xxlJobInfo) {
        Map<String, Object> paramMap = BeanUtil.beanToMap(xxlJobInfo);
        HttpResponse response = HttpUtil.createPost(adminAddress+JOB_INFO_URI+"/add")
                .form(paramMap)
                .cookie(jobLoginService.getCookie())
                .execute();

        JSON json = JSONUtil.parse(response.body());
        Object code = json.getByPath("code");
        if (code.equals(200)){
            logger.info("新增任务成功");
            return Convert.toInt(json.getByPath("content"));
        }
        throw new RuntimeException("add jobInfo error!");
    }

}