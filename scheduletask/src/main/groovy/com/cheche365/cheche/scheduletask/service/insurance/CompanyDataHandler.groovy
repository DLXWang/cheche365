package com.cheche365.cheche.scheduletask.service.insurance

import com.cheche365.cheche.common.math.NumberUtils
import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.manage.common.exception.FileUploadException
import com.cheche365.cheche.manage.common.model.OfflineInsuranceCompanyImportData
import com.cheche365.cheche.manage.common.repository.OfflineInsuranceCompanyImportDataRepository
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.Row
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

import static com.cheche365.cheche.common.util.DateUtils.getDate
import static com.cheche365.cheche.common.util.StringUtil.defaultNullStr
import static com.cheche365.cheche.manage.common.model.OfflineInsuranceCompanyImportData.Enum.STATUS_NOT_MATCH
import static com.cheche365.cheche.manage.common.service.offlinedata.OfflineOrderDataConvertHandler.*

/**
 * 保险公司数据转换,处理
 * Created by yinJianBin on 2017/12/5.
 */
@Service
@Slf4j
class CompanyDataHandler {

    @Autowired
    OfflineInsuranceCompanyImportDataRepository repository

    static OfflineInsuranceCompanyImportData readExcelRow(Row row, history) {
        OfflineInsuranceCompanyImportData model = new OfflineInsuranceCompanyImportData()
        StringBuilder sb = new StringBuilder()
        Integer rowNum = row.rowNum + 1
        def currentDate = new Date()
        try {
            //序号
            model.setOrder(rowNum);

            model.policyNo = getCellValue(row.getCell(0))
            model.insuredName = getCellValue(row.getCell(1))
            model.paidAmount = StringUtils.isBlank(getCellValue(row.getCell(2))) ? null : NumberUtils.toFinancialDouble(getCellValue(row.getCell(2)))
            model.rebate = StringUtils.isBlank(getCellValue(row.getCell(3))) ? null : NumberUtils.toFinancialDouble(getCellValue(row.getCell(3)))
            model.rebateAmount = StringUtils.isBlank(getCellValue(row.getCell(4))) ? null : NumberUtils.toFinancialDouble(getCellValue(row.getCell(4)))
//            model.balanceTime = getDate(getCellValue(row.getCell(5)), DateUtils.DATE_SHORTDATE_PATTERN)
            model.licensePlateNo = getCellValue(row.getCell(6))
            model.brandModel = getCellValue(row.getCell(7))
            model.engineNo = getCellValue(row.getCell(8))
            model.vinNo = getCellValue(row.getCell(9))
            model.issueTime = getDate(getCellValue(row.getCell(10)), DateUtils.DATE_SHORTDATE_PATTERN)
            model.rebateAddTimes = NumberUtils.toInt(getCellValue(row.getCell(11)), 1)

            model.createTime = currentDate
            model.updateTime = currentDate
            model.status = STATUS_NOT_MATCH
            model.history = history
            model.matchNum = 0

        } catch (RuntimeException re) {
            sb.append("model转换异常! ")
            log.error('第{}行数据模型转换异常', rowNum, re)
        } catch (Exception e) {
            throw new FileUploadException(e.getMessage(), e)
        }
        if (StringUtils.isNotBlank(sb.toString())) {
            model.setErrorMessage(sb.toString())
        }
        model
    }

    OfflineInsuranceCompanyImportData checkRow(OfflineInsuranceCompanyImportData model, Set<String> policyNoSet) {
        StringBuilder sb = new StringBuilder(StringUtils.defaultString(model.getErrorMessage() as String))
        Integer rowNum = model.order + 1
        assertNotBlank(model.policyNo, rowNum, "保险单号码/批单号码", sb)
        assertNotBlank(model.insuredName, rowNum, "被保险人", sb)
        assertNotNull(model.paidAmount, rowNum, "实收保费", sb)
        assertNotNull(model.rebate, rowNum, "费用比例%", sb)
        assertNotNull(model.rebateAmount, rowNum, "已结收付费", sb)
//        assertNotNull(model.balanceTime, rowNum, "结算时间", sb)
        assertNotBlank(model.licensePlateNo, rowNum, "车牌号码", sb)
        assertNotBlank(model.brandModel, rowNum, "车型名称", sb)
        assertNotBlank(model.engineNo, rowNum, "发动机号", sb)
        assertNotBlank(model.vinNo, rowNum, "车架号", sb)
        assertNotNull(model.issueTime, rowNum, "出单日期", sb)


        OfflineInsuranceCompanyImportData companyImportDataPO = null
        if (model.policyNo && !policyNoSet.add(model.policyNo)) {
            sb.append('保单号重复! ')
        } else {
            if (StringUtils.isNotBlank(model.policyNo)) {
                companyImportDataPO = repository.findByPolicyNoAndRebateAddTimes(model.policyNo, model.rebateAddTimes)
            }
        }
        if (StringUtils.isNotBlank(sb.toString())) {
            model.setErrorMessage(sb.toString())
        } else {
            if (companyImportDataPO != null) {
                //如果已经存在相同批次的补点,就覆盖原来的值
                model.setId(companyImportDataPO.id)
                model.setCreateTime(companyImportDataPO.createTime)
                model.setUpdateTime(new Date())
                model.setStatus(STATUS_NOT_MATCH)
                model.setComment('相同批次补点数据,覆盖')
            }
        }
        model
    }

    static List<String> getStringList(Set<OfflineInsuranceCompanyImportData> models) {
        def stringList = []
        models.each { model ->
            stringList << [
                    defaultNullStr(model.policyNo),
                    defaultNullStr(model.insuredName),
                    defaultNullStr(model.paidAmount),
                    defaultNullStr(model.rebate),
                    defaultNullStr(model.rebateAmount),
                    defaultNullStr(DateUtils.getDateString(model.balanceTime, DateUtils.DATE_SHORTDATE_PATTERN)),
                    defaultNullStr(model.licensePlateNo),
                    defaultNullStr(model.brandModel),
                    defaultNullStr(model.engineNo),
                    defaultNullStr(model.vinNo),
                    defaultNullStr(DateUtils.getDateString(model.issueTime, DateUtils.DATE_SHORTDATE_PATTERN)),
                    " ", " ", defaultNullStr(model.errorMessage) + '\r'
            ].join(",")
        }
        stringList
    }

//    @Transactional
//    def saveBatch(Set<OfflineInsuranceCompanyImportData> models, OfflineOrderImportHistory history) {
//        if (models) {
//            repository.save(models)
//            writeFile(history.successPath, getStringList(models, true))
//        }
//    }

    @Transactional
    def singleSave(model) {
        repository.save(model)
    }

}