package com.cheche365.cheche.ordercenter.constants;

/**
 * Created by xu.yelong on 2016-05-03.
 */
public enum CustomerReAssignMethodEnum {
    ONE_NEW_CUSTOM("1"),
    RANDOM_ALL_CUSTOM("2"),
    RANDOM_ALL_CUSTOM_EXCEPT_ONE("3");

    private String index;

    CustomerReAssignMethodEnum(String index) {
        this.index = index;
    }

    public String getIndex() {
        return this.index;
    }
}
