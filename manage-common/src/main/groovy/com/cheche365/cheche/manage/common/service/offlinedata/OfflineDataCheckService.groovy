package com.cheche365.cheche.manage.common.service.offlinedata

import com.cheche365.cheche.core.repository.InsuranceCompanyRepository
import com.cheche365.cheche.core.service.AutoService
import com.cheche365.cheche.core.util.AutoUtils
import com.cheche365.cheche.manage.common.model.InsuranceOfflineDataModel
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import static com.cheche365.cheche.manage.common.service.offlinedata.OfflineDataService.getIcMapping
import static com.cheche365.cheche.manage.common.service.offlinedata.OfflineDataService.getInsureDate
import static groovyx.gpars.GParsExecutorsPool.withPool

/**
 * 线下数据检查服务实现
 * Created by suyaqiang on 2017/9/19.
 */
@Slf4j
@Service
class OfflineDataCheckService implements IOfflineDataCheckService {

    private AutoService autoService
    private InsuranceCompanyRepository insuranceCompanyRepository

    OfflineDataCheckService(AutoService autoService, InsuranceCompanyRepository insuranceCompanyRepository) {
        this.autoService = autoService
        this.insuranceCompanyRepository = insuranceCompanyRepository
    }

    @Override
    Map check(Collection<InsuranceOfflineDataModel> lines, Map options) {
        def invalidResults = [:]
        def normalizedLines = lines

        log.info '检查字段'
        // 字段检查
        getInvalidFieldLine(normalizedLines, getFiledBarriers(options, insuranceCompanyRepository), invalidResults)

        log.info '检查车牌号'
        normalizedLines.findAll {
            !it.licenseNo
        }.with {
            it ? invalidResults.invalidLicenseNo = it : null
        }

//        log.info '检查车牌号重复'
//        getLicenseNoDuplicatedLines(normalizedLines).with {
//            it ? invalidResults.licenseNoDuplicated = it : null
//        }
//
//        log.info '检查保单号重复'
//        getPolicyNoDuplicatedLines(normalizedLines).with {
//            it ? invalidResults.policyNoDuplicated = it : null
//        }

        log.info '根据提供的日期范围：[options.startInsuredDate--options.endInsuredDate]检查投保日期'
        getInvalidInsuredDateLines(
                normalizedLines.findAll {
                    !(it.createTime in invalidResults.errorDate.collect { dateLine -> dateLine.createTime })
                },
                options.startInsuredDate,
                options.endInsuredDate).with {
            it ? invalidResults.invalidInsuredDate = it : null
        }

        def invalidLines = invalidResults.inject([]) { invalidPolicyNos, currLines ->
            if ('invalidData' != currLines.key) {
                invalidPolicyNos.addAll(currLines.value.collect {
                    it.policyNo
                })
            }
            invalidPolicyNos
        }
        log.info '线下数据检查，信息有误的保单：{}', invalidLines
        def validRecords = normalizedLines.findAll {
            !(it.policyNo in invalidLines)
        }

        def checkResult = [
                id  : options.id,
                code: invalidLines ? 1 : 0,
                data: [
                        validRecords  : validRecords,
                        invalidRecords: invalidResults
                ]
        ]
        log.info '线下数据检查结果：{}', checkResult.code

        checkResult
    }

    private normalizeLines(checkingLines) {
        withPool(12) {
            checkingLines.withIndex().collectParallel { line, idx ->
                washLine line
                // 根据车牌号获取车辆地区
                line.area = line.licenseNo && line.licenseNo.size() > 1 ? AutoUtils.getAreaOfAuto(line.licenseNo) : null
                log.debug 'line --> {}', idx
                line
            }
        }
    }

    // TODO
    // 整理数据格式
    private static washLine(InsuranceOfflineDataModel line) {
        [
                'totalPremium', 'compulsory', 'autoTax', 'premium',
                'upCommercialAmount', 'upCompulsoryAmount', 'downCommercialAmount', 'downCompulsoryAmount'
        ].each { // 数字格式的字段去除一些特殊字符
            line[it] = line[it] ? line[it].trim().replace('"', '').replace(',', '') : line[it]
        }
    }

    /**
     * 检查字段策略：
     * 一个长度为4的list ： [错误类型, 要检查的字段, 检查规则(false:检查未通过， true:检查通过), 描述]
     * @param options
     * @return
     */
    private static getFiledBarriers(options, insuranceCompanyRepository) {
        [
                [
                        'invalidInsuranceCompany',
                        ['insuranceCompanyName'],
                        {
                            it in getIcMapping(options.exInsuranceCompanies, insuranceCompanyRepository).keySet()
                        },
                        '未识别的保险公司'
                ],
//            [
//                'invalidPaymentChannel',
//                ['payChannel'],
//                {
//                    it in getPaymentChannelMapping(options.exPaymentChannels).keySet()
//                },
//                '未识别的支付方式'
//            ],
                [
                        'errorDate',
                        ['createTime'],
                        {
                            it instanceof Date ? true : (it =~ /\d{4}\/\d{1,2}\/\d{1,2}/).matches() || (it =~ /\d{4}-\d{1,2}-\d{1,2}/).matches()
                        },
                        '非法的日期'
                ]
//                ,
//                [
//                        'invalidAmount',
//                        ['totalPremium', 'compulsory', 'autoTax', 'premium'],
//                        {
//                            try {
//                                null == it || '' == it
//                            } catch (e) {
//                                false
//                            }
//                        },
//                        '非法的金额'
//                ]
        ]
    }

    /**
     * 检查字段
     * @param results
     * @param barriers
     * @param invalidResults
     * @return
     */
    private static getInvalidFieldLine(results, barriers, invalidResults) {
        results.each { line ->
            barriers.each { barrier ->
                barrier[1].any { field ->
                    if (!barrier[2].call(line[field])) { // 检查未通过
                        if (!invalidResults[barrier[0]]) {
                            invalidResults[barrier[0]] = []
                        }
                        invalidResults[barrier[0]] << line
                        true
                    }
                }
            }
        }
    }

    private static getLicenseNoDuplicatedLines(results) {
        results.groupBy {
            it.insuranceType
        }.inject([]) { r, groupedResults ->
            r.addAll(groupedResults.value.groupBy {
                it.licenseNo
            }.findResults {
                it.value.size() > 1 ? it.value : null
            })
            r
        }
    }

    private static getPolicyNoDuplicatedLines(results) {
        results.groupBy {
            it.policyNo
        }.findResults {
            it.key && it.value.size() > 1 ? it.value : null
        }
    }

    private static getInvalidInsuredDateLines(results, startDate, endDate) {
        if (startDate && endDate) {
            results.findAll {
                def insureDate = getInsureDate(it.createTime)
                insureDate < startDate || insureDate > endDate
            }
        } else if (startDate) {
            results.findAll {
                def insureDate = getInsureDate(it.createTime)
                insureDate < startDate
            }
        } else if (endDate) {
            results.findAll {
                def insureDate = getInsureDate(it.createTime)
                insureDate > endDate
            }
        }
    }

}
