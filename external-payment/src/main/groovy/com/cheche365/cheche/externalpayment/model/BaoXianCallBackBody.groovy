package com.cheche365.cheche.externalpayment.model

/**
 * Created by zhengwei on 4/14/17.
 * 泛华回调参数对象
 */
class BaoXianCallBackBody {

    Map original
    public BaoXianCallBackBody(Map original){
        this.original = original
    }

    def methodMissing(String name, args){

    }
}
