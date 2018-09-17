package com.cheche365.cheche.operationcenter.web.model.partner;

import javax.validation.constraints.NotNull;

/**
 * 触发短信条件
 * Created by lyh on 2015/10/13.
 */
public class ScheduleConditionViewModel {
    @NotNull
    private Long id;
    private String condition;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
