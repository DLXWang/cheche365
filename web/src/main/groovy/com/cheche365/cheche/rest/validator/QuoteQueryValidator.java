package com.cheche365.cheche.rest.validator;

import com.cheche365.cheche.core.constants.ModelConstants;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.InsurancePackage;
import com.cheche365.cheche.rest.exception.FieldsNotValidationException;
import com.cheche365.cheche.rest.model.QuoteQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;

/**
 * Created by zhengwei on 4/8/15.
 * 校验报价请求
 */
public class QuoteQueryValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return QuoteQuery.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {

        if (errors.hasErrors()) {  //do the basic validation
            throw new FieldsNotValidationException(errors);
        }

        QuoteQuery target = (QuoteQuery) obj;
        Auto auto = target.getAuto();
        Boolean reInsurance = (target.getPref() != null && target.getPref().getFlowType() != null
            && ModelConstants._FLOW_TYPE_RENEWAL_CHANNEL.toString().equals(target.getPref().getFlowType().toString())) ? true : false;

        if (auto != null) {
            validateAuto(target, errors, reInsurance);
        }
    }

    public static void validateQuoteRequest(QuoteQuery query, BindingResult bindingResult, Boolean allowPkgNull) {
        QuoteQueryValidator validator = new QuoteQueryValidator();

        if (!allowPkgNull && query.getInsurancePackage() == null) {
            bindingResult.rejectValue("insurancePackage", "报价套餐不能为空");
        }

        validator.validate(query, bindingResult);

        if (bindingResult.hasFieldErrors("auto")) {
            throw new FieldsNotValidationException(BusinessException.Code.INPUT_FIELD_NOT_VALID, bindingResult);
        }

        if (bindingResult.hasErrors()) {
            throw new FieldsNotValidationException(bindingResult);
        }
    }

    public static void doQuoteRecordValidation(InsurancePackage insurancePackage, BindingResult bindingResult) {
        QuoteQuery query = new QuoteQuery();
        query.setInsurancePackage(insurancePackage);
        QuoteQueryValidator.validateQuoteRequest(query, bindingResult, false);
    }

    private void validateAuto(QuoteQuery query, Errors errors, Boolean reInsurance) {
        Auto auto = query.getAuto();
        if (!query.isNewCarFlag()) {
            if (StringUtils.isBlank(auto.getLicensePlateNo())) {
                errors.rejectValue("auto", "车牌号不能为空");
            } else {
                auto.setLicensePlateNo(auto.getLicensePlateNo().trim());
            }
        }

        if (StringUtils.isBlank(auto.getOwner())) {
            errors.rejectValue("auto", "车主姓名不能为空");
        } else {
            auto.setOwner(auto.getOwner().trim());
        }

        if (StringUtils.isBlank(auto.getIdentity())) {
            errors.rejectValue("auto", "证件号码不能为空");
        } else {
            auto.setIdentity(auto.getIdentity().trim());
        }

        if (reInsurance) {
            return;
        }

        if (StringUtils.isBlank(auto.getEngineNo())) {
            errors.rejectValue("auto", "发动机号不能为空");
        } else {
            auto.setEngineNo(auto.getEngineNo().trim().toUpperCase(Locale.US));
        }

        if (StringUtils.isBlank(auto.getVinNo())) {
            errors.rejectValue("auto", "车架号不能为空");
        } else {
            auto.setVinNo(auto.getVinNo().trim().toUpperCase(Locale.US));
        }
    }

}
