package com.yolo.auto.register.service;



import com.yolo.auto.register.model.XxlJobGroup;

import java.util.List;

public interface JobGroupService {

    List<XxlJobGroup> getJobGroup();

    boolean autoRegisterGroup();

    boolean preciselyCheck();

}