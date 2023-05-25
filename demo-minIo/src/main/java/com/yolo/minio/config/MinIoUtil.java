package com.yolo.minio.config;

import cn.hutool.core.util.StrUtil;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * MinIO工具类
 * @description Java Client API参考文档：https://min.io/docs/minio/linux/developers/java/API.html
 */
@Slf4j
@Component
public class MinIoUtil {

    @Resource
    private MinIoProperties minIoProperties;

    private static MinioClient minioClient;

    /**
     * 初始化minio配置
     */
    @PostConstruct
    public void init() {
        try {
            minioClient = MinioClient.builder()
                    .endpoint(minIoProperties.getUrl())
                    .credentials(minIoProperties.getAccessKey(), minIoProperties.getSecretKey())
                    .build();
            createBucket(minIoProperties.getBucketName());
        } catch (Exception e) {
            log.error("初始化minio配置异常：", e);
        }
    }

    // **************************** ↓↓↓↓↓↓ 桶操作 ↓↓↓↓↓↓ ****************************

    /**
     * 判断桶是否存在
     *
     * @param bucketName 桶名
     * @return true:存在 false:不存在
     */
    @SneakyThrows(Exception.class)
    public static boolean bucketExists(String bucketName) {
        boolean flag = false;
        try {
            flag = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        }catch (Exception e){
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * 创建桶
     *
     * @param bucketName 桶名
     */
    @SneakyThrows(Exception.class)
    public static void createBucket(String bucketName) {
        try {
            boolean isExist = bucketExists(bucketName);
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 获取全部桶
     *
     * @return 桶信息
     */
    @SneakyThrows(Exception.class)
    public static List<Bucket> getAllBuckets() {
        List<Bucket> bucketList = new ArrayList<>();
        try {
            bucketList = minioClient.listBuckets();
        }catch (Exception e){
            e.printStackTrace();
        }

        return bucketList;
    }

    // **************************** ↓↓↓↓↓↓ 文件操作 ↓↓↓↓↓↓ ****************************

    /**
     * 文件上传
     *
     * @param bucketName 桶名
     * @param file       文件
     * @return 文件url地址
     * @date 2020/8/16 23:40
     */
    @SneakyThrows(Exception.class)
    public static String upload(String bucketName, MultipartFile file) {
        InputStream inputStream = null;
        String fileName = file.getOriginalFilename();
        fileName = fileName + System.currentTimeMillis();
        try {
            inputStream = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return getFileUrl(bucketName, fileName);
    }


    /**
     * description: 上传文件
     */
    public static List<String> upload(String bucketName,MultipartFile[] multipartFile) {
        List<String> names = new ArrayList<>(multipartFile.length);
        for (MultipartFile file : multipartFile) {
            String fileName = file.getOriginalFilename();
            if (StrUtil.isNotBlank(fileName)){
                String[] split = fileName.split("\\.");
                if (split.length > 1) {
                    fileName = split[0] + "_" + System.currentTimeMillis() + "." + split[1];
                } else {
                    fileName = fileName + System.currentTimeMillis();
                }
            }
            InputStream in = null;
            try {
                in = file.getInputStream();
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(in, in.available(), -1)
                        .contentType(file.getContentType())
                        .build()
                );
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            names.add(fileName);
        }
        return names;
    }


    /**
     * 删除文件
     *
     * @param bucketName 桶名
     * @param fileName   文件名
     * @return void
     * @date 2020/8/16 20:53
     */
    @SneakyThrows(Exception.class)
    public static void deleteFile(String bucketName, String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 下载文件
     *
     * @param bucketName 桶名
     * @param fileName   文件名
     * @param response
     * @return void
     * @date 2020/8/17 0:34
     */
    @SneakyThrows(Exception.class)
    public static void download(String bucketName, String fileName, HttpServletResponse response) {
        // 获取对象的元数据
        InputStream is = null;
        try {
            final StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(fileName).build());
            response.setContentType(stat.contentType());
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            is = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
            IOUtils.copy(is, response.getOutputStream());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取minio文件的预览地址
     *
     * @param bucketName 桶名
     * @param fileName   文件名
     * @return 预览地址
     * @date 2020/8/16 22:07
     */
    @SneakyThrows(Exception.class)
    public static String getFileUrl(String bucketName, String fileName) {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(fileName)
                .expiry(2, TimeUnit.HOURS)
                .build());
    }

}
