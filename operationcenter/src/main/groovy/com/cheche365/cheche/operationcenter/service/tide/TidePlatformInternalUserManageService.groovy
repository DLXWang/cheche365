package com.cheche365.cheche.operationcenter.service.tide

import com.cheche365.cheche.core.repository.tide.TidePlatformInternalUserRepository
import com.cheche365.cheche.manage.common.service.BaseService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
/**
 * Created by yinJianBin on 2018/4/19.
 */
@Service
@Slf4j
class TidePlatformInternalUserManageService extends BaseService {

    @Autowired
    TidePlatformInternalUserRepository tidePlatformInternalUserRepository


    def getByInternalUserId(internalUserId) {
        tidePlatformInternalUserRepository.findAllByInternalUserId(internalUserId)
    }


}
