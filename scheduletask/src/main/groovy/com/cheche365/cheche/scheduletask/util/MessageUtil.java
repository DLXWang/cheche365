package com.cheche365.cheche.scheduletask.util;

import com.cheche365.cheche.scheduletask.constants.ExceptionConstants;
import com.cheche365.cheche.scheduletask.exception.TaskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by sunhuazhong on 2015/5/29.
 */
public class MessageUtil {

    private static Logger logger = LoggerFactory.getLogger(MessageUtil.class);

    /**
     * 消息内容
     * @param templateFile
     * @param params
     * @return
     */
    public static String getContentByTemplate(String templateFile, Map params) {
        String content;
        try {
            content = VelocityUtil.getInstance().parseVelocityTemplate(templateFile, params);
        } catch (Exception e) {
            logger.error("parse message template error.",e);
            throw new TaskException(ExceptionConstants.EXCEPTION_PARSE_TEMPLATE, ExceptionConstants.EXCEPTION_PARSE_TEMPLATE_MESSAGE);
        }
        return content;
    }
}
