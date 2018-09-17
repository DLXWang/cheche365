package com.cheche365.cheche.core.service.image

import com.cheche365.cheche.core.service.IResourceService
import com.cheche365.cheche.core.service.IThirdPartyUploadingService
import com.cheche365.cheche.core.util.FileUtil
import groovy.util.logging.Slf4j
import org.apache.commons.lang.exception.ExceptionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.nio.file.Files
import java.nio.file.Path

@Service
@Slf4j
public abstract class ImgUploadService {

    @Autowired
    IResourceService resourceService;
    @Autowired(required = false)
    List<IThirdPartyUploadingService> uploadingServices

    public static final Integer BUSINESS_TYPE_DAILY_INSURE = 5; // 安心按天买车险核保图片上传（补充参数：orderNo）
    public static final Integer BUSINESS_TYPE_DAILY_RESTART = 6; // 安心按天买车险复驶图片上传（补充参数：orderNo、restartInsuranceId）

    public static String Owner = "Owner";
    public static String Driver = "Driver";

    def doService(List<UnifyImageFile> files, Map initParams) {

        perCheck(initParams)
        log.debug('图片上传初始化参数完毕，当前用户id:{}, channel:{}', initParams.user?.id, initParams.channel?.id)

        files.each { file ->
            filePath(initParams, file)
            log.debug('待上传图片绝对路径 {}, 相对路径 {}', file.absolutePath, file.relativePath)

            uploadToFileServer(file.absolutePath, file.name, file.content)
            log.debug('图片上传结束')
        }

        initPersistObj(files, initParams)

        if (initParams.scan) {
            log.debug('图片需要被扫描')
            scan(files, initParams)
        }

        persist(files, initParams) //图片全部上传成功后再持久化，有一张失败则全部图片都不会持久化
        log.debug('图片数据持久化结束')

        thirdPartyServiceUpload(files, initParams)

        responseBody(files, initParams)
    }

    def thirdPartyServiceUpload(List<UnifyImageFile> files, Map initParams) {
        if(!initParams.thirdPartyServiceUpload){
            return
        }

        def contents = files.collect { toAbsoluteUrl(it) }
        log.debug('调用第三方上传图片服务，contents:{},additionalParameters:{}', contents, initParams.additionalParameters)

        try {
            uploadingServices.find {
                it.isSuitable(initParams.additionalParameters.conditions)
            }?.upload(contents, initParams.additionalParameters)
        } catch (Exception e) {
            log.error('调用第三方上传图片服务错误，contents:{},additionalParameters:{},exception:{}', contents, initParams.additionalParameters, ExceptionUtils.getStackTrace(e))
        }
    }

    def static uploadToFileServer(Path absPath, String fileName, byte[] fileContent) {
        Files.createDirectories(absPath)
        log.debug('创建文件路径结束 {}', absPath.toString())
        FileUtil.writeFile(absPath.toString() + File.separator + fileName, fileContent)
    }

    abstract boolean support(Map initParams)

    abstract Map toUpload(Map initParams)

    abstract void perCheck(Map initParams)

    abstract initPersistObj(List<UnifyImageFile> files, Map initParams)

    abstract void filePath(Map context, UnifyImageFile file)

    abstract void scan(List<UnifyImageFile> files, Map initParams)

    abstract void persist(List<UnifyImageFile> files, Map initParams)

    Object toAbsoluteUrl(UnifyImageFile file) {
        resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.properties.orderImagePath), file.relativeFilePath())
    }

    Object responseBody(List<UnifyImageFile> files, Map initParams) {
        files.collect {
            [(it.type): toAbsoluteUrl(it)]
        }
    }

}
