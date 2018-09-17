package com.cheche365.cheche.core.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zhaozhong on 2015/9/1.
 */

@Service
public class DisplayMessageService {

    @Autowired
    private IResourceService resourceService;

    public static final String RESOURCE_VERSION = "1";


    public String genPicUrl(String imageName, String version) {
        if (StringUtils.isBlank(imageName)) {
            imageName = resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getIosPath()), imageName) + "?version=" + (StringUtils.isBlank(version) ? RESOURCE_VERSION : version);
        }
        return imageName;
    }

    public String genLinkUrl(String url) {
        return resourceService.absoluteUrl("", url);
    }

}
