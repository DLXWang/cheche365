package com.cheche365.cheche.operationcenter.service.dataStatistics;

import com.cheche365.cheche.common.excel.ExcelExportUtil;
import com.cheche365.cheche.common.excel.entity.params.ExcelExportEntity;
import com.cheche365.cheche.common.excel.entity.params.ExportParams;
import com.cheche365.cheche.common.excel.entity.params.PoiBaseConstants;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.model.BusinessActivity;
import com.cheche365.cheche.core.model.CooperationMode;
import com.cheche365.cheche.core.model.MarketingRule;
import com.cheche365.cheche.core.repository.BusinessActivityRepository;
import com.cheche365.cheche.core.service.PurchaseOrderIdService;
import com.cheche365.cheche.core.util.URLUtils;
import com.cheche365.cheche.manage.common.exception.FileUploadException;
import com.cheche365.cheche.manage.common.model.ActivityMonitorUrl;
import com.cheche365.cheche.manage.common.repository.ActivityMonitorUrlRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.manage.common.util.ExcelUtil;
import com.cheche365.cheche.manage.common.util.ResponseOutUtil;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.operationcenter.model.ActivityMonitorDataQuery;
import com.cheche365.cheche.operationcenter.service.partner.BusinessActivityService;
import com.cheche365.cheche.operationcenter.web.model.partner.BusinessActivityViewModel;
import com.cheche365.cheche.web.util.UrlUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

/**
 * Created by chenx on 2017/6/9.
 */

@Service
public class ActivityMonitorDataService extends BaseService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ActivityMonitorUrlRepository activityMonitorUrlRepository;
    @Autowired
    private BusinessActivityService businessActivityService;
    @Autowired
    private BusinessActivityRepository businessActivityRepository;
    @Autowired
    private InternalUserManageService internalUserManageService;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PurchaseOrderIdService purchaseOrderIdService;


    public Page<Object[]> findMonitorDataBySpecAndPaginate(ActivityMonitorDataQuery activityMonitorDataQuery) {
        String sql;
        if(!activityMonitorDataQuery.getGroupByDay()){
            sql=buildQuery(activityMonitorDataQuery);
        }else{
            sql=buildQueryByMonitor(activityMonitorDataQuery);
        }
        Query query = entityManager.createNativeQuery(sql);
        int totals = query.getResultList().size();
        List<Object[]> currentView = query.setFirstResult((activityMonitorDataQuery.getCurrentPage() - 1) * activityMonitorDataQuery.getPageSize())
            .setMaxResults(activityMonitorDataQuery.getPageSize()).getResultList();
        Page<Object[]> page = new PageImpl<Object[]>(currentView, new PageRequest(activityMonitorDataQuery.getCurrentPage() - 1, activityMonitorDataQuery.getPageSize()), totals);
        return page;
    }


    private String buildQuery(ActivityMonitorDataQuery activityMonitorDataQuery) {
        String startTime = activityMonitorDataQuery.getStartTimeStr();
        String endTime = activityMonitorDataQuery.getEndTimeStr();
        StringBuffer joinAmd=new StringBuffer(" LEFT JOIN ( SELECT amu4.id, sum(amd.pv) pvs, sum(amd.uv) uvs FROM  activity_monitor_url amu4 JOIN activity_monitor_data amd ON amu4.business_activity = amd.business_activity");
        StringBuffer joinU = new StringBuffer("  LEFT JOIN ( SELECT  amu1.id, Count(u.id) AS users FROM activity_monitor_url amu1 JOIN `user` u  ON u.source_type = 1 AND u.source_id = amu1.business_activity  ");
        StringBuffer joinP1 = new StringBuffer("  LEFT JOIN ( SELECT  amu2.id,  count(po.id) AS orders  FROM   activity_monitor_url amu2 JOIN purchase_order po ON po.order_source_type = 1 AND po.order_source_id = amu2.business_activity ");
        StringBuffer joinP2 = new StringBuffer("  LEFT JOIN ( SELECT  amu2.id,  count(po.id) AS orders,sum(po.paid_amount) AS amount FROM   activity_monitor_url amu2 JOIN purchase_order po ON po.order_source_type = 1 " +
            "AND po.order_source_id = amu2.business_activity WHERE po.status=5 ");
        StringBuffer joinMS = new StringBuffer("LEFT JOIN ( SELECT amu3.id, count(ms.mobile) AS usernum FROM  activity_monitor_url amu3 JOIN marketing_success ms ON amu3.business_activity = ms.business_activity  ");
        if(!StringUtil.isNull(activityMonitorDataQuery.getStartTimeStr()) && !StringUtil.isNull(activityMonitorDataQuery.getEndTimeStr())){
            joinAmd.append(" WHERE  amd.monitor_time between '").append(startTime).append("' and '").append(endTime).append("' ");
            joinU.append(" WHERE  u.create_time between '").append(startTime).append("' and '").append(endTime).append("' ");
            joinP1.append(" WHERE  po.create_time between '").append(startTime).append("' and '").append(endTime).append("' ");
            joinP2.append(" AND po.update_time between '").append(startTime).append("' and '").append(endTime).append("' ");
            joinMS.append(" WHERE  ms.create_time between '").append(startTime).append("' and '").append(endTime).append("' ");
        }
        joinAmd.append(" GROUP BY amu4.id) tamd ON amu.id = tamd.id");
        joinU.append(" GROUP BY amu1.id) tu ON amu.id = tu.id  ");
        joinP1.append("GROUP BY amu2.id) tpo1  ON amu.id = tpo1.id ");
        joinP2.append("GROUP BY amu2.id) tpo2  ON amu.id = tpo2.id ");
        joinMS.append("GROUP BY amu3.id) AS tms ON amu.id = tms.id");
        StringBuffer sql=new StringBuffer("SELECT amu.id,amu.scope,amu.source,amu.plan,amu.unit,amu.keyword,ba.landing_page,Ifnull(tamd.pvs, 0) AS pvs,Ifnull(tamd.uvs, 0) AS uvs,Ifnull(tu.users, 0)   " +
            "  AS users,Ifnull(tpo2.orders, 0)   AS paynum,Ifnull(tpo2.amount, 0)   AS amount,Ifnull(tpo1.orders, 0)   AS orders,Ifnull(tms.usernum, 0)  AS tel,'2017-01-01 ' FROM   activity_monitor_url " +
            "AS amu JOIN business_activity ba  ON ba.id = amu.business_activity").append(joinU).append(joinP1).append(joinP2).append(joinMS).append(joinAmd).append(" WHERE amu.ENABLE = 1");
        if (StringUtils.isNotEmpty(activityMonitorDataQuery.getScope())) {
            sql.append(" AND amu.scope = '").append(activityMonitorDataQuery.getScope()).append("'");
        }
        if (StringUtils.isNotEmpty(activityMonitorDataQuery.getSource())) {
            sql.append(" AND amu.source = '").append(activityMonitorDataQuery.getSource()).append("'");
        }
        if (StringUtils.isNotEmpty(activityMonitorDataQuery.getPlan())) {
            sql.append(" AND amu.plan = '").append(activityMonitorDataQuery.getPlan()).append("'");
        }
        if (StringUtils.isNotEmpty(activityMonitorDataQuery.getUnit())) {
            sql.append(" AND amu.unit = '").append(activityMonitorDataQuery.getUnit()).append("'");
        }
        if (StringUtils.isNotEmpty(activityMonitorDataQuery.getKeyword())) {
            sql.append(" AND amu.keyword like  '%").append(activityMonitorDataQuery.getKeyword()).append("%'");
        }
        sql.append(" GROUP BY amu.id  ORDER BY " + activityMonitorDataQuery.getSort() + " ").append(activityMonitorDataQuery.getSortRule());
        return sql.toString();
    }

    private String buildQueryByMonitor(ActivityMonitorDataQuery activityMonitorDataQuery) {
        String startTime = activityMonitorDataQuery.getStartTimeStr();
        String endTime = activityMonitorDataQuery.getEndTimeStr();
        if (StringUtil.isNull(activityMonitorDataQuery.getStartTimeStr()) || StringUtil.isNull(activityMonitorDataQuery.getEndTimeStr())) {
            startTime = DateUtils.getDateString(DateUtils.getCustomDate(new Date(),-30,0,0,0),DateUtils.DATE_LONGTIME24_PATTERN);
            endTime = DateUtils.getCurrentDateString(DateUtils.DATE_LONGTIME24_PATTERN);
        }

        StringBuffer joinU = new StringBuffer("  LEFT JOIN ( SELECT  amu1.id, Count(u.id)  AS users,u.create_time FROM activity_monitor_url amu1 JOIN `user` u  ON u.source_type = 1 AND u.source_id = amu1.business_activity  ");
        StringBuffer joinP1 = new StringBuffer("  LEFT JOIN ( SELECT  amu2.id,  count(po.id) AS orders,po.create_time  FROM   activity_monitor_url amu2 JOIN purchase_order po ON po.order_source_type = 1 AND po.order_source_id = amu2.business_activity ");
        StringBuffer joinP2 = new StringBuffer("  LEFT JOIN ( SELECT  amu2.id,  count(po.id) AS orders,sum(po.paid_amount) AS amount,po.update_time FROM   activity_monitor_url amu2 JOIN purchase_order po ON po.order_source_type = 1 " +
            "AND po.order_source_id = amu2.business_activity WHERE po.status=5 ");
        StringBuffer joinMS = new StringBuffer("LEFT JOIN ( SELECT amu3.id, count(ms.mobile) AS usernum,ms.create_time FROM  activity_monitor_url amu3 JOIN marketing_success ms ON amu3.business_activity = ms.business_activity  ");
        StringBuffer joinAmd=new StringBuffer(" LEFT JOIN ( SELECT amu4.id, sum(amd.pv) pvs, sum(amd.uv) uvs ,amd.monitor_time FROM  activity_monitor_url amu4 JOIN activity_monitor_data amd ON amu4.business_activity = amd.business_activity");
        joinU.append(" WHERE  u.create_time between '").append(startTime).append("' and '").append(endTime).append("' ");
        joinP1.append(" WHERE  po.create_time between '").append(startTime).append("' and '").append(endTime).append("' ");
        joinP2.append(" AND po.update_time between '").append(startTime).append("' and '").append(endTime).append("' ");
        joinMS.append(" WHERE  ms.create_time between '").append(startTime).append("' and '").append(endTime).append("' ");
        joinAmd.append(" WHERE  amd.monitor_time between '").append(startTime).append("' and '").append(endTime).append("' ");
        joinU.append(" GROUP BY amu1.id,TO_DAYS(u.create_time)) tu ON amu.id = tu.id  ").append(" AND TO_DAYS(tamd.monitor_time) = TO_DAYS(tu.create_time)");
        joinP1.append("GROUP BY amu2.id,TO_DAYS(po.create_time)) tpo1  ON amu.id = tpo1.id ").append(" AND TO_DAYS(tamd.monitor_time) = TO_DAYS(tpo1.create_time) ");
        joinP2.append("GROUP BY amu2.id,TO_DAYS( po.update_time)) tpo2  ON amu.id = tpo2.id ").append(" AND TO_DAYS(tamd.monitor_time) = TO_DAYS(tpo2.update_time) ");
        joinMS.append("GROUP BY amu3.id,TO_DAYS( ms.create_time)) AS tms ON amu.id = tms.id").append(" AND TO_DAYS(tamd.monitor_time) = TO_DAYS(tms.create_time) ");
        joinAmd.append(" GROUP BY amu4.id,TO_DAYS( amd.monitor_time)) tamd ON amu.id = tamd.id");
        StringBuffer sql=new StringBuffer("SELECT amu.id,amu.scope,amu.source,amu.plan,amu.unit,amu.keyword,ba.landing_page,Ifnull(tamd.pvs, 0) AS pvs,Ifnull(tamd.uvs, 0) AS uvs,Ifnull(tu.users, 0)   " +
            "  AS users,Ifnull(tpo2.orders, 0)   AS paynum,Ifnull(tpo2.amount, 0)   AS amount,Ifnull(tpo1.orders, 0)   AS orders,Ifnull(tms.usernum, 0)  AS tel ,tamd.monitor_time FROM   activity_monitor_url " +
            "AS amu JOIN business_activity ba  ON ba.id = amu.business_activity").append(joinAmd).append(joinU).append(joinP1).append(joinP2).append(joinMS).append(" WHERE amu.ENABLE = 1");

        if (StringUtils.isNotEmpty(activityMonitorDataQuery.getScope())) {
            sql.append(" AND amu.scope = '").append(activityMonitorDataQuery.getScope()).append("'");
        }
        if (StringUtils.isNotEmpty(activityMonitorDataQuery.getSource())) {
            sql.append(" AND amu.source = '").append(activityMonitorDataQuery.getSource()).append("'");
        }
        if (StringUtils.isNotEmpty(activityMonitorDataQuery.getPlan())) {
            sql.append(" AND amu.plan = '").append(activityMonitorDataQuery.getPlan()).append("'");
        }
        if (StringUtils.isNotEmpty(activityMonitorDataQuery.getUnit())) {
            sql.append(" AND amu.unit = '").append(activityMonitorDataQuery.getUnit()).append("'");
        }
        if (StringUtils.isNotEmpty(activityMonitorDataQuery.getKeyword())) {
            sql.append(" AND amu.keyword like  '%").append(activityMonitorDataQuery.getKeyword()).append("%'");
        }
        sql.append(" and (tamd.pvs>0 or tu.users>0 or tpo1.orders>0 or tpo2.orders>0 or tms.usernum>0) GROUP BY amu.id ,tamd.monitor_time ORDER BY amu.id desc,tamd.monitor_time desc");
        return sql.toString();
    }


    public Page<ActivityMonitorUrl> getUrlByPage(ActivityMonitorDataQuery query) {
        return findUrlListBySpecAndPaginate(super.buildPageable(query.getCurrentPage(), query.getPageSize(),
            Sort.Direction.DESC, "createTime"), query);
    }

    public Page<ActivityMonitorUrl> findUrlListBySpecAndPaginate(Pageable pageable, ActivityMonitorDataQuery dataQuery) {
        return activityMonitorUrlRepository.findAll(new Specification<ActivityMonitorUrl>() {
            @Override
            public Predicate toPredicate(Root<ActivityMonitorUrl> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<MarketingRule> criteriaQuery = cb.createQuery(MarketingRule.class);
                //条件构造
                List<Predicate> predicateList = new ArrayList<>();
                if (!StringUtil.isNull(dataQuery.getScope())) {
                    Path<String> scope = root.get("scope");
                    predicateList.add(cb.like(scope, "%" + dataQuery.getScope() + "%"));
                }
                if (!StringUtil.isNull(dataQuery.getSource())) {
                    Path<String> source = root.get("source");
                    predicateList.add(cb.like(source, "%" + dataQuery.getSource() + "%"));
                }
                if (!StringUtil.isNull(dataQuery.getPlan())) {
                    Path<String> plan = root.get("plan");
                    predicateList.add(cb.like(plan, "%" + dataQuery.getPlan() + "%"));
                }
                if (!StringUtil.isNull(dataQuery.getUnit())) {
                    Path<String> unit = root.get("unit");
                    predicateList.add(cb.like(unit, "%" + dataQuery.getUnit() + "%"));
                }
                if (!StringUtil.isNull(dataQuery.getKeyword())) {
                    Path<String> keyword = root.get("keyword");
                    predicateList.add(cb.like(keyword, "%" + dataQuery.getKeyword() + "%"));
                }
                if (!StringUtil.isNull(dataQuery.getStartTimeStr())) {
                    Date startTime = DateUtils.getDate(dataQuery.getStartTimeStr() + " 00:00:00", DateUtils.DATE_LONGTIME24_PATTERN);
                    predicateList.add(cb.greaterThanOrEqualTo(root.get("createTime"), startTime));
                }
                if (!StringUtil.isNull(dataQuery.getEndTimeStr())) {
                    Date endTime = DateUtils.getDate(dataQuery.getEndTimeStr() + " 23:59:59", DateUtils.DATE_LONGTIME24_PATTERN);
                    predicateList.add(cb.lessThanOrEqualTo(root.get("createTime"), endTime));
                }
                if (!StringUtil.isNull(dataQuery.getUrl())) {
                    predicateList.add(cb.equal(root.get("businessActivity").get("landingPage"), dataQuery.getUrl()));
                }
                Path<String> keyword = root.get("enable");
                predicateList.add(cb.equal(keyword, true));
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    @Transactional
    public void delUrlData(Long id) throws Exception {
        ActivityMonitorUrl activityMonitorUrl = activityMonitorUrlRepository.findOne(id);
        activityMonitorUrl.setEnable(false);
        activityMonitorUrl.setOperator(internalUserManageService.getCurrentInternalUser());
        activityMonitorUrlRepository.save(activityMonitorUrl);
        BusinessActivity activity = activityMonitorUrl.getBusinessActivity();
        activity.setEndTime(new Date());
        activity.setUpdateTime(new Date());
        activity.setRefreshFlag(false);
        activity.setEnable(false);
        activity.setOperator(internalUserManageService.getCurrentInternalUser());
        businessActivityRepository.save(activity);
    }

    @Transactional
    public void addUrlData(ActivityMonitorDataQuery query) throws Exception {
        String url = getUrl(query.getUrl().trim());
        BusinessActivityViewModel model = new BusinessActivityViewModel();
        model.setCooperationMode(CooperationMode.Enum.MARKETING.getId());
        model.setStartTime(DateUtils.getCurrentDateString("yyyy-MM-dd HH:mm"));
        model.setEndTime("2099-12-31 23:59:59");
        model.setEnable(true);
        model.setRefreshFlag(true);
        BusinessActivity activity = businessActivityService.createBusinessActivity(model);
        ActivityMonitorUrl activityMonitorUrl = new ActivityMonitorUrl();
        activityMonitorUrl.setBusinessActivity(activity);
        activityMonitorUrl.setScope(query.getScope());
        activityMonitorUrl.setSource(query.getSource());
        activityMonitorUrl.setPlan(query.getPlan());
        activityMonitorUrl.setUnit(query.getUnit());
        activityMonitorUrl.setKeyword(query.getKeyword());
        activityMonitorUrl.setEnable(true);
        activityMonitorUrl.setCreateTime(new Date());
        activityMonitorUrl.setOperator(activity.getOperator());
        activityMonitorUrl.setUrl(url);
        activityMonitorUrl.setQuote(query.getQuote() == 1);
        String code = purchaseOrderIdService.getBACode();
        activity.setCode(code);
        activity.setLandingPage(activity.assembleCpsUrl(url));
        activity.setOriginalUrl(url);
        activity.setEnable(true);
        activity.setDisplay(true);
        activity.setHome(true);
        activity.setMine(true);
        activity.setFooter(true);
        activity.setBtn(true);
        activity.setApp(false);
        activity.setName(query.getPlan());
        businessActivityRepository.save(activity);
        activityMonitorUrlRepository.save(activityMonitorUrl);
    }

    private String getUrl(String url) {
        url = url.replaceFirst("(?i)^((http(s?))|(" + UrlUtil.getSchema() + "))://", "");
        url = url.substring(url.indexOf("/"), url.length());
        return url;
    }

    public ResultModel export(List<Object[]> activityMonitorDataQueryList, HttpServletResponse response, Boolean groupByDay) {
        ExportParams params = new ExportParams("数据查询");
        List<ExcelExportEntity> entitiesDataInfo = new ArrayList<>();
        List applicantPersonList = new ArrayList<>();
        ExcelExportEntity firstColumn = new ExcelExportEntity("序号", "id", 8);
        firstColumn.setFormat(PoiBaseConstants.IS_ADD_INDEX);
        entitiesDataInfo.add(firstColumn);
        if (groupByDay) {
            entitiesDataInfo.add(new ExcelExportEntity("统计时间", "monitorTime", 10));
        }
        entitiesDataInfo.add(new ExcelExportEntity("岗位", "scope", 15));
        entitiesDataInfo.add(new ExcelExportEntity("渠道", "source", 15));
        entitiesDataInfo.add(new ExcelExportEntity("计划", "plan", 15));
        entitiesDataInfo.add(new ExcelExportEntity("单元", "unit", 15));
        entitiesDataInfo.add(new ExcelExportEntity("关键词", "keyword", 15));
        entitiesDataInfo.add(new ExcelExportEntity("来源", "url", 50));
        entitiesDataInfo.add(new ExcelExportEntity("pv", "pv", 10));
        entitiesDataInfo.add(new ExcelExportEntity("uv", "uv", 10));
        entitiesDataInfo.add(new ExcelExportEntity("注册数", "register", 10));
        entitiesDataInfo.add(new ExcelExportEntity("出单数", "finishOrderCount", 10));
        entitiesDataInfo.add(new ExcelExportEntity("保费金额", "paymentAmount", 20));
        entitiesDataInfo.add(new ExcelExportEntity("订单数", "orderCount", 10));
        entitiesDataInfo.add(new ExcelExportEntity("电话数", "telCount", 10));
        for (Object[] activityMonitorDataQuery : activityMonitorDataQueryList) {
            Map applicantPersonMap = new HashMap<String, Object>();
            applicantPersonMap.put("id", activityMonitorDataQuery[0].toString());
            applicantPersonMap.put("scope", activityMonitorDataQuery[1].toString());
            applicantPersonMap.put("source", activityMonitorDataQuery[2].toString());
            applicantPersonMap.put("plan", activityMonitorDataQuery[3].toString());
            applicantPersonMap.put("unit", activityMonitorDataQuery[4].toString());
            applicantPersonMap.put("keyword", activityMonitorDataQuery[5].toString());
            applicantPersonMap.put("url", activityMonitorDataQuery[6].toString());
            applicantPersonMap.put("pv", activityMonitorDataQuery[7].toString());
            applicantPersonMap.put("uv", activityMonitorDataQuery[8].toString());
            applicantPersonMap.put("register", activityMonitorDataQuery[9].toString());
            applicantPersonMap.put("finishOrderCount", activityMonitorDataQuery[10].toString());
            applicantPersonMap.put("paymentAmount", activityMonitorDataQuery[11].toString());
            applicantPersonMap.put("orderCount", activityMonitorDataQuery[12].toString());
            applicantPersonMap.put("telCount", activityMonitorDataQuery[13] != null ? activityMonitorDataQuery[13].toString() : 0);
            applicantPersonMap.put("monitorTime", activityMonitorDataQuery[14] == null ? "" : activityMonitorDataQuery[14].toString().substring(0, 10));
            applicantPersonList.add(applicantPersonMap);
        }
        String currentDate = DateUtils.getCurrentDateString(DateUtils.DATE_SHORTDATE_PATTERN);
        Workbook workbook = ExcelExportUtil.exportExcel(params, entitiesDataInfo, applicantPersonList);
        return ResponseOutUtil.excelExport(workbook, response, currentDate + "数据查询.xls");
    }

    @Transactional
    public void uploadFile(MultipartFile file) throws Exception {
        Workbook book;
        try {
            book = ExcelUtil.upload(file);
            if (book == null) {
                throw new FileUploadException("文件转换错误 !");
            }
        } catch (IOException e) {
            throw new FileUploadException("文件转换错误 !");
        }
        Sheet sheet = book.getSheetAt(0);
        List<ActivityMonitorDataQuery> dataList = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || row.getCell(0) == null) {
                continue;
            }
            String url = ExcelUtil.getCellValue(row.getCell(5));
            if (!URLUtils.isURL(url)) {
                throw new FileUploadException("文件中网址不正确");
            }
            ActivityMonitorDataQuery query = new ActivityMonitorDataQuery();
            String scope = ExcelUtil.getCellValue(row.getCell(0));
            query.setScope(scope);
            String source = ExcelUtil.getCellValue(row.getCell(1));
            query.setSource(source);
            String plan = ExcelUtil.getCellValue(row.getCell(2));
            query.setPlan(plan);
            String unit = ExcelUtil.getCellValue(row.getCell(3));
            query.setUnit(unit);
            String keyword = ExcelUtil.getCellValue(row.getCell(4));
            query.setKeyword(keyword);
            query.setUrl(url);
            String quote = StringUtil.convertNull(ExcelUtil.getCellValue(row.getCell(6)));
            query.setQuote(quote.equals("是") ? 1 : 0);
            this.addUrlData(query);
        }
    }

}
