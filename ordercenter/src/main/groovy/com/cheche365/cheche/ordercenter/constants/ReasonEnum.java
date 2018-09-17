package com.cheche365.cheche.ordercenter.constants;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by sunhuazhong on 2015/11/16.
 */
public enum ReasonEnum {
    INFO_ERROR(1, "信息有误"),
    UNDERWRITING_NO_PASS(2, "核保未通过"),
    NO_INSTITUTION(3, "无人接单"),
    NO_INSURANCE(4, "无法出单"),
    QUOTE_AUDIT_NO_PASS(5, "报价审核未通过"),
    OTHER(6, "其他");

    private int index;
    private String content;

    private ReasonEnum(int index, String content) {
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

    public static ReasonEnum format(int index) {
        for(ReasonEnum reasonEnum : ReasonEnum.values()) {
            if(reasonEnum.getIndex() == index) {
                return reasonEnum;
            }
        }
        return null;
    }

    public static ReasonEnum formatByReason(String reason) {
        for(ReasonEnum reasonEnum : ReasonEnum.values()) {
            if(reasonEnum.getContent().equals(StringUtils.trimToEmpty(reason))) {
                return reasonEnum;
            }
        }
        return null;
    }
}
