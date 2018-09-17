package com.cheche365.cheche.scheduletask.util;

import com.cheche365.cheche.scheduletask.constants.ExceptionConstants;
import com.cheche365.cheche.scheduletask.exception.TaskException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

/**
 * Created by sunhuazhong on 2015/4/27.
 */
public class VelocityUtil {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(VelocityUtil.class);

    private static VelocityUtil instance = new VelocityUtil();

    private VelocityContext context;

    private VelocityUtil() {
        try {
            InputStream is = this.getClass().getResourceAsStream("/velocity/velocity.properties");
            Properties properties = new Properties();
            properties.load(is);
            Velocity.init(properties);
            context = new VelocityContext();
        } catch (IOException e) {
            logger.error("init velocity error", e);
            throw new TaskException(ExceptionConstants.EXCEPTION_INIT_VELOCITY, ExceptionConstants.EXCEPTION_INIT_VELOCITY_MESSAGE);
        }
    }

    /**
     * 返回VelocityParserUtil实例
     * @return
     */
    public static VelocityUtil getInstance() {
        return instance;
    }

    /**
     * 解析velocity模板
     * @param templateFile
     * @param model
     * @return String
     * @throws ParseErrorException
     * @throws MethodInvocationException
     * @throws ResourceNotFoundException
     */
    public synchronized String parseVelocityTemplate(String templateFile, Map model)
        throws ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        context.put("data", model);
        // get the Template
        Template template = Velocity.getTemplate(templateFile, "UTF-8");
        // now render the template into a Writer, here a StringWriter
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        // use the output in the body of your XML
        String returnString = writer.toString();
        return returnString;
    }
}
