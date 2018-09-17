package com.cheche365.cheche.externalpayment.constants

import com.cheche365.cheche.core.util.ProfileProperties
import com.pingplusplus.Pingpp
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class PingPlusConstant {
    private static Logger logger = LoggerFactory.getLogger(PingPlusConstant.class);

    public static final String APP_ID;
    public static final String APP_KEY;
    public static final String PRIVATE_KEY;
    public static final String PEM_FILE_PATH

    static {
        ProfileProperties prop = getProperties("/pingplus.properties");
        APP_ID = prop.getProperty("app.id");
        APP_KEY = prop.getProperty("app.key");
        PRIVATE_KEY = prop.getProperty("private.key");
        PEM_FILE_PATH = prop.getProperty("pp.public.key.file.path")
    }

    static ProfileProperties getProperties(String path) {
        Properties properties = new Properties();
        try {
            properties.load(PingPlusConstant.class.getResourceAsStream(path));
        } catch (IOException ex) {
            logger.error("load ping plus properties files has error", ex);
        }
        return new ProfileProperties(properties);
    }
}
