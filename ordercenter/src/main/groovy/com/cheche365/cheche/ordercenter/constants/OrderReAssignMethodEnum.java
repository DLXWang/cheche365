package com.cheche365.cheche.ordercenter.constants;

/**
 * Created by yang on 2015/12/21.
 */
public enum OrderReAssignMethodEnum {
    ONE_NEW_CUSTOM("1"),
    RANDOM_ALL_CUSTOM("2"),
    RANDOM_ALL_CUSTOM_EXCEPT_ONE("3");

    private String index;

    OrderReAssignMethodEnum(String index) {
        this.index = index;
    }

    public String getIndex() {
        return this.index;
    }
}
