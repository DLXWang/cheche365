package com.cheche365.cheche.core.service

/**
 * 资源服务接口
 */
interface IResourceService {

    /**
     *
     * @param relativePath
     * @return
     */
    String getResourceAbsolutePath(String relativePath)

    String getResourceUrl(String path)

    String absoluteUrl(String subPath, String fileName)

    String absoluteUrl(String subPath)

    ResourceProperties getProperties()

}
