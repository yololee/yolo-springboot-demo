package com.yolo.demo;


import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.yolo.demo.domain.ApiResponse;
import com.yolo.demo.domain.UserVO;
import com.yolo.demo.rpc.MyClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = DemoForestApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class DemoForestApplicationTests {

    @Autowired
    private MyClient myClient;

    @Test
    public void test1() {
        String result = myClient.simpleRequest();
        log.info("一个简单请求: 【{}】", result);
    }

    @Test
    public void test2() {
        String result = myClient.send2("http://127.0.0.1:8080/forest/hello");
        log.info("一个简单请求: 【{}】", result);
    }
    @Test
    public void test3() {
        String result = myClient.send3("127.0.0.1",8080);
        log.info("一个简单请求: 【{}】", result);
    }

    @Test
    public void test4() {
        String result = myClient.send4();
        log.info("一个简单请求: 【{}】", result);
    }

    @Test
    public void test5() {
//        String result = myClient.send5();
        ForestResponse<ApiResponse> response = myClient.send7();
        // 判断请求是否成功
        if (response.isSuccess()) {
            // 通过getResult方法获取其响应内容反序列化后的结果
            // 因为返回类型 ForestResponse<userVO> 中泛型参数为 userVO,
            // 所以得到反序列化后的对象也是userVO类型对象
            ApiResponse result = response.getResult();
            UserVO userVO = (UserVO) result.getData();
            log.info("一个简单请求: 【{}】", userVO);
        }

    }


}
