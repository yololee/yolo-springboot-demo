package com.yolo.auto.register.core;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.yolo.auto.register.annotation.XxlRegister;
import com.yolo.auto.register.service.JobGroupService;
import com.yolo.auto.register.service.JobInfoService;
import com.yolo.auto.register.model.XxlJobGroup;
import com.yolo.auto.register.model.XxlJobInfo;
import groovy.util.logging.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Slf4j
@Component
public class XxlJobAutoRegister implements ApplicationListener<ApplicationReadyEvent>,
        ApplicationContextAware {

    private static final Log log =LogFactory.get();

    private ApplicationContext applicationContext;

    @Autowired
    private JobGroupService jobGroupService;

    @Autowired
    private JobInfoService jobInfoService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //注册执行器
        addJobGroup();
        //注册任务
        addJobInfo();
    }

    /**
     * 自动注册执行器
     * 配置文件中的appName和title精确匹配查看调度中心是否已有执行器被注册过了，如果存在则跳过，不存在则新注册一个
     */
    private void addJobGroup() {
        //存在直接返回
        if (jobGroupService.preciselyCheck()) {
            return;
        }

        //不存在新增执行器
        if(jobGroupService.autoRegisterGroup()) {
            log.info("auto register xxl-job group success!");
        }
    }


    /**
     * 添加任务
     * 1、通过applicationContext拿到spring容器中的所有bean，再拿到这些bean中所有添加了@XxlJob注解的方法
     * 2、对上面获取到的方法进行检查，是否添加了我们自定义的@XxlRegister注解，如果没有则跳过，不进行自动注册
     * 3、对同时添加了@XxlJob和@XxlRegister的方法，通过执行器id和jobHandler的值判断是否已经在调度中心注册过了，如果已存在则跳过
     * 4、对于满足注解条件且没有注册过的jobHandler，调用接口注册到调度中心
     */
    private void addJobInfo() {
        //获取执行器
        List<XxlJobGroup> jobGroups = jobGroupService.getJobGroup();
        XxlJobGroup xxlJobGroup = jobGroups.get(0);

        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);

            Map<Method, XxlJob> annotatedMethods  = MethodIntrospector.selectMethods(bean.getClass(),
                    (MethodIntrospector.MetadataLookup<XxlJob>) method -> AnnotatedElementUtils.findMergedAnnotation(method, XxlJob.class));
            for (Map.Entry<Method, XxlJob> methodXxlJobEntry : annotatedMethods.entrySet()) {
                Method executeMethod = methodXxlJobEntry.getKey();
                XxlJob xxlJob = methodXxlJobEntry.getValue();

                //自动注册
                if (executeMethod.isAnnotationPresent(XxlRegister.class)) {
                    XxlRegister xxlRegister = executeMethod.getAnnotation(XxlRegister.class);
                    List<XxlJobInfo> jobInfo = jobInfoService.getJobInfo(xxlJobGroup.getId(), xxlJob.value());
                    if (!jobInfo.isEmpty()){
                        //因为是模糊查询，需要再判断一次
                        Optional<XxlJobInfo> first = jobInfo.stream()
                                .filter(xxlJobInfo -> xxlJobInfo.getExecutorHandler().equals(xxlJob.value()))
                                .findFirst();
                        if (first.isPresent()) {
                            continue;
                        }
                    }

                    XxlJobInfo xxlJobInfo = createXxlJobInfo(xxlJobGroup, xxlJob, xxlRegister);
                    jobInfoService.addJobInfo(xxlJobInfo);
                }
            }
        }
    }

    private XxlJobInfo createXxlJobInfo(XxlJobGroup xxlJobGroup, XxlJob xxlJob, XxlRegister xxlRegister){
        XxlJobInfo xxlJobInfo=new XxlJobInfo();
        xxlJobInfo.setJobGroup(xxlJobGroup.getId());
        xxlJobInfo.setJobDesc(xxlRegister.jobDesc());
        xxlJobInfo.setAuthor(xxlRegister.author());
        xxlJobInfo.setScheduleType("CRON");
        xxlJobInfo.setScheduleConf(xxlRegister.cron());
        xxlJobInfo.setGlueType("BEAN");
        xxlJobInfo.setExecutorHandler(xxlJob.value());
        xxlJobInfo.setExecutorRouteStrategy(xxlRegister.executorRouteStrategy());
        xxlJobInfo.setMisfireStrategy("DO_NOTHING");
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobInfo.setExecutorTimeout(0);
        xxlJobInfo.setExecutorFailRetryCount(0);
        xxlJobInfo.setGlueRemark("GLUE代码初始化");
        xxlJobInfo.setTriggerStatus(xxlRegister.triggerStatus());

        return xxlJobInfo;
    }

}