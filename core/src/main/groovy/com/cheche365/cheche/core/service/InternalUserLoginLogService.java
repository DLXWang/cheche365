package com.cheche365.cheche.core.service;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.InternalUserLoginLog;
import com.cheche365.cheche.core.repository.InternalUserLoginLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by xu.yelong on 2016-05-16.
 */
@Service
public class InternalUserLoginLogService {
    private Logger logger = LoggerFactory.getLogger(InternalUserLoginLogService.class);

    @Autowired
    private InternalUserLoginLogRepository internalUserLoginLogRepository;

    public void saveLog(String ip, InternalUser user,String platform){
        logger.debug("internal user:{}, login ip:{}, login time：{}", user.getEmail(), ip, DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN));
        InternalUserLoginLog internalUserLoginLog =new InternalUserLoginLog();
        internalUserLoginLog.setIp(ip);
        internalUserLoginLog.setLoginTime(new Date());
        internalUserLoginLog.setInternalUser(user);
        internalUserLoginLog.setPlatform(platform);
        internalUserLoginLogRepository.save(internalUserLoginLog);
    }

}
