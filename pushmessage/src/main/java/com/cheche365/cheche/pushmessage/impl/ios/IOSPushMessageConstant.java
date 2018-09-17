package com.cheche365.cheche.pushmessage.impl.ios;

import com.cheche365.cheche.core.util.ProfileProperties;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by liqiang on 7/20/15.
 */
public class IOSPushMessageConstant {


    private static final java.lang.String PRODUCTION_CONF_PASSWORD = "wechat.conf.password";

    private static final Logger logger = LoggerFactory.getLogger(IOSPushMessageConstant.class);

    public static final String CERTIFICATE_FILE;

    public static final String CERTIFICATE_PASSWORD;

    static {

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(System.getProperty(PRODUCTION_CONF_PASSWORD,"password")); // could be got from web, env variable...

        Properties properties = new EncryptableProperties(encryptor);
        try {
            properties.load(IOSPushMessageConstant.class.getResourceAsStream("/IOSPushMessageConfig.properties"));
        } catch (IOException e) {
            logger.error("load ios push message configuration file failed,",e);
        }

        ProfileProperties profileProperties = new ProfileProperties(properties);
        CERTIFICATE_FILE =  profileProperties.getProperty("ios.certificate.file");
        CERTIFICATE_PASSWORD = profileProperties.getProperty("ios.certificate.password");

    }

}
