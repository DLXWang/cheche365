package com.cheche365.cheche.wechat;

import com.cheche365.cheche.core.WechatConstant;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.wechat.message.json.CustomerMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by liqiang on 7/10/15.
 */

@Component
public class WelcomeMessageHolder {
    private Logger logger = LoggerFactory.getLogger(WelcomeMessageHolder.class);
    private HashMap<String, List<CustomerMessage.Article>> welcomeMessageMap;//key scene id, value: welcome message

    public synchronized void init(){
        welcomeMessageMap = new HashMap<>();

        Properties properties = new Properties();
        try {
            URL resource = WechatConstant.class.getResource("/welcomeMessage.properties");
            properties.load(resource.openStream());
        } catch (IOException e) {
            logger.error("load wechat welcome message configuration file failed,",e);
            return;
        }

        Set<String> sceneIDs = properties.keySet()
            .stream()
            .map(s -> {
                String key = (String) s;
                return key.substring(0, key.indexOf("."));
            })
            .collect(Collectors.toSet());

        sceneIDs.forEach(key -> {
            logger.debug("try to load welcome message for :" + key);
            welcomeMessageMap.put(key, convertToWelcomeMessage(key, properties));
        });
    }

    private List<CustomerMessage.Article> convertToWelcomeMessage(String key, Properties properties) {
        List<CustomerMessage.Article> articles = new ArrayList<>();
        int length = Integer.parseInt((String)properties.get(key + ".wl.length"));
        String prefix;
        for (int i = 1; i <= length; i++){
            prefix = key + ".wl." + i + ".";
            CustomerMessage.Article article = new CustomerMessage.Article();
            article.setTitle(getProperty(properties, prefix + "title"));
            article.setPicurl(WebConstants.getDomainURL() + getProperty(properties, prefix + "picurl"));
            article.setUrl(getProperty(properties, prefix + "url"));
            article.setDescription(getProperty(properties, prefix + "description"));
            articles.add(article);

        }
        return articles;
    }


    //TODO fix encoding issue, should use native2ascii tool
    private String getProperty(Properties properties, String key) {
        String value =  properties.getProperty(key);
        if (StringUtils.isBlank(value)){
            return value;
        }
        try {
            return new String(value.getBytes("ISO8859-1"),"UTF8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    public List<CustomerMessage.Article> getWelcomeMessage(String sceneID){
        if(welcomeMessageMap==null){
            init();
        }
        if (StringUtils.isNotBlank(sceneID)){
            if (welcomeMessageMap.containsKey(sceneID.trim())){
                return welcomeMessageMap.get(sceneID.trim());
            }
        }
        logger.debug("will try to get default welcome message");
        return welcomeMessageMap.get("default");
    }

}
