package com.cheche365.cheche.ordercenter.web.controller.gift;

import com.cheche365.cheche.core.model.GiftType;
import com.cheche365.cheche.core.repository.GiftTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by yellow on 2017/11/13.
 */
@RestController
@RequestMapping(value = "/orderCenter/gift")
public class GiftController {
    @Autowired
    private GiftTypeRepository giftTypeRepository;

    /**
     * 额外赠送礼品
     * */
    @RequestMapping(value = "/resendGifts", method = RequestMethod.GET)
    public List<GiftType> resendGifts() {
        return giftTypeRepository.findByCategoryAndDisable(6, false);
    }


}
