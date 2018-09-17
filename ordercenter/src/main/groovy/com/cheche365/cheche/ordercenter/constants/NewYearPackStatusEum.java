package com.cheche365.cheche.ordercenter.constants;

/**
 * Created by yang on 2016/1/5.
 */
public enum NewYearPackStatusEum {
    CREATE_STATUS(1, "新建"),
    PAID_STATUS(2, "已支付"),
    USED_STATUS(3, "已使用"),
    CANCEL_STATUS(4, "已取消");

    private int index;
    private String content;

    NewYearPackStatusEum(int index, String content) {
        this.index = index;
        this.content = content;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
