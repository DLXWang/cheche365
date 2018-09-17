package com.cheche365.cheche.web.service.shareInfo

import com.cheche365.cheche.core.service.IResourceService
import com.cheche365.cheche.core.util.RuntimeUtil
import com.cheche365.cheche.web.model.ShareInfo
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sun.misc.BASE64Encoder

/**
 * Author:   shanxf
 * Date:     2018/6/21 10:10
 */
@Service
@Slf4j
abstract class ShareAbstract {

    @Autowired
    IResourceService resourceService

    abstract String title(String title)

    abstract String desc(String desc)

    abstract String imgAbsolutePath(String path)

    ShareInfo assembleShareInfo(title,desc,link,imgUrl){
        new ShareInfo().with {
            it.title  = title
            it.desc   = desc
            it.link   = link
            it.imgUrl = imgUrl
            it.img    = imgEncode(imgUrl)
            it
        }
    }

    String imgEncode(imgUrl){
        if(RuntimeUtil.isDevEnv()){
            return ''
        }
        new BASE64Encoder().encode(new URL(imgUrl).bytes)
    }
}
