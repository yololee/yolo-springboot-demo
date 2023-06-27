package com.yolo.demo.handler.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.yolo.demo.handler.EncryptionHandler;
import com.yolo.demo.util.EncryptUtil;
import com.yolo.demo.util.JsonUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;


@Slf4j
public class DecryptHttpInputMessage implements HttpInputMessage {
    private final HttpInputMessage inputMessage;
    private final String secretKey;


    public DecryptHttpInputMessage(HttpInputMessage inputMessage, String secretKey) {
        this.inputMessage = inputMessage;
        this.secretKey = secretKey;
    }

    @Override
    public InputStream getBody() {
        try {
            InputStream body = inputMessage.getBody();
            String content = getStringByInputStream(body);

            // content转map,获取密文
            Map<String, String> map = JsonUtils.jsonToHashMap(content,String.class);
            if (CollectionUtils.isEmpty(map)){
                throw new RuntimeException("请求体位空");
            }

            String cipherText = map.get(EncryptionHandler.ENCRYPT_KEY);

            // 解密密文
            // 获取偏移量
            String offset = secretKey.substring(10, 26);
            String decryptBody = EncryptUtil.decryptBySymmetry(cipherText, offset, EncryptUtil.AES);
            return new ByteArrayInputStream(decryptBody.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("请求数据处理失败！");
        }

    }

    @Override
    public HttpHeaders getHeaders() {
        return inputMessage.getHeaders();
    }

    public static String getStringByInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            byte[] b = new byte[10240];
            int n;
            while ((n = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, n);
            }
        } catch (Exception e) {
            inputStream.close();
            outputStream.close();
        }
        return outputStream.toString();
    }

}
