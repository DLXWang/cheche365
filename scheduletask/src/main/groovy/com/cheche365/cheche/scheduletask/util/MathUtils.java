package com.cheche365.cheche.scheduletask.util;

import java.math.BigDecimal;

/**
 * Created by guoweifu on 2015/11/30.
 */
public class MathUtils {

    /**
     * double类型数字相加
     *
     * @param v1
     * @param v2
     * @return
     */
    public static Double add(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return new Double(b1.add(b2).doubleValue());
    }

    /**
     * double类型数字相加除
     *
     * @param v1
     * @param v2
     * @param scale（保留几位）
     * @return
     */
    public static Double div(Double v1, Double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return new Double(b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue());
    }

    /**
     * 两数相乘
     *
     * @param v1
     * @param v2
     * @return
     */
    public static Double mul(Double v1, Double v2, int scale) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return new Double(b1.multiply(b2).setScale(scale).doubleValue());
    }
}
