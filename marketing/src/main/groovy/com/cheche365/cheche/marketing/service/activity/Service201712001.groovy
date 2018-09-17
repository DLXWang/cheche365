package com.cheche365.cheche.marketing.service.activity

import com.cheche365.cheche.core.model.MoAttendMarketingPartner
import com.cheche365.cheche.core.mongodb.repository.MoAttendMarketingPartnerRepository
import com.cheche365.cheche.marketing.service.MarketingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service



/**
 * Created by shanxf on 2017/12/4.
 */

@Service
class Service201712001 extends MarketingService {

    @Autowired
    private MoAttendMarketingPartnerRepository moAttendMarketingListRepository

    @Override
    List attends(code) {
        List<MoAttendMarketingPartner> moAttendMarketingLists = moAttendMarketingListRepository.findByMarketingCode(code)
        return moAttendMarketingLists
            .findAll { it -> compareDate(it.getDate()) }
            .sort {a,b -> SDF.parse(b.getDate()) <=> SDF.parse(a.getDate())}
    }


}
