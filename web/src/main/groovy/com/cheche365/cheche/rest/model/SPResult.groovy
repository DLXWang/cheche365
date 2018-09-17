package com.cheche365.cheche.rest.model

import groovy.transform.Canonical

/**
 * Created by zhengwei on 5/25/15.
 * 异步报价响应格式
 */
@Canonical()
class SPResult {

    String id
    Object quoteFlag
    String channel
    String status
    Object data
    String message
    Boolean isSelf

    static class Channel {
        public static final String RESULT = "quote.result"
        public static final String MARKETING = "quote.marketing"
        public static final String STAGE = "quote.stage"
        public static final String METAINFO = "quote.metaInfo"
        public static final String FINISH = "quote.finish"
        public static final String ALL_FINISH = "quote.all.finish"
    }

    static class Status {
        public static final String SUCCESS = "success"
        public static final String FAIL = "fail"
    }

}
