package com.cheche365.cheche.manage.common.exception;

/**
 * Created by yinJianBin on 2017/3/6.
 */
public class FileUploadException extends RuntimeException {

    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Exception cause) {
        super(message, cause);
    }
}
