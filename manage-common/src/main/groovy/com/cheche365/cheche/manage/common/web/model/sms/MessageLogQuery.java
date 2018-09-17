package com.cheche365.cheche.manage.common.web.model.sms;

import com.cheche365.cheche.manage.common.model.PublicQuery;

/**
 * Created by yinJianBin on 2017/10/19.
 */
public class MessageLogQuery extends PublicQuery {
    Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
