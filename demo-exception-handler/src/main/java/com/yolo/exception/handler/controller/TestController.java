package com.yolo.exception.handler.controller;
import com.yolo.exception.handler.common.Status;
import com.yolo.exception.handler.exception.JsonException;
import com.yolo.exception.handler.common.ApiResponse;
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
        throw new JsonException(Status.UNKNOWN_ERROR);
    }

}