package com.cheche365.cheche.piccuk.util

import com.cheche365.cheche.core.model.InsurancePackage
import groovy.util.logging.Slf4j
import groovy.xml.StreamingMarkupBuilder
import org.apache.commons.codec.digest.DigestUtils

import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder

import static com.cheche365.cheche.common.util.XmlUtils.mapToXml
import static com.cheche365.cheche.common.util.XmlUtils.xmlToMap
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT5
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT
import static com.cheche365.cheche.parser.Constants._DRIVER_IOP
import static com.cheche365.cheche.parser.Constants._ENGINE
import static com.cheche365.cheche.parser.Constants._ENGINE_IOP
import static com.cheche365.cheche.parser.Constants._GLASS
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT
import static com.cheche365.cheche.parser.Constants._PASSENGER_IOP
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT
import static com.cheche365.cheche.parser.Constants._SCRATCH_IOP
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS
import static com.cheche365.cheche.parser.Constants._THEFT
import static com.cheche365.cheche.parser.Constants._THEFT_IOP
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_IOP
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted



/**
 * 业务相关工具类
 */
@Slf4j
class BusinessUtils {

    static getEffectiveAdvice(adjustAdvices, advices) {
        adjustAdvices(advices)
    }

    static final _GET_EFFECTIVE_ADVICES = { advices ->
        def m = advices =~ /.*不通过原因为：(.*)/
        def m1 = advices =~ /.*不满足自动核保规则，转人工核保.*/
        if (m.find()) {
            m.collect { advice ->
                advice[1].split(';')
            }.flatten()
        } else if (m1.find()) {
            ['不满足自动核保规则，转人工核保']
        } else {
            ['自动核保失败']
        }
    }


    static getAllBaseKindItems(context, convertersConfig) {
        convertersConfig.collectEntries { outerKindCode, innerKindCode, _2, extConfig, _4, _5 ->
            [
                (outerKindCode): [
                    amountList: extConfig,
                    amount    : context.carActualValue,
                ]
            ]
        }
    }

    /**
     * 获取报价后商业险的所有险种结果
     * @param itemKindList 报价返回结果中商业险List
     * @return
     */
    static getAllKindItems(itemKindList) {
        itemKindList.collectEntries { itemKind ->
            [
                (itemKind.kindCode): [
                    amount    : itemKind.amount,
                    unitAmount: itemKind.unitAmount,
                    premium   : itemKind.premium,
                    modeCode  : itemKind.modeCode
                ]
            ]
        }
    }

    static getEnrollDateText(context) {
        _DATE_FORMAT3.format getEnrollDate(context)
    }

    static getEnrollDate(context) {
        if (context.carInfo?.enrollDate) {
            new Date(context.carInfo.enrollDate.time as long)
        } else if (context.auto.enrollDate) {
            context.auto.enrollDate
        } else {
            new Date()
        }
    }

    private static final _KIND_CODE_INSURANCE_PACKAGE_MAPPINGS = [
        ['050602', _THIRD_PARTY_AMOUNT, _THIRD_PARTY_IOP, false], // 第三者责任险
        ['050711', _DRIVER_AMOUNT, _DRIVER_IOP, false], // 车上人员责任险-司机
        ['050712', _PASSENGER_AMOUNT, _PASSENGER_IOP, false, false,
         { passengerCount, amount -> amount / passengerCount }
        ], // 车上人员责任险-乘客
        ['050211', _SCRATCH_AMOUNT, _SCRATCH_IOP, false], // 车身划痕损失险
        ['050202', _DAMAGE, _DAMAGE_IOP, false], // 机动车辆损失险
        ['050501', _THEFT, _THEFT_IOP, false], // 盗抢险
        ['050311', _SPONTANEOUS_LOSS, null, false], // 自燃损失险
        ['050461', _ENGINE, _ENGINE_IOP, true], // 发动机特别损失险
        ['050232', _GLASS, null, true, true], // 玻璃险
    ]

    /**
     * 获取续保套餐
     */
    static generateRenewalPackage(context, allItemKinds) {
        def renewalPackage = new InsurancePackage()
        renewalPackage.compulsory = true
        renewalPackage.autoTax = true

        _KIND_CODE_INSURANCE_PACKAGE_MAPPINGS.each { kindCode, propName, iopName,
                                                     isBoolean, isGlass = false, amountConverter = null ->
            def item = allItemKinds[kindCode]
            def amount = item?.amount ? (item?.amount as double) : 0
            renewalPackage[propName] = isBoolean ? (item?.chooseFlag ?: false)
                : (item?.chooseFlag ? (amountConverter?.curry(getCarSeat(context) - 1)?.call(amount))
                ?: amount
                : 0)
            if (iopName) {
                renewalPackage[iopName] = item?.specialFlag ?: false
            }
            if (isGlass && item?.chooseFlag) {
                renewalPackage.glassType = (10 == item?.modeCode ? DOMESTIC_1 : IMPORT_2)
            }
        }

        renewalPackage
    }

    static getCarSeat(context) {
        def seatCount = context.selectedCarModel?.vehicleSeat ?: context.carInfo?.seatCount ?: 5
        seatCount <= 9 ? seatCount : 9
    }

    /**
     * 01：只投交强险和车船税，10：只投商业险，11投商业险和交强险
     * @param context
     * @return
     */
    static getBICIFlag(context) {
        def biQuoted = isCommercialQuoted context.accurateInsurancePackage
        def ciQuoted = isCompulsoryOrAutoTaxQuoted context.accurateInsurancePackage

        biQuoted && ciQuoted ? '11' : biQuoted ? '10' : '01'
    }

    /**
     * 车保易需要进行比价
     * 1.现在比价所需要的参数都是String类型
     * 2.所以此处把排量更改为String
     * 3.因人保uk返回的排量是1.598 所以还需要四舍五入
     * @param vehicles
     * @return
     */
    static changeExhaust(vehicles) {
        vehicles.collect { v ->
            v.vehicleExhaust ? (v.vehicleExhaust.setScale(1, BigDecimal.ROUND_UP) as String ):[:]
            v
        }
    }

    /**
     * 在已有的xmlStr上追加本次新上传的照片
     * @param context
     * @param xmlStrHad
     */
    static getXmlStrOld(context) {
        def xmlStrOld = context.xmlStrOld
        def oldMap = xmlToMap(xmlStrOld)
        def now = _DATE_FORMAT5.format(new Date())
        def batchID = oldMap.doc.DocInfo.BATCH_ID
        def interVer = (oldMap.doc.DocInfo.INTER_VER as int) + 1
        oldMap.doc.DocInfo.BATCH_VER = interVer
        oldMap.doc.DocInfo.INTER_VER = interVer
        oldMap.doc.DocInfo.MOD_DATE = context.modifyTime
        context.batchID = batchID
        context.interVer = interVer
        def PAGES = oldMap.doc.PageInfo.PAGE

        def pageUnit = PAGES instanceof Map ? PAGES : PAGES instanceof List ? PAGES[0] : null
        StringBuffer pageStr = new StringBuffer()
        StringBuffer leafStr = new StringBuffer()
        context.additionalParameters?.supplementInfo?.images.withIndex().collect { imgUrl, index ->
            def fileName = imgUrl.split('/')[-1]
            def imgName = fileName.split('\\.')[0]
            def md5 = getFileMd5(imgUrl)
            def pageContentMap = pageUnit?.clone().with {
                CREATE_TIME = now
                MODIFY_TIME = context.modifyTime
                PAGE_URL = fileName
                THUM_URL = fileName + '.jpg'
                IS_LOCAL = '1'
                ORGINAL_NAME = PAGE_DESC = imgName
                PAGE_CRC = md5
                PAGE_VER = context.interVer
                it
            }
            def singlePageMapToXml0 = mapToXml([PAGE: pageContentMap])
            //加入pageid属性
            def pageID = UUID.randomUUID().toString()
            def singlePageMapToXml = new StringBuffer(singlePageMapToXml0).insert(5, ' PAGEID=\"' + pageID + '\"').toString()
            pageStr.append singlePageMapToXml
            leafStr.append('<LEAF>' + pageID + '</LEAF>')
        }
        //将新照片xml拼接到原xml中
        //需要将部分属性值更改
        def rep1 = xmlStrOld.substring(xmlStrOld.indexOf('<BATCH_VER>'), xmlStrOld.indexOf('</BATCH_VER>'))
        def rep2 = xmlStrOld.substring(xmlStrOld.indexOf('<MOD_DATE>'), xmlStrOld.indexOf('</MOD_DATE>'))
        def rep3 = xmlStrOld.substring(xmlStrOld.indexOf('<INTER_VER>'), xmlStrOld.indexOf('</INTER_VER>'))
        def oldXml1 = xmlStrOld.replace(rep1, '<BATCH_VER>' + context.interVer).replace(rep2, '<MOD_DATE>' + context.modifyTime).replace(rep3, '<INTER_VER>' + context.interVer)
        def oldXmlBuffer = new StringBuffer(oldXml1)
        oldXmlBuffer.insert(oldXmlBuffer.indexOf('</PageInfo>'), pageStr).insert(oldXmlBuffer.indexOf('</NODE>'), leafStr)
        def newXml = oldXmlBuffer.toString()
        newXml
    }

    /**
     * @return 商业险影像和客户资料的xmlStr的map
     */
    static getXmlStrNew(context) {
        def now = _DATE_FORMAT5.format(new Date())
        context.interVer = '1'
        def xmlStrMap = originXmlStrMap(context, now)
        def simpleXmlStr = mapToXml(xmlStrMap)
        //需要补齐属性
        def doc = new XmlSlurper().parseText(simpleXmlStr)
        def batchID = getUUID32()
        doc.DocInfo.BATCH_ID = batchID
        context.batchID = batchID
        def pageAmout = xmlStrMap.doc.PageInfo.PAGE.size()
        pageAmout.times {
            def pageID = UUID.randomUUID().toString()
            doc.PageInfo.PAGE[it].@PAGEID = pageID
            doc.VTree.NODE.LEAF[it] = pageID
        }
        doc.VTree.NODE.@ID = 'prpolpenbd'
        doc.VTree.NODE.@NAME = '保单批单相关资料'
        doc.Property.Extend.@def_code = 'customerID'
        doc.Property.Extend.@def_name = '投保客户号'
        doc.Property.Extend.@disp_flag = '0'

        def markerpBuilder = new StreamingMarkupBuilder()
        markerpBuilder.setEncoding('gbk')
        def xmlStr0 = markerpBuilder.bind {
            mkp.yield doc
        }

        def xmlStr = ('<?xml version="1.0" encoding="GBK"?>' + (xmlStr0 as String)).replaceAll("'", '"')
        xmlStr
    }

    static originXmlStrMap(context, now) {
        def pageElements = generatePageElement(context)
        //无图片的xmlStr的map形式
        [
            doc: [
                DocInfo : [
                    BATCH_ID: UUID.randomUUID(), BATCH_VER: '1', INTER_VER: '1', BUSI_NO: context.sunECMBusinessNo, BUSI_NAME: '投保单号', APP_CODE: 'prpol', STATUS: '1', CREATE_USER: 'A410100744', CREATE_DATE: now, MOD_USER: 'A410100744', MOD_DATE: context.modifyTime
                ],
                PageInfo: [PAGE: pageElements.pages],
                VTree   : [
                    NODE: [LEAF: pageElements.leafs],
                ],
                Property: [
                    Extend: context.ECMCustomerID
                ]
            ]
        ]


    }

    //为每一张影像生成一个<page>
    static generatePageElement(context) {
        def createTime = _DATE_FORMAT5.format(new Date())
        def pageTemplate = [
            CREATE_USER : 'A410100744',
            CREATE_TIME : createTime,
            MODIFY_USER : 'A410100744',
            MODIFY_TIME : context?.modifyTime ?: createTime,
            PAGE_URL    : '',
            THUM_URL    : '',
            IS_LOCAL    : '1',
            ORGINAL_NAME: '01',
            PAGE_VER    : context.interVer,
            PAGE_DESC   : '01',
            PAGE_CRC    : '',
            PAGE_FORMAT : 'image/jpg',
            PAGE_ENCRYPT: '0',
            EXT_INFO    : null,
            ISPS        : null,
            ISMOD       : null,
            ISPRI       : null
        ]

        def images = context.additionalParameters?.supplementInfo?.images
        def pages = []
        def leafs = []
        images.withIndex().collect { imgUrl, index ->
            def fileName = imgUrl.split('/')[-1]
            def imgName = fileName.split('\\.')[0]
            def md5 = getFileMd5(imgUrl)
            pages[index] = pageTemplate.clone().with {
                it.PAGE_URL = fileName
                it.THUM_URL = fileName + '.jpg'
                it.ORGINAL_NAME = it.PAGE_DESC = imgName
                it.PAGE_CRC = md5
                it
            }
            leafs[index] = ''
        }
        [
            pages: pages,
            leafs: leafs
        ]
    }


    private static final getFileMd5(imgUrl) {
        def file = new URL(imgUrl).withInputStream { is ->
            File.createTempFile('cpicuk-upload-image', '.jpg').with { tmpFile ->
                tmpFile.withOutputStream { os ->
                    os << is
                }
                tmpFile
            }
        }
        //将file文件内容转成字符串
        BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file), 'UTF-8'))
        DigestUtils.md5Hex(bf.toString()) toUpperCase()
    }

    private static getUUID32() {
        UUID.randomUUID().toString().replaceAll('-', '')
    }

    /**
     * 查询支付结果-获取支付类型对应编号
     * @param payTypeName
     * @return
     */
    static getPayTypeNo(payTypeName) {
        def payTypeNo
        switch (payTypeName) {
            case 'wechat': payTypeNo = '9'; break
            case 'wechatpublic': payTypeNo = '51'; break
            default: payTypeNo = '9'
        }
        payTypeNo
    }


    static getPiccFormatter() {
        new DateTimeFormatterBuilder().appendPattern('yyyy-M-d HH:mm:ss').toFormatter()
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
     *
     * @param id input 的 id 集合
     * @param renewalBaseInfo  返回的html
     * @return 【id:value，
     *           id2：value2】
     */
    static getValueById(id, renewalBaseInfo) {
        def allInfo = [:]
        renewalBaseInfo.findAll { input ->
            input.@id in id ? allInfo.put(input.@id, input.@value) : ''
        }
        allInfo
    }

    static getProposalNoForBICI(proposalNos) {
        def ciNo = ''
        def biNo = ''
        if (proposalNos && proposalNos.size() > 0) {
            def proposalNoList = proposalNos.first()
            if (proposalNoList && !proposalNoList.isEmpty()) {
                proposalNoList.each { it ->
                    if (it.contains('TDZA')) {
                        ciNo = it
                    }
                    if (it.contains('TDAA')) {
                        biNo = it
                    }
                }
            }
        }
        new Tuple2(ciNo, biNo)
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
            '北京市东城区大取灯胡同2号'
        }
    }

}
