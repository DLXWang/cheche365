package com.cheche365.cheche.scheduletask.util;

import com.cheche365.cheche.common.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串参数工具类
 * Created by guoweifu on 2015/12/24.
 */
public class ParameterUtil {

    /**
     * 参数${parameter}的正则表达式
     */
    public static final Pattern pattern = Pattern.compile("\\$\\{((\\w*)\\.?(\\w*))}");


    /**
     * 替换Str中参数
     * @param paramMap
     * @param str
     * @return
     */
    public static String replaceParamForStr(Map<String, Object> paramMap, String str) {
        if(StringUtils.isNotEmpty(str)){
            //获取参数值
            List<String> parameterList = getParameter(str);
            if(!CollectionUtils.isEmpty(parameterList)){
                for(String parameter : parameterList){
                    String parameterStr = parameter.substring(parameter.indexOf("${")+2,parameter.indexOf("}"));
                    if(parameterStr.equals("currentDate")){
                        String currentDateStr = DateUtils.getCurrentDateString("yyyy年MM月dd日");
                        str = str.replace(parameter,currentDateStr);
                    }
                    if(paramMap !=null && paramMap.containsKey(parameterStr)){
                        String parameterValue = (String) paramMap.get(parameterStr);
                        str = str.replace(parameter,parameterValue);
                    }
                }
            }
        }
        return str;
    }

    /**
     * 获取字符串中参数 参数格式 ${parameter}
     * @param str
     * @return
     */
    private static List<String> getParameter(String str){
        Matcher matcher = pattern.matcher(str);
        List<String> parameterList = new ArrayList<>();
        while (matcher.find()) {
            String parameterCode = matcher.group(0);
            parameterList.add(parameterCode);
        }
        return parameterList;
    }

}
