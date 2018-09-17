package com.cheche365.cheche.core.service

/**
 * 通用配置服务接口声明
 */
interface IConfigService {

    String getProperty(String name)

    String getProperty(String name, String defaultValue)

    Map<String, String> getAllProperties(String namespace)

}
