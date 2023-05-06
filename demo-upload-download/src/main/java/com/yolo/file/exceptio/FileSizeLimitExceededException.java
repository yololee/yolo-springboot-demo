package com.yolo.file.exceptio;

/**
 * 文件名大小限制异常类
 *
 * @author ruoyi
 */
public class FileSizeLimitExceededException extends BaseException {
    private static final long serialVersionUID = 1L;


    public FileSizeLimitExceededException(Integer code, String message) {
        super(code, message);
    }

    public FileSizeLimitExceededException(long defaultMaxSize) {
        super(500,"upload.exceed.maxSize"+ ":" + defaultMaxSize);
    }
}
