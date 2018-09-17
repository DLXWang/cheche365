package com.cheche365.cheche.scheduletask.exception;

/**
 * Created by sunhuazhong on 2015/5/5.
 */
public class TaskException extends RuntimeException {
    private String code;
    private String message;

    public TaskException() {
    }

    public TaskException(String code) {
        this.code = code;
    }

    public TaskException(String code, String message) {
        super(code);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
