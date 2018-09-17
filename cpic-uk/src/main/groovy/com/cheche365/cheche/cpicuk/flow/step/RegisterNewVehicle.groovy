package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 新车备案
 * flow  -> maybe 报价失败->提示未备案->备案->报价  现在还没找到车测试
 * create by yujt 2018-05-31
 * source from issue.cpic.com.cn/ecar/viewdiverjs/portal/car_insurance/dialogTemplet
 * line 1001 : newCarSubmit
 */
@Component
@Slf4j
class RegisterNewVehicle implements IStep {

    private static final _API_PATH_REGISTER_NEW_VEHICLE = '/ecar/ecar/registerNewVehicle'

    @Override
    run(context) {
        RESTClient client = context.client
        def auto = context.auto
        def selectedCarModel = context.selectedCarModel
        def supplementInfo = context.additionalParameters.supplementInfo
        def emptyWeight = (selectedCarModel?.fullWeight ?: 0 as int) / 1000
        // TODO 如果前面的接口拿不到信息，request body 中的 数据需要从补充信息中获取
        // ### Body中的参数都是必传项 ###
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_REGISTER_NEW_VEHICLE,
            body              : [
                meta  : [:],
                redata: [
                    engineNo           : auto.engineNo, //发动机号
                    carVIN             : auto.vinNo, // 车架号
                    ownerName          : auto.owner, // 行驶证车主
                    certType           : CERT_TYPE.身份证, //车主证件类型 1 身份证
                    "certNo"           : auto.identity, //车主证件号码
                    "vehStyle"         : REGISTER_NEW_VEHICLE_STYLE.客车, // 车辆类型,
                    "lmtLoadPerson"    : selectedCarModel?.seatCount ?: '0', // 核定载客量
                    "tonnage"          : supplementInfo?.tonnage ?: '0', //核定载质量(吨)  核定载质量大于0时，车辆类型不得为“客车类”
                    "emptyWeight"      : emptyWeight,
                    "displacement"     : selectedCarModel?.displacement ?: '0',
                    "fuelType"         : FUEL_TYPE.汽油,  // 暂都设置为汽油
                    "certificateType"  : supplementInfo?.certificateType ?: CERTIFICATE_TYPE.销售发票,  // 车辆来历凭证种类
                    "certificateNo"    : supplementInfo?.certificateNo ?: 'NOFOUNDTHECERTIFICATENO',  // 车辆来历凭证编号
                    "stCertificateDate": supplementInfo?.stCertificateDate ?: _DATE_FORMAT3.format(new Date()) // 开具车辆来历凭证所载日期 2018-05-31
                ]
            ]
        ]

        log.debug '新车备案 ：{}', args.toString()
        def result = client.post args, { resp, json ->
            json
        }

        if ('success' == result.message?.code) {
            log.debug '新车备案成功 ：{}', result
            getContinueFSRV result
        } else {
            log.debug '新车备案失败 ：{}', result
            def errorMsg = result.message?.message ?: '新车备案失败'
            getKnownReasonErrorFSRV errorMsg
        }

    }

    /**
     * 新车备案车辆类型
     */
    static REGISTER_NEW_VEHICLE_STYLE = [
        '半挂车'   : 'B11',
        '电车'    : 'D11',
        '全挂车'   : 'G11',
        '货车'    : 'H11',
        '重型罐式货车': 'H14',
        '重型集装厢车': 'H16',
        '普通低速货车': 'H51',
        '轮式装载机械': 'J11',
        '客车'    : 'K11',
        '摩托车'   : 'M11',
        '三轮运输车' : 'N11',
        '牵引车'   : 'Q11',
        '拖拉机'   : 'T11',
        '专项作业车' : 'Z11'
    ]

    /**
     * 新车备案能源类型
     */
    static FUEL_TYPE = [
        '汽油'   : 'A',
        '柴油'   : 'B',
        '电'    : 'C',
        '混合油'  : 'D',
        '天然气'  : 'E',
        '液化石油气': 'F',
        '甲醇'   : 'L',
        '乙醇'   : 'M',
        '太阳能'  : 'N',
        '混合动力' : 'O',
        '无'    : 'Y',
        '其他'   : 'Z',
    ]

    /**
     * 车主证件类型
     */
    static CERT_TYPE = [
        '身份证'        : '1',
        '护照'         : '2',
        '军官证'        : '3',
        '社保证'        : '4',
        '组织机构代码'     : '6',
        '税务登记证'      : '8',
        '企业代码'       : '9',
        '法人证书'       : '10',
        '营业执照'       : '11',
        '军官退休证'      : '12',
        '台胞证'        : '13',
        '内部编码'       : '14',
        '港澳居民来往内地通行证': '15',
        '外国人永久居留身份证' : '16'
    ]

    /**
     * 车辆来历凭证种类
     */
    static CERTIFICATE_TYPE = [
        '销售发票'            : '01',
        '法院调解书'           : '02',
        '法院裁定书'           : '03',
        '法院判决书'           : '04',
        '仲裁裁决书'           : '05',
        '相关文书（继承、赠予、协议抵债）': '06',
        '批准文件'            : '07',
        '调拨证明'            : '08',
        '修理发票'            : '09',
    ]
}
