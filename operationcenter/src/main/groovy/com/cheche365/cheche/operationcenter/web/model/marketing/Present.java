package com.cheche365.cheche.operationcenter.web.model.marketing;

/**
 * Created by chenxiangyin on 2017/5/16.
 */

public class Present{
    Double full;
    Double discount;
    Long present;
    public Double getDiscount() {  return discount;  }

    public void setDiscount(Double discount) { this.discount = discount; }

    public Long getPresent() {  return present; }

    public void setPresent(Long present) { this.present = present; }

    public Double getFull() {  return full; }

    public void setFull(Double full) {  this.full = full;  }

    public static Boolean checkEmpty(Present present){
        if(present.getFull() == null || present.getDiscount() == null || present.getPresent() == null){
            return true;
        }
        return false;
    }
}
