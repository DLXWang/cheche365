package com.cheche365.cheche.core.service.giftcode.convertor;

import com.cheche365.cheche.core.exception.BusinessException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mahong on 2015/7/9.
 */
public class FullLimitAmountConvertor {

    public static Map<Double, Double> getGiftFullLimits(String fullLimitParam) {
        if (fullLimitParam == null || fullLimitParam.isEmpty()) {
            return null;
        }

        String[] fullLimits = fullLimitParam.split(";");
        Map<Double, Double> fullLimitMap = new HashMap<>(fullLimits.length);
        for (int i = 0; i < fullLimits.length; i++) {
            String[] fullLimit = fullLimits[i].split("_");
            if (fullLimit.length != 2) {
                throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "优惠码满减金额配置项格式错误");
            }
            fullLimitMap.put(Double.valueOf(fullLimit[0]), Double.valueOf(fullLimit[1]));
        }
        return fullLimitMap;
    }
}
