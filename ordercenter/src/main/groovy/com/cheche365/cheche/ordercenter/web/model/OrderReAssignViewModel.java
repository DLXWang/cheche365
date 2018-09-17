package com.cheche365.cheche.ordercenter.web.model;

import java.util.List;

/**
 * Created by wangfei on 2015/5/5.
 */
public class OrderReAssignViewModel {
    private boolean pass;
    private String message;
    private String oldOperatorName;
    private Long oldOperatorId;
    private List newOperatorList;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OrderReAssignViewModel() {
        this(true, "成功");
    }

    public OrderReAssignViewModel(boolean pass, String message){
        this.pass = pass;
        this.message = message;
    }

    public String getOldOperatorName() {
        return oldOperatorName;
    }

    public void setOldOperatorName(String oldOperatorName) {
        this.oldOperatorName = oldOperatorName;
    }

    public Long getOldOperatorId() {
        return oldOperatorId;
    }

    public void setOldOperatorId(Long oldOperatorId) {
        this.oldOperatorId = oldOperatorId;
    }

    public List getNewOperatorList() {
        return newOperatorList;
    }

    public void setNewOperatorList(List newOperatorList) {
        this.newOperatorList = newOperatorList;
    }

    public boolean getPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }
}
