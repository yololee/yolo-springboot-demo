package com.yolo.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yolo.demo.domain.MsgLog;
import com.yolo.demo.service.MsgLogService;
import com.yolo.demo.mapper.MsgLogMapper;
import org.springframework.stereotype.Service;

/**
* @author jujueaoye
* @description 针对表【msg_log(消息投递日志)】的数据库操作Service实现
* @createDate 2023-06-14 17:24:26
*/
@Service
public class MsgLogServiceImpl extends ServiceImpl<MsgLogMapper, MsgLog>
    implements MsgLogService{

}




