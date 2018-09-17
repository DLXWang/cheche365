package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.PurchaseOrder;

/**
 * 阳光保险：第三方业务处理
 * Created by sunhuazhong on 2015/3/30.
 */
public interface IThirdPartyBusinessService {

    /**
     * 为投保单号和投保人名称加密，返回到支付页面的URL
     *
     * @param proposalNo
     * @param applicantName
     * @return
     */
    public String getProposalNoAndApplicantNameWithEncrypt(String proposalNo, String applicantName) throws Exception;

    /**
     * 获取验证码
     * @param purchaseOrder
     * @return
     */
    public boolean getVerificationCode(PurchaseOrder purchaseOrder);

    /**
     * 保存验证码
     * @param purchaseOrder
     * @param issueCode
     * @return
     */
    public boolean saveVerificationCode(PurchaseOrder purchaseOrder, String issueCode);
}
