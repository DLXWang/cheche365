package com.cheche365.cheche.core.service.image

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.exception.handler.LackOfSupplementInfoHandler
import com.cheche365.cheche.core.model.AutoServiceType
import com.cheche365.cheche.core.model.Driver
import com.cheche365.cheche.core.model.QuoteEntrance
import com.cheche365.cheche.core.model.QuotePhoto
import com.cheche365.cheche.core.model.UserImg
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.core.repository.QuotePhotoRepository
import com.cheche365.cheche.core.repository.UserImgRepository
import com.cheche365.cheche.core.service.IAutoVehicleLicenseService
import com.cheche365.cheche.core.service.IOCRService
import com.cheche365.cheche.core.service.SupplementInfoService
import groovy.util.logging.Slf4j
import org.springframework.transaction.annotation.Transactional

import java.nio.file.Paths

import static com.cheche365.cheche.core.constants.WebConstants.CHANNEL_SERVICE_ITEMS
import static com.cheche365.cheche.core.exception.Constants.getFIELD_ORDER_TOC
import static com.cheche365.cheche.core.exception.Constants.getVL_CLIENT_VISIBLE_FIELD
import static com.cheche365.cheche.core.exception.handler.LackOfSupplementInfoHandler.formatFields

/**
 * Created by zhengwei on 4/1/17.
 */

@Slf4j
class QuotePhoneUploadService extends ImgUploadService {

    public static final Integer BUSINESS_TYPE_QUOTE_PHONE = 1
    private static final String DATE_LONGTIME24_PATTERN = "yyyyMMdd HHmmss";

    protected UserImgRepository userImgRepository
    protected QuotePhotoRepository quotePhotoRepository
    protected IOCRService ocrService
    protected IAutoVehicleLicenseService autoVehicleLicenseService
    protected SupplementInfoService supplementInfoService

    QuotePhoneUploadService(UserImgRepository userImgRepository,
                            QuotePhotoRepository quotePhotoRepository,
                            IOCRService ocrService,
                            IAutoVehicleLicenseService autoVehicleLicenseService,
                            SupplementInfoService supplementInfoService) {
        this.userImgRepository = userImgRepository
        this.quotePhotoRepository = quotePhotoRepository
        this.ocrService = ocrService
        this.autoVehicleLicenseService = autoVehicleLicenseService
        this.supplementInfoService = supplementInfoService
    }

    @Override
    void filePath(Map context, UnifyImageFile file) {

        def rootPath = Paths.get(resourceService.properties.rootPath)
        file.absolutePath = Paths.get(rootPath.toString(), resourceService.getProperties().getDidiPath(), context.user.mobile as String, DateUtils.getDateString(new Date(), DATE_LONGTIME24_PATTERN), file.type)
        file.relativePath = file.absolutePath.subpath(rootPath.nameCount, file.absolutePath.nameCount)
    }


    @Override
    def initPersistObj(List<UnifyImageFile> files, Map initParams) {
        def now = new Date()
        files.each { file ->
            file.persistObj =
                new QuotePhoto(
                    user: initParams.user,
                    createTime: now,
                    updateTime: now,
                    userImg: new UserImg(
                        user: initParams.user,
                        quoteEntrance: QuoteEntrance.Enum.QUOTE_1,
                        createTime: now,
                        active: true,
                        sourceChannel: initParams.channel
                    )
                )
        }
    }

    @Override
    void scan(List<UnifyImageFile> files, Map initParams) {
        def driver = files.find { it.type == Driver }
        log.debug('被扫描行驶证的绝对路径为:{}', driver.toURI().toString())
        VehicleLicense vehicleLicense = ocrService.getInformation driver.toURI().toString(), [channel: initParams.channel, (CHANNEL_SERVICE_ITEMS): autoVehicleLicenseService.getAutoVehicleLicenseServiceItem(initParams.channel, AutoServiceType.Enum.VEHICLE_LICENSE)]
        log.debug('行驶证扫描得到的信息:{}', vehicleLicense.toString())
        if (!vehicleLicense)
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, '行驶证扫描失败！')
        loadFields(vehicleLicense, driver.persistObj)
    }

    @Transactional
    @Override
    void persist(List<UnifyImageFile> files, Map initParams) {
        def typedQP = files.first().persistObj as QuotePhoto  //行驶证驾驶证两张图片对应一条数据库纪录，所以persisObj是一样的
        typedQP.with {
            it.userImg.ownerIdentityPath = files.find { it.type == Owner }?.relativeFilePath()
            it.userImg.drivingLicensePath = files.find { it.type == Driver }?.relativeFilePath()
            this.userImgRepository.save(it.userImg)
            this.quotePhotoRepository.save(it)
        }
    }

    @Override
    boolean support(Map initParams) {
        log.debug('图片上传初始化参数initParams:{}', initParams)
        !initParams.orderNo && initParams.businessType==BUSINESS_TYPE_QUOTE_PHONE && initParams.apiVersion <'v1.6'
    }

    @Override
    Map toUpload(Map initParams) {
        throw new BusinessException(BusinessException.Code.UNIMPLEMENTED_METHOD, '电话报价不支持待上传图片查询')
    }

    @Override
    void perCheck(Map initParams) {
        if (!initParams.user) {
            throw new BusinessException(BusinessException.Code.NONE_VALID_SESSION, "无用户信息，请登录。")
        }
    }

    private def loadFields(vehicleLicense, persistObj) {
        ['licensePlateNo', 'engineNo', 'owner', 'vinNo', 'enrollDate', 'identity'].each {
            persistObj[it] = vehicleLicense[it]
        }
        persistObj['code'] = vehicleLicense['brandCode']
    }

    @Override
    Object responseBody(List<UnifyImageFile> files, Map initParams) {
        files.collect {
            if (initParams.scan) {
                def formattedParams = formatFields(it.persistObj?.properties, VL_CLIENT_VISIBLE_FIELD).with {
                    Boolean allProps = initParams.additionalParameters?.quote?.format?.fields
                    allProps ? supplementInfoService.addNullValue(it, FIELD_ORDER_TOC) : it
                }
                LackOfSupplementInfoHandler.formatResponse(formattedParams)
            } else {
                [(it.type): ['absoluteFilePath': toAbsoluteUrl(it)]]
            }
        }.flatten()
    }

}
