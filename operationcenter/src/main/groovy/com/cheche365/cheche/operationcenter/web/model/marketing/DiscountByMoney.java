package com.cheche365.cheche.operationcenter.web.model.marketing;

/**
 * Created by chenxiangyin on 2017/5/16.
 */
public class DiscountByMoney{
    Double full;
    Double discount;
    public Double getFull() { return full; }

    public void setFull(Double full) { this.full = full; }

    public Double getDiscount() { return discount; }

    public void setDiscount(Double discount) { this.discount = discount; }

    public static Boolean checkEmpty(DiscountByMoney discountByMoney){
        if(discountByMoney.getFull() == null || discountByMoney.getDiscount() == null){
            return true;
        }
        return false;
    }
}
