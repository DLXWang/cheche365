package com.cheche365.cheche.wallet.web.controller;

import com.cheche365.cheche.common.util.HashUtils;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.service.BankService;
import com.cheche365.cheche.core.service.UserService;
import com.cheche365.cheche.core.service.agent.ApproveService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.sms.client.service.ValidatingService;
import com.cheche365.cheche.soopay.payment.withdraw.SoopayWithdrawHandler;
import com.cheche365.cheche.wallet.model.*;
import com.cheche365.cheche.wallet.service.WalletService;
import com.cheche365.cheche.wallet.service.WalletTradeService;
import com.cheche365.cheche.wallet.utils.RandomUitl;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.cheche365.cheche.core.constants.SmsConstants._SMS_PRODUCT_CHECHE;
import static com.cheche365.cheche.core.constants.SmsConstants._SMS_PRODUCT_KEY;
import static com.cheche365.cheche.core.model.agent.ApproveStatus.Enum.NOT_APPROVE_1;


/**
 * Created by mjg on 2017/6/6.
 */
@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/wallet")
@VersionedResource(from = "1.1")
public class WalletResource extends ContextResource {
    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletTradeService tradeService;

    @Autowired
    private BankService bankService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ValidatingService validationCodeService;

    @Autowired
    private SoopayWithdrawHandler withdrawHandler;

    @Autowired
    private UserService userService;
    @Autowired
    private ApproveService approveService;

    private static Logger logger = LoggerFactory.getLogger(WalletResource.class);

    /**
     * 获取当前用户账户信息
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getWalletInfo() {
        Wallet wallet = walletService.queryOrCreateWallet(currentUser(), ClientTypeUtil.getChannel(request));
        Map ret = new HashMap();
        ret.put("walletId", wallet.getId());
        ret.put("balance", wallet.getBalance());
        ret.put("unbalance", wallet.getUnbalance());
        ret.put("hasPwd", (wallet.getPaymentPwd() != null && wallet.getPaymentPwd().length() > 0) ? true : false);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }


    /**
     * 校验支付密码
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/paymentPwd", method = RequestMethod.POST)
    public ResponseEntity<?> paymentPwd(@RequestBody Map<String, String> param) {
        String method = param.get("method");
        String password = param.get("paymentPwd");
        String uuidCode = param.get("uuidCode");

        if (StringUtils.isBlank(method)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "提交method参数不能为空！");
        }
        if (StringUtils.isBlank(password)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "密码不能为空！");
        }
        Wallet wallet = walletService.queryByUserIdAndChannel(currentUser().getId(), ClientTypeUtil.getChannel(request));
        if (wallet == null) {
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "查询钱包账户错误！");
        }

        if (method.equals("valid")) {
            if (this.validPayPwd(wallet, password)) {
                String uuid = UUID.randomUUID().toString();
                CacheUtil.putValueWithExpire(redisTemplate, "wallet.paymentPwd.setPwd.lock." + uuid, true, 10, TimeUnit.MINUTES);
                Map ret = new HashMap();
                ret.put("uuidCode", uuid);
                return new ResponseEntity<>(ret, HttpStatus.OK);
            }
        } else if (method.equals("hasPwd")) {
            if (this.isSetPayPwd(wallet)) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(false, HttpStatus.OK);
            }
        } else if (method.equals("set")) {
            if (this.isSetPayPwd(wallet)) {
                if (StringUtils.isBlank(uuidCode)) {
                    throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "密码设置操作未授权！");
                }
                if (!CacheUtil.hasKey(redisTemplate, "wallet.paymentPwd.setPwd.lock." + uuidCode)) {
                    throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "设置支付密码校验超时！");
                }
                CacheUtil.putValueWithExpire(redisTemplate, "wallet.paymentPwd.setPwd.lock." + uuidCode, true, 1, TimeUnit.SECONDS);
            }
            return this.setPayPwd(wallet, password);
        } else {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "提交method参数信息错误！");
        }
        return new ResponseEntity(false, HttpStatus.OK);
    }


    /**
     * 校验支付密码
     *
     * @return
     */
    public boolean validPayPwd(Wallet wallet, String password) {
        if (StringUtils.isBlank(wallet.getPaymentPwd())) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "请先设置支付密码！");
        }
        if (CacheUtil.hasKey(redisTemplate, "wallet.paymentPwd.lock." + wallet.getUserId())) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "账户已冻结！");
        }
        if (wallet.getWrTimes() < 3) {
            if (wallet.getPaymentPwd().equals(HashUtils.MD5(password))) {
                wallet.setWrTimes(0);
                walletService.updateWallet(wallet);
                return true;
            } else {
                wallet.setWrTimes(wallet.getWrTimes() + 1);
                walletService.updateWallet(wallet);
                throw new BusinessException(BusinessException.Code.UNAUTHORIZED_ACCESS, "支付密码错误，请重试！");
            }
        } else {
            CacheUtil.putValueWithExpire(redisTemplate, "wallet.paymentPwd.lock." + wallet.getUserId(), true, 1, TimeUnit.HOURS);
            wallet.setWrTimes(0);
            walletService.updateWallet(wallet);
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "密码错误次数超限，账户已冻结！请一小时后再试！");
        }
    }

    /**
     * 是否设置过支付密码
     */
    public boolean isSetPayPwd(Wallet wallet) {
        return wallet != null && StringUtils.isNotBlank(wallet.getPaymentPwd());
    }

    /**
     * 设置支付密码
     */
    public ResponseEntity<?> setPayPwd(Wallet wallet, String password) {
        wallet.setPaymentPwd(HashUtils.MD5(password));
        wallet.setUpdateTime(new Date());
        wallet.setWrTimes(0);
        boolean isSuccess = walletService.updateWallet(wallet);
        if (CacheUtil.hasKey(redisTemplate, "wallet.paymentPwd.lock." + wallet.getUserId())) {
            CacheUtil.putValueWithExpire(redisTemplate, "wallet.paymentPwd.lock." + wallet.getUserId(), true, 1, TimeUnit.SECONDS);
        }
        if (isSuccess) {
            return new ResponseEntity(true, HttpStatus.OK);
        } else {
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "设置钱包支付密码错误！");
        }
    }

    /**
     * 找回密码短信校验
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/smsCode", method = RequestMethod.POST)
    public ResponseEntity<?> smsCode(@RequestBody Map<String, Object> param) {
        String validCode = String.valueOf(param.get("validCode"));
        if (StringUtils.isBlank(validCode)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "提交验证码不能为空！");
        }
        Map<String, String> additionalParam = new HashMap<>();
        additionalParam.put(_SMS_PRODUCT_KEY, _SMS_PRODUCT_CHECHE);
        validationCodeService.validate(currentUser().getMobile(), validCode, additionalParam);
        String uuid = UUID.randomUUID().toString();
        CacheUtil.putValueWithExpire(redisTemplate, "wallet.paymentPwd.setPwd.lock." + uuid, true, 10, TimeUnit.MINUTES);
        Map ret = new HashMap();
        ret.put("uuidCode", uuid);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }


    /**
     * 钱包提现
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/withdraw", method = RequestMethod.POST)
    public ResponseEntity<?> doWithdraw(HttpServletRequest request, @RequestBody Map<String, Object> param) {

        User user = currentUser();
        Channel channel = ClientTypeUtil.getChannel(request);

        if (Channel.agents().contains(channel)) {
            userService.checkInfo(user);
        }

        String amount = String.valueOf(param.get("amount"));
        String bankCardId = String.valueOf(param.get("bankcardId"));
        String paymentPwd = String.valueOf(param.get("paymentPwd"));
        String validSms = String.valueOf(param.get("validSms"));
        if (StringUtils.isBlank(amount)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "提现金额不能为空！");
        }
        if (StringUtils.isBlank(bankCardId)) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "提现银行卡不能为空！");
        }

        Wallet wallet = walletService.queryOrCreateWallet(user, channel);

        if (!Channel.self().contains(channel) && !channel.isAgentChannel()) {
            if (StringUtils.isBlank(validSms)) {
                throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "短信验证码不能为空！");
            }
            Map<String, String> additionalParam = new HashMap<>();
            additionalParam.put(_SMS_PRODUCT_KEY, _SMS_PRODUCT_CHECHE);
            validationCodeService.validate(user.getMobile(), validSms, additionalParam);
        } else {
            if (StringUtils.isBlank(paymentPwd)) {
                throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "支付密码不能为空！");
            }
            this.validPayPwd(wallet, paymentPwd);

        }

        withdrawValidation(channel, amount, wallet);

        WalletTrade trade = new WalletTrade();
        trade.setTradeFlag(0);
        trade.setAmount(new BigDecimal(amount));
        trade.setBalance(wallet.getBalance().subtract(trade.getAmount()));
        trade.setBankcardId(Long.valueOf(bankCardId));
        trade.setUserId(user.getId());
        trade.setTradeNo(RandomUitl.buildOrderNo("T"));
        trade.setTradeType(WalletTradeSource.Enum.WITHDRAW_3);
        trade.setStatus(channel.isAgentChannel() ? WalletTradeStatus.Enum.CREATE_1 : WalletTradeStatus.Enum.PROCESSING_5);
        trade.setChannel(channel.getId());
        WalletRemitRecord wrr = tradeService.withdraw(trade, wallet, channel, user);

        if (channel.isAgentChannel() && wrr != null) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }

        Map retMap = tradeService.doRemitPay(wrr.getRequestNo(), trade.getTradeNo(), user);
        if (retMap != null && (retMap.get("ret_code").equals("0000") || retMap.get("ret_code").equals("00180021"))) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            if (retMap.get("ret_code").equals("00060700") || retMap.get("ret_code").equals("00060869")) {
                tradeService.updateTradeStatus(retMap, trade.getTradeNo(), wrr.getRequestNo(), user);
            }
            throw new BusinessException(BusinessException.Code.EXTERNAL_SERVICE_ERROR, String.valueOf(retMap.get("ret_msg")));
        }

    }

    private void withdrawValidation(Channel channel, String amount, Wallet wallet) {

        if(Channel.agentLevelChannels().contains(channel) && approveService.caApproveStatus(getCurrentChannelAgent()).equals(NOT_APPROVE_1)){
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED,"未认证不允许体现操作!");
        }

        if (Channel.agentLevelChannels().contains(channel) && new BigDecimal("100").compareTo(wallet.getBalance()) > 0) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "钱包金额不足100，无法提现！");
        }

        //如果所有提现金额大于钱包余额,提示余额不足
        if (wallet.getBalance().compareTo(new BigDecimal(amount)) < 0) {
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "余额不足，交易失败！(" + wallet.getBalance() + ")");
        }
    }

    /**
     * 提现异步通知处理
     *
     * @param request
     */
    @RequestMapping(value = "/withdraw/notice", method = RequestMethod.GET)
    public void doNoticeWD(HttpServletRequest request, HttpServletResponse response) {
        Map notice = request.getParameterMap();
        logger.debug("联动优势回调======" + JSONObject.fromObject(notice));

        Map<String, String> noticeMap = withdrawHandler.callBack(request);
        WalletTrade trade = tradeService.queryTradeByRequestNo(noticeMap.get("order_id"));
        if (trade == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "未找到联动优势交易编号:" + noticeMap.get("order_id"));
        }
        if (!trade.getStatus().getId().equals(WalletTradeStatus.Enum.PROCESSING_5.getId()) && (!noticeMap.get("ret_code").equals("0000") || trade.getStatus().getId().equals(WalletTradeStatus.Enum.FINISHED_2.getId()))) {
            //向联动优势返回response信息
            withdrawHandler.notice2Soopay(noticeMap, response);
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "该订单号【" + noticeMap.get("order_id") + "】对应交易已经处理，忽略当前异步通知！");
        }
        User user = new User();
        user.setId(trade.getUserId());
        if (noticeMap != null && noticeMap.get("ret_code").equals("0000")) {
            tradeService.updateNoticeStatus(noticeMap, trade, WalletTradeStatus.Enum.FINISHED_2, user);
        } else {
            tradeService.updateNoticeStatus(noticeMap, trade, WalletTradeStatus.Enum.FAIL_3, user);
        }
        //向联动优势返回response信息
        withdrawHandler.notice2Soopay(noticeMap, response);

    }

    @RequestMapping(value = "/mer/balance", method = RequestMethod.GET)
    public void getMerBalance(HttpServletRequest request) {
        withdrawHandler.queryMerBalance();
    }


}
