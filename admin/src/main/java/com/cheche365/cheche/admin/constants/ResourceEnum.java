package com.cheche365.cheche.admin.constants;

/**
 * Created by yang on 2015/12/16.
 */
public enum ResourceEnum {
    FirstLevel(1),
    SecondLevel(2),
    ThirdLevel(3);

    private int index;

    ResourceEnum(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }
}
