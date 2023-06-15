package com.yolo.demo.mapper;

import com.yolo.demo.domain.MsgLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jujueaoye
* @description 针对表【msg_log(消息投递日志)】的数据库操作Mapper
* @createDate 2023-06-14 17:24:26
* @Entity com.yolo.demo.domain.MsgLog
*/
@Mapper
public interface MsgLogMapper extends BaseMapper<MsgLog> {

}




