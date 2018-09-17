package com.cheche365.cheche.rest.service

import com.cheche365.cheche.core.model.InsuranceInfo
import com.cheche365.cheche.core.repository.QuotePhotoRepository
import com.cheche365.cheche.core.repository.UserImgRepository
import com.cheche365.cheche.core.service.IAutoVehicleLicenseService
import com.cheche365.cheche.core.service.IOCRService
import com.cheche365.cheche.core.service.SupplementInfoService
import com.cheche365.cheche.core.service.image.QuotePhoneUploadService
import com.cheche365.cheche.core.service.image.UnifyImageFile
import com.cheche365.cheche.rest.service.vl.VehicleLicenseService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import static com.cheche365.cheche.core.model.VehicleLicense.createVLByQuotePhoto

/**
 * @Author shanxf
 * @Date 2017/12/14  19:28
 */
@Slf4j
@Service
class SimplifyQuoteSupportService extends QuotePhoneUploadService {

    @Autowired
    private VehicleLicenseService vehicleLicenseService

    SimplifyQuoteSupportService(UserImgRepository userImgRepository,
                                QuotePhotoRepository quotePhotoRepository,
                                @Qualifier("ccintService") IOCRService ocrService,
                                IAutoVehicleLicenseService autoVehicleLicenseService,
                                SupplementInfoService supplementInfoService) {
        super(userImgRepository, quotePhotoRepository, ocrService, autoVehicleLicenseService, supplementInfoService)
    }

    @Override
    boolean support(Map initParams) {
        log.debug('图片上传初始化参数initParams:{}', initParams)
        !initParams.orderNo && initParams.businessType == BUSINESS_TYPE_QUOTE_PHONE && initParams.apiVersion >= 'v1.6'
    }

    @Override
    Object responseBody(List<UnifyImageFile> files, Map initParams) {
        files.collect {
            if (initParams.scan) {
                def vl = createVLByQuotePhoto(it.persistObj)
                vehicleLicenseService.formatInsuranceInfo(new InsuranceInfo(vehicleLicense: vl), [owner: vl.owner, photeQuote: true])
            } else {
                [(it.type): ['absoluteFilePath': toAbsoluteUrl(it)]]
            }
        }.flatten()
    }

}
