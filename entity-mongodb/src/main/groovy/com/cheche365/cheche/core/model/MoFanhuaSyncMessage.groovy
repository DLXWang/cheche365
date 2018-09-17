package com.cheche365.cheche.core.model

import com.cheche365.cheche.common.util.DateUtils
import org.springframework.data.annotation.Id

import static com.cheche365.cheche.common.util.DateUtils.DATE_LONGTIME24_PATTERN

/**
 * Created by zhangtc on 2017/12/4.
 */
public class MoFanhuaSyncMessage {

    @Id
    private String id
    private Date createTime
    private Integer messageType
    private String content

    public static class Enum {

        public static Integer NEW = 1//messageType待处理
        public static Integer SUCCESS = 2//messageType成功
        public static Integer FAILED = 3//messageType失败
    }

    String getId() {
        return id
    }

    void setId(String id) {
        this.id = id
    }

    Date getCreateTime() {
        return createTime
    }

    void setCreateTime(Date createTime) {
        this.createTime = createTime
    }

    Integer getMessageType() {
        return messageType
    }

    void setMessageType(Integer messageType) {
        this.messageType = messageType
    }

    String getContent() {
        return content
    }

    void setContent(String content) {
        this.content = content
    }

    Map failedMap(String errorMsg) {
        [
            id        : getId(),
            createTime: DateUtils.getDateString(getCreateTime(), DATE_LONGTIME24_PATTERN),
            content   : errorMsg
        ]
    }
}
