package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import com.cheche365.cheche.scheduletask.service.task.DatebaoOrderReportService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by yinJianBin on 2017/5/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ScheduleTaskConfig.class}
)
public class DatebaoOrderReportTaskTest {

//    @Autowired
//    private DatebaoOrderReportTask datebaoOrderReportTask;
//
//    @Autowired
//    private DatebaoOrderReportService datebaoOrderReportService;
//
//    @Autowired
//    PurchaseOrderRepository purchaseOrderRepository;
//
//
//    @Test
//    public void testDoProcess() throws Exception {
//        datebaoOrderReportTask.process();
//    }
//
//    @Test
//    public void testGetMessageInfo() throws Exception {
//        datebaoOrderReportTask.getMessageInfo();
//    }
//
//    @Test
//    public void testGetObjects() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
////        datebaoOrderReportService.getEmailDataList();
//        Date yesterdayStart = DateUtils.getCustomDate(new Date(), -100, 00, 00, 00);
//        Date yesterdayEnd = DateUtils.getCustomDate(new Date(), -1, 23, 59, 59);
//        List datebaoChannelList = Arrays.asList(Channel.Enum.PARTNER_JINGSUANSHI_58.getId(), Channel.Enum.ORDER_CENTER_JINGSUANSHI_59.getId());
//        List<Object[]> datebaoOrders = purchaseOrderRepository.findDatebaoOrders(yesterdayStart, yesterdayEnd, datebaoChannelList);
//
//        List<String> fieldNameList = Arrays.asList("parterUserId", "orderNo", "orderCreateTime", "orderStatus", "cityName", "insuranceCompanyName", "mobile", "nickName", "ownerMobile", "source", "registerChannel", "licensePlateNo", "vinNo", "engineNo", "enrollDate", "seats", "brandAndModel", "transferDate", "insranceEffectiveDate", "insranceExpireDate", "insrancePolicyNo", "compulsoryEffectiveDate", "compulsoryExpireDate", "compulsoryPolicyNo", "payableAmount", "paidAmount", "compulsoryPremium", "autoTax", "commecialPremium", "damagePremium", "damageAmount", "thirdPartyPremium", "thirdPartyAmount", "driverPremium", "driverAmount", "passengerPremium", "passengerAmount", "theftPremium", "theftAmount", "scratchPremium", "scratchAmount", "spontaneousLossPremium", "spontaneousLossAmount", "enginePremium", "engineAmount", "ownerName", "ownerIdentityType", "ownerIdentity", "applicantName", "applicantIdentityType", "applicantIdentity", "insuredName", "insuredIdentityType", "insuredIdentity", "paymentId", "paymentType", "amount", "status", "updateTime", "paymentChannel", "outTradeNo", "thirdpartyPaymentNo", "receiver", "receiverIdentity", "receiverMobile", "sendTime", "sendPeriod", "address");
//        List<DatebaoEmailInfo> datebaoEmailInfos = this.objectMapper(datebaoOrders, fieldNameList, DatebaoEmailInfo.class);
//
//
//        System.out.println(datebaoEmailInfos);
//    }
//
//
//    List<DatebaoEmailInfo> objectMapper(List<Object[]> objectsList, List<String> fieldNameList, Class clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//
//        List<DatebaoEmailInfo> emailInfoList = new ArrayList<>(objectsList.size());
//        for (int i = 0; i < objectsList.size(); i++) {
//            Object[] objects = objectsList.get(i);
//            DatebaoEmailInfo model = new DatebaoEmailInfo();
//            for (int j = 0; j < fieldNameList.size(); j++) {
//                String name = fieldNameList.get(j); // 获取属性的名字
////                name = name.substring(0, 1).toUpperCase() + name.substring(1);
////                Method m = clazz.getMethod(name, String.class);
////                m.invoke(model, objects[j] == null ? " " : objectsList.get(j));
//
//                model.getMetaClass().setAttribute(model, name, objects[j] == null ? " " : (String) objects[j]);
//            }
//            emailInfoList.add(model);
//        }
//
//        return emailInfoList;
//    }

}
