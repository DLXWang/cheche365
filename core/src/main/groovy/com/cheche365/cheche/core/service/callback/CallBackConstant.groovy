package com.cheche365.cheche.core.service.callback

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.util.ProfileProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CallBackConstant {
    private static Logger logger = LoggerFactory.getLogger(CallBackConstant.class);

    public static String CALL_BACK_URL;

    static {
        ProfileProperties properties = getProperties("/properties/web.properties");

        CALL_BACK_URL = properties.getProperty("callback.url");

    }

    static ProfileProperties getProperties(String path) {
        Properties properties = new Properties();
        try {
            properties.load(CallBackConstant.class.getResourceAsStream(path));
        } catch (IOException ex) {
            logger.error("load web properties files error", ex);
        }
        return new ProfileProperties(properties);
    }

    public static String getCallBackUrl(){
        return WebConstants.getSchemaURL()+CALL_BACK_URL;
    }
}
