package com.cheche365.cheche.operationcenter.service.tide

import com.cheche365.cheche.core.model.LogType
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.service.InternalUserService
import com.cheche365.cheche.manage.common.model.PublicQuery
import com.cheche365.cheche.manage.common.service.BaseService
import com.cheche365.cheche.operationcenter.web.model.tide.LogViewModel
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
@Slf4j
class TideLogService extends BaseService {
    @Autowired
    private MoApplicationLogRepository logRepository
    @Autowired
    private InternalUserService internalUserService

    def getLogByPage(PublicQuery query, sourceTable) {
        Pageable pageable = buildPageable(query.currentPage ?: 1, query.pageSize ?: 1000, Sort.Direction.DESC, "createTime")
        logRepository.findByLogTypeAndObjTableAndObjIdOrderByCreateTimeDesc(LogType.Enum.TIDE_58.id, sourceTable, query.keyword, pageable)
    }

    def tranformLogViewModel(MoApplicationLog log) {
        new LogViewModel(
                createTime: log.createTime,
                name: internalUserService.findByID(log.opeartor)?.name,
                mess: log.instanceNo
        )
    }
}
