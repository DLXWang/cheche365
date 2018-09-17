package com.cheche365.cheche.wallet.service;


import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.wallet.model.Wallet;
import com.cheche365.cheche.wallet.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Created by mjg on 2017/6/6.
 */
@Service
@Transactional
public class WalletService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WalletRepository walletRepository;

    public Wallet queryOrCreateWallet(User user, Channel channel) {
        Wallet wallet = this.queryByUserIdAndChannel(user.getId(), channel);
        logger.info("by user id ：{},channel id:{},find wallet:{}",user.getId(),channel.getId(), CacheUtil.doJacksonSerialize(wallet));
        if (wallet != null) {
            return wallet;
        }
        wallet = new Wallet();
        wallet.setUserId(user.getId());
        wallet.setMobile(user.getMobile());
        wallet.setChannel(this.getWalletChannel(channel));
        wallet.setBalance(new BigDecimal(0));
        wallet.setUnbalance(new BigDecimal(0));
        wallet.setStatus(1);
        wallet.setCreateTime(new Date());
        return walletRepository.save(wallet);
    }

    public Channel getWalletChannel(Channel channel) {
        return channel.isAgentChannel() ? Channel.findAgentChannel(channel.getParent()) : channel.isPartnerChannel() ? channel.getParent() : null;
    }

    public boolean updateWallet(Wallet wallet) {
        try {
            walletRepository.save(wallet);
            return true;
        } catch (Exception e) {
            logger.error("update wallet error", e);
            return false;
        }
    }

    public Wallet queryByUserIdAndChannel(Long userId, Channel channel) {
        return walletRepository.findByUserIdAndChannel(userId, this.getWalletChannel(channel));
    }

}
