package com.cheche365.cheche.mock.controller

import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.*
import com.cheche365.cheche.core.service.AutoService
import com.cheche365.cheche.core.service.InsurancePackageService
import com.cheche365.cheche.core.service.OrderOperationInfoService
import com.cheche365.cheche.core.service.PurchaseOrderIdService
import com.cheche365.cheche.core.tools.SystemTool
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.externalapi.api.sinosafe.SinosafeQueryAPI
import com.cheche365.cheche.externalpayment.util.ZaSignUtil
import com.cheche365.cheche.web.ContextResource
import com.cheche365.cheche.web.counter.annotation.NonProduction
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.web.bind.annotation.*

import javax.validation.Valid

import static com.cheche365.cheche.core.model.ApiPartner.findByCode
import static com.cheche365.cheche.core.tools.SystemTool.*

/**
 * Created by wenling on 2017/5/31.
 * 单元测试实现类
 */
@RestController
@RequestMapping("/v1.6/mock/model")
@Slf4j
class MockModelResource extends ContextResource {

    @Autowired
    private PurchaseOrderIdService orderNoService
    @Autowired
    private InsurancePackageService ipService
    @Autowired
    private QuoteRecordRepository qrRepo
    @Autowired
    private OrderOperationInfoService ooiService
    @Autowired
    AutoService autoService;
    @Autowired
    UserRepository userRepo
    @Autowired
    PartnerUserRepository partnerUserRepository
    @Autowired
    SinosafeQueryAPI sinosafeQueryStateHandler
    @Autowired
    CompulsoryInsuranceRepository ciRepo;
    @Autowired
    InsuranceRepository iRepo;

    static final MODEL_UNIQUE_FIELD = [
            user: 'Mobile',
            internaluser: 'Email',
            insurancepackage: 'UniqueString',
            wechatuserinfo: 'Unionid',
            wechatuserchannel: 'OpenId'
    ]

    static final MODEL_TO_MAP = ['quoterecord', 'autotype', 'vehiclelicense','insurance','compulsoryInsurance']

    def MODEL_PRE_PROCESS = [
        purchaseorder: { it.orderNo = orderNoService.getNext(OrderType.Enum.INSURANCE) }
    ]
    def MODEL_POST_PROCESS = [
        purchaseorder: { PurchaseOrder po ->
                QuoteRecord qr = qrRepo.findOne(po.objId)
                !qr.premium ?:save('insurance', generateBill(qr, Insurance, po))
                !qr.compulsoryPremium ?: save('compulsoryinsurance', generateBill(qr, CompulsoryInsurance, po))
                ooiService.saveOrderCenterInfo(po)
                //对于保存的初始订单状态为完成、出单中等，orderOperationInfo.currentStatus值为null，导致出单中心查询订单报错。
                def ooi = ooiService.findByOrderNo(po.orderNo)
                if(!ooi.currentStatus){
                    ooi.setCurrentStatus(OrderTransmissionStatus.Enum.UNPAID)
                    ooiService.save(ooi)
                }
        }
    ]


    @NonProduction
    @RequestMapping(value="{model}", method= RequestMethod.POST)
    persist(@PathVariable String model, @RequestBody String data){
        def modelObj = findModel(model)
                        .with{ Class clazz ->
                            if(data.trim().startsWith('[')){
                                CacheUtil.doListJacksonDeserialize(data, clazz)
                            } else {
                                CacheUtil.doJacksonDeserialize(data, clazz)
                            }
                        }
        MODEL_PRE_PROCESS.each { k, action ->
            k != model.toLowerCase() ?: action(modelObj)
        }

        def persistModel = MODEL_TO_MAP.contains(model) ? asMap(save(model, modelObj)) : save(model, modelObj)
        MODEL_POST_PROCESS.each { k, action ->
            k != model.toLowerCase() ?: action(persistModel)
        }
        persistModel
    }


    @NonProduction
    @RequestMapping(value="{model}/{id}", method= RequestMethod.DELETE)
    delete(@PathVariable String model,  @PathVariable Long id){
        findRepoBean(model){
            it.delete(id)
        }
    }

    @NonProduction
    @RequestMapping(value="{model}/{id}", method= RequestMethod.GET)
    findOne(@PathVariable String model, @PathVariable Long id){
        def modelObj = findRepoBean(model){
            it.findOne(id)
        }
        MODEL_TO_MAP.contains(model) ? asMap(modelObj) : modelObj
    }

    @NonProduction
    @RequestMapping(value="{model}", method= RequestMethod.GET)
    findByConditions(@PathVariable String model, @RequestParam Map conditions){
        callRepoFindBy(model, conditions)
    }

    @NonProduction
    @RequestMapping(value = "user/{mobile}",method = RequestMethod.DELETE)
    deleteUser(@PathVariable String mobile){
        def existed = userRepo.findUsersByMobile(mobile)
        existed.each {
            it.mobile = null
        }

        ['users': existed.size() > 0 ? save('user', existed) : []]

    }

    @NonProduction
    @DeleteMapping('partnerUser/{partnerCode}/{uid}')
    deletePartnerUser(@PathVariable String partnerCode, @PathVariable String uid) {
        ApiPartner partner = findByCode(partnerCode)
        def partnerUser = partnerUserRepository.findFirstByPartnerAndPartnerId(partner, uid)
        ['partnerUser': partnerUser ? [partnerUserRepository.save(partnerUser.with {
            partnerId = UUID.randomUUID(); it
        })] : []]
    }

    @NonProduction
    @RequestMapping(value="sign", method= RequestMethod.POST)
    persistSign( @RequestBody Map conditions){
        ZaSignUtil.buildRequestPara(conditions.sParaTemp,conditions.key)
    }

    @NonProduction
    @RequestMapping(value="batch", method= RequestMethod.POST)
    persistOrderSign( @RequestBody @Valid PurchaseOrder order){
        Insurance insurance=iRepo.findByQuoteRecordId(order.objId)
        CompulsoryInsurance compulsoryInsurance=ciRepo.findByQuoteRecordId(order.objId)
        sinosafeQueryStateHandler.call(insurance,compulsoryInsurance)
    }


    @NonProduction
    @RequestMapping(value="auto/encryptAuto", method= RequestMethod.POST)
    persistEncryptAuto( @RequestBody Map conditions){
        def vl=new VehicleLicense(licensePlateNo: conditions.licensePlateNo, owner: conditions.owner,
            identity: conditions.identity, vinNo: conditions.vinNo, engineNo: conditions.engineNo)
        autoService.encryptVehicleLicense(request.getSession().getId(), vl)
        CacheUtil.doJacksonSerialize(vl)
    }

    @NonProduction
    @RequestMapping(value="auto/decryptAuto", method= RequestMethod.POST)
    persistDecryptAuto( @RequestBody Map conditions) {
        def auto = new Auto(licensePlateNo: conditions.licensePlateNo, owner: conditions.owner,
            identity: conditions.identity, vinNo: conditions.vinNo, engineNo: conditions.engineNo)

        autoService.decryptAuto(auto, safeGetCurrentUser(), request.session.id)
        CacheUtil.doJacksonSerialize(auto)
    }

    @NonProduction
    @RequestMapping(value="ba", method= RequestMethod.POST)
    persistBA(@RequestBody String modelJson){
        CacheUtil.doJacksonDeserialize(modelJson , BusinessActivity).with {
            it.landingPage = '/m/channel/' + it.code
            save('businessactivity', it)
        }
    }

    @NonProduction
    @RequestMapping(value="channels",method= RequestMethod.GET)
    def channels(){
        Channel.allChannels()
    }


    def save(String model, modelObj) {
        findRepoBean(model) { CrudRepository repo ->
            def uniqueField = MODEL_UNIQUE_FIELD.get(model.toLowerCase())
            (List.isInstance(modelObj) ? modelObj : [modelObj]).collect { mo ->
                def uniqueFieldValue = uniqueField ? mo."${SystemTool.initialCase(uniqueField) { it.toLowerCase() }}" : null
                (uniqueFieldValue ? repo."findFirstBy$uniqueField"(uniqueFieldValue) : null) ?: repo.save(mo)
            }.with { it.size() > 1 ? it : it.first() }
        }
    }


    def generateBill(QuoteRecord quoteRecord, Class targetClass, PurchaseOrder order) {

        def ignoreProperties = ['metaClass', 'class', 'id']
        def target = targetClass.newInstance();
        target.metaClass.properties.each {
            if (quoteRecord.metaClass.hasProperty(quoteRecord, it.name) && !ignoreProperties.contains(it.name))
                it.setProperty(target, quoteRecord.metaClass.getProperty(quoteRecord, it.name))
        }

        target.with {
            it.quoteRecord = quoteRecord
            insuredName = order.insuredName ? order.insuredName : quoteRecord.auto?.owner
            insuredIdNo = order.insuredIdNo && !order.insuredIdNo.contains('*') ? order.insuredIdNo : quoteRecord.auto.identity
            insuredIdentityType = order.insuredIdentityType ?: IdentityType.Enum.IDENTITYCARD
            applicantName = order.applicantName ? order.applicantName : target.insuredName
            applicantIdNo = order.applicantIdNo && !order.applicantIdNo.contains('*') ? order.applicantIdNo : target.insuredIdNo
            applicantIdentityType = order.applicantIdentityType ?: IdentityType.Enum.IDENTITYCARD

            it
        }

    }



}
