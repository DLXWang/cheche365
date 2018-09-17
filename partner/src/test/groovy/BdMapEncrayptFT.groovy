import com.cheche365.cheche.core.model.Address
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.partner.utils.BaiduEncryptUtil

/**
 * Created by zhengwei on 5/16/16.
 */
class BdMapEncrayptFT extends PartnerFT{

    def "decrapyt test" (){

        given : '加密后数据'

        def testData =  '{"id":47055,"description":"2016-05-12 16:07:43 银联9114.31元","applicant":{"id":1465945,"name":null,"userId":null,"mobile":"13006158050","deviceUniqueID":null,"email":null,"gender":null,"genderStr":null,"yearOfBirth":null,"memberPoints":null,"isMember":null,"identity":null,"identityType":null,"password":null,"userType":{"id":1,"name":"消费者","description":"消费者"},"headImgUrl":null,"nickName":null,"sex":null,"bound":true,"createTime":1463040146000,"updateTime":1463040146000,"birthday":null,"vipCompany":null,"wechatUser":null,"registerUser":false,"registerIp":null,"registerChannel":{"id":3,"name":"微信","description":"微信公众号"},"userSource":null,"alipayUser":null,"audit":1},"auto":{"id":435976,"kilometerPerYear":0,"autoType":{"id":34107,"brand":null,"brandLogo":null,"newPrice":null,"seats":null,"currentPrice":null,"code":"宝马","name":null,"manufacturer":null,"description":null,"family":null,"group":null,"model":null,"exhaustScale":null,"logo":null,"logoUrl":null,"supplementInfo":null},"licensePlateNo":"鄂A855M5","engineNo":"2200C381","owner":"杨迎霞","vinNo":"LBV5S1108ESH70592","enrollDate":1401206400000,"area":{"id":420100,"name":"武汉市","type":{"id":3,"name":"市","description":"市"},"shortCode":"鄂A","active":true,"postalCode":"430000","children":null,"reform":true},"licenseType":null,"licenseColorCode":"01","identity":"420321197703190030","identityType":{"id":1,"name":"身份证","description":"身份证"},"disable":false,"billRelated":true,"userAutos":null,"autoTypeExternalCode":null,"mobile":null},"type":{"id":1,"name":"车险","description":"车险"},"objId":80918,"status":{"id":1,"status":"创建","description":"建"},"payableAmount":"9114.31","paidAmount":"9114.31","channel":{"id":3,"channel":"银联","description":"银联","customerPay":true},"sourceChannel":{"id":3,"name":"微信","description":"微信公众号"},"deliveryAddress":{"id":32906,"description":null,"applicant":{"id":1465945,"name":null,"userId":null,"mobile":"13006158050","deviceUniqueID":null,"email":null,"gender":null,"genderStr":null,"yearOfBirth":null,"memberPoints":null,"isMember":null,"identity":null,"identityType":null,"password":null,"userType":{"id":1,"name":"消费者","description":"消费者"},"headImgUrl":null,"nickName":null,"sex":null,"bound":true,"createTime":1463040146000,"updateTime":1463040146000,"birthday":null,"vipCompany":null,"wechatUser":null,"registerUser":false,"registerIp":null,"registerChannel":{"id":3,"name":"微信","description":"微信公众号"},"userSource":null,"alipayUser":null,"audit":1},"area":null,"street":"常青花园114","district":"420112","districtName":"东西湖区","city":"420100","cityName":"武汉市","province":"420000","provinceName":"湖北省","name":"杨霞","telephone":null,"mobile":"13006158050","postalcode":null,"disable":false,"defaultAddress":false,"address":"湖北省武汉市东西湖区常青花园114"},"operator":null,"createTime":1463040437000,"updateTime":null,"invoiceHeader":null,"sendDate":null,"timePeriod":null,"orderNo":"I20160512000046","wechatPaymentCalledTimes":0,"wechatPaymentSuccessOrderNo":null,"version":0,"vipCompanyActivity":null,"area":{"id":420100,"name":"武汉市","type":{"id":3,"name":"市","description":"市"},"shortCode":"鄂A","active":true,"postalCode":"430000","children":null,"reform":true},"trackingNo":null,"giftId":null,"realGifts":null,"insuredName":null,"insuredIdNo":null,"applicantName":null,"applicantIdNo":null,"orderSourceType":{"id":1,"name":"CPS渠道","description":"CPS渠道"},"orderSourceId":"86","deliveryInfo":null,"comment":null,"expireTime":"2016-05-13 00:07:17"}'

        when :
//        println(BaiduEncryptUtil.decrypt(testData))

        CacheUtil.doJacksonDeserialize(testData, PurchaseOrder.class)

        then:
        true
    }
}
