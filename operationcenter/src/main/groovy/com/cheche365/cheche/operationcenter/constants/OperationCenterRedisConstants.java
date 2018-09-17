package com.cheche365.cheche.operationcenter.constants;

/**
 * Created by guoweifu on 2015/10/29.
 */
public class OperationCenterRedisConstants {
    // 微信二维码渠道：从redis load 关注数扫描数开关
    public static final String WECHAT_QRCODE_COUNT_LOADING_FLAG = "refresh.scan.subscribe.count.flag";

    // 微信二维码渠道：从redis load 关注数扫描数开关：打开
    public static final String WECHAT_QRCODE_COUNT_LOADING_FLAG_OPEN = "1";

    // 微信二维码渠道：从redis load 关注数扫描数开关：关闭
    public static final String WECHAT_QRCODE_COUNT_LOADING_FLAG_CLOSE = "0";
}
