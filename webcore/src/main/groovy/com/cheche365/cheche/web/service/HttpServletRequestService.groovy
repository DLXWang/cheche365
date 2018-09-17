package com.cheche365.cheche.web.service

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.service.IHttpServletRequestService
import org.springframework.stereotype.Service
import org.springframework.web.context.request.ServletRequestAttributes

import static com.cheche365.cheche.web.util.ClientTypeUtil.getChannel
import static org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes
import static com.cheche365.cheche.web.util.ClientTypeUtil.getVersion

/**
 * Created by liheng on 2018/5/4 0004.
 */
@Service
class HttpServletRequestService implements IHttpServletRequestService {

    @Override
    def getRequest() {
        ((ServletRequestAttributes) currentRequestAttributes()).request
    }

    @Override
    Channel getChannel() {
        getChannel getRequest()
    }

    @Override
    String getVersion(){
       getVersion getRequest()
    }
}
