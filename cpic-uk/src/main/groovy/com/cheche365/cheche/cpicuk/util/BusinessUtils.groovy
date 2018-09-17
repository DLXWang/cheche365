package com.cheche365.cheche.cpicuk.util

import groovy.util.logging.Slf4j

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3 as dateFormat3
import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode
import static com.cheche365.cheche.cpicuk.flow.Constants._FUEL_TYPE_MAPPING
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMMERCIAL_CAPTCHA_IMAGE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMPULSORY_CAPTCHA_IMAGE_TEMPLATE_QUOTING



/**
 * 业务相关工具类
 */
@Slf4j
class BusinessUtils {

    static final DateTimeFormatter _DATETIME_FORMAT_NEW = new DateTimeFormatterBuilder().appendPattern('yyyy-MM-dd HH:mm').toFormatter()

    static def getCpicTime(stDate, format = _DATETIME_FORMAT3) {
        _DATETIME_FORMAT_NEW.format(LocalDate.parse(stDate, format).atTime(0, 0, 0))
    }

    static def getIDExpireDate(stDate) {
        dateFormat3.format(new SimpleDateFormat('yyyy.MM.dd').parse(stDate))
    }

    static def getTaxDate(stDate, format = _DATETIME_FORMAT3) {
        if (!stDate) {
            stDate = format.format(LocalDate.now())
        }
        def now = LocalDate.parse(stDate, format)
        def thisYear = now.getYear();
        def stTaxStartDate = format.format(LocalDate.of(thisYear, 1, 1))
        def stTaxEndDate = format.format(LocalDate.of(thisYear, 12, 31))
        new Tuple2(stTaxStartDate, stTaxEndDate)
    }

    /**
     * 计算车辆实际价值
     * @param usageType 使用性质 上下文获取 或者请求/ecar/insure/queryInsureInfo
     * @param vehiclePurpose 车辆用途 上下文获取 或者请求/ecar/insure/queryInsureInfo
     * @param purchasePrice 新车购置价  上下文获取 或者请求/ecar/insure/queryInsureInfo
     * @param registerDate 初登日期 yyyy-MM-DD 上下文获取 或者请求 /ecar/insure/queryInsureInfo
     * @param inceptionDate 商业险开始时间 yyyy-MM-DD
     */
    static calculateActualValue(usageType, vehiclePurpose, purchasePrice, registerDate, inceptionDate) {
        if (!purchasePrice) {
            return 0
        }

        if (usageType == null || vehiclePurpose == null) {
            return purchasePrice
        }
        // 计算使用时长
        def regDate = registerDate ? registerDate : new Date()
        def icpDate = inceptionDate ? _DATE_FORMAT3.parse(inceptionDate) : new Date()

        def reqCalendar = Calendar.getInstance()
        reqCalendar.setTime(regDate)
        def icpCalendar = Calendar.getInstance()
        icpCalendar.setTime(icpDate)

        def usedMonths = ((icpCalendar.get(Calendar.MONTH) - reqCalendar.get(Calendar.MONTH))) + (
            (icpCalendar.get(Calendar.YEAR) - reqCalendar.get(Calendar.YEAR)) * 12)

        if (reqCalendar.get(Calendar.DAY_OF_MONTH) > icpCalendar.get(Calendar.DAY_OF_MONTH)) {
            usedMonths = usedMonths - 1
        }

        def rate = getDeprecationRate(usageType, vehiclePurpose)

        def actualValue = (rate == null) ? Long.valueOf(purchasePrice).longValue()
            : (Long.valueOf(purchasePrice).longValue() * (1 - Math.min(rate * usedMonths, 0.8)))

        new BigDecimal(actualValue).setScale(0, BigDecimal.ROUND_HALF_UP)

    }

    static def getDeprecationRate(usageType, vehiclePurpose) {
        _DISCOUNT_RATE_MAPPING.get(usageType)?.get(vehiclePurpose)
    }

    /**
     * 折损rate  from  issue.cpic.com.cn/ecar/page/js/config.js  如果金额计算错误，请看一下js的数据有没有变化
     */
    static final _DISCOUNT_RATE_MAPPING = [
        '101': ['01': 0.006, '02': 0.009],
        '201': ['01': 0.006, '02': 0.009, '03': 0.009, '04': 0.009, '05': 0.011, '07': 0.009],
        '202': ['01': 0.006, '02': 0.009, '03': 0.009, '04': 0.009, '05': 0.011, '07': 0.009],
        '301': ['01': 0.006, '02': 0.009, '03': 0.009, '04': 0.009, '05': 0.011, '07': 0.009],
        '701': ['06': 0.011, 'other': 0.009],
        '702': ['06': 0.011, 'other': 0.009],
        '401': ["01": 0.011, "02": 0.011],
        '402': ["01": 0.011, "02": 0.011],
        '501': ["01": 0.009, "02": 0.009],
        '502': ["01": 0.009, "02": 0.009],
        '601': ["03": 0.011, "04": 0.011, "05": 0.014, "07": 0.009]
    ]

    /**
     * 根据车辆座位选择车辆种类vehicleType
     * '01':'6座以下客车',
     * '02':'6座及10座以下客车',
     * '03':'10座及20座以下客车',
     * '04':'20座及36座以下客车',
     * '05':'36座及36座以上客车'
     */
    static selectVehicleType(seatCountText) {
        def seatCount = seatCountText as int   // string to int
        if (seatCount < 6) {
            '01'
        } else if (seatCount < 10) {
            '02'
        } else if (seatCount < 20) {
            '03'
        } else if (seatCount < 36) {
            '04'
        } else {
            '05'
        }
    }

    /**
     * 根据地区编码不同，选择交强险纳税类型
     * 非北京  “T” 正常缴税
     * 北京  "3" 正常缴税
     * 杭州  '3' 正常缴税  add by yujingtai 2018-06-19
     * @param areaId
     */
    static changeBaxTypeByCity(areaId) {
        areaId in [110000L, 330100L] ? '3' : 'T'
    }
    /**
     * @param context
     * 处理燃油类型
     */
    static getFuelType(context) {
        def type = getObjectByCityCode(context.area, _FUEL_TYPE_MAPPING)
        type[context.vehicleInfo.fuelType] as String
    }

    /**
     * 转保验证码补充参数处理
     */
    static getSupplementCaptchaImageType(context, checkCode, isTrafficQuestion) {
        def imageType = isTrafficQuestion == 'Y' ? _SUPPLEMENT_INFO_COMPULSORY_CAPTCHA_IMAGE_TEMPLATE_QUOTING : _SUPPLEMENT_INFO_COMMERCIAL_CAPTCHA_IMAGE_TEMPLATE_QUOTING
        context.needSupplementInfos = []
        context.needSupplementInfos << mergeMaps(imageType, [meta: [imageData: checkCode, orderNo: context.order.orderNo]])
    }

    static getAddress(deliveryAddress) {
        if(deliveryAddress?.street) {
            ([
                deliveryAddress?.provinceName,
                deliveryAddress?.cityName,
                deliveryAddress?.districtName,
                deliveryAddress.street,
            ] - null).inject { p, c -> p + c }
        } else {
            '北京市东城区大区灯胡同2号'
        }
    }

}
