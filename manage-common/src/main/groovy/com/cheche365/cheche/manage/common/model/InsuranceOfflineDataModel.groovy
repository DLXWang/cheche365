package com.cheche365.cheche.manage.common.model

import com.cheche365.cheche.common.util.DateUtils
import groovy.transform.ToString
import groovy.transform.TupleConstructor

import static com.cheche365.cheche.common.util.StringUtil.defaultNullStr
import static org.apache.commons.lang3.StringUtils.EMPTY

/**
 * Created by yinJianBin on 20.0 as Double17/9/19.
 */
@TupleConstructor
@ToString
public class InsuranceOfflineDataModel implements Comparable<InsuranceOfflineDataModel> {
    def order              //序号
    def createTime              //出单时间
    def institution              //出单机构
    def insuranceCompanyName              //保险公司
    def agentName              //平台/来源(代理人姓名)
    String agentIdentity              //代理人身份证
    def owner              //车主
    def licenseNo              //车牌号
    def totalPremium              //保费总额
    def compulsory              //交强险
    def autoTax              //车船税
    def premium              //商业险
    def upCompulsoryRebate = 0.0 as Double              //交强点位
    def upCommercialRebate = 0.0 as Double              //商业点位
    def upCommercialAmount = 0.0 as Double          //渠优（保酷）(上游商业险佣金)
    def upCompulsoryAmount = 0.0 as Double       //渠优（保酷）(上游交强险佣金)
    def policyNo              //保单号
    def engineNo              //发动机号
    def vinNo              //车架号
    def downCommercialRebate = 0.0 as Double              //天道佣金费率(商业险)
    def downCompulsoryRebate = 0.0 as Double          //天道佣金费率(交强险)
    def downCommercialAmount = 0.0 as Double      //天道佣金(下游商业险佣金)
    def downCompulsoryAmount = 0.0 as Double  //天道佣金(下游交强险佣金)
    def area              //根据车牌号计算出的地区
    def insuranceType              //保险类型("商业险"或者"交强险")
    double totalPremiumDouble = 0.0

    def errorMessage        //上传错误原因

    def insurance = null
    def compulsoryInsurance = null
    def purchaseOrder = null
    OfflineDataHistory offlineDataHistory = null

    def cardNumber
    def applicantName
    def code
    def enrollDate

    def seats
    def newPrice

    def kilometerPerYear

    def effectiveDate

    def insuranceDetail

    def extraPayamount = null      //增补或者退款的金额
    def fullRefund = false


    InsuranceOfflineDataModel setErrorMessage(errorMessage) {
        this.errorMessage = errorMessage
        return this
    }

    public static final String INSURANCE_TYPE_COMMERCIAL = "商业险"
    public static final String INSURANCE_TYPE_COMPULSORY = "交强险"


    public static final Map<String, String> resultMap = [
            "invalidInsuranceCompany": "未识别的保险公司",
            "errorDate"              : "日期格式错误",
            "invalidAmount"          : "非法的金额",
            "empty"                  : "不能为空",
            "invalidLicenseNo"       : "非法的车牌号",
            "licenseNoDuplicated"    : "车牌号重复",
            "policyNoDuplicated"     : "保单号重复",
            "invalidInsuredDate"     : "投保日期不在指定的范围内"
    ]


    public String toStringList(history) {
        String upAmount = EMPTY
        String downAmount = EMPTY
        if (insuranceType == INSURANCE_TYPE_COMMERCIAL) {
            upAmount = upCommercialAmount
            downAmount = downCommercialAmount
        } else {
            upAmount = upCompulsoryAmount
            downAmount = downCompulsoryAmount
        }
        def createTimeString = (createTime != null && createTime instanceof Date) ? DateUtils.getDateString(createTime, DateUtils.DATE_SHORTDATE2_PATTERN) : createTime
        def enrollDateString = (enrollDate != null && enrollDate instanceof Date) ? DateUtils.getDateString(enrollDate, DateUtils.DATE_SHORTDATE2_PATTERN) : enrollDate
        def effectiveDateString = (effectiveDate != null && effectiveDate instanceof Date) ? DateUtils.getDateString(effectiveDate, DateUtils.DATE_SHORTDATE2_PATTERN) : effectiveDate

        List<String> stringList =
                history.type == OfflineOrderImportHistory.TYPE_FANHUA_ADDED ?
                        //'出单日期', '出单机构', '付款单位', '保险公司', '代理人', '代理人身份证号', '收款银行卡号', '投保人姓名', '车牌号', '发动机号', '车架号', '车型', '初登日期', '车身颜色', '约定行驶区域', '使用性质', '号牌种类', '车身自重', '过户车', '机动车种类', '号牌颜色', '使用年限', '座位数', '新车购置价', '发动机排量', '车辆用户类型', '平均行驶里程', '保单号', '保费总额', '险种', '车船税', '投保保险公司代码', '起保日期', '代理人交强点位', '代理人商业点位', '商业险种细节','错误原因'
                        [
                                createTimeString, defaultNullStr(institution), '', defaultNullStr(insuranceCompanyName), defaultNullStr(agentName), "\t" + defaultNullStr(agentIdentity),
                                "\t" + defaultNullStr(cardNumber), defaultNullStr(applicantName), defaultNullStr(licenseNo), "\t" + engineNo, "\t" + vinNo, defaultNullStr(code),
                                defaultNullStr(enrollDateString), '', '', '', '', '', '', '', '', '', defaultNullStr(seats), defaultNullStr(newPrice), '', '', defaultNullStr(kilometerPerYear),
                                "\t" + policyNo, defaultNullStr(totalPremium), '机动车' + insuranceType, defaultNullStr(autoTax), '', effectiveDateString,
                                '', '', '', defaultNullStr(errorMessage)
                        ]
                        :
                        //'序号', '收表日期', '出单时间', '出单机构', '付款单位', '保险公司', '平台/来源', '代理人身份证', '车主', '车牌号', '保费总额', '交强险', '车船税', '商业险', '交强点位', '商业点位', '渠优（保酷）', '保单号', '发动机号', '车架号', '交强点位', '商业点位', '天道佣金', '结算给泛华'
                        [
                                order as String, "", createTimeString, institution, "", insuranceCompanyName, defaultNullStr(agentName), "\t" + defaultNullStr(agentIdentity),
                                owner, licenseNo, totalPremium, defaultNullStr(compulsory), defaultNullStr(autoTax), defaultNullStr(premium), defaultNullStr(upCompulsoryRebate),
                                defaultNullStr(upCommercialRebate), upAmount, "\t" + policyNo, "\t" + engineNo, "\t" + vinNo, "", "", downAmount, "", defaultNullStr(errorMessage)
                        ]
        return stringList.join(",") + "\r"
    }

    @Override
    int compareTo(InsuranceOfflineDataModel o) {
        return this.order - o.order
    }
}
