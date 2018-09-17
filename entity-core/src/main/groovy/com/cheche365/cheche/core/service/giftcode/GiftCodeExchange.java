package com.cheche365.cheche.core.service.giftcode;

import com.cheche365.cheche.core.model.Gift;
import com.cheche365.cheche.core.model.GiftCode;
import com.cheche365.cheche.core.model.User;

import java.util.List;

/**
 * Created by mahong on 2015/6/26.
 */
public interface GiftCodeExchange {
    List<Gift> exchangeGiftCode(GiftCode giftCode, User user);
}
