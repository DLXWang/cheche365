package com.cheche365.cheche.core.serializer.converter

import com.cheche365.cheche.common.util.DoubleUtils

/**
 * Created by zhengwei on 4/28/16.
 */
class ArrayBillsGenerator extends ArrayFieldsGenerator {

    private Double amount = 0.0;

    @Override
    protected void doBeforeMerge() {
    }

    @Override
    protected void doAfterMerge(List<Field> fields) {

        this.setAmount(0.0);
        for(String group : this.total.keySet()){

            this.setAmount(DoubleUtils.displayDoubleValue(this.getAmount() + (this.getTotal().get(group) as Double)));

            this.getTotal().put(group, prettyPrintDouble(this.getTotal().get(group) as Double))

        }

    }

    Double getAmount() {
        return amount;
    }

    void setAmount(Double amount) {
        this.amount = amount;
    }
}
