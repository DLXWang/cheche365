package com.cheche365.cheche.idcredit.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.repository.ApplicationLogRepository
import com.cheche365.cheche.core.service.IContext
import com.cheche365.cheche.core.service.ISelfIncrementCountingCurrentLimiter
import com.cheche365.cheche.parser.service.AThirdPartyVehicleLicenseService
import groovy.util.logging.Slf4j
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.ssl.SSLSocketFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.core.constants.WebConstants.CHANNEL_SERVICE_ITEMS
import static com.cheche365.cheche.core.model.AutoVehicleLicenseServiceItem.Enum.IDCREDIT_1
import static com.cheche365.cheche.idcredit.flow.Constants._VEHICLE_INFO_EXTRACTOR
import static com.cheche365.cheche.idcredit.flow.FlowMappings._FLOW_CATEGORY_VEHICLE_LICENSE_FLOW_MAPPINGS

/**
 * 绿湾行驶证服务
 */
@Service
@Profile([
    'qa',
    'production'
])
@Slf4j
class IdcreditVehicleLicenseService extends AThirdPartyVehicleLicenseService {

    @Autowired
    @Qualifier('idcreditGlobalContext')
    private IContext globalContext

    @Autowired
    @Qualifier('idcreditAPIThrottleFindVehicleInfo')
    private ISelfIncrementCountingCurrentLimiter findVehicleInfoCurrentLimiter

    @Autowired
    @Qualifier('idcreditAPIThrottleGetToken')
    private ISelfIncrementCountingCurrentLimiter getTokenCurrentLimiter

    @Autowired
    private ApplicationLogRepository applicationLogRepository


    @Override
    final protected doCreateContext(env, area, auto, additionalParameters) {
        [
            client                          : getHttpClient(getEnvProperty(
                [env: env],
                'idcredit.api_base_url'
            )),
            globalContext                   : globalContext,
            getTokenCurrentLimiter          : getTokenCurrentLimiter,
            findVehicleInfoCurrentLimiter   : findVehicleInfoCurrentLimiter,
            cityVehicleLicenseFlowMappings  : _FLOW_CATEGORY_VEHICLE_LICENSE_FLOW_MAPPINGS,
            vehicleInfoExtractor            : _VEHICLE_INFO_EXTRACTOR,
            applicationLogRepository        : applicationLogRepository,
            additionalParameters            : additionalParameters ?: [:]
        ]
    }

    @Override
    boolean isServiceAvailable(context) {
        findVehicleInfoCurrentLimiter.allowed
    }

    @Override
    boolean isOperationAllowed(context) {
        // 判断行驶证服务支持的是否包含绿湾
        def autoVehicleLicenseServiceItems = context.additionalParameters[CHANNEL_SERVICE_ITEMS]

        autoVehicleLicenseServiceItems?.any {
            IDCREDIT_1.serviceName == it.serviceName
        }
    }

    private static getHttpClient(baseUri) {

        def trustManager = trustAnyTrustManager

        def sslCtx = SSLContext.getInstance('TLS')
        sslCtx.init(null, [trustManager] as TrustManager[], null)
        def socketFactory = new SSLSocketFactory(sslCtx, null, null, null)

        new RESTClient(baseUri).with {
            client.connectionManager.schemeRegistry.register(new Scheme('https', 443, socketFactory))
            it
        }
    }

    private static getTrustAnyTrustManager() {
        new X509TrustManager() {

            @Override
            void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            X509Certificate[] getAcceptedIssuers() {
                null
            }
        }
    }

}
