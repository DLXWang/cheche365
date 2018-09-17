package com.cheche365.cheche.scheduletask.service.insurance

import com.cheche365.cheche.core.message.InsuranceImportResultMessage
import com.cheche365.cheche.core.model.InternalUser
import com.cheche365.cheche.core.repository.AddressRepository
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.UserRepository
import com.cheche365.cheche.core.service.InternalUserService
import com.cheche365.cheche.core.util.BeanUtil
import com.cheche365.cheche.manage.common.exception.FileUploadException
import com.cheche365.cheche.manage.common.model.InsuranceOfflineDataModel
import com.cheche365.cheche.manage.common.model.OfflineFanhuaTempDataModel
import com.cheche365.cheche.manage.common.model.OfflineInsuranceCompanyImportData
import com.cheche365.cheche.manage.common.model.OfflineOrderImportHistory
import com.cheche365.cheche.manage.common.repository.OfflineOrderImportHistoryRepository
import com.cheche365.cheche.manage.common.service.offlinedata.IOfflineDataService
import com.cheche365.cheche.manage.common.util.ExcelUtil
import com.monitorjbl.xlsx.StreamingReader
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

import java.nio.file.Files
import java.nio.file.Paths

import static com.cheche365.cheche.manage.common.service.offlinedata.OfflineOrderDataConvertHandler.writeFile

/**
 *
 * Created by yinJianBin on 2017/9/25.
 */
@Slf4j
@Service
class InsuranceImportResultService {
    private static int BATCH_SIZE = 100
    private static final int BUFFER_SIZE = 10240

    IOfflineDataService offlineDataService;
    UserRepository userRepository
    InsuranceRepository insuranceRepository
    CompulsoryInsuranceRepository compulsoryInsuranceRepository
    FanhuaDataHandler fanhuaDataHandler
    FanhuaAddedDataHandler fanhuaAddedDataHandler
    CompanyDataHandler companyDataHandler
    AddressRepository addressRepository
    InternalUserService internalUserService
    OfflineOrderImportHistoryRepository historyRepository
    StringRedisTemplate stringRedisTemplate
    FanhuaTempDataHandler fanhuaTempDataHandler

    InsuranceImportResultService(IOfflineDataService offlineDataService, UserRepository userRepository, InsuranceRepository insuranceRepository, CompulsoryInsuranceRepository compulsoryInsuranceRepository, FanhuaDataHandler fanhuaDataHandler, FanhuaAddedDataHandler fanhuaAddedDataHandler, CompanyDataHandler companyDataHandler, AddressRepository addressRepository, InternalUserService internalUserService, OfflineOrderImportHistoryRepository historyRepository, StringRedisTemplate stringRedisTemplate, FanhuaTempDataHandler fanhuaTempDataHandler) {
        this.offlineDataService = offlineDataService
        this.userRepository = userRepository
        this.insuranceRepository = insuranceRepository
        this.compulsoryInsuranceRepository = compulsoryInsuranceRepository
        this.fanhuaDataHandler = fanhuaDataHandler
        this.fanhuaAddedDataHandler = fanhuaAddedDataHandler
        this.companyDataHandler = companyDataHandler
        this.addressRepository = addressRepository
        this.internalUserService = internalUserService
        this.historyRepository = historyRepository
        this.stringRedisTemplate = stringRedisTemplate
        this.fanhuaTempDataHandler = fanhuaTempDataHandler
    }

    InternalUser admin
    AbstractFanhuaDataHandler dataHandler


    def processData(OfflineOrderImportHistory history) {
        if (history.type == OfflineOrderImportHistory.TYPE_FANHUA) {
            dataHandler = fanhuaDataHandler
        } else {
            dataHandler = fanhuaAddedDataHandler
        }

        updateHistory(history, false, "开始处理", 0)
        //数据校验,数据处理
        doProcess(history)
        //更新处理结果
        updateHistory(history, true, "处理完成", 0)
    }


    def updateHistory(OfflineOrderImportHistory history, Boolean status, def comment, Integer successSize) {
        def currentDate = new Date()
        history.setUpdateTime(currentDate)
        history.setStatus(status)
        history.startTime == null && history.setStartTime(currentDate)
        history.setEndTime(currentDate)
        history.setComment(comment as String)
        history.setSuccessSize(history.getSuccessSize() + successSize)
        historyRepository.save(history)
    }

    static Workbook fileConvert(String filePath) throws Exception {
        Workbook book;
        try {
            def inputStream = Files.newInputStream(Paths.get(filePath))
            if (filePath.endsWith(ExcelUtil.EXTENSION_XLS)) {
                book = new HSSFWorkbook(inputStream);//大文件会导致内存溢出
            } else if (filePath.endsWith(ExcelUtil.EXTENSION_XLSX)) {
                book = StreamingReader.builder()
                        .rowCacheSize(BATCH_SIZE)  //缓存到内存中的行数，默认是10
                        .bufferSize(BUFFER_SIZE)  //读取资源时，缓存到内存的字节大小，默认是1024
                        .open(inputStream);  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件
            } else {
                throw new FileUploadException("文件转换错误 ,文件地址:" + filePath);
            }
        } catch (Exception e) {
            log.debugEnabled && log.debug("文件转换错误 !,文件地址:({})", filePath, e);
            throw new FileUploadException("文件转换错误 ,文件地址:" + filePath, e);
        }
        log.debugEnabled && log.debug('文件转换成功,文件地址:({})', filePath)
        return book
    }

    def doProcess(OfflineOrderImportHistory history) {
        def filePath = history.filePath
        def workbook = fileConvert(filePath)
        log.debugEnabled && log.debug("文件转换成功,开始处理!")

        if (history.type == OfflineOrderImportHistory.TYPE_COMPANY) {
            BATCH_SIZE = 500
            resoleCompanyExcel(history, workbook)
        } else if (history.type == OfflineOrderImportHistory.TYPE_FANHUA_TEMP) {
            BATCH_SIZE = 500
            resolveFanhuaTempData(history, workbook)
        } else {
            resolveFanhuaExcel(history, workbook)
        }
    }

    def resoleCompanyExcel(OfflineOrderImportHistory history, Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0)
        Integer rowNum = 0
        Set<OfflineInsuranceCompanyImportData> successList = [] as TreeSet, errorList = [] as TreeSet
        Iterator<Row> rowIterator = sheet.rowIterator()
        rowIterator.next()
        Set<String> policyNoSet = new TreeSet<>()
        Integer batchNum = 1
        rowIterator.each { row ->
            rowNum = row.getRowNum() + 1
            OfflineInsuranceCompanyImportData model = companyDataHandler.readExcelRow(row, history)
            model = companyDataHandler.checkRow(model, policyNoSet)
            if (StringUtils.isNotBlank(model.errorMessage as String)) {
                errorList << model
            } else {
                successList << model
            }
            if (rowNum % BATCH_SIZE == 0) {
                log.info('批量保存保险公司线下数据,当前批次({}),当前批次数据量量({})条,当前批次处理至({})行', batchNum++, successList.size() + errorList.size(), rowNum)
                saveCompanyData(successList, errorList, history, rowNum)
                successList.clear()
                errorList.clear()
            }
        }
        log.info('批量保存保险公司线下数据,当前批次({})为最后一批次,当前批次数据量量({})条,当前批次处理至({})行', batchNum, successList.size() + errorList.size(), rowNum)
        saveCompanyData(successList, errorList, history, rowNum)
        policyNoSet.clear()
    }

    def saveCompanyData(successList, errorList, history, rowNum) {
        def finishedList = [] as TreeSet<OfflineInsuranceCompanyImportData>
        successList.each { OfflineInsuranceCompanyImportData model ->
            try {
                companyDataHandler.singleSave(model)
                finishedList << model
            } catch (Exception e) {
                log.info("线下导入保险公司数据保存异常", e)
                model.errorMessage = '数据库保存异常'
                errorList << model
            }
        }
        if (finishedList) writeFile(history.successPath, companyDataHandler.getStringList(finishedList))
        if (errorList) writeFile(history.failedPath, companyDataHandler.getStringList(errorList))
        updateHistory(history, false, rowNum, finishedList.size())
    }

    def resolveFanhuaExcel(OfflineOrderImportHistory history, Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0)
        Integer rowNum = 0
        int batchNo = 1
        Set<InsuranceOfflineDataModel> successList = [] as TreeSet, errorList = [] as TreeSet
        Iterator<Row> rowIterator = sheet.rowIterator()
        rowIterator.next()
        Set<String> policyNoSet = new TreeSet<>()
        rowIterator.each { row ->
            rowNum = row.getRowNum() + 1
            InsuranceOfflineDataModel model = dataHandler.readExcelRow(row)
            if (StringUtils.isBlank(model.agentIdentity) && StringUtils.isBlank(model.policyNo) && StringUtils.isBlank(model.licenseNo)) {
                log.debugEnabled && log.debug("第${rowNum}行数据为空行,跳过")
                return
            }
            model = dataHandler.checkRow(model, policyNoSet, history)
            if (StringUtils.isNotBlank(model.errorMessage)) {
                errorList << model
            } else {
                successList << model
            }
            if (rowNum % BATCH_SIZE == 0) {
                checkBatch(successList, errorList, history, rowNum, batchNo++)
                successList.clear()
                errorList.clear()
            }
        }
        checkBatch(successList, errorList, history, rowNum, batchNo)//最后一批
        policyNoSet.clear()
    }

    def  resolveFanhuaTempData(OfflineOrderImportHistory history, Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0)
        Integer rowNum = 0
        int batchNo = 1
        Iterator<Row> rowIterator = sheet.rowIterator()
        Set<OfflineFanhuaTempDataModel> successList = [] as TreeSet
        rowIterator.next()
        def currentTime = new Date()
        rowIterator.each { row ->
            rowNum = row.getRowNum() + 1
            OfflineFanhuaTempDataModel model = fanhuaTempDataHandler.readTempExcelRow(row)
            if (StringUtils.isBlank(model.agentName) && StringUtils.isBlank(model.policyNo) && StringUtils.isBlank(model.licensePlateNo)) {
                log.debugEnabled && log.debug("第${rowNum}行数据为空行,跳过")
                return
            }
            model.setRowNo(rowNum)
            model.setCreateTime(currentTime)
            model.setHistoryId(history.id)
            model.setStatus(0)

            successList << model
            if (rowNum % BATCH_SIZE == 0) {
                log.info('批量保存泛华台帐数据,当前批次({}),当前批次数据量量({})条,当前批次处理至({})行', batchNo++, successList.size(), rowNum)
                fanhuaTempDataHandler.save(successList)
                successList.clear()
            }
        }
        log.info('批量保存泛华台帐数据,当前为最后批次({}),当前批次数据量量({})条,当前批次处理至({})行', batchNo++, successList.size(), rowNum)
        fanhuaTempDataHandler.save(successList)
    }

    def checkBatch(successList, Set<InsuranceOfflineDataModel> errorList, OfflineOrderImportHistory history, index, batchNo) {
        errorList as TreeSet
        Integer faildSize = 0
        log.isDebugEnabled() && log.debug("线下导入保单数据格式校验,批次:({}),校验成功数量:({}),校验失败数量:({})", batchNo, successList.size(), errorList.size());
        if (successList) {
            try {
                Map resultMap = offlineDataService.exportData(successList, ['enableBihuService': false])
                def faildList = processResult(resultMap, history)
                faildSize = faildList.size()
                errorList.addAll(faildList)
            } catch (Exception e) {
                successList*.setErrorMessage('数据抓取异常! ')
                errorList.addAll successList
                log.isDebugEnabled() && log.debug("线下导入保单数据调用服务校验异常,批次:({}),数量:({})", batchNo, successList.size(), e);
            }
        }
        writeFile(history.getFailedPath(), errorList*.toStringList(history))
        log.isDebugEnabled() && log.debug("已经处理完文件(historyId:{})第({})行", history.id, index);
        updateHistory(history, false, index, (successList.size() - faildSize) as Integer)
        InsuranceImportResultMessage.setRunningFlag(stringRedisTemplate, history.getId() as String)
    }

    def processResult(resultMap, history) {
        def errorList = [] as TreeSet<InsuranceOfflineDataModel>
        if (resultMap) {
            def resultData = resultMap.get('data')
            def validRecords = resultData.get('domainObjects')
            if (validRecords) errorList.addAll processSuccessData(validRecords, history)

            def invalidRecordsMap = resultData.get('invalidRecords')
            if (invalidRecordsMap) errorList.addAll processFailedData(invalidRecordsMap as Map)
        }
        errorList
    }

    static def processFailedData(Map invalidRecordsMap) {
        def errorDataList = [] as TreeSet<InsuranceOfflineDataModel>
        invalidRecordsMap.keySet().each { String errorMessage ->
            String errorDetail = InsuranceOfflineDataModel.resultMap.get(errorMessage)
            if (errorDetail == null) {
                errorDetail = errorMessage
            }
            def lines = invalidRecordsMap[errorMessage] as ArrayList
            lines.flatten().each { line ->
                def dataModel = BeanUtil.objectToBean(line, InsuranceOfflineDataModel)
                dataModel.setErrorMessage(errorDetail)
                errorDataList << dataModel
            }
        }
        errorDataList
    }

    def processSuccessData(def domainObjects, history) {
        admin = internalUserService.getRandomAdmin()
        return successBatchSave(domainObjects, history, admin)
    }

    def successBatchSave(List<Map> dataList, OfflineOrderImportHistory history, admin) {
        def successList = [] as TreeSet<InsuranceOfflineDataModel>, errorList = [] as TreeSet<InsuranceOfflineDataModel>
        for (Map map in dataList) {
            InsuranceOfflineDataModel model = dataHandler.buildRawData(map)
            try {
                dataHandler.singleSave(map, history, admin)
                successList << model
            } catch (FileUploadException fe) {
                model.setErrorMessage(fe.getMessage())
                errorList << model
                log.error("第{}行数据保存发生异常", model.order, fe)
            } catch (Exception e) {
                model.setErrorMessage("数据保存出错")
                errorList << model
                log.error("第{}行数据保存发生异常", model.order, e)
            }
        }
        writeFile(history.successPath, successList*.toStringList(history))
        errorList
    }

}
