package com.cheche365.cheche.operationcenter.web.model.tide

import com.fasterxml.jackson.annotation.JsonFormat

class LogViewModel {
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime
    String name
    String mess
}
