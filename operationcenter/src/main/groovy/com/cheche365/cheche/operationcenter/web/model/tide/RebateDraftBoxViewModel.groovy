package com.cheche365.cheche.operationcenter.web.model.tide

import com.fasterxml.jackson.annotation.JsonFormat
import groovy.transform.ToString

@ToString
class RebateDraftBoxViewModel {
    Long id
    String name
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime
    List<RebateViewModel> contractRebateList
}
