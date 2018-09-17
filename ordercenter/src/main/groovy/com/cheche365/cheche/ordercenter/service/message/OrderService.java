package com.cheche365.cheche.ordercenter.service.message;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import com.cheche365.cheche.ordercenter.web.model.PurchaseOrderSummary;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

/**
 * Created by sunhuazhong on 2015/3/2.
 */
@Service
@Transactional
public class OrderService implements IOrderService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private IInternalUserManageService internalUserManageService;

    /**
     * Configure the entity manager to be used.
     * @param em the {@link javax.persistence.EntityManager} to set.
     */
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public Map<String, Object> listNewOrder(String keyword, int pageNum, int pageSize) {
        Map<String, Object> objectMap = new HashMap<>();
        List<PurchaseOrder> orders = listCreatedPurchaseOrder(keyword, pageNum, pageSize);
        if(orders != null && orders.size() > 0) {
            List<PurchaseOrderSummary> purchaseOrderSummaryList = new ArrayList<>();
            for(PurchaseOrder order : orders) {
                QuoteRecord quoteRecord = quoteRecordRepository.findOne(order.getObjId());
                PurchaseOrderSummary purchaseOrderSummary = new PurchaseOrderSummary();
                purchaseOrderSummary.setId(order.getId());
                purchaseOrderSummary.setOwner(order.getAuto().getOwner());
                purchaseOrderSummary.setOrderNo(order.getOrderNo());
                purchaseOrderSummary.setOrderDate(DateUtils.getDateString(
                    order.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
                purchaseOrderSummary.setPremium(order.getPayableAmount());
                purchaseOrderSummary.setModel(order.getAuto().getAutoType() == null ? "" : order.getAuto().getAutoType().getModel());
                purchaseOrderSummary.setInsuranceCompany(quoteRecord.getInsuranceCompany().getName());
                purchaseOrderSummaryList.add(purchaseOrderSummary);
            }
            objectMap.put("order", purchaseOrderSummaryList);

            // 客服人员
            InternalUser internalUser = getCurrentInternalUser();
            long totalElements;
            if(StringUtils.isNotEmpty(keyword)) {
                totalElements = purchaseOrderService.getNewOrderTotalCountByUser(keyword, keyword, internalUser.getId());
            }else{
                totalElements = purchaseOrderService.getNewOrderTotalCount(internalUser.getId());
            }
            objectMap.put("totalElements", totalElements);
            objectMap.put("totalPage", (totalElements + pageSize - 1) / pageSize);

            return objectMap;
        }

        objectMap.put("order", new ArrayList<PurchaseOrderSummary>());
        objectMap.put("totalElements", 0);
        objectMap.put("totalPage", 0);

        return objectMap;
    }

    @Override
    public PurchaseOrderSummary getNewOrderBasicInfo(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(id);
        PurchaseOrderSummary purchaseOrderSummary = new PurchaseOrderSummary();
        purchaseOrderSummary.setOrderType(purchaseOrder.getType().getName());
        purchaseOrderSummary.setPremium(purchaseOrder.getPayableAmount());
        purchaseOrderSummary.setOwner(purchaseOrder.getAuto().getOwner());
        purchaseOrderSummary.setMobile(purchaseOrder.getDeliveryAddress().getMobile());
        purchaseOrderSummary.setOrderNo(purchaseOrder.getOrderNo());
        purchaseOrderSummary.setLicenseNo(purchaseOrder.getAuto().getLicensePlateNo());
        purchaseOrderSummary.setEngineNo(purchaseOrder.getAuto().getEngineNo());
        purchaseOrderSummary.setVinNo(purchaseOrder.getAuto().getVinNo());
        return purchaseOrderSummary;
    }

    private List<PurchaseOrder> listCreatedPurchaseOrder(String keyword, int pageNum, int pageSize) {
        // 客服人员
        InternalUser internalUser = getCurrentInternalUser();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        //针对PurchaseOrder实体的条件查询
        CriteriaQuery<PurchaseOrder> criteriaQuery = criteriaBuilder.createQuery(PurchaseOrder.class);
        //获取实体PurchaseOrder的属性集
        Root<PurchaseOrder> productRoot = criteriaQuery.from(PurchaseOrder.class); //HQL
        //获取实体属性
        Path<Long> idPath = productRoot.get("id");
        Path<InternalUser> operatorPath = productRoot.get("operator");
        Path<String> namePath = productRoot.get("auto").get("owner");
        Path<String> mobilePath = productRoot.get("deliveryAddress").get("mobile");
        Path<OrderType> orderTypePath = productRoot.get("type").get("id");
        Path<OrderStatus> orderStatusPath = productRoot.get("status").get("id");
        Path<Date> updateTimePath = productRoot.get("updateTime");
        //条件构造
        List<Predicate> predicateList = new ArrayList<>();
        if(keyword != null && !keyword.isEmpty()) {
            predicateList.add(criteriaBuilder.or(
                criteriaBuilder.like(namePath, keyword + "%"),
                criteriaBuilder.like(mobilePath, keyword + "%")));
        }

        predicateList.add(criteriaBuilder.or(
            criteriaBuilder.isNull(operatorPath),
            criteriaBuilder.equal(operatorPath, internalUser.getId())));//操作人为空
        predicateList.add(criteriaBuilder.or(
            criteriaBuilder.equal(orderStatusPath, OrderStatus.Enum.PENDING_PAYMENT_1.getId()),
            criteriaBuilder.equal(orderStatusPath, OrderStatus.Enum.PAID_3.getId())));//订单状态
        predicateList.add(criteriaBuilder.equal(orderTypePath, OrderType.Enum.INSURANCE.getId()));//车险
        predicateList.add(criteriaBuilder.isNull(updateTimePath));//修改时间

        Predicate[] predicates = new Predicate[predicateList.size()];
        predicates = predicateList.toArray(predicates);
        criteriaQuery.where(predicates);

        criteriaQuery.orderBy(new Order() {
            @Override
            public Order reverse() {
                return null;
            }

            @Override
            public boolean isAscending() {
                return false;
            }

            @Override
            public Expression<?> getExpression() {
                return idPath;
            }
        });

        TypedQuery<PurchaseOrder> queryResult = em.createQuery(criteriaQuery);
        queryResult.setMaxResults(pageSize);
        queryResult.setFirstResult((pageNum -1) * pageSize);
        List<PurchaseOrder> list = queryResult.getResultList();
        return list;
    }

    /**
     * 获取当前登录用户
     * @return
     */
    public InternalUser getCurrentInternalUser() {
        // 客服人员
        return internalUserManageService.getCurrentInternalUser();
    }
}
