package com.cheche365.cheche.core.service.callback;

import java.util.Map;

public interface CallBackProcessor {

    public boolean doProcessor(String outTradeNo, Map<String, String> params);
}
