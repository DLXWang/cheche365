package com.cheche365.cheche.manage.common.service

import com.baidu.tool.RsaUtil
import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.common.util.StringUtil
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.*
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.core.service.ResourceService
import com.cheche365.cheche.core.util.FileUtil
import com.cheche365.cheche.core.util.RuntimeUtil
import com.cheche365.cheche.manage.common.constants.BaiduInsurConstant
import com.cheche365.cheche.manage.common.model.BaiduInsurSenderViewModel
import com.cheche365.cheche.manage.common.util.FtpUtils
import net.sf.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

import java.text.DecimalFormat

import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty

/**
 * Created by chenxiangyin on 2018/3/29.
 */
@Service
class HttpBaiduSender {
    private Logger logger = LoggerFactory.getLogger(HttpBaiduSender.class);
    @Autowired
    PurchaseOrderRepository purchaseOrderRepository
    @Autowired
    ResourceService resourceService
    @Autowired
    InsuranceRepository insuranceRepository
    @Autowired
    CompulsoryInsuranceRepository compulsoryInsuranceRepository
    @Autowired
    QuoteRecordRepository quoteRecordRepository
    @Autowired
    PartnerOrderRepository partnerOrderRepository
    @Autowired
    PurchaseOrderService purchaseOrderService
    @Autowired
    InsurancePolicyRepository insurancePolicyRepository
    @Autowired
    private Environment env
    /**
     * apiPartnerProperties 通过key获取value
     * @param constant key值
     * @return value值
     */
    def findPartnerProperties(constant){
        return ApiPartnerProperties.findByPartnerAndKey(ApiPartner.Enum.BDINSUR_PARTNER_50, constant).getValue()
    }

    def getPartnerOrderList(){
        Date yesterdayStart = DateUtils.getCustomDate(new Date(), -1, 0, 0, 0)
        Date yesterdayEnd = DateUtils.getCustomDate(new Date(), -1, 23, 59, 59)
        return partnerOrderRepository.getPartnerByTime(Arrays.asList(OrderStatus.Enum.FINISHED_5, OrderStatus.Enum.REFUNDED_9),
            ApiPartner.Enum.BDINSUR_PARTNER_50, yesterdayStart, yesterdayEnd)
    }



    String setFile(){
        //初始化需要从ApiPartnerProperties
        def ftp_url = findPartnerProperties(BaiduInsurConstant.FTP_URL)
        def ftp_pwd = findPartnerProperties(BaiduInsurConstant.FTP_PWD)
        def ftp_port = findPartnerProperties(BaiduInsurConstant.FTP_PORT)
        def ftp_directory = findPartnerProperties(BaiduInsurConstant.FTP_DERECTORY)
        def private_key =findPartnerProperties(BaiduInsurConstant.CHECHE_PRIVATE_KEY)
        def sp_no = findPartnerProperties(BaiduInsurConstant.PARTNER_SP_NO)
        def sync_order_url = findPartnerProperties(ApiPartnerProperties.Key.SYNC_ORDER_URL)
        def env = [env: env]
        if(!RuntimeUtil.isProductionEnv())
        {
        ftp_url = getEnvProperty(env, 'baidusend.ftpurl')
        ftp_pwd = getEnvProperty(env, 'baidusend.ftppwd')
        def ftp_ports = getEnvProperty(env, 'baidusend.ftpport')
        ftp_port =Integer.parseInt(ftp_ports.toString())
        sp_no = getEnvProperty(env, 'baidusend.partnerspno')
        }


        //order列表 -->partnerOrderList
        List<PartnerOrder> partnerOrderList = getPartnerOrderList()
        //文件名 -->fileName
        String dateStr = DateUtils.getDateString(new Date(), DateUtils.DATE_NUMBER)
        String fileName = "insur_" + sp_no + "_confirm_" + dateStr + ".txt"
        //本地临时路径 -->filePathStr
        String filePath = resourceService.getResourceAbsolutePath( resourceService.getProperties().getBaiduInsurPath())
        logger.info("百度临时txt路径：" + filePath)
        //file内容 -->content
        String content = fillContent(partnerOrderList)
        //写入file
        FileUtil.writeFile(filePath+fileName,content.getBytes())
        String sign = RsaUtil.signFile (filePath + fileName, private_key)
        logger.debug("签名文件路径：" + filePath + "    名字：" + fileName+ ".OK", sign)
        writeFile(filePath, fileName+ ".OK", sign)
        File file = new File(filePath + fileName)
        boolean flag = FtpUtils.uploadFile(ftp_url + ftp_directory+ dateStr + "/", Integer.parseInt(ftp_port), sp_no, ftp_pwd, sync_order_url, fileName, new FileInputStream(file))
        if(flag){
            File signFile = new File(filePath + fileName + ".OK")
            boolean signFlag = FtpUtils.uploadFile(ftp_url + ftp_directory + dateStr + "/", Integer.parseInt(ftp_port), sp_no, ftp_pwd, sync_order_url, fileName + ".OK", new FileInputStream(signFile))
            if(signFlag){
                logger.debug("传输成功")
            }else{
                logger.error("第二个文件传输错误")
            }
        }else{
            logger.error("第一个文件传输错误")
        }
        return content
    }

    /**
     * 格式化content
     * @param finishedOrderList
     * @return 写入file内容
     */
    def fillContent(List<PartnerOrder> finishedOrderList){
        StringBuffer content = new StringBuffer()
        //------------- start -------------
        //第一行ST
        content.append("ST" + frontFill(finishedOrderList.size(),16) + "\n")
        //接下来投保人被保人信息
        if(finishedOrderList.size() != 0){
            finishedOrderList.each {
                content.append(formatOrderLine(it))
            }
        }
        //结尾行
        content.append("EN")
        //------------- end -------------
        return content
    }

    /**
     * 格式化content
     * @param partnerOrder 单条order数据
     * @return 单条数据返回
     */
    def formatOrderLine(PartnerOrder partnerOrder){
        def POLICY_CODE_MAPPING = [ //险种
                                    compulsoryPremium   : '100', // 机动车强制责任保险
                                    autoTax             : '962', // 车船税
                                    damage              : '200', // 车损险
                                    scratch             : '210', // 车身划痕损失险条款
                                    glass               : '231', // 玻璃单独破碎险条款
                                    engine              : '291', // 发动机特别损失险条款
                                    spontaneousLoss     : '310', // 自燃损失险条款
                                    theft               : '500', // 盗抢险
                                    thirdParty          : '600', // 三者险
                                    driver              : '701', // 车上人员责任险（司机）
                                    passenger           : '702', // 车上人员责任险（乘客）
                                    iopTotal            : '965', // 不计免赔率特约条款
                                    damageIop           : '911', // 不计免赔率（车损险）
                                    thirdPartyIop       : '912', // 不计免赔率（三者险）
                                    theftIop            : '921', // 不计免赔率（机动车盗抢险）
                                    scratchIop          : '922', // 不计免赔率（车身划痕损失险）
                                    engineIop           : '924', // 不计免赔率（发动机特别损失险）
                                    driverIop           : '928', // 不计免赔率（车上人员责任险（司机））
                                    passengerIop        : '929', // 不计免赔率（车上人员责任险（乘客））
                                    spontaneousLossIop  : '963', // 不计免赔率（自燃）
                                    unableFindThirdParty: '964', // 无法找到第三方特约险
                                    designatedRepairShop: '252', // 指定专修厂险
        ]

        PurchaseOrder order = partnerOrder.getPurchaseOrder()
        Insurance insurance = insuranceRepository.findByQuoteRecordId(order.getObjId())
        CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceRepository.findByQuoteRecordId(order.getObjId())

        def policy = insurance != null ? insurance:compulsoryInsurance
        Payment payment = purchaseOrderService.getPaymentByPurchaseOrder(order)
        DecimalFormat df   = new DecimalFormat("######0.00")
        List<BaiduInsurSenderViewModel> insuranceModelList = new ArrayList<>()
        if(compulsoryInsurance != null){
            BaiduInsurSenderViewModel senderModel = createModel(compulsoryInsurance, order, partnerOrder, payment)
            insuranceModelList.add(senderModel.setPremium(
                df.format(0),
                df.format(compulsoryInsurance.getCompulsoryPremium()),
                df.format(compulsoryInsurance.getCompulsoryPremium()),
                '1',
                POLICY_CODE_MAPPING['compulsoryPremium']
            ))
            insuranceModelList.add(senderModel.setPremium(
                df.format(0),
                df.format(compulsoryInsurance.getAutoTax()),
                df.format(compulsoryInsurance.getAutoTax()),
                '2',
                POLICY_CODE_MAPPING['autoTax']
            ))
        }
        if(insurance != null){
            BaiduInsurSenderViewModel senderModel = createModel(insurance, order, partnerOrder, payment)
            POLICY_CODE_MAPPING.each{
                if(!(it.key.equals('compulsoryPremium') || it.key.equals('unableFindThirdParty') || it.key.equals('designatedRepairShop')|| it.key.equals('autoTax'))){
                    if(it.key.contains('iop') || it.key.contains('Iop')){
                        Double premium = policy."$it.key"
                        insuranceModelList.add(senderModel.setPremium(
                            df.format(0),
                            df.format(premium == null ? 0:premium),
                            df.format(premium == null ? 0:premium),
                            '2',
                            it.value
                        ))
                    }else{
                        Double amount = policy."${it.key}Amount"
                        Double premium = policy."${it.key}Premium"
                        insuranceModelList.add(senderModel.setPremium(
                            df.format(amount == null ? 0:amount),
                            df.format(premium == null ? 0:premium),
                            df.format(premium == null ? 0:premium),
                            '1',
                            it.value
                        ))
                    }
                }
            }
            insuranceModelList.add(senderModel.setPremium(
                df.format(0),
                df.format(insurance.getUnableFindThirdPartyPremium()),
                df.format(insurance.getUnableFindThirdPartyPremium()),
                '2',
                POLICY_CODE_MAPPING['unableFindThirdParty']
            ))

            insuranceModelList.add(senderModel.setPremium(
                df.format(0),
                df.format(insurance.getDesignatedRepairShopPremium()),
                df.format(insurance.getDesignatedRepairShopPremium()),
                '2',
                POLICY_CODE_MAPPING['designatedRepairShop']
            ))
        }
        StringBuffer returnBuffer = new StringBuffer()
        insuranceModelList.each { currentInsuranceModel->
            returnBuffer.append(BaiduInsurSenderViewModel.fillModel(currentInsuranceModel))
            returnBuffer.append("\n")
        }
        return returnBuffer.toString()
    }


    def createModel(policy, order, partnerOrder, payment){
        def orderStatusMapping = [ //订单状态
                                   (OrderStatus.Enum.HANDLING_2)       : '0', // 无记录
                                   (OrderStatus.Enum.PENDING_PAYMENT_1): '1', // 核保通过（待支付）
                                   (OrderStatus.Enum.INSURE_FAILURE_7) : '2', // 核保失败
                                   (OrderStatus.Enum.PENDING_PAYMENT_1): '3', // 支付处理中
                                   (OrderStatus.Enum.FINISHED_5)       : '4', // 承保成功
                                   (OrderStatus.Enum.PAID_3)           : '5', // 承保失败
                                   (OrderStatus.Enum.REFUNDED_9)       : '6', // 退保成功
        ]
        BaiduInsurSenderViewModel senderModel = new BaiduInsurSenderViewModel()
        DecimalFormat df   = new DecimalFormat("######0.00")
        senderModel.setApplyNo(partnerOrder.getPartnerOrderNo())
        senderModel.setPolicyNo(policy.policyNo)
        senderModel.setApplicationNo(policy.proposalNo)
        def state = partnerOrder.state
        if(!StringUtil.isNull(state)){
            JSONObject jsonObj = JSONObject.fromObject(state)
            senderModel.setItemCode(jsonObj.get("productCode"))
        }
        senderModel.setHolderName(policy.applicantName)
        senderModel.setHolderCertType(getCertType(policy.applicantIdentityType == null?"b" : policy.applicantIdentityType.name))
        senderModel.setHolderCertNo(policy.applicantIdNo)
        senderModel.setHolderEmail(policy.applicantEmail)
        senderModel.setHolderMobile(policy.applicantMobile)
        senderModel.setInsuredName(policy.insuredName)
        senderModel.setInsuredCertType(getCertType(policy.insuredIdentityType == null?"b" : policy.insuredIdentityType.name))
        senderModel.setInsuredCertNo(policy.insuredIdNo)
        senderModel.setInsuredRela('1')
        senderModel.setStatus(orderStatusMapping[order.status])
        senderModel.setUnit('1')
        senderModel.setPayAmount(df.format(payment.getAmount()))
        senderModel.setPayTime(DateUtils.getDateString(payment.getCreateTime(), "yyyyMMddHHmmss"))
        senderModel.setAcceptTime(DateUtils.getDateString(payment.getCreateTime(), "yyyyMMddHHmmss"))
        senderModel.setValidateDate(DateUtils.getDateString(policy.effectiveDate, "yyyyMMddHHmmss"))
        senderModel.setExpireDate(DateUtils.getDateString(policy.expireDate, "yyyyMMddHHmmss"))
        senderModel.setLicensePlateNo(policy.auto.licensePlateNo)
        senderModel.setLicensePlateType('02')
        senderModel.setMotorUsageTypeCode('11')
        senderModel.setMotorUsageTypeCode('000')
        senderModel.setFirstRegisterDate(DateUtils.getDateString(policy.auto.enrollDate, "yyyyMMddHHmmss"))
        senderModel.setVin(policy.auto.vinNo)
        senderModel.setEngineNo(policy.auto.engineNo)
        senderModel.setModel(policy.auto.autoType == null?"":policy.auto.autoType.code)
        senderModel.setNoLicenseFlag('0')
        senderModel.setNewVehicleFlag('0')
        senderModel.setChgOwnerFlag('0')
        senderModel.setOwnerName(policy.auto.owner)
        senderModel.setOwnerCertType(getCertType(policy.auto.identityType))
        senderModel.setReciverName(order.getDeliveryAddress().getName())
        senderModel.setReciverMobile(order.getDeliveryAddress().getMobile())
        senderModel.setReciverAddress(order.getDeliveryAddress().getAddress())
        senderModel
    }
    /*
　　* 0 指前面补充零
　　* formatLength 字符总长度为 formatLength
　　* d 代表为正数
　　*/
    def writeFile(String path, String fileName, String content){
        def reportFile = new File(path, fileName)
        if(!reportFile.exists()){
            try{
                reportFile.createNewFile()
                writeFile(path,fileName,content)
            }catch(IOException e){
                logger.error("文件操作异常"+ path + fileName)
            }
        }

        logger.debug("开始写入文件-》" + path + fileName)
        FileWriter fw = new FileWriter(reportFile,false)
        fw.write(content)
        fw.close()
        return reportFile
    }

    /*
　　* 0 指前面补充零
　　* formatLength 字符总长度为 formatLength
　　* d 代表为正数。1
　　*/
    def frontFill(int source,int formatLength){
        return String.format("%0"+formatLength+"d", source)
    }

    private static getCertType(insuredIdentityType) {
        def identityTypeMapping = [
            (IdentityType.Enum.IDENTITYCARD)                 : 'b', // 身份证
            (IdentityType.Enum.PASSPORT)                     : 'c', // 护照
            (IdentityType.Enum.OTHER_IDENTIFICATION)         : 'i', // 其他
            (IdentityType.Enum.TAIWAN_LAISSEZ_PASSER)        : 'j', // 港澳台护照
            (IdentityType.Enum.HONGKONG_MACAO_LAISSEZ_PASSER): 'k', // 港澳通行证
            (IdentityType.Enum.MTP)                          : 'l', // 台胞证
            (IdentityType.Enum.RESIDENCE_BOOKLET)            : 'm', // 户口本
            (IdentityType.Enum.OFFICERARD)                   : 'o', // 军官证
        ]
        identityTypeMapping[insuredIdentityType] ?: 'i'
    }
}
