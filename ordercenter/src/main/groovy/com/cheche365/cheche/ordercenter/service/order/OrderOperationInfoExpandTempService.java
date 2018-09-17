package com.cheche365.cheche.ordercenter.service.order;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.OrderOperationInfoRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.service.OrderOperationInfoService;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangfei on 2015/12/16.
 */
@Service("orderOperationInfoExpandTempService")
@Transactional
public class OrderOperationInfoExpandTempService extends OrderOperationInfoService {
    private Logger logger = LoggerFactory.getLogger(OrderOperationInfoExpandTempService.class);

    @Autowired
    private BaseService baseService;

    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;

    @Autowired
    private IInternalUserManageService internalUserManageService;

    @Autowired
    private OrderManageService orderManageService;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    private static final String SORT_UPDATE_TIME = "updateTime";
    private static final String SORT_CREATE_TIME = "createTime";
    private static final Integer PAYMENT_STATUS_SUCCESS = 2;
    private static final Integer PAYMENT_STATUS_UNSUCCESS = 1;

    private static final String PURCHASE_ORDER_TEMP_KEY_MAP = "purchaseOrder.id.temp.key.map";
    private static final String PURCHASE_ORDERS_TEMP_KEYS = "purchaseOrder.ids.temp.keys.";

    public String getNewPurchaseOrderId(PurchaseOrder purchaseOrder) {
        QuoteRecord quoteRecord = orderManageService.getQuoteRecordByPurchaseOrder(purchaseOrder);

        //已经存在的，直接返回上次用的
        String existId = (String)stringRedisTemplate.opsForHash().get(PURCHASE_ORDER_TEMP_KEY_MAP, purchaseOrder.getId().toString());
        if (StringUtils.isNotBlank(existId)) {
            return existId;
        }

        //所有已用数据
        Set<Long> orderIdSet = redisTemplate.opsForSet().members(PURCHASE_ORDERS_TEMP_KEYS + quoteRecord.getInsuranceCompany().getId());

        PurchaseOrder enableOrder;
        //备用演示数据
        if (!CollectionUtils.isEmpty(orderIdSet)) {
            enableOrder = purchaseOrderRepository.findTempDataNotIn(quoteRecord.getInsuranceCompany().getId(), orderIdSet);
            if (null == enableOrder) {
                //清空redis set
                redisTemplate.delete(PURCHASE_ORDERS_TEMP_KEYS + quoteRecord.getInsuranceCompany().getId());
                enableOrder = purchaseOrderRepository.findTempData(quoteRecord.getInsuranceCompany().getId());
            }
        } else {
            enableOrder = purchaseOrderRepository.findTempData(quoteRecord.getInsuranceCompany().getId());
        }

        //放进已用演示数据set
        redisTemplate.opsForSet().add(PURCHASE_ORDERS_TEMP_KEYS + quoteRecord.getInsuranceCompany().getId(), enableOrder.getId());
        redisTemplate.expire(PURCHASE_ORDERS_TEMP_KEYS + quoteRecord.getInsuranceCompany(), 60, TimeUnit.DAYS);

        //放进对应关系
        stringRedisTemplate.opsForHash().put(PURCHASE_ORDER_TEMP_KEY_MAP, purchaseOrder.getId().toString(), enableOrder.getId().toString());
        stringRedisTemplate.expire(PURCHASE_ORDER_TEMP_KEY_MAP, 60, TimeUnit.DAYS);

        return enableOrder.getId().toString();
    }


    public Page<OrderOperationInfo> getOrdersByPage(PublicQuery query) {
        return findBySpecAndPaginate(baseService.buildPageable(query.getCurrentPage(), query.getPageSize(),
                Sort.Direction.DESC, getSortName(query.getSort())), query);
    }

    private String getSortName(String sort) {
        if (SORT_UPDATE_TIME.equals(sort)) {
            return "updateTime";
        } else if (SORT_CREATE_TIME.equals(sort)) {
            return "createTime";
        }
        return "updateTime";
    }

    private Page<OrderOperationInfo> findBySpecAndPaginate(Pageable pageable, PublicQuery publicQuery) {
        return orderOperationInfoRepository.findAll(new Specification<OrderOperationInfo>() {
            @Override
            public Predicate toPredicate(Root<OrderOperationInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<OrderOperationInfo> criteriaQuery = cb.createQuery(OrderOperationInfo.class);
                List<Predicate> predicateList = new ArrayList<>();

                /*boolean hasAdminPermission = internalUserManageService.hasPermission(Permission.Enum.INTERNAL_USER_PERMISSION_ALL_ORDER);
                if (!hasAdminPermission) {
                    //非管理员查询当前用户的订单
                    InternalUser internalUser = internalUserManageService.getCurrentInternalUser();
                    Path<Long> orderOwnerPath = root.get("owner").get("id");
                    predicateList.add(cb.equal(orderOwnerPath, internalUser.getId()));
                }*/

                //车主
                if (StringUtils.isNotBlank(publicQuery.getOwner())) {
                    Path<String> ownerPath = root.get("purchaseOrder").get("auto").get("owner");
                    predicateList.add(cb.like(ownerPath, publicQuery.getOwner() + "%"));
                }
                //车牌号
                if (StringUtils.isNotBlank(publicQuery.getLicensePlateNo())) {
                    Path<String> licensePlateNoPath = root.get("purchaseOrder").get("auto").get("licensePlateNo");
                    predicateList.add(cb.like(licensePlateNoPath, publicQuery.getLicensePlateNo() + "%"));
                }
                //手机号
                if (StringUtils.isNotBlank(publicQuery.getMobile())) {
                    Path<String> mobilePath = root.get("purchaseOrder").get("applicant").get("mobile");
                    predicateList.add(cb.like(mobilePath, publicQuery.getMobile() + "%"));
                }
                //订单号
                if (StringUtils.isNotBlank(publicQuery.getOrderNo())) {
                    Path<String> orderNoPath = root.get("purchaseOrder").get("orderNo");
                    predicateList.add(cb.like(orderNoPath, publicQuery.getOrderNo() + "%"));
                }
                //订单状态
                if (null != publicQuery.getOrderStatus()) {
                    Path<Long> orderStatusPath = root.get("currentStatus").get("id");
                    Path<Long> referencePath = root.get("currentStatus").get("reference").get("id");
                    predicateList.add(cb.or(
                        cb.equal(orderStatusPath, publicQuery.getOrderStatus()),
                        cb.equal(referencePath, publicQuery.getOrderStatus())
                    ));
                }
                //地区
                if (null != publicQuery.getArea()) {
                    Path<Long> areaPath = root.get("purchaseOrder").get("auto").get("area").get("id");
                    predicateList.add(cb.equal(areaPath, publicQuery.getArea()));
                }
                //支付渠道
                if (null != publicQuery.getPaymentChannel()) {
                    Path<Long> channelPath = root.get("purchaseOrder").get("channel").get("id");
                    predicateList.add(cb.equal(channelPath, publicQuery.getPaymentChannel()));
                }

                //下单平台
                if (null != publicQuery.getSourceChannel()) {
                    Path<Long> sourceChannelPath = root.get("purchaseOrder").get("sourceChannel").get("id");
                    predicateList.add(cb.equal(sourceChannelPath, publicQuery.getSourceChannel()));
                }

                //保险公司

                if (publicQuery.getInsuranceCompany() != null) {
                    Join<OrderOperationInfo, PurchaseOrder> orderJoin = root.join("purchaseOrder");
                    Path<Long> objIdPath = orderJoin.get("objId");//报价记录id
                    Root<QuoteRecord> recordRoot = query.from(QuoteRecord.class);
                    Join<QuoteRecord, InsuranceCompany> InsuranceCompanyJoin = recordRoot.join("insuranceCompany");
                    predicateList.add(cb.equal(objIdPath, recordRoot.get("id")));
                    predicateList.add(cb.equal(InsuranceCompanyJoin.get("id"), publicQuery.getInsuranceCompany()));
                }

                // 下单日期
                if(StringUtils.isNotBlank(publicQuery.getOrderStartDate()) && StringUtils.isNotBlank(publicQuery.getOrderEndDate())) {
                    Path<Date> createTimePath = root.get("purchaseOrder").get("createTime");//下单时间
                    Date startDate = DateUtils.getDate(publicQuery.getOrderStartDate(), DateUtils.DATE_LONGTIME24_PATTERN);
                    Date endDate = DateUtils.getDate(publicQuery.getOrderEndDate(), DateUtils.DATE_LONGTIME24_PATTERN);
                    Expression<Date> startDateExpression = cb.literal(startDate);
                    Expression<Date> endDateExpression = cb.literal(endDate);
                    predicateList.add(cb.between(createTimePath, startDateExpression, endDateExpression));
                }
                predicateList.add(cb.notEqual(root.get("purchaseOrder").get("type").get("id"), OrderType.Enum.HEALTH.getId()));
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }
}
