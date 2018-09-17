package com.cheche365.cheche.admin.exception;

import com.cheche365.cheche.manage.common.web.model.ResultModel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


/**
 * Created by wangfei on 2016/6/1.
 */
@Component
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(OperationNotAllowedException.class)
    public ResponseEntity<Object> handlerOperationNotAllowedException(RuntimeException ex, WebRequest request) {
        logger.error("order center has unsupported operation, get a operationNotAllowedException:\n {}", ExceptionUtils.getStackTrace(ex));
        return handleExceptionInternal(ex, new ResultModel(false, ex.getMessage()), getJsonHttpHeaders(), HttpStatus.OK, request);
    }

    private HttpHeaders getJsonHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
