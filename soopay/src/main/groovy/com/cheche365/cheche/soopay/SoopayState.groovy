package com.cheche365.cheche.soopay;



/**
 * Created by mjg on 2017/6/17.
 */
public enum SoopayState {
    SUCCESS("0000"),//成功
    INNER_ERROR("00060999"),//系统错误
    VALID_SIGN_FAIL("00060710"),//验签失败
    ORDER_TIMEOUT("00200079"),//订单已过期请重新下单
    ORDER_PAYING("00080730");//订单正在支付，不能重复发起

    private final String value;

    SoopayState(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static List<SoopayState> paySuccess() {
//        return Arrays.asList(new SoopayState[]{SUCCESS});
        [SUCCESS]
    }

    public static List<SoopayState> unknownTrade() {
//        return Arrays.asList(new SoopayState[]{ORDER_PAYING});
        [ORDER_PAYING]
    }

    public static boolean isPaySuccess(String respCode) {
        for (SoopayState state : paySuccess()) {
            if (state.value.equals(respCode)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isUnknown(String respCode) {
        for (SoopayState state : unknownTrade()) {
            if (state.value.equals(respCode)) {
                return true;
            }
        }
        return false;
    }
}
