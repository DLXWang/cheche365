package com.cheche365.cheche.core.service

import com.cheche365.cheche.common.util.ServletUtils
import com.cheche365.cheche.core.model.InternalUser
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.model.MoPlatformAccessLog
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.mongodb.repository.MoPlatformAccessLogRepository
import net.sf.json.util.JSONUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

/**
 * Created by wenling on 2017/8/18.
 */
@Service
class DoubleDBService {

    Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MoApplicationLogRepository applicationLogMongoRepository;
    @Autowired
    private MoPlatformAccessLogRepository platformAccessLogMongoRepository;

    @Transactional
    public MoApplicationLog saveApplicationLog(MoApplicationLog log) {
        String message = String.valueOf(log.getLogMessage())
        try {
            if(JSONUtils.mayBeJSON(message)){
                log.setLogMessage(new net.sf.json.groovy.JsonSlurper().parseText(message))
            }
        } catch (Exception e){
            //忽略
        }

        return applicationLogMongoRepository.save(log)
    }


    @Transactional
    public void savePlatformAccessLog(HttpServletRequest request, InternalUser user, String platform){
        try{
            MoPlatformAccessLog platformAccessLog=new MoPlatformAccessLog();
            platformAccessLog.setIp(ServletUtils.getIP(request));
            platformAccessLog.setParam(ServletUtils.getParameters(request));
            platformAccessLog.setRequestType(request.getMethod());
            platformAccessLog.setInternalUser(user?.id);
            platformAccessLog.setUrl(request.getRequestURL().toString());
            platformAccessLog.setRequestTime(new Date());
            platformAccessLog.setPlatform(platform);
            platformAccessLogMongoRepository.save(platformAccessLog);
        }catch (Exception e){
            logger.debug("保存平台操作日志异常,请求url={}",request.getRequestURL(),e);
        }
    }
}
