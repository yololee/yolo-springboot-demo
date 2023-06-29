package com.yolo.demo.task;



import cn.hutool.core.bean.BeanUtil;
import com.yolo.demo.service.CompanyService;
import com.yolo.demo.vo.CompanyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class QueryTaskLoader<R, P> {
    public R load(P p) {
        CompanyVO vo = new CompanyVO();
        BeanUtil.copyProperties(p,vo);
        return (R) vo;
    }


}
