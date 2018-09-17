package com.cheche365.cheche.ordercenter.service.telMarketingCenter;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.CompulsoryInsurance;
import com.cheche365.cheche.core.model.Insurance;
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository;
import com.cheche365.cheche.core.repository.InsuranceRepository;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.core.model.TelMarketingCenterRepeat;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterStatus;
import com.cheche365.cheche.core.repository.TelMarketingCenterRepeatRepository;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.TelMarketingCenterRequestParams;
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.TelMarketingCenterViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TelMarketingCenterRepeatService extends BaseService {

    @Autowired
    private TelMarketingCenterRepeatRepository telMarketingCenterRepeatRepository;
    @Autowired
    private BaseService baseService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository;
    @Autowired
    private InsuranceRepository insuranceRepository;
    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;
    @Autowired
    private IInternalUserManageService internalUserManageService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取今天处理列表
     *
     * @param params
     * @return
     */
    public DataTablePageViewModel<TelMarketingCenterViewModel> getTodayPage(TelMarketingCenterRequestParams params) {
        List<TelMarketingCenterViewModel> priorityList = new ArrayList<>();
        Page<TelMarketingCenterRepeat> todatPage = this.findTodayPageBySpecification(baseService.buildPageable(params.getCurrentPage(), params.getPageSize(), Sort.Direction.ASC, "createTime"));

        for (TelMarketingCenterRepeat telMarketingCenterRepeat : todatPage) {
            TelMarketingCenter telMarketingCenter = telMarketingCenterRepository.findFirstByMobileOrderByUpdateTimeDesc(telMarketingCenterRepeat.getMobile());
            TelMarketingCenterViewModel model = TelMarketingCenterViewModel.createViewModel(telMarketingCenter, resourceService);
            String countDown = "";
            if (telMarketingCenterRepeat.getSourceTable().equals("insurance")) {
                Insurance insurance = insuranceRepository.findOne(telMarketingCenterRepeat.getSourceId());
                model.setExpireTime(DateUtils.getDateString(insurance.getExpireDate(), DateUtils.DATE_LONGTIME24_PATTERN));
                model.setAutoId(insurance.getAuto().getId());
                countDown = TelMarketingCenterViewModel.getDaysBetweenToString(new Date(), insurance.getExpireDate());
            } else {
                CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceRepository.findOne(telMarketingCenterRepeat.getSourceId());
                model.setExpireTime(DateUtils.getDateString(compulsoryInsurance.getExpireDate(), DateUtils.DATE_LONGTIME24_PATTERN));
                model.setAutoId(compulsoryInsurance.getAuto().getId());
                countDown = TelMarketingCenterViewModel.getDaysBetweenToString(new Date(), compulsoryInsurance.getExpireDate());
            }
            model.setCountDown(countDown);
            priorityList.add(model);
        }
        DataTablePageViewModel<TelMarketingCenterViewModel> viewModel = new DataTablePageViewModel<>();
        viewModel.setAaData(priorityList);
        viewModel.setDraw(params.getDraw());
        viewModel.setiTotalDisplayRecords(todatPage.getTotalElements());
        viewModel.setiTotalRecords(todatPage.getTotalElements());

        return viewModel;
    }

    private Page<TelMarketingCenterRepeat> findTodayPageBySpecification(Pageable pageable) {
        return telMarketingCenterRepeatRepository.findAll(new Specification<TelMarketingCenterRepeat>() {
            @Override
            public Predicate toPredicate(Root<TelMarketingCenterRepeat> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<TelMarketingCenterRepeat> criteriaQuery = cb.createQuery(TelMarketingCenterRepeat.class);
                List<Predicate> predicateList = new ArrayList<>();
                Root telMarketingCenterRoot = query.from(TelMarketingCenter.class);
                predicateList.add(cb.equal(telMarketingCenterRoot.get("mobile"), root.get("mobile")));
                predicateList.add(cb.equal(root.get("source"),206));
                predicateList.add(cb.isNotNull(root.get("createTime")));
                Date yesterday = DateUtils.calculateDateByDay(new Date(),-1);
                predicateList.add(cb.greaterThanOrEqualTo(root.get("createTime"), DateUtils.getDayStartTime(yesterday)));
                predicateList.add(cb.lessThanOrEqualTo(root.get("createTime"), DateUtils.getDayEndTime(yesterday)));
//                predicateList.add(cb.greaterThanOrEqualTo(root.get("createTime"), DateUtils.getDayStartTime(new Date())));
//                predicateList.add(cb.lessThanOrEqualTo(root.get("createTime"), DateUtils.getDayEndTime(new Date())));
                predicateList.add(cb.equal(telMarketingCenterRoot.get("operator"), internalUserManageService.getCurrentInternalUser()));

                Path<Long> status = telMarketingCenterRoot.get("status").get("id");
                CriteriaBuilder.In<Long> statusIn = cb.in(status);
                statusIn.value(TelMarketingCenterStatus.Enum.UNTREATED.getId());//未处理
                statusIn.value(TelMarketingCenterStatus.Enum.VACANT_NUMBER.getId());//空号
                statusIn.value(TelMarketingCenterStatus.Enum.NO_ANSWER.getId());//无人接听
                statusIn.value(TelMarketingCenterStatus.Enum.NO_EXPIRE.getId());//车险未到期
                statusIn.value(TelMarketingCenterStatus.Enum.ORDER_CANCEL.getId());//已取消
                statusIn.value(TelMarketingCenterStatus.Enum.REFUSE.getId());//拒绝
                statusIn.value(TelMarketingCenterStatus.Enum.CANNOT_CONNECT.getId());//无法接通
                statusIn.value(TelMarketingCenterStatus.Enum.HANG_UP.getId());//挂断
                statusIn.value(TelMarketingCenterStatus.Enum.OTHER_STATUS.getId());//其他
//                predicateList.add(statusIn);

                Predicate andStuff =  cb.and(statusIn,
                    cb.greaterThanOrEqualTo(root.get("createTime"), DateUtils.getDayStartTime(yesterday)),
                    cb.lessThanOrEqualTo(root.get("createTime"), DateUtils.getDayEndTime(yesterday)));
                predicateList.add(cb.or(
                    andStuff,
                    cb.equal(status,TelMarketingCenterStatus.Enum.UNTREATED.getId())
                ));
                query.groupBy(root.get("id"));
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }


}
