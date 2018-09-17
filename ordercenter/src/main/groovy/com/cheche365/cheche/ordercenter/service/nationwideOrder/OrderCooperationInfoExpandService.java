package com.cheche365.cheche.ordercenter.service.nationwideOrder;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.OrderCooperationInfoRepository;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.service.OrderCooperationInfoService;
import com.cheche365.cheche.ordercenter.model.NationwideOrderQuery;
import com.cheche365.cheche.manage.common.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangfei on 2015/11/13.
 */
@Service
@Transactional
public class OrderCooperationInfoExpandService extends OrderCooperationInfoService {
    private Logger logger = LoggerFactory.getLogger(OrderCooperationInfoExpandService.class);

    @Autowired
    private BaseService baseService;

    @Autowired
    private OrderCooperationInfoRepository orderCooperationInfoRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public Page<OrderCooperationInfo> getOrdersByPage(Integer currentPage, Integer pageSize, NationwideOrderQuery query) {
        return findBySpecAndPaginate(baseService.buildPageable(currentPage, pageSize, Sort.Direction.DESC, getSortPropertyByStatus(query)),
            query);
    }

    private String getSortPropertyByStatus(NationwideOrderQuery query) {
        Long statusId = query.getStatusId();
        if (null != statusId) {
            if (statusId.equals(OrderCooperationStatus.Enum.QUOTE_NO_AUDIT.getId())
                || statusId.equals(OrderCooperationStatus.Enum.AUDIT_NO_PAYMENT.getId())
                || statusId.equals(OrderCooperationStatus.Enum.PAYMENT_NO_INSURANCE.getId())) {
                return "updateTime";
            }
        }
        return "id";
    }

    public PaymentStatus getPaymentStatus(PurchaseOrder purchaseOrder) {
        Payment payment = paymentRepository.findFirstByChannelAndPurchaseOrderOrderByIdDesc(purchaseOrder.getChannel(), purchaseOrder);
        return null != payment ? payment.getStatus() : null;
    }

    public String getPaymentComment(PaymentStatus paymentStatus, PurchaseOrder purchaseOrder) {
        if (null == paymentStatus) return "未支付";

        if (PaymentStatus.Enum.PAYMENTSUCCESS_2.getId().equals(paymentStatus.getId())) {
            return "已支付";
        } else {
            if (OrderStatus.Enum.CANCELED_6.getId().equals(purchaseOrder.getStatus().getId())
                || OrderStatus.Enum.EXPIRED_8.getId().equals(purchaseOrder.getStatus().getId())) {
                return "放弃支付";
            } else {
                return "未支付";
            }
        }
    }

    private Page<OrderCooperationInfo> findBySpecAndPaginate(Pageable pageable, NationwideOrderQuery orderQuery) {
        return orderCooperationInfoRepository.findAll(new Specification<OrderCooperationInfo>() {
            @Override
            public Predicate toPredicate(Root<OrderCooperationInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<OrderCooperationInfo> criteriaQuery = cb.createQuery(OrderCooperationInfo.class);
                List<Predicate> predicateList = new ArrayList<>();
                if (StringUtils.isNotBlank(orderQuery.getOrderNo())) {
                    Path<String> orderNoPath = root.get("purchaseOrder").get("orderNo");
                    predicateList.add(cb.like(orderNoPath, orderQuery.getOrderNo() + "%"));
                }
                if (StringUtils.isNotBlank(orderQuery.getOwner())) {
                    Path<String> ownerPath = root.get("purchaseOrder").get("auto").get("owner");
                    predicateList.add(cb.like(ownerPath, orderQuery.getOwner() + "%"));
                }
                if (StringUtils.isNotBlank(orderQuery.getLicensePlateNo())) {
                    Path<String> licensePlateNoPath = root.get("purchaseOrder").get("auto").get("licensePlateNo");
                    predicateList.add(cb.like(licensePlateNoPath, orderQuery.getLicensePlateNo() + "%"));
                }
                if (StringUtils.isNotBlank(orderQuery.getInstitutionName())) {
                    Path<String> institutionNamePath = root.get("institution").get("name");
                    predicateList.add(cb.like(institutionNamePath, orderQuery.getInstitutionName() + "%"));
                }
                if (null != orderQuery.getAreaId()) {
                    Path<Long> areaIdPath = root.get("area").get("id");
                    predicateList.add(cb.equal(areaIdPath, orderQuery.getAreaId()));
                }
                if (null != orderQuery.getStatusId()) {
                    Path<Long> statusIdPath = root.get("status").get("id");
                    predicateList.add(cb.equal(statusIdPath, orderQuery.getStatusId()));
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }
}
