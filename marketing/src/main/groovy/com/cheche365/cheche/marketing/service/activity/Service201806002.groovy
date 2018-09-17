package com.cheche365.cheche.marketing.service.activity

import com.cheche365.cheche.core.model.MoMarketingDetail
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.mongodb.repository.MoMarketingDetailRepository
import com.cheche365.cheche.marketing.service.MarketingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by liushijie on 2018/6/25.
 */

@Service
class Service201806002 extends MarketingService{

    @Autowired
    MoMarketingDetailRepository marketingDetailRepository

    @Override
    Map<String, Object> isAttend(String code, User user, Map<String, String> params) {
        MoMarketingDetail marketingDetail =  marketingDetailRepository.findByMarketingCode(code)
        List resultList = marketingDetail.message as List

        resultList.each {
            it.logoUrl =  getAbsoluteUrl(it.logoUrl as String)
            it.products.each{
                it.logoUrl = getAbsoluteUrl(it.logoUrl as String)
                it.fileLink = getAbsoluteUrl(it.fileLink as String)
            }
            sortByOrder(it.products)
        }
        
        sortByOrder(resultList)

        [result:resultList]
    }


    private sortByOrder(List targetList){
        targetList.sort(new Comparator() {
            @Override
            int compare(Object o1, Object o2) {
                return o1.order - o2.order
            }
        })
    }


    private getAbsoluteUrl(String path){
        resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getMarketingPath()), path)
    }

}
