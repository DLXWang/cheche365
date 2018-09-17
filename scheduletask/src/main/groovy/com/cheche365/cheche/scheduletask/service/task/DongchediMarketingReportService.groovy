package com.cheche365.cheche.scheduletask.service.task

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.MarketingSuccess
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.model.PartnerUser
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.repository.MarketingSuccessRepository
import com.cheche365.cheche.core.repository.PartnerUserRepository
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.scheduletask.model.DongchediMarketingReportModel
import org.apache.commons.collections.CollectionUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by zhangtc on 2018/1/29.
 */
@Service
class DongchediMarketingReportService {

    def logger = LoggerFactory.getLogger(DongchediMarketingReportService.class)

    @Autowired
    MoApplicationLogRepository moApplicationLogRepository

    @Autowired
    MarketingSuccessRepository marketingSuccessRepository

    @Autowired
    PartnerUserRepository partnerUserRepository


    public List<DongchediMarketingReportModel> getList() {
        List<DongchediMarketingReportModel> resultList = new ArrayList<>()
        def yesterdayStart = DateUtils.getCustomDate(new Date(), -1, 00, 00, 00)
        def yesterdayEnd = DateUtils.getCustomDate(new Date(), -1, 23, 59, 59)

        List<MarketingSuccess> list = marketingSuccessRepository.findByMarketingIdAndSourceChannel('96', 'PARTNER_DONGCHEDI', yesterdayStart, yesterdayEnd)
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach() { marketingSucess ->
                PartnerUser partnerUser = partnerUserRepository.findFirstByExtendObject('marketing_success', marketingSucess.getId())
                Map dongchediMessage = getDongchediMessage(String.valueOf(marketingSucess.getId()))
                if(partnerUser!=null && dongchediMessage !=null){
                    resultList.add(DongchediMarketingReportModel.create(marketingSucess,partnerUser,dongchediMessage))
                }
            }
        }
        resultList
    }

    Map getDongchediMessage(String objId) {
        String logMessage = '{}'
        List<MoApplicationLog> logList = moApplicationLogRepository.findByObjIdAndLogType(objId, 52L)
        if (CollectionUtils.isNotEmpty(logList)) {
            logMessage = logList.get(0).getLogMessage()["resultMessage"]
        }

        return CacheUtil.doJacksonDeserialize(logMessage.replaceAll('\'','"'), Map.class)
    }


}
