package com.cheche365.cheche.core.serializer.converter

import com.cheche365.cheche.core.model.QuoteRecord
import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 4/25/16.
 */

@Service
class ArrayQRGenerator extends ArrayFieldsGenerator {

    @Override
    protected void doBeforeMerge() {
    }

    @Override
    protected void doAfterMerge(List<Field> fields) {

        for(String group : this.total.keySet()){
            this.getTotal().put(group, prettyPrintDouble(this.getTotal().get(group) as Double))
        }
    }


    @Override
    protected boolean emptyValue(Object bill, String fieldName, Double premium) {
        return super.emptyValue(bill, fieldName, premium) || (fieldName.equals("autoTax") && ((QuoteRecord)bill).autoTaxFree());
    }

}
