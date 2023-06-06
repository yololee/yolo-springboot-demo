package com.yolo.demo.dto;

import com.yolo.demo.entity.Car;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

@Data
@AutoMapper(target = Car.class)
public class CarDto {
    private SeatConfigurationDto seatConfiguration;

    private String name;
}
