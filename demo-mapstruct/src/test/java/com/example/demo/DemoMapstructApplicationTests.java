package com.example.demo;


import com.example.demo.dto.PersonDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.BasicEntity;
import com.example.demo.entity.User;
import com.example.demo.mapstruct.UserConvertMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest(classes = DemoMapstructApplication.class)
@RunWith(SpringRunner.class)
public class DemoMapstructApplicationTests {

    @Test
    public void quickStart() {
        UserDto dto = new UserDto();
        dto.setUsername("jack");
        dto.setAge(23);
        dto.setYoung(false);

        User user = UserConvertMapper.INSTANCE.convert(dto);
        System.out.println(user);
    }


    @Test
    public void test1() {
        UserDto dto = new UserDto();
        dto.setUsername("jack");
        dto.setAge(23);
        dto.setYoung(false);

        User user = new User();
//        UserConvertMapper.INSTANCE.dto2Entity(dto,user);

        System.out.println(user);//User(username=jack, age=23, young=false, address=武汉, createTime=Wed Jun 07 10:40:45 CST 2023, source=null, height=0.0)
    }


    @Test
    public void test12() {
        UserDto dto = new UserDto();
        dto.setUsername("jack");
        dto.setAge(23);
        dto.setYoung(false);
        dto.setCreateTime(new Date());

        User user = new User();
        UserConvertMapper.INSTANCE.dto2Entity2(dto, user);

        System.out.println(user);
    }


    @Test
    public void test13() {
        UserDto dto = new UserDto();
        dto.setUsername("jack");
        dto.setAge(23);
        dto.setYoung(false);
        dto.setCreateTime(new Date(1685548800000L));//2023-06-01


        BasicEntity basicEntity = new BasicEntity();
        basicEntity.setCreateTime(new Date());

        User user = UserConvertMapper.INSTANCE.dtoToEntity2(dto, basicEntity);

        System.out.println(user);//User(username=jack, age=23, young=false, address=null, createTime=2023-06-07, source=null, height=0.0)
    }



    @Test
    public void test14() {
        UserDto dto = new UserDto();
        dto.setUsername("jack");
        dto.setAge(23);
        dto.setYoung(false);
        dto.setCreateTime(new Date(1685548800000L));//2023-06-01

        PersonDto personDto = new PersonDto();
        personDto.setPhones("1,2,3,4");
        dto.setPersonDto(personDto);

        User user = UserConvertMapper.INSTANCE.dtoToEntity3(dto);
        //User(username=jack, age=23, young=false, address=武汉, createTime=2023-06-01, source=null, height=0.0, personDto=PersonDto(phones=1,2,3,4))
        System.out.println(user);
    }

}
