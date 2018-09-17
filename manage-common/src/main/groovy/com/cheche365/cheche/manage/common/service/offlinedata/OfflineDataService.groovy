package com.cheche365.cheche.manage.common.service.offlinedata

import com.cheche365.cheche.bihu.service.BihuInsuranceInfoService
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository
import com.cheche365.cheche.core.repository.InsuranceInfoRepository
import com.cheche365.cheche.core.repository.VehicleLicenseRepository
import com.cheche365.cheche.core.service.IInsuranceInfoService
import com.cheche365.cheche.manage.common.model.InsuranceOfflineDataModel
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

import java.text.SimpleDateFormat

import static com.cheche365.cheche.common.util.DateUtils.getLocalDateTime
import static com.cheche365.cheche.core.constants.WebConstants.CHANNEL_SERVICE_ITEMS
import static com.cheche365.cheche.core.model.AutoVehicleLicenseServiceItem.Enum.BIHU_5
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import static java.time.ZoneId.systemDefault
import static java.time.temporal.ChronoUnit.SECONDS

/**
 * 线下数据服务
 * Created by suyaqiang on 2017/9/19.
 */
@Slf4j
@Service
class OfflineDataService implements IOfflineDataService {

    private IInsuranceInfoService insuranceInfoService
    private VehicleLicenseRepository vehicleLicenseRepo
    private InsuranceInfoRepository insuranceInfoRepository
    private MongoTemplate mongoTemplate
    private IOfflineDataCheckService offlineDataCheckService
    private StringRedisTemplate redisTemplate
    private IOfflineDataInsuranceInfoService offlineDataInsuranceInfoService
    private InsuranceCompanyRepository insuranceCompanyRepository
    static Map icMap = null

    OfflineDataService(
        BihuInsuranceInfoService insuranceInfoService,
        VehicleLicenseRepository vehicleLicenseRepo,
        InsuranceInfoRepository insuranceInfoRepository,
        MongoTemplate mongoTemplate,
        IOfflineDataCheckService offlineDataCheckService,
        StringRedisTemplate redisTemplate,
        IOfflineDataInsuranceInfoService offlineDataInsuranceInfoService,
        InsuranceCompanyRepository insuranceCompanyRepository
    ) {
        this.insuranceInfoService = insuranceInfoService
        this.vehicleLicenseRepo = vehicleLicenseRepo
        this.insuranceInfoRepository = insuranceInfoRepository
        this.mongoTemplate = mongoTemplate
        this.offlineDataCheckService = offlineDataCheckService
        this.redisTemplate = redisTemplate
        this.offlineDataInsuranceInfoService = offlineDataInsuranceInfoService
        this.insuranceCompanyRepository = insuranceCompanyRepository
    }

    @Override
    void exportDataAsync(List<InsuranceOfflineDataModel> lines, Map options) {
        throw new UnsupportedOperationException('不支持异步调用')
//        startExportTask lines, options
//        log.info '线下数据导出任务启动成功'
    }

    @Override
    Map check(Collection<InsuranceOfflineDataModel> lines, Map options) {
        log.debug '原始数据大小：{}', lines.size()
        def checkResult = offlineDataCheckService.check lines, options
        log.info '线下数据检查结果：{}', checkResult.code
//        if (1 == checkResult.code) {
//            log.debug '线下数据有误：{}', checkResult.data.invalidRecords
//            redisTemplate.opsForList().leftPush(InsuranceImportResultMessage.QUEUE_LIST, serialize(checkResult))
//            log.debug '线下数据有误，将检查结果放入队列完成'
//        }

        checkResult
    }

    @Override
    Map exportData(Collection<InsuranceOfflineDataModel> lines, Map options) {
        log.debug '原始数据大小：{}', lines.size()
        def checkResult = this.check lines, options

        log.info '线下数据检查完成，准备组装数据'
        def validRecords = checkResult.data.validRecords

        def failedRecords = [] // 失败的数据列表
        def bundledObjects = validRecords.collect { line ->
            def domainObjects
            try {
                domainObjects = getDomainObjects(line, options)
            } catch (e) {
                log.error "${line.licenseNo}生成域模型对象失败", e
                failedRecords << line // 将失败的数据放入列表中
                return null
            }
            log.debug 'licenseNo:{} -> domainObjects完成', domainObjects.auto.licensePlateNo
            domainObjects
        } - null

        log.info '线下数据获取信息全部完成。但失败的车辆：{}', failedRecords, failedRecords.collect { it?.auto?.licensePlateNo }
        def exportResult = [
            code: checkResult.code,
            id  : "${options.id}_0".toString(),
            data: [
                invalidRecords: checkResult.data.invalidRecords,
                domainObjects : bundledObjects,
                failedRecords : failedRecords
            ]
        ]
        log.debug '线下数据导出完成'

        exportResult
    }

    private void startExportTask(List<InsuranceOfflineDataModel> lines, Map options) {
        new Thread({
            this.exportData(lines, options)
        }).start()
    }

    private getDomainObjects(line, options) {
        def insureDate = getInsureDate(line.createTime)
        def totalPremium = toDouble(line.totalPremium)
        def compulsory = toDouble(line.compulsory)
        def autoTax = toDouble(line.autoTax)
        def premium = toDouble(line.premium)

        def upCommercialRebate = toDouble(line.upCommercialRebate)
        def upCompulsoryRebate = toDouble(line.upCompulsoryRebate)
        def upCommercialAmount = toDouble(line.upCommercialAmount)
        def upCompulsoryAmount = toDouble(line.upCompulsoryAmount)
        def downCommercialRebate = toDouble(line.downCommercialRebate)
        def downCompulsoryRebate = toDouble(line.downCompulsoryRebate)
        def downCommercialAmount = toDouble(line.downCommercialAmount)
        def downCompulsoryAmount = toDouble(line.downCompulsoryAmount)

        def agentName = line.agentName
        def agentIdentity = line.agentIdentity

        def auto = new Auto(licensePlateNo: line.licenseNo, owner: line.owner)

        InsuranceInfo insuranceInfo = options.enableBihuService ? getBihuInsuranceInfo(line.area, auto) : null
        log.info 'licenseNo:{} -> insuranceInfo:{}', line.licenseNo, insuranceInfo
        def vl = insuranceInfo?.vehicleLicense

        def mongoInfo = mongoTemplate.findOne(Query.query(Criteria.where('UserInfo.LicenseNo').is(line.licenseNo)), Map, 'bihu_insurance_info')
        def userInfo = mongoInfo?.UserInfo
        def (biStartDate, biEndDate, ciStartDate, ciEndDate) = getInsuranceDate(userInfo, options)
        def insuranceCompany = getIcByName(line.insuranceCompanyName, options.exInsuranceCompanies, insuranceCompanyRepository)
        def paymentChannel = null // getPaymentChannelByName(line.payChannel, options.exPaymentChannels)

        [
            rawData            : line,
            insuranceCompany   : insuranceCompany,
            auto               : new Auto(owner: line.owner, licensePlateNo: line.licenseNo, engineNo: line.engineNo ?: vl?.engineNo, vinNo: line.vinNo ?: vl?.vinNo, enrollDate: vl?.enrollDate),
            purchaseOrder      : new PurchaseOrder(createTime: insureDate, payableAmount: totalPremium, paidAmount: totalPremium, area: line.area, giftId: null),
            payment            : new Payment(amount: totalPremium, paymentType: PaymentType.Enum.INITIALPAYMENT_1, channel: paymentChannel),
            insurance          : premium ? new Insurance(policyNo: line.policyNo ?: userInfo?.BizNo, premium: premium, insuredName: userInfo?.InsuredName, insuredIdNo: userInfo?.InsuredIdCard, insuredMobile: userInfo?.InsuredMobile, effectiveDate: biStartDate, expireDate: biEndDate) : null,
            compulsoryInsurance: compulsory || autoTax ? new CompulsoryInsurance(policyNo: line.policyNo ?: userInfo?.ForceNo, autoTax: autoTax, compulsoryPremium: compulsory, effectiveDate: ciEndDate, expireDate: ciStartDate) : null,
            rebate             : new InsurancePurchaseOrderRebate(
                upCommercialAmount: upCommercialAmount == null ? 0.00d : upCommercialAmount,
                upCompulsoryAmount: upCompulsoryAmount == null ? 0.00d : upCompulsoryAmount,
                upCommercialRebate: upCommercialRebate == null ? 0.00d : upCommercialRebate,
                upCompulsoryRebate: upCompulsoryRebate == null ? 0.00d : upCompulsoryRebate,
                downCommercialRebate: downCommercialRebate == null ? 0.00d : downCommercialRebate,
                downCompulsoryRebate: downCompulsoryRebate == null ? 0.00d : downCompulsoryRebate,
                downCommercialAmount: downCommercialAmount == null ? 0.00d : downCommercialAmount,
                downCompulsoryAmount: downCompulsoryAmount == null ? 0.00d : downCompulsoryAmount
            ),
            institution        : new Institution(name: line.institution),
            insurancePackage   : insuranceInfo?.insuranceBasicInfo?.insurancePackage,
            agent              : new Agent(name: agentName, identity: agentIdentity)
        ]
    }

    private getBihuInsuranceInfo(area, auto) {
        this.vehicleLicenseRepo.findFirstByLicensePlateNoAndOwner(auto.licensePlateNo, auto.owner).with { internalVehicleLicense ->
            log.debug 'internalVehicleLicense：{} -> {}', auto.licensePlateNo, internalVehicleLicense
            def internalInsuranceInfo = insuranceInfoRepository.findFirstByVehicleLicense internalVehicleLicense
            if (!internalInsuranceInfo) {
                log.debug 'DB未获取到保险信息'
                InsuranceInfo insuranceInfo = insuranceInfoService.getInsuranceInfo(area, auto, [
                    (CHANNEL_SERVICE_ITEMS): [BIHU_5],
                    asyncResultHandler     : { msg ->
                        log.debug '收到阶段性结果：', msg
                    }])
                log.debug 'bihu获取车辆信息结果，{}', insuranceInfo

                if (insuranceInfo) {
                    offlineDataInsuranceInfoService.saveInsuranceInfo insuranceInfo
                }
            } else {
                log.debug '从DB获取到保险信息'
                internalInsuranceInfo
            }
        }
    }

    // 获取商业和交强的起保日期
    private static getInsuranceDate(userInfo, options) {
        def df = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
        def biEndDate = userInfo?.BusinessExpireDate ? df.parse(userInfo.BusinessExpireDate) : null
        def ciEndDate = userInfo?.ForceExpireDate ? df.parse(userInfo.ForceExpireDate) : null
        def biStartDate = biEndDate ? Date.from(getLocalDateTime(biEndDate).plusYears(-1).plus(1, SECONDS).toLocalDateTime().atZone(systemDefault()).toInstant()) : null
        def ciStartDate = ciEndDate ? Date.from(getLocalDateTime(ciEndDate).plusYears(-1).plus(1, SECONDS).toLocalDateTime().atZone(systemDefault()).toInstant()) : null

        if (options.biStartDate) {
            if (options.biStartDate > biStartDate) { // 无效的起保日期
                biEndDate = null
                ciEndDate = null
                biStartDate = null
                ciStartDate = null
            }
        }
        [biStartDate, biEndDate, ciStartDate, ciEndDate]
    }

    /**
     * 保险公司映射
     * @param externalIcs
     * @return
     */
    static getIcMapping(externalIcs, InsuranceCompanyRepository insuranceCompanyRepository) {
        if (icMap) {
            return icMap
        } else {
            icMap = insuranceCompanyRepository.findAll().collectEntries {
                [(it.name.trim()): it]
            } << (externalIcs ?: [:])
            return icMap
        }
    }

    /**
     * 支付方式映射
     * @param externalPaymentChannels
     * @return
     */
    static getPaymentChannelMapping(externalPaymentChannels) { // TODO
        ([
            '转支付宝/转公户': PaymentChannel.Enum.ALIPAY_1,
        ] << (externalPaymentChannels ?: [:]))
    }

    static getInsureDate(date) {
        if (date instanceof Date) {
            return date
        }
        if ((date =~ /\d{4}\/\d{1,2}\/\d{1,2}/).matches()) {
            new SimpleDateFormat('yyyy/MM/dd').parse(date)
        } else if ((date =~ /\d{4}-\d{1,2}-\d{1,2}/).matches()) {
            new SimpleDateFormat('yyyy-MM-dd').parse(date)
        }
    }

    private static getIcByName(name, externalIcs, insuranceCompanyRepository) {
        getIcMapping(externalIcs, insuranceCompanyRepository).get(name)
    }

    private static getPaymentChannelByName(name, externalPaymentChannels) {
        getPaymentChannelMapping(externalPaymentChannels).get(name)
    }

    private static serialize(pojo) {
        def mapper = new ObjectMapper()
        mapper.setDateFormat(new SimpleDateFormat('yyyy-MM-dd HH:mm:ss'))
        mapper.setSerializationInclusion(NON_NULL)
        mapper.writeValueAsString(pojo)
    }

    private static toDouble(str) {
        null == str || '' == str ? null : Double.valueOf(str)
    }

}
