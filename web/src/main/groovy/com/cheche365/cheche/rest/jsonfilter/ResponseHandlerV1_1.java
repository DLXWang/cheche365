package com.cheche365.cheche.rest.jsonfilter;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.exception.KnownReasonException;
import com.cheche365.cheche.core.exception.NonFatalBusinessException;
import com.cheche365.cheche.core.model.LogType;
import com.cheche365.cheche.core.model.MoApplicationLog;
import com.cheche365.cheche.core.service.DoubleDBService;
import com.cheche365.cheche.rest.exception.ErrorResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import org.springframework.http.HttpStatus;

import java.util.Calendar;


/**
 * Created by zhengwei on 7/13/15.
 */
public class ResponseHandlerV1_1 implements ResponseHandler{


    @Override
    public Object getBodyData(Object rawObj) {

        Integer code;
        String message = null;
        String debugMessage = null;
        Object data = null;

        if (rawObj instanceof ErrorResource) {
            ErrorResource error = (ErrorResource) rawObj;
            code = Integer.parseInt(error.getCode());
            message = error.getMessage();
            data = error.getData();
            debugMessage = saveDebugMessage(error);
        }  else if (rawObj instanceof NonFatalBusinessException) {
            NonFatalBusinessException nonFatalBusinessException = (NonFatalBusinessException) rawObj;
            code = nonFatalBusinessException.getCode().getCodeValue();
            message = nonFatalBusinessException.getMessage();
            data = nonFatalBusinessException.getErrorObject();
        } else if (rawObj instanceof KnownReasonException) {
            KnownReasonException knownReasonException = (KnownReasonException) rawObj;
            code = BusinessException.Code.COMMON_KNOWN_REASON_ERROR.getCodeValue();
            message = knownReasonException.getMessage();
        } else {
            code = HttpStatus.OK.value();
            Object entity;

            if(rawObj instanceof RestResponseEnvelope){
                entity = ((RestResponseEnvelope) rawObj).getEntity();
                debugMessage = ((RestResponseEnvelope) rawObj).getDebugMessage();
                message = ((RestResponseEnvelope) rawObj).getMessage();
            } else {
                entity = rawObj;
            }
            data = entity;
        }
        return new ResponseResultEntity(code, message, debugMessage, data);
    }
    private String saveDebugMessage(ErrorResource errorResource){
        MoApplicationLog moApplicationLog= new MoApplicationLog();
        moApplicationLog.setLogLevel(4);
        moApplicationLog.setLogType(LogType.Enum.EXCEPTION_STACK_51);
        moApplicationLog.setCreateTime(Calendar.getInstance().getTime());
        moApplicationLog.setLogMessage(errorResource.getDebugMessage());
        DoubleDBService doubleDBService= ApplicationContextHolder.getApplicationContext().getBean(DoubleDBService.class);
        return doubleDBService.saveApplicationLog(moApplicationLog).getId();
    }
}
