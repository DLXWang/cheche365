package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.Channel

/**
 * Created by liheng on 2018/5/7 0007.
 */
interface IHttpServletRequestService {

    def getRequest()

    Channel getChannel()

    String getVersion()
}
