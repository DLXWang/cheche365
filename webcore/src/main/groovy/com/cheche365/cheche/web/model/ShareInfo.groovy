package com.cheche365.cheche.web.model

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.service.IResourceService
import com.cheche365.cheche.web.service.system.ChannelAgentInviteURL
import groovy.transform.TupleConstructor
import sun.misc.BASE64Encoder

/**
 * Author:   shanxf
 * Date:     2018/6/20 13:52
 *
 * 标准分享对象
 */
@TupleConstructor
class ShareInfo {

    String title
    String desc
    String link
    String imgUrl
    /**
     * img  app端需要base64编码
     */
    String img

}
