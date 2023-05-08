package com.yolo.file.controller;

import com.yolo.file.common.AjaxResult;
import com.yolo.file.common.Constants;
import com.yolo.file.config.ServerConfig;
import com.yolo.file.config.YoloConfig;
import com.yolo.file.util.FileUploadUtils;
import com.yolo.file.util.FileUtils;
import com.yolo.file.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用请求处理
 */
@Controller
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Autowired
    private ServerConfig serverConfig;

    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    @ResponseBody
    public AjaxResult uploadFile(MultipartFile file) {
        try {
            // 上传文件路径
            String filePath = YoloConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUtils.getName(fileName));
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 通用上传请求（多个）
     */
    @PostMapping("/uploads")
    @ResponseBody
    public AjaxResult uploadFiles(List<MultipartFile> files) {
        try {
            // 上传文件路径
            String filePath = YoloConfig.getUploadPath();
            List<String> urls = new ArrayList<>();
            List<String> fileNames = new ArrayList<>();
            List<String> newFileNames = new ArrayList<>();
            for (MultipartFile file : files) {
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(filePath, file);
                String url = serverConfig.getUrl() + fileName;
                urls.add(url);
                fileNames.add(fileName);
                newFileNames.add(FileUtils.getName(fileName));
            }
            AjaxResult ajax = AjaxResult.success();
            ajax.put("urls", StringUtils.join(urls, ","));
            ajax.put("fileNames", StringUtils.join(fileNames, ","));
            ajax.put("newFileNames", StringUtils.join(newFileNames, ","));
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 本地资源通用下载
     * http://localhost:8080/common/download/resource?resource=/profile/upload/2023/05/06/程序员头像_20230506095214A006.jpeg
     */
    @GetMapping("/download/resource")
    public void resourceDownload(String resource, Boolean delete,HttpServletResponse response) {
        try {
            if (!FileUtils.checkAllowDownload(resource)) {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
            }
            // 本地资源路径
            String localPath = YoloConfig.getFileUploadPath();
            // 数据库资源地址
            String downloadPath = localPath + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
            // 下载名称
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
            if (delete) {
                FileUtils.deleteFile(downloadPath);
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }
}
