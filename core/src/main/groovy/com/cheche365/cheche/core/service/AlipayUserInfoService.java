package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.AlipayUserInfo;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.repository.AlipayUserInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by Jason on 04/28/2016.
 */
@Service
public class AlipayUserInfoService {

    @Autowired
    private AlipayUserInfoRepository alipayUserInfoRepository;

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Transactional
    public void updateFollowFlag(String openId, boolean follow) {
        AlipayUserInfo alipayUserInfo = alipayUserInfoRepository.findByOpenid(openId);
        if (null != alipayUserInfo) {
            updateFollowFlag(alipayUserInfo, follow);
        } else {
            logger.debug("can't find alipayUserInfo, openId={}", openId);
        }
    }

    private void updateFollowFlag(AlipayUserInfo alipayUserInfo, boolean follow) {
        alipayUserInfo.setFollow(follow);
        if (follow) {
            alipayUserInfo.setFollowTime(new Date());
        } else {
            alipayUserInfo.setUnFollowTime(new Date());
        }
        alipayUserInfoRepository.save(alipayUserInfo);
    }


}
