package com.cheche365.cheche.web.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentStatus
import com.cheche365.cheche.core.model.PaymentType
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.externalapi.api.za.ZaSignStatusAPI
import com.cheche365.cheche.externalapi.model.ZaSignStatusResponse
import org.springframework.data.redis.core.StringRedisTemplate

import static com.cheche365.cheche.externalapi.model.ZaSignStatusResponse.IDENTITY_TYPE_TO_ZA_MAPPING
import com.zhongan.scorpoin.signature.SignatureUtils
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

import javax.ws.rs.core.UriBuilder
import java.text.SimpleDateFormat
import static com.cheche365.cheche.core.util.MockUrlUtil.findPrivateKey
import static com.cheche365.cheche.core.util.MockUrlUtil.findPublicKey
import static com.cheche365.cheche.core.constants.CacheConstants.ZHONGAN_SIGN_UPLOAD_FINISHED

/**
 * Created by wen on 2018/7/20.
 */
@Service
class ZhongAnSignService {

    private static Logger logger = LoggerFactory.getLogger(ZhongAnSignService.class)

    @Autowired
    ZaSignStatusAPI zaSignStatusAPI

    @Autowired
    PaymentRepository paymentRepository

    @Autowired
    StringRedisTemplate redisTemplate

    @Autowired
    Environment env

    boolean isUnsigned(PurchaseOrder purchaseOrder){

        if(OrderStatus.Enum.FINISHED_5 != purchaseOrder.status){
            logger.debug('众安订单{}未完成，忽略签名链接生成', purchaseOrder.orderNo)
            return false
        }

        Payment payment = paymentRepository
                            .findByPurchaseOrder(purchaseOrder)
                            .find {it.status == PaymentStatus.Enum.PAYMENTSUCCESS_2 && it.paymentType == PaymentType.Enum.INITIALPAYMENT_1}

        if(!payment){
            logger.debug('众安订单{}无成功的支付记录，忽略签名链接生成', purchaseOrder.orderNo)
            return false
        }

        ZaSignStatusResponse res = getSignStatus(payment.outTradeNo)

        if(res.isFlowFinished()){
            logger.debug('众安订单{}签名/上传流程已结束，添加到已完成集合', purchaseOrder.orderNo)
            redisTemplate.opsForSet().add(ZHONGAN_SIGN_UPLOAD_FINISHED, purchaseOrder.orderNo)
        }

        logger.debug('众安订单{}签名必要性查询结果，是否未上传: {}, 是否未签名: {}', purchaseOrder.orderNo, res.isNotUploaded(), res.isUnsigned())
        return res.needLink()
    }

    def buildSignLink(Auto auto){

        verify(auto)

        UriBuilder.fromUri(env.getProperty('zhongan.image_upload_url').trim().toURI())
            .queryParam("vehicleLicencePlateNo" , auto.licensePlateNo)
            .queryParam("vehicleFrameNo" , auto.vinNo)
            .queryParam("applicantCertificateNo" , auto.identity[-6..-1])
            .queryParam("applicantCertificateType" , IDENTITY_TYPE_TO_ZA_MAPPING.get(auto.identityType))
            .build().toString()

    }

    def getSignStatus(String outTradeNo){
        def response = zaSignStatusAPI.call(reqEncryptData(outTradeNo))
        def bizContent = resDecrypt(response)

        logger.info("众安电子保单签名状态响应解密后:${bizContent}")
        new ZaSignStatusResponse(new JsonSlurper().parseText(new JsonSlurper().parseText(bizContent)?.responseResult as String) as Map)

    }

    def requestBuild(String outTradeNo){
        def reqJson =[
            requestNo: UUID.randomUUID().toString(),
            thirdCode: env.getProperty('zhongan.auth_third_party_code'),
            outTradeNo : outTradeNo
        ]

        [
            appKey      : env.getProperty('zhongan.auth_app_key'),
            charset     : 'UTF-8',
            serviceName : 'zhongan.castle.policy.querySignStatus',
            signType    : 'RSA',
            format      : 'json',
            version     : '1.0.0',
            timestamp   : new SimpleDateFormat('yyyyMMddHHmmssSSS').format(new Date()),
            requestParam:  new JsonBuilder(reqJson).toString()
        ]
    }

    def reqEncryptData(String outTradeNo){
        SignatureUtils.encryptAndSign(
            requestBuild(outTradeNo),
            findPublicKey() ?: env.getProperty('zhongan.auth_public_key'),
            findPrivateKey() ?: env.getProperty('zhongan.auth_private_key'),
            'UTF-8',
            true,
            true)
    }

    def resDecrypt(String response){
        Map responseToMap = new JsonSlurper().parseText(response) as Map
        SignatureUtils.checkSignAndDecrypt(
            responseToMap,
            findPublicKey() ?: env.getProperty('zhongan.auth_public_key'),
            findPrivateKey() ?: env.getProperty('zhongan.auth_private_key'),
            true,
            responseToMap.bizContent as boolean)
    }

    static verify(Auto auto){
        if(!auto.identity || !IDENTITY_TYPE_TO_ZA_MAPPING.get(auto.identityType)){
            logger.error("车辆信息不符合众安电子签名规则")
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED,'签名申请失败')
        }
    }

}
