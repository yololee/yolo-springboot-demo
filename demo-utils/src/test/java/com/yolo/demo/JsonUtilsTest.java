package com.yolo.demo;

import com.yolo.demo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class JsonUtilsTest extends DemoUtilsApplicationTests{


    @Test
    public void test1(){
        User user = User.builder().id("121")
                .username("zhangsan")
                .date(new Date())
                .createTime(1685548800000L)
                .mobile("123123123")
                .build();

//        String json = JsonUtil.objectToJson(new User());

//        User newUser = JsonUtil.jsonToObject(json, User.class);
//        log.info("json转对象{}:",newUser);
//
//        Map<Object, String> objectStringMap = JsonUtil.jsonToHashMap(json, String.class);
//        System.out.println(objectStringMap);
    }


    @Test
    public void test2(){
        User user = User.builder().id("121")
                .username("zhangsan")
                .date(new Date())
                .createTime(1685548800000L)
                .mobile("123123123")
                .build();

        User user2 = User.builder().id("121")
                .username("lisi")
                .date(new Date())
                .createTime(1685548800000L)
                .mobile("123123123")
                .build();

        List<User> list = new ArrayList<>();
        list.add(user2);
        list.add(user);



//        String json = JsonUtil.objectToJson(list);
//        log.info("对象转json{}:",json);



//        User[] users = JsonUtil.jsonToArray(json, User.class);
//        for (User user1 : users) {
//            System.out.println(user1);
//        }
//        log.info("json转对象{}:",users);
    }

}
