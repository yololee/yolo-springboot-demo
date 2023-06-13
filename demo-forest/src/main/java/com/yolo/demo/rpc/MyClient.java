package com.yolo.demo.rpc;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.yolo.demo.domain.ApiResponse;
import com.yolo.demo.domain.UserVO;
import org.springframework.stereotype.Component;


/**
 * 若全局变量中已定义 baseUrl
 * 便会将全局变量中的值绑定到 @BaseRequest 的属性中
 */
@BaseRequest(baseURL = "${baseURL}")
@Component
//@Address(host = "${host}",port = "${port}")
public interface MyClient {

    @Request("http://127.0.0.1:8080/forest/hello")
    String simpleRequest();

    /**
     * 整个完整的URL都通过 @Var 注解修饰的参数动态传入
     */
    @Get("{myURL}")
    String send2(@Var("myURL") String myUrl);

    @Address(host = "{0}", port = "{1}")
    @Get("/forest/hello")
    String send3(String host, int port);

    @Get("/forest/hello")
    String send4();

    @Post("/forest/user/list")
    String send5();

    @Post("/forest/user/list")
    ForestResponse<String> send6();

    @Post(value = "/forest/user/list",dataType = "json")
    ForestResponse<ApiResponse> send7();

}
