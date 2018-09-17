package com.cheche365.cheche.core.service

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 资源属性
 */
@ConfigurationProperties(prefix = "cheche365.res")
class ResourceProperties {

    String rootPath
    String prefix
    String insuranceCompanyLogoPath
    String insuranceProductCompanyLogoPath
    String insuranceProductTypeLogoPath
    String userHeadIconPath
    String autoshopPicturePath
    String autoshopLogoPath
    String paymentWechatQrPath
    String autoBrandLogoPath
    String autoFamilyLogoPath
    String wechatQRCodePath
    String areaPath
    String didiPath
    String iosPath
    String pinganPath
    String partnerPath
    Boolean apiCountEnabled
    String androidInstallPackage
    String android
    String doubleonePath
    String freeSingle
    String insurancePath
    String marketingUpload
    String inviteQRCodePath
    String channelPath
    String bannerPath;
    String orderImagePath
    String orderPayQRCodePath
    String bankLogoPath
    String bankBgPath
    String marketingPath
    String templatePath
    String offlineInsurance
    String walletRemitReport
    String baiduInsurPath
    String tideContractPath
    String agentInviteQrCodePath
}
