package com.cheche365.cheche.core.serializer.converter;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer;
import com.cheche365.cheche.core.util.BeanUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.*;

import static com.cheche365.cheche.core.serializer.converter.ArrayFieldsGenerator.GroupPolicy.Three;
import static com.cheche365.cheche.core.serializer.converter.ArrayFieldsGenerator.GroupPolicy.Two;

/**
 * Created by zhengwei on 4/25/16.
 */
public class ArrayQuoteRecord implements ModelViewConverter<QuoteRecord, ArrayQuoteRecord> {

    final Set<String> FILTER_FIELDS = new HashSet(Arrays.asList("quote,applicant,auto,type,updateTime,uniqueString,compulsoryOriginalPolicyNo,originalPolicyNo,createTime,message,compulsoryEffectiveDate,expireDate,compulsoryExpireDate".split(",")));
    final List<String> COPY_CALCULATE_FIELDS = Arrays.asList(new String[]{"fields", "total","discounts"});

    private List<Field> fields;
    private Map total;

    private List<Map<String,Object>> discounts;

    private QuoteRecord quoteRecord;

    public ArrayQuoteRecord convert(QuoteRecord quoteRecord) {

        this.quoteRecord = quoteRecord;

        ArrayQRGenerator generator = new ArrayQRGenerator();
        generator.toArray(this.quoteRecord, null != quoteRecord.getChannel() && quoteRecord.getChannel().isPartnerAPIChannel() ? Three : Two);
        generator.setDiscounts(this.quoteRecord.getDiscounts());
        generator.setQuoteSource(null == quoteRecord.getType() ? null : quoteRecord.getType().getId());
        BeanUtil.copyBeanProperties(generator, this, COPY_CALCULATE_FIELDS);
        return this;
    }

    public List<Map<String, Object>> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<Map<String, Object>> discounts) {
        this.discounts = discounts;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Set<String> filterFields() {
        return FILTER_FIELDS;
    }

    public Object getInsuranceCompany() {
        return this.quoteRecord.getInsuranceCompany();
    }

    public Area getArea() {
        return this.quoteRecord.getArea();
    }

    public Object getInsurancePackage() {
        return this.quoteRecord.getInsurancePackage();
    }

    public List<QuoteFieldStatus> getQuoteFieldStatus() {
        return this.quoteRecord.getQuoteFieldStatus();
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    public Double getPaidAmount() {
        return this.quoteRecord.getPaidAmount();
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    public Double getTotalPremium() {
        return this.quoteRecord.getTotalPremium();
    }

    public Map getTotal() {
        return total;
    }

    public void setTotal(Map total) {
        this.total = total;
    }

    public int getQuotedFieldsNum() {
        return this.quoteRecord.getQuotedFieldsNum();
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    public Double getPremium() {
        return this.quoteRecord.getPremium();
    }

    public String getQuoteRecordKey() {
        return this.quoteRecord.getQuoteRecordKey();
    }

    public Object getAuto() {
        return this.quoteRecord.getAuto();
    }

    public Map getAnnotations() {
        return this.quoteRecord.getAnnotations();
    }

    public Long getId() {
        return this.quoteRecord.getId();
    }

    public String getOwnerMobile() {
        return this.quoteRecord.getOwnerMobile();
    }

    public Date getEffectiveDate() {
        return this.quoteRecord.getEffectiveDate();
    }

    public Date getCompulsoryEffectiveDate() {
        return this.quoteRecord.getCompulsoryEffectiveDate();
    }

    public Date getExpireDate() {
        return this.quoteRecord.getExpireDate();
    }

    public Date getCompulsoryExpireDate() {
        return this.quoteRecord.getCompulsoryExpireDate();
    }

    public Double getDiscount() {
        return this.quoteRecord.getDiscount();
    }

}
