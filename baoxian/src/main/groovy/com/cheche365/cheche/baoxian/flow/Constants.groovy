package com.cheche365.cheche.baoxian.flow

import static com.cheche365.cheche.baoxian.util.BusinessUtils.sendAndReceiveV2
import static com.cheche365.cheche.baoxian.util.BusinessUtils.sendAndReceive
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT5
import static com.cheche365.flow.business.flow.util.BusinessUtils.getVehicleOption
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMMERCIAL_START_DATE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMPULSORY_START_DATE_TEMPLATE_QUOTING


/**
 * 流程步骤所需的常量
 */
class Constants {

    static final _TASKID_TTL = 12L

    static final _STATUS_CODE_INSURE_SUCCESS = -10001L

    static final _API_PATH_PREFIX_OLD = '/chn/channel'

    static final _API_PATH_PREFIX_NEW = '/cm/channelService'

    static final _BAOXIAN_GET_VEHICLE_OPTION = { context, vehicle ->
        def vehicleOptionInfo = [
            brand           : '',
            family          : '',
            gearbox         : vehicle.gearbox,
            exhaustScale    : '',
            model           : vehicle.vehicleName,
            productionDate  : vehicle.maketDate,
            seats           : vehicle.seat,
            newPrice        : vehicle.price,
        ]
        getVehicleOption vehicle.vehicleId, vehicleOptionInfo
    }

    static final get_VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS() {
        [
            default: [
                _SUPPLEMENT_INFO_BRAND_CODE_TEMPLATE_QUOTING,
                _SUPPLEMENT_INFO_CAR_MODEL_LIST_TEMPLATE_QUOTING
            ]
        ]
    }

    public static final _COMPANY_I2O_MAPPINGS = [
        10000L: '2005',//人保财险
        15000L: '2019',//阳光保险
        20000L: '2007',//平安保险
        25000L: '2011',//太平洋保险
        30000L: '2016',//中国太平
        40000L: '2002',//国寿财险
        45000L: '2027',//中华联合
        55000L: '2026',//安盛天平
        60000L: '2077',//富德
        65000L: '2049',//安心保险
        75000L: '2071',//史带财险
        90000L: '2085',//利宝保险
        95000L: '2022',//永诚保险
        240000L: '2021', // 大地
        165000L: '2095', //紫金保险
        85000L : '2066', //亚太财险
        205000L:'2043',//华安
    ]

    public static final _CITY_PROVIDER_ID_MAPPINGS = [
        441900L: [//东莞
            10000L: '20054419', // 人保财险
            15000L: '20194419', // 阳光财险
            20000L: '20074419', // 平安财险
            25000L: '20114419', // 太保财险
            30000L: '20164419', // 太平财险
            55000L: '20264419', //安盛天平财险
        ],
        610100L: [//西安
            15000L: '201961',   //阳光财险
            20000L: '200761',   //平安
            25000L: '201161',   //太平洋保险
            40000L: '200261',   //国寿财险
            55000L: '202661',   //安盛天平财险
        ],
        410100L: [//郑州
            15000L: '201941',   // 阳光财险
            20000L: '20074101', // 平安财险
            25000L: '201141',   // 太平洋保险
            40000L: '20024101', //中国人寿
            55000L: '202641',   //安盛天平财险
        ],
        120100L: [//天津
            55000L: '202612', //安盛天平财险
            20000L: '200712',//平安财险
        ],
        320100L: [//南京
            45000L: '20273201',      //中华联合财险
            25000L: '2011320199',    //太平洋
            40000L: '20023201',      //国寿财
            55000L: '202632',        //安盛天平
            20000L: '20073201',      //平安
            25000L: '20163201',      //太平洋
            15000L: '201932',        //阳光
        ],
        320500L: [//苏州
            15000L: '201932',//阳光
            20000L: '20073201',//平安
            55000L: '202632',//安盛
            40000L: '20023201',//国寿财
            45000L: '20273201',//中华联合
            25000L: '2011320582',//太平洋
            30000L: '20163201',//太平财险
        ],
        510100L: [//四川全省
            20000L: '200751',//平安,
            45000L: '20275101',//中华联合,
            25000L: '201151',//太平洋
        ],
        450100L: [//广西全省
            20000L: '200745',//平安,
            30000L: '201645',//太平财险,
            25000L: '20114501',//太平洋,
            45000L: '202745',//中华联合,
            55000L: '20264501',//安盛
        ],
        370100L: [//济南
            45000L: '20273701',//中华联合,
            25000L: '20113701',//太平洋,
            55000L: '202637',//安盛
        ],
        371600L:[//滨州
            45000L:'20273716',//中华联合
        ]
    ]

    static final _BAOXIAN_LOAD_PERSISTENT_STATE = { context, persistentState ->
        [
            taskId              : persistentState?.taskId,
            provider            : persistentState?.provider,
            imageInfos          : persistentState?.imageInfos,
            token               : persistentState?.token,
            insureSupplys       : persistentState?.insureSupplys
        ]
    }


    static final _BAOXIAN_SAVE_PERSISTENT_STATE = { context ->
        [
            taskId              : context.taskId,
            provider            : context.provider,
            payUrl              : context.payUrl,
            payValidTime        : context.payValidTime,
            imageInfos          : context.imageInfos,
            token               : context.token,
            insureSupplys       : context.insureSupplys
        ]
    }

    static final _BAOXIAN_ADDITIONAL_QUOTE_RECORD_INFO_EXTRACTOR = { context ->
        [
            expireDate: context.quoteResult?.payValidTime ? _DATE_FORMAT5.parse(context.quoteResult.payValidTime) : null
        ]
    }

    static final _FLOW_ENCRIPT_PARAMS_SENDING_MAPPINGS = [
//        default  : sendAndReceive,
        default  : sendAndReceiveV2
    ]

}
