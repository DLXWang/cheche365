package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.DailyInsuranceRepository;
import com.cheche365.cheche.core.repository.DailyRestartInsuranceRepository;
import com.cheche365.cheche.core.repository.InsuranceRepository;
import com.cheche365.cheche.core.serializer.SerializerUtil;
import com.cheche365.cheche.core.service.*;
import com.cheche365.cheche.wallet.model.Wallet;
import com.cheche365.cheche.wallet.model.WalletTrade;
import com.cheche365.cheche.wallet.model.WalletTradeSource;
import com.cheche365.cheche.wallet.model.WalletTradeStatus;
import com.cheche365.cheche.wallet.service.WalletService;
import com.cheche365.cheche.wallet.service.WalletTradeService;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.core.service.DailyInsuranceService;
import com.cheche365.cheche.core.service.WebPurchaseOrderService;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cheche365.cheche.core.exception.BusinessException.Code.OPERATION_NOT_ALLOWED;
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ANSWERN_65000;
import static com.cheche365.cheche.core.serializer.SerializerUtil.toMap;

/**
 * Created by mahong on 2016/11/29.
 * 按天买车险-停驶/复驶 [API]
 */
@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/dailyInsurances")
@VersionedResource(from = "1.4")
class DailyInsuranceResource extends ContextResource {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String STOP_ERROR_MSG = "当前状态不允许申请停驶，请稍后再试";
    private static final String RESTART_ERROR_MSG = "当前状态不允许申请复驶，请稍后再试";

    @Autowired
    private WebPurchaseOrderService orderService;
    @Autowired
    private DailyInsuranceRepository dailyInsuranceRepository;
    @Autowired
    private DailyInsuranceService dailyInsuranceService;
    @Autowired
    private DailyRestartInsuranceRepository restartInsuranceRepository;
    @Autowired
    private DailyRestartInsuranceService restartInsuranceService;
    @Autowired(required = false)
    @Qualifier("answernDailyInsuranceService")
    private IThirdPartyDailyInsuranceService answernDailyInsuranceService;
    @Autowired(required = false)
    @Qualifier("answernDailyInsuranceSyncService")
    private IThirdPartySyncInterruptableService answernDailyInsuranceSyncService;
    @Autowired
    private InsuranceRepository insuranceRepository;
    @Autowired
    private OrderImageService orderImageService;
    @Autowired
    private WalletTradeService tradeService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private ModuleService moduleService;
    @Autowired(required = false)
    public HttpServletRequest request;

    @VersionedResource(from = "1.4")
    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope> stopApply(@RequestBody Map<String, Object> param) {

        DailyInsurance dailyInsurance = dailyInsuranceService.extractStopParam(param);

        PurchaseOrder order = dailyInsuranceService.checkBeforeStop(dailyInsurance, currentUser());

        if (dailyInsuranceService.checkOrderBinding(order)) {
            throw new BusinessException(OPERATION_NOT_ALLOWED, STOP_ERROR_MSG);
        }

        Insurance insurance = insuranceRepository.findByQuoteRecordId(order.getObjId());

        dailyInsuranceService.initStopParams(param, dailyInsurance, order, insurance);

        answernDailyInsuranceSyncService.applySuspend(dailyInsurance.getPurchaseOrder(), insurance, dailyInsurance, new HashMap());

        dailyInsuranceService.saveDailyInsuranceCascade(dailyInsurance, dailyInsurance.getDailyInsuranceDetails());

        return getResponseEntity(toMap(dailyInsurance, "purchaseOrder,restartDate,discountAmount,bankCard,status,insurancePackage"));
    }

    @VersionedResource(from = "1.4")
    @RequestMapping(value = "/stop/confirm", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope> stopConfirm(@RequestBody Map<String, Object> param) {

        DailyInsurance dailyInsuranceDB = dailyInsuranceService.checkBeforeStopConfirm(param, currentUser());

        dailyInsuranceService.initStopConfirmParams(param, dailyInsuranceDB);

        Insurance insurance = insuranceRepository.findByPolicyNo(dailyInsuranceDB.getPolicyNo());

        answernDailyInsuranceService.suspend(dailyInsuranceDB.getPurchaseOrder(), insurance, dailyInsuranceDB, new HashMap());

        dailyInsuranceService.saveDailyInsuranceCascade(dailyInsuranceDB, dailyInsuranceDB.getDailyInsuranceDetails());

        dailyInsuranceDB.setDays(DateUtils.getDaysBetween(dailyInsuranceDB.getEndDate(), dailyInsuranceDB.getBeginDate()) + 1);

        Map resultInMap = SerializerUtil.convertDailyInsurance(dailyInsuranceDB);

        return getResponseEntity(resultInMap);
    }

    @VersionedResource(from = "1.4")
    @RequestMapping(value = "/stop/confirm/wallet", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope> stopWalletConfirm(@RequestBody Map<String, Object> param) {

        DailyInsurance dailyInsurance = dailyInsuranceService.extractStopParam(param);

        PurchaseOrder order = dailyInsuranceService.checkBeforeStop(dailyInsurance, currentUser());

        if (!dailyInsuranceService.bindPurchaseOrder(order, 60L * 2)) {
            throw new BusinessException(OPERATION_NOT_ALLOWED, RESTART_ERROR_MSG);
        }

        Insurance insurance = insuranceRepository.findByQuoteRecordId(order.getObjId());

        dailyInsuranceService.initStopParams(param, dailyInsurance, order, insurance);

        answernDailyInsuranceSyncService.applySuspend(dailyInsurance.getPurchaseOrder(), insurance, dailyInsurance, new HashMap());

        DailyInsurance dailyInsuranceDB = dailyInsuranceService.saveDailyInsuranceCascade(dailyInsurance, dailyInsurance.getDailyInsuranceDetails());

        dailyInsuranceService.initStopConfirmParams(param, dailyInsuranceDB);

        Insurance insurance2 = insuranceRepository.findByPolicyNo(dailyInsuranceDB.getPolicyNo());

        dailyInsuranceService.saveDailyInsuranceCascade(dailyInsuranceDB, dailyInsuranceDB.getDailyInsuranceDetails());

        dailyInsuranceDB.setDays(DateUtils.getDaysBetween(dailyInsuranceDB.getEndDate(), dailyInsuranceDB.getBeginDate()) + 1);

        Map resultInMap = SerializerUtil.convertDailyInsurance(dailyInsuranceDB);

        //记录钱包交易记录
        BigDecimal allReturnMoney = dailyInsuranceRepository.findAllReturnMoney(dailyInsurance.getPurchaseOrder());

        if ((allReturnMoney!=null ? allReturnMoney.doubleValue():0d) + dailyInsurance.getTotalRefundAmount() < insurance.getPremium()) {
            User user = currentUser();
            Channel channel = ClientTypeUtil.getChannel(request);
            Wallet wallet = walletService.queryOrCreateWallet(user, channel);
            WalletTrade trade = new WalletTrade();
            trade.setLicensePlateNo(dailyInsuranceDB.getPurchaseOrder().getAuto().getLicensePlateNo());
            trade.setStopBeginDate(dailyInsuranceDB.getBeginDate());
            trade.setStopEndDate(dailyInsuranceDB.getEndDate());
            trade.setRefundAmt(new BigDecimal(dailyInsuranceDB.getTotalRefundAmount()));
            trade.setAmount(new BigDecimal(dailyInsuranceDB.getTotalRefundAmount()));
            trade.setStatus(WalletTradeStatus.Enum.FINISHED_2);
            trade.setTradeSourceId(dailyInsuranceDB.getId());
            trade.setTradeFlag(1);
            trade.setChannel(channel.getId());
            trade.setTradeType(WalletTradeSource.Enum.DALIYBACK_1);
            trade.setUserId(user.getId());
            tradeService.createWalletTrade(trade, wallet);
        }else {
            logger.info("该订单停驶返钱的总金额大于商业险保险，钱包总提现金额为：{},本次返钱金额为：{}，总共商业险保费为：{}",allReturnMoney.doubleValue(),dailyInsurance.getTotalRefundAmount(),insurance.getPremium());
        }

        try {
            answernDailyInsuranceSyncService.syncSuspend(dailyInsuranceDB.getPurchaseOrder(), insurance2, dailyInsuranceDB, new HashMap());
        } catch (Exception e) {
            logger.error("调用安心停驶同步服务失败,订单号:{},exception:{}", order.getOrderNo(), ExceptionUtils.getStackTrace(e));
            dailyInsuranceDB.setIsSync(2);
        }
        dailyInsuranceService.saveDailyInsurance(dailyInsuranceDB);

        dailyInsuranceService.unbindPurchaseOrder(order);

        return getResponseEntity(resultInMap);
    }

    @VersionedResource(from = "1.4")
    @RequestMapping(value = "/restart", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope<DailyRestartInsurance>> restartApply(
        @RequestBody DailyInsurance dailyInsurance) {

        DailyInsurance dailyInsuranceDB = dailyInsuranceService.checkBeforeRestart(dailyInsurance);

        if (dailyInsuranceService.checkOrderBinding(dailyInsuranceDB.getPurchaseOrder())) {
            throw new BusinessException(OPERATION_NOT_ALLOWED, RESTART_ERROR_MSG);
        }

        DailyRestartInsurance restartInsurance = dailyInsuranceService.initRestartParams(dailyInsuranceDB, dailyInsurance);

        Insurance insurance = insuranceRepository.findByPolicyNo(dailyInsuranceDB.getPolicyNo());
        // 针对原调用安心接口停驶的用户，复驶时仍需要调用安心接口
        if(restartInsurance.getDailyInsurance().getBankCard() != null) {
            answernDailyInsuranceService.applyResume(dailyInsuranceDB.getPurchaseOrder(), insurance, restartInsurance, new HashMap());
        } else {
            answernDailyInsuranceSyncService.applyResume(dailyInsuranceDB.getPurchaseOrder(), insurance, restartInsurance, new HashMap());
        }

        restartInsuranceService.saveDailyRestartInsuranceCascade(restartInsurance, restartInsurance.getRestartInsuranceDetails());
        return new ResponseEntity<>(new RestResponseEnvelope(restartInsurance), HttpStatus.OK);
    }

    @VersionedResource(from = "1.4")
    @RequestMapping(value = "/restart/confirm/{restartInsuranceId}", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope<Map>> restartConfirm(
        @PathVariable Long restartInsuranceId) {
        DailyRestartInsurance restartInsurance = restartInsuranceRepository.findOne(restartInsuranceId);
        if (restartInsurance == null) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "复驶标识输入错误");
        }
        PurchaseOrder purchaseOrder = restartInsurance.getDailyInsurance().getPurchaseOrder();

        if (!dailyInsuranceService.bindPurchaseOrder(purchaseOrder, 60L)) {
            throw new BusinessException(OPERATION_NOT_ALLOWED, RESTART_ERROR_MSG);
        }

        Insurance insurance = insuranceRepository.findByQuoteRecordId(purchaseOrder.getObjId());

        Map supplementInfo = Maps.newHashMap();
        supplementInfo.put("images", orderImageService.getUploadImgUrls(restartInsuranceId, PurchaseOrderImageScene.Enum.DAILY_RESTART_6));
        Map<String, Object> additionalParameters = Maps.newHashMap();
        additionalParameters.put("supplementInfo", supplementInfo);
        // 针对原调用安心接口停驶的用户，复驶时仍需要调用安心接口
        if(restartInsurance.getDailyInsurance().getBankCard() != null) {
            answernDailyInsuranceService.resume(restartInsurance.getDailyInsurance().getPurchaseOrder(), insurance, restartInsurance, additionalParameters);
        }

        Payment payment = orderService.saveDailyRestartPayment(purchaseOrder, restartInsurance);
        restartInsurance.setPayment(payment);
        restartInsurance.appendDescription("复驶确认");
        restartInsuranceService.saveDailyRestartInsurance(restartInsurance);

        Map<String, Object> responseMap = Maps.newHashMap();
        responseMap.put("success", Boolean.TRUE);
        return new ResponseEntity<>(new RestResponseEnvelope(responseMap), HttpStatus.OK);
    }

    @VersionedResource(from = "1.4")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<DailyInsurance>> getDetailInfoByOrderNo(@RequestParam(value = "orderNo", required = false) String orderNo) {

        User user = this.currentUser();
        // 订单号为空，默认返回最近一次（按查看时间倒序、保单生效日期倒序）,一期暂时按订单倒序排序取第一个
        PurchaseOrder order = null;
        if (!StringUtils.isBlank(orderNo)) {
            order = orderService.checkOrder(orderNo, user);
        }
        if (StringUtils.isBlank(orderNo)) {
            List<PurchaseOrder> orders = orderService.findFinishedOrdersByApplicantAndCompany(user, ANSWERN_65000);
            order = CollectionUtils.isEmpty(orders) ? null : orders.get(0);
        }
        BusinessActivity businessActivity = businessActivity();
        final Channel channel = ClientTypeUtil.getChannel(request);
        Object homeMarketing = moduleService.homeMessages("HOME_MARKETING", businessActivity, channel);
        Map responseMap = (order != null) ? dailyInsuranceService.decorateResult(order, homeMarketing) : null;
        RestResponseEnvelope envelope = new RestResponseEnvelope(responseMap);
        return new ResponseEntity(envelope, HttpStatus.OK);
    }

    @VersionedResource(from = "1.4")
    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<DailyInsurance>> getAllOrders() {
        List<PurchaseOrder> orders = orderService.findFinishedOrdersByApplicantAndCompany(this.currentUser(), ANSWERN_65000);
        return new ResponseEntity<>(new RestResponseEnvelope(decorateResponse(orders)), HttpStatus.OK);
    }

    private List<Map<String, Object>> decorateResponse(List<PurchaseOrder> orders) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (PurchaseOrder order : orders) {
            Map<String, Object> each = new HashMap<>();
            each.put("orderNo", order.getOrderNo());
            each.put("licensePlateNo", order.getAuto().getLicensePlateNo());
            each.put("owner", order.getAuto().getOwner());
            List<DailyInsurance> dailyInsurances = dailyInsuranceRepository.findAllByPurchaseOrderOrderByIdDesc(order);
            Insurance insurance = insuranceRepository.findByQuoteRecordId(order.getObjId());
            each.put("status", DailyInsuranceStatus.getOrderDailyDisplayStatus(insurance, dailyInsurances));
            result.add(each);
        }
        return result;
    }


    @GetMapping("/trial/data")
    public HttpEntity<RestResponseEnvelope> trialData(HttpServletRequest request) {
        return getResponseEntity(null);
    }

    @VersionedResource(from = "1.4")
    @RequestMapping(value = "/bills/{orderNo}", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<Map>> bills(@PathVariable String orderNo) {
        Map dailyInsuranceData = dailyInsuranceService.getOneYearDailyInsuranceData();
        PurchaseOrder order = null;
        if (StringUtils.isNotBlank(orderNo)) {
            order = orderService.checkOrder(orderNo, this.currentUser());
        }
        if (null == order || !dailyInsuranceService.checkOrderHasBills(order)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "该订单停驶账单未生成！");
        }
        return new ResponseEntity<>(new RestResponseEnvelope(dailyInsuranceService.createBills(order, dailyInsuranceData)), HttpStatus.OK);
    }

}
