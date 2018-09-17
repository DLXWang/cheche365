package com.cheche365.cheche.manage.common.service

import com.cheche365.cheche.core.model.LogType
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.service.DoubleDBService
import com.cheche365.cheche.manage.common.annotation.TideLogAnnotation
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import groovy.util.logging.Slf4j
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Aspect
@Component
@Slf4j
class TideLogAspectService {
    @Autowired
    private DoubleDBService mongoDBService
    @Autowired
    private InternalUserManageService internalUserManageService

    @AfterReturning(returning = "result", pointcut = "execution(* com.cheche365.cheche.operationcenter.service.tide..*.*(..)) && @annotation(logAnnotation)")
    void doAfterAdvice(JoinPoint joinPoint, TideLogAnnotation logAnnotation, Object result) {
        def ids
        def operatorId = internalUserManageService.currentInternalUser?.id
        if (result) {
            if (result instanceof Collection) {
                ids = result.collect {
                    if (it.metaClass.hasProperty(it, 'id')) {
                        return it.id
                    }
                }
            } else {
                if (result.metaClass.hasProperty(result, 'id')) {
                    ids = [result.id]
                }
            }

            ids.each { id ->
                saveLog(logAnnotation.description(), joinPoint.args[0]?.toString(), logAnnotation.table(), id, operatorId)
            }
        }
    }

    def saveLog(instanceNo, logMessage, objTable, objId, operatorId) {
        new MoApplicationLog(
                logType: LogType.Enum.TIDE_58,
                instanceNo: instanceNo,
                logMessage: logMessage,
                objTable: objTable,
                objId: objId,
                createTime: new Date(),
                opeartor: operatorId
        ).with {
            mongoDBService.saveApplicationLog(it)
        }
    }
}
