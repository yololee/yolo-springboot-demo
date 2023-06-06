package com.yolo.demo;

import com.yolo.demo.dto.CarDto;
import com.yolo.demo.dto.SeatConfigurationDto;
import com.yolo.demo.entity.Car;
import io.github.linpeilie.Converter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CustomObjectTest extends DemoMapstructPlusApplicationTests{

    @Autowired
    private Converter converter;

    @Test
    public void test1(){
        SeatConfigurationDto seatConfigurationDto = new SeatConfigurationDto();
        seatConfigurationDto.setCount(20);
        log.info("SeatConfigurationDto：{}",seatConfigurationDto);
        CarDto carDto = new CarDto();
        carDto.setName("大巴");
        carDto.setSeatConfiguration(seatConfigurationDto);
        log.info("CarDto：{}",carDto);

        Car car = converter.convert(carDto, Car.class);
        log.info("Car：{}",car);
    }
}
