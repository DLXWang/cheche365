package com.cheche365.cheche.rest.util

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.service.image.ImgUploadService
import com.cheche365.cheche.core.service.image.UnifyImageFile
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.web.multipart.MultipartFile

import static com.cheche365.cheche.core.model.PurchaseOrderImageType.Enum.API_CUSTOM_IMAGE_SUB_TYPE

/**
 * Created by zhengwei on 4/2/17.
 * 图片处理工具类，之所以写在web下而不是core，是因为要依赖spring web的类(MultipartFile)
 */

class ImageFileUtil {

    static final Integer IMAGE_TYPE_BASE64 = 1
    static final Integer IMAGE_TYPE_URL = 2
    static final Long MAX_FILE_SIZE = 1024 * 1024 * 10L

    static final MULTIPART_FILE_NAME_MAPPING = [ //binary上传驾驶证／行驶证图片，图片name并没有按服务器规范叫Owner/Driver，所以需要个映射关系
        ownerIdentityImg : ImgUploadService.Owner,
        driverImg: ImgUploadService.Driver
    ]

    static List<UnifyImageFile> unifyFormat(Collection files, Integer imageType=null){

        List<UnifyImageFile> unifyImageFiles
        if (files.first() instanceof MultipartFile){
            unifyImageFiles = unifyFormatMultiPart(files)
        } else if (IMAGE_TYPE_BASE64 == imageType){
            unifyImageFiles = unifyFormatBase64(files)
        } else if (IMAGE_TYPE_URL == imageType) {
            unifyImageFiles = unifyFormatURL(files)
        } else {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "非预期图片类型$imageType")
        }

        if(unifyImageFiles.any {!it.content || it.content.length==0}){
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "图片内容为空")
        }

        if(unifyImageFiles.any{it.content.length > MAX_FILE_SIZE}){
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "图片大小超限")
        }

        return unifyImageFiles
    }

    def static unifyFormatMultiPart(Collection<MultipartFile> files){

        files.collect {file ->
            new UnifyImageFile(
                name: unifyFileName(file.originalFilename?.split('\\.')?.last()),
                content: file.bytes,
                type: MULTIPART_FILE_NAME_MAPPING.get(file.name) ?: file.name
            )
        }
    }

    /**
     * 参数格式：

        [
             {
                 "content": "32432",
                 "extension": "jpeg",
                 "type": "8"
             },
             {
                 "content": "232123",
                 "extension": "jpeg",
                 "type": "9"
             }
         ]
     */
    def static unifyFormatBase64(Collection files){
        files.collect {file ->
            new UnifyImageFile(
                name: unifyFileName(file.extension),
                content: file.content.decodeBase64(),
                type: file.type ?: API_CUSTOM_IMAGE_SUB_TYPE
            )
        }
    }

    def static unifyFormatURL(Collection files){
        files.collect{file ->
            new UnifyImageFile(
                name: unifyFileName(file.content.split('\\.').last()),
                content: new URL(file.content).bytes,
                type: file.type ?: API_CUSTOM_IMAGE_SUB_TYPE
            )
        }
    }

    def static unifyFileName(String extension){
        System.currentTimeMillis() + RandomStringUtils.randomAlphanumeric(10).toUpperCase() + '.' + extension
    }


}
