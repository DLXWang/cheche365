package com.cheche365.cheche.ordercenter.service;

import com.cheche365.cheche.core.model.LogType;
import com.cheche365.cheche.core.model.MoApplicationLog;
import com.cheche365.cheche.core.service.DoubleDBService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xu.yelong on 2016-04-15.
 * 用于目标bean的数据改变时记录原数据至日志
 */
public abstract class DataHistoryLogService<T, U> extends BaseService<T, U> {
    @Autowired
    private DoubleDBService doubleDBService;

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    protected Map logMap;

    private List propertiesList;

    private Object bean;

    protected void createLog(Object bean) {
        createLog(bean, "");
    }

    protected void createLog(Object bean, String... propertiesArray) {
        this.bean = bean;
        propertiesList=new ArrayList<>();
        for (String properties : propertiesArray) {
            propertiesList.add(properties);
        }
        propertiesList.add("id");
        saveLog();
    }

    private void saveLog() {
        if (!bean.getClass().isInstance(Map.class)) {
            convertBean(bean);
        }
        MoApplicationLog applicationLog = createApplicationLog();
        if (applicationLog != null) {
            doubleDBService.saveApplicationLog(applicationLog);
        }
    }

    private MoApplicationLog createApplicationLog() {
        if (logMap.size() == 0) {
            return null;
        }
        MoApplicationLog applicationLog = new MoApplicationLog();
        applicationLog.setCreateTime(new Date());
        applicationLog.setLogLevel(1);
        applicationLog.setLogMessage(CacheUtil.doJacksonSerialize(logMap));
        applicationLog.setLogType(LogType.Enum.INSURANCE_HISTORY_28);
        applicationLog.setObjId(logMap.get("objId").toString());
        applicationLog.setObjTable(logMap.get("objTable").toString());
        applicationLog.setOpeartor(orderCenterInternalUserManageService.getCurrentInternalUser().getId());
        return applicationLog;
    }

    private void convertBean(Object bean) {
        Class clazz = bean.getClass();
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        logMap=new HashMap<>();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class") &&(propertiesList.indexOf(propertyName) != -1 || propertyName.equals("id"))) {
                Method readMethod = descriptor.getReadMethod();
                Object result = null;
                try {
                    result = readMethod.invoke(bean, new Object[0]);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (result != null) {
                    logMap.put(propertyName, result);
                } else {
                    logMap.put(propertyName, "");
                }
            }
        }
        logMap.put("objId",logMap.get("id")!=null?logMap.get("id").toString():"");
        logMap.put("objTable",classToTableName(bean.getClass().getSimpleName()));
    }

    private String classToTableName(String className){
        Pattern p=Pattern.compile("[A-Z]");
        if(StringUtils.isEmpty(className)){
            return "";
        }
        StringBuilder builder=new StringBuilder(className);
        Matcher mc=p.matcher(className);
        int i=0;
        while(mc.find()){
            builder.replace(mc.start()+i, mc.end()+i, "_"+mc.group().toLowerCase());
            i++;
        }
        if('_' == builder.charAt(0)){
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }
}
