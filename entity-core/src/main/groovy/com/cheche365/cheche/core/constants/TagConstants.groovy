package com.cheche365.cheche.core.constants

class TagConstants {

    public static final Map PARTNER_TAGS = [
            NO_PARAMETER       : [
                    mask: 1 << 1,
                    desc: '链接无参数'
            ],
            WITH_USER          : [
                    mask: 1 << 2,
                    desc: '有用户参数'
            ],
            WITH_AUTO          : [
                    mask: 1 << 3,
                    desc: '有车辆参数'
            ],
            SINGLE_COMPANY     : [
                    mask: 1 << 4,
                    desc: '直通车模式（只有一个保险公司）'
            ],
            NEED_DECRYPT       : [
                    mask: 1 << 5,
                    desc: '参数需要解密'
            ],
            NEED_SYNC_ORDER    : [
                    mask: 1 << 6,
                    desc: '需要同步订单'
            ],
            SUPPORT_AMEND      : [
                    mask: 1 << 7,
                    desc: '支持订单增补'
            ],
            NEED_SYNC_REBATE   : [
                    mask: 1 << 8,
                    desc: '同步订单包含返点信息'
            ],
            TO_PHOTO_PAGE      : [
                    mask: 1 << 9,
                    desc: '跳到拍照报价页'
            ],
            WITH_STATE         : [
                    mask: 1 << 10,
                    desc: '带state参数'
            ],
            REDIRECT_WITH_UID  : [
                    mask: 1 << 11,
                    desc: '跳转链接包含uid'
            ],
            CREATE_PARTNER_USER: [
                    mask: 1 << 12,
                    desc: '登录创建第三方用户'
            ],
            SYNC_BODY_NEED_ENCRYPT: [
                    mask: 1 << 13,
                    desc: '同步报文需要加密'
            ],
            NEED_EMAIL_SYNC    : [
                    mask: 1 << 14,
                    desc: '需要邮件同步订单'
            ]
    ].asImmutable()

    public static final Map CHANNEL_TAGS = [
            SELF                : [
                    mask: 1 << 0,
                    desc: '车车自有渠道'
            ],
            PARTNER_H5          : [
                    mask: 1 << 1,
                    desc: 'h5对接渠道'
            ],
            ORDER_CENTER        : [
                    mask: 1 << 2,
                    desc: '出单中心渠道'
            ],
            AGENT               : [
                    mask: 1 << 3,
                    desc: '代理人渠道'
            ],
            NON_AUTO            : [
                    mask: 1 << 4,
                    desc: '非车渠道'
            ],
            SELF_APP            : [
                    mask: 1 << 5,
                    desc: '车车自有app'
            ],
            DISABLED_CHANNEL    : [
                    mask: 1 << 6,
                    desc: '已下线渠道'
            ],
            REBATE_INTO_WALLET  : [
                    mask: 1 << 7,
                    desc: '返点进钱包'
            ],
            NORMAL_LOGIN_PARTNER: [
                    mask: 1 << 8,
                    desc: '车车登录partner'
            ],
            QUOTE_FORBID        : [
                    mask: 1 << 9,
                    desc: '禁止报价'
            ],
            PAY_FORBID          : [
                    mask: 1 << 10,
                    desc: '禁止支付'
            ],
            WECHAT_APP          : [
                    mask: 1 << 11,
                    desc: '车车微信小程序渠道'
            ],
            STANDARD_AGENT      : [
                    mask: 1 << 12,
                    desc: '标准代理人渠道'
            ],
            LEVEL_AGENT         : [
                    mask: 1 << 13,
                    desc: '支持分级代理'
            ],
            PARTNER_API         : [
                    mask: 1 << 14,
                    desc: 'api对接渠道'
            ]
    ].asImmutable()

    public static final Map COMPANY_TAGS = [
            QUOTE            : [
                    mask: 1 << 0,
                    desc: '支持报价'
            ],
            DISPLAY          : [
                    mask: 1 << 1,
                    desc: '前端可展示'
            ],
            RENEW_SUPPORT    : [
                    mask: 1 << 2,
                    desc: '支持续保'
            ],
            PROMOTE          : [
                    mask: 1 << 3,
                    desc: '推广'
            ],
            RECOMMEND        : [
                    mask: 1 << 4,
                    desc: '推荐使用'
            ],
            REFERENCE_BASE   : [
                    mask: 1 << 5,
                    desc: '参考报价基准'
            ],
            // 注：1 << 6 、 1 << 7 、1 << 13 、 1 << 14未使用，新增配置项优先使用
            API_QUOTE        : [
                    mask: 1 << 8,
                    desc: '报价方式-接口报价'
            ],
            DISABLED         : [
                    mask: 1 << 9,
                    desc: '已下线'
            ],
            NON_AUTO_SUPPORT : [
                    mask: 1 << 10,
                    desc: '非车保险公司'
            ],
            FANHUA_SUPPORT   : [
                    mask: 1 << 11,
                    desc: '泛华保险公司'
            ],
            OC_MANUAL_SUPPORT: [
                    mask: 1 << 12,
                    desc: '出单中心手动报价'
            ],
            RE_INSURE_SUPPORT: [
                    mask: 1 << 15,
                    desc: '下单需重新核保'
            ],
            CIRC             : [
                    mask: 1 << 16,
                    desc: '保监会官网保险公司'
            ],
             USE_CASHIER    : [
                 mask: 1 << 17,
                 desc: '使用车车收银台'
             ]
    ].asImmutable()

    public static final Map MARKETING_TAGS = [
            ATTEND_WITHOUT_LOGIN: [
                    mask: 1 << 1,
                    desc: '免登录访问'
            ],
            MULTI_ATTEND        : [
                    mask: 1 << 2,
                    desc: '允许重复参加'
            ],
            WITH_UUID           : [
                    mask: 1 << 3,
                    desc: '带UUID'
            ],
            NEED_WECHAT_OAUTH   : [
                    mask: 1 << 4,
                    desc: '需要微信OAuth认证'
            ],
            ATTEND_CREATE_USER:[
                mask: 1 << 5,
                desc: '参与并创建用户'
            ]
    ].asImmutable()
}

