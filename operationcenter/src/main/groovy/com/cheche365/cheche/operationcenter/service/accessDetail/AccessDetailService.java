package com.cheche365.cheche.operationcenter.service.accessDetail;


import com.cheche365.cheche.common.excel.ExcelExportUtil;
import com.cheche365.cheche.common.excel.entity.params.ExcelExportEntity;
import com.cheche365.cheche.common.excel.entity.params.ExportParams;
import com.cheche365.cheche.common.excel.entity.params.PoiBaseConstants;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.AccessDetail;
import com.cheche365.cheche.core.repository.AccessDetailRepository;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterStatus;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.util.ResponseOutUtil;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.operationcenter.model.AccessDetailQuery;
import com.cheche365.cheche.operationcenter.web.model.marketing.AccessDetailData;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * Created by chenxiangyin on 2017/8/29.
 */
@Service
@Slf4j
public class AccessDetailService extends BaseService {
    @Autowired
    private EntityManager entityManager;
    public List<Tuple> findAccessDetailGroup(AccessDetailQuery accessDetailQuery) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Date sTime = new Date(), eTime = new Date();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<AccessDetail> root = query.from(AccessDetail.class);
        Path<String> id = root.get("id");
        Path<String> source = root.get("source");
        Path<String> mobile = root.get("mobile");
        List<Predicate> predicateList = new ArrayList<>();
        if (!StringUtils.isEmpty(accessDetailQuery.getStartDate())) {
            predicateList.add(cb.greaterThanOrEqualTo(root.get("createTime"), DateUtils.getDate(accessDetailQuery.getStartDate(), DateUtils.DATE_LONGTIME24_PATTERN)));
        }
        if (!StringUtils.isEmpty(accessDetailQuery.getEndDate())) {
            predicateList.add(cb.lessThanOrEqualTo(root.get("createTime"), DateUtils.getDate(accessDetailQuery.getEndDate(), DateUtils.DATE_LONGTIME24_PATTERN)));
        }
        List<Selection<?>> selectionList = new ArrayList<>();
        selectionList.add(id);
        selectionList.add(cb.count(mobile));
        selectionList.add(source);
        Predicate[] predicates = new Predicate[predicateList.size()];
        predicates = predicateList.toArray(predicates);
        query.multiselect(selectionList).where(predicates).groupBy(source).orderBy(cb.desc(root.get("id")));
        return entityManager.createQuery(query).getResultList();
    }

    public List<Tuple> getOrdersByPage(AccessDetailQuery accessDetailQuery) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Date sTime = new Date(), eTime = new Date();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<AccessDetail> root = query.from(AccessDetail.class);
        Path<String> id = root.get("id");
        Path<String> referer = root.get("referer");
        Path<String> mobile = root.get("mobile");
        List<Predicate> predicateList = new ArrayList<>();
        if (!StringUtils.isEmpty(accessDetailQuery.getStartDate())) {
            predicateList.add(cb.greaterThanOrEqualTo(root.get("createTime"), DateUtils.getDate(accessDetailQuery.getStartDate(), DateUtils.DATE_LONGTIME24_PATTERN)));
        }
        if (!StringUtils.isEmpty(accessDetailQuery.getEndDate())) {
            predicateList.add(cb.lessThanOrEqualTo(root.get("createTime"), DateUtils.getDate(accessDetailQuery.getEndDate(), DateUtils.DATE_LONGTIME24_PATTERN)));
        }
        if(accessDetailQuery.getSource().equals("0")){
            predicateList.add(cb.isNull(root.get("source")));
        }else{
            predicateList.add(cb.equal(root.get("source"),accessDetailQuery.getSource()));
        }
        List<Selection<?>> selectionList = new ArrayList<>();
        selectionList.add(id);
        selectionList.add(referer);
        selectionList.add(cb.count(mobile));
        Predicate[] predicates = new Predicate[predicateList.size()];
        predicates = predicateList.toArray(predicates);
        query.multiselect(selectionList).where(predicates).groupBy(referer).orderBy(cb.desc(root.get("id")));
        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((accessDetailQuery.getCurrentPage() - 1) * accessDetailQuery.getPageSize());
        typedQuery.setMaxResults(accessDetailQuery.getPageSize());
        return typedQuery.getResultList();
    }

    public AccessDetailData createAccessDetail(Tuple result){
        AccessDetailData detail =  new AccessDetailData();
        detail.setId(Long.valueOf(result.get(0).toString()));
        if(result.get(2) == null){
            detail.setSource("其他");
            detail.setSourceId("0");
        }else{
            String sourceId = result.get(2).toString();
            detail.setSourceId(sourceId);
            String name = AccessDetailData.AccessDetailSource.getName(Integer.parseInt(sourceId));
            if(StringUtil.isNull(name)){
                detail.setSource("未知搜索引擎");
            }else{
                detail.setSource(AccessDetailData.AccessDetailSource.getName(Integer.parseInt(sourceId)));
            }
        }
        detail.setMobileNum(result.get(1).toString());
        return detail;
    }
    public AccessDetailData createAccessDetailInfo(Tuple result){
        AccessDetailData detail =  new AccessDetailData();
        detail.setId(Long.valueOf(result.get(0).toString()));
        detail.setUrl(result.get(1).toString());
        detail.setMobileNum(result.get(2).toString());
        return detail;
    }

//    public ResultModel export(List<Tuple> activityMonitorDataQueryList, HttpServletResponse response) {
//        ExportParams params = new ExportParams("SEO跟踪");
//        List<ExcelExportEntity> entitiesDataInfo = new ArrayList<>();
//        List applicantPersonList = new ArrayList<>();
//        ExcelExportEntity firstColumn = new ExcelExportEntity("序号", "id", 8);
//        firstColumn.setFormat(PoiBaseConstants.IS_ADD_INDEX);
//        entitiesDataInfo.add(firstColumn);
//        entitiesDataInfo.add(new ExcelExportEntity("序号", "id", 10));
//        entitiesDataInfo.add(new ExcelExportEntity("来源", "source", 15));
//        entitiesDataInfo.add(new ExcelExportEntity("电话数", "mobile", 15));
//        for (Tuple activityMonitorDataQuery : activityMonitorDataQueryList) {
//            Map applicantPersonMap = new HashMap<String, Object>();
//            applicantPersonMap.put("id", activityMonitorDataQuery.get(0).toString());
//            applicantPersonMap.put("source", activityMonitorDataQuery.get(1).toString());
//            applicantPersonMap.put("mobile", activityMonitorDataQuery.get(2).toString());
//            applicantPersonList.add(applicantPersonMap);
//        }
//        String currentDate = DateUtils.getCurrentDateString(DateUtils.DATE_SHORTDATE_PATTERN);
//        Workbook workbook = ExcelExportUtil.exportExcel(params, entitiesDataInfo, applicantPersonList);
//        return ResponseOutUtil.excelExport(workbook, response, currentDate + "数据查询.xls");
//    }

    public ResultModel exportInfo(List<Tuple> accessDetailList, HttpServletResponse response) {
        ExportParams params = new ExportParams("SEO详情");
        List<ExcelExportEntity> entitiesDataInfo = new ArrayList<>();
        List resultList = new ArrayList<>();
        ExcelExportEntity firstColumn = new ExcelExportEntity("序号", "id", 8);
        firstColumn.setFormat(PoiBaseConstants.IS_ADD_INDEX);
        entitiesDataInfo.add(firstColumn);
        entitiesDataInfo.add(new ExcelExportEntity("访问链接", "url", 30));
        entitiesDataInfo.add(new ExcelExportEntity("信息数", "mobileNum", 15));
        for (Tuple tuple: accessDetailList) {
            Map applicantPersonMap = new HashMap<String, Object>();
            applicantPersonMap.put("url", tuple.get(1));
            applicantPersonMap.put("mobileNum", tuple.get(2));
            resultList.add(applicantPersonMap);
        }
        String currentDate = DateUtils.getCurrentDateString(DateUtils.DATE_SHORTDATE_PATTERN);
        Workbook workbook = ExcelExportUtil.exportExcel(params, entitiesDataInfo, resultList);
        return ResponseOutUtil.excelExport(workbook, response, currentDate + "数据查询.xls");
    }

}
