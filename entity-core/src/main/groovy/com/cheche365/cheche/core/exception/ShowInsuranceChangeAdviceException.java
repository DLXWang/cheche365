package com.cheche365.cheche.core.exception;

/**
 * 该异常用于在所选套餐无法报价时,展示保险公司官网反馈的套餐修改建议
 * Created by houjinxin on 16/1/7.
 */
public class ShowInsuranceChangeAdviceException extends NonFatalBusinessException  {

    public ShowInsuranceChangeAdviceException(String msg, Object errorObject) {
        super(Code.SHOW_INSURANCE_CHANGE_ADVICE, msg, errorObject);
    }
}
