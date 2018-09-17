package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderAmendRepository
import com.cheche365.cheche.core.util.BigDecimalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.PaymentType.Enum.*


/**
 * Created by Administrator on 2016/9/12 0012.
 */
@Service
public class PaymentRefundService {

    private Logger logger = LoggerFactory.getLogger(PaymentRefundService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PurchaseOrderAmendRepository purchaseOrderAmendRepository;

    @Autowired
    private PaymentSerialNumberGenerator paymentSerialNumberGenerator;

    /**
     * 保存退款单
     * @param PaymentRefundList
     * @param orderNo
     * @return
     */
    public boolean saveRefundPayment(List<Payment> paymentRefundList,String orderNo){
        boolean bol = true;
        try {
            for (Payment p : paymentRefundList) {
                PurchaseOrderAmend purchaseOrderAmend = new PurchaseOrderAmend();
                purchaseOrderAmend.setPurchaseOrderAmendStatus(PurchaseOrderAmendStatus.Enum.CREATE);
                purchaseOrderAmendRepository.save(purchaseOrderAmend);
                p.setPurchaseOrderAmend(purchaseOrderAmend);
                paymentRepository.save(p);
            }
        }catch(Exception e){
            logger.error("订单:"+orderNo+"保存退款单失败");
            logger.error(e.getMessage());
            bol = false;
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "订单"+orderNo+"保存退款单失败");
        }
        return bol;
    }


    /**
     * 根据金额计算订单的退款单
     * @param paymentPayList 首次支付和二次支付已经完成的未取消的
     * @param refundPrice 退款金额
     * @param purchaseOrder 订单
     * @return
     * 方法描述:订单退款金额可能在方法内部一直变化,因为订单支付可能是对此支付,退款金额可能会大于某次n次支付的总和
     * 所以每计算一笔支付单的可退款金额都需要重新计算订单退款金额，方法是订单退款金额减去支付单的可以款金额
     * 为新的订单退款金额,依次类推单订单退款金额为0
     * 支付单的可退款金额的计算,之前有可能退过款，所以支付单的支付金额不一定是支付单的可退款金额
     * 需要计算支付单之前退款金额的总和和支付单的支付金额，支付单的支付金额减去支付单之前退款金额的总和为支付单的可退款金额
     */
    public List<Payment> getRefundPayment(List<Payment> paymentPayList,Double refundPrice,PurchaseOrder purchaseOrder){
        logger.info("订单:"+purchaseOrder.getOrderNo()+" 退款金额:"+refundPrice);
        return getRefund(paymentPayList, BigDecimalUtil.bigDecimalValue(refundPrice), purchaseOrder);
    }

    private List<Payment> getRefund(List<Payment> allPayments,BigDecimal refundPrice,PurchaseOrder purchaseOrder){
        if(0==refundPrice){
            return null;
        }
        def refundPriceDes = refundPrice;
        def paymentPayList = allPayments.findAll{ [INITIALPAYMENT_1, ADDITIONALPAYMENT_2].contains(it.paymentType)&&it.getStatus().equals(PaymentStatus.Enum.PAYMENTSUCCESS_2)}

        List<Payment> refundPaymentList = new ArrayList<Payment>();

        for(int i = paymentPayList.size()-1;i>=0;i--){
            BigDecimal amountPrice = BigDecimalUtil.bigDecimalValue(paymentPayList.get(i).getAmount());//当前退款单总支付金额
            BigDecimal alsoPrice = BigDecimalUtil.bigDecimalValue(0.00);//声明还可退金额
            //支付单的子退款单退款成功集合
            List<Payment> paymentRefundList = getpaymentRefundList(allPayments,paymentPayList.get(i));
            BigDecimal beforeSumPrice = BigDecimalUtil.bigDecimalValue(0d);//声明之前该支付单已退款金额
            //计算已退款金额
            if(null!=paymentRefundList&&paymentRefundList.size()>0) {
                //计算该收费单还可退款的金额
                for (Payment p : paymentRefundList) {
                    beforeSumPrice = BigDecimalUtil.add(beforeSumPrice, BigDecimalUtil.bigDecimalValue(p.getAmount()));
                }
            }
            //支付金额减去已退款金额为当前支付单可退金额
            alsoPrice = BigDecimalUtil.subtract(amountPrice,beforeSumPrice);
            if (alsoPrice.doubleValue() == 0) {
                continue;
            }
            Payment payment = makeRefundPayment(purchaseOrder,paymentPayList.get(i));
            if(refundPrice.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()>alsoPrice.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()){//如果计算后的订单退款金额大于当前支付单可退金额
                payment.setAmount(alsoPrice.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                refundPrice = BigDecimalUtil.subtract(refundPrice,alsoPrice);//计算订单退款金额之前的退款金额减去当前支付单可退金额为计算后的订单退款金额
                refundPaymentList.add(payment);
            }else if(refundPrice.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() == alsoPrice.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()){//如果计算后的订单退款金额等于当前支付单可退金额
                payment.setAmount(alsoPrice.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                refundPrice = BigDecimalUtil.subtract(refundPrice,alsoPrice);
                refundPaymentList.add(payment);
                break;
            }else{
                payment.setAmount(refundPrice.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                refundPrice = BigDecimalUtil.subtract(refundPrice,alsoPrice);
                refundPaymentList.add(payment);
                break;
            }
        }
        //退款金额大于实际可退金额
        if(refundPrice.doubleValue()>0){
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "订单"+purchaseOrder.getOrderNo()+"退款单失败,退款金额大于实际金额.");
        }else{
            purchaseOrder.appendDescription("生成退款记录,退款金额为:$refundPriceDes");
        }
        return refundPaymentList;
    }

    /**
     * 根据支付单获得该支付单已经退款成功的退款单集合
     * @param allPayments 订单下支付单退款单集合
     * @param payment 支付单
     * @return
     */
    private List<Payment> getpaymentRefundList(List<Payment> allPayments,Payment payment){
        return allPayments.findAll {payment == it.upstreamId&&it.getStatus().equals(PaymentStatus.Enum.PAYMENTSUCCESS_2)}   //收费单以退款的退款单
    }

    /**
     * 退款单公共信息
     * @param purchaseOrder
     * @param paymentUps
     * @return
     */
    Payment makeRefundPayment(PurchaseOrder purchaseOrder, Payment paymentUps) {
        Payment payment = new Payment();
        payment.clientType = paymentUps.clientType
        payment.user = paymentUps.user
        payment.purchaseOrder = purchaseOrder
        payment.status = PaymentStatus.Enum.NOTPAYMENT_1
        payment.upstreamId = paymentUps
        payment.channel = paymentUps.channel
        payment.comments = paymentUps.channel.name
        return payment;
    }

}
