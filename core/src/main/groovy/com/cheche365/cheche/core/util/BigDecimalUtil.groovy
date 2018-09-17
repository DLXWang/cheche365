package com.cheche365.cheche.core.util

class BigDecimalUtil {
    private static final int DEF_DIV_SCALE = 3

    static BigDecimal bigDecimalValue(Double d) {
        bigDecimalValue(d, DEF_DIV_SCALE)
    }

    static BigDecimal bigDecimalValue(Double d, int scale) {
        return d ? new BigDecimal(d).setScale(scale, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0)
    }

    static BigDecimal subtract(BigDecimal d1, BigDecimal d2) {
        return d1.setScale(DEF_DIV_SCALE, BigDecimal.ROUND_HALF_UP).subtract(d2.setScale(3, BigDecimal.ROUND_HALF_UP)).setScale(3, BigDecimal.ROUND_HALF_UP)
    }

    static BigDecimal subtract(Double d1, Double d2) {
        return subtract(bigDecimalValue(d1), bigDecimalValue(d2))
    }

    static BigDecimal add(BigDecimal d1, BigDecimal d2) {
        return d1.setScale(DEF_DIV_SCALE, BigDecimal.ROUND_HALF_UP).add(d2.setScale(3, BigDecimal.ROUND_HALF_UP)).setScale(3, BigDecimal.ROUND_HALF_UP)
    }

    static BigDecimal add(Double d1, Double d2) {
        return add(bigDecimalValue(d1), bigDecimalValue(d2))
    }

    /**
     * 提供精确的乘法运算。
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1))
        BigDecimal b2 = new BigDecimal(Double.toString(v2))
        return b1.multiply(b2).doubleValue()
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后${DEF_DIV_SCALE}位，以后的数字四舍五入。
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    static double div(Double v1, Double v2) {
        return div(v1, v2, DEF_DIV_SCALE)
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
     *
     * @param v1 除数
     * @param v2 被除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    static double div(Double v1, Double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero")
        }
        if (!v2) {
            throw new IllegalArgumentException("The number be divided can not be null or zero")
        }
        BigDecimal b1 = new BigDecimal(v1 ?: 0d)
        BigDecimal b2 = new BigDecimal(v2)
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue()
    }

    /**
     * 提供精确的小数位四舍五入处理。
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero")
        }
        BigDecimal b = new BigDecimal(Double.toString(v))
        BigDecimal one = new BigDecimal("1")
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue()
    }
}
