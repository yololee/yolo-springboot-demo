package com.yolo.file.exceptio;

/**
 * 文件名称超长限制异常类
 */
public class FileNameLengthLimitExceededException extends BaseException {
    private static final long serialVersionUID = 1L;

    public FileNameLengthLimitExceededException(int defaultFileNameLength) {
        super(500,"upload.filename.exceed.length" + ":" +defaultFileNameLength);
    }
}
