package com.yolo.exception.handler.controller;
import com.yolo.exception.handler.common.ApiResponse;
import com.yolo.exception.handler.common.ApiStatus;
import com.yolo.exception.handler.exception.JsonException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 测试Controller
 */
@Controller
public class TestController {

    @GetMapping("/json")
    @ResponseBody
    public ApiResponse jsonException() {
        throw new JsonException(ApiStatus.UNKNOWN_ERROR);
    }

}