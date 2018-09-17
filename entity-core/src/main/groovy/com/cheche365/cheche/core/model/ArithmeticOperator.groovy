package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.util.RuntimeUtil

import javax.persistence.Entity

@Entity
class ArithmeticOperator extends AutoLoadEnum {


    static class Enum{
        public static ArithmeticOperator ADD_1, SUB_2, MUL_3, DIV;

        static {
           RuntimeUtil.loadEnum('arithmeticOperatorRepository', ArithmeticOperator, Enum)
        }
    }
}
