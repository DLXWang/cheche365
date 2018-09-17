package com.cheche365.cheche.core.service.giftcode.convertor;

import com.cheche365.cheche.core.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahong on 2015/7/9.
 */
public class GiftAmountConvertor {

    public static List<Double> getGiftAmounts(String amountParam) {
        if (amountParam == null || amountParam.isEmpty()) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "优惠码可兑换金额配置项不能为空");
        }

        String[] amounts = amountParam.split(";");
        List<Double> amountList = new ArrayList<>(amounts.length);
        for (int i = 0; i < amounts.length; i++) {
            amountList.add(Double.valueOf(amounts[i]));
        }
        return amountList;
    }
}
