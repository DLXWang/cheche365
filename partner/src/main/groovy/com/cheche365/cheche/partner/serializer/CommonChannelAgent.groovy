package com.cheche365.cheche.partner.serializer

import com.cheche365.cheche.core.serializer.ModelFieldsCopier

/**
 * Created by liheng on 2018/7/12 0012.
 */
class CommonChannelAgent extends ModelFieldsCopier {

    CommonChannelAgent generateChannelAgent(apiInput) {
        copyFields apiInput
        this
    }

    @Override
    def fieldsMapping() {
        [
            [
                sourcePath: ['channelAgent'],
                targetPath: 'data',
                fields    : ['id', 'user.mobile->mobile', 'user.name->name', 'user.identity->identity', 'user.email->email', 'inviteCode', 'shopType.id->shopType', 'shop', 'disable', 'parent.id->parentId']
            ],
            [
                sourcePath: ['channelAgent.agentLevel'],
                targetPath: 'data.agentLevel',
                fields    : ['id', 'description']
            ],
            [
                sourcePath: ['headers'],
                targetPath: 'metaInfo',
                fields    : ['entityChangeType->dataType']
            ],
            [
                sourcePath: ['partnerUser'],
                targetPath: 'data',
                fields    : ['partnerId->uid']
            ]
        ]
    }
}
