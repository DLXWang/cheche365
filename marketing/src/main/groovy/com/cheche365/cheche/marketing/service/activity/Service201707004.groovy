package com.cheche365.cheche.marketing.service.activity

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.AccessDetailRepository
import com.cheche365.cheche.core.repository.BusinessActivityRepository
import com.cheche365.cheche.marketing.service.MarketingService
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

import javax.servlet.http.HttpServletRequest

/**
 * Created by wenling on 2017/7/28.
 */
@Service
class Service201707004 extends MarketingService {

    private static final String SOURCE_COOKIE = 'CC_SEO_SOURCE'
    private static final String LANDING_PAGE = 'CC_LANDING_PAGE'

    @Autowired
    private AccessDetailRepository accessDetailRepo

    @Autowired
    private BusinessActivityRepository businessActivityRepo

    @Override
    protected String activityName() {
        return "pc首页默认活动";
    }

    Object attend(Marketing marketing, User user, Channel channel, Map<String, Object> payload) {
        String mobile = loginUnRequired(marketing) ? payload.mobile : user.mobile
        String licensePlateNo = payload.licensePlateNo;
        validLicensePlateNo(licensePlateNo)
        def ms = toMS(marketing.getAmount() as Double, marketing, mobile, channel)
        ms.licensePlateNo = licensePlateNo
        MarketingSuccess afterSave = marketingSuccessRepository.save(ms);

        saveAccessDetail(afterSave)

        return super.doAfterAttend(afterSave, user, payload);
    }

    def saveAccessDetail(MarketingSuccess ms) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()

        String landingPage = request.getCookies().find { LANDING_PAGE == it.name }?.value
        String sourceCookie = request.getCookies().find { SOURCE_COOKIE == it.name }?.value
        String source = AccessDetail.SOURCE_MAP.values().find { sourceCookie?.contains(it.url) }?.id

        if (!source || !landingPage || landingPage && businessActivityRepo.findFirstByLandingPage(landingPage)) {
            return
        }

        AccessDetail accessDetail = new AccessDetail(
            source: source,
            referer: landingPage ? landingPage : request.getHeader("Referer"),
            mobile: ms.mobile,
            licensePlateNo: ms.licensePlateNo,
            createTime: new Date()
        )
        accessDetailRepo.save(accessDetail)
    }

    void validLicensePlateNo(String licensePlateNo) {
        if (StringUtils.isBlank(licensePlateNo)) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "请输入车牌号!");
        }
    }
}
