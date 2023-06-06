package com.yolo.demo.dto;


import io.github.linpeilie.annotations.AutoMapper;
import lombok.Builder;
import lombok.Data;

@Data
@AutoMapper(target = SeatConfigurationDto.class)
public class SeatConfigurationDto {

    private Integer count;

}
