package com.cheche365.cheche.ordercenter.service.insurance;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.OrderOperationInfo;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.model.OfflineInsuranceCompanyImportData;
import com.cheche365.cheche.manage.common.model.OfflineOrderImportHistory;
import com.cheche365.cheche.manage.common.repository.OfflineInsuranceCompanyImportDataRepository;
import com.cheche365.cheche.manage.common.repository.OfflineOrderImportHistoryRepository;
import com.cheche365.cheche.ordercenter.web.model.insurance.ImportDataHistoryModel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chenxiangyin on 2017/12/18.
 */
@Service
public class ImportDataHistoryService {
    private static Logger logger = LoggerFactory.getLogger(CacheUtil.class);
    @Autowired
    private OfflineOrderImportHistoryRepository repository;
    @Autowired
    private OfflineInsuranceCompanyImportDataRepository offlineInsuranceCompanyImportDataRepository;

    public ImportDataHistoryModel createViewModel(OfflineOrderImportHistory history) {
        ImportDataHistoryModel returnModel = new ImportDataHistoryModel();
        returnModel.setImportDateStart(DateUtils.getDateString(history.getStartTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        returnModel.setDataType(OfflineOrderImportHistory.getDataTypeMap().get(history.getType()));
        returnModel.setDataTypeId(history.getType());
        returnModel.setHistoryId(history.getId().toString());
        returnModel.setArea(history.getArea() == null ? "" : history.getArea().getName());
        returnModel.setComment(history.getComment());
        returnModel.setDescription(history.getDescription());
        returnModel.setBalanceTime(history.getBalanceTime() == null ? "" : DateUtils.getDateString(history.getBalanceTime(), DateUtils.DATE_SHORTDATE_PATTERN));
        returnModel.setOrderNum(history.getSuccessSize().toString());
        return returnModel;
    }

    public Page<OfflineOrderImportHistory> getHistorysByPage(ImportDataHistoryModel query) {
        return findBySpecAndPaginate(buildPageable(query.getCurrentPage(), query.getPageSize(),
            Sort.Direction.DESC, "id"), query);
    }

    public Pageable buildPageable(Integer currentPage, Integer pageSize, Sort.Direction direction, String column) {
        Sort sort = new Sort(direction, column);
        return new PageRequest(currentPage - 1, pageSize, sort);
    }

    private Page<OfflineOrderImportHistory> findBySpecAndPaginate(Pageable pageable, ImportDataHistoryModel historyQuery) {
        return repository.findAll(new Specification<OfflineOrderImportHistory>() {
            @Override
            public Predicate toPredicate(Root<OfflineOrderImportHistory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<OrderOperationInfo> criteriaQuery = cb.createQuery(OrderOperationInfo.class);
                List<Predicate> predicateList = new ArrayList<>();
                //地区
                if (StringUtils.isNotBlank(historyQuery.getArea())) {
                    predicateList.add(cb.equal(root.get("area"), Long.parseLong(historyQuery.getArea())));
                }
                //数据类型
                if (historyQuery.getDataTypeId() != null) {
                    predicateList.add(cb.equal(root.get("type"), historyQuery.getDataTypeId()));
                }
                //导入时间
                if (!StringUtil.isNull(historyQuery.getImportDateStart()) && !StringUtil.isNull(historyQuery.getImportDateEnd())) {
                    predicateList.add(cb.between(root.get("createTime"), cb.literal(DateUtils.getDate(historyQuery.getImportDateStart() + " 00:00:00", DateUtils.DATE_LONGTIME24_PATTERN)), cb.literal(DateUtils.getDate(historyQuery.getImportDateEnd() + " 23:59:59", DateUtils.DATE_LONGTIME24_PATTERN))));
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    @Transactional
    public void add(ImportDataHistoryModel form) {
        OfflineOrderImportHistory history = repository.findOne(Long.parseLong(form.getHistoryId()));
        List<OfflineInsuranceCompanyImportData> dataList = offlineInsuranceCompanyImportDataRepository.findByHistory(history);
        Date date = DateUtils.getDate(form.getBalanceTime(), DateUtils.DATE_SHORTDATE_PATTERN);
        history.setBalanceTime(date);
        dataList.forEach(data -> {
            data.setBalanceTime(date);
        });
        try {
            repository.save(history);
            offlineInsuranceCompanyImportDataRepository.save(dataList);
        } catch (Exception e) {
            logger.error("exception when insert import data historyid = {}", form.getHistoryId());
        }
    }
}
