package com.cheche365.cheche.manage.common.service.reverse.step

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.AgentRepository
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.repository.UserRepository
import com.cheche365.cheche.core.service.AutoService
import com.cheche365.cheche.manage.common.service.reverse.OrderReverse
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by yellow on 2017/11/6.
 */
@Service
@Slf4j
class GenerateAuto implements TPlaceInsuranceStep {

    @Transactional
    @Override
    Object run(Object context) {
        log.debug("------生成车辆------")
        AutoService autoService = context.autoService
        AgentRepository agentRepository = context.agentRepository
        AreaRepository areaRepository = context.areaRepository
        UserRepository userRepository = context.userRepository
        OrderReverse model = context.model
        User user
        if (model.getRecommender() != null) {
            user = agentRepository.findOne(model.getRecommender()).getUser()
        } else {
            user = userRepository.findByMobile(model.getMobile())
        }
        Area area = areaRepository.findOne(model.getArea())
        def validArea = StringUtils.isNotEmpty(model.licensePlateNo)
        context.auto = autoService.saveOrMerge(createAuto(model, area), user, validArea, new StringBuilder(), validArea)
        getContinueFSRV true
    }

    def createAuto(OrderReverse model, Area area) {
        Auto auto = new Auto()
        AutoType autoType = new AutoType()
        autoType.setCode(model.getBrand())
        auto.setAutoType(autoType)
        String vinNo = model.getVinNo()
        auto.setEngineNo(model.getEngineNo())
        auto.setVinNo(model.getVinNo())
        auto.setIdentity(model.getIdentity())
        auto.setIdentityType(model.getIdentityType())
        String licensePlateNo = model.getLicensePlateNo()
        if (StringUtils.isEmpty(licensePlateNo) && model.getIsNewCar()) {
            licensePlateNo = new StringBuffer(area.getShortCode()).append(vinNo.substring(vinNo.length() - 5, vinNo.length())).append("#").toString()
        }
        auto.setLicensePlateNo(licensePlateNo)
        auto.setOwner(model.getOwner())
        auto.setEnrollDate(DateUtils.getDate(model.getEnrollDate(), DateUtils.DATE_SHORTDATE_PATTERN))
        auto.setArea(area)
        auto
    }
}
