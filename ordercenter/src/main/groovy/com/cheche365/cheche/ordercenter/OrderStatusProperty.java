package com.cheche365.cheche.ordercenter;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Created by wangfei on 2015/5/7.
 */
public class OrderStatusProperty {
    private static Logger logger = LoggerFactory.getLogger(OrderStatusProperty.class);
    private static Element node;

    /**
     * 初始化订单状态配置文件
     */
    public void initFile() {
        try {
            InputStream is = this.getClass().getResourceAsStream("/order/statusTransmission.xml");
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(is);
            node = getRootElement(document);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("init order status failed");
        }
    }

    /**
     * 获取指定节点配置值
     * @param names 节点数组
     * @return Element
     */
    public static Element getProperty(String... names) {
        Element element = null;
        if(node == null){
            return null;
        }
        for(int i=0; i<names.length; i++){
            if(i == 0){
                element = node.element(names[i]);
            }else{
                element = element.element(names[i]);
            }
        }
        return element;
    }

    /**
     * 获取根节点
     * @param doc Document
     * @return Element
     */
    public static Element getRootElement(Document doc) {
        if (doc == null) {
            return null;
        }
        return doc.getRootElement();
    }

}
