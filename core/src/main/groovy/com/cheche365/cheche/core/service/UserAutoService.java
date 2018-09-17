package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.model.UserAuto;
import com.cheche365.cheche.core.repository.UserAutoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mahong on 2015/7/8.
 */
@Service
@Transactional
public class UserAutoService {

    private Logger logger = LoggerFactory.getLogger(UserAutoService.class);

    @Autowired
    private UserAutoRepository userAutoRepository;

    public UserAuto saveUserAuto(Auto auto, User user) {
        UserAuto userAuto = userAutoRepository.findFirstByUserAndAuto(user, auto);
        if (userAuto != null) {
            return userAuto;
        }
        logger.debug("保存用户车辆关联信息");
        userAuto = new UserAuto();
        userAuto.setUser(user);
        userAuto.setAuto(auto);
        return this.userAutoRepository.save(userAuto);
    }
    /**
     * 用户信息
     */
    public List<UserAuto> findByAuto(Auto auto){
        return userAutoRepository.findByAuto(auto);
    }
}
