package com.yolo.auto.register.service;



import com.yolo.auto.register.model.XxlJobInfo;

import java.util.List;

public interface JobInfoService {

    List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler);

    Integer addJobInfo(XxlJobInfo xxlJobInfo);

}