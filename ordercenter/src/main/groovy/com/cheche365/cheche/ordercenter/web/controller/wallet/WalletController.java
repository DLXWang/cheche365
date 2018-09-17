package com.cheche365.cheche.ordercenter.web.controller.wallet;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.repository.ChannelRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.ordercenter.model.WalletQuery;
import com.cheche365.cheche.ordercenter.service.wallet.WalletManagerService;
import com.cheche365.cheche.ordercenter.web.model.user.UserViewModel;
import com.cheche365.cheche.ordercenter.web.model.wallet.WalletViewModel;
import com.cheche365.cheche.wallet.repository.WalletTradeSourceRepository;
import com.cheche365.cheche.wallet.repository.WalletTradeStatusRepository;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by wangshaobin on 2017/5/3.
 */

@RestController
@RequestMapping("/orderCenter/wallet")
public class WalletController {
    private Logger logger = LoggerFactory.getLogger(WalletController.class);

    @Autowired
    private WalletManagerService walletManagerService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private WalletTradeStatusRepository walletTradeStatusRepository;

    @Autowired
    private WalletTradeSourceRepository walletTradeSourceRepository;

    @Autowired
    private ChannelRepository channelRepository;

    /**
     * 钱包列表
     * @param walletQuery
     * @return
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @VisitorPermission("or1101")
    public DataTablePageViewModel<WalletViewModel> list(WalletQuery walletQuery){
        Page<WalletViewModel> page = walletManagerService.findWallets(walletQuery);
        PageInfo pageInfo = baseService.createPageInfo(page);
        return new DataTablePageViewModel<WalletViewModel>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), walletQuery.getDraw(), page.getContent());
    }

    /**
     * 获取钱包列表上方的提现总金额、总余额
     * @param walletQuery
     * @return
     */
    @RequestMapping(value = "/walletAmount",method = RequestMethod.GET)
    public WalletViewModel walletAmount(WalletQuery walletQuery){
        WalletViewModel viewModel = walletManagerService.InAndOutAmount(walletQuery);
        viewModel.setBalance(walletManagerService.balance(walletQuery));
        return viewModel;
    }

    /**
     * 钱包详情页——用户信息
     * @param walletId
     * @return
     */
    @RequestMapping(value = "/userInfo",method = RequestMethod.GET)
    @VisitorPermission("or1101")
    public UserViewModel getWalletUserInfo(@RequestParam(value = "walletId", required = true) Long walletId){
        UserViewModel model = walletManagerService.getWalletUserInfo(walletId);
        return model;
    }

    /**
     * 钱包详情页——银行卡列表
     * @param walletQuery
     * @return
     */
    @RequestMapping(value = "/bankCardList",method = RequestMethod.GET)
    @VisitorPermission("or1101")
    public DataTablePageViewModel<WalletViewModel> bankCardList(WalletQuery walletQuery){
        Page<WalletViewModel> page = walletManagerService.findBankCardList(walletQuery);
        PageInfo pageInfo = baseService.createPageInfo(page);
        return new DataTablePageViewModel<WalletViewModel>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), walletQuery.getDraw(), page.getContent());
    }

    /**
     * 钱包详情页——交易信息列表
     * @param walletQuery
     * @return
     */
    @RequestMapping(value = "/transactionList",method = RequestMethod.GET)
    @VisitorPermission("or1101")
    public DataTablePageViewModel<WalletViewModel> transactionList(WalletQuery walletQuery){
        Page<WalletViewModel> page = walletManagerService.findTransactionList(walletQuery);
        PageInfo pageInfo = baseService.createPageInfo(page);
        return new DataTablePageViewModel<WalletViewModel>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), walletQuery.getDraw(), page.getContent());
    }

    /**
     * 钱包详情页——获取交易信息列表上方的提现总金额、提现金额
     * @param walletQuery
     * @return
     */
    @RequestMapping(value = "/transactionAmount",method = RequestMethod.GET)
    public WalletViewModel transactionAmount(WalletQuery walletQuery){
        return walletManagerService.tradeAmount(walletQuery);
    }

    /**
     * 钱包详情页——获取平台
     *
     * @return
     */
    @RequestMapping(value = "/channel", method = RequestMethod.GET)
    public List<Channel> getChannel() {
        return IteratorUtils.toList(channelRepository.findAll().iterator());
    }

    /**
     * 钱包详情页——获取来源
     *
     * @return
     */
    @RequestMapping(value = "/source", method = RequestMethod.GET)
    public List<Channel> getSource() {
        return IteratorUtils.toList(walletTradeSourceRepository.findAll().iterator());
    }

    /**
     * 钱包详情页——获取状态
     *
     * @return
     */
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public List<Channel> getStatus() {
        return IteratorUtils.toList(walletTradeStatusRepository.findAll().iterator());
    }
}
