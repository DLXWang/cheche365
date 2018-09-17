package com.cheche365.cheche.ordercenter.service.order;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.constants.AnswernConstant;
import com.cheche365.cheche.core.model.Address;
import com.cheche365.cheche.core.model.Agent;
import com.cheche365.cheche.core.model.AgentRebateHistory;
import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.AutoType;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.CompulsoryInsurance;
import com.cheche365.cheche.core.model.DeliveryInfo;
import com.cheche365.cheche.core.model.Insurance;
import com.cheche365.cheche.core.model.InsuranceCompany;
import com.cheche365.cheche.core.model.InsurancePurchaseOrderRebate;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.OrderOperationInfo;
import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.model.OrderTransmissionStatus;
import com.cheche365.cheche.core.model.OrderType;
import com.cheche365.cheche.core.model.Payment;
import com.cheche365.cheche.core.model.PaymentChannel;
import com.cheche365.cheche.core.model.PaymentStatus;
import com.cheche365.cheche.core.model.PaymentType;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.model.QuoteSource;
import com.cheche365.cheche.core.model.RebateChannel;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.repository.AddressRepository;
import com.cheche365.cheche.core.repository.AgentRepository;
import com.cheche365.cheche.core.repository.AreaRepository;
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository;
import com.cheche365.cheche.core.repository.DeliveryInfoRepository;
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository;
import com.cheche365.cheche.core.repository.InsurancePurchaseOrderRebateRepository;
import com.cheche365.cheche.core.repository.InsuranceRepository;
import com.cheche365.cheche.core.repository.OrderOperationInfoRepository;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import com.cheche365.cheche.core.service.AgentRebateHistoryService;
import com.cheche365.cheche.core.service.AutoService;
import com.cheche365.cheche.core.service.IInternalUserService;
import com.cheche365.cheche.core.service.OrderAgentService;
import com.cheche365.cheche.core.service.OrderOperationInfoService;
import com.cheche365.cheche.core.service.PurchaseOrderGiftService;
import com.cheche365.cheche.core.service.PurchaseOrderIdService;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.core.util.AutoUtils;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.manage.common.service.InsurancePurchaseOrderRebateManageService;
import com.cheche365.cheche.manage.common.service.OrderInsurancePackageService;
import com.cheche365.cheche.manage.common.service.reverse.OrderReverse;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.InsurancePurchaseOrderRebateViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.ordercenter.service.DataHistoryLogService;
import com.cheche365.cheche.ordercenter.service.user.IInternalUserManageService;
import com.cheche365.cheche.ordercenter.web.model.order.OrderInsuranceViewModel;
import org.apache.commons.collections.CollectionUtils;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by wangfei on 2015/8/18.
 * Line:251,409:quoteRecord以及insurance的iopTotal不从分项计算，从页面中取（录入时不填写分项数据）
 */
@Service
@Transactional
public class OrderReverseGeneratorService extends DataHistoryLogService<OrderOperationInfo, OrderInsuranceViewModel> {

    private Logger logger = LoggerFactory.getLogger(OrderReverseGeneratorService.class);

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private InsuranceCompanyRepository insuranceCompanyRepository;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private OrderOperationInfoRepository orderOperationInfoRepository;

    @Autowired
    private IInternalUserService internalUserService;

    @Autowired
    private PurchaseOrderIdService purchaseOrderIdService;

    @Autowired
    private AutoService autoService;

    @Autowired
    private OrderOperationInfoService orderOperationInfoService;

    @Autowired
    private IInternalUserManageService internalUserManageService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private DeliveryInfoRepository deliveryInfoRepository;

    @Autowired
    private OrderAgentService orderAgentService;

    @Autowired
    private InsurancePurchaseOrderRebateRepository insurancePurchaseOrderRebateRepository;

    @Autowired
    private InsurancePurchaseOrderRebateManageService insurancePurchaseOrderRebateManageService;

    @Autowired
    private AgentRebateHistoryService agentRebateHistoryService;

    @Autowired
    private OrderInsurancePackageService orderInsurancePackageService;

    @Autowired
    private PurchaseOrderGiftService purchaseOrderGiftService;

    public boolean validAutoArea(OrderReverse model) {
        Area area = areaRepository.findOne(model.getArea());
        if (null == area)
            throw new RuntimeException("can not find area by area -> " + model.getArea());

        return area.equals(AutoUtils.getAreaOfAuto(model.getLicensePlateNo()));
    }

    public String validRepetitivePolicyNo(String commercialPolicyNo, String compulsoryPolicyNo) {
        if (StringUtils.isNotBlank(commercialPolicyNo)) {
            if (insuranceRepository.countByPolicyNo(StringUtils.trimToEmpty(commercialPolicyNo)) > 0) {
                logger.error("policyNo {} already exists in insurance.", commercialPolicyNo);
                return StringUtils.trimToEmpty(commercialPolicyNo);
            }
        }

        if (StringUtils.isNotBlank(compulsoryPolicyNo)) {
            if (compulsoryInsuranceRepository.countByPolicyNo(StringUtils.trimToEmpty(compulsoryPolicyNo)) > 0) {
                logger.error("policyNo {} already exists in compulsoryInsurance.", compulsoryPolicyNo);
                return StringUtils.trimToEmpty(compulsoryPolicyNo);
            }
        }

        return "";
    }

    public boolean validAgent(Long agentId) {
        User user = agentRepository.findOne(agentId).getUser();
        if (null == user) {
            logger.error("agent:{} is not bounded mobile to create user", agentId);
            return false;
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveOrderAndInsurance(OrderReverse model) {
        User user = agentRepository.findOne(model.getRecommender()).getUser();
        Area area = areaRepository.findOne(model.getArea());
        if (null == area)
            throw new RuntimeException("can not find area by area -> " + model.getArea());

        Auto auto = autoService.saveOrMerge(this.createAuto(model, area), user, new StringBuilder());
        QuoteRecord quoteRecord = this.createQuoteRecord(null, model, user, auto, area);
        PurchaseOrder purchaseOrder = this.createPurchaseOrder(null, model, quoteRecord);
        logger.info("generate order finish, orderNo -> {}", purchaseOrder.getOrderNo());

        this.createPayment(purchaseOrder);
        this.createInsurance(model, purchaseOrder, quoteRecord);
        this.createOrderOperationInfo(purchaseOrder);
        //保存订单佣金费率信息
        this.createInsurancePurchaseOrderRebate(purchaseOrder, model);

    }

    protected void createInsurance(OrderReverse model, PurchaseOrder purchaseOrder, QuoteRecord quoteRecord) {
        Insurance insurance = insuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);
        CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);

        if (model.getCommercialPremium() > 0) {
            Insurance newInsurance;
            if (null != insurance) {
                newInsurance = insurance;
                logger.info("commercialPremium is more than 0, update insurance.");
            } else {
                newInsurance = new Insurance();
                logger.info("commercialPremium is more than 0, save insurance.");
            }

            this.saveInsurance(model, newInsurance, purchaseOrder, quoteRecord);
        } else {
            if (null != insurance)
                insuranceRepository.delete(insurance);
        }

        if (model.getCompulsoryPremium() > 0) {
            CompulsoryInsurance newCompulsoryInsurance;
            if (null != compulsoryInsurance) {
                logger.info("compulsoryPremium is more than 0, update compulsoryInsurance.");
                newCompulsoryInsurance = compulsoryInsurance;
            } else {
                logger.info("compulsoryPremium is more than 0, save compulsoryInsurance.");
                newCompulsoryInsurance = new CompulsoryInsurance();
            }

            this.saveCompulsoryInsurance(model, newCompulsoryInsurance, purchaseOrder, quoteRecord);
        } else {
            if (null != compulsoryInsurance)
                compulsoryInsuranceRepository.delete(compulsoryInsurance);
        }
    }

    private void saveCompulsoryInsurance(OrderReverse model, CompulsoryInsurance insurance, PurchaseOrder purchaseOrder, QuoteRecord quoteRecord) {
        String[] contains = new String[]{"compulsoryPremium", "autoTax", "insuredIdNo", "insuredName", "insuranceImage", "applicantName", "applicantIdNo"};
        //super.createLog(insurance,contains);
        BeanUtil.copyPropertiesContain(model, insurance, contains);
        insurance.setPolicyNo(model.getCompulsoryPolicyNo());
        insurance.setEffectiveDate(DateUtils.getDate(model.getCompulsoryEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        insurance.setEffectiveHour(model.getCompulsoryEffectiveHour());
        insurance.setExpireDate(DateUtils.getDate(model.getCompulsoryExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        insurance.setExpireHour(model.getCompulsoryExpireHour());
        insurance.setInsuranceImage(getInsuranceImageUrl(model.getCompulsoryInsuranceImage()));
        insurance.setStamp(getInsuranceImageUrl(model.getCompulsoryStampFile()));
        InsuranceCompany insuranceCompany = quoteRecord.getInsuranceCompany();
        insurance.setInsuranceCompany(insuranceCompany);
        insurance.setDiscount(model.getDiscountCI());
        insurance.setInsuredIdentityType(model.getInsuredIdType());
        insurance.setApplicantIdentityType(model.getApplicantIdType());
        if (insurance.getId() != null) {
            insurance.setUpdateTime(new Date());
        } else {
            insurance.setCreateTime(new Date());
        }
        CompulsoryInsurance.setCompulsoryInsuranceReferences(purchaseOrder, quoteRecord, purchaseOrder.getOperator(), insurance);

        compulsoryInsuranceRepository.save(insurance);
    }

    private void saveInsurance(OrderReverse model, Insurance insurance, PurchaseOrder purchaseOrder, QuoteRecord quoteRecord) {
        String[] contains = new String[]{"thirdPartyPremium",
                "thirdPartyAmount", "damagePremium", "damageAmount", "theftPremium", "theftAmount", "enginePremium",
                "engineAmount", "driverPremium", "driverAmount", "passengerPremium", "passengerAmount", "passengerCount", "spontaneousLossPremium",
                "spontaneousLossAmount", "glassPremium", "glassAmount", "scratchAmount", "scratchPremium", "unableFindThirdPartyPremium", "designatedRepairShopPremium", "damageIop", "thirdPartyIop",
                "theftIop", "engineIop", "driverIop", "passengerIop", "spontaneousLossIop", "scratchIop", "insuredIdNo", "insuredName", "insuranceImage", "discount", "applicantName", "applicantIdNo"};
        //super.createLog(insurance,contains);
        BeanUtil.copyPropertiesContain(model, insurance, contains);
        insurance.setPremium(model.getCommercialPremium());
        insurance.setPolicyNo(model.getCommercialPolicyNo());
        insurance.setEffectiveDate(DateUtils.getDate(model.getCommercialEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        insurance.setEffectiveHour(model.getCommercialEffectiveHour());
        insurance.setExpireDate(DateUtils.getDate(model.getCommercialExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        insurance.setExpireHour(model.getCommercialExpireHour());
        InsuranceCompany insuranceCompany = quoteRecord.getInsuranceCompany();
        insurance.setInsuranceCompany(insuranceCompany);
        insurance.setInsuranceImage(getInsuranceImageUrl(model.getInsuranceImage()));
        insurance.setIopTotal(model.getIop());
        insurance.setInsuredIdentityType(model.getInsuredIdType());
        insurance.setApplicantIdentityType(model.getApplicantIdType());
        /**
         * 如果是安心的话，需要设置停驶返现比例，否则停复驶失败
         * **/
        if (insuranceCompany.equals(InsuranceCompany.Enum.ANSWERN_65000))
            if (DoubleUtils.isNotZero(insurance.getProportion())) {
                insurance.setProportion(AnswernConstant.PROPORTION);
            }
        if (insurance.getId() != null) {
            insurance.setUpdateTime(new Date());
        } else {
            insurance.setCreateTime(model.getApplicantDate());
        }
        Insurance.setInsuranceReferences(purchaseOrder, quoteRecord, purchaseOrder.getOperator(), insurance);

        insuranceRepository.save(insurance);
    }

    protected OrderOperationInfo createOrderOperationInfo(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null)
            throw new RuntimeException("save orderOperationInfo, purchaseOrder can not be null");

        OrderOperationInfo orderOperationInfo = null;
        OrderOperationInfo oldOperationInfo = orderOperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);
        InternalUser currentUser = internalUserManageService.getCurrentInternalUser();
        if (null != oldOperationInfo) {
            orderOperationInfo = new OrderOperationInfo();
            BeanUtil.copyPropertiesContain(oldOperationInfo, orderOperationInfo);
            orderOperationInfo.setUpdateTime(new Date());
            orderOperationInfo.setOperator(currentUser);
        } else {
            orderOperationInfo = orderOperationInfoService.createOperationInfo(purchaseOrder, orderOperationInfo);
            orderOperationInfo.setCurrentStatus(OrderTransmissionStatus.Enum.ORDER_INPUTED);
            orderOperationInfo.setInsuranceInputter(currentUser);
            orderOperationInfo.setOperator(currentUser);
            orderOperationInfo.setConfirmOrderDate(purchaseOrder.getCreateTime());
        }

        return orderOperationInfoRepository.save(orderOperationInfo);
    }

    protected Auto createAuto(OrderReverse model, Area area) {
        Auto auto = new Auto();
        AutoType autoType = new AutoType();
        autoType.setModel(model.getBrand());
        auto.setAutoType(autoType);
        String vinNo = model.getVinNo();
        auto.setEngineNo(model.getEngineNo());
        auto.setVinNo(model.getVinNo());
        auto.setIdentity(model.getIdentity());
        auto.setIdentityType(model.getIdentityType());
        String licensePlateNo = model.getLicensePlateNo();
        if (StringUtils.isEmpty(licensePlateNo) && model.getIsNewCar()) {
            licensePlateNo = new StringBuffer(area.getShortCode()).append(vinNo.substring(vinNo.length() - 5, vinNo.length())).append("#").toString();
        }
        auto.setLicensePlateNo(licensePlateNo);
        auto.setOwner(model.getOwner());
        auto.setEnrollDate(DateUtils.getDate(model.getEnrollDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        auto.setArea(area);

        return auto;
    }

    protected Payment createPayment(PurchaseOrder purchaseOrder) {
        Payment payment = paymentRepository.findFirstByPurchaseOrder(purchaseOrder);
        if (null != payment) {
            payment.setUpdateTime(new Date());
        } else {
            payment = new Payment();
            payment.setPurchaseOrder(purchaseOrder);
            payment.setChannel(PaymentChannel.Enum.ALIPAY_1);
            payment.setClientType(Channel.Enum.WAP_8);
            payment.setStatus(PaymentStatus.Enum.PAYMENTSUCCESS_2);
            payment.setComments("该订单是由出单中心录入保单功能反向生成的订单，状态置为已支付");
            payment.setPaymentType(PaymentType.Enum.INITIALPAYMENT_1);
            payment.setCreateTime(purchaseOrder.getCreateTime());
        }

        payment.setUser(purchaseOrder.getApplicant());
        payment.setAmount(purchaseOrder.getPaidAmount());
        payment.setOperator(purchaseOrder.getOperator());

        return paymentRepository.save(payment);
    }

    protected PurchaseOrder createPurchaseOrder(PurchaseOrder oldOrder, OrderReverse model, QuoteRecord quoteRecord) {
        PurchaseOrder purchaseOrder = new PurchaseOrder();

        if (null != oldOrder) {
            BeanUtil.copyPropertiesContain(oldOrder, purchaseOrder);
            purchaseOrder.setUpdateTime(new Date());
        } else {
            purchaseOrder.setType(OrderType.Enum.INSURANCE);
            purchaseOrder.setStatus(OrderStatus.Enum.FINISHED_5);
            purchaseOrder.setChannel(PaymentChannel.Enum.ALIPAY_1);
            purchaseOrder.setSourceChannel(Channel.Enum.WAP_8);
            purchaseOrder.setCreateTime(model.getApplicantDate());
            // purchaseOrder.setOrderNo(purchaseOrderIdService.getNext(orderTypeEnum.INSURANCE));
            purchaseOrder.setOrderNo(purchaseOrderIdService.getNextByTime(OrderType.Enum.INSURANCE, model.getApplicantDate()));
            purchaseOrder.setDeliveryAddress(this.createAddress(quoteRecord.getApplicant(), quoteRecord.getArea()));
            purchaseOrder.appendDescription("该订单是由出单中心录入保单功能反向生成的订单，状态置为已完成");
        }
        purchaseOrder.setApplicant(quoteRecord.getApplicant());
        purchaseOrder.setAuto(quoteRecord.getAuto());
        purchaseOrder.setArea(quoteRecord.getArea());
        purchaseOrder.setObjId(quoteRecord.getId());
        purchaseOrder.setPayableAmount(model.getOriginalPremium());
        purchaseOrder.setPaidAmount(model.getRebateExceptPremium());
        purchaseOrder.setOperator(internalUserService.getRandomCustomer());
        purchaseOrder.setTrackingNo(model.getTrackingNo());
        purchaseOrder.setDeliveryInfo(createDeliveryInfo(purchaseOrder, model));
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        orderAgentService.checkAgent(purchaseOrder);
        return purchaseOrder;
    }

    private Address createAddress(User user, Area area) {
        List<Address> addressList = addressRepository.findByApplicant(user);
        if (CollectionUtils.isNotEmpty(addressList)) {
            return addressList.get(0);
        } else {
            Address address = new Address();
            address.setApplicant(user);
            address.setStreet("北苑路北");
            address.setArea(area);
            address.setCity(area.getId().toString());
            address.setCreateTime(new Date());
            return addressRepository.save(address);
        }
    }

    protected QuoteRecord createQuoteRecord(QuoteRecord oldQuoteRecord, OrderReverse model, User user, Auto auto, Area area) {
        QuoteRecord quoteRecord = new QuoteRecord();
        InsuranceCompany company = insuranceCompanyRepository.findOne(model.getInsuranceCompany());

        if (null != oldQuoteRecord) {
            BeanUtil.copyPropertiesContain(oldQuoteRecord, quoteRecord);
            quoteRecord.setUpdateTime(new Date());
        } else {
            quoteRecord.setCreateTime(model.getApplicantDate());
            quoteRecord.setType(QuoteSource.Enum.WEBPARSER_2);
        }
        quoteRecord.setAuto(auto);
        quoteRecord.setApplicant(user);
        quoteRecord.setInsuranceCompany(company);
        quoteRecord.setInsurancePackage(orderInsurancePackageService.createInsurancePackage(model));
        quoteRecord.setArea(area);

        quoteRecord.setPremium(model.getCommercialPremium());
        quoteRecord.setEffectiveDate(DateUtils.getDate(model.getCommercialEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        quoteRecord.setCompulsoryEffectiveDate(DateUtils.getDate(model.getCompulsoryEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        quoteRecord.setExpireDate(DateUtils.getDate(model.getCommercialExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        quoteRecord.setCompulsoryExpireDate(DateUtils.getDate(model.getCompulsoryExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        quoteRecord.setType(QuoteSource.Enum.TELEMARKETING_3);
        quoteRecord.setChannel(Channel.Enum.ORDER_CENTER_11);
        String[] contains = new String[]{"compulsoryPremium", "autoTax", "thirdPartyPremium", "thirdPartyAmount",
                "damagePremium", "damageAmount", "theftPremium", "theftAmount", "enginePremium", "driverPremium", "driverAmount",
                "passengerPremium", "passengerAmount", "spontaneousLossPremium", "spontaneousLossAmount", "glassPremium", "scratchAmount",
                "scratchPremium", "unableFindThirdPartyPremium", "designatedRepairShopPremium", "damageIop", "thirdPartyIop", "theftIop", "engineIop", "driverIop", "passengerIop", "spontaneousLossIop", "scratchIop"};
        BeanUtil.copyPropertiesContain(model, quoteRecord, contains);
        quoteRecord.setIopTotal(model.getIop());

        return quoteRecordRepository.save(quoteRecord);
    }


    public DataTablePageViewModel<OrderInsuranceViewModel> listInsurances(PublicQuery query) throws Exception {
        Sort.Direction sort = Sort.Direction.DESC;
        if (query.getOrderDir().equals("asc")) {
            sort = Sort.Direction.ASC;
        }
        Page<OrderOperationInfo> operationInfoPage = this.findBySpecAndPaginate(query.getKeyword(),
                super.buildPageable(query.getCurrentPage(), query.getPageSize(), sort, query.getSort()));
        return super.createResult(operationInfoPage, query.getDraw());
    }

    private Page<OrderOperationInfo> findBySpecAndPaginate(String keyword, Pageable pageable) {
        return orderOperationInfoRepository.findAll(new Specification<OrderOperationInfo>() {
            @Override
            public Predicate toPredicate(Root<OrderOperationInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<OrderOperationInfo> criteriaQuery = cb.createQuery(OrderOperationInfo.class);
                Path<Long> inputterPath = root.get("insuranceInputter");
                List<Predicate> predicateList = new ArrayList<>();
                predicateList.add(cb.isNotNull(inputterPath));
                if (StringUtils.isNotBlank(keyword)) {
                    Path<String> namePath = root.get("purchaseOrder").get("auto").get("owner");
                    Path<String> licensePlateNoPath = root.get("purchaseOrder").get("auto").get("licensePlateNo");
                    predicateList.add(cb.or(
                            cb.like(namePath, keyword + "%"),
                            cb.like(licensePlateNoPath, keyword + "%")
                            )
                    );
                }
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    @Override
    public List<OrderInsuranceViewModel> createList(List<OrderOperationInfo> orderOperationInfoList) throws Exception {
        if (orderOperationInfoList == null)
            return null;

        List<OrderInsuranceViewModel> viewDataList = new ArrayList<>();
        for (OrderOperationInfo orderOperationInfo : orderOperationInfoList) {
            viewDataList.add(this.createViewData(orderOperationInfo));
        }

        return viewDataList;
    }

    private OrderInsuranceViewModel createViewData(OrderOperationInfo orderOperationInfo) {
        OrderInsuranceViewModel model = new OrderInsuranceViewModel();
        PurchaseOrder purchaseOrder = orderOperationInfo.getPurchaseOrder();
        Auto auto = purchaseOrder.getAuto();
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        Insurance insurance = insuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);
        CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);
        String[] autoContains = new String[]{"licensePlateNo", "engineNo", "owner", "vinNo", "identity", "identityType"};
        BeanUtil.copyPropertiesContain(auto, model, autoContains);
        model.setBrand(auto.getAutoType() == null ? "" : auto.getAutoType().getCode());
        model.setEnrollDate(DateUtils.getDateString(auto.getEnrollDate(), DateUtils.DATE_SHORTDATE_PATTERN));
        if (auto.getLicensePlateNo().lastIndexOf("#") > -1) {
            model.setNewCar(true);
        }
        model.setInsuranceCompany(quoteRecord.getInsuranceCompany().getId());
        model.setOriginalPremium(purchaseOrder.getPayableAmount());
        model.setRebateExceptPremium(purchaseOrder.getPaidAmount());
        model.setArea(purchaseOrder.getArea().getId());
        model.setAreaName(purchaseOrder.getArea().getName());
        model.setApplicantName(purchaseOrder.getArea().getName());
        model.setExpressCompany(purchaseOrder.getDeliveryInfo() != null ? purchaseOrder.getDeliveryInfo().getExpressCompany() : null);
        model.setOrderType(1);
        Agent agent = agentRepository.findFirstByUser(purchaseOrder.getApplicant());
        if (null != agent) {
            model.setRecommender(agent.getId());
            model.setRecommenderName(agent.getName());
            model.setOrderType(2);
        }

        if (null != insurance) {
            String[] insuranceContains = new String[]{"thirdPartyPremium",
                    "thirdPartyAmount", "damagePremium", "damageAmount", "theftPremium", "theftAmount", "enginePremium",
                    "engineAmount", "driverPremium", "driverAmount", "passengerPremium", "passengerAmount", "passengerCount", "spontaneousLossPremium",
                    "spontaneousLossAmount", "glassPremium", "glassAmount", "scratchAmount", "scratchPremium", "unableFindThirdPartyPremium", "designatedRepairShopPremium", "insuredIdNo", "insuredName", "discount", "applicantName", "applicantIdNo"};
            BeanUtil.copyPropertiesContain(insurance, model, insuranceContains);
            model.setCommercialPremium(insurance.getPremium());
            model.setCommercialPolicyNo(insurance.getPolicyNo());
            model.setCommercialEffectiveDate(DateUtils.getDateString(insurance.getEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            model.setCommercialEffectiveHour(insurance.getEffectiveHour());
            model.setCommercialExpireDate(DateUtils.getDateString(insurance.getExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            model.setCommercialExpireHour(insurance.getExpireHour());
            model.setGlassType(quoteRecord.getInsurancePackage().getGlassType() == null ? null : quoteRecord.getInsurancePackage().getGlassType().getId());
            model.setInsuranceImage(getResourceAbsoluteUrl(insurance.getInsuranceImage()));
            model.setDamageIop(DoubleUtils.positiveDouble(insurance.getDamageIop()));
            model.setThirdPartyIop(DoubleUtils.positiveDouble(insurance.getThirdPartyIop()));
            model.setTheftIop(DoubleUtils.positiveDouble(insurance.getTheftIop()));
            model.setEngineIop(DoubleUtils.positiveDouble(insurance.getEngineIop()));
            model.setDriverIop(DoubleUtils.positiveDouble(insurance.getDriverIop()));
            model.setPassengerIop(DoubleUtils.positiveDouble(insurance.getPassengerIop()));
            model.setSpontaneousLossIop(DoubleUtils.positiveDouble(insurance.getSpontaneousLossIop()));
            model.setScratchIop(DoubleUtils.positiveDouble(insurance.getScratchIop()));
            model.setIop(insurance.getIopTotal());
            model.setInsuredIdType(insurance.getInsuredIdentityType());
            model.setApplicantIdType(insurance.getApplicantIdentityType());
        }

        if (null != compulsoryInsurance) {
            String[] compulsoryInsuranceContains = new String[]{"compulsoryPremium", "autoTax", "insuredIdType", "insuredIdNo", "insuredName", "applicantName", "applicantIdType", "applicantIdNo"};
            BeanUtil.copyPropertiesContain(compulsoryInsurance, model, compulsoryInsuranceContains);
            model.setCompulsoryPolicyNo(compulsoryInsurance.getPolicyNo());
            model.setCompulsoryEffectiveDate(DateUtils.getDateString(compulsoryInsurance.getEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            model.setCompulsoryEffectiveHour(compulsoryInsurance.getEffectiveHour());
            model.setCompulsoryExpireDate(DateUtils.getDateString(compulsoryInsurance.getExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN));
            model.setCompulsoryExpireHour(compulsoryInsurance.getExpireHour());
            model.setCompulsoryStampFile(getResourceAbsoluteUrl(compulsoryInsurance.getStamp()));
            model.setCompulsoryInsuranceImage(getResourceAbsoluteUrl(compulsoryInsurance.getInsuranceImage()));
            model.setDiscountCI(compulsoryInsurance.getDiscount());
        }

        model.setOrderNo(purchaseOrder.getOrderNo());
        model.setInsuranceInputter(orderOperationInfo.getInsuranceInputter() != null ? orderOperationInfo.getInsuranceInputter().getName() : "");
        model.setInsuranceOperator(orderOperationInfo.getOperator() == null ? "" : orderOperationInfo.getOperator().getName());
        model.setPurchaseOrderId(purchaseOrder.getId());
        model.setCreateTime(DateUtils.getDateString(orderOperationInfo.getCreateTime(), DateUtils.DATE_SHORTDATE_PATTERN));
        model.setUpdateTime(DateUtils.getDateString(orderOperationInfo.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        model.setTrackingNo(purchaseOrder.getTrackingNo());
        model.setChannel(purchaseOrder.getSourceChannel().getId());
        model.setGiftInfo(purchaseOrderGiftService.getGiftDetail(purchaseOrder));
        model.setChannelType((purchaseOrder.getSourceChannel().isOrderCenterChannel() || !purchaseOrder.getSourceChannel().isThirdPartnerChannel()) ? 1 : 2);
        InsurancePurchaseOrderRebate insurancePurchaseOrderRebate = insurancePurchaseOrderRebateRepository.findFirstByPurchaseOrder(purchaseOrder);
        if (insurancePurchaseOrderRebate != null) {
            model.setInstitution(insurancePurchaseOrderRebate.getDownChannelId());
            model.setInsurancePurchaseOrderRebateViewModel(InsurancePurchaseOrderRebateViewModel.createViewModel(insurancePurchaseOrderRebate));
        }
        model.setMobile(purchaseOrder.getApplicant().getMobile());
        return model;
    }

    public OrderInsuranceViewModel findModelByOrderNo(String orderNo) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findFirstByOrderNo(orderNo);
        OrderOperationInfo orderOperationInfo = orderOperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder);
        return this.createViewData(orderOperationInfo);
    }

    public void updateInsurance(OrderReverse model) {
        Long agentId = model.getRecommender();
        User user = agentRepository.findOne(agentId).getUser();
        Area area = areaRepository.findOne(model.getArea());
        if (null == area)
            throw new RuntimeException("can not find area by area -> " + model.getArea());

        PurchaseOrder oldOrder = purchaseOrderRepository.findFirstByOrderNo(model.getOrderNo());
        QuoteRecord oldQuoteRecord = quoteRecordRepository.findOne(oldOrder.getObjId());
        Auto auto = autoService.saveOrMerge(this.createAuto(model, area), user, new StringBuilder());
        QuoteRecord quoteRecord = this.createQuoteRecord(oldQuoteRecord, model, user, auto, area);
        PurchaseOrder purchaseOrder = this.createPurchaseOrder(oldOrder, model, quoteRecord);

        this.createPayment(purchaseOrder);
        this.createInsurance(model, purchaseOrder, quoteRecord);
        this.createOrderOperationInfo(purchaseOrder);
        this.createInsurancePurchaseOrderRebate(purchaseOrder, model);
    }

    public ResultModel validRepetitivePolicyNo(String orderNo, String commercialPolicyNo, String compulsoryPolicyNo) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findFirstByOrderNo(orderNo);
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        if (StringUtils.isNotBlank(commercialPolicyNo)) {
            if (null != insuranceRepository.findByPolicyNoAndQuoteRecordNot(StringUtils.trimToEmpty(commercialPolicyNo), quoteRecord)) {
                logger.warn("commercial policyNo {} already exists in commercial insurance.", commercialPolicyNo);
                return new ResultModel(false, "该商业险保单号已存在");
            }
        }

        if (StringUtils.isNotBlank(compulsoryPolicyNo)) {
            if (null != compulsoryInsuranceRepository.findByPolicyNoAndQuoteRecordNot(StringUtils.trimToEmpty(compulsoryPolicyNo), quoteRecord)) {
                logger.warn("compulsory policyNo {} already exists in compulsory insurance.", compulsoryPolicyNo);
                return new ResultModel(false, "该交强险保单号已存在");
            }
        }

        return null;
    }

    private String getInsuranceImageUrl(String resourceAbsoluteUrl) {
        if (StringUtils.isBlank(resourceAbsoluteUrl)) {
            return null;
        }
        String insurancePath = resourceService.getProperties().getInsurancePath();
        int index;
        if ((index = resourceAbsoluteUrl.indexOf(insurancePath)) > -1) {
            return resourceAbsoluteUrl.substring(index, resourceAbsoluteUrl.length());
        }
        return null;
    }

    private String getResourceAbsoluteUrl(String insuranceImageUrl) {
        if (StringUtils.isBlank(insuranceImageUrl)) {
            return null;
        }
        String insurancePath = resourceService.getProperties().getInsurancePath();
        int index;
        if ((index = insuranceImageUrl.indexOf(insurancePath)) > -1) {
            return resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(insurancePath), insuranceImageUrl.substring(index + insurancePath.length(), insuranceImageUrl.length()));
        }
        return null;
    }

    public DeliveryInfo createDeliveryInfo(PurchaseOrder purchaseOrder, OrderReverse model) {
        DeliveryInfo deliveryInfo = purchaseOrder.getDeliveryInfo();
        if (deliveryInfo == null) {
            deliveryInfo = new DeliveryInfo();
            deliveryInfo.setCreateTime(Calendar.getInstance().getTime());
        }
        deliveryInfo.setExpressCompany(model.getExpressCompany());
        deliveryInfo.setTrackingNo(model.getTrackingNo());
        deliveryInfo.setUpdateTime(Calendar.getInstance().getTime());
        deliveryInfo.setOperator(internalUserManageService.getCurrentInternalUser());
        return deliveryInfoRepository.save(deliveryInfo);
    }

    public List<OrderInsuranceViewModel> listByLicensePlateNo(String licensePlateNo) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        List<OrderOperationInfo> orderOperationInfoList = orderOperationInfoRepository.findByLicensePlateNo(licensePlateNo, calendar.getTime());
        return this.createList(orderOperationInfoList);
    }

    /**
     * 如果投保时间小于代理人更新时间且查询无当前时间条件下的费率信息，提示回录历史费率
     *
     * @param model
     * @return
     */
    public boolean validAgentRebate(OrderReverse model) {
        Agent agent = agentRepository.findOne(model.getRecommender());
        Area area = areaRepository.findById(model.getArea());
        if (!DateUtils.compareDate(model.getApplicantDate(), agent.getUpdateTime())) {
            AgentRebateHistory agentRebateHistory = agentRebateHistoryService.listByAgentAndAreaAndInsuranceCompanyAndDateTime(agent, area, model.getInsuranceCompany(), model.getApplicantDate());
            if (agentRebateHistory == null) {
                return false;
            }
        }
        return true;
    }

    protected void createInsurancePurchaseOrderRebate(PurchaseOrder purchaseOrder, OrderReverse model) {
        InsurancePurchaseOrderRebateViewModel insurancePurchaseOrderRebateViewModel = model.getInsurancePurchaseOrderRebateViewModel();
        Agent agent = agentRepository.findOne(model.getRecommender());
        // Area area = autoService.getAreaByLicensePlateNo(model.getLicensePlateNo());
        Area area = areaRepository.findOne(model.getArea());
        Date scopeDate = model.getApplicantDate();
        AgentRebateHistory agentRebateHistory = agentRebateHistoryService.listByAgentAndAreaAndInsuranceCompanyAndDateTime(agent, area, model.getInsuranceCompany(), scopeDate);
        if (agentRebateHistory != null) {
            insurancePurchaseOrderRebateViewModel.setUpCommercialRebate(agentRebateHistory.getCommercialRebate());
            insurancePurchaseOrderRebateViewModel.setUpCompulsoryRebate(agentRebateHistory.getCompulsoryRebate());
        } else {
            insurancePurchaseOrderRebateViewModel.setUpCommercialRebate(agent.getRebate());
            insurancePurchaseOrderRebateViewModel.setUpCompulsoryRebate(agent.getRebate());
        }
        insurancePurchaseOrderRebateViewModel.setUpRebateChannel(RebateChannel.Enum.REBATE_CHANNEL_AGENT);
        insurancePurchaseOrderRebateViewModel.setUpChannelId(agent.getId());
        insurancePurchaseOrderRebateViewModel.setDownRebateChannel(RebateChannel.Enum.REBATE_CHANNEL_INSTITUTION);
        insurancePurchaseOrderRebateViewModel.setDownChannelId(model.getInstitution());
        insurancePurchaseOrderRebateViewModel.setCommercialPremium(model.getCommercialPremium());
        insurancePurchaseOrderRebateViewModel.setCompulsoryPremium(model.getCompulsoryPremium());
        insurancePurchaseOrderRebateViewModel.setPurchaseOrderId(purchaseOrder.getId());
        insurancePurchaseOrderRebateManageService.savePurchaseOrderRebate(insurancePurchaseOrderRebateViewModel);
    }


    public void saveCommercialInsuranceImage(String policyNo, String insuranceInputFilePath) {

        Insurance insurance = insuranceRepository.findLastByPolicyNo(policyNo);
        if (insurance != null) {
            insurance.setInsuranceImage(insuranceInputFilePath);
            insurance.setUpdateTime(new Date());

            insuranceRepository.save(insurance);
        }
    }

    public void saveCompulsoryInsuranceImage(String policyNo, String insuranceInputFilePath) {

        CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceRepository.findLastByPolicyNo(policyNo);
        if (compulsoryInsurance != null) {
            compulsoryInsurance.setInsuranceImage(insuranceInputFilePath);
            compulsoryInsurance.setUpdateTime(new Date());

            compulsoryInsuranceRepository.save(compulsoryInsurance);
        }
    }

}
