package com.cheche365.cheche.parser.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.AopUtils._INSURANCE_INFO_PHASE_CODE
import static com.cheche365.cheche.common.util.AopUtils._VEHICLE_LICENSE_COMMON_CODE
import static com.cheche365.cheche.parser.util.AopUtils.interceptConcurrentInsuranceInfoExec
import static com.cheche365.cheche.parser.util.AopUtils.interceptIndividualInsuranceInfoExec

@Component
@Aspect
class ConcurrentServiceAspect {

    /**
     * 切入并发获取保险信息服务，记录公共统计信息及vehicleLicense
     * @param pjp
     * @return vehicleLicense
     */
    @Around('execution(* com.cheche365.cheche.parser.service.ConcurrentInsuranceInfoService.getInsuranceInfo(..))')
    static concurrentInsuranceInfo(ProceedingJoinPoint pjp) {
        interceptConcurrentInsuranceInfoExec pjp, _VEHICLE_LICENSE_COMMON_CODE
    }

}
