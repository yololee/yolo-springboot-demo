package com.yolo.shardingjdbc.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yolo.shardingjdbc.domain.Order;
import org.springframework.stereotype.Component;

/**
 * 订单表 Mapper
 */
@Component
public interface OrderMapper extends BaseMapper<Order> {
}