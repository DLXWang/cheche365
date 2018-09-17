package com.cheche365.cheche.partner.service.index

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.model.Channel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * 规则：
 * 1.首先根据api_partner查找特定的用户关联处理类
 * 2.未找到使用通用的用户关联处理类
 */
@Service
class PartnerServiceFactory {

    @Autowired
    private List<PartnerService> partnerServices

    PartnerService getPartnerService(ApiPartner partner) {
        if (Channel.findByApiPartner(partner)?.disable()) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "第三方合作渠道[" + partner.getCode() + "]已下线")
        }

        PartnerService service = partnerServices.find { partner == it.apiPartner() }

        service ? service : partnerServices.find { it.class.simpleName.contains(PartnerService.class.simpleName) }
    }
}
