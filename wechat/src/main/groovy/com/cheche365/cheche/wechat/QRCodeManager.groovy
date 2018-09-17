package com.cheche365.cheche.wechat

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.service.ResourceService
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.wechat.message.json.qrcode.ActionInfo
import com.cheche365.cheche.wechat.message.json.qrcode.QRCodeCreationRequest
import com.cheche365.cheche.wechat.message.json.qrcode.QRCodeCreationResponse
import com.cheche365.cheche.wechat.message.json.qrcode.Scene
import com.cheche365.cheche.core.model.WechatQRCode
import com.cheche365.cheche.core.repository.WechatQRCodeRepository
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created by liqiang on 7/15/15.
 */
@Component
public class QRCodeManager {

    @Autowired
    private MessageSender messageSender;
    @Autowired
    private WechatQRCodeRepository wechatQRCodeRepository;
    private Logger logger = LoggerFactory.getLogger(QRCodeManager.class);

    @Autowired
    private ResourceService resourceService;

    public WechatQRCode createQRCode(WechatQRCode templateQRCode){

        String actionName = templateQRCode.getActionName();
        String sceneStr = templateQRCode.getSceneStr();
        long sceneId = templateQRCode.getSceneId();
        WechatQRCode existedQRCode = null;
        Scene scene = new Scene();
        switch (WechatQRCode.ActionName.valueOf(actionName)){
            case 'QR_LIMIT_SCENE':
            case 'QR_SCENE':
                existedQRCode = wechatQRCodeRepository.findFirstByActionNameAndSceneId(actionName,sceneId);
                scene.setSceneId(sceneId);
                break;
            case 'QR_LIMIT_STR_SCENE':
                existedQRCode = wechatQRCodeRepository.findFirstByActionNameAndSceneStr(actionName,sceneStr);
                scene.setSceneStr(sceneStr);
                break;
            default:
                throw new IllegalArgumentException("invalid action name: " + actionName);
        }

        if (existedQRCode != null){
            logger.warn(String.format("qrcode for scene_id [%d], scene_str[%s], action_name[%s] already exists", sceneId, sceneStr, actionName));
            return existedQRCode;
        }

        ActionInfo actionInfo = new ActionInfo();
        actionInfo.setScene(scene);
        QRCodeCreationRequest request = new QRCodeCreationRequest();
        request.setActionName(actionName);
        request.setActionInfo(actionInfo);
        request.setExpireSeconds(templateQRCode.getExpireSeconds());

        QRCodeCreationResponse response = messageSender.postMessage("cgi-bin/qrcode/create",new HashMap(),request,QRCodeCreationResponse.class, Channel.Enum.WE_CHAT_3);
        if (StringUtils.isNoneBlank(response.getErrCode())){
            logger.error("generate qrcode failure, error code [" + response.getErrMessage() + "] error message [" + response.getErrMessage());
            return null;
        }
        WechatQRCode wechatQRCode = new WechatQRCode();
        wechatQRCode.setActionName(request.getActionName());
        switch (WechatQRCode.ActionName.valueOf(actionName)){
            case 'QR_LIMIT_SCENE':
            case 'QR_SCENE':
                wechatQRCode.setSceneId(request.getActionInfo().getScene().getSceneId());
                wechatQRCode.setImageURL(createQRImage(actionName,String.valueOf(wechatQRCode.getSceneId()),response.getUrl()));
                break;
            case 'QR_LIMIT_STR_SCENE':
                wechatQRCode.setSceneStr(request.getActionInfo().getScene().getSceneStr());
                scene.setSceneStr(sceneStr);
                wechatQRCode.setImageURL(createQRImage(actionName,sceneStr,response.getUrl()));
                break;
            default:
                throw new IllegalArgumentException("invalid action name: " + actionName);
        }

        wechatQRCode.setTicket(response.getTicket());
        wechatQRCode.setUrl(response.getUrl());
//        wechatQRCode.setExpireSeconds(response.getExpireSeconds());
        wechatQRCode.setExpireSeconds(templateQRCode.getExpireSeconds());
        wechatQRCode.setTarget(templateQRCode.getTarget());
        wechatQRCode.setComments(templateQRCode.getComments());

        wechatQRCodeRepository.save(wechatQRCode);

        return wechatQRCode;
    }

    private String createQRImage(String subPath, String imgName, String code_url) {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(code_url, BarcodeFormat.QR_CODE, 400, 400);
        Path filePath = Paths.get(resourceService.getResourceAbsolutePath(resourceService.getProperties().getWechatQRCodePath()), subPath, imgName + "_" + System.currentTimeMillis() + ".png");
        if (logger.isDebugEnabled()) {
            logger.debug("file path is: {}", filePath);
        }
        createDirectoriesIfNeed(filePath);
        MatrixToImageWriter.writeToPath(matrix, "PNG", filePath);
        logger.debug("the qr image location is {}", filePath.toString());
        return resourceService.absoluteUrl(filePath.toString());
    }

    private void createDirectoriesIfNeed(Path filePath) {
        File parentDirectory = filePath.getParent().toFile();
        if (!parentDirectory.exists()){
            parentDirectory.mkdirs();
        }

    }

    String createQRCode(Map param) {
        Channel channel = param.channel
        Channel.nativeWechatApp().contains(channel) ? formatWechatAppParam(param) : formatWechatParam(param)
        messageSender.postMessage(param.path, new HashMap<>(), CacheUtil.doJacksonSerialize(param.reqBody), Map.class, channel)?.with {
            createQRImage(channel.getName(), param.imageName, it.url)
        }
    }

    def formatWechatParam(Map param) {
        param.reqBody = [
            'expire_seconds': 2591000,
            'action_name'   : param.actionName,
            'action_info'   : [
                'scene': ['scene_str': param.scene]
            ]
        ]
        param.path = "cgi-bin/qrcode/create"
        param.imageName = param.scene
    }

    def formatWechatAppParam(Map param) {
        param.reqBody = [
            'scene': param.scene,
            'page' : param.page
        ]
        param.path = "/wxa/getwxacodeunlimit"
        param.imageName = param.scene
    }

}
