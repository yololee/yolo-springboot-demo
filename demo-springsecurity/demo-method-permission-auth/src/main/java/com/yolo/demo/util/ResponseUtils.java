package com.yolo.demo.util;

import com.yolo.demo.common.dto.ApiResponse;
import com.yolo.demo.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Slf4j
public class ResponseUtils {

    public static void out(HttpServletResponse response, ApiResponse apiResponse) {
        String json = JsonUtils.objectToJson(apiResponse);

        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            out = response.getWriter();
            out.println(json);
        } catch (Exception ex) {
            log.error(ex + "输出JSON出错");
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }
}
