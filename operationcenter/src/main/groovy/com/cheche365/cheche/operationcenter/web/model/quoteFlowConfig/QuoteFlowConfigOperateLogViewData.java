package com.cheche365.cheche.operationcenter.web.model.quoteFlowConfig;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.QuoteFlowConfig;
import com.cheche365.cheche.manage.common.model.QuoteFlowConfigOperateLog;

/**
 * Created by chenxiangyin on 2017/7/13.
 */
public class QuoteFlowConfigOperateLogViewData {
    private String operator;
    private String operateInfo;
    private String comment;
    private String operateTime;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperateInfo() {
        return operateInfo;
    }

    public void setOperateInfo(String operateInfo) {
        this.operateInfo = operateInfo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public static QuoteFlowConfigOperateLogViewData createViewData(QuoteFlowConfigOperateLog operateLog){
        QuoteFlowConfigOperateLogViewData data = new QuoteFlowConfigOperateLogViewData();
        data.setComment(operateLog.getComment());
        data.setOperator(operateLog.getOperator().getName());
        String operateInfo = "";
        if(operateLog.getOperationType() == QuoteFlowConfigOperateLog.OperationType.ON_OOF_LINE.getIndex()){       // 0 上线下线 1 换接入方式 3 覆盖
            operateInfo = "上下线改为：" + QuoteFlowConfigOperateLog.OperationValue.getName(operateLog.getOperationValue());
        }else if(operateLog.getOperationType() == QuoteFlowConfigOperateLog.OperationType.ACCESS_MODE.getIndex()){
            operateInfo = "报价方式改为：" + QuoteFlowConfig.ConfigValue.getName(operateLog.getOperationValue());
        }else{
            operateInfo = "覆盖";
        }
        data.setOperateInfo(operateInfo);
        data.setOperateTime(DateUtils.getDateString(operateLog.getExecutionTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        return data;
    }
}
