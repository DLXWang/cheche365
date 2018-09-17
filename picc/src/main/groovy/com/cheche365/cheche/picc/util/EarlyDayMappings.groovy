package com.cheche365.cheche.picc.util



/**
 * 投保期限日期限制
 */
class EarlyDayMappings {

    //报价的期限,北京上海不限制;其他取earlyDays4Insure，因为其他地区日期过大无法报价
//    private static final MAX_EARLY_DAYS_4_QUOTE = 375

    static final _EARLY_DAY_MAPPINGS = [
        default: [
            earlyDays4Renewal  : 90,
            earlyDays4Insure   : 90,
            earlyDays4InsureBZ : 90,
            nextDays4Start     : 1,
        ],
        310000L: [                  //上海
            earlyDays4Renewal  : 90,
            earlyDays4Insure   : 65,
            earlyDays4InsureBZ : 30, // 人保API返回的是120，但是其网站JS却写死30
        ],
        330100L: [                  //杭州
            earlyDays4Renewal  : 90,
            earlyDays4Insure   : 60,
            earlyDays4InsureBZ : 60,
            nextDays4Start     : 0,
        ],
        320500L: [                  //苏州
            earlyDays4Renewal  : 90,
            earlyDays4Insure   : 40,
            earlyDays4InsureBZ : 40,
        ],
        320100L: [                  //南京
            earlyDays4Renewal  : 90,
            earlyDays4Insure   : 40,
            earlyDays4InsureBZ : 40,
            nextDays4Start     : 0,
        ],
        370100L: [                  //济南
            earlyDays4Renewal  : 30,
            earlyDays4Insure   : 30,
            earlyDays4InsureBZ : 30,
        ],
        430100L: [                  //长沙
            earlyDays4Renewal  : 60,
            earlyDays4Insure   : 60,
            earlyDays4InsureBZ : 60,
        ],
        520100L: [                  //贵阳
            earlyDays4Renewal  : 180,
            earlyDays4Insure   : 90,
            earlyDays4InsureBZ : 90,
        ],
        540100L: [                  //拉萨
            earlyDays4Renewal  : 90,
            earlyDays4Insure   : 90,
            earlyDays4InsureBZ : 90,
            nextDays4Start     : 2,
        ],
        620100L: [                  //兰州
            earlyDays4Renewal  : 180,
            earlyDays4Insure   : 90,
            earlyDays4InsureBZ : 90,
            nextDays4Start     : 2,
        ],
        350100L: [                  //福州
            earlyDays4Renewal  : 180,
            earlyDays4Insure   : 30,
            earlyDays4InsureBZ : 90,
            nextDays4Start     : 2,
        ],

        330200L: [                  //宁波
            earlyDays4Renewal  : 30,
            earlyDays4Insure   : 30,
            earlyDays4InsureBZ : 30,
        ],
        330300L: [                  //温州
            earlyDays4Renewal  : 90,
            earlyDays4Insure   : 60,
            earlyDays4InsureBZ : 60,
        ],
        330400L: [                  //嘉兴
            earlyDays4Renewal  : 90,
            earlyDays4Insure   : 60,
            earlyDays4InsureBZ : 60,
        ],
        330500L: [                  //湖州
            earlyDays4Renewal  : 90,
            earlyDays4Insure   : 60,
            earlyDays4InsureBZ : 60,
        ],
        330600L: [                  //绍兴 不是猜的
            earlyDays4Renewal  : 60,
            earlyDays4Insure   : 60,
            earlyDays4InsureBZ : 60,
        ],
        330700L: [                  //金华
            earlyDays4Renewal  : 60,
            earlyDays4Insure   : 60,
            earlyDays4InsureBZ : 60,
        ],
        330782L: [                  //义乌 不是猜的
            earlyDays4Renewal  : 60,
            earlyDays4Insure   : 60,
            earlyDays4InsureBZ : 60,
        ],
        330800L: [                  //衢州
            earlyDays4Renewal  : 60,
            earlyDays4Insure   : 60,
            earlyDays4InsureBZ : 60,
        ],
        331000L: [                  //台州
            earlyDays4Renewal  : 60,
            earlyDays4Insure   : 60,
            earlyDays4InsureBZ : 60,
        ],
        331100L: [                  //丽水
            earlyDays4Renewal  : 60,
            earlyDays4Insure   : 60,
            earlyDays4InsureBZ : 60,
        ],
        130100L: [                  //石家莊
            nextDays4Start : 2,
        ],
        120000L: [                  //天津
            nextDays4Start : 2,
        ],
        610100L: [                  //西安
            nextDays4Start : 2,
        ],
        440300L: [                  //深圳
            nextDays4Start : 2,
        ],
        340100L: [
            nextDays4Start : 0      //合肥
        ]
    ]

}
