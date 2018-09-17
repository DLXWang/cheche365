package com.cheche365.cheche.operationcenter.service.tide

import com.cheche365.cheche.core.model.InternalUser
import com.cheche365.cheche.core.repository.tide.TidePlatformRepository
import com.cheche365.cheche.manage.common.service.BaseService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by yinJianBin on 2018/4/19.
 */
@Service
@Slf4j
class TidePlatformManageService extends BaseService {

    @Autowired
    TidePlatformRepository tidePlatformRepository

    def getByInternalUser(InternalUser internalUser) {
        tidePlatformRepository.findAllByOperator(internalUser)
    }

}
