package com.cheche365.cheche.ordercenter.service.order;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.model.agent.ChannelAgent;
import com.cheche365.cheche.core.model.agent.ChannelAgentPurchaseOrderRebate;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.ordercenter.web.model.InternalUserData;
import com.cheche365.cheche.ordercenter.web.model.order.OrderFilterRequestParams;
import com.cheche365.cheche.ordercenter.web.model.order.StopRestartOrderViewData;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import javax.persistence.criteria.CriteriaBuilder.In;
import java.util.*;

/**
 * Created by sunhuazhong on 2015/5/13.
 */
@Service(value = "orderFilterService")
public class OrderFilterService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;

    @Autowired
    private InternalUserRoleRepository internalUserRoleRepository;

    @Autowired
    private InsuranceCompanyRepository insuranceCompanyRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    DailyInsuranceRepository dailyInsuranceRepository;

    @Autowired
    DailyRestartInsuranceRepository dailyRestartInsuranceRepository;

    @Autowired
    private UserRepository userRepository;


    /*根据PurchaseOrder查询导出订单信息*/
    public List<Object[]> findDeliveryInfo(String[] checkedIds) {
        List<Object[]> exportData = purchaseOrderRepository.findDataByPurchaseOrderId(Arrays.asList(checkedIds));
        return exportData;
    }

    public Page filterOrders(OrderFilterRequestParams reqParams) {
        try {
            Page<OrderOperationInfo> operationInfoPage = this.filterBySpecAndPaginate(reqParams,
                    this.buildPageable(reqParams.getCurrentPage(), reqParams.getPageSize()));
            return operationInfoPage;
        } catch (Exception e) {
            logger.error("filter order has error", e);
        }
        return null;
    }

    private Specification<OrderOperationInfo> createSpecification(OrderFilterRequestParams reqParams) {
        return new Specification<OrderOperationInfo>() {
            @Override
            public Predicate toPredicate(Root<OrderOperationInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<OrderOperationInfo> criteriaQuery = cb.createQuery(OrderOperationInfo.class);

                //条件构造
                List<Predicate> predicateList = new ArrayList<>();
                predicateList.add(cb.notEqual(root.get("purchaseOrder").get("type").get("id"), OrderType.Enum.HEALTH.getId()));
                Long[] orderSourceTypeIds = {6L, 7L};//暂时排除线下导入和泛华接口同步的订单
                CriteriaBuilder.In<Object> orderSourceTypeIn = cb.in(root.get("purchaseOrder").get("orderSourceType").get("id"));
                for (Long orderSourceTypeId : orderSourceTypeIds) {
                    orderSourceTypeIn.value(orderSourceTypeId);
                }

                predicateList.add(cb.or(
                        cb.isNull(root.get("purchaseOrder").get("orderSourceType")),
                        cb.not(orderSourceTypeIn)
                ));
                /**
                 * 精准查找
                 */
                findByPrecision(root, query, cb, predicateList, reqParams);

                /**
                 * 平台类查找
                 */
                findByPlatform(root, cb, predicateList, reqParams, query);

                /**
                 * 按地区查找
                 */
                findByArea(root, cb, predicateList, reqParams);

                /**
                 * 按指定人查找
                 */
                findByPerson(root, cb, predicateList, reqParams);

                /**
                 * 按渠道查找
                 */
                findByChannel(root, cb, predicateList, reqParams);

                /**
                 * 按状态查找
                 */
                findByStatus(root, cb, predicateList, reqParams);

                /**
                 * 按日期查找
                 */
                findByDate(root, cb, predicateList, reqParams);

                //orderSourceType 按照报价来源查找
                findByOrderSourceType(root, cb, predicateList, reqParams);

                //按照广告来源查询
                findByOrderSourceId(root, cb, predicateList, reqParams);

                /**
                 * 订单统计查询
                 */
                addCountCondition(root, cb, predicateList, reqParams);
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        };
    }

    private void findByOrderSourceType(Root<OrderOperationInfo> root, CriteriaBuilder cb, List<Predicate> predicateList, OrderFilterRequestParams reqParams) {
        if ((reqParams.getOrderSourceType() != null)) {
            Path<OrderSourceType> orderSourceTypePath = root.get("purchaseOrder").get("orderSourceType");
            predicateList.add(cb.equal(orderSourceTypePath, reqParams.getOrderSourceType()));
        }
    }

    /**
     * 按照广告来源查询的话，orderSourceType为1，orderSourceId为选择广告来源的ID
     **/
    private void findByOrderSourceId(Root<OrderOperationInfo> root, CriteriaBuilder cb, List<Predicate> predicateList, OrderFilterRequestParams reqParams) {
        if ((reqParams.getOrderSourceId() != null)) {
            Path<Long> orderSourceTypePath = root.get("purchaseOrder").get("orderSourceType").get("id");
            Path<Long> orderSourceIdPath = root.get("purchaseOrder").get("orderSourceId");//广告来源id
            predicateList.add(cb.equal(orderSourceTypePath, 1));
            predicateList.add(cb.equal(orderSourceIdPath, reqParams.getOrderSourceId()));
        }
    }

    public Long countOrders(OrderFilterRequestParams reqParams) {
        return orderOperationInfoRepository.count(createSpecification(reqParams));
    }

    public Page<OrderOperationInfo> filterBySpecAndPaginate(OrderFilterRequestParams reqParams, Pageable pageable) {
        return orderOperationInfoRepository.findAll(createSpecification(reqParams), pageable);
    }

    private void addCountCondition(Root<OrderOperationInfo> root, CriteriaBuilder cb, List<Predicate> predicateList, OrderFilterRequestParams reqParams) {
        if (StringUtils.isEmpty(reqParams.getCountField())) {
            return;
        }
        Path<Long> orderStatusIdPath = root.get("purchaseOrder").get("status").get("id");
        In<Long> orderStatusIdIn = cb.in(orderStatusIdPath);
        if (reqParams.getCountField().equals(CountField.PAID_ORDER_COUNT.getValue())) {
            OrderStatus.Enum.paidStatus().forEach(paidStatus -> orderStatusIdIn.value(paidStatus.getId()));
        } else if (reqParams.getCountField().equals(CountField.ALREADY_PAID_ORDER_COUNT.getValue())) {
            OrderStatus.Enum.paidEdStatus().forEach(paidStatus -> orderStatusIdIn.value(paidStatus.getId()));
        }
        predicateList.add(orderStatusIdIn);
    }

    private void findByDate(Root<OrderOperationInfo> root, CriteriaBuilder cb, List<Predicate> predicateList, OrderFilterRequestParams reqParams) {
        // 下单日期
        if (StringUtils.isNotBlank(reqParams.getOrderStartDate()) && StringUtils.isNotBlank(reqParams.getOrderEndDate())) {
            Path<Date> createTimePath = root.get("purchaseOrder").get("createTime");//下单时间
            Date startDate = DateUtils.getDayStartTime(DateUtils.getDate(reqParams.getOrderStartDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            Date endDate = DateUtils.getDayEndTime(DateUtils.getDate(reqParams.getOrderEndDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            Expression<Date> startDateExpression = cb.literal(startDate);
            Expression<Date> endDateExpression = cb.literal(endDate);
            predicateList.add(cb.between(createTimePath, startDateExpression, endDateExpression));
        }
        //操作时间
        if (StringUtils.isNotBlank(reqParams.getOperateStartDate()) && StringUtils.isNotBlank(reqParams.getOperateEndDate())) {
            Path<Date> updateTimePath = root.get("updateTime");//操作时间
            Date startDate = DateUtils.getDayStartTime(DateUtils.getDate(reqParams.getOperateStartDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            Date endDate = DateUtils.getDayEndTime(DateUtils.getDate(reqParams.getOperateEndDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            Expression<Date> startDateExpression = cb.literal(startDate);
            Expression<Date> endDateExpression = cb.literal(endDate);
            predicateList.add(cb.between(updateTimePath, startDateExpression, endDateExpression));
        }
    }

    private void findByStatus(Root<OrderOperationInfo> root, CriteriaBuilder cb, List<Predicate> predicateList, OrderFilterRequestParams reqParams) {
        // 出单状态
        if (reqParams.getStatus() != null) {
            Path<Long> statusPath = root.get("currentStatus").get("id");
            Path<Long> referencePath = root.get("currentStatus").get("reference").get("id");
            predicateList.add(cb.or(
                    cb.equal(statusPath, reqParams.getStatus()),
                    cb.equal(referencePath, reqParams.getStatus())
            ));
        }
        //订单状态
        if (null != reqParams.getOrderStatus()) {
            Path<Long> orderStatusIdPath = root.get("purchaseOrder").get("status").get("id");//订单状态
            if (reqParams.getOrderStatus() == 0) {
                predicateList.add(cb.isNotNull(orderStatusIdPath));
            } else {
                predicateList.add(cb.equal(orderStatusIdPath, reqParams.getOrderStatus()));
            }
        }
        //支付方式
        if (null != reqParams.getPaymentChannel()) {
            Path<Long> paymentChannelIdPath = root.get("purchaseOrder").get("channel").get("id");//支付方式
            if (reqParams.getPaymentChannel() == 0) {
                predicateList.add(cb.isNotNull(paymentChannelIdPath));
            } else {
                predicateList.add(cb.equal(paymentChannelIdPath, reqParams.getPaymentChannel()));
            }
        }

    }

    private void findByChannel(Root<OrderOperationInfo> root, CriteriaBuilder cb, List<Predicate> predicateList, OrderFilterRequestParams reqParams) {


        // CPS渠道
        if (reqParams.getCpsChannel() != null) {
            Path<Long> orderSourceIdPath = root.get("purchaseOrder").get("orderSourceId");//订单来源对象id
            Path<OrderSourceType> orderSourceTypePath = root.get("purchaseOrder").get("orderSourceType");//订单来源类型，区分CPS渠道，大客户，滴滴专车增补保险
            predicateList.add(cb.equal(orderSourceTypePath, OrderSourceType.Enum.CPS_CHANNEL_1));
            if (reqParams.getCpsChannel() == 0) {
                predicateList.add(cb.isNotNull(orderSourceIdPath));
            } else {
                predicateList.add(cb.equal(orderSourceIdPath, reqParams.getCpsChannel()));
            }
        }

    }

    private void findByPerson(Root<OrderOperationInfo> root, CriteriaBuilder cb, List<Predicate> predicateList, OrderFilterRequestParams reqParams) {
        // 指定人
        if (reqParams.getAssigner() != null) {
            Path<Long> assignerPath = root.get("assigner").get("id");//指定人
            if (reqParams.getAssigner() == 0) {
                predicateList.add(cb.isNotNull(assignerPath));
            } else {
                predicateList.add(cb.equal(assignerPath, reqParams.getAssigner()));
            }
        }
        // 推荐人
        if (reqParams.getAgent() != null) {
            Path<Long> userTypePath = root.get("purchaseOrder").get("applicant").get("userType").get("id");// 用户类型id
            Path<Long> userPath = root.get("purchaseOrder").get("applicant").get("id");// 用户id
            predicateList.add(cb.equal(userTypePath, UserType.Enum.Agent.getId()));
            if (reqParams.getAgent() == 0) {
                predicateList.add(cb.isNotNull(userPath));
            } else {
                predicateList.add(cb.equal(userPath, agentRepository.findOne(reqParams.getAgent()).getUser().getId()));
            }
        }
    }

    private void findByArea(Root<OrderOperationInfo> root, CriteriaBuilder cb, List<Predicate> predicateList, OrderFilterRequestParams reqParams) {
        if (reqParams.getQuoteArea() != null) {
            Path<Long> areaPath = root.get("purchaseOrder").get("auto").get("area").get("id");
            Path<OrderType> orderTypePath = root.get("purchaseOrder").get("type");
            predicateList.add(cb.equal(orderTypePath, OrderType.Enum.INSURANCE.getId()));
            if (reqParams.getQuoteArea() == 0) {
                predicateList.add(cb.isNotNull(areaPath));
            } else {
                predicateList.add(cb.equal(areaPath, reqParams.getQuoteArea()));
            }
        }
    }

    private void findByPlatform(Root<OrderOperationInfo> root, CriteriaBuilder cb, List<Predicate> predicateList, OrderFilterRequestParams reqParams, CriteriaQuery<?> query) {
        // 保险公司
        if (reqParams.getInsuranceCompany() != null) {
            /**
             *  root.get("purchaseOrder").get("objId")
             *  这种的底层实现为orderOperationInfo表cross join表purchaseOrder；形成一个笛卡尔积，效率相对来说不高
             *  因此修改为orderOperationInfo表join表purchaseOrder；
             */
            Join<OrderOperationInfo, PurchaseOrder> orderJoin = root.join("purchaseOrder");
            Path<Long> objIdPath = orderJoin.get("objId");//报价记录id
            if (reqParams.getInsuranceCompany() == 0) {
                predicateList.add(cb.isNotNull(objIdPath));
            } else {
                Root<QuoteRecord> recordRoot = query.from(QuoteRecord.class);
                Join<QuoteRecord, InsuranceCompany> InsuranceCompanyJoin = recordRoot.join("insuranceCompany");
                predicateList.add(cb.equal(objIdPath, recordRoot.get("id")));
                predicateList.add(cb.equal(InsuranceCompanyJoin.get("id"), reqParams.getInsuranceCompany()));
            }
        }
        // 产品平台
        if (reqParams.getChannel() != null) {
            Path<Long> sourceChannelPath = root.get("purchaseOrder").get("sourceChannel").get("id");//来源
            if (reqParams.getChannel() == 0) {
                predicateList.add(cb.isNotNull(sourceChannelPath));
            } else {
                predicateList.add(cb.equal(sourceChannelPath, reqParams.getChannel()));
            }
        }
    }

    private void findByPrecision(Root<OrderOperationInfo> root, CriteriaQuery query, CriteriaBuilder cb, List<Predicate> predicateList, OrderFilterRequestParams reqParams) {

        //收件人
        if (StringUtils.isNotEmpty(reqParams.getReceiveUser())) {
            predicateList.add(cb.equal(root.get("purchaseOrder").get("deliveryAddress").get("name"), reqParams.getReceiveUser()));
        } else if (StringUtils.isNotEmpty(reqParams.getInsurance())) {//投保人
            Root<Insurance> InsuranceRoot = query.from(Insurance.class);
            predicateList.add(cb.equal(root.get("purchaseOrder").get("objId"), InsuranceRoot.get("quoteRecord").get("id")));
            predicateList.add(cb.equal(InsuranceRoot.get("applicantName"), reqParams.getInsurance()));
        } else if (StringUtils.isNotEmpty(reqParams.getInsuranced())) {//被保险人
            Root<Insurance> InsuranceRoot = query.from(Insurance.class);
            predicateList.add(cb.equal(root.get("purchaseOrder").get("objId"), InsuranceRoot.get("quoteRecord").get("id")));
            predicateList.add(cb.equal(InsuranceRoot.get("insuredName"), reqParams.getInsuranced()));
        } else if (StringUtils.isNotEmpty(reqParams.getOwner())) {//车主
            predicateList.add(cb.equal(root.get("purchaseOrder").get("auto").get("owner"), reqParams.getOwner()));
        }

        // 手机号
        if (StringUtils.isNotEmpty(reqParams.getMobile())) {
            Path<String> mobilePath = root.get("purchaseOrder").get("applicant").get("mobile");
            predicateList.add(cb.like(mobilePath, reqParams.getMobile() + "%"));
        }
        // 车牌号
        if (StringUtils.isNotEmpty(reqParams.getLicenseNo())) {
            Path<String> licenseNoPath = root.get("purchaseOrder").get("auto").get("licensePlateNo");
            predicateList.add(cb.like(licenseNoPath, reqParams.getLicenseNo() + "%"));
        }
        // 订单号
        if (StringUtils.isNotBlank(reqParams.getOrderNo())) {
            Path<String> orderNoPath = root.get("purchaseOrder").get("orderNo");
            predicateList.add(cb.equal(orderNoPath, reqParams.getOrderNo()));
        }
        // 邀请人
        if (StringUtils.isNotBlank(reqParams.getInviter())) {
            Join<OrderOperationInfo, PurchaseOrder> purchaseOrderJoin = root.join("purchaseOrder");
            Root<ChannelAgentPurchaseOrderRebate> rebateRoot = query.from(ChannelAgentPurchaseOrderRebate.class);
            Join<ChannelAgentPurchaseOrderRebate, ChannelAgent> channelAgentRoot = rebateRoot.join("channelAgent");
            Path<String> inviter = channelAgentRoot.get("user").get("name");
            predicateList.add(cb.equal(inviter, reqParams.getInviter()));
            CriteriaBuilder.In<T> orderIn = cb.in(purchaseOrderJoin.get("id"));
            orderIn.value(rebateRoot.get("purchaseOrder"));
            predicateList.add(orderIn);
        }
        // 间接邀请人
        if (StringUtils.isNotBlank(reqParams.getIndirectionInviter())) {
            Join<OrderOperationInfo, PurchaseOrder> purchaseOrderJoin = root.join("purchaseOrder");
            Root<ChannelAgentPurchaseOrderRebate> rebateRoot = query.from(ChannelAgentPurchaseOrderRebate.class);
            Join<ChannelAgentPurchaseOrderRebate, ChannelAgent> channelAgentRoot = rebateRoot.join("channelAgent");
            Path<String> indirectionInviter = channelAgentRoot.get("parent").get("user").get("name");
            predicateList.add(cb.equal(indirectionInviter, reqParams.getIndirectionInviter()));
            CriteriaBuilder.In<T> orderIn = cb.in(purchaseOrderJoin.get("id"));
            orderIn.value(rebateRoot.get("purchaseOrder"));
            predicateList.add(orderIn);
        }
    }

    /**
     * 构建分页信息
     *
     * @param currentPage 当前页面
     * @param pageSize    每页显示数
     * @return Pageable
     */
    public Pageable buildPageable(int currentPage, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        return new PageRequest(currentPage - 1, pageSize, sort);
    }

    public List<InsuranceCompany> listAllInsuranceCompany() {
        // 获取所有的保险公司
        Iterable<InsuranceCompany> insuranceCompanyIterable = insuranceCompanyRepository.findAll();
        Iterator<InsuranceCompany> insuranceCompanyIterator = insuranceCompanyIterable.iterator();

        List<InsuranceCompany> resultList = new ArrayList<>();
        List<Long> insuranceCompanyIdList = new ArrayList<>();
        while (insuranceCompanyIterator.hasNext()) {
            InsuranceCompany insuranceCompany = insuranceCompanyIterator.next();
            if (insuranceCompany != null && !insuranceCompanyIdList.contains(insuranceCompany.getId())) {
                resultList.add(insuranceCompany);
            }
        }
        return resultList;
    }

    public List<InternalUserData> listAllCustomer() {
        // 获取所有的客服角色的内部用户
        Role customerRole = Role.Enum.INTERNAL_USER_ROLE_CUSTOMER;
        List<InternalUserRole> customerInternalUserRoles = internalUserRoleRepository.findByRole(customerRole);
        List<InternalUserData> resultList = new ArrayList<>();
        List<Long> internalUserIdList = new ArrayList<>();
        for (InternalUserRole internalUserRole : customerInternalUserRoles) {
            InternalUser internalUser = internalUserRole.getInternalUser();
            if (internalUser != null && !internalUserIdList.contains(internalUser.getId())) {
                InternalUserData data = new InternalUserData();
                data.setId(internalUser.getId());
                data.setName(internalUser.getName());
                resultList.add(data);
                internalUserIdList.add(internalUser.getId());
            }
        }
        return resultList;
    }

    public List<OrderTransmissionStatus> listAllStatus() {
        return OrderTransmissionStatus.Enum.ALLSTATUS;
    }

    public static enum CountField {
        PAID_ORDER_COUNT("paid_order"), ALREADY_PAID_ORDER_COUNT("already_paid_order");

        private final String value;

        CountField(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }


    public Page filterStopRestartOrders(OrderFilterRequestParams reqParams) {
        try {
            CriteriaQuery<Object[]> criteriaQuery = findDataCriteriaQuery(reqParams);
            Query query = entityManager.createQuery(criteriaQuery);

            int totals = query.getResultList().size();
            List<Object[]> currentData = query.setFirstResult((reqParams.getCurrentPage() - 1) * reqParams.getPageSize())
                    .setMaxResults(reqParams.getPageSize()).getResultList();
            Page<Object[]> operationInfoPage = new PageImpl<Object[]>(currentData, new PageRequest(reqParams.getCurrentPage() - 1, reqParams.getPageSize()), totals);
            return operationInfoPage;
        } catch (Exception e) {
            logger.error("filter order has error", e);
        }
        return null;
    }


    private CriteriaQuery<Object[]> findDataCriteriaQuery(OrderFilterRequestParams params) {
        List<Predicate> predicateList = new ArrayList<>();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = cb.createQuery(Object[].class);
        Root<DailyInsurance> stopRoot = criteriaQuery.from(DailyInsurance.class);
        predicateList.add(cb.notEqual(stopRoot.get("status").get("id"), DailyInsuranceStatus.Enum.STOP_CALCULATE.getId()));//状态不为1（停驶试算）
        Join<DailyInsurance, PurchaseOrder> orderJoin = stopRoot.join("purchaseOrder");
        Join<PurchaseOrder, OrderSourceType> sourceJoin = orderJoin.join("orderSourceType", JoinType.LEFT);
        Join<PurchaseOrder, Auto> autoJoin = orderJoin.join("auto");
        Root<QuoteRecord> quoteRoot = criteriaQuery.from(QuoteRecord.class);
        Join<QuoteRecord, Area> areaJoin = quoteRoot.join("area");

        Root<Insurance> insuranceRoot = criteriaQuery.from(Insurance.class);
        predicateList.add(cb.equal(orderJoin.get("objId"), quoteRoot.get("id")));
        predicateList.add(cb.equal(quoteRoot.get("id"), insuranceRoot.get("quoteRecord").get("id")));

        Join<PurchaseOrder, Channel> channelJoin = orderJoin.join("sourceChannel");

        //停驶日期
        if (StringUtils.isNotBlank(params.getStopBeginDate()) && StringUtils.isNotBlank(params.getStopEndDate())) {
            predicateList.add(cb.between(stopRoot.get("createTime"),
                    cb.literal(DateUtils.getDayStartTime(DateUtils.getDate(params.getStopBeginDate(), DateUtils.DATE_SHORTDATE_PATTERN))),
                    cb.literal(DateUtils.getDayEndTime(DateUtils.getDate(params.getStopEndDate(), DateUtils.DATE_SHORTDATE_PATTERN)))));
        }
        //复驶日期
        if (StringUtils.isNotBlank(params.getRestartBeginDate()) && StringUtils.isNotBlank(params.getRestartEndDate())) {
            Root<DailyRestartInsurance> restartRoot = criteriaQuery.from(DailyRestartInsurance.class);
            predicateList.add(cb.equal(stopRoot.get("id"), restartRoot.get("dailyInsurance").get("id")));
            predicateList.add(cb.between(restartRoot.get("createTime"),
                    cb.literal(DateUtils.getDayStartTime(DateUtils.getDate(params.getRestartBeginDate(), DateUtils.DATE_SHORTDATE_PATTERN))),
                    cb.literal(DateUtils.getDayEndTime(DateUtils.getDate(params.getRestartEndDate(), DateUtils.DATE_SHORTDATE_PATTERN)))));
        }

        // 下单日期
        if (StringUtils.isNotBlank(params.getOrderStartDate()) && StringUtils.isNotBlank(params.getOrderEndDate())) {
            Path<Date> createTimePath = orderJoin.get("createTime");//下单时间
            Date startDate = DateUtils.getDayStartTime(DateUtils.getDate(params.getOrderStartDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            Date endDate = DateUtils.getDayEndTime(DateUtils.getDate(params.getOrderEndDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            Expression<Date> startDateExpression = cb.literal(startDate);
            Expression<Date> endDateExpression = cb.literal(endDate);
            predicateList.add(cb.between(createTimePath, startDateExpression, endDateExpression));
        }
        //操作时间
        if (StringUtils.isNotBlank(params.getOperateStartDate()) && StringUtils.isNotBlank(params.getOperateEndDate())) {
            Root<OrderOperationInfo> operationRoot = criteriaQuery.from(OrderOperationInfo.class);
            predicateList.add(cb.equal(orderJoin.get("id"), operationRoot.get("purchaseOrder").get("id")));

            Path<Date> updateTimePath = operationRoot.get("updateTime");//操作时间
            Date startDate = DateUtils.getDayStartTime(DateUtils.getDate(params.getOperateStartDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            Date endDate = DateUtils.getDayEndTime(DateUtils.getDate(params.getOperateEndDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            Expression<Date> startDateExpression = cb.literal(startDate);
            Expression<Date> endDateExpression = cb.literal(endDate);
            predicateList.add(cb.between(updateTimePath, startDateExpression, endDateExpression));
        }

        //城市
        if (params.getQuoteArea() != null) {
            Path<OrderType> orderTypePath = orderJoin.get("type");
            predicateList.add(cb.equal(orderTypePath, OrderType.Enum.INSURANCE.getId()));
            if (params.getQuoteArea() == 0) {
                predicateList.add(cb.isNotNull(areaJoin.get("id")));
            } else {
                predicateList.add(cb.equal(areaJoin.get("id"), params.getQuoteArea()));
            }
        }


        // CPS渠道
        if (params.getCpsChannel() != null) {
            Path<Long> orderSourceIdPath = orderJoin.get("orderSourceId");//订单来源对象id
            Path<OrderSourceType> orderSourceTypePath = orderJoin.get("orderSourceType");//订单来源类型，区分CPS渠道，大客户，滴滴专车增补保险
            predicateList.add(cb.equal(orderSourceTypePath, OrderSourceType.Enum.CPS_CHANNEL_1));
            if (params.getCpsChannel() == 0) {
                predicateList.add(cb.isNotNull(orderSourceIdPath));
            } else {
                predicateList.add(cb.equal(orderSourceIdPath, params.getCpsChannel()));
            }
        }

        //收件人
        if (StringUtils.isNotEmpty(params.getReceiveUser())) {
            predicateList.add(cb.equal(orderJoin.get("deliveryAddress").get("name"), params.getReceiveUser()));
        } else if (StringUtils.isNotEmpty(params.getInsurance())) {//投保人
            predicateList.add(cb.equal(orderJoin.get("objId"), insuranceRoot.get("quoteRecord").get("id")));
            predicateList.add(cb.equal(insuranceRoot.get("applicantName"), params.getInsurance()));
        } else if (StringUtils.isNotEmpty(params.getInsuranced())) {//被保险人
            predicateList.add(cb.equal(orderJoin.get("objId"), insuranceRoot.get("quoteRecord").get("id")));
            predicateList.add(cb.equal(insuranceRoot.get("insuredName"), params.getInsuranced()));
        } else if (StringUtils.isNotEmpty(params.getOwner())) {//车主
            predicateList.add(cb.equal(orderJoin.get("auto").get("owner"), params.getOwner()));
        }

        // 手机号
        if (StringUtils.isNotEmpty(params.getMobile())) {
            Path<String> mobilePath = orderJoin.get("applicant").get("mobile");
            predicateList.add(cb.like(mobilePath, params.getMobile() + "%"));
        }
        // 车牌号
        if (StringUtils.isNotEmpty(params.getLicenseNo())) {
            Path<String> licenseNoPath = orderJoin.get("auto").get("licensePlateNo");
            predicateList.add(cb.like(licenseNoPath, params.getLicenseNo() + "%"));
        }
        // 订单号
        if (StringUtils.isNotBlank(params.getOrderNo())) {
            Path<String> orderNoPath = orderJoin.get("orderNo");
            predicateList.add(cb.equal(orderNoPath, params.getOrderNo()));
        }


        Predicate[] predicates = new Predicate[predicateList.size()];
        List<Selection<?>> selectionList = new ArrayList<>();
        selectionList.add(orderJoin.get("id"));
        selectionList.add(orderJoin.get("createTime"));
        selectionList.add(orderJoin.get("orderNo"));
        selectionList.add(autoJoin.get("owner"));
        selectionList.add(autoJoin.get("licensePlateNo"));
        selectionList.add(areaJoin.get("name"));
        selectionList.add(channelJoin.get("name"));
        selectionList.add(sourceJoin.get("name"));
        selectionList.add(orderJoin.get("orderSourceId"));
        selectionList.add(orderJoin.get("paidAmount"));
        selectionList.add(insuranceRoot.get("premium"));
        selectionList.add(stopRoot.get("status").get("description"));
        selectionList.add(cb.count(stopRoot.get("id")));//13
        //selectionList.add(cb.sum(cb.diff(stopRoot.get("beginDate"), stopRoot.get("endDate"))));//14
        //selectionList.add(cb.sum(cb.diff(restartRoot.get("beginDate"), stopRoot.get("endDate"))));//15
        selectionList.add(cb.sum(stopRoot.get("totalRefundAmount")));//14

        criteriaQuery.groupBy(orderJoin.get("id"));

        predicates = predicateList.toArray(predicates);
        criteriaQuery.multiselect(selectionList).where(predicates).orderBy(cb.desc(orderJoin.get("id")));
        return criteriaQuery;

    }

    public List<Object[]> findStopDataByIds(List<String> idList) {
        return dailyInsuranceRepository.findStopDataByIds(idList);
    }

    public List<Object[]> findRestartDataByIds(List<String> idList) {
        return dailyRestartInsuranceRepository.findRestartDataByIds(idList);
    }


    public StopRestartOrderViewData getStopRestartOrderViewData(Object[] obj, Map<String, String> stopMap, Map<String, String> restartMap) {
        StopRestartOrderViewData stopRestartOrderViewData = new StopRestartOrderViewData();
        stopRestartOrderViewData.setId(Long.valueOf(obj[0].toString()));
        stopRestartOrderViewData.setCreateTime(obj[1].toString().substring(0, 19));
        stopRestartOrderViewData.setOrderNo(obj[2].toString());
        stopRestartOrderViewData.setOwner(obj[3].toString());
        stopRestartOrderViewData.setLicenseNo(obj[4].toString());
        stopRestartOrderViewData.setQuoteArea(obj[5].toString());
        stopRestartOrderViewData.setChannel(obj[6].toString());
        if (obj[7] != null) {
            if (String.valueOf(obj[7]).equals(OrderSourceType.Enum.CPS_CHANNEL_1.getName()) && (String.valueOf(obj[8]).equals("407") || String.valueOf(obj[8]).equals("408"))) {
                stopRestartOrderViewData.setOrderSource("微车CPS");
            } else {
                stopRestartOrderViewData.setOrderSource(String.valueOf(obj[7]));
            }
        } else {
            stopRestartOrderViewData.setOrderSource("");
        }
        stopRestartOrderViewData.setPaidAmount(Double.parseDouble(obj[9].toString()));
        stopRestartOrderViewData.setPremium(Double.parseDouble(obj[10].toString()));
        stopRestartOrderViewData.setStatus(obj[11].toString());
        stopRestartOrderViewData.setStopNum(Integer.parseInt(obj[12].toString()));
        //stopRestartOrderViewData.setTotalStopDays((int)Double.parseDouble(obj[14].toString()) + Integer.parseInt(obj[13].toString()));
        //stopRestartOrderViewData.setTotalRestartDays(Integer.parseInt(obj[15].toString())+Integer.parseInt(obj[13].toString()));
        stopRestartOrderViewData.setTotalRefundAmount(Double.parseDouble(obj[13].toString()));
        if (stopMap.get(obj[0].toString()) != null) {
            String[] lastStopData = stopMap.get(obj[0].toString()).split(",");
            stopRestartOrderViewData.setLastStopBeginDate(lastStopData[0]);
            stopRestartOrderViewData.setLastStopDays(Integer.parseInt(lastStopData[1]));
        }
        if (restartMap.get(obj[0].toString()) != null) {
            stopRestartOrderViewData.setLastRestartBeginDate(restartMap.get(obj[0].toString()));
        }
        return stopRestartOrderViewData;
    }

    /*搜索选中的停复驶订单信息并导出*/
    public List<Object[]> findCheckedOrderInfo(String[] checkedIds) {
        List<Object[]> stopRestartData = dailyInsuranceRepository.findDataByOrderIds(Arrays.asList(checkedIds));
        return stopRestartData;
    }

}
