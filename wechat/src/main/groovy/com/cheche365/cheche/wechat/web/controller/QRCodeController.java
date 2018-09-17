package com.cheche365.cheche.wechat.web.controller;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.wechat.QRCodeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by liqiang on 7/21/15.
 */
@RestController
public class QRCodeController {

    @Autowired
    private QRCodeManager qrCodeManager;

    @RequestMapping(value = "/web/wechat/qrcodes/{channelId}", method = RequestMethod.POST)
    public String create(@PathVariable Long channelId, @RequestBody Map param) {
        Channel channel = Channel.toChannel(channelId);
        if (channel == null || param == null) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "输入参数错误");
        }

        param.put("channel",channel);
        return qrCodeManager.createQRCode(param);
    }
}
