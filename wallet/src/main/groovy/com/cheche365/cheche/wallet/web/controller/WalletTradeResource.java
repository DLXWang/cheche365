package com.cheche365.cheche.wallet.web.controller;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.wallet.model.*;
import com.cheche365.cheche.wallet.service.WalletService;
import com.cheche365.cheche.wallet.service.WalletTradeService;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Created by mjg on 2017/6/6.
 */
@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "")
@VersionedResource(from = "1.0")
public class WalletTradeResource extends ContextResource {
    @Autowired
    private WalletTradeService walletTradeService;

    @Autowired
    private WalletService walletService;


    @RequestMapping(value = "/wallet/trades/{tradeNo}", method = RequestMethod.GET)
    public ResponseEntity<?> queryTradeDetail(@PathVariable String tradeNo) {
        if(tradeNo == null || tradeNo.length() <= 0){
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "订单号tradeNo参数不能为空！");
        }
        User user = currentUser();
        WalletTrade trade = walletTradeService.queryTradeByTradeNo(tradeNo, user);
        if (trade != null) {
            return new ResponseEntity(trade, HttpStatus.OK);
        } else {
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "钱包订单交易明细失败！");
        }
    }

    /**
     * 获取银行卡打款明细
     *
     * @param requestNo
     * @return
     */
    @RequestMapping(value = "/wallet/remits/{requestNo}", method = RequestMethod.GET)
    public ResponseEntity<?> queryRemitDetail(@PathVariable String requestNo) {
        if(requestNo == null || requestNo.length() <= 0){
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "订单号requestNo参数不能为空！");
        }
        User user = currentUser();
        WalletRemitRecord remitRecord = walletTradeService.queryRemitByRequestNo(requestNo, user);
        if (remitRecord != null) {
            return new ResponseEntity(remitRecord, HttpStatus.OK);
        } else {
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "查询钱包打款订单交易明细失败！");
        }
    }

    /**
     * 获取交易列表
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/wallet/trade/tradelist", method = RequestMethod.GET)
    public ResponseEntity<?> queryTradeList(@RequestParam(value = "beginDate") String beginDate, @RequestParam(value = "endDate") String endDate) {
        List<WalletTrade> trades = walletTradeService.queryTradeByTradeNo(beginDate, endDate);
        if (trades != null) {
            return new ResponseEntity(new ResultDataModel("0","查询成功！",trades), HttpStatus.OK);
        } else {
            return new ResponseEntity(new ResultDataModel("-1","查询交易明细失败！"), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/wallet/trades", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getTradeListByPage(@RequestParam(value = "status", required = false) String status,
                                                               @RequestParam(value = "page", required = false) Integer page,
                                                               @RequestParam(value = "size", required = false) Integer size) {

        Pageable pageable = new PageRequest(toPageStart(page), toPageSize(size), new Sort(Sort.Direction.DESC, "createTime"));


        List<WalletTradeStatus> formattedStatus;
        if (StringUtils.isNotBlank(status)) {
            formattedStatus = WalletTradeStatus.Enum.format(Arrays.asList(status.split(",")));
        } else {
            formattedStatus = WalletTradeStatus.Enum.ALL;
        }
        Wallet wallet = walletService.queryOrCreateWallet(currentUser(), ClientTypeUtil.getChannel(request));
        Map orders = walletTradeService.findByStatusAndUser(formattedStatus, wallet, pageable);
        return new ResponseEntity<>(new RestResponseEnvelope(orders), HttpStatus.OK);
    }

    /**
     *  收支明细详情
     * @param tradeId walletTrade id
     * @param tradeType  tradeType id
     * @return
     */
    @RequestMapping(value = "wallet/trades/detail", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getTradeDetail(@RequestParam(value = "tradeId") Long tradeId,
                                                           @RequestParam(value = "tradeType")Long tradeType){

        return new ResponseEntity<RestResponseEnvelope>(new RestResponseEnvelope(walletTradeService.walletTradeDetailInfo(tradeId,tradeType)),HttpStatus.OK);

    }

}
