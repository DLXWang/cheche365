package com.cheche365.cheche.ordercenter.web.model.order;

import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.WechatConstant;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.Payment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangshaobin on 2016/9/21.
 */
public class PaymentInfoViewModel {
    private Long id;
    private String type;
    private String amount;
    private String status;
    private String operateTime;
    private String channel;
    private String outTradeNo;
    private String thirdpartyPaymentNo;
    private Long typeId;
    private Long recordId;
    private Long statusId;
    private String orderNo;
    private String clientType;
    private String paymentType;
    private String mchId;
    private String paymentPlatform;

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public void setThirdpartyPaymentNo(String thirdpartyPaymentNo) {
        this.thirdpartyPaymentNo = thirdpartyPaymentNo;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Long getRecordId() {
        return recordId;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public String getChannel() {
        return channel;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public String getThirdpartyPaymentNo() {
        return thirdpartyPaymentNo;
    }

    public Long getTypeId() {
        return typeId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getPaymentPlatform() {
        return paymentPlatform;
    }

    public void setPaymentPlatform(String paymentPlatform) {
        this.paymentPlatform = paymentPlatform;
    }

    public static List<PaymentInfoViewModel> changeObjArrToPaymentInfoViewModel(List<Payment> payments){
        List<PaymentInfoViewModel> list = new ArrayList<PaymentInfoViewModel>();
        for(Payment payment : payments){
            PaymentInfoViewModel viewModel = new PaymentInfoViewModel();
            viewModel.setId(payment.getId());
            Long type = payment.getPaymentType().getId();
            viewModel.setType(payment.getPaymentType().getDescription());
            viewModel.setAmount(String.valueOf(payment.getAmount()));
            payment.toDisplayText();
            viewModel.setStatus(payment.getStatus().getStatus());
            viewModel.setOperateTime(payment.getUpdateTime() == null? "":String.valueOf(payment.getUpdateTime()).substring(0,19));
            viewModel.setChannel(payment.getChannel()==null?"":payment.getChannel().getFullDescription());
            viewModel.setThirdpartyPaymentNo(payment.getThirdpartyPaymentNo());
            viewModel.setTypeId(type);
            viewModel.setOutTradeNo(payment.getOutTradeNo());
            viewModel.setRecordId(payment.getPurchaseOrder().getObjId());
            viewModel.setStatusId(payment.getStatus().getId());
            viewModel.setOrderNo(payment.getPurchaseOrder().getOrderNo());
            Channel clientType = payment.getClientType();
            if(null != clientType){
                viewModel.setClientType(payment.getClientType().getName());
            }
            viewModel.setPaymentType(payment.getPaymentType().getDescription());
            if(!StringUtil.isNull(payment.getMchId())){
                viewModel.setMchId(payment.getMchId());
                viewModel.setPaymentPlatform(WechatConstant.findPaymentClient(payment.getMchId()).getDescription());
            }
            list.add(viewModel);
        }
        return list;
    }
}
