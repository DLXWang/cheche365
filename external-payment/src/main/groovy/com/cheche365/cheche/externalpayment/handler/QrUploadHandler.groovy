package com.cheche365.cheche.externalpayment.handler

import com.cheche365.cheche.core.service.IResourceService
import com.cheche365.cheche.core.service.qrcode.QRCodeService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.nio.file.Paths

import static com.cheche365.cheche.core.service.image.ImgUploadService.uploadToFileServer
import static java.lang.System.currentTimeMillis
import static java.util.UUID.randomUUID

@Service
@Slf4j
class QrUploadHandler {

    @Autowired
    IResourceService resourceService;

    @Autowired
    QRCodeService qrCodeService


    def convertToServer(String qrUrl){
        upload(qrUrl.split("\\.").last(),new URL(qrUrl).bytes)
    }

    def upload(String suffix=null,byte[] picByte){
        String rootPath = getRootPath()
        def fileName = randomUUID().toString() + "." + (suffix ?: "png")
        uploadToFileServer(Paths.get(rootPath),fileName,picByte)
        log.debug('二维码转存服务器结束')

        resourceService.absoluteUrl(rootPath.toString(), fileName)
    }

    def getRootPath(){
        log.debug('支付二维码图片待上传至服务器')
        resourceService.getResourceAbsolutePath(resourceService.getProperties().getOrderPayQRCodePath())
    }

    String createQrCode(String text){
        qrCodeService.generateQRCode(text,getRootPath(),randomUUID().toString() + currentTimeMillis() + '.png')
    }

}
