package com.cheche365.cheche.core.service.callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CallBackProcessorChain implements CallBackProcessor {

    List<CallBackProcessor> processors = new ArrayList<CallBackProcessor>();

    boolean ifContinue = true;

    public CallBackProcessorChain addProcessor(CallBackProcessor p){
        this.processors.add(p);
        return this;
    }

    @Override
    public boolean doProcessor(String outTradeNo,Map<String, String> params) {
        for(CallBackProcessor p : processors){
            if(ifContinue){
                ifContinue = p.doProcessor(outTradeNo,params);
            }
        }
        return ifContinue;
    }
}
