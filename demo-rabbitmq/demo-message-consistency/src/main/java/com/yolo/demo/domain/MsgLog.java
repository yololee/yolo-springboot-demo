package com.yolo.demo.domain;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;



import java.io.Serializable;


/**
* 消息投递日志
* @TableName msg_log
*/
@Data
@Builder
public class MsgLog implements Serializable {

    /**
    * 消息唯一标识
    */
    @TableId
    private String id;
    /**
    * 消息体, json格式化
    */
    private String msg;
    /**
    * 交换机
    */
    private String exchange;
    /**
    * 路由键
    */
    private String routingKey;
    /**
    * 状态: 0投递中 1投递成功 2投递失败 3已消费
    */
    private Integer status;
    /**
    * 重试次数
    */
    private Integer tryCount;
    /**
    * 下一次重试时间
    */
    private Long nextTryTime;
    /**
    * 创建时间
    */
    private Long createTime;
    /**
    * 更新时间
    */
    private Long updateTime;
}
