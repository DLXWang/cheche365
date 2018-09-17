package com.cheche365.cheche.core.service.qrcode

import com.cheche365.cheche.common.util.QRCodeUtils
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.service.IResourceService
import groovy.util.logging.Slf4j
import org.apache.commons.lang.exception.ExceptionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 08/02/2018.
 */

@Service
@Slf4j
class QRCodeService {

    public static final String CHE_CHE_LOGO = "agentInvite/logo/cheche.jpg"
    @Autowired
    private IResourceService resourceService;

    String generateQRCode(String text, String imagePrefix) {
        def imagePath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getOrderPayQRCodePath())
        def imageName = imagePrefix + '_' + System.currentTimeMillis() + '.png'

        generateQRCode(text, imagePath, imageName)
    }

    Map agentInviteQRCode(String text, String imageName, Channel channel) {

        String absUrl = this.generateQRCode(
            text,
            resourceService.getResourceAbsolutePath(resourceService.getProperties().getAgentInviteQrCodePath()),
            imageName,
            Channel.agentLevelChannels().contains(channel) ? resourceService.getResourceAbsolutePath(CHE_CHE_LOGO) : null
        )

        [
            absUrl: absUrl,
            relUrl: resourceService.getProperties().getAgentInviteQrCodePath() + imageName
        ]

    }

    String generateQRCode(String text, String imagePath, String imageName, logoPath = null) {

        try {
            QRCodeUtils.generateQRCode2File(text, imagePath, imageName, logoPath)
            resourceService.absoluteUrl(imagePath, imageName)
        } catch (Exception e) {
            log.error("生成支付二维码失败, text:{}, imagePath:{}, imageName:{}, Exception:{}", text, imagePath, imageName, ExceptionUtils.getStackTrace(e))
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, '生成支付二维码失败')
        }
    }
}
