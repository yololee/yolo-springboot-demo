package com.yolo.demo.task;



import cn.hutool.core.bean.BeanUtil;
import com.yolo.demo.service.CompanyService;
import com.yolo.demo.vo.CompanyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.List;


@Component
public class TaskLoader<R, P> {


    @Autowired
    private CompanyService companyService;

    public R load(P p) {
        CompanyVO vo = new CompanyVO();
        BeanUtil.copyProperties(p,vo);
        return (R) vo;
    }


}
