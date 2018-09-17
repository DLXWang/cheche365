package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.constants.WebConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * 资源服务缺省实现
 */
@Service
class ResourceService implements IResourceService {


    @Autowired
    private ResourceProperties properties

    @Override
    String getResourceAbsolutePath(String path) {
        "${properties.rootPath}/$path"
    }

    @Override
    String getResourceUrl(String path) {
        path.contains(properties.prefix) ?
            path.substring(path.indexOf(properties.prefix)) :
            path
    }

    @Override
    String absoluteUrl(String subPath, String fileName) {
         WebConstants.getDomainURL() + getResourceUrl(subPath + fileName);
    }

    @Override
    String absoluteUrl(String subPath) {
         WebConstants.getDomainURL() + subPath;
    }

    @Override
    ResourceProperties getProperties() {
        properties
    }

}
