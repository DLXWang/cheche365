package com.cheche365.cheche.fanhua.web.model.insurance

import com.cheche365.cheche.fanhua.annotation.Essential
import com.cheche365.cheche.fanhua.annotation.EssentialHandler

/**
 * Created by zhangtc on 2017/11/30.
 */
class RecordInsuranceCoverViewModel {
    @Essential
    private List<RecordInsuranceViewModel> res
    @Essential
    private String cnt
    @Essential
    private String sign
    @Essential
    private String random

    List<RecordInsuranceViewModel> getRes() {
        return res
    }

    void setRes(List<RecordInsuranceViewModel> res) {
        this.res = res
    }

    String getCnt() {
        return cnt
    }

    void setCnt(String cnt) {
        this.cnt = cnt
    }

    String getSign() {
        return sign
    }

    void setSign(String sign) {
        this.sign = sign
    }

    String getRandom() {
        return random
    }

    void setRandom(String random) {
        this.random = random
    }
    Tuple2 checkParam(){
        EssentialHandler.checkParam(this)
    }
}
