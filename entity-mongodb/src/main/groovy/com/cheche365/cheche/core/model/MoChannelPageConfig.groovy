package com.cheche365.cheche.core.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * 渠道前端参数配置
 * Created by liheng on 2018/3/7 007.
 */
@Document(collection = 'channel_page_config')
class MoChannelPageConfig {

    @Id
    String id
    Long channelId
    Map config
}
