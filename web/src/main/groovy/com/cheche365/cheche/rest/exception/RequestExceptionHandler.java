package com.cheche365.cheche.rest.exception;

import com.cheche365.cheche.core.exception.*;
import com.cheche365.cheche.core.exception.handler.LackOfSupplementInfoHandler;
import com.cheche365.cheche.core.message.NotifyEmailMessage;
import com.cheche365.cheche.core.message.RedisPublisher;
import com.cheche365.cheche.core.util.RuntimeUtil;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.JDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

/**
 * Created by zhengwei on 3/30/15.
 */
@Component
@ControllerAdvice
public class RequestExceptionHandler extends ResponseEntityExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(RequestExceptionHandler.class);

    @Autowired
    private RedisPublisher redisPublisher;

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.error("REST API exception handler got a message not readable exception:\n" + ExceptionUtils.getStackTrace(ex));

        ErrorResource error = new ErrorResource(BusinessException.Code.INPUT_FIELD_NOT_VALID.getCodeValue(), ex.getMessage());
        this.setDebugMessage(error, ex);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(ex, error, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler({NonFatalBusinessException.class, KnownReasonException.class})
    protected ResponseEntity<Object> handleQuoteExceptionRequest(RuntimeException e, WebRequest request) {

        logger.error("REST API exception handler got a non-fatal business exception:\n" + ExceptionUtils.getStackTrace(e));
        if (e instanceof LackOfSupplementInfoException || e instanceof BadQuoteParameterException) {
            NonFatalBusinessException ex = (NonFatalBusinessException) e;
            HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            Object updatedErrorObject = LackOfSupplementInfoHandler.writeResponse(ex.getErrorObject(), ClientTypeUtil.getChannel(httpServletRequest));
            ex.setErrorObject(updatedErrorObject);
        }

        BusinessException businessException = (BusinessException) e;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status = getStatusByRequest(request);

        Object body = requestVersionIsV1(request) ? new ErrorResource(businessException.getCode().getCodeValue(), businessException.getMessage()) : businessException;
        return handleExceptionInternal(e, body, headers, status, request);
    }

    @ExceptionHandler({ASAPNotifyException.class})
    protected ResponseEntity<Object> handleASAPNotifyExceptionSendEmail(RuntimeException e, WebRequest request) {
        logger.error("REST API exception handler got a ASAPNotifyException which need to send email :\n" + ExceptionUtils.getStackTrace(e));
        redisPublisher.publish(new NotifyEmailMessage(ExceptionUtils.getStackTrace(e.getCause())));
        ASAPNotifyException asapNotifyException = (ASAPNotifyException) e;
        BusinessException cause = (BusinessException) asapNotifyException.getCause();
        ErrorResource error = new ErrorResource(cause.getCode().getCodeValue(), cause.getMessage());
        this.setDebugMessage(error, asapNotifyException);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(e, error, headers, HttpStatus.OK, request);
    }

    @ExceptionHandler({BusinessException.class})
    protected ResponseEntity<Object> handleInvalidRequest(RuntimeException e, WebRequest request) {

        logger.error("REST API exception handler got a business exception:\n" + ExceptionUtils.getStackTrace(e));
        BusinessException businessException = (BusinessException) e;

        ErrorResource error = new ErrorResource(businessException.getCode().getCodeValue(),businessException.getMessage(),businessException.getErrorObject());

        this.setDebugMessage(error, businessException);
        logger.debug("debugMessage:" + ExceptionUtils.getFullStackTrace(businessException));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status = getStatusByRequest(request);
        return handleExceptionInternal(e, error, headers, status, request);

    }

    @ExceptionHandler({DataAccessException.class, JDBCException.class, SQLException.class})
    protected ResponseEntity<Object> handleDBException(Exception ex, WebRequest request) throws Exception {
        if (requestVersionIsV1(request)) {
            if (ex instanceof RuntimeException) {
                return handleGeneralException(ex, request);
            } else {
                return handleException(ex, request);
            }
        }

        logger.error("数据库异常" + ExceptionUtils.getStackTrace(ex));
        ErrorResource error = new ErrorResource(BusinessException.Code.DB_ERROR.getCodeValue(), "数据库异常");
        this.setDebugMessage(error, ex);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(ex, error, headers, HttpStatus.OK, request);
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest request) {
        logger.error("REST API exception handler got a unexpected exception " + ExceptionUtils.getStackTrace(ex));

        String message = "非预期异常，请拨打4000-150-999";
        ErrorResource error = new ErrorResource(BusinessException.Code.INTERNAL_SERVICE_ERROR.getCodeValue(), message);
        this.setDebugMessage(error, ex);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status = getStatusByRequest(request);

        return handleExceptionInternal(ex, error, headers, status, request);
    }

    private HttpStatus getStatusByRequest(WebRequest request) {
        HttpStatus status;
        if (requestVersionIsV1(request)) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
        } else if (isBaoXianRequest(request)||isPingPlusRequest(request)) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            status = HttpStatus.OK;
        }
        return status;
    }

    private boolean requestVersionIsV1(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI().toString().startsWith("/v1/");
    }

    private boolean isBaoXianRequest(WebRequest request){
        return ((ServletWebRequest) request).getRequest().getRequestURI().toString().startsWith("/api/callback");
    }
    private boolean isPingPlusRequest(WebRequest request){
        return ((ServletWebRequest) request).getRequest().getRequestURI().toString().startsWith("/pingpp/callback");
    }

    private void setDebugMessage(ErrorResource error, Exception ex) {
        error.setDebugMessage(ExceptionUtils.getFullStackTrace(ex));
    }

}
