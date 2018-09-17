package com.cheche365.cheche.core.service.image

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.PurchaseOrderImage
import com.cheche365.cheche.core.model.PurchaseOrderImageScene
import com.cheche365.cheche.core.model.PurchaseOrderImageType
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.OrderImageService
import com.cheche365.cheche.core.service.PurchaseOrderImageService
import groovy.util.logging.Slf4j
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

import javax.transaction.Transactional
import java.nio.file.Path
import java.nio.file.Paths

import static com.cheche365.cheche.core.model.PurchaseOrderImage.SOURCE.WEB
import static com.cheche365.cheche.core.model.PurchaseOrderImage.STATUS.AUDIT

/**
 * Created by zhengwei on 4/1/17.
 */

@Service
@Slf4j
@Order(8)
class PurchaseOrderUploadService extends ImgUploadService {

    PurchaseOrderImageService poiService
    PurchaseOrderRepository poRepo
    OrderImageService oiService
    public PurchaseOrderUploadService(PurchaseOrderImageService poiService, PurchaseOrderRepository poRepo, OrderImageService oiService){
        this.poiService = poiService
        this.poRepo = poRepo
        this.oiService = oiService
    }

    @Override
    void filePath(Map context, UnifyImageFile file) {

        Auto auto = context.order.auto
        PurchaseOrderImageType imageType = poiService.getImageType(file.type as Long)
        User user = context.user

        Path rootPath = Paths.get(resourceService.getResourceAbsolutePath(resourceService.getProperties().getOrderImagePath()))
        def pathSegments = [1l, 6l, 7l].contains(imageType.parentId) ? [user.id.toString(),  auto.identity, file.type] : [auto.licensePlateNo, auto.engineNo, auto.vinNo, file.type]
        pathSegments.inject(Paths.get('')) {Path path, part ->
            path.resolve(part?.replace("*", "米")?.replace("#", "井")?.trim())
        }.with {
            file.absolutePath = rootPath.resolve(it)
            file.relativePath = file.absolutePath.subpath(rootPath.nameCount, file.absolutePath.nameCount)
        }
    }

    @Override
    void scan(List<UnifyImageFile> files, Map initParams) {
        throw new BusinessException(BusinessException.Code.UNIMPLEMENTED_METHOD, '订单图片处理服务不支持扫描功能')
    }

    @Override
    def initPersistObj(List<UnifyImageFile> files, Map initParams) {

        PurchaseOrderImageScene imageScene = this.getImageScene(initParams)

        files.each {file ->
            PurchaseOrderImageType imageType = poiService.getImageType(file.type as Long)
            if(!imageType){
                log.warn('图片类型无数据库纪录对应, {}',file.type)
            }
            file.persistObj = poiService.findOrCreatePOI(initParams.objId, imageScene, imageType).with {
                updateTime = new Date()
                channel = initParams.channel as Channel
                source = WEB
                status = AUDIT
                it
            }
        }
    }

    PurchaseOrderImageScene getImageScene(Map initParams) {
        oiService.getImageScene(initParams.order)
    }

    @Transactional
    @Override
    void persist(List<UnifyImageFile> files, Map initParams) {

        def poiList = files.collect{ file ->
            def typedPoi = file.persistObj as PurchaseOrderImage
            typedPoi.url = file.relativeFilePath()
            typedPoi
        }

        poiService.save(poiList)

        if (!poiList.first().imageScene?.csIgnore) {
            poiService.updatePOIStatus(poiList.first().objId)
        }
    }

    @Override
    boolean support(Map initParams) {
        log.debug('图片上传初始化参数initParams:{}', initParams)
        return initParams.order
    }

    @Override
    Map toUpload(Map initParams) {
        Boolean showImageTab = oiService.showImageTab(initParams.quoteRecord, initParams.order)
        if (showImageTab) {
            return oiService.getUploadImgGroupByOrderNo(initParams.order.orderNo, getImageScene(initParams))
        }
        null
    }

    @Override
    void perCheck(Map initParams) {
        if (!initParams.order) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, '订单不存在')
        }
        if (!initParams.user) {
            throw new BusinessException(BusinessException.Code.NONE_VALID_SESSION, "无用户信息，请登录。");
        }
        initParams << [objId: initParams.order.id]
    }

    @Override
    Object responseBody(List<UnifyImageFile> files, Map initParams) {
        return super.responseBody(files, initParams)?.collect{Map firstLevel ->firstLevel.collect{it.value} }?.flatten()
    }
}
