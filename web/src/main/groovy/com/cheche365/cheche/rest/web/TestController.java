package com.cheche365.cheche.rest.web;

import com.cheche365.cheche.alipay.web.AlipayMenuManager;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.OrderStatus;
import com.cheche365.cheche.core.model.PartnerOrder;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.repository.PartnerOrderRepository;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.partner.utils.BaiduEncryptUtil;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.counter.annotation.NonProduction;
import com.cheche365.cheche.web.model.Message;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.service.ClearCacheService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

import static com.cheche365.cheche.web.integration.Constants._SYNC_ORDER_CHANNEL;

/**
 * Created by liqiang on 7/23/15.
 */

@RestController
public class TestController {

    @Autowired
    private PurchaseOrderService orderService;

    @Autowired
    private PartnerOrderRepository partnerOrderRepository;

    @Autowired
    ClearCacheService clearCacheService;

    @NonProduction
    @RequestMapping(value = "/baidu/{mobile}/encrypt", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<String>> encryptMobile(@PathVariable String mobile) {
        String encrypt = BaiduEncryptUtil.encrypt(mobile);
        RestResponseEnvelope envelope = new RestResponseEnvelope(encrypt);
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }

    @RequestMapping(value = "/redis/pubsub/{orderNo}", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<String>> redisPublish(@PathVariable String orderNo,
                                                                 @RequestParam(value = "status", required = false) Long status) {
        PurchaseOrder purchaseOrder = this.orderService.findFirstByOrderNo(orderNo);
        if (null == purchaseOrder) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, orderNo + "订单号不存在。");
        }
        PartnerOrder partnerOrder = partnerOrderRepository.findFirstByPurchaseOrderId(purchaseOrder.getId());
        if (null == partnerOrder) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, orderNo + "订单号不存在与第三方的关联关系。");
        }

        if (status != null) {
            for (OrderStatus orderStatus : OrderStatus.Enum.ALL) {
                if (orderStatus.getId().equals(status)) {
                    purchaseOrder.setStatus(orderStatus);
                    break;
                }
            }
        }
        _SYNC_ORDER_CHANNEL.send(new Message<PurchaseOrder>(purchaseOrder));
        RestResponseEnvelope envelope = new RestResponseEnvelope("同步订单[" + orderNo + "]成功");
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }


    @RequestMapping(value = "/" + ContextResource.VERSION_NO + "/web/alipay/menu/{descEnv}", method = RequestMethod.POST)
    @NonProduction
    public HttpEntity<RestResponseEnvelope<String>> changeMenuManager(@PathVariable String descEnv) {
        if (!("pre_release".equals(descEnv) || "itg".equals(descEnv))) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "输入参数有误");
        }
        System.setProperty("spring.profiles.active", descEnv);//production,pre_release.itg
        AlipayMenuManager manager = new AlipayMenuManager();
        manager.update();
        return new ResponseEntity<>(new RestResponseEnvelope("成功切换服务窗到" + descEnv), HttpStatus.OK);
    }


    @RequestMapping(value = "/quote/config/cache",method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> clearCache(String cacheKey){
        clearCacheService.clearCache(StringUtils.isNotBlank(cacheKey) ? Arrays.asList(cacheKey.split(",")) : null);

        return new ResponseEntity<>(new RestResponseEnvelope("ok"),HttpStatus.OK);
    }

}
