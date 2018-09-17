package com.cheche365.cheche.ordercenter.service.order;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.OrderOperationInfo;
import com.cheche365.cheche.core.model.OrderType;
import com.cheche365.cheche.core.model.Permission;
import com.cheche365.cheche.core.repository.OrderOperationInfoRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.util.DataFilter.DataFilter;
import com.cheche365.cheche.manage.common.util.DataFilter.PermissionFilter;
import com.cheche365.cheche.manage.common.util.DataFilter.QueryParamFilter;
import com.cheche365.cheche.ordercenter.annotation.DataPermission;
import com.cheche365.cheche.ordercenter.aop.CustomSpecification;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.List;

/**
 * Created by wangfei on 2015/12/16.
 */
@Service
@Transactional
public class OrderOperationInfoExpandService extends BaseService {
    private Logger logger = LoggerFactory.getLogger(OrderOperationInfoExpandService.class);

    @Autowired
    private BaseService baseService;

    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;

    @Autowired
    private IInternalUserManageService internalUserManageService;

    @Autowired
    private QueryParamFilter queryParamFilter;

    @Autowired
    private PermissionFilter permissionFilter;

    @DataPermission(code = "OC1", handler = "publicQueryConditionHandler")
    public Page<OrderOperationInfo> getOrdersByPage(PublicQuery query) {
        return findBySpecAndPaginate(baseService.buildPageable(query.getCurrentPage(), query.getPageSize(),
                Sort.Direction.DESC, query.getSort()), query);
    }


    private Page<OrderOperationInfo> findBySpecAndPaginate(Pageable pageable, PublicQuery publicQuery) {
        return orderOperationInfoRepository.findAll(
                new CustomSpecification<OrderOperationInfo>() {
                    @Override
                    //  @DataPermission(entity = {"purchaseOrder", "purchaseOrder"}, fields = {"area.id", "sourceChannel.id"}, handler = "specificationConditionHandler")
                    public Predicate toPredicate(CustomSpecification.SpecificationParam specificationParam) {
                        Root<OrderOperationInfo> root = specificationParam.getRoot();
                        List<Predicate> predicateList = specificationParam.getPredicateList();
                        CriteriaBuilder cb = specificationParam.getCriteriaBuilder();

                        CriteriaQuery<OrderOperationInfo> criteriaQuery = cb.createQuery(OrderOperationInfo.class);
                        // List<Predicate> predicateList = new ArrayList<>();
                        boolean hasAdminPermission = internalUserManageService.hasPermission(Permission.Enum.INTERNAL_USER_PERMISSION_ALL_ORDER);
                        if (!hasAdminPermission) {
                            //非管理员查询当前用户的订单
                            InternalUser internalUser = internalUserManageService.getCurrentInternalUser();
                            Path<Long> orderOwnerPath = root.get("owner").get("id");
                            predicateList.add(cb.equal(orderOwnerPath, internalUser.getId()));
                        }
                        //车主b
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
                            predicateList.add(cb.equal(orderStatusPath, publicQuery.getOrderStatus()
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
                        DataFilter.process(root, predicateList, cb, publicQuery, permissionFilter, queryParamFilter);
                        predicateList.add(cb.notEqual(root.get("purchaseOrder").get("type").get("id"), OrderType.Enum.HEALTH.getId()));
                        Long[] orderSourceTypeIds = {1L, 2L, 3L, 4L, 5L,8L,9L};//小财神：暂时排除泛华线下同步的订单
                        CriteriaBuilder.In<Object> orderSourceTypeIn = cb.in(root.get("purchaseOrder").get("orderSourceType").get("id"));
                        for (Long orderSourceTypeId : orderSourceTypeIds) {
                            orderSourceTypeIn.value(orderSourceTypeId);
                        }
                        predicateList.add(cb.or(
                                cb.isNull(root.get("purchaseOrder").get("orderSourceType")),
                                orderSourceTypeIn
                        ));
                        Predicate[] predicates = new Predicate[predicateList.size()];
                        predicates = predicateList.toArray(predicates);
                        return criteriaQuery.where(predicates).getRestriction();
                    }
                }, pageable);
    }

}
