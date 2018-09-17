package com.cheche365.cheche.marketing.service.activity

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.AccessDetailRepository
import com.cheche365.cheche.core.repository.BusinessActivityRepository
import com.cheche365.cheche.core.service.DoubleDBService
import com.cheche365.cheche.core.util.ValidationUtil
import com.cheche365.cheche.externalapi.api.lifeinsurace.InsuranceAPI
import com.cheche365.cheche.marketing.service.MarketingService
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Service201710006 extends MarketingService {

    private static final String SUCCEEDED = 'SUCCEEDED'

    @Autowired
    private AccessDetailRepository accessDetailRepo

    @Autowired
    private BusinessActivityRepository businessActivityRepo

    @Autowired
    private DoubleDBService doubleDBService

    @Autowired
    InsuranceAPI insuranceAPI

    @Override
    protected String activityName() {
        return "免费领意外险活动"
    }

    @Override
    void preCheck(Marketing marketing, String mobile, Channel clientType) {
        if (marketing == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "营销活动不存在");
        }
        if (new Date().before(marketing.getBeginDate())) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "活动尚未开始");
        }
        if (new Date().after(marketing.getEndDate())) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "活动已结束");
        }
        if(!ValidationUtil.validMobile(mobile)){
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "手机号格式校验失败");
        }
    }

    Object attend(Marketing marketing, User user, Channel channel, Map<String, Object> payload) {


        String mobile = payload.mobile
        MarketingSuccess ms

        List<MarketingSuccess> marketingSuccesses = marketingSuccessRepository.findByMobileAndMarketingId(mobile,marketing.id)
        if(marketingSuccesses.isEmpty()){
            ms = toMS(marketing.getAmount() as Double, marketing, mobile, channel)
            ms.licensePlateNo = payload.licensePlateNo
            ms.owner = payload.policyHolderName
            ms.identity = payload.policyHolderIdCard
            marketingSuccessRepository.save(ms)

            if(payload.uid){
                savePartnerUserExtend(payload,ms,channel)
            }

        }else {
            ms = marketingSuccesses.get(0)
        }


        String response = insuranceAPI.call(payload)

        saveLog(payload,response,ms.id)

        Map<String,String> result = new JsonSlurper().parseText(response)

        def resultMessage
        if(SUCCEEDED == result.status){
            resultMessage = result.detailMessage.find {SUCCEEDED == it.status}.with {
                "恭喜您，已成功领取"+it.productName
            }
        }else{
            resultMessage = result.message.contains('渠道量已满') ? '意外险领取成功，稍后为您投保。' : result.message
        }

        [status: SUCCEEDED == result.status, message: resultMessage]
    }

    private void saveLog(Map<String, Object> payload,String resultMessage,Long objectId){
        payload.put("resultMessage",resultMessage)
        String jsonStr = JsonOutput.toJson(payload)
        MoApplicationLog log = new MoApplicationLog()
        log.setCreateTime(Calendar.getInstance().getTime())
        log.setLogMessage(jsonStr)
        log.setObjId(String.valueOf(objectId))
        log.setLogType(LogType.Enum.MARKETING_FREE_INSURANCE_52)
        doubleDBService.saveApplicationLog(log)
    }

}
