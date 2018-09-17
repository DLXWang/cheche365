package com.cheche365.cheche.core.serializer.converter;

import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.InsuranceBills;
import com.cheche365.cheche.core.model.InsuranceCompany;
import com.cheche365.cheche.core.model.InsurancePackage;
import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer
import com.cheche365.cheche.core.util.BeanUtil
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.text.SimpleDateFormat;

import java.util.*;

/**
 * Created by zhengwei on 4/28/16.
 */
public class ArrayInsuranceBills implements ModelViewConverter<InsuranceBills, ArrayInsuranceBills> {

    //通过格式转换生成的fields
    private List<Field> fields;
    private Map total;
    private Double amount;
    public static final List<String> CALCULATE_COPY_FIELDS = ["fields", "total", "amount"]

    //从其他bean copy过来的bean
    private InsuranceCompany insuranceCompany;
    private InsurancePackage insurancePackage;
    private Auto auto;
    public static final List<String> BILL_BEAN_COPY_FIELDS = ["insuranceCompany", "insurancePackage", "auto"]

    //从其他bean copy过来的map
    private Map<String, Object> insuredPerson = new HashMap<>();
    private Map<String, Object> insurance
    private Map<String, Object> compulsoryInsurance;
    public static final List<String> BILL_MAP_COPY_FIELDS = ["proposalNo", "policyNo", "effectiveDate", "expireDate", "valid"]
    public static final List<String> INSURED_MAP_COPY_FIELDS = ["insuredIdNo", "insuredName", "insuredIdentityType"]

    //新增字段
    private static final String HAS_EXPIRED = "已过期";
    private static final String HAS_EFFECTIVE = "已生效";
    private static final String NOT_EFFECTIVE = "未生效";
    private String status = "";
    private String orderNo;
    private String specialAgreement;

    static final Closure INSURANCE_STATUS = {desc ->
        [
            id          : INSURANCE_STATUS_MAPPING.get(desc) ?: 4L,
            description : desc
        ]
    }

    static final INSURANCE_STATUS_MAPPING = [
        (ArrayInsuranceBills.HAS_EXPIRED)    : 1L,
        (ArrayInsuranceBills.HAS_EFFECTIVE)  : 2L,
        (ArrayInsuranceBills.NOT_EFFECTIVE)  : 3L
    ]

    @Override
    public ArrayInsuranceBills convert(InsuranceBills source) {

        this.setOrderNo(source.getOrderNo());
        if (source.getInsurance()==null && source.getCi()==null) {
            return this;
        }

        ArrayBillsGenerator arrayGenerator = new ArrayBillsGenerator();
        arrayGenerator.toArray(source.getInsurance(), source.getCi());
        BeanUtil.copyBeanProperties(arrayGenerator, this, CALCULATE_COPY_FIELDS);

        //下面两个if顺序不要换，如果同时投保商业险和交强险，用商业险的值覆盖交强险的同名值
        this.setStatus(NOT_EFFECTIVE);

        if(null != source.getCi()){
            this.compulsoryInsurance = new HashMap<>();
            BeanUtil.copyBeanToMap(source.getCi(), this.compulsoryInsurance, BILL_MAP_COPY_FIELDS);
            BeanUtil.copyBeanProperties(source.getCi(), this, BILL_BEAN_COPY_FIELDS);
            BeanUtil.copyBeanToMap(source.getCi(), this.insuredPerson, INSURED_MAP_COPY_FIELDS);
            this.compulsoryInsurance.put("status",INSURANCE_STATUS(validDate(source.getCi().getEffectiveDate(), source.getCi().getExpireDate())));
            this.setStatus(validDate(source.getCi().getEffectiveDate(), source.getCi().getExpireDate()));
        }

        if(null != source.getInsurance()){
            this.insurance = new HashMap<>();
            BeanUtil.copyBeanToMap(source.getInsurance(), this.insurance, BILL_MAP_COPY_FIELDS);
            BeanUtil.copyBeanProperties(source.getInsurance(), this, BILL_BEAN_COPY_FIELDS);
            BeanUtil.copyBeanToMap(source.getInsurance(), this.insuredPerson, INSURED_MAP_COPY_FIELDS);
            this.insurance.put("status",INSURANCE_STATUS(validDate(source.getInsurance().getEffectiveDate(), source.getInsurance().getExpireDate())));
            this.setStatus(validDate(source.getInsurance().getEffectiveDate(), source.getInsurance().getExpireDate()));
            this.setSpecialAgreement(source.getInsurance().getSpecialAgreement());
        }

        return this;
    }

    @Override
    public Set<String> filterFields() {
        return new HashSet(Arrays.asList("glass,id,uniqueString,inforArrayList".split(",")));
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Map getTotal() {
        return total;
    }

    public void setTotal(Map total) {
        this.total = total;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Map<String, Object> getInsurance() {
        return insurance;
    }

    public Map<String, Object> getCompulsoryInsurance() {
        return compulsoryInsurance;
    }

    public InsuranceCompany getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(InsuranceCompany insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public InsurancePackage getInsurancePackage() {
        return insurancePackage;
    }

    public void setInsurancePackage(InsurancePackage insurancePackage) {
        this.insurancePackage = insurancePackage;
    }

    public Auto getAuto() {
        return auto;
    }

    public void setAuto(Auto auto) {
        this.auto = auto;
    }

    public Map getInsuredPerson() {
        return insuredPerson;
    }

    private String validDate(Date effectiveDate, Date expireDate) {
        if (effectiveDate == null || expireDate == null) {
            return NOT_EFFECTIVE;
        }

        Date currentDate = new Date();
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        Calendar currentCalendar = Calendar.getInstance();
        startCalendar.setTime(effectiveDate);
        endCalendar.setTime(expireDate);
        currentCalendar.setTime(currentDate);
        if (currentCalendar.compareTo(startCalendar) >= 0 && currentCalendar.compareTo(endCalendar) <= 0) {
            return HAS_EFFECTIVE;
        } else if (currentCalendar.compareTo(endCalendar) > 0) {
            return HAS_EXPIRED;
        } else if (currentCalendar.compareTo(startCalendar) < 0) {
            return new SimpleDateFormat("yyyy-MM-dd生效").format(effectiveDate).toString();
        }
        return NOT_EFFECTIVE;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setSpecialAgreement(String specialAgreement) {
        this.specialAgreement = specialAgreement;
    }

    public String getSpecialAgreement() {
        return specialAgreement;
    }

    public boolean isValid() {
        return false;
    }  //貌似没用字段，等跟前端确认了就干掉
}
