package com.cheche365.cheche.admin.constants;

/**
 * Created by yang on 2015/12/16.
 */
public enum KeyTypeEnum {
    MOBILE(1),
    LICENSE_PLATE_NO(2),
    ROLE_NAME(3);

    private int index;

    KeyTypeEnum(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }
}
