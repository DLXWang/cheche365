package com.cheche365.cheche.marketing.util

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.service.IResourceService
import com.cheche365.cheche.core.service.ResourceProperties
import com.cheche365.cheche.core.util.RuntimeUtil
import com.cheche365.cheche.core.model.WechatUserChannel
import com.cheche365.cheche.core.model.WechatUserInfo
import com.cheche365.cheche.core.repository.WechatUserChannelRepository

/**
 * Created by zhengwei on 3/7/17.
 */


class WechatUtil {


    def static findByUser(User user){
        RuntimeUtil.devEnv ?
            new  WechatUserChannel(wechatUserInfo: new WechatUserInfo(nickname: '33'))  :
            ApplicationContextHolder.getApplicationContext().getBean(WechatUserChannelRepository.class).findByUser(user)
    }

    def static resourceService(){
        RuntimeUtil.devEnv ?
            new IResourceService(){

                @Override
                String getResourceAbsolutePath(String relativePath) {
                    return null
                }

                @Override
                String getResourceUrl(String path) {
                    return null
                }

                @Override
                String absoluteUrl(String subPath, String fileName) {
                    return "http://www.cheche365.com/v1.4/companies"
                }

                @Override
                String absoluteUrl(String subPath) {
                    return null
                }

                @Override
                ResourceProperties getProperties() {
                    return null
                }
            } :
            ApplicationContextHolder.getApplicationContext().getBean(IResourceService.class)
    }
}
