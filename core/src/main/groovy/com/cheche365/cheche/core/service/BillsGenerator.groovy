package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

/**
 * Created by zhengwei on 9/13/16.
 * 根据报价生成商业险和交强险保单数据
 */
@Service
class BillsGenerator {

    private Logger log = LoggerFactory.getLogger(BillsGenerator.class);

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;

    @Autowired
    private InsuranceRepository insuranceRepository;

    Object[] generateBills(QuoteRecord originalQuoteRecord,QuoteRecord quoteRecord, PurchaseOrder order) {
        Insurance insuranceBill = null;
        CompulsoryInsurance compulsoryInsuranceBill = null;
        if (this.isBusinessInsuranceBillable(quoteRecord)) {
            insuranceBill = this.generateInsurance(originalQuoteRecord,quoteRecord, order);
        }
        if (this.isCompulsoryInsuranceBillable(quoteRecord)) {
            compulsoryInsuranceBill = this.generateCompulsoryInsurance(originalQuoteRecord,quoteRecord, order);
        }
        Object[] obj = new Object[2];
        obj[0]=insuranceBill;
        obj[1]=compulsoryInsuranceBill;
        return obj;
    }

    private boolean isBusinessInsuranceBillable(QuoteRecord quoteRecord) {
        return quoteRecord.getPremium() > 0;
    }

    private Insurance generateInsurance(QuoteRecord originalQuoteRecord,QuoteRecord quoteRecord, PurchaseOrder order) {
        Insurance insurance = this.generateInsuranceBillByQuote(quoteRecord);
        if (null != originalQuoteRecord) { //因为京东渠道需要实时同步证件信息，所以暂时这样处理，如果没有旧的商业险保单就从旧的交强险保单里取证件信息
            Insurance oldInsurance = insuranceRepository.findFirstByQuoteRecordOrderByIdDesc(originalQuoteRecord);
            CompulsoryInsurance oldCompulsoryInsurance = compulsoryInsuranceRepository.findFirstByQuoteRecordOrderByIdDesc(originalQuoteRecord)
            insurance.setInsuredName(oldInsurance?.insuredName ?: oldCompulsoryInsurance?.insuredName)
            insurance.setInsuredIdNo(oldInsurance?.insuredIdNo ?: oldCompulsoryInsurance?.insuredIdNo)
            insurance.setInsuredIdentityType(oldInsurance?.insuredIdentityType ?: oldCompulsoryInsurance?.insuredIdentityType)
            insurance.setApplicantName(oldInsurance?.applicantName ?: oldCompulsoryInsurance?.applicantName)
            insurance.setApplicantIdNo(oldInsurance?.applicantIdNo ?: oldCompulsoryInsurance?.applicantIdNo)
            insurance.setApplicantIdentityType(oldInsurance?.applicantIdentityType ?: oldCompulsoryInsurance?.applicantIdentityType)
            insurance.setProposalNo(oldInsurance?.proposalNo)
            insurance.setPolicyNo(oldInsurance?.policyNo)
            insurance.setEffectiveDate(oldInsurance?.effectiveDate)
            insurance.setExpireDate(oldInsurance?.expireDate)
        }


//        if (!StringUtils.isBlank(order.getInsuredName())) {  //这部分只有增补时候使用，没必要处理被保险人等信息，后续会重用下单被保险人处理代码
//            insurance.setInsuredName(order.getInsuredName());
//            log.debug("the insured name is different from the auto owner, set the insured name to {} for insurance bill", insurance.getInsuredName());
//        } else {
//            insurance.setInsuredName(quoteRecord.getAuto() != null ? quoteRecord.getAuto().getOwner() : "");
//            log.debug("the insured name is same with the auto owner, set the insured name to {} for insurance bill", insurance.getInsuredName());
//        }
//        if (!StringUtils.isBlank(order.getInsuredIdNo()) && !order.getInsuredIdNo().contains("*")) {
//            insurance.setInsuredIdNo(order.getInsuredIdNo());
//            log.debug("the insured id NO is different from the auto identity, set the insured id NO to {} for insurance bill", insurance.getInsuredIdNo());
//        } else {
//            insurance.setInsuredIdNo(quoteRecord.getAuto() != null ? quoteRecord.getAuto().getIdentity() : "");
//            log.debug("the insured id NO is same with the auto identity, set the insured id NO to {} for insurance bill", insurance.getInsuredIdNo());
//        }
//
//        if(StringUtils.isNotBlank(order.getApplicantName())) {
//            insurance.setApplicantName(order.getApplicantName());
//        }else {
//            insurance.setApplicantName(insurance.getInsuredName());
//            log.debug("the applicantName is null, from insurance, set the insured name to {} for insurance bill", insurance.getApplicantName());
//        }
//        if(StringUtils.isNotBlank(order.getApplicantIdNo())) {
//            insurance.setApplicantIdNo(order.getApplicantIdNo());
//        }else {
//            insurance.setApplicantIdNo(insurance.getInsuredIdNo());
//            log.debug("the applicantName is null, from insurance, set the insured name to {} for insurance bill", insurance.getInsuredIdNo() );
//        }
        this.saveInsurance(insurance);
        return insurance;
    }

    public void saveInsurance(Insurance insurance) {
        this.insuranceRepository.save(insurance);
    }

    public Insurance generateInsuranceBillByQuote(QuoteRecord quoteRecord) {
        Insurance insurance = new Insurance();
        BeanUtils.copyProperties(quoteRecord, insurance, "id");
        insurance.setQuoteRecord(quoteRecord);
        insurance.setInsurancePackage(quoteRecord.getInsurancePackage());
        return insurance;
    }

    private boolean isCompulsoryInsuranceBillable(QuoteRecord quoteRecord) {
        return quoteRecord.getCompulsoryPremium() > 0 && quoteRecord.getInsurancePackage().isCompulsory();
    }

    private CompulsoryInsurance generateCompulsoryInsurance(QuoteRecord originalQuoteRecord, QuoteRecord quoteRecord, PurchaseOrder order) {
        CompulsoryInsurance compulsoryInsurance = this.generateCompulsoryBillByQuote(quoteRecord);
        if (null != originalQuoteRecord) { //因为京东渠道需要实时同步证件信息，所以暂时这样处理，如果没有旧的交强险保单就从旧的商业险保单里取证件信息
            CompulsoryInsurance oldCompulsoryInsurance = compulsoryInsuranceRepository.findFirstByQuoteRecordOrderByIdDesc(originalQuoteRecord)
            Insurance oldInsurance = insuranceRepository.findFirstByQuoteRecordOrderByIdDesc(originalQuoteRecord)
            compulsoryInsurance.setInsuredName(oldCompulsoryInsurance?.insuredName ?: oldInsurance?.insuredName)
            compulsoryInsurance.setInsuredIdNo(oldCompulsoryInsurance?.insuredIdNo ?: oldInsurance?.insuredIdNo)
            compulsoryInsurance.setInsuredIdentityType(oldCompulsoryInsurance?.insuredIdentityType ?: oldInsurance?.insuredIdentityType)
            compulsoryInsurance.setApplicantName(oldCompulsoryInsurance?.applicantName ?: oldInsurance?.applicantName)
            compulsoryInsurance.setApplicantIdNo(oldCompulsoryInsurance?.applicantIdNo ?: oldInsurance?.applicantIdNo)
            compulsoryInsurance.setApplicantIdentityType(oldCompulsoryInsurance?.applicantIdentityType ?: oldInsurance?.applicantIdentityType)
            compulsoryInsurance.setProposalNo(oldCompulsoryInsurance?.proposalNo)
            compulsoryInsurance.setPolicyNo(oldCompulsoryInsurance?.policyNo)
            compulsoryInsurance.setEffectiveDate(oldCompulsoryInsurance?.effectiveDate)
            compulsoryInsurance.setExpireDate(oldCompulsoryInsurance?.expireDate)
        }

//        if (!StringUtils.isBlank(order.getInsuredName())) {
//            compulsoryInsurance.setInsuredName(order.getInsuredName());
//            log.debug("set the insured name to {} for compulsory insurance bill", compulsoryInsurance.getInsuredName());
//        } else {
//            compulsoryInsurance.setInsuredName(quoteRecord.getAuto() != null ? quoteRecord.getAuto().getOwner() : "");
//            log.debug("the insured name is same with the auto owner, set the insured name to {} for compulsory insurance bill", compulsoryInsurance.getInsuredName());
//        }
//        if (!StringUtils.isBlank(order.getInsuredIdNo()) && !order.getInsuredIdNo().contains("*")) {
//            compulsoryInsurance.setInsuredIdNo(order.getInsuredIdNo());
//            log.debug("set the insured id NO to {} for compulsory insurance bill", compulsoryInsurance.getInsuredIdNo());
//        } else {
//            compulsoryInsurance.setInsuredIdNo(quoteRecord.getAuto() != null ? quoteRecord.getAuto().getIdentity() : "");
//            log.debug("the insured id NO is same with the auto identity, set the insured id NO to {} compulsory for insurance bill", compulsoryInsurance.getInsuredIdNo());
//        }
//        if (StringUtils.isNotBlank(order.getApplicantName())) {
//            compulsoryInsurance.setApplicantName(order.getApplicantName());
//        } else {
//            compulsoryInsurance.setApplicantName(compulsoryInsurance.getInsuredName());
//            log.debug("the applicantName is null, from compulsory insurance, set the Applicant name to {} for compulsory insurance bill", compulsoryInsurance.getApplicantName());
//        }
//        if (StringUtils.isNotBlank(order.getApplicantIdNo())) {
//            compulsoryInsurance.setApplicantIdNo(order.getApplicantIdNo());
//        } else {
//            compulsoryInsurance.setApplicantIdNo(compulsoryInsurance.getInsuredIdNo());
//            log.debug("the ApplicantIdNo is null, from compulsory insurance, set the Applicant Id No to {} for compulsory insurance bill", compulsoryInsurance.getInsuredIdNo());
//        }
        this.saveCompulsoryInsurance(compulsoryInsurance);
        return compulsoryInsurance;
    }

    public CompulsoryInsurance generateCompulsoryBillByQuote(QuoteRecord quoteRecord) {
        CompulsoryInsurance compulsoryInsurance = new CompulsoryInsurance();
        BeanUtils.copyProperties(quoteRecord, compulsoryInsurance, "id");
        compulsoryInsurance.setQuoteRecord(quoteRecord);
        compulsoryInsurance.setInsurancePackage(quoteRecord.getInsurancePackage());
        return compulsoryInsurance;
    }

    public void saveCompulsoryInsurance(CompulsoryInsurance compulsoryInsurance) {
        this.compulsoryInsuranceRepository.save(compulsoryInsurance);
    }
}
