package com.cheche365.cheche.partner.model;

import com.cheche365.cheche.partner.service.PartnerResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by mahong on 2016/2/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaiduResponse implements PartnerResponse {

    private Integer err_no;
    private String err_msg;

    public Integer getErr_no() {
        return err_no;
    }

    public void setErr_no(Integer err_no) {
        this.err_no = err_no;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

    @Override
    public Integer getRespCode() {
        return getErr_no();
    }

    @Override
    public String getRespMsg() {
        return getErr_msg();
    }
}
