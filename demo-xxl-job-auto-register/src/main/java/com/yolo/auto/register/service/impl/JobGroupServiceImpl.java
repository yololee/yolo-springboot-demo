package com.yolo.auto.register.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.yolo.auto.register.model.XxlJobActuatorInfo;
import com.yolo.auto.register.service.JobGroupService;
import com.yolo.auto.register.service.JobLoginService;
import com.yolo.auto.register.model.XxlJobGroup;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

@Service
public class JobGroupServiceImpl implements JobGroupService {

    private static final Logger logger = LoggerFactory.getLogger(JobGroupServiceImpl.class);

    @Value("${xxl.job.admin.address}")
    private String adminAddress;

    @Value("${xxl.job.executor.app-name}")
    @NotBlank
    @Length(max = 64)
    private String appName;

    @Value("${xxl.job.executor.title}")
    @NotBlank
    @Length(max = 12)
    private String title;

    /*
     * 执行器地址类型：0=自动注册、1=手动录入
     * */
    @Value("${xxl.job.executor.addressType:0}")
    private Integer addressType;

    /*
     * 执行器地址列表，多地址逗号分隔(手动录入)
     * */
    @Value("${xxl.job.executor.addressList:}")
    private String addressList;

    @Autowired
    private JobLoginService jobLoginService;

    private final static String JOB_GROUP_URI = "/jobgroup";


    /**
     * 根据appName和执行器名称title查询执行器列表
     *
     * @return {@link List}<{@link XxlJobGroup}>
     */
    @Override
    public List<XxlJobGroup> getJobGroup() {
//        logger.info("原生配置xxl-job调度中心地址-adminAddress  "+adminAddress);
//        logger.info("原生配置xxl-job执行器名称-appName  "+appName);
//        logger.info("新增配置yolo执行器标题-title  "+title);
//        logger.info("新增配置yolo注册类型-addressType  "+addressType);
//        logger.info("新增配置yolo注册地址-addressList  "+addressList);



        HttpResponse response = HttpUtil.createPost(adminAddress + JOB_GROUP_URI + "/pageList")
                .form("appname", appName)
                .form("title", title)
                .cookie(jobLoginService.getCookie())
                .execute();

        XxlJobActuatorInfo xxlJobActuatorInfo = JSONUtil.toBean(JSONUtil.parseObj(response.body()), XxlJobActuatorInfo.class);
        List<XxlJobGroup> data = xxlJobActuatorInfo.getData();
//        logger.info("模糊查询（执行器名称和标题）所有的执行器"+JSONUtil.toJsonStr(data));
        return data;
    }

    /**
     * 注册新executor到调度中心
     *
     * @return boolean
     */
    @Override
    public boolean autoRegisterGroup() {
        HttpRequest httpRequest = HttpUtil.createPost(adminAddress + JOB_GROUP_URI + "/save")
                .form("appname", appName)
                .form("title", title)
                .form("addressType", addressType);

        //0=自动注册、1=手动录入
        if (addressType.equals(1)) {
            if (Strings.isBlank(addressList)) {
                throw new RuntimeException("手动录入模式下,执行器地址列表不能为空");
            }
            httpRequest.form("addressList", addressList);
        }

        HttpResponse response = httpRequest.cookie(jobLoginService.getCookie())
                .execute();
        Object code = JSONUtil.parse(response.body()).getByPath("code");
        logger.info("添加执行器状态：" + response.body());
        return code.equals(200);
    }


    /**
     * 精确检查
     * 根据 appName和title
     *
     * @return boolean
     */
    @Override
    public boolean preciselyCheck() {
        //原生xxl-job-admin中分页模糊查询
        List<XxlJobGroup> jobGroup = getJobGroup();
        if (CollUtil.isEmpty(jobGroup)){
            return false;
        }
        Optional<XxlJobGroup> has = jobGroup.stream()
                .filter(xxlJobGroup -> xxlJobGroup.getAppname().equals(appName)
                        && xxlJobGroup.getTitle().equals(title))
                .findAny();
        //isPresent()方法用于判断value是否存在，不为NULL则返回true
        return has.isPresent();
    }

}