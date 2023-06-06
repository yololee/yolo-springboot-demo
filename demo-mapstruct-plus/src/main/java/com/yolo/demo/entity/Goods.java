package com.yolo.demo.entity;

import com.yolo.demo.vo.GoodsVo;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import lombok.Data;

@Data
@AutoMapper(target = GoodsVo.class, reverseConvertGenerate = false)
public class Goods {

    @AutoMapping(source = "seat.count", target = "price")
    private SeatConfiguration seat;

}
