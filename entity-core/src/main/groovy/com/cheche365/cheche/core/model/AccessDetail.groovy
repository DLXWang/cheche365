package com.cheche365.cheche.core.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.Canonical

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Canonical
class AccessDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id
    String source //百度、360、搜狗
    String referer
    String mobile
    String licensePlateNo
    Date createTime

    public static final Map SOURCE_MAP = [
        SOURCE_BAIDU : [
            id : 1,
            url: 'baidu.com'
        ],
        SOURCE_360   : [
            id : 2,
            url: 'so.com'
        ],
        SOURCE_SOUGOU: [
            id : 3,
            url: 'sogou.com'
        ],
        SOURCE_SHENMA: [
            id : 4,
            url: 'm.sm.cn'
        ],
        SOURCE_GOOGLE: [
            id : 5,
            url: 'google.com'
        ],
        SOURCE_BING  : [
            id : 6,
            url: 'bing.com'
        ],
        SOURCE_YAHOO : [
            id : 7,
            url: 'yahoo.com'
        ],
        SOURCE_SOSO  : [
            id : 8,
            url: 'soso.com'
        ]
    ].asImmutable()
}
