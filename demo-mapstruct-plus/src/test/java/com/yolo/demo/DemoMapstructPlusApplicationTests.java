package com.yolo.demo;

import com.yolo.demo.dto.UserDto;
import com.yolo.demo.entity.User;
import io.github.linpeilie.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = DemoMapstructPlusApplication.class)
@RunWith(SpringRunner.class)
public class DemoMapstructPlusApplicationTests {


    @Autowired
    private Converter converter;

    @Test
    public void quickStart() {

        User user = new User();
        user.setUsername("jack");
        user.setAge(23);
        user.setYoung(false);


        UserDto userDto = new UserDto();
        userDto.setUsername("zhangsan");
        userDto.setAge(18);
        userDto.setYoung(true);

//        UserDto userDto = converter.convert(user, UserDto.class);
//        System.out.println(userDto);    // UserDto{username='jack', age=23, young=false}

//        assert user.getUsername().equals(userDto.getUsername());
//        assert user.getAge() == userDto.getAge();
//        assert user.isYoung() == userDto.isYoung();

        User newUser = converter.convert(userDto, user);

        System.out.println(newUser);    // User{username='jack', age=23, young=false}
    }


}
