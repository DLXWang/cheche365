package com.cheche365.cheche.ordercenter.web.controller.quote;

import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.GiftRepository;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import com.cheche365.cheche.core.service.*;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.manage.common.model.PurchaseOrderExtend;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.service.quote.QuoteAmendService;
import com.cheche365.cheche.ordercenter.web.model.quote.QuoteRecordViewModel;
import com.cheche365.cheche.web.service.InsurancePurchaseOrderRebateService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by xu.yelong on 2016/9/10.
 */
@RestController
@RequestMapping("/orderCenter/quote/amend")
public class QuoteAmendController {
    private Logger log = LoggerFactory.getLogger(QuoteAmendController.class);

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private InsurancePackageService insurancePackageService;

    @Autowired
    private InsuranceCompanyService insuranceCompanyService;

    @Autowired
    private QuoteRecordService quoteRecordService;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private QuoteAmendService quoteAmendService;

    @Autowired
    private GiftRepository giftRepository;

    @Autowired
    private PurchaseOrderGiftService purchaseOrderGiftService;

    @Autowired
    private PurchaseOrderAmendService purchaseOrderAmendService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private InsurancePurchaseOrderRebateService insurancePurchaseOrderRebateService;

    private static final String RECORD_LIST = "recordList";
    private static final String PAYABLE_AMOUNT = "payableAmount";
    private static final String PAID_AMOUNT = "paidAmount";
    private static final String GIFT_AMOUNT = "giftAmount";
    private static final String GIFT_DETAIL = "giftDetail";

    @RequestMapping(value = "/{orderId}", method = RequestMethod.GET)
    public Map<String, Object> getQuoteInfo(@PathVariable Long orderId) {
        Map<String, Object> map = new HashMap<String, Object>();
        PurchaseOrder purchaseOrder = purchaseOrderService.findById(orderId);
        List<QuoteRecord> recordList = getQuoteByAmend(purchaseOrder);
        Double giftAmount = 0.00;
        List<Long> giftId = (List<Long>) purchaseOrder.getGiftId();
        giftId.remove(null);//因为会出现“all elements are null”的情况，所以把null的元素去除掉
        if (CollectionUtils.isNotEmpty(giftId)) {
            Gift gift = giftRepository.findOne(((List<Long>) purchaseOrder.getGiftId()).get(0));
            if (gift != null)
                giftAmount = gift.getGiftAmount();
        }
        Double paidAmount = purchaseOrderAmendService.getPaidAmountByOrderId(orderId);
        map.put(RECORD_LIST, recordList);
        map.put(PAYABLE_AMOUNT, purchaseOrder.getPayableAmount());
        map.put(PAID_AMOUNT, paidAmount);
        map.put(GIFT_AMOUNT, giftAmount);
        map.put(GIFT_DETAIL, purchaseOrderGiftService.getGiftDetail(purchaseOrder));
        return map;
    }

    public List<QuoteRecord> getQuoteByAmend(PurchaseOrder purchaseOrder) {
        List<QuoteRecord> quoteRecordList = new ArrayList<>();
        QuoteRecord quoteRecord = quoteRecordService.getById(purchaseOrder.getObjId());
        PurchaseOrderAmend purchaseOrderAmend = purchaseOrderAmendService.findByQuoteRecord(quoteRecord);
        if (purchaseOrderAmend != null) {
            quoteRecordList.add(purchaseOrderAmend.getOriginalQuoteRecord());
            quoteRecordList.add(purchaseOrderAmend.getNewQuoteRecord());
        } else {
            quoteRecordList.add(quoteRecord);
        }
        return quoteRecordList;
    }

    @Transactional
    @RequestMapping(value = "/saveQuote", method = RequestMethod.POST)
    public Long saveQuote(@RequestBody QuoteRecordViewModel viewModel) {
        PurchaseOrder purchaseOrder = purchaseOrderService.findById(viewModel.getPurchaseOrderId());
        QuoteRecord originalQuoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        QuoteRecord quoteRecord = quoteRecordService.reGenerateQuoteRecord(originalQuoteRecord);
        InsuranceCompany company = insuranceCompanyService.findById(viewModel.getInsuranceCompanyId());
        quoteRecord.setCompulsoryPremium(viewModel.getCompulsoryPremium());
        quoteRecord.setPremium(viewModel.getPremium());
        quoteRecord.setInsuranceCompany(company);
        quoteRecord.setInsurancePackage(createInsurancePackage(viewModel));
        quoteRecord.setArea(purchaseOrder.getArea());
        quoteRecord.setUpdateTime(new Date());
        quoteRecord.setCreateTime(new Date());
        quoteRecord.setType(QuoteSource.Enum.TELEMARKETING_3);

        String[] contains = new String[]{"premium", "autoTax",
                "thirdPartyPremium", "damagePremium", "theftPremium",
                "enginePremium", "driverPremium", "passengerPremium",
                "spontaneousLossPremium", "glassPremium", "scratchPremium",
                "iopTotal", "theftAmount", "spontaneousLossAmount", "unableFindThirdPartyPremium"};
        BeanUtil.copyPropertiesContain(viewModel, quoteRecord, contains);

        contains = new String[]{"thirdPartyAmount",
                "damageAmount", "theftAmount", "driverAmount",
                "passengerAmount", "spontaneousLossAmount", "scratchAmount",
                "damageIop", "thirdPartyIop", "theftIop", "engineIop", "driverIop", "passengerIop", "scratchIop", "spontaneousLossIop"};
        BeanUtil.copyPropertiesContain(viewModel.getInsurancePackage(), quoteRecord, contains);

        quoteRecord = quoteRecordRepository.save(quoteRecord);
        return quoteRecord.getId();
    }

    @RequestMapping(value = "/modify/{orderId}", method = RequestMethod.POST)
    public ResultModel modifyOrder(@PathVariable Long orderId, @RequestBody PurchaseOrderExtend purchaseOrder) {

        PurchaseOrder originalPurchaseOrder = purchaseOrderService.findById(orderId);
//        if(originalPurchaseOrder.getPayableAmount().doubleValue()==purchaseOrder.getPayableAmount().doubleValue()){
//            return new ResultModel(false,"订单报价无调整，请重新调整报价后提交");
//        }
//        Double paidAmount=purchaseOrderAmendService.getPaidAmountByOrderId(originalPurchaseOrder.getId());
//        if(paidAmount.equals(purchaseOrder.getPaidAmount())){
//            return new ResultModel(false,"增补金额为0，请重新调整报价后提交");
//        }
        ResultModel resultModel = new ResultModel();
        if (originalPurchaseOrder.getPayableAmount() < purchaseOrder.getPayableAmount()) {
            resultModel.setMessage(PaymentType.Enum.ADDITIONALPAYMENT_2.getId().toString());
        }
        try {
            quoteAmendService.modifyOrder(originalPurchaseOrder, purchaseOrder);
        } catch (BusinessException be) {
            log.debug("修改订单业务异常!", be);
            resultModel.setPass(false);
            resultModel.setMessage(be.getMessage());
        } catch (Exception e) {
            log.debug("修改订单系统异常!", e);
            resultModel.setPass(false);
            resultModel.setMessage("系统异常!");
        }
        return resultModel;
    }

    @RequestMapping(value = "/check/{orderId}", method = RequestMethod.POST)
    public ResultModel checkPrevAmend(@PathVariable Long orderId) {
        ResultModel resultModel = new ResultModel();
        PurchaseOrder originalPurchaseOrder = purchaseOrderService.findById(orderId);
        if (quoteAmendService.getPrevAmend(originalPurchaseOrder).size() > 0) {
            resultModel = new ResultModel(false, "当前订单已存在未处理完成的增补记录，继续修改将取消上次增补记录，是否继续？");
        }
        return resultModel;
    }

    @RequestMapping(value = "/agent/rebate", method = RequestMethod.GET)
    public Double getAgentRebate(@RequestParam(value = "quoteRecordId", required = true) Long quoteRecordId,
                                 @RequestParam(value = "agentId", required = true) Long agentId) {
        Agent agent = agentService.findOne(agentId);
        QuoteRecord quoteRecord = quoteRecordService.getById(quoteRecordId);
        Double rebateAmount = agentService.calculateRebateAmount(quoteRecord, agent);
        return rebateAmount;
    }

    @RequestMapping(value = "/channel/rebate", method = RequestMethod.GET)
    public Double getChannelRebate(@RequestParam(value = "quoteRecordId") Long quoteRecordId,
                                   @RequestParam(value = "orderId") Long orderId) {
        QuoteRecord quoteRecord = quoteRecordService.getById(quoteRecordId);
        PurchaseOrder purchaseOrder = purchaseOrderService.findById(orderId);
        QuoteRecord origionalQuoteRecord = quoteRecordService.getById(purchaseOrder.getObjId());
        //新的报价所选保险公司和历史报价保险公司不一致，不获取历史费率
        if (!origionalQuoteRecord.getInsuranceCompany().getId().equals(quoteRecord.getInsuranceCompany().getId())) {
            return DoubleUtils.DOUBLE_ZERO;
        }

        InsurancePurchaseOrderRebate rebate = insurancePurchaseOrderRebateService.findByPurchaseOrder(purchaseOrder);
        if (rebate == null || purchaseOrder.getSourceChannel().isAgentChannel()) {
            return DoubleUtils.DOUBLE_ZERO;
        }
        return insurancePurchaseOrderRebateService.discountAmount(quoteRecord, rebate);
    }

    private InsurancePackage createInsurancePackage(QuoteRecordViewModel viewModel) {
        InsurancePackage insurancePackage = viewModel.getInsurancePackage();
        insurancePackage.calculateUniqueString();
        InsurancePackage old = insurancePackageService.findByUniqueString(insurancePackage.getUniqueString());
        if (null != old)
            return old;
        return insurancePackageService.saveInsurancePackage(insurancePackage);
    }


}
