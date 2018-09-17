package com.cheche365.cheche.web.service.system

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.model.Channel
import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 04/04/2018.
 */

@Service
class QRURL extends SystemURL {

    String toClientPage(Long qrId, Channel channel) {

        String uuid = super.cacheUuid(qrId)

        return super.generate(
            [
                host: root(),
                path: WebConstants.getRootPath(channel),
                qs: [
                        nolink: true,
                        src: Channel.allPartners().contains(channel) ? channel.apiPartner.code : null
                ],
                fragment: 'quote/nlId/' + uuid
            ]
        )
    }


    Long cachedValue(String uuid) {
        super.cachedValue(uuid) as Long
    }

    @Override
    String cacheKeyPrefix() {
        ''
    }

    @Override
    String desc() {
        '报价页链接生成器'
    }
}
