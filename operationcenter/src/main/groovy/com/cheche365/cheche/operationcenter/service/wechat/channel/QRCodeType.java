package com.cheche365.cheche.operationcenter.service.wechat.channel;

/**
 * Created by wangfei on 2015/7/29.
 */
public enum QRCodeType {
    QR_SCENE,
    QR_LIMIT_SCENE;

    public static QRCodeType format(String qrCodeType) {
        for (QRCodeType type : QRCodeType.values()) {
            if (type.toString().equals(qrCodeType)) {
                return type;
            }
        }

        return null;
    }
}
