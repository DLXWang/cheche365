package com.cheche365.cheche.fanhua.util

import com.cheche365.cheche.core.model.IdentityType
import org.apache.commons.lang.StringUtils

/**
 * Created by zhangtc on 2017/11/30.
 */
class IdentityTypeMappings {

    public static final _IDENTITY_TYPE_MAPPINGS = [
        '01' : [
            kindName : '身份证',
            localCode: IdentityType.Enum.IDENTITYCARD
        ],
        '02' : [
            kindName : '户口本',
            localCode: IdentityType.Enum.RESIDENCE_BOOKLET
        ],
        '03' : [
            kindName : '驾照',
            localCode: IdentityType.Enum.IDENTITYCARD
        ],
        '04' : [
            kindName : '军官证/士兵证',
            localCode: IdentityType.Enum.OFFICERARD
        ],
        '07' : [
            kindName : '护照',
            localCode: IdentityType.Enum.PASSPORT
        ],
        '11' : [
            kindName : '港澳回乡证/台胞证',
            localCode: IdentityType.Enum.MTP
        ],
        '71' : [
            kindName : '组织代码证',
            localCode: IdentityType.Enum.ORGANIZATION_CODE
        ],
        '99' : [
            kindName : '其他证件',
            localCode: IdentityType.Enum.OTHER_IDENTIFICATION
        ],
        '74' : [
            kindName : '社会信用代码证',
            localCode: IdentityType.Enum.UNIFIED_SOCIAL_CREDIT_CODE
        ],
        '72' : [
            kindName : '税务登记证',
            localCode: IdentityType.Enum.BUSINESS_REGISTRATION_NUMBER
        ],
        '73': [
            kindName : '营业执照',
            localCode: IdentityType.Enum.BUSINESS_LICENSE
        ]
    ]

    static IdentityType getLocal(String certKind) {
        if (StringUtils.isBlank(certKind) || _IDENTITY_TYPE_MAPPINGS[certKind] == null) {
            return _IDENTITY_TYPE_MAPPINGS['99'].localCode
        }
        return _IDENTITY_TYPE_MAPPINGS[certKind].localCode
    }

}
