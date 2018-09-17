package com.cheche365.cheche.ordercenter.web.model.insurance;
import java.util.List;

/**
 * 财务对账查询子列表
 * Created by cxy on 2017/12/15.
 */
public class OfflineInsuranceSubListModel {
    private Integer countNum = 0;
    private Double allRebate = 0.0;
    private List<SubListModel> subList;
    public Integer getCountNum() {
        return countNum;
    }

    public void setCountNum(Integer countNum) {
        this.countNum = countNum;
    }

    public Double getAllRebate() {
        return allRebate;
    }

    public void setAllRebate(Double allRebate) {
        this.allRebate = allRebate;
    }

    public List<SubListModel> getSubList() {
        return subList;
    }

    public void setSubList(List<SubListModel> subList) {
        this.subList = subList;
    }
}
