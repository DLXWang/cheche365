package com.cheche365.cheche.zhongan.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.service.ISignService
import com.cheche365.flow.core.service.TSimpleService
import groovy.util.logging.Slf4j
import org.springframework.core.env.Environment

import static com.cheche365.cheche.zhongan.flow.FlowMappings._FLOW_QUERY_SIGN_STATUS_MAPPINGS



/**
 * 深圳地区签名查询接口
 * create by sufc
 */
@Slf4j
class ZhonganQuerySignService implements ISignService<Object, Object, String>, TSimpleService {

    ZhonganQuerySignService(Environment env) {
        this.env = env
    }

    protected creatContext(env, insureFlowCode) {

        [
            client                    : new RESTClient(env.getProperty('zhongan.api_base_url')),
            citySignStatusFlowMappings: _FLOW_QUERY_SIGN_STATUS_MAPPINGS,
            insureFlowCode            : insureFlowCode
        ]
    }

    @Override
    Object sign(Object obj) {
        return new UnsupportedOperationException("当前操作不支持")
    }

    @Override
    Boolean isSigned(String obj) {
        def context = creatContext(com_cheche365_flow_core_service_TSimpleService__env, obj)
        service context, "SignStatus", "Zhongan深圳地区查询签名接口"
    }
}
