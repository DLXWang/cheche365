package com.cheche365.cheche.core.model;

/**
 * Created by suyq on 2016/5/19.
 * 报价和保单（商业及交强险）注解的枚举
 */
public enum QuoteRecordAndInsuranceAnnotation {

    /**
     * QR
     */
    TimeCausedInsuranceBothNotAllowed,  // 时间引发的商业交强都不允许投保
    Notification,                       // 告知单
    InsuranceClause,                    // 保险条款
    SpecialAgreement,                   // 特别约定
    InsuringDeclaration,                // 投保声明
    CommercialDisclaimer,               // 商业险免责声明

    /**
     * Insurance
     */
    UnderwritingStatus                        // 核保状态
}
