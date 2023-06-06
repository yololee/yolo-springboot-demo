package com.yolo.demo;

import com.yolo.demo.dto.CarDto;
import com.yolo.demo.dto.SeatConfigurationDto;
import com.yolo.demo.dto.UserDto;
import com.yolo.demo.entity.*;
import com.yolo.demo.vo.GoodsVo;
import io.github.linpeilie.Converter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
public class CustomTypeConvertTest extends DemoMapstructPlusApplicationTests {


    @Autowired
    private Converter converter;

    @Test
    public void test1() {
        UserDto userDto = new UserDto();
        userDto.setEducations("1,2,3");

        log.info("userDto：{}", userDto);

        User user = converter.convert(userDto, User.class);
        log.info("Car：{}", user);
    }

    @Test
    public void test2() {
        SeatConfiguration seatConfiguration = new SeatConfiguration();
        seatConfiguration.setCount(30);

        Goods goods = new Goods();
        goods.setSeat(seatConfiguration);

        GoodsVo convert = converter.convert(goods, GoodsVo.class);
        System.out.println(convert);
    }


    @Test
    public void test3() {
        Order order = new Order();
        order.setOrderTime(LocalDateTime.now());
        order.setCreateTime(new Date());
        order.setDate("2022-11-11");
        System.out.println(order);

        OrderEntity convert = converter.convert(order, OrderEntity.class);
        System.out.println(convert);
    }

    @Test
    public void test4() {
        Order order = new Order();
        order.setOrderPrice(new BigDecimal("1.54766536"));
        order.setGoodsNum(2);
        System.out.println(order);

        OrderEntity orderEntity = converter.convert(order, OrderEntity.class);
        System.out.println(orderEntity);

        Order order1 = converter.convert(orderEntity, Order.class);
        System.out.println(order1);
    }
}
