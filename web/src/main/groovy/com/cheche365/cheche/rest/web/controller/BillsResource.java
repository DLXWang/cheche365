package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.service.CompulsoryInsuranceService;
import com.cheche365.cheche.core.service.InsuranceService;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhengwei on 7/10/15.
 */

@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/bills")
@VersionedResource(from = "1.0")
public class BillsResource extends ContextResource {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private InsuranceService insuranceService;

    @Autowired
    private CompulsoryInsuranceService cis;


    @VersionedResource(from = "1.3")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<List<InsuranceBills>>> getBills(
        @RequestParam(value = "page") Integer page,
        @RequestParam(value = "size", required = false) Integer size) {

        int pageSize = size == null ? WebConstants.PAGE_SIZE : size;
        int pageNum = (page == null || page <= 0) ? 0 : page;
        Pageable pageable = new PageRequest(pageNum, pageSize);
        Channel channel = ClientTypeUtil.getChannel(request);
        Page<PurchaseOrder> purchaseOrderPage = purchaseOrderService.findEffectiveOrdersByStatusAndApplicant(OrderStatus.Enum.allAvailable(), this.currentUser(), channel, pageable);
        List<PurchaseOrder> orders = purchaseOrderPage.getContent();
        List<Long> orderIds = new ArrayList<Long>();
        orders.forEach((p)->orderIds.add(p.getId()));

        StringBuffer debugMessage = new StringBuffer();
        Map resultMap = Maps.newHashMap();
        if (!orderIds.isEmpty()){
            List<Insurance> insurances = insuranceService.findInsurancesByUser(this.currentUser(),orderIds);
            List<CompulsoryInsurance> compulsoryInsurances = cis.findCompulsoryInsurancesByUser(this.currentUser(),orderIds);

            Map<String, Long> orderMap = purchaseOrderService.assambleOrder(orders);
            Map<Long, Insurance> insuranceMaps = insuranceService.assambleInsurance(insurances);
            Map<Long, CompulsoryInsurance> compulsoryInsuranceMaps = cis.assambleCompulsoryInsurance(compulsoryInsurances);

            List<InsuranceBills> bills = assambleInsuranceBills(orderMap, insuranceMaps, compulsoryInsuranceMaps, debugMessage);

            resultMap.put("content", bills);

        } else {
            resultMap.put("content", new ArrayList<InsuranceBills>());
        }
        resultMap.put("totalElements", purchaseOrderPage.getTotalElements());
        resultMap.put("totalPages", purchaseOrderPage.getTotalPages());
        resultMap.put("numberOfElements", purchaseOrderPage.getNumberOfElements());
        resultMap.put("last", purchaseOrderPage.isLast());
        resultMap.put("first", purchaseOrderPage.isFirst());
        resultMap.put("size", purchaseOrderPage.getSize());
        resultMap.put("number", purchaseOrderPage.getNumber());

        RestResponseEnvelope envelope = new RestResponseEnvelope(resultMap);
        envelope.setDebugMessage(debugMessage.toString());

        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }


    @RequestMapping(value = "{orderNo}", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<InsuranceBills>> getInsuranceBills(@PathVariable String orderNo) {

        InsuranceBills bills = new InsuranceBills();
        bills.setOrderNo(orderNo);

        Insurance insurance = this.purchaseOrderService.getInsuranceBillsByOrder(orderNo, this.currentUser());
        bills.setInsurance(insurance);

        CompulsoryInsurance ci = this.purchaseOrderService.getCIBillByOrder(orderNo, this.currentUser());
        bills.setCi(ci);

        StringBuffer debugMessage = new StringBuffer();
        this.validateBill(orderNo, bills, debugMessage);

        RestResponseEnvelope envelope = new RestResponseEnvelope(bills);
        envelope.setDebugMessage(debugMessage.toString());

        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }


    private void validateBill(String orderNo, InsuranceBills bills, StringBuffer debugMessage) {
        if (null == bills.getInsurance()) {
            debugMessage.append("订单[" + orderNo + "]商业险保单不存在。 ");
        } else if (StringUtils.isBlank(bills.getInsurance().getPolicyNo())) {
            bills.setInsurance(null);
            debugMessage.append("订单[" + orderNo + "]商业保险保单未完成，已过滤。 ");
        }


        if (null == bills.getCi()) {
            debugMessage.append("订单[" + orderNo + "]交强险保单不存在。");
        }
        if (null != bills.getCi() && StringUtils.isBlank(bills.getCi().getPolicyNo())) {
            bills.setCi(null);
            debugMessage.append("订单[" + orderNo + "]交强险保单未完成，已过滤" + System.lineSeparator());
        }
    }

    private List<InsuranceBills> assambleInsuranceBills(Map<String, Long> orderMap, Map<Long, Insurance> insuranceMaps, Map<Long, CompulsoryInsurance> compulsoryInsuranceMaps, StringBuffer debugMessage) {
        if (orderMap == null) {
            debugMessage.append("当期登录用户下无任何订单");
            return null;
        }
        List<InsuranceBills> bills = new ArrayList<>();
        for (String orderNo : orderMap.keySet()) {
            InsuranceBills bill = new InsuranceBills();
            bill.setOrderNo(orderNo);
            Long quoteRecordId = orderMap.get(orderNo);
            if (insuranceMaps != null && quoteRecordId != null) {
                bill.setInsurance(insuranceMaps.get(quoteRecordId));
            }
            if (compulsoryInsuranceMaps != null && quoteRecordId != null) {
                bill.setCi(compulsoryInsuranceMaps.get(quoteRecordId));
            }

            validateBill(bill.getOrderNo(), bill, debugMessage);

            if (bill.getInsurance() != null || bill.getCi() != null) {
                bills.add(bill);
            }
        }

        return bills;
    }

}
