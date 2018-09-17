package com.cheche365.cheche.ordercenter.service.telMarketingCenter

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.common.util.MobileUtil
import com.cheche365.cheche.common.util.StringUtil
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.BusinessActivity
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.InternalUser
import com.cheche365.cheche.core.model.MarketingSuccess
import com.cheche365.cheche.core.model.MobileArea
import com.cheche365.cheche.core.model.OrderOperationInfo
import com.cheche365.cheche.core.model.OrderTransmissionStatus
import com.cheche365.cheche.core.model.Permission
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuotePhone
import com.cheche365.cheche.core.model.QuotePhoto
import com.cheche365.cheche.core.model.TelMarketingCenterRepeat
import com.cheche365.cheche.core.model.TelMarketingCenterSource
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.UserLoginInfo
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.MarketingSuccessRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.repository.QuotePhoneRepository
import com.cheche365.cheche.core.repository.QuotePhotoRepository
import com.cheche365.cheche.core.repository.TelMarketingCenterRepeatRepository
import com.cheche365.cheche.core.service.AutoService
import com.cheche365.cheche.core.service.ResourceService
import com.cheche365.cheche.core.service.UserService
import com.cheche365.cheche.manage.common.model.ActivityMonitorUrl
import com.cheche365.cheche.manage.common.model.TelMarketingCenter
import com.cheche365.cheche.manage.common.model.TelMarketingCenterHistory
import com.cheche365.cheche.manage.common.model.TelMarketingCenterOrder
import com.cheche365.cheche.manage.common.model.TelMarketingCenterStatus
import com.cheche365.cheche.manage.common.repository.ActivityMonitorUrlRepository
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterHistoryRepository
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterStatusRepository
import com.cheche365.cheche.manage.common.service.BaseService
import com.cheche365.cheche.manage.common.util.DataFilter.DataFilter
import com.cheche365.cheche.manage.common.util.DataFilter.PermissionFilter
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel
import com.cheche365.cheche.manage.common.web.model.PageInfo
import com.cheche365.cheche.manage.common.web.model.PageViewModel
import com.cheche365.cheche.manage.common.web.model.ResultModel
import com.cheche365.cheche.ordercenter.annotation.DataPermission
import com.cheche365.cheche.ordercenter.constants.TelMarketingCenterType
import com.cheche365.cheche.ordercenter.service.resource.AreaResource
import com.cheche365.cheche.ordercenter.service.resource.TelMarketingCenterResource
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService
import com.cheche365.cheche.ordercenter.web.model.auto.AutoViewModel
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.TelMarketingCenterHistoryViewModel
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.TelMarketingCenterListViewModel
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.TelMarketingCenterRepeatViewModel
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.TelMarketingCenterRequestParams
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.TelMarketingCenterViewModel
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.ArrayUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

import javax.persistence.EntityManager
import javax.persistence.Query
import javax.persistence.Tuple
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Join
import javax.persistence.criteria.JoinType
import javax.persistence.criteria.ListJoin
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import java.text.DecimalFormat

import static org.springframework.data.domain.Sort.Direction.DESC

/**
 * Created by lyh on 2015/11/5.
 */
@Service
class TelMarketingCenterManageService extends BaseService<TelMarketingCenterRepeat, Object> {

    private Logger logger = LoggerFactory.getLogger(this.getClass())

    @Autowired
    private AutoService autoService
    @Autowired
    private IInternalUserManageService internalUserManageService

    @Autowired
    private UserService userService

    @Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository

    @Autowired
    private TelMarketingCenterStatusRepository telMarketingCenterStatusRepository

    @Autowired
    private TelMarketingCenterHistoryRepository telMarketingCenterHistoryRepository

    @Autowired
    private QuotePhoneRepository quotePhoneRepository

    @Autowired
    private TelMarketingCenterResource telMarketingCenterResource

    @Autowired
    private TelMarketingCenterRepeatRepository telMarketingCenterRepeatRepository

    @Autowired
    private EntityManager entityManager

    @Autowired
    private QuotePhotoRepository quotePhotoRepository

    @Autowired
    private InsuranceRepository insuranceRepository

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository

    @Autowired
    private ResourceService resourceService

    @Autowired
    private TelMarketingCenterStatusHandler telMarketingCenterStatusHandler

    @Autowired
    private BaseService baseService

    @Autowired
    private ActivityMonitorUrlRepository activityMonitorUrlRepository

    @Autowired
    private MarketingSuccessRepository marketingSuccessRepository

    @Autowired
    private TelMarketingCenterAutoService telMarketingCenterAutoService

    @Autowired
    private PermissionFilter permissionFilter

    @Autowired
    private AreaResource areaResource

    private static final Integer APPOINTMENT_VAL = 6   //预约数据
    private static final Integer COMPLETE_ORDER = 4    //成单统计
    private static final Integer IN_STORE_NUM = 7      //数据进库量
    private static final Integer PAGE_SIZE = 10000
    private static final Integer SOURCE_PAGE_SIZE = 20

    /**
     * 获取展示列表
     *
     * @return
     */
    TelMarketingCenterListViewModel getList(TelMarketingCenterRequestParams params) {
        TelMarketingCenterListViewModel listViewModel = new TelMarketingCenterListViewModel()
        if (isTelStaff()) {
            List<TelMarketingCenterStatus> triggerStatus = TelMarketingCenterStatus.Enum.APPOINTMENT_STATUS
            listViewModel.setNormalList(getNormalList(params))
            listViewModel.setPriorityList(getPriorityList(triggerStatus))
        }
        return listViewModel
    }


    List<TelMarketingCenterViewModel> getNormalList(TelMarketingCenterRequestParams params) {
        List<TelMarketingCenterViewModel> modelList
        InternalUser internalUser = internalUserManageService.getCurrentInternalUser()
        if (ArrayUtils.isEmpty(params.getChannelIds()) && ArrayUtils.isEmpty(params.getTelTypes()) && params.getAreaType() == null) {
            List<TelMarketingCenter> telMarketingCenterList = telMarketingCenterRepository.findByOperatorAndDisplayOrderByCreateTime(internalUser, true)
            return toDisplay(telMarketingCenterList)//重新分配并显示到页面
        }
        long startTimeMills = System.currentTimeMillis()
        logger.debug("开始查询数据:{}", startTimeMills)
        def page = findBySpecAndPaginate(baseService.buildPageable(1, 5, DESC, "id"), params)
        modelList = toDisplay(page.getContent())
        long endTimeMills = System.currentTimeMillis()
        logger.debug("结束查询数据:{}", endTimeMills)
        logger.debug("查询数据执行时间:{}", (endTimeMills - startTimeMills))
        logger.debug("组装数据执行时间:{}", (System.currentTimeMillis() - endTimeMills))
        return modelList
    }

    /**
     * 获取当前用户的优先列表
     *
     * @return
     */
    private List<TelMarketingCenterViewModel> getPriorityList(List<TelMarketingCenterStatus> triggerStatus) {
        List<TelMarketingCenterViewModel> priorityList = new ArrayList<>()
        List<TelMarketingCenter> telMarketingCenterList = telMarketingCenterRepository
                .findByInternalUserAndTriggerTime(internalUserManageService.getCurrentInternalUser().getId())
        for (TelMarketingCenter telMarketingCenter : telMarketingCenterList) {
            TelMarketingCenterHistory history = null
            if (telMarketingCenter.getTriggerTime() != null)
                history = telMarketingCenterHistoryRepository.findByTelMarketingCenterAndStatusOrderByCreateTimeDesc(telMarketingCenter, triggerStatus)
            priorityList.add(TelMarketingCenterViewModel.createViewModel(telMarketingCenter, history, resourceService))
        }
        return priorityList
    }

    /**
     * 换一批
     *
     * @return
     */
    ResultModel newBatch() {
        ResultModel result = new ResultModel()
        result.setPass(true)
        List<TelMarketingCenter> tmkcList = telMarketingCenterRepository.findDisplayByStatus(internalUserManageService.getCurrentInternalUser(), true, 1)
        if (tmkcList.size() > 0) {
            result.setPass(false)
            result.setMessage("当前页面还有未处理完的电话")
            return result
        } else {//全部处理完
            tmkcList = telMarketingCenterRepository.findByOperatorAndDisplayOrderByCreateTime(internalUserManageService.getCurrentInternalUser(), true)
//查看该工号是否有在页面显示的电话
            for (TelMarketingCenter tmk : tmkcList) {
                tmk.setDisplay(false)
                telMarketingCenterRepository.save(tmk)
            }
        }
        return result
    }

    /**
     * 把一部分值显示到页面
     *
     * @return
     */
    private List<TelMarketingCenterViewModel> toDisplay(List<TelMarketingCenter> tmkcList) {
        List<TelMarketingCenterViewModel> normalList = new ArrayList<>()
        if (CollectionUtils.isEmpty(tmkcList)) {
            return normalList
        }
        for (TelMarketingCenter tmr : tmkcList) {
            InternalUser internalUser = internalUserManageService.getCurrentInternalUser()
            if(tmr.getOperator()==null){
                logger.debug("无跟进人数据ID：{}因未处理分配给跟进人{}", tmr.getId(), internalUser.getId())
            }
            tmr.setOperator(internalUser)//把查出来未处理的记录打到自己工号下
            tmr.setDisplay(true)//置为显示
            telMarketingCenterRepository.save(tmr)
            normalList.add(TelMarketingCenterViewModel.createViewModel(tmr, resourceService))//赋值给正常处理列表
        }
        return normalList
    }

    /**
     * 是否角色是电话专员
     *
     * @return
     */
    private boolean isTelStaff() {
        boolean hasShowPermission = internalUserManageService.hasPermission(Permission.Enum.PURPOSE_CUSTOMER_SHOW)
        boolean hasEditPermission = internalUserManageService.hasPermission(Permission.Enum.PURPOSE_CUSTOMER_EDIT)
        return hasShowPermission && hasEditPermission
    }

    /**
     * 获取当前用户的优先列表
     *
     * @return
     */
    DataTablePageViewModel<TelMarketingCenterViewModel> getPriorityPage(TelMarketingCenterRequestParams params) {
        List<TelMarketingCenterStatus> triggerStatus = TelMarketingCenterStatus.Enum.APPOINTMENT_STATUS
        List<TelMarketingCenterViewModel> priorityList = new ArrayList<>()

        def priorityPage = this.findPriorityPageBySpecification(baseService.buildPageable(params.getCurrentPage(), params.getPageSize(), Sort.Direction.ASC, "triggerTime"))

        for (TelMarketingCenter telMarketingCenter : priorityPage) {
            TelMarketingCenterHistory history = null
            if (telMarketingCenter.getTriggerTime() != null)
                history = telMarketingCenterHistoryRepository.findByTelMarketingCenterAndStatusOrderByCreateTimeDesc(telMarketingCenter, triggerStatus)
            priorityList.add(TelMarketingCenterViewModel.createViewModel(telMarketingCenter, history, resourceService))
        }

        DataTablePageViewModel<TelMarketingCenterViewModel> viewModel = new DataTablePageViewModel<>()
        viewModel.setAaData(priorityList)
        viewModel.setDraw(params.getDraw())
        viewModel.setiTotalDisplayRecords(priorityPage.getTotalElements())
        viewModel.setiTotalRecords(priorityPage.getTotalElements())

        return viewModel
    }

    /**
     * 获取今天处理列表
     *
     * @param params
     * @return
     */
    DataTablePageViewModel<TelMarketingCenterViewModel> getTodayPage(TelMarketingCenterRequestParams params) {
        List<TelMarketingCenterStatus> triggerStatus = TelMarketingCenterStatus.Enum.APPOINTMENT_STATUS
        List<TelMarketingCenterViewModel> priorityList = new ArrayList<>()
        def todatPage = this.findTodayPageBySpecification(baseService.buildPageable(params.getCurrentPage(), params.getPageSize(), Sort.Direction.ASC, "triggerTime"))

        for (TelMarketingCenter telMarketingCenter : todatPage) {
            TelMarketingCenterHistory history = null
            if (telMarketingCenter.getTriggerTime() != null)
                history = telMarketingCenterHistoryRepository.findByTelMarketingCenterAndStatusOrderByCreateTimeDesc(telMarketingCenter, triggerStatus)
            priorityList.add(TelMarketingCenterViewModel.createViewModel(telMarketingCenter, history, resourceService))
        }
        DataTablePageViewModel<TelMarketingCenterViewModel> viewModel = new DataTablePageViewModel<>()
        viewModel.setAaData(priorityList)
        viewModel.setDraw(params.getDraw())
        viewModel.setiTotalDisplayRecords(todatPage.getTotalElements())
        viewModel.setiTotalRecords(todatPage.getTotalElements())

        return viewModel
    }

    @DataPermission(code = "OC1", handler = "publicQueryConditionHandler")
    DataTablePageViewModel<TelMarketingCenterViewModel> getNormalPage(TelMarketingCenterRequestParams params) {
        List<TelMarketingCenterViewModel> modelList = new ArrayList<>()
        long startTimeMills = System.currentTimeMillis()
        logger.debug("开始查询数据:{}", startTimeMills)
        def page = findPageBySpecAndPaginate(baseService.buildPageable(params.getCurrentPage(), params.getPageSize(), DESC, "sourceCreateTime"), params, false)
        List<TelMarketingCenter> tmkcList = page.getContent()
        for (TelMarketingCenter tmr : tmkcList) {
            modelList.add(TelMarketingCenterViewModel.createViewModel(tmr, resourceService))//赋值给正常处理列表
        }
        long endTimeMills = System.currentTimeMillis()
        logger.debug("结束查询数据:{}", endTimeMills)
        logger.debug("查询数据执行时间:{}", (endTimeMills - startTimeMills))
        logger.debug("组装数据执行时间:{}", (System.currentTimeMillis() - endTimeMills))


        DataTablePageViewModel<TelMarketingCenterViewModel> viewModel = new DataTablePageViewModel<>()
        logger.debug("获取优先和正常列表数据，条件:{}", params.toString())
        viewModel.setAaData(modelList)
        viewModel.setDraw(params.getDraw())
        viewModel.setiTotalDisplayRecords(page.getTotalElements())
        viewModel.setiTotalRecords(page.getTotalElements())

        return viewModel
    }


    def findAssignDataByUserAndParam(InternalUser user, TelMarketingCenterRequestParams params, Pageable pageable) {
        pageable = baseService.buildPageable(1, 500, DESC, "id")
        return telMarketingCenterRepository.findAll(this.buildSpecification(user, params), pageable)
    }


    Long countAssignDataByUserAndParam(InternalUser user, TelMarketingCenterRequestParams params) {
        return telMarketingCenterRepository.count(this.buildSpecification(user, params))
    }

    private Specification buildSpecification(InternalUser user, TelMarketingCenterRequestParams params) {
        Specification<TelMarketingCenter> specification = new Specification<TelMarketingCenter>() {
            @Override
            Predicate toPredicate(Root<TelMarketingCenter> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<>()

                Root repeatRoot = query.from(TelMarketingCenterRepeat.class)
                predicateList.add(cb.equal(repeatRoot.get("mobile"), root.get("mobile")))

                Root sourceRoot = null
                if (user == null) {
                    predicateList.add(cb.isNull(root.get("operator")))
                } else {
                    predicateList.add(cb.equal(root.get("operator").get("id"), user.getId()))
                }

                query.distinct(true)
                predicateList.add(cb.isTrue(root.get("display")))
                if (ArrayUtils.isNotEmpty(params.getChannelIds())) {

                    List<Long> ids = Channel.getDataSourceChannel(params.getChannelIds())
                    CriteriaBuilder.In<Long> channelIdIn = cb.in(repeatRoot.get("channel").get("id"))
                    for (Long channelId : ids) {
                        channelIdIn.value(channelId)
                    }
                    predicateList.add(channelIdIn)
                }

                if (ArrayUtils.isNotEmpty(params.getTelTypes())) {
                    sourceRoot = query.from(TelMarketingCenterSource.class)
                    if (user == null &&StringUtils.isEmpty(params.getStartTime()) && StringUtils.isEmpty(params.getEndTime())) {
                        predicateList.add(cb.equal(root.get("source").get("id"), sourceRoot.get("id")))
                    }else{
                        predicateList.add(cb.equal(repeatRoot.get("source").get("id"), sourceRoot.get("id")))
                    }
                    CriteriaBuilder.In<Long> typeIdIn = cb.in(sourceRoot.get("type"))
                    for (String channelId : params.getTelTypes()) {
                        typeIdIn.value(Long.parseLong(channelId))
                    }
                    predicateList.add(typeIdIn)
                }

                if (StringUtils.isNotEmpty(params.getDataLevel())) {
                    if (sourceRoot == null) {
                        sourceRoot = query.from(TelMarketingCenterSource.class)
                        predicateList.add(cb.equal(repeatRoot.get("source").get("id"), sourceRoot.get("id")))
                    }
                    CriteriaBuilder.In<Integer> typeIdIn = cb.in(sourceRoot.get("type"))
                    for (int channelId : TelMarketingCenterType.Enum.DATA_TYPE_MAP.get(params.getDataLevel())) {
                        typeIdIn.value(channelId)
                    }
                    predicateList.add(typeIdIn)
                }

                //根据开通城市查询
                long[] areaTotalList = params.getAreaId();
                List<Long> areaTotalList1 = Arrays.asList(areaTotalList);
                List<Long> areaTotalList2 = new ArrayList();
                List<Area> areaList = areaResource.listByCache();
                for(Area flag:areaList){
                    areaTotalList2.add(flag.getId());
                }
                areaTotalList2.removeAll(areaTotalList1);
                if (ArrayUtils.isNotEmpty(params.getAreaId())&&(areaTotalList2.size() != 0)) {
                    Root areaRoot = query.from(Area.class)
                    Root loginInfoRoot = query.from(UserLoginInfo.class)
                    Root mobileAreaRoot = query.from(MobileArea.class)
                    predicateList.add(cb.equal(root.get("mobile"), mobileAreaRoot.get("mobile")))
                    predicateList.add(cb.equal(root.get("user"), loginInfoRoot.get("user")))
                    predicateList.add(cb.or(cb.equal(mobileAreaRoot.get("area").get("id"), areaRoot.get("id")), cb.equal(loginInfoRoot.get("area").get("id"), areaRoot.get("id"))))
                    predicateList.add(cb.equal(areaRoot.get("active"), 1))
                    CriteriaBuilder.In<Long> areaIdIn = cb.in(areaRoot.get("id"))
                    for (Long areaId : params.getAreaId()) {
                        areaIdIn.value(areaId)
                    }
                    predicateList.add(areaIdIn)
                }

                if (StringUtils.isNotEmpty(params.getStartTime()) && StringUtils.isNotEmpty(params.getEndTime())) {
                    predicateList.add(cb.between(repeatRoot.get("sourceCreateTime"),
                            cb.literal(DateUtils.getDayStartTime(DateUtils.getDate(params.getStartTime(), DateUtils.DATE_SHORTDATE_PATTERN))),
                            cb.literal(DateUtils.getDayEndTime(DateUtils.getDate(params.getEndTime(), DateUtils.DATE_SHORTDATE_PATTERN)))))
                }

                if (StringUtils.isNotEmpty(params.getRenewalDate())) {
                    predicateList.add(cb.equal(repeatRoot.get("renewalDate"), params.getRenewalDate()))
                }

                Predicate[] predicates = new Predicate[predicateList.size()]
                predicates = predicateList.toArray(predicates)
                return query.where(predicates).getRestriction()
            }
        }
        return specification
    }

    private def findBySpecAndPaginate(Pageable pageable, TelMarketingCenterRequestParams params) {
        return telMarketingCenterRepository.findAll(new Specification<TelMarketingCenter>() {
            @Override
            Predicate toPredicate(Root<TelMarketingCenter> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                InternalUser internalUser = internalUserManageService.getCurrentInternalUser()
                CriteriaQuery<TelMarketingCenter> criteriaQuery = cb.createQuery(TelMarketingCenter.class)
                List<Predicate> predicateList = new ArrayList<>()
                Root repeatRoot = query.from(TelMarketingCenterRepeat.class)
                DataFilter.process(repeatRoot, predicateList, cb, params, permissionFilter)
                query.distinct(true)
                predicateList.add(cb.isFalse(root.get("display")))
                predicateList.add(cb.or(cb.isNull(root.get("operator")), cb.equal(root.get("operator").get("id"), internalUser.getId())))
                predicateList.add(cb.between(root.get("sourceCreateTime"),
                        cb.literal(DateUtils.getDayStartTime(DateUtils.getDate(params.getStartTime(), DateUtils.DATE_SHORTDATE_PATTERN))),
                        cb.literal(DateUtils.getDayEndTime(DateUtils.getDate(params.getEndTime(), DateUtils.DATE_SHORTDATE_PATTERN)))))
                predicateList.add(cb.equal(repeatRoot.get("mobile"), root.get("mobile")))
                if (StringUtils.isNotBlank(params.getExpireTime())) {
                    Date startDate = DateUtils.getCustomDate(DateUtils.getDate(params.getExpireTime(), DateUtils.DATE_SHORTDATE_PATTERN), -90, 0, 0, 0)
                    Date endDate = DateUtils.getDate(params.getExpireTime(), DateUtils.DATE_SHORTDATE_PATTERN)
                    predicateList.add(cb.or(cb.isNotNull(root.get("expireTime")), cb.between(root.get("expireTime"), cb.literal(startDate), cb.literal(endDate))))
                }
                predicateList.add(cb.lessThanOrEqualTo(root.get("updateTime"), repeatRoot.get("createTime")))
                if (ArrayUtils.isNotEmpty(params.getChannelIds())) {
                    List<Long> ids = Channel.getDataSourceChannel(params.getChannelIds())
                    CriteriaBuilder.In<Long> channelIdIn = cb.in(repeatRoot.get("channel").get("id"))
                    for (Long channelId : ids) {
                        channelIdIn.value(channelId)
                    }
                    predicateList.add(channelIdIn)
                }
                if (ArrayUtils.isNotEmpty(params.getTelTypes())) {
                    Root sourceRoot = query.from(TelMarketingCenterSource.class)
                    predicateList.add(cb.equal(repeatRoot.get("source").get("id"), sourceRoot.get("id")))
                    CriteriaBuilder.In<Long> typeIdIn = cb.in(sourceRoot.get("type"))
                    for (String channelId : params.getTelTypes()) {
                        typeIdIn.value(Long.parseLong(channelId))
                    }
                    predicateList.add(typeIdIn)
                }
                if (params.getAreaType() != null) {
                    Root areaRoot = query.from(Area.class)
                    Root loginInfoRoot = query.from(UserLoginInfo.class)
                    Root mobileAreaRoot = query.from(MobileArea.class)
                    predicateList.add(cb.equal(root.get("mobile"), mobileAreaRoot.get("mobile")))
                    predicateList.add(cb.equal(root.get("user"), loginInfoRoot.get("user")))
                    predicateList.add(cb.or(cb.equal(mobileAreaRoot.get("area").get("id"), areaRoot.get("id")), cb.equal(loginInfoRoot.get("area").get("id"), areaRoot.get("id"))))
                    predicateList.add(cb.equal(areaRoot.get("active"), 1))
                    if (ArrayUtils.isNotEmpty(params.getAreaId())) {
                        CriteriaBuilder.In<Long> areaIdIn = cb.in(areaRoot.get("id"))
                        for (Long areaId : params.getAreaId()) {
                            areaIdIn.value(areaId)
                        }
                        predicateList.add(areaIdIn)
                    } else {
                        predicateList.add(cb.and(cb.notEqual(areaRoot.get("id"), 420100L), cb.notEqual(areaRoot.get("id"), 440300L)))
                    }

                }
                Predicate[] predicates = new Predicate[predicateList.size()]
                predicates = predicateList.toArray(predicates)
                return criteriaQuery.where(predicates).getRestriction()
            }
        }, pageable)
    }


    private
    def findPageBySpecAndPaginate(Pageable pageable, TelMarketingCenterRequestParams params, Boolean triggerFlag) {
        return telMarketingCenterRepository.findAll(new Specification<TelMarketingCenter>() {
            @Override
            Predicate toPredicate(Root<TelMarketingCenter> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                InternalUser internalUser = internalUserManageService.getCurrentInternalUser()
                CriteriaQuery<TelMarketingCenter> criteriaQuery = cb.createQuery(TelMarketingCenter.class)
                List<Predicate> predicateList = new ArrayList<>()
                Root repeatRoot = query.from(TelMarketingCenterRepeat.class)
                predicateList.add(cb.equal(repeatRoot.get("mobile"), root.get("mobile")))
                Root sourceRoot = null
                query.distinct(true)
                predicateList.add(cb.isTrue(root.get("display")))
                predicateList.add(cb.equal(root.get("operator").get("id"), internalUser.getId()))
                params.getStartTime() && predicateList.add(cb.greaterThanOrEqualTo(root.get("sourceCreateTime"), cb.literal(DateUtils.getDayStartTime(DateUtils.getDate(params.getStartTime(), DateUtils.DATE_SHORTDATE_PATTERN)))))
                params.getEndTime() && predicateList.add(cb.lessThanOrEqualTo(root.get("sourceCreateTime"), cb.literal(DateUtils.getDayEndTime(DateUtils.getDate(params.getEndTime(), DateUtils.DATE_SHORTDATE_PATTERN)))))
                if (StringUtils.isNotBlank(params.getExpireTime())) {
                    Date startDate = DateUtils.getCustomDate(DateUtils.getDate(params.getExpireTime(), DateUtils.DATE_SHORTDATE_PATTERN), -90, 0, 0, 0)
                    Date endDate = DateUtils.getDate(params.getExpireTime(), DateUtils.DATE_SHORTDATE_PATTERN)
                    predicateList.add(cb.or(cb.isNotNull(root.get("expireTime")), cb.between(root.get("expireTime"), cb.literal(startDate), cb.literal(endDate))))
                }
                predicateList.add(cb.lessThanOrEqualTo(root.get("updateTime"), repeatRoot.get("createTime")))
                if (ArrayUtils.isNotEmpty(params.getChannelIds())) {
                    List<Long> ids = Channel.getDataSourceChannel(params.getChannelIds())
                    CriteriaBuilder.In<Long> channelIdIn = cb.in(repeatRoot.get("channel").get("id"))
                    for (Long channelId : ids) {
                        channelIdIn.value(channelId)
                    }
                    predicateList.add(channelIdIn)
                }

                if (ArrayUtils.isNotEmpty(params.getTelTypes())) {
                    if (sourceRoot == null) {
                        sourceRoot = query.from(TelMarketingCenterSource.class)
                        predicateList.add(cb.equal(repeatRoot.get("source").get("id"), sourceRoot.get("id")))
                    }
                    CriteriaBuilder.In<Long> typeIdIn = cb.in(sourceRoot.get("type"))
                    for (String channelId : params.getTelTypes()) {
                        typeIdIn.value(Long.parseLong(channelId))
                    }
                    predicateList.add(typeIdIn)
                }

                if (StringUtils.isNotEmpty(params.getDataLevel())) {
                    if (sourceRoot == null) {
                        sourceRoot = query.from(TelMarketingCenterSource.class)
                        predicateList.add(cb.equal(repeatRoot.get("source").get("id"), sourceRoot.get("id")))
                    }
                    CriteriaBuilder.In<Integer> typeIdIn = cb.in(sourceRoot.get("type"))
                    for (int channelId : TelMarketingCenterType.Enum.DATA_TYPE_MAP.get(params.getDataLevel())) {
                        typeIdIn.value(channelId)
                    }
                    predicateList.add(typeIdIn)
                }

                //根据开通城市查询
                if (ArrayUtils.isNotEmpty(params.getAreaId())) {
                    Root areaRoot = query.from(Area.class)
                    Root loginInfoRoot = query.from(UserLoginInfo.class)
                    Root mobileAreaRoot = query.from(MobileArea.class)
                    predicateList.add(cb.equal(root.get("mobile"), mobileAreaRoot.get("mobile")))
                    predicateList.add(cb.equal(root.get("user"), loginInfoRoot.get("user")))
                    predicateList.add(cb.or(cb.equal(mobileAreaRoot.get("area").get("id"), areaRoot.get("id")), cb.equal(loginInfoRoot.get("area").get("id"), areaRoot.get("id"))))
                    predicateList.add(cb.equal(areaRoot.get("active"), 1))
                    CriteriaBuilder.In<Long> areaIdIn = cb.in(areaRoot.get("id"))
                    for (Long areaId : params.getAreaId()) {
                        areaIdIn.value(areaId)
                    }
                    predicateList.add(areaIdIn)
                }
                DataFilter.process(repeatRoot, predicateList, cb, params, permissionFilter)
                Predicate[] predicates = new Predicate[predicateList.size()]
                predicates = predicateList.toArray(predicates)
                return criteriaQuery.where(predicates).getRestriction()
            }

        }, pageable)
    }

    private def findPriorityPageBySpecification(Pageable pageable) {
        return telMarketingCenterRepository.findAll(new Specification<TelMarketingCenter>() {
            @Override
            Predicate toPredicate(Root<TelMarketingCenter> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                InternalUser internalUser = internalUserManageService.getCurrentInternalUser()
                CriteriaQuery<TelMarketingCenter> criteriaQuery = cb.createQuery(TelMarketingCenter.class)
                List<Predicate> predicateList = new ArrayList<>()
                Root repeatRoot = query.from(TelMarketingCenterRepeat.class)
                predicateList.add(cb.equal(repeatRoot.get("mobile"), root.get("mobile")))

                query.distinct(true)
//                List<TelMarketingCenterStatus> triggerStatus = TelMarketingCenterStatus.Enum.APPOINTMENT_STATUS
//                CriteriaBuilder.In<List<TelMarketingCenterStatus>> telStatusIn = cb.in(root.get("status"))
//                telStatusIn.value(triggerStatus)
//                predicateList.add(telStatusIn)

                Date after15Minutes = this.getafter15MinutesDate()
                predicateList.add(cb.isNotNull(root.get("triggerTime")))
                predicateList.add(cb.lessThanOrEqualTo(root.get("triggerTime"), after15Minutes))
                predicateList.add(cb.equal(root.get("operator").get("id"), internalUser.getId()))

                Predicate[] predicates = new Predicate[predicateList.size()]
                predicates = predicateList.toArray(predicates)
                return criteriaQuery.where(predicates).getRestriction()
            }

            private Date getafter15MinutesDate() {
                Calendar calendar = Calendar.getInstance()
                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 15)
                return calendar.getTime()
            }
        }, pageable)
    }

    private def findTodayPageBySpecification(Pageable pageable) {
        return telMarketingCenterRepository.findAll(new Specification<TelMarketingCenter>() {
            @Override
            Predicate toPredicate(Root<TelMarketingCenter> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                InternalUser internalUser = internalUserManageService.getCurrentInternalUser()
                CriteriaQuery<TelMarketingCenter> criteriaQuery = cb.createQuery(TelMarketingCenter.class)
                List<Predicate> predicateList = new ArrayList<>()
                Root repeatRoot = query.from(TelMarketingCenterRepeat.class)
                predicateList.add(cb.equal(repeatRoot.get("mobile"), root.get("mobile")))
                query.distinct(true)
                Date after24Hour = this.getafter24HourOfDay()
                predicateList.add(cb.isNotNull(root.get("triggerTime")))
                predicateList.add(cb.lessThanOrEqualTo(root.get("triggerTime"), after24Hour))
                predicateList.add(cb.equal(root.get("operator").get("id"), internalUser.getId()))
                Predicate[] predicates = new Predicate[predicateList.size()]
                predicates = predicateList.toArray(predicates)
                return criteriaQuery.where(predicates).getRestriction()
            }

            private Date getafter24HourOfDay() {
                Calendar calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 24)
                return calendar.getTime()
            }
        }, pageable)
    }

    /**
     * 获取单个号码详情
     *
     * @return
     */
    TelMarketingCenterViewModel findById(Long id, Long hisId) {
        return createDetailsViewModel(telMarketingCenterRepository.findOne(id), hisId)
    }

    /**
     * 实体类转展示类
     *
     * @param telMarketingCenter
     * @return
     */
    private TelMarketingCenterViewModel createDetailsViewModel(TelMarketingCenter telMarketingCenter, Long hisId) {
        Map<String, Long> timeMap = new HashMap<>()
        long start = System.currentTimeMillis()
        TelMarketingCenterViewModel telMarketingCenterViewModel = TelMarketingCenterViewModel.createViewModel(telMarketingCenter, resourceService)
        timeMap.put("createViewModel", System.currentTimeMillis() - start)
        if (hisId != null) {
            TelMarketingCenterHistory marketingCenterHistory = this.telMarketingCenterHistoryRepository.findOne(hisId)
            if (marketingCenterHistory.getStatus() != null) {
                telMarketingCenterViewModel.setStatusId(marketingCenterHistory.getStatus().getId())
                telMarketingCenterViewModel.setStatusName(marketingCenterHistory.getStatus().getName())
            }
        } else {
            if (telMarketingCenter.getStatus() != null) {
                telMarketingCenterViewModel.setStatusId(telMarketingCenter.getStatus().getId())
                telMarketingCenterViewModel.setStatusName(telMarketingCenter.getStatus().getName())
            }
        }

        start = System.currentTimeMillis()
        //车辆信息
        telMarketingCenterViewModel.setAutoInfoList(getAutoInfo(telMarketingCenter.getSource().getId(), telMarketingCenter.getUser(), telMarketingCenter.getMobile()))
        timeMap.put("setAutoInfoList", System.currentTimeMillis() - start)

        start = System.currentTimeMillis()
        //获取联系状况（操作历史）
        List<TelMarketingCenterHistory> historyList = telMarketingCenterHistoryRepository.findByTelMarketingCenterOrderByCreateTimeDesc(telMarketingCenter)
        List<TelMarketingCenterHistoryViewModel> hisViewModelList = new ArrayList<>()
        for (TelMarketingCenterHistory history : historyList) {
            hisViewModelList.add(TelMarketingCenterHistoryViewModel.createSimpleViewModel(history))
        }
        telMarketingCenterViewModel.setDealHisList(hisViewModelList)
        timeMap.put("setDealHisList", System.currentTimeMillis() - start)

        start = System.currentTimeMillis()
        //获取号码以前的记录
        telMarketingCenterViewModel.setRepeatList(getTelMarketingCenterRepeatByMobile(telMarketingCenter.getMobile(), buildPageable(1, SOURCE_PAGE_SIZE, DESC, "id")))
        timeMap.put("setRepeatList", System.currentTimeMillis() - start)

        start = System.currentTimeMillis()
        //通过telMarketingCenter去电销历史表查是否有对应记录，取备注
        TelMarketingCenterHistory telMarketingCenterHistory = this.telMarketingCenterHistoryRepository
                .findLastTelMarketingCenterHistory(telMarketingCenter.getId())
        if (telMarketingCenterHistory != null && telMarketingCenterHistory.getComment() != null) {
            telMarketingCenterViewModel.setComment(telMarketingCenterHistory.getComment())
        }
        timeMap.put("setComment", System.currentTimeMillis() - start)
        for (String methodName : timeMap.keySet()) {
            System.out.println(methodName + ":--->" + timeMap.get(methodName))
        }

        return telMarketingCenterViewModel
    }

    /**
     * 电销子表查询相关的auto
     *
     * @param telMarketingCenterRepeatId
     * @return
     */
    List<AutoViewModel> getAutoInfo(Long telMarketingCenterRepeatId) {
        TelMarketingCenterRepeat telMarketingCenterRepeat = telMarketingCenterRepeatRepository.findOne(telMarketingCenterRepeatId)
        List<AutoViewModel> autoViewModelList = new ArrayList<>()
        Long telSourceId = telMarketingCenterRepeat.getSource().getId()
        Long sourceId = telMarketingCenterRepeat.getSourceId()
        String sourceTable = telMarketingCenterRepeat.getSourceTable()
        if (sourceId == null || StringUtils.isEmpty(sourceTable)) {
            return getAutoInfo(telSourceId, telMarketingCenterRepeat.getUser(), telMarketingCenterRepeat.getMobile())
        }
        if (sourceTable.equals(TelMarketingCenterRepeat.Enum.INSURANCE)) {
            Insurance insurance = insuranceRepository.findOne(sourceId)
            if (insurance != null && insurance.getAuto() != null && !insurance.getAuto().isDisable()) {
                autoViewModelList.add(AutoViewModel.createViewModel(insurance.getAuto()))
            }
        } else if (sourceTable.equals(TelMarketingCenterRepeat.Enum.COMPULSORY_INSURANCE)) {
            CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceRepository.findOne(sourceId)
            if (compulsoryInsurance != null && compulsoryInsurance.getAuto() != null && !compulsoryInsurance.getAuto().isDisable()) {
                autoViewModelList.add(AutoViewModel.createViewModel(compulsoryInsurance.getAuto()))
            }
        } else if (sourceTable.equals(TelMarketingCenterRepeat.Enum.QUOTE_PHOTO)) {
            QuotePhoto quotePhoto = quotePhotoRepository.findOne(sourceId)
            if (quotePhoto != null && StringUtils.isNotBlank(quotePhoto.getLicensePlateNo())) {
                autoViewModelList.add(AutoViewModel.createViewModel(quotePhoto))
            }
        } else if (sourceTable.equals(TelMarketingCenterRepeat.Enum.PURCHASE_ORDER)) {
            PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(telMarketingCenterRepeat.getSourceId())
            if (purchaseOrder.getAuto() != null && !purchaseOrder.getAuto().isDisable()) {
                autoViewModelList.add(AutoViewModel.createViewModel(purchaseOrder.getAuto()))
            }
        } else if (sourceTable.equals(TelMarketingCenterRepeat.Enum.MARKETING_SUCCESS)) {
            MarketingSuccess marketingSuccess = marketingSuccessRepository.findOne(sourceId)
            if (marketingSuccess != null && marketingSuccess.getLicensePlateNo() != null) {
                autoViewModelList.add(AutoViewModel.createViewModel(marketingSuccess, autoService))
            }
        }
        if (autoViewModelList.size() == 0) {
            autoViewModelList.addAll(getAutoInfo(telMarketingCenterRepeat.getSource().getId(), telMarketingCenterRepeat.getUser(), telMarketingCenterRepeat.getMobile()))
        }
        return autoViewModelList
    }

    List<AutoViewModel> getAutoInfo(Long sourceId, User user, String mobile) {
        return telMarketingCenterAutoService.getAutoInfo(sourceId, user, mobile)
    }

    /**
     * 通过手机号查询电销repeat表.
     *
     * @param mobile
     * @return
     */
    private PageViewModel<TelMarketingCenterRepeatViewModel> getTelMarketingCenterRepeatByMobile(String mobile, Pageable pageable) {
        PageViewModel model = new PageViewModel<TelMarketingCenterRepeatViewModel>()
        def repeatPage = telMarketingCenterRepeatRepository.findAllByMobile(mobile, pageable)
        if (repeatPage.totalElements == 0) {
            return null
        }
        List<TelMarketingCenterRepeat> repeatList = repeatPage.getContent()
        List<TelMarketingCenterRepeatViewModel> telMarketingCenterRepeatViewModels = new ArrayList<>(20)
        def start = System.currentTimeMillis()
        for (TelMarketingCenterRepeat telMarketingCenterRepeat : repeatList) {
            if ("marketing_success".equals(telMarketingCenterRepeat.getSourceTable())) {
                ActivityMonitorUrl url = activityMonitorUrlRepository.findByMarketingSuccessId(telMarketingCenterRepeat.getSourceId())
                if (url != null) {
                    BusinessActivity activity = url.getBusinessActivity()
                    telMarketingCenterRepeat.setActiveUrlId(activity.getId())
                    telMarketingCenterRepeat.setActiveUrlSource(url.getSource())
                }
            }
            telMarketingCenterRepeatViewModels.add(TelMarketingCenterRepeatViewModel.createViewModel(telMarketingCenterRepeat, getOrderId(telMarketingCenterRepeat), resourceService))
        }
        println "执行时间:${System.currentTimeMillis() - start}"
        PageInfo pageInfo = createPageInfo(repeatPage)
        model.setPageInfo(pageInfo)
        model.setViewList(telMarketingCenterRepeatViewModels)
        return model
    }


    private Long getOrderId(TelMarketingCenterRepeat telMarketingCenterRepeat) {
        if (StringUtils.isEmpty(telMarketingCenterRepeat.getSourceTable())) {
            return null
        }
        Long orderId = null
        if (telMarketingCenterRepeat.getSourceTable().equals(TelMarketingCenterRepeat.Enum.PURCHASE_ORDER)) {
            orderId = telMarketingCenterRepeat.getSourceId()
        } else if (telMarketingCenterRepeat.getSourceTable().equals(TelMarketingCenterRepeat.Enum.INSURANCE)) {
            orderId = insuranceRepository.findOrderIdByInsuranceId(telMarketingCenterRepeat.getSourceId())
        } else if (telMarketingCenterRepeat.getSourceTable().equals(TelMarketingCenterRepeat.Enum.COMPULSORY_INSURANCE)) {
            orderId = insuranceRepository.findOrderIdByInsuranceId(telMarketingCenterRepeat.getSourceId())
        }
        return orderId
    }

    /**
     * 保存信息
     *
     * @param viewModel
     * @return
     */
    ResultModel save(TelMarketingCenterViewModel viewModel) {
        ResultModel result = new ResultModel()
        try {
            TelMarketingCenter telMarketingCenter = telMarketingCenterRepository.findOne(viewModel.getId())
            TelMarketingCenterStatus status = telMarketingCenterStatusRepository.findOne(viewModel.getStatusId())
            viewModel.setResultDetail("已处理")

            result = telMarketingCenterStatusHandler.request(telMarketingCenter, status, viewModel)

        } catch (Exception e) {
            logger.error("add telMarketingCenter has error", e)
            result.setMessage(e.getMessage())
            result.setPass(false)
        }
        return result
    }

    /**
     * 报价操作
     *
     * @param telMarketingCenterId
     * @param mobile
     * @return
     */
    List<QuotePhone> quote(Long telMarketingCenterId, String mobile) {
        User user = userService.getBindingUser(mobile)
        List<QuotePhone> quotePhoneList = quotePhoneRepository.findByUser(user)
        saveHistoryForAction(telMarketingCenterId, "报价", 3)
        return quotePhoneList
    }

    /**
     * 发短信和报价保存记录
     *
     * @param telMarketingCenterId
     * @param dealResult
     * @return
     */
    ResultModel saveHistoryForAction(Long telMarketingCenterId, String dealResult, int type) {
        ResultModel result = new ResultModel()
        TelMarketingCenter telMarketingCenter = telMarketingCenterRepository.findOne(telMarketingCenterId)
        TelMarketingCenterHistory his = new TelMarketingCenterHistory()
        his.setTelMarketingCenter(telMarketingCenter)
        his.setDealResult(dealResult)
        his.setCreateTime(Calendar.getInstance().getTime())
        his.setOperator(internalUserManageService.getCurrentInternalUser())
        his.setType(type)
        telMarketingCenterHistoryRepository.save(his)
        result.setPass(true)
        return result
    }

    /**
     * 获取呼出记录,短信数量，报价记录，成单记录
     *
     * @return
     */
    PageViewModel<TelMarketingCenterHistoryViewModel> getHistoryRecordsForMobile(TelMarketingCenterRequestParams params) {
        // 操作人id
        Long operatorId = getOperatorId(params.getUserId())
        // 筛选条件总条数
        Long totalElement = telMarketingCenterHistoryRepository.countHistoryDataForMobile(params.getMobileNo(), operatorId)

        PageViewModel model = new PageViewModel<TelMarketingCenterHistoryViewModel>()
        PageInfo pageInfo = new PageInfo()
        pageInfo.setTotalElements(totalElement)
        def totalPage = totalElement % params.getPageSize() == 0 ? totalElement / params.getPageSize() : (totalElement / params.getPageSize() + 1)
        pageInfo.setTotalPage(totalPage.toLong().longValue())
        model.setPageInfo(pageInfo)

        List<TelMarketingCenterHistoryViewModel> pageViewDataList = new ArrayList<>()
        if (totalElement > 0) {
            List<TelMarketingCenterHistory> historyList = telMarketingCenterHistoryRepository.findHistoryDataForMobile(
                    params.getMobileNo(), operatorId, (params.getCurrentPage() - 1) * params.getPageSize(), params.getPageSize())
            pageViewDataList = getHistoryPageViewModel(historyList)
        }
        model.setViewList(pageViewDataList)

        return model
    }

    private List<TelMarketingCenterHistoryViewModel> getHistoryPageViewModel(List<TelMarketingCenterHistory> historyList) {
        List<TelMarketingCenterHistoryViewModel> pageViewDataList = new ArrayList<>()
        if (CollectionUtils.isNotEmpty(historyList)) {
            //是否可以查看所有用户
            boolean hasAllUserPermission = internalUserManageService.hasPermission(Permission.Enum.TEL_MARKETING_CENTER_PERMISSION_ALL_USER)
            for (TelMarketingCenterHistory telMarketingCenterHistory : historyList) {
                TelMarketingCenterHistoryViewModel viewModel = TelMarketingCenterHistoryViewModel.createDetailsViewModel(telMarketingCenterHistory, resourceService)
                viewModel.setIsTelMaster(hasAllUserPermission ? "Y" : "N")
                pageViewDataList.add(viewModel)
            }
        }
        return pageViewDataList
    }

    private List<TelMarketingCenterHistoryViewModel> getHistoryPageViewModelFromObject(List<Object[]> historyObjectList) {
        List<TelMarketingCenterHistoryViewModel> pageViewDataList = new ArrayList<>()
        if (CollectionUtils.isNotEmpty(historyObjectList)) {
            //是否可以查看所有用户
            boolean hasAllUserPermission = internalUserManageService.hasPermission(Permission.Enum.TEL_MARKETING_CENTER_PERMISSION_ALL_USER)
            for (Object[] object : historyObjectList) {
                TelMarketingCenterHistoryViewModel viewModel = TelMarketingCenterHistoryViewModel.getHistoryPageViewModelFromObject(object, resourceService)
                viewModel.setIsTelMaster(hasAllUserPermission ? "Y" : "N")
                pageViewDataList.add(viewModel)
            }
        }
        return pageViewDataList
    }

    private List<Object[]> getObjectPageViewModelFromObject(List<Object[]> objectList) {
        if (CollectionUtils.isNotEmpty(objectList)) {
            for (Object[] obj : objectList) {
                obj[0] = MobileUtil.getEncyptMobile(String.valueOf(obj[0]))
                obj[3] = String.valueOf(obj[3])
                obj[4] = String.valueOf(obj[4])
                obj[9] = String.valueOf(obj[9])
            }
        }
        return objectList
    }

    private List<TelMarketingCenterHistoryViewModel> getHistoryPageViewModelFromCenter(List<TelMarketingCenter> centerList) {
        List<TelMarketingCenterHistoryViewModel> pageViewDataList = new ArrayList<>()
        if (CollectionUtils.isNotEmpty(centerList)) {
            //是否可以查看所有用户
            boolean hasAllUserPermission = internalUserManageService.hasPermission(Permission.Enum.TEL_MARKETING_CENTER_PERMISSION_ALL_USER)
            for (TelMarketingCenter center : centerList) {
                TelMarketingCenterHistoryViewModel viewModel = TelMarketingCenterHistoryViewModel.getHistoryPageViewModelFromCenter(center, resourceService)
                viewModel.setIsTelMaster(hasAllUserPermission ? "Y" : "N")
                pageViewDataList.add(viewModel)
            }
        }
        return pageViewDataList
    }

    /**
     * 获取呼出记录,短信数量，报价记录，成单记录
     *
     * @return
     */
    PageViewModel<TelMarketingCenterHistoryViewModel> getRecords(TelMarketingCenterRequestParams params) {
        PageViewModel model = new PageViewModel<TelMarketingCenterHistoryViewModel>()
        List<TelMarketingCenterHistoryViewModel> pageViewDataList = new ArrayList<>()
        PageInfo pageInfo = null
        if (params.getType() == APPOINTMENT_VAL) {//预约数据
            //triggerTime字段没有索引，排序的话效率不高
            def centerPage = findAppointmentDataBySpecAndPaginate(buildPageable(params.getCurrentPage(), params.getPageSize(), DESC, "triggerTime"), params)
            pageViewDataList = getHistoryPageViewModelFromCenter(centerPage.getContent())
            pageInfo = createPageInfo(centerPage)
        } else if (params.getType() == COMPLETE_ORDER) {//成单记录
            def historyObjectPage = findCompleteOrderBySpecAndPaginate(params)
            pageViewDataList = getHistoryPageViewModelFromObject(historyObjectPage.getContent())
            pageInfo = createPageInfo(historyObjectPage)
        } else if (params.getType() == IN_STORE_NUM) {//数据量
            def inStoreNumPage = findInStoreNumBySpecAndPaginate(params)
            pageInfo = createPageInfo(inStoreNumPage)
            model.setViewList(getObjectPageViewModelFromObject(inStoreNumPage.getContent()))
        } else {
            Page<TelMarketingCenterHistory> page = findWorkDataBySpecAndPaginate(buildPageable(params.getCurrentPage(), params.getPageSize(), DESC, "id"), params)
            pageViewDataList = getHistoryPageViewModel(page.getContent())
            pageInfo = createPageInfo(page)
        }
        if (params.getType() != IN_STORE_NUM)
            model.setViewList(pageViewDataList)
        model.setPageInfo(pageInfo)
        return model
    }

    List getExportExcelData(TelMarketingCenterRequestParams params) {
        List workDetailList = new ArrayList<>()
        Integer startIndex = 0
        Integer pageSize = PAGE_SIZE
        List<Object[]> currentView = getExportRecords(params, startIndex, pageSize)
        while (CollectionUtils.isNotEmpty(currentView)) {
            workDetailList.addAll(setExcelData(currentView))
            if (currentView.size() < PAGE_SIZE)
                break
            startIndex += currentView.size()
            currentView = getExportRecords(params, startIndex, pageSize)
        }
        return workDetailList
    }

    List<Object[]> getExportRecords(TelMarketingCenterRequestParams params, Integer startIndex, Integer pageSize) {
        String sql = buildQuery(params)
        Query query = entityManager.createNativeQuery(sql)
        List<Object[]> currentView = query.setFirstResult(startIndex).setMaxResults(pageSize).getResultList()
        return currentView
    }

    private List setExcelData(List<Object[]> totals) {
        List workDetailList = new ArrayList<>()
        for (Object[] obj : totals) {
            Map workDetailMap = new HashMap<String, Object>()
            workDetailMap.put("mobile", StringUtil.defaultNullStr(obj[0]))
            workDetailMap.put("channel", StringUtil.defaultNullStr(obj[5]))
            workDetailMap.put("type", StringUtil.defaultNullStr(obj[6]))
            workDetailMap.put("expireDate", StringUtil.formatTimeToString(obj[1]))
            workDetailMap.put("isHandled", obj[3] == null ? "未处理" : obj[3].toString().compareTo(StringUtil.defaultNullStr(obj[4])) > 0 ? "已处理" : "未处理")
            workDetailMap.put("operator", StringUtil.defaultNullStr(obj[2]))
            workDetailMap.put("sourceCreateTime", DateUtils.getDateString((Date) obj[9], DateUtils.DATE_LONGTIME24_PATTERN))
            workDetailList.add(workDetailMap)
        }
        return workDetailList
    }

//    private def  findInStoreNumBySpecAndPaginate(TelMarketingCenterRequestParams params) {
//        CriteriaQuery<Object[]> criteriaQuery = findInStoreNumCriteriaQuery(params)
//        Query query = entityManager.createQuery(criteriaQuery)
//        int totals = query.getResultList().size()
//        List<Object[]> currentView = query.setFirstResult((params.getCurrentPage() - 1) * params.getPageSize())
//            .setMaxResults(params.getPageSize()).getResultList()
//        def  page = new PageImpl<Object[]>(currentView, new PageRequest(params.getCurrentPage() - 1, params.getPageSize()), totals)
//        return page
//    }


    def findInStoreNumBySpecAndPaginate(TelMarketingCenterRequestParams params) {
        String sql = buildQuery(params)
        Query query = entityManager.createNativeQuery(sql)
        int totals = query.getResultList().size()
        List<Object[]> currentView = query.setFirstResult((params.getCurrentPage() - 1) * params.getPageSize())
                .setMaxResults(params.getPageSize()).getResultList()
        def page = new PageImpl<Object[]>(currentView, new PageRequest(params.getCurrentPage() - 1, params.getPageSize()), totals)
        return page
    }

    private String buildQuery(TelMarketingCenterRequestParams params) {
        Integer handleMode = params.getHandleMode()
        Integer isHandled = params.getIsHandled()
        Map timeMap = getChangedTime(params)
        StringBuffer sql = new StringBuffer()
        String groupby = ""
        String count = ""
        if (handleMode == 1) {
            groupby = " GROUP BY tmc.mobile "
            count = " count(distinct tmc.mobile), "
        } else if (handleMode == 2) {
            groupby = " GROUP BY `repeat`.id "
            count = " count(distinct `repeat`.id), "
        }
        sql.append("SELECT tmc.mobile,tmc.expire_time,iu.name,max(history.create_time) AS history_time, " +
                "            max(`repeat`.create_time) AS repeat_time,channel.description channel_name, " +
                "            source.description source_name,tmc.id," + count +
                "            `repeat`.source_create_time AS repeat_source_time " +
                "        FROM tel_marketing_center tmc  " +
                "        LEFT JOIN tel_marketing_center_history history ON tmc.id=history.tel_marketing_center  " +
                "        LEFT JOIN internal_user iu ON tmc.operator=iu.id  " +
                "        JOIN tel_marketing_center_repeat `repeat` ON `repeat`.mobile = tmc.mobile " +
                "        LEFT JOIN channel ON `repeat`.channel=channel.id " +
                "        JOIN tel_marketing_center_source source on `repeat`.source = source.id ")

        //根据开通城市查询
        if (ArrayUtils.isNotEmpty(params.getAreaId())) {
            sql.append("        JOIN user_login_info ulogin ON tmc.`user`=ulogin.`user`  " +
                    "        JOIN mobile_area marea ON tmc.mobile=marea.mobile " +
                    "        JOIN area ON ( marea.area=area.id OR ulogin.area=area.id) ")
        }
        sql.append("        WHERE 1=1 ")
        if (StringUtils.isNotBlank(params.getExpireTime())) {
            String startDate = DateUtils.getDateString(DateUtils.getCustomDate(DateUtils.getDate(params.getExpireTime(), DateUtils.DATE_SHORTDATE_PATTERN), -90, 0, 0, 0), DateUtils.DATE_SHORTDATE_PATTERN)
            String endDate = DateUtils.getDateString(DateUtils.getDate(params.getExpireTime(), DateUtils.DATE_SHORTDATE_PATTERN), DateUtils.DATE_SHORTDATE_PATTERN)
            sql.append(" AND tmc.expire_time BETWEEN  '" + startDate + "' AND '" + endDate + "' ")
        }
        //拼接repeat表的渠道
        if (ArrayUtils.isNotEmpty(params.getChannelIds())) {
            List<Long> ids = Channel.getDataSourceChannel(params.getChannelIds())
            sql.append(" AND `repeat`.channel in (" + StringUtils.join(ids, ",") + "  ) ")
        }
        //拼接repeat的类型
        if (ArrayUtils.isNotEmpty(params.getTelTypes())) {
            sql.append(" AND source.type in (" + StringUtils.join(params.getTelTypes(), ",") + ") ")
        }
        //根据开通城市查询
        if (ArrayUtils.isNotEmpty(params.getAreaId())) {
            sql.append(" AND area.id in ( " + StringUtils.join(params.getAreaId(), ",") + " ) ")
        }
        sql.append(" AND `repeat`.create_time BETWEEN '" + DateUtils.getDateString((Date) timeMap.get("startTime"), DateUtils.DATE_LONGTIME24_START_PATTERN) + "' AND '" + DateUtils.getDateString((Date) timeMap.get("endTime"), DateUtils.DATE_LONGTIME24_END_PATTERN) + "' ")

        if (isHandled != null) {
            if (isHandled == 1) {
                sql.append(" AND history_time IS NOT NULL AND history_time > `repeat`.source_create_time AND `repeat`.status IS NOT NULL " + groupby)
                groupby = ""
            } else if (isHandled == 0) {
                sql.append(groupby + " HAVING history_time IS NULL OR history_time<=`repeat`.source_create_time ")
                groupby = ""
            }
        }
        sql.append(groupby)
        sql.append(" ORDER BY `repeat`.id DESC ")

        return sql.toString()
    }

//    private CriteriaQuery<Object[]> findInStoreNumCriteriaQuery(TelMarketingCenterRequestParams params) {
//        Integer handleMode = params.getHandleMode()
//        Map timeMap = getChangedTime(params)
//        CriteriaBuilder cb = entityManager.getCriteriaBuilder()
//        CriteriaQuery<Object[]> criteriaQuery = cb.createQuery(Object[].class)
//        Root<TelMarketingCenter> root = criteriaQuery.from(TelMarketingCenter.class)
//        List<Predicate> predicateList = new ArrayList<>()
//        Root<TelMarketingCenterRepeat> repeatRoot = criteriaQuery.from(TelMarketingCenterRepeat.class)
//        Join<TelMarketingCenter, TelMarketingCenterHistory> historyJoin = root.join("historyList", JoinType.LEFT)
//        //主表与子表关联
//        predicateList.add(cb.equal(repeatRoot.get("mobile"), root.get("mobile")))
//        //center表left join关联internalUser表获取对应的跟进人
//        Join<TelMarketingCenter, InternalUser> internalUserJoin = root.join("operator", JoinType.LEFT)
//        //经生产数据查看，repeat表中的source不会为空，但是在source为41时，channel为空，因此需要对Chanel进行left join关联，而不是join
//        Join<TelMarketingCenterRepeat, Channel> channelJoin = repeatRoot.join("channel", JoinType.LEFT)
//        //拼接子表创建时间段的参数
//        predicateList.add(cb.between(repeatRoot.get("createTime"),
//            cb.literal((Date) timeMap.get("startTime")), cb.literal((Date) timeMap.get("endTime"))))
//        List<Long> channels = Channel.Enum.getDataSourceChannel(params.getChannelIds())
//        if (CollectionUtils.isNotEmpty(channels)) {
//            //拼接repeat表的渠道
//            CriteriaBuilder.In<Long> channelIn = cb.in(repeatRoot.get("channel").get("id"))
//            for (Long channel : channels)
//                channelIn.value(channel)
//            predicateList.add(channelIn)
//        }
//        //拼接repeat的类型
//        if (params.getTelTypes().length != 0) {
//            Root sourceRoot = criteriaQuery.from(TelMarketingCenterSource.class)
//            predicateList.add(cb.equal(repeatRoot.get("source").get("id"), sourceRoot.get("id")))
//            CriteriaBuilder.In<Long> typeIdIn = cb.in(sourceRoot.get("type"))
//            for (String channelId : params.getTelTypes()) {
//                typeIdIn.value(Long.parseLong(channelId))
//            }
//            predicateList.add(typeIdIn)
//        }
//        //根据开通城市查询
//        if (ArrayUtils.isNotEmpty(params.getAreaId())) {
//            Root areaRoot = criteriaQuery.from(Area.class)
//            Root loginInfoRoot = criteriaQuery.from(UserLoginInfo.class)
//            Root mobileAreaRoot = criteriaQuery.from(MobileArea.class)
//            predicateList.add(cb.equal(root.get("mobile"), mobileAreaRoot.get("mobile")))
//            predicateList.add(cb.equal(root.get("user"), loginInfoRoot.get("user")))
//            predicateList.add(cb.or(cb.equal(mobileAreaRoot.get("area").get("id"), areaRoot.get("id")), cb.equal(loginInfoRoot.get("area").get("id"), areaRoot.get("id"))))
//            CriteriaBuilder.In<Long> areaIdIn = cb.in(areaRoot.get("id"))
//            for (Long areaId : params.getAreaId()) {
//                areaIdIn.value(areaId)
//            }
//            predicateList.add(areaIdIn)
//        }
//        //是否已处理；已处理的标识为history的创建时间大于子表的创建时间
//        Integer isHandled = params.getIsHandled()
//        if (isHandled != null) {
//            if (isHandled == 1) {
//                predicateList.add(cb.and(cb.isNotNull(historyJoin.get("createTime")), cb.greaterThan(historyJoin.get("createTime"), repeatRoot.get("createTime"))))
//                predicateList.add(cb.isNotNull(historyJoin.get("status")))
//            } else if (isHandled == 0) {
//                predicateList.add(cb.or(cb.isNull(historyJoin.get("createTime")), cb.lessThanOrEqualTo(historyJoin.get("createTime"), repeatRoot.get("createTime"))))
//            }
//        }
//        Predicate[] predicates = new Predicate[predicateList.size()]
//        List<Selection<?>> selectionList = new ArrayList<>()
//        selectionList.add(root.get("mobile"))
//        selectionList.add(root.get("expireTime"))
//        selectionList.add(internalUserJoin.get("name"))
//        selectionList.add(cb.max(historyJoin.get("createTime")))
//        selectionList.add(cb.max(repeatRoot.get("createTime")))
//        selectionList.add(channelJoin.get("description"))
//        selectionList.add(repeatRoot.get("source").get("description"))
//        selectionList.add(root.get("id"))
//        if (handleMode == 1) {
//            selectionList.add(cb.countDistinct(root.get("mobile")))
//            criteriaQuery.groupBy(root.get("mobile"))
//        } else if (handleMode == 2) {
//            selectionList.add(cb.countDistinct(repeatRoot.get("id")))
//            criteriaQuery.groupBy(repeatRoot.get("id"))
//        }
//        selectionList.add(repeatRoot.get("sourceCreateTime"))
//        predicates = predicateList.toArray(predicates)
//        criteriaQuery.multiselect(selectionList).where(predicates).orderBy(cb.desc(repeatRoot.get("id")))
//        return criteriaQuery
//    }

    private def findCompleteOrderBySpecAndPaginate(TelMarketingCenterRequestParams params) {
        Long operatorId = getOperatorId(params.getUserId())
        Map timeMap = getChangedTime(params)
        CriteriaBuilder cb = entityManager.getCriteriaBuilder()
        CriteriaQuery<Object[]> criteriaQuery = cb.createQuery(Object[].class)
        Root<TelMarketingCenterHistory> root = criteriaQuery.from(TelMarketingCenterHistory.class)
        List<Predicate> predicateList = new ArrayList<>()
        criteriaQuery.distinct(true)
        //history表left join表centerOrder
        ListJoin<TelMarketingCenterHistory, TelMarketingCenterOrder> centerOrderJoin = root.joinList("centerOrderList", JoinType.LEFT)
        //centerOrder表left join表order
        Join<TelMarketingCenterOrder, PurchaseOrder> orderJoin = centerOrderJoin.join("purchaseOrder", JoinType.LEFT)
        //order表left join表orderOperationInfo
        Join<PurchaseOrder, OrderOperationInfo> infoJoin = orderJoin.join("orderOperationInfo", JoinType.LEFT)
        //info表left join表status
        Join<OrderOperationInfo, OrderTransmissionStatus> statusJoin = infoJoin.join("currentStatus", JoinType.LEFT)
        //history表join表center
        Join<TelMarketingCenterHistory, TelMarketingCenter> centerJoin = root.join("telMarketingCenter")
        //history表join表internalUser
        Join<TelMarketingCenterHistory, InternalUser> internalUserJoin = root.join("operator")
        //center表left join表user
        Join<TelMarketingCenter, User> userJoin = centerJoin.join("user", JoinType.LEFT)
        //user表left join表Channel
        Join<User, Channel> channelJoin = userJoin.join("registerChannel", JoinType.LEFT)
        predicateList.add(cb.equal(root.get("type"), COMPLETE_ORDER))//类型为4的表示成单
        if (operatorId != null)
            predicateList.add(cb.equal(root.get("operator").get("id"), operatorId))
        if (params.getOrderStatus().length != 0) {
            CriteriaBuilder.In<Long> orderStatusIn = cb.in(statusJoin.get("id"))
            for (Long status : params.getOrderStatus())
                orderStatusIn.value(status)
            predicateList.add(orderStatusIn)
        }
        predicateList.add(cb.between(root.get("createTime"),
                cb.literal((Date) timeMap.get("startTime")), cb.literal((Date) timeMap.get("endTime"))))
        Predicate[] predicates = new Predicate[predicateList.size()]
        predicates = predicateList.toArray(predicates)
        Query query = entityManager.createQuery(criteriaQuery.multiselect(root.get("id"), internalUserJoin.get("name"), userJoin.get("mobile"),
                root.get("createTime"), channelJoin.get("icon"), orderJoin.get("orderNo"), statusJoin.get("description")).where(predicates).orderBy(cb.desc(root.get("id"))))
        int totals = query.getResultList().size()
        List<Object[]> currentView = query.setFirstResult((params.getCurrentPage() - 1) * params.getPageSize())
                .setMaxResults(params.getPageSize()).getResultList()
        def page = new PageImpl<Object[]>(currentView, new PageRequest(params.getCurrentPage() - 1, params.getPageSize()), totals)
        return page
    }

    private Page<TelMarketingCenterHistory> findWorkDataBySpecAndPaginate(Pageable pageable, TelMarketingCenterRequestParams params) {
        return telMarketingCenterHistoryRepository.findAll(new Specification<TelMarketingCenterHistory>() {
            @Override
            Predicate toPredicate(Root<TelMarketingCenterHistory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Map timeMap = getChangedTime(params)
                // 操作人id
                Long operatorId = getOperatorId(params.getUserId())
                CriteriaQuery<TelMarketingCenterHistory> criteriaQuery = cb.createQuery(TelMarketingCenterHistory.class)
                List<Predicate> predicateList = new ArrayList<Predicate>()
                query.distinct(true)

                Root<TelMarketingCenter> centerRoot = query.from(TelMarketingCenter.class)
                //拼表关联
                //a) history和center主表关联
                predicateList.add(cb.equal(root.get("telMarketingCenter").get("id"), centerRoot.get("id")))

                //拼参数
                //1) 拼接history的type参数
                CriteriaBuilder.In<Integer> historyTypeIn = cb.in(root.get("type"))
                for (Integer type : params.getTypeList()) {
                    historyTypeIn.value(type)
                }
                predicateList.add(historyTypeIn)
                //2) 拼接center主表的状态
                if (params.getOperationStatusId() != null)
                    predicateList.add(cb.equal(centerRoot.get("status").get("id"), params.getOperationStatusId()))
                //3) 拼接history的operator
                if (operatorId != null)
                    predicateList.add(cb.equal(root.get("operator").get("id"), operatorId))
                //4) 拼接history表的status
                if (params.getStatus() != null)
                    predicateList.add(cb.equal(root.get("status").get("id"), params.getStatus()))
                //5) 拼接history的createTime
                predicateList.add(cb.between(root.get("createTime"),
                        cb.literal((Date) timeMap.get("startTime")), cb.literal((Date) timeMap.get("endTime"))))

                List<Long> channels = Channel.getDataSourceChannel(params.getChannelIds())
                if (params.getTelTypes().length != 0 || CollectionUtils.isNotEmpty(channels)) {
                    Root<TelMarketingCenterRepeat> repeatRoot = query.from(TelMarketingCenterRepeat.class)
                    //b) center主表和repeat子表关联
                    predicateList.add(cb.equal(centerRoot.get("mobile"), repeatRoot.get("mobile")))
                    //6) 拼接source的type
                    if (params.getTelTypes().length != 0) {
                        Root<TelMarketingCenterSource> sourceRoot = query.from(TelMarketingCenterSource.class)
                        //center主表通过mobile关联repeat子表，repeat子表关联source表
                        //c) repeat子表和source表关联
                        predicateList.add(cb.equal(repeatRoot.get("source").get("id"), sourceRoot.get("id")))
                        //6) 拼接source的type
                        CriteriaBuilder.In<Integer> sourceTypeIn = cb.in(sourceRoot.get("type"))
                        for (String str : params.getTelTypes())
                            sourceTypeIn.value(Integer.valueOf(str))
                        predicateList.add(sourceTypeIn)
                    }
                    if (CollectionUtils.isNotEmpty(channels)) {
                        //7) 拼接repeat表的channel
                        CriteriaBuilder.In<Long> channelIn = cb.in(repeatRoot.get("channel").get("id"))
                        for (Long channel : channels)
                            channelIn.value(channel)
                        predicateList.add(channelIn)
                    }
                }
                Predicate[] predicates = new Predicate[predicateList.size()]
                predicates = predicateList.toArray(predicates)
                return criteriaQuery.where(predicates).getRestriction()
            }
        }, pageable)
    }

    private def findAppointmentDataBySpecAndPaginate(Pageable pageable, TelMarketingCenterRequestParams params) {
        return telMarketingCenterRepository.findAll(new Specification<TelMarketingCenter>() {
            @Override
            Predicate toPredicate(Root<TelMarketingCenter> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Map timeMap = getChangedTime(params)
                // 操作人id
                Long operatorId = getOperatorId(params.getUserId())
                List<Predicate> predicateList = new ArrayList<Predicate>()
                CriteriaQuery<TelMarketingCenter> criteriaQuery = cb.createQuery(TelMarketingCenter.class)
                predicateList.add(cb.between(root.get("triggerTime"),
                        cb.literal((Date) timeMap.get("startTime")), cb.literal((Date) timeMap.get("endTime"))))
                if (operatorId != null)
                    predicateList.add(cb.equal(root.get("operator").get("id"), operatorId))
                Predicate[] predicates = new Predicate[predicateList.size()]
                predicates = predicateList.toArray(predicates)
                return criteriaQuery.where(predicates).getRestriction()
            }
        }, pageable)
    }

    private Long getOperatorId(Long operator) {
        Long operatorId = null//操作人id
        //是否可以查看所有用户
        boolean hasAllUserPermission = internalUserManageService.hasPermission(Permission.Enum.TEL_MARKETING_CENTER_PERMISSION_ALL_USER)
        //当前用户没有查看所有用户权限，则只能查看自己处理的
        if (!hasAllUserPermission) {
            operatorId = internalUserManageService.getCurrentInternalUser().getId()
        }
        //当前用户有可以查看所有用户权限
        else {
            if (operator != null) {
                operatorId = operator
            }
        }
        return operatorId
    }

    PageViewModel<TelMarketingCenterHistoryViewModel> createResult(Page page) {
        PageViewModel model = new PageViewModel<TelMarketingCenterHistoryViewModel>()
        PageInfo pageInfo = new PageInfo()
        pageInfo.setTotalElements(page.getTotalElements())
        pageInfo.setTotalPage(page.getTotalPages())
        model.setPageInfo(pageInfo)

        List<TelMarketingCenterHistoryViewModel> pageViewDataList = new ArrayList<>()
        List<TelMarketingCenterHistory> historyList = (List<TelMarketingCenterHistory>) page.getContent()
        pageViewDataList = getHistoryPageViewModel(historyList)
        model.setViewList(pageViewDataList)

        return model
    }

    /**
     * 获取整体情况
     *
     * @return
     */
    TelMarketingCenterHistoryViewModel getWholeSituation(TelMarketingCenterRequestParams params) throws Exception {
        Date sTime = new Date(), eTime = new Date()
        Map timeMap = getChangedTime(params)
        sTime = (Date) timeMap.get("startTime")
        eTime = (Date) timeMap.get("endTime")

        int callCount = 0
        if (params.getStatus() == null || params.getStatus() != 1) {
            callCount = getCount(params.getStatus(), params.getSource(), params.getUserId(), sTime, eTime, 3).size()
//call总次数,要把未处理的除外
        }

        int isEffectiveCount = getCount(params.getStatus(), params.getSource(), params.getUserId(), sTime, eTime, 1).size()
//有效
        int noEffectiveCount = getCount(params.getStatus(), params.getSource(), params.getUserId(), sTime, eTime, 0).size()
//无效

        List<TelMarketingCenter> tmcList = getCount(TelMarketingCenterStatus.Enum.ORDER.getId(), params.getSource(), params.getUserId(), sTime, eTime, 2)
        int orderCount = tmcList.size()//成单总数
        /**
         * 成单操作时记录时间-新建时间）总和(毫秒)
         */
        long timeDiffSum = 0L
        for (TelMarketingCenter telMarketingCenter : tmcList) {//得到总分钟差
            long timeDiff = telMarketingCenter.getUpdateTime().getTime() - telMarketingCenter.getCreateTime().getTime()
            timeDiffSum += timeDiff
        }

        //状态为60的也就是成单操作总次数
        int orderOperatorCount = getHisToryCount(params.getStatus(), params.getSource(), params.getUserId(), sTime, eTime, 1).size()

        TelMarketingCenterHistoryViewModel telMarketingCenterHistoryViewModel = new TelMarketingCenterHistoryViewModel()

        //拼接已拨打
        if (callCount > 0) {
            telMarketingCenterHistoryViewModel.setCallPercentage("[['已拨：" + callCount + "'," + callCount + "]]")
        }

        //拼接有无效数据
        if (isEffectiveCount > 0 || noEffectiveCount > 0) {
            telMarketingCenterHistoryViewModel.setEffectivePercentage("[['有效数据：" + isEffectiveCount + "'," + isEffectiveCount + "],['无效数据：" + noEffectiveCount + "'," + noEffectiveCount + "]]")
        }

        //平均*分成单
        String orderAverageCountStr = "0"
        if (orderCount != 0) {
            orderAverageCountStr = new java.text.DecimalFormat("#.00").format(timeDiffSum / (60000 * orderCount))
            telMarketingCenterHistoryViewModel.setAverageOrderTime("[['平均" + orderAverageCountStr + "分成单' ," + orderAverageCountStr + "]]")
        }

        //平均接通*次成单
        String averageStr = "0"
        if (orderCount != 0) {
            averageStr = new java.text.DecimalFormat("#.00").format(orderOperatorCount / orderCount)
            telMarketingCenterHistoryViewModel.setOrderByCallTimes("[['平均接通" + averageStr + " 次成单'," + averageStr + "]]")
        }

        //数据状况
        List<Tuple> statusList = getCountByStatusOrSource(params, "status")
        List<Tuple> sourceList = getCountByStatusOrSource(params, "source")
        StringBuffer statusName = new StringBuffer(), statusNameCount = new StringBuffer(), sourceName = new StringBuffer(), sourceNameCount = new StringBuffer(), sourcePercent = new StringBuffer()
        List<TelMarketingCenterStatus> listAllStatus = telMarketingCenterResource.listAllStatus()
        for (TelMarketingCenterStatus telMarketingCenterStatus : listAllStatus) {
            statusName.append("'" + telMarketingCenterStatus.getName() + "',")
            statusNameCount.append(getStatusY(telMarketingCenterStatus, statusList) + ",")
        }

        String statusNameStr = statusName.length() > 0 ? statusName.substring(0, statusName.length() - 1).toString() : ""
        String statusNameCountStr = statusNameCount.length() > 0 ? statusNameCount.substring(0, statusNameCount.length() - 1).toString() : ""
        statusNameStr = "[" + statusNameStr + "]"
        statusNameCountStr = "[" + statusNameCountStr + "]"
        telMarketingCenterHistoryViewModel.setStatusX(statusNameStr)
        telMarketingCenterHistoryViewModel.setStatusY(statusNameCountStr)


        DecimalFormat format = new DecimalFormat("#.##")
        if (sourceList.size() > 0) {
            for (int i = 0; i < sourceList.size(); i++) {
                Tuple tuple = sourceList.get(i)
                sourceName.append("'" + (String) tuple.get(0) + "',")
                sourceNameCount.append((Long) tuple.get(1) + ",")
                Double dPercent = Double.parseDouble(format.format((Long) tuple.get(1))) / Double.parseDouble(format.format(orderCount))
                sourcePercent.append(format.format(dPercent * 100) + ",")
            }
            String sourceNameStr = sourceName.substring(0, sourceName.length() - 1).toString()
            String sourceNameCountStr = sourceNameCount.substring(0, sourceNameCount.length() - 1).toString()
            String sourcePercentStr = sourcePercent.substring(0, sourcePercent.length() - 1).toString()
            // String  =
            sourceNameStr = "[" + sourceNameStr + "]"
            sourceNameCountStr = "[" + sourceNameCountStr + "]"
            sourcePercentStr = "[" + sourcePercentStr + "]"
            telMarketingCenterHistoryViewModel.setOrderX(sourceNameStr)
            telMarketingCenterHistoryViewModel.setOrderY(sourceNameCountStr)
            telMarketingCenterHistoryViewModel.setOrderPercent(sourcePercentStr)
        }

        return telMarketingCenterHistoryViewModel
    }

    /**
     * 获取电销中心表相关信息
     *
     * @param status
     * @param source
     * @param userId
     * @param sTime
     * @param eTime
     * @param isEffective
     * @return
     * @throws Exception
     */
    private List<TelMarketingCenter> getCount(Long status, Long source, Long userId, Date sTime, Date eTime, Integer isEffective) throws Exception {
        return telMarketingCenterRepository.findAll(new Specification<TelMarketingCenter>() {
            @Override
            Predicate toPredicate(Root<TelMarketingCenter> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<TelMarketingCenter> criteriaQuery = cb.createQuery(TelMarketingCenter.class)

                Path<String> operatorPath = root.get("operator").get("id")
                Path<Date> updateTimePath = root.get("updateTime")
                Path<Long> statusPath = root.get("status")
                Path<Integer> sourcePath = root.get("source")
                List<Predicate> predicateList = new ArrayList<>()

                if (userId != null) {
                    predicateList.add(
                            cb.equal(operatorPath, userId)
                    )
                }
                if (source != null) {
                    predicateList.add(
                            cb.equal(sourcePath, source)
                    )
                }
                if (isEffective == 3) {//call总次数
                    if (status == null) {//如果不选状态，则是除了未处理的的状态数据
                        predicateList.add(
                                cb.notEqual(statusPath, 1)
                        )
                    } else {//如果选了状态，则查处选出的状态
                        predicateList.add(
                                cb.equal(statusPath, status)
                        )
                    }
                }
                if (isEffective == 2) {//成单总数，不受状态
                    predicateList.add(
                            cb.equal(statusPath, status)
                    )
                }

                if (isEffective == 0 || isEffective == 1) {
                    List<Long> effectiveList = new ArrayList<Long>()
                    if (isEffective == 1) {//有效'20','30','40','60'
                        effectiveList.clear()
                        effectiveList.add(TelMarketingCenterStatus.Enum.ALREADY_OFFER.getId())
                        effectiveList.add(TelMarketingCenterStatus.Enum.ALREADY_NOTIFY.getId())
                        effectiveList.add(TelMarketingCenterStatus.Enum.OVERDUE_CONTACT.getId())
                        effectiveList.add(TelMarketingCenterStatus.Enum.ORDER.getId())
                    } else {//无效//'10','50','70','80','90','91','92'
                        effectiveList.clear()
                        effectiveList.add(TelMarketingCenterStatus.Enum.ALREADY_ORDER.getId())
                        effectiveList.add(TelMarketingCenterStatus.Enum.NO_OPEN_CITY.getId())
                        effectiveList.add(TelMarketingCenterStatus.Enum.REFUSE.getId())
                        effectiveList.add(TelMarketingCenterStatus.Enum.NOT_OWNER.getId())
                        effectiveList.add(TelMarketingCenterStatus.Enum.CANNOT_CONNECT.getId())
                        effectiveList.add(TelMarketingCenterStatus.Enum.HANG_UP.getId())
                        effectiveList.add(TelMarketingCenterStatus.Enum.OTHER_STATUS.getId())
                    }
                    CriteriaBuilder.In<Long> statusIn = cb.in(statusPath)
                    for (Long statusId : effectiveList) {
                        statusIn.value(statusId)
                    }
                    predicateList.add(statusIn)
                }

                Expression<Date> startDateExpression = cb.literal(sTime)
                Expression<Date> endDateExpression = cb.literal(eTime)
                predicateList.add(
                        cb.between(updateTimePath, startDateExpression, endDateExpression)
                )

                Predicate[] predicates = new Predicate[predicateList.size()]
                predicates = predicateList.toArray(predicates)
                return criteriaQuery.where(predicates).getRestriction()
            }
        })
    }

    /**
     * 获取电销中心操作历史表相关信息
     *
     * @param status
     * @param source
     * @param userId
     * @param sTime
     * @param eTime
     * @param isOrder
     * @return
     * @throws Exception
     */
    private List<TelMarketingCenterHistory> getHisToryCount(Long status, Long source, Long userId, Date sTime, Date eTime, Integer isOrder) throws Exception {
        return telMarketingCenterHistoryRepository.findAll(new Specification<TelMarketingCenterHistory>() {
            @Override
            Predicate toPredicate(Root<TelMarketingCenterHistory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<TelMarketingCenterHistory> criteriaQuery = cb.createQuery(TelMarketingCenterHistory.class)

                Path<String> operatorPath = root.get("operator").get("id")
                Path<Date> createTimePath = root.get("createTime")
                Path<Date> telMarketingCenterPath = root.get("telMarketingCenter")
                List<Predicate> predicateList = new ArrayList<>()

                if (userId != null) {
                    predicateList.add(
                            cb.equal(operatorPath, userId)
                    )
                }
                if (isOrder == 1) {//表示只查成单的
                    predicateList.add(
                            cb.equal(telMarketingCenterPath.get("status"), TelMarketingCenterStatus.Enum.ORDER.getId())
                    )
                } else if (status != null) {
                    predicateList.add(
                            cb.equal(telMarketingCenterPath.get("status"), status)
                    )
                }

                if (source != null) {
                    predicateList.add(
                            cb.equal(telMarketingCenterPath.get("source"), source)
                    )
                }
                query.groupBy()
                Expression<Date> startDateExpression = cb.literal(sTime)
                Expression<Date> endDateExpression = cb.literal(eTime)
                predicateList.add(
                        cb.between(createTimePath, startDateExpression, endDateExpression)
                )

                Predicate[] predicates = new Predicate[predicateList.size()]
                predicates = predicateList.toArray(predicates)
                return criteriaQuery.where(predicates).getRestriction()
            }
        })
    }

    /**
     * 柱状图数据来源
     *
     * @return
     * @throws Exception
     */
    private List<Tuple> getCountByStatusOrSource(TelMarketingCenterRequestParams params, String type) throws Exception {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder()
        Date sTime = new Date(), eTime = new Date()
        CriteriaQuery<Tuple> query = cb.createTupleQuery()
        Root<TelMarketingCenter> root = query.from(TelMarketingCenter.class)
        Path<Integer> statusPath = root.get("status")
        Path<Integer> sourcePath = root.get("source")
        Path<Integer> sourceNamePath = root.get("source").get("name")
        Path<Date> updateTimePath = root.get("updateTime")
        Path<Date> operatorPath = root.get("operator")

        List<Predicate> predicateList = new ArrayList<>()
        if ("source".equals(type)) {//来源查询，查出来的都是成单的
            predicateList.add(
                    cb.equal(statusPath, TelMarketingCenterStatus.Enum.ORDER.getId())
            )
        } else if (params.getStatus() != null) {
            predicateList.add(
                    cb.equal(statusPath, params.getStatus())
            )
        }
        if (params.getSource() != null) {
            predicateList.add(
                    cb.equal(sourcePath, params.getSource())
            )
        }
        if (params.getUserId() != null) {
            predicateList.add(
                    cb.equal(operatorPath, params.getUserId())
            )
        }
        Map timeMap = getChangedTime(params)
        sTime = (Date) timeMap.get("startTime")
        eTime = (Date) timeMap.get("endTime")
        if (params.getTimeSlot() != null
                || (StringUtils.isNotBlank(params.getStartTime()) && StringUtils.isNotBlank(params.getEndTime()))) {
            Expression<Date> startDateExpression = cb.literal(sTime)
            Expression<Date> endDateExpression = cb.literal(eTime)
            predicateList.add(
                    cb.between(updateTimePath, startDateExpression, endDateExpression)
            )
        }

        Predicate[] predicates = new Predicate[predicateList.size()]
        predicates = predicateList.toArray(predicates)
        query.where(predicates)
        if ("status".equals(type)) {//查询status信息
            query.select(cb.tuple(statusPath, cb.count(root)))
            query.groupBy(statusPath)
        }
        if ("source".equals(type)) {//查询source信息
            query.select(cb.tuple(sourceNamePath, cb.count(root)))
            query.groupBy(sourcePath)
        }
        //query.multiselect(statusPath, cb.count(root))//
        TypedQuery<Tuple> q = entityManager.createQuery(query)
        List<Tuple> result = q.getResultList()
        return result
    }

    /**
     * 获取时间，如果选了起始和结束时间，则查询范围在起始和结束之间，如果选了时间类型，比如今天，昨天，需要时间转换
     *
     * @return
     */
    private Map getChangedTime(TelMarketingCenterRequestParams params) {
        String startTimeStr = params.getStartTime()
        String endTimeStr = params.getEndTime()
        Integer timeSlot = params.getTimeSlot()
        Map timeMap = new HashMap()
        Date nowDate = new Date(), startTime = new Date(), endTime = new Date()
        Calendar calendar = Calendar.getInstance()  //得到日历
        if (timeSlot != null) {
            switch (timeSlot) {
                case 1://今天
                    startTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_START_PATTERN)
                    endTime = nowDate
                    break
                case 2://昨天
                    calendar.setTime(new Date())//把当前时间赋给日历
                    calendar.add(Calendar.DAY_OF_MONTH, -1)  //设置为前一天
                    startTime = DateUtils.getDate(calendar.getTime(), DateUtils.DATE_LONGTIME24_START_PATTERN)
                    endTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_START_PATTERN)
                    break
                case 3://最近7天
                    calendar.setTime(new Date())//把当前时间赋给日历
                    calendar.add(Calendar.DAY_OF_MONTH, -7)  //设置为前7天
                    startTime = DateUtils.getDate(calendar.getTime(), DateUtils.DATE_LONGTIME24_PATTERN)
                    endTime = nowDate
                    break
                case 4://最近一个月
                    calendar.setTime(new Date())//把当前时间赋给日历
                    calendar.add(Calendar.DAY_OF_MONTH, -30)  //设置为前一个月
                    startTime = DateUtils.getDate(calendar.getTime(), DateUtils.DATE_LONGTIME24_PATTERN)
                    endTime = nowDate
                    break
            }
        }
        if (StringUtils.isNotBlank(startTimeStr) && StringUtils.isNotBlank(endTimeStr)) {
            startTime = DateUtils.getDate(startTimeStr, DateUtils.DATE_LONGTIME24_PATTERN)
            endTime = DateUtils.getDate(endTimeStr, DateUtils.DATE_LONGTIME24_PATTERN)
        }
        timeMap.put("startTime", startTime)
        timeMap.put("endTime", endTime)
        return timeMap
    }

    private Integer getStatusY(TelMarketingCenterStatus telMarketingCenterStatus, List<Tuple> statusList) {
        Integer result = 0
        for (int i = 0; i < statusList.size(); i++) {
            Tuple tuple = statusList.get(i)
            TelMarketingCenterStatus tmkStatus = (TelMarketingCenterStatus) tuple.get(0)
            if (telMarketingCenterStatus.getId().equals(tmkStatus.getId())) {
                result = Integer.valueOf((Long) tuple.get(1) + "")
            }
        }
        return result
    }

    PageViewModel<TelMarketingCenterRepeatViewModel> getTelMarketingCenterRepeat(Long centerId, TelMarketingCenterRequestParams params) {
        TelMarketingCenter telMarketingCenter = telMarketingCenterRepository.findOne(centerId)
        return getTelMarketingCenterRepeatByMobile(telMarketingCenter.getMobile(), buildPageable(params.getCurrentPage(), params.getPageSize(), DESC, "createTime"))
    }

    TelMarketingCenterRepeat getRepeatByMobile(String mobile) {
        return telMarketingCenterRepeatRepository.findFirstByMobileOrderByCreateTimeDesc(mobile)
    }
}
