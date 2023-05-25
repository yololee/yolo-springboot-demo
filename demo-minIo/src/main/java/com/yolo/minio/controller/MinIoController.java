package com.yolo.minio.controller;

import com.yolo.minio.config.MinIoProperties;
import com.yolo.minio.config.MinIoUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * MinIO测试接口
 */
@RestController
@RequestMapping("/api/minio")
public class MinIoController {

    @Resource
    private MinIoProperties minIoProperties;

    /**
     * 注解@RequestPart注解用于处理通过multipart/form-data格式上传的文件
     */
    @PostMapping(value = "/upload")
    public String upload(@RequestPart @RequestParam MultipartFile file,String name) {
        System.out.println(name);
        return MinIoUtil.upload(minIoProperties.getBucketName(), file);
    }

    @PostMapping(value = "/upload1")
    public List<String> upload1(@RequestPart @RequestParam MultipartFile[] file) {
        List<String> upload = MinIoUtil.upload(minIoProperties.getBucketName(),file);
        List<String> path = new ArrayList<>();
        for (String s : upload) {
            path.add( MinIoUtil.getFileUrl(minIoProperties.getBucketName(),s));
        }
        return path;
    }

    @GetMapping(value = "/download")
    public void download(@RequestParam("fileName") String fileName, HttpServletResponse response) {
        MinIoUtil.download(minIoProperties.getBucketName(), fileName, response);
    }

    @GetMapping(value = "/delete")
    public String delete(@RequestParam("fileName") String fileName) {
        MinIoUtil.deleteFile(minIoProperties.getBucketName(), fileName);
        return "删除成功";
    }

}
