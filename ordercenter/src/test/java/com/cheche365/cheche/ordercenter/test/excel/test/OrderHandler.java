package com.cheche365.cheche.ordercenter.test.excel.test;

import com.cheche365.cheche.common.excel.handler.ExcelDataHandlerDefaultImpl;

/**
 * Created by xu.yelong on 2016/1/8.
 */
public class OrderHandler extends ExcelDataHandlerDefaultImpl{
    public Object exportHandler(Object obj, String name, Object value) {
        if(name.equals("应付金额")||name.equals("实付金额")){
            return String.valueOf(value)+"元";
        }
        return null;
    }

    public void setNeedHandlerFields(String[] needHandlerFields) {
        super.setNeedHandlerFields(needHandlerFields);
    }
}
