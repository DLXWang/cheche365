package com.cheche365.cheche.ordercenter.service.insurance

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.common.util.StringUtil
import com.cheche365.cheche.core.message.InsuranceImportResultMessage
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.OrderOperationInfo
import com.cheche365.cheche.core.service.ResourceService
import com.cheche365.cheche.core.util.FileUtil
import com.cheche365.cheche.manage.common.exception.FileUploadException
import com.cheche365.cheche.manage.common.model.OfflineInsuranceCompanyImportData
import com.cheche365.cheche.manage.common.model.OfflineOrderImportHistory
import com.cheche365.cheche.manage.common.repository.OfflineInsuranceCompanyImportDataRepository
import com.cheche365.cheche.manage.common.repository.OfflineOrderImportHistoryRepository
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import com.cheche365.cheche.manage.common.service.offlinedata.OfflineOrderDataConvertHandler
import com.cheche365.cheche.manage.common.util.AssertUtil
import com.cheche365.cheche.manage.common.util.ExcelUtil
import com.cheche365.cheche.ordercenter.web.model.insurance.OfflineInsuranceImportDataModel
import com.cheche365.cheche.ordercenter.web.model.insurance.OfflineInsuranceSubListModel
import com.cheche365.cheche.ordercenter.web.model.insurance.SubListModel
import com.monitorjbl.xlsx.StreamingReader
import com.monitorjbl.xlsx.impl.StreamingRow
import groovy.util.logging.Slf4j
import org.apache.commons.lang.RandomStringUtils
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.*
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

import javax.persistence.EntityManager
import javax.persistence.Query
import javax.persistence.criteria.*
import java.text.SimpleDateFormat

import static com.cheche365.cheche.core.util.BigDecimalUtil.subtract
import static com.cheche365.cheche.manage.common.constants.OfflineDataExcelHeaderContants.*
import static com.cheche365.cheche.manage.common.model.OfflineOrderImportHistory.*

/**
 * 上传文件处理
 * Created by yinJianBin on 2017/3/3.
 */
@Service
@Slf4j
public class InsuranceDataImportService {

    @Autowired
    private ResourceService resourceService
    @Autowired
    private InternalUserManageService internalUserManageService
    @Autowired
    private OfflineOrderImportHistoryRepository offlineOrderImportHistoryRepository
    @Autowired
    private StringRedisTemplate stringRedisTemplate
    @Autowired
    private EntityManager entityManager
    @Autowired
    private OfflineInsuranceCompanyImportDataRepository offlineInsuranceCompanyImportDataRepository

    public OfflineInsuranceSubListModel subList(String policyNo) {
        List<OfflineInsuranceCompanyImportData> dataList = offlineInsuranceCompanyImportDataRepository.findByPolicyNo(policyNo)
        List<SubListModel> subList = new ArrayList<>()
        Double allPaid = 0
        for (OfflineInsuranceCompanyImportData data : dataList) {
            SubListModel subModel = new SubListModel()
            subModel.setCreateTime(DateUtils.getDateString(data.getCreateTime(), DateUtils.DATE_SHORTDATE_PATTERN))
            subModel.setRebateAmount(data.getRebateAmount())
            subModel.setPaidTime(data.getBalanceTime() == null ? "" : DateUtils.getDateString(data.getBalanceTime(), DateUtils.DATE_SHORTDATE_PATTERN))
            subModel.setRebate(data.getRebate())
            subModel.setAging(0)
            if (data.getBalanceTime() != null && data.getIssueTime() != null) {
                subModel.setAging(DateUtils.getDaysBetween(data.getBalanceTime(), data.getIssueTime()))
            }
            subList.add(subModel)


            allPaid += data.getRebateAmount()
        }
        OfflineInsuranceSubListModel returnModel = new OfflineInsuranceSubListModel()
        returnModel.setSubList(subList)
        returnModel.setAllRebate(allPaid)
        returnModel.setCountNum(dataList.size())
        return returnModel
    }

    public OfflineInsuranceImportDataModel formatData(Object[] data) {
        OfflineInsuranceImportDataModel returnData = new OfflineInsuranceImportDataModel()
        returnData.setArea(data[1].toString())
        returnData.setInstitution(data[2].toString())
        returnData.setBalanceStartTime((data[3] == null) ? "" : data[3].toString().substring(0, 10))
        returnData.setOrderNo(data[4].toString())
        returnData.setPolicyNo((data[5] == null) ? data[6].toString() : data[5].toString())
        returnData.setLicensePlateNo(data[7].toString())
        returnData.setInsuranceComp(data[8].toString())
        returnData.setCompulsoryRebate(data[9].toString())
        returnData.setCommercialRebate(data[10].toString())
        BigDecimal payableAmount = new BigDecimal(data[11])
        BigDecimal paidAmount = new BigDecimal(data[12])
        returnData.setPayableAmount(payableAmount)
        returnData.setPaidAmount(paidAmount)
        returnData.setDiffer(subtract(payableAmount, paidAmount))
        returnData.setPurchaseOrderId(Long.valueOf(data[0].toString()))
        returnData.setRebateId(Long.valueOf(data[13].toString()))
        if (data[5] == null) {
            returnData.setInsuranceType(1)
        } else {
            returnData.setInsuranceType(0)
        }
        return returnData
    }

    private String getCountQuery() {
        return " SELECT " +
                "  DISTINCT(count(po.id)), " +
                " sum(IFNULL(reb.down_commercial_amount, 0) + IFNULL(reb.down_compulsory_amount ,0)), " +
                " IFNULL(sum(reb.company_amount),0)  " //3 出单时间
    }

    private String getCountByBanlanQuery(String sql) {
        return " SELECT count(id),IFNULL(sum(sum_payable),0),IFNULL(sum(sum_paid),0) FROM ( " + sql + " ) AS tem_table"
    }

    private String getSelectQuery() {
        return " SELECT " +
                " DISTINCT(po.id) id, " +
                " a.`name`, " +//1 城市
                " inst.`name` name1, " +//2 出单机构
                "  opi.confirm_order_date, " +//3 出单时间
                " po.order_no, " +//4 订单号
                " insu.policy_no , " +//5 商业险保单号
                " ci.policy_no policy_no2, " +//6 交强险保单号
                " au.license_plate_no, " +//7 车牌号
                "  ic.`name` name2, " +//8 保险公司
                " reb.down_compulsory_rebate, " +//9 交强险点位
                " reb.down_commercial_rebate, " +//10 商业险点位
                " IFNULL(reb.down_commercial_amount,0) + IFNULL(reb.down_compulsory_amount,0) sum_payable, " +//11 应收款（元）
                " IFNULL(reb.company_amount,0) sum_paid, " +//12 保险公司已结款（元）
                " reb.id rebateid " //13 保险公司已结款（元）
    }

    private String getWhereComdition(OfflineInsuranceImportDataModel reqParams) {
        StringBuffer sql = new StringBuffer()
        String groupby = ""
        sql.append(
                " FROM " +
                        " purchase_order po " +
                        " JOIN area a ON a.id = po.area " +
                        " JOIN insurance_purchase_order_rebate reb ON reb.purchase_order = po.id " +
                        " JOIN quote_record qr ON qr.id = po.obj_id " +
                        " LEFT JOIN insurance insu ON insu.quote_record = qr.id " +
                        " LEFT JOIN compulsory_insurance ci ON ci.quote_record = qr.id " +
                        " JOIN institution inst ON reb.down_channel_id = inst.id " +
                        " JOIN order_operation_info opi ON opi.purchase_order = po.id " +
                        " JOIN insurance_company ic ON ic.id = qr.insurance_company " +
                        " JOIN auto au ON po.auto = au.id ")
        if (!StringUtil.isNull(reqParams.getBalanceStartTime()) && !StringUtil.isNull(reqParams.getBalanceEndTime())) {
            sql.append(" JOIN offline_insurance_company_import_data dat ON dat.purchase_order = po.id ")
        }

        sql.append(" WHERE po.order_source_type IN (5, 6) ")
        if (!StringUtil.isNull(reqParams.getArea())) {//省市
            sql.append(" AND po.area = " + reqParams.getArea() + " ")
        }
        if (!StringUtil.isNull(reqParams.getInstitution())) {//出单机构
            sql.append(" AND inst.id = " + reqParams.getInstitution())
        }
        if (!StringUtil.isNull(reqParams.getInsuranceComp())) {//保险公司
            sql.append(" AND ic.id = " + reqParams.getInsuranceComp())
        }
//        if(!StringUtil.isNull(reqParams.getArea())){//出单时间
//            sql.append(" AND po.area = " + reqParams.getArea())
//        }
        if (!StringUtil.isNull(reqParams.getIssueStartTime()) && !StringUtil.isNull(reqParams.getIssueEndTime())) {
            //出单时间
            sql.append(" AND opi.confirm_order_date >= '").append(reqParams.getIssueStartTime()).append(" 00:00:00' AND opi.confirm_order_date <= '").append(reqParams.getIssueEndTime()).append(" 23:59:59' ")
        }
        if (!StringUtil.isNull(reqParams.getPolicyNo())) {//保单号
            sql.append(" AND insu.policy_no = '" + reqParams.getPolicyNo() + "' OR ci.policy_no= '" + reqParams.getPolicyNo() + "'")
        }
        if (!StringUtil.isNull(reqParams.getOrderNo())) {//订单号
            sql.append(" AND po.order_no = '" + reqParams.getOrderNo() + "' ")
        }
        if (!StringUtil.isNull(reqParams.getLicensePlateNo())) {//车牌号
            sql.append(" AND au.license_plate_no = '" + reqParams.getLicensePlateNo() + "' ")
        }
        if (!StringUtil.isNull(reqParams.getBalanceStartTime()) && !StringUtil.isNull(reqParams.getBalanceEndTime())) {
            //到账时间
            sql.append(" AND dat.balance_time >= '").append(reqParams.getBalanceStartTime()).append(" 00:00:00' AND dat.balance_time<= '").append(reqParams.getBalanceEndTime()).append(" 23:59:59' ")
            groupby = " GROUP BY po.id "
        }
        if (reqParams.getStatus() != null) {//结算状态
            if (reqParams.getStatus() == 1) {
                sql.append(" AND  (IFNULL(reb.down_commercial_amount,0) + IFNULL(reb.down_compulsory_amount,0)) = IFNULL(reb.company_amount,0)")
            } else if (reqParams.getStatus() == 2) {
                sql.append(" AND  (IFNULL(reb.down_commercial_amount,0) + IFNULL(reb.down_compulsory_amount,0))  <> IFNULL(reb.company_amount,0) ")
            }
        }
        sql.append(groupby)
        return sql.toString()
    }

    private createUnionAllQuery(OfflineInsuranceImportDataModel reqParams) {
        return this.getSelectQuery() + this.getWhereComdition(reqParams)
    }

    public OfflineInsuranceImportDataModel countAll(OfflineInsuranceImportDataModel reqParams) {
        OfflineInsuranceImportDataModel model = new OfflineInsuranceImportDataModel()
        String sql
        //到账时间
        if (!StringUtil.isNull(reqParams.getBalanceStartTime()) && !StringUtil.isNull(reqParams.getBalanceEndTime())) {
            sql = getCountByBanlanQuery(this.getSelectQuery() + this.getWhereComdition(reqParams))
        } else {
            sql = this.getCountQuery() + this.getWhereComdition(reqParams)
        }
        Query query = entityManager.createNativeQuery(sql)
        List<Object[]> result = query.getResultList()
        if (result != null) {
            Object[] res = result.get(0)
            model.setNum(Integer.parseInt(res[0].toString()))
            BigDecimal payableAmount = new BigDecimal(res[1]).doubleValue()
            BigDecimal paidAmount = new BigDecimal(res[2]).doubleValue()
            model.setPayableAmount(payableAmount)
            model.setPaidAmount(paidAmount)
            model.setDiffer(subtract(payableAmount, paidAmount))
        }
        return model
    }

    public Page<Object[]> findDataBySpecAndPaginate(OfflineInsuranceImportDataModel reqParams) {
        String sql = this.createUnionAllQuery(reqParams)
        Query query = entityManager.createNativeQuery(sql)
        int totals = query.getResultList().size()
        List<Object[]> currentView = query.setFirstResult((reqParams.getCurrentPage() - 1) * reqParams.getPageSize())
                .setMaxResults(reqParams.getPageSize()).getResultList()
        Page<Object[]> page = new PageImpl<Object[]>(currentView, new PageRequest(reqParams.getCurrentPage() - 1, reqParams.getPageSize()), totals)
        return page
    }

    public Page<OfflineInsuranceCompanyImportData> filterBySpecAndPaginate(OfflineInsuranceImportDataModel reqParams) {
        return offlineInsuranceCompanyImportDataRepository.findAll(
                createSpecification(reqParams),
                this.buildPageable(reqParams.getCurrentPage(), reqParams.getPageSize())
        )
    }

    public Pageable buildPageable(int currentPage, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime")
        return new PageRequest(currentPage - 1, pageSize, sort)
    }

    private Specification<OfflineInsuranceCompanyImportData> createSpecification(OfflineInsuranceImportDataModel reqParams) {
        return new Specification<OfflineInsuranceCompanyImportData>() {
            @Override
            public Predicate toPredicate(Root<OfflineInsuranceCompanyImportData> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<OrderOperationInfo> criteriaQuery = cb.createQuery(OrderOperationInfo.class)
                //条件构造
                List<Predicate> predicateList = new ArrayList<>()
                if (!StringUtil.isNull(reqParams.getArea())) {
                    predicateList.add(cb.equal(root.get("purchaseOrder").get("area"), Long.valueOf(reqParams.getArea())))
                }

                if (!StringUtil.isNull(reqParams.getPolicyNo())) {
                    predicateList.add(cb.like(root.get("policyNo"), reqParams.getPolicyNo() + "%"))
                }

                if (!StringUtil.isNull(reqParams.getOrderNo())) {
                    predicateList.add(cb.notEqual(root.get("purchaseOrder").get("orderNo"), reqParams.getOrderNo() + "%"))
                }

                if (!StringUtil.isNull(reqParams.getVinNo())) {
                    predicateList.add(cb.notEqual(root.get("vinNo"), reqParams.getVinNo() + "%"))
                }

                if ((!StringUtil.isNull(reqParams.getIssueStartTime())) && (!StringUtil.isNull(reqParams.getIssueEndTime()))) {
                    Path<Date> createTimePath = root.get("issueTime")//下单时间
                    Date startDate = DateUtils.getDayStartTime(DateUtils.getDate(reqParams.getIssueStartTime(), DateUtils.DATE_SHORTDATE_PATTERN))
                    Date endDate = DateUtils.getDayEndTime(DateUtils.getDate(reqParams.getIssueEndTime(), DateUtils.DATE_SHORTDATE_PATTERN))
                    Expression<Date> startDateExpression = cb.literal(startDate)
                    Expression<Date> endDateExpression = cb.literal(endDate)
                    predicateList.add(cb.between(createTimePath, startDateExpression, endDateExpression))
                }
                if ((!StringUtil.isNull(reqParams.getBalanceStartTime())) && (!StringUtil.isNull(reqParams.getBalanceEndTime()))) {
                    Path<Date> createTimePath = root.get("balanceTime")//下单时间
                    Date startDate = DateUtils.getDayStartTime(DateUtils.getDate(reqParams.getIssueStartTime(), DateUtils.DATE_SHORTDATE_PATTERN))
                    Date endDate = DateUtils.getDayEndTime(DateUtils.getDate(reqParams.getIssueEndTime(), DateUtils.DATE_SHORTDATE_PATTERN))
                    Expression<Date> startDateExpression = cb.literal(startDate)
                    Expression<Date> endDateExpression = cb.literal(endDate)
                    predicateList.add(cb.between(createTimePath, startDateExpression, endDateExpression))
                }
                Predicate[] predicates = new Predicate[predicateList.size()]
                predicates = predicateList.toArray(predicates)
                return criteriaQuery.where(predicates).getRestriction()
            }
        }
    }

    public void saveFile(MultipartFile file, area, description, Integer type) throws IOException {
        AssertUtil.notNull(file, "文件不可为空")
        type = checkFileType(file, type)
        String originalFileName = file.getOriginalFilename()
        String[] split = originalFileName.split("\\.")
        String fileName = split[0]
        String basePath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getOfflineInsurance())
        basePath = basePath + new SimpleDateFormat("yyyyMM").format(new Date()) + File.separator + RandomStringUtils.randomAlphanumeric(4)
        if (!new File(basePath).exists()) {
            if (!new File(basePath).mkdirs()) throw new RuntimeException("创建存储文件目录失败")
        }
        String successFileName = fileName + "__success.csv"
        String failedFileName = fileName + "__failed.csv"
        String newFilePath = basePath + File.separator + originalFileName
        String successFilePath = basePath + File.separator + successFileName
        String failedFilePath = basePath + File.separator + failedFileName
        FileUtil.writeFile(newFilePath, file.getBytes())
        FileUtil.writeFile(successFilePath, getSuccessFileHeaders(type))
        FileUtil.writeFile(failedFilePath, getFailedFileHeaders(type))
        //保存上传历史记录
        OfflineOrderImportHistory history = saveHistory(newFilePath, successFilePath, failedFilePath, type, area, description, originalFileName)
        InsuranceImportResultMessage.setRunningFlag(stringRedisTemplate, history.getId() + "")//增加redis任务运行的锁
    }

    static byte[] getSuccessFileHeaders(int type) {
        if (type == TYPE_FANHUA_TEMP) {
            return "".getBytes()
        }
        def successHeadersString = (type == TYPE_COMPANY ? COMPANY_HEADERS : (type == TYPE_FANHUA ? FANHUA_HEADERS : FANHUA_ADDED_HEADERS)).join(',') + '\r'
        successHeadersString.getBytes()
    }

    static byte[] getFailedFileHeaders(int type) {
        if (type == TYPE_FANHUA_TEMP) {
            return "".getBytes()
        }
        def faildHeaderString = (type == TYPE_COMPANY ? COMPANY_HEADERS : (type == TYPE_FANHUA ? FANHUA_HEADERS : FANHUA_ADDED_HEADERS)).join(',') + ',错误原因\r'
        faildHeaderString.getBytes()
    }

    static def checkFileType(MultipartFile multipartFile, Integer fileType) {
        //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件
        Workbook book = StreamingReader.builder().open(multipartFile.inputStream)
        Sheet sheet = book.getSheetAt(0)
        Iterator<Row> rowIterator = sheet.rowIterator()
        Row headerRow = rowIterator.next() as StreamingRow
        def cellMap = headerRow.cellMap
        def headers = FANHUA_ADDED_HEADERS
        if (fileType == TYPE_FANHUA) {
            def cellValue = ExcelUtil.getCellValue(cellMap.get(0))
            if ('出单日期'.equals(ExcelUtil.getCellValue(cellMap.get(0)))) {//泛华补充数据模版
                headers = FANHUA_ADDED_HEADERS
                fileType = OfflineOrderImportHistory.TYPE_FANHUA_ADDED
            } else if ("收表日期".equals(ExcelUtil.getCellValue(cellMap.get(1)))) {//泛华数据模版
                headers = FANHUA_HEADERS
            } else {
                throw new FileUploadException("上传表格格式错误,请根据模版校验格式!")
            }
        } else if (fileType == TYPE_COMPANY) {
            headers = COMPANY_HEADERS
        } else if (fileType == TYPE_FANHUA_TEMP) {
            headers = FANHUA_TEMP_HEADERS
        }
        headers.eachWithIndex { String columnName, def columnIndex ->
            String header = cellMap.get(columnIndex).getRichStringCellValue().getString()
            if (!columnName.equals(header)) {
                log.error("上传文件的格式不匹配,第 {} 列,模版文件列名:[{}],上传文件列名:[{}]", columnIndex, columnName, header)
                throw new FileUploadException("上传表格格式错误，模版文件第 ${columnIndex + 1} 列列名：[ ${columnName} ]，当前上传文件列名[ ${header} ]")
            }
        }

        Row firstRow = rowIterator.next() as StreamingRow
        Map firstRowMap = firstRow.getCellMap()
        firstRowMap.each { OfflineOrderDataConvertHandler.getCellValue(it.value) }
        fileType
    }

    private OfflineOrderImportHistory saveHistory(String newFilePath, String successFilePath, String failedFilePath, Integer type, areaId, description, originalFileName) {
        OfflineOrderImportHistory history = new OfflineOrderImportHistory()
        history.setCreateTime(new Date())
        history.setOperator(internalUserManageService.getCurrentInternalUser())
        history.setFilePath(newFilePath)
        history.setSuccessPath(successFilePath)
        history.setFailedPath(failedFilePath)
        history.setStatus(false)
        history.setType(type)
        if (areaId) {
            Area area = new Area()
            area.setId(areaId)
            history.setArea(area)
        }
        history.setDescription(description)
        history.setFileName(originalFileName)
        return offlineOrderImportHistoryRepository.save(history)
    }
}
