package com.cheche365.cheche.marketing.service.activity

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.Marketing
import com.cheche365.cheche.core.model.MarketingSuccess
import com.cheche365.cheche.core.model.MoMarketingDetail
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.mongodb.repository.MoMarketingDetailRepository
import com.cheche365.cheche.marketing.service.MarketingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by taichangwei on 2018/5/16.
 */
@Service
class Service201804015 extends MarketingService{

    @Autowired
    MoMarketingDetailRepository moMarketingDetailRepository

    @Override
    Object attend(Marketing marketing, User user, Channel channel, Map<String, Object> payload) {
        String mobile =  payload.mobile ?: user?.mobile
        def ms = toMS(marketing.getAmount() as Double ,marketing, mobile, channel)
        ms.licensePlateNo = payload.licensePlateNo
        ms.owner = payload.owner
        ms.area = payload.area ? Area.Enum.getValueByCode(payload.area as Long) : null
        ms.identity = payload.identity
        ms.detailTableName = 'moMarketingDetail'
        ms.detail = payload.carId
        MarketingSuccess afterSave = marketingSuccessRepository.save(ms)
        sendSimpleMessage marketing, channel, mobile
        doAfterAttend afterSave, user, payload
    }

    @Override
    Map<String, Object> isAttend(String code, User user, Map<String, String> params) {
        MoMarketingDetail moMarketingDetail = moMarketingDetailRepository.findByMarketingCode(code)
        moMarketingDetail?.message?.each { _, info ->
            !info.titleImg ?: (info.titleImg = (getAbsoluteUrl(info.titleImg)))
            !info.carImg ?: (info.carImg = (getAbsoluteUrl(info.carImg)))
            info.list.each{ carItem ->
                !carItem.carImg ?: (carItem.carImg = getAbsoluteUrl(carItem.carImg))
            }
        }
        moMarketingDetail?.message
    }

    private getAbsoluteUrl(String path){
        resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getMarketingPath()), path)
    }
}
