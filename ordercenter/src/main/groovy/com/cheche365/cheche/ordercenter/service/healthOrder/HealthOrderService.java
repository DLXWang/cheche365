package com.cheche365.cheche.ordercenter.service.healthOrder;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.model.abao.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.ordercenter.web.model.healthOrder.InsurancePolicyViewModel;
import com.cheche365.cheche.ordercenter.web.model.order.PaymentInfoViewModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by cxy on 2016/12/29.
 */
@Service
@Transactional
public class HealthOrderService extends BaseService {
    private Logger logger = LoggerFactory.getLogger(HealthOrderService.class);

    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;
    @Autowired
    private InsurancePolicyRepository insurancePolicyRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private InsuranceQuoteFieldRepository insuranceQuoteFieldRepository;
    @Autowired
    private InsuranceProductDetailRepository insuranceProductDetailRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private InsurancePersonRepository insurancePersonRepository;
    @Autowired
    private InsurancePolicyExportRepository insurancePolicyExportRepository;
    @Autowired
    private AgentTmpRepository agentTmpRepository;

    @Autowired
    private OrderAgentRepository orderAgentRepository;


    public InsurancePolicy getById(Long insurancePolicyId) {
        return insurancePolicyRepository.findOne(insurancePolicyId);
    }

    public InsurancePolicyViewModel createViewModel(InsurancePolicy insurancePolicy) {
        if (insurancePolicy != null) {
            InsurancePolicyViewModel returnModel = InsurancePolicyViewModel.createAllViewMdel(insurancePolicy);
            InsuranceQuoteField quoteField = insuranceQuoteFieldRepository.findFirstByInsuranceQuote(insurancePolicy.getInsuranceQuote());
            if (quoteField != null) {
                returnModel.setAmount(quoteField.getAmount());
            }
            returnModel.setInsuranceProduct(insurancePolicy.getInsuranceQuote().getInsuranceProduct());

            InsuranceProductDetail detail = insuranceProductDetailRepository.findDetailByProductAndDetailName(
                insurancePolicy.getInsuranceQuote().getInsuranceProduct().getId(),
                InsuranceProductDetailName.Enum.VALID_DAYS.getId());
            if (detail != null) {
                returnModel.setWaitingDays(detail.getValue());
            }

            try{
                List<Object[]> list=agentTmpRepository.findFanHuaNonAutoAgentByOrder(insurancePolicy.getPolicyNo());
                if(list.size()>0){
                    Object[] objects=list.get(0);
                    if(objects[0] != null){
                        returnModel.setAgentName(String.valueOf(objects[0]));
                        Double commission = objects[1] == null ? 0.0 : Double.valueOf(objects[1].toString());
                        returnModel.setCardNum(String.valueOf(objects[2]));
                        returnModel.setRebate(DoubleUtils.div(commission, returnModel.getPremium(), 3)*100);
                        return returnModel;
                    }
                }
            }catch(Exception e){
                logger.info("not in production environment");
            }
            if(StringUtils.isEmpty(returnModel.getAgentName()) && insurancePolicy.getUser() != null
                && insurancePolicy.getUser().getUserType() != null
                && insurancePolicy.getUser().getUserType().getId() == UserType.Enum.Agent.getId()) {
                OrderAgent orderAgent=orderAgentRepository.findByPurchaseOrder(insurancePolicy.getPurchaseOrder());
                if(orderAgent !=null||orderAgent.getAgent() !=null){
                    returnModel.setAgentName(orderAgent.getAgent().getName());
                }
            }
            return returnModel;
        }
        return new InsurancePolicyViewModel();
    }

    public List<InsurancePolicyViewModel> createViewModelList(List<OrderOperationInfo> operationInfos) {
        List<InsurancePolicyViewModel> modelList = new ArrayList<>();
        for (OrderOperationInfo operationInfo : operationInfos) {
            InsurancePolicy insurancePolicy = insurancePolicyRepository.findFirstByPurchaseOrder(operationInfo.getPurchaseOrder());
            if (insurancePolicy != null) {
                InsurancePolicyViewModel insurancePolicyViewModel = InsurancePolicyViewModel.createBaseViewModel(insurancePolicy);
                modelList.add(insurancePolicyViewModel);
            }
        }
        return modelList;
    }

    public Page<OrderOperationInfo> getOrdersByPage(PublicQuery query) {
        return findBySpecAndPaginate(buildPageable(query.getCurrentPage(), query.getPageSize(),
            Sort.Direction.DESC, "id"), query);
    }


    private Page<OrderOperationInfo> findBySpecAndPaginate(Pageable pageable, PublicQuery publicQuery) {
        return orderOperationInfoRepository.findAll(new Specification<OrderOperationInfo>() {
            @Override
            public Predicate toPredicate(Root<OrderOperationInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<OrderOperationInfo> criteriaQuery = cb.createQuery(OrderOperationInfo.class);
                List<Predicate> predicateList = new ArrayList<>();

                Path<String> orderType = root.get("purchaseOrder").get("type").get("id");
                predicateList.add(cb.equal(orderType, OrderType.Enum.HEALTH.getId()));

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
                    Path<Long> orderStatusPath = root.get("purchaseOrder").get("status").get("id");
                    predicateList.add(cb.equal(orderStatusPath, publicQuery.getOrderStatus()
                    ));
                }
                //渠道
                if (null != publicQuery.getChannel()) {
                    Path<Long> channelPath = root.get("purchaseOrder").get("sourceChannel").get("id");
                    predicateList.add(cb.equal(channelPath, publicQuery.getChannel()));
                }
                if (StringUtils.isNotBlank(publicQuery.getOrderStartDate())) {
                    Date startDate = DateUtils.getDate(publicQuery.getOrderStartDate(), DateUtils.DATE_SHORTDATE_PATTERN);
                    predicateList.add(cb.greaterThan(root.get("purchaseOrder").get("createTime"), startDate));
                }
                if (StringUtils.isNotBlank(publicQuery.getOrderEndDate())) {
                    Date endDate = DateUtils.getDate(publicQuery.getOrderEndDate(), DateUtils.DATE_SHORTDATE_PATTERN);
                    predicateList.add(cb.lessThan(root.get("purchaseOrder").get("createTime"), endDate));
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    public Page<InsurancePolicy> findInsurancePolicyBySpecAndPaginate(Pageable pageable, PublicQuery publicQuery, String effectiveDate, String expireDate) {
        return insurancePolicyRepository.findAll(new Specification<InsurancePolicy>() {
            @Override
            public Predicate toPredicate(Root<InsurancePolicy> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<InsurancePolicy> criteriaQuery = cb.createQuery(InsurancePolicy.class);
                List<Predicate> predicateList = new ArrayList<>();
                Root purchaseOrderRoot = query.from(PurchaseOrder.class);
                predicateList.add(cb.equal(root.get("purchaseOrder").get("id"), purchaseOrderRoot.get("id")));
                predicateList.add(cb.equal(purchaseOrderRoot.get("type").get("id"), OrderType.Enum.HEALTH.getId()));
                if (StringUtils.isNotBlank(publicQuery.getOrderNo())) {
                    predicateList.add(cb.like(purchaseOrderRoot.get("orderNo"), publicQuery.getOrderNo() + "%"));
                }
                if (null != publicQuery.getOrderStatus()) {
                    predicateList.add(cb.equal(purchaseOrderRoot.get("status").get("id"), publicQuery.getOrderStatus()));
                }
                if (null != publicQuery.getChannel()) {
                    predicateList.add(cb.equal(purchaseOrderRoot.get("sourceChannel").get("id"), publicQuery.getChannel()));
                }

                //生效时间和失效时间的范围查询
                if (!StringUtils.isEmpty(effectiveDate) && !StringUtils.isEmpty(expireDate)) {
                    predicateList.add(cb.between(root.get("effectiveDate"), DateUtils.getDate(effectiveDate, DateUtils.DATE_SHORTDATE_PATTERN), DateUtils.getDate(expireDate, DateUtils.DATE_SHORTDATE_PATTERN)));
                }
                if (StringUtils.isEmpty(effectiveDate) && !StringUtils.isEmpty(expireDate)) {
                    predicateList.add(cb.lessThanOrEqualTo(root.get("expireDate"), DateUtils.getDate(expireDate, DateUtils.DATE_SHORTDATE_PATTERN)));
                }
                if (!StringUtils.isEmpty(effectiveDate)) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.get("effectiveDate"), DateUtils.getDate(effectiveDate, DateUtils.DATE_SHORTDATE_PATTERN)));
                }

                //手机号
                if (!StringUtils.isEmpty(publicQuery.getMobile())) {
                    predicateList.add(cb.like(root.get("applicantPerson").get("mobile"), publicQuery.getMobile()));
                }

                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);

    }


    //支付信息
    public List<PaymentInfoViewModel> getPaymentInfoDetail(Long orderId) {
        List<Long> excludeType = Arrays.asList(PaymentType.Enum.DISCOUNT_5.getId(), PaymentType.Enum.CHECHEPAY_6.getId());//, PaymentType.Enum.DAILY_RESTART_PAY_7.getId());
        List<Payment> paymentList = paymentRepository.findPaymentInfoByOrderId(orderId, excludeType);
        if (CollectionUtils.isNotEmpty(paymentList)) {
            return PaymentInfoViewModel.changeObjArrToPaymentInfoViewModel(paymentList);
        }
        return new ArrayList<PaymentInfoViewModel>();
    }


    public InsurancePolicy findByPurchaseOrder(Long orderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(orderId);
        InsurancePolicy insurancePolicy = insurancePolicyRepository.findFirstByPurchaseOrder(purchaseOrder);
        return insurancePolicy;
    }

    public List<InsurancePolicy> findByExportNotExists() {
        List<InsurancePolicy> notExists = insurancePolicyRepository.findByExportNotExists();
        if (CollectionUtils.isNotEmpty(notExists)) {
            List<InsurancePolicyExport> insurancePolicyExportList = new ArrayList<>();
            InsurancePolicyExport insurancePolicyExport;
            for (InsurancePolicy insurancePolicy : notExists) {
                insurancePolicyExport = new InsurancePolicyExport();
                insurancePolicyExport.setInsurancePolicy(insurancePolicy);

                insurancePolicyExportList.add(insurancePolicyExport);
            }
            insurancePolicyExportRepository.save(insurancePolicyExportList);
        }
        return notExists;
    }

    /**
     * @param dataList
     */
    @Transactional
    public void importDatas(List<List<String>> dataList) {
        List<InsurancePolicy> insurancePolicyList = new ArrayList<>();
        List<InsurancePerson> applicatPersionList = new ArrayList<>();
        List<InsurancePerson> insuredPersonList = new ArrayList<>();


        for (List<String> rowList : dataList) {
            String orderNo = rowList.get(20);
            InsurancePolicy insurancePolicy = insurancePolicyRepository.findFirstByOrderNo(orderNo);
            if (insurancePolicy == null) {
                continue;
            }

            //投保人信息
            InsurancePerson applicantPerson = insurancePolicy.getApplicantPerson();
            applicantPerson.setMobile(rowList.get(5));
            applicantPerson.setEmail(rowList.get(6));
            applicatPersionList.add(applicantPerson);

            //被保险人信息
            InsurancePerson insuredPerson = insurancePolicy.getInsuredPerson();
            insuredPerson.setMobile(rowList.get(13));
            insuredPerson.setEmail(rowList.get(14));
            insuredPersonList.add(insuredPerson);


            //保单信息
            insurancePolicy.setEffectiveDate(DateUtils.getDate(rowList.get(16), DateUtils.DATE_SHORTDATE2_PATTERN));
            insurancePolicy.setPolicyNo(rowList.get(21));
            insurancePolicyList.add(insurancePolicy);
        }

        if (insurancePolicyList.size() > 0)
            insurancePolicyRepository.save(insurancePolicyList);
        if (applicatPersionList.size() > 0)
            insurancePersonRepository.save(applicatPersionList);
        if (insuredPersonList.size() > 0)
            insurancePersonRepository.save(insuredPersonList);
    }
}
