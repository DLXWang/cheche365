package com.cheche365.cheche.ordercenter.web.model;

import java.util.List;

/**
 * Created by xu.yelong on 2016-04-29.
 */
public class CustomerReAssignViewModel {
    private boolean pass;
    private String message;
    private String oldOperatorName;
    private Long oldOperatorId;
    private List newOperatorList;
    private Integer count;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CustomerReAssignViewModel() {
        this(true, "成功");
    }

    public CustomerReAssignViewModel(boolean pass, String message){
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
