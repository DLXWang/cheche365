package com.cheche365.cheche.web.service.shareInfo

import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.web.model.ShareInfo
import com.cheche365.cheche.web.service.system.SystemUrlGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sun.misc.BASE64Encoder

/**
 * Author:   shanxf
 * Date:     2018/6/25 17:51
 */
@Service
class QuoteRecordShareHandler extends ShareAbstract {

    @Autowired
    SystemUrlGenerator systemUrlGenerator

    String title(String title){}

    String desc(String desc){
        '报价均为各保险公司网销报价，最终价格以出单时保单价格为准'
    }

    Map shareInfo(QuoteRecord quoteRecord){
        String imgRelativePath = getImgPath(quoteRecord)
        [
            title : quoteRecord.insuranceCompany.name + '为您的爱车' + quoteRecord.auto.licensePlateNo + '的车险报价' + quoteRecord.getTotalPremium() + '元',
            desc  : desc(),
            link  : quoteRecord.id ? systemUrlGenerator.toQrUrl(quoteRecord.id, quoteRecord.channel) : '',
            imgUrl: imgAbsolutePath(imgRelativePath)
        ]
    }

    private String getImgPath(QuoteRecord quoteRecord) {
        def imgRelativePath = 'agent/chebaoyi.jpeg'
        if (quoteRecord?.channel?.apiPartner && ['kunlun','kunlunbz'].contains(quoteRecord.channel.apiPartner.code.toString())) {
            imgRelativePath = 'agent/kunlun.png'
        }
        imgRelativePath
    }

    String imgAbsolutePath(String path){
        resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getIosPath()),path)
    }
}
