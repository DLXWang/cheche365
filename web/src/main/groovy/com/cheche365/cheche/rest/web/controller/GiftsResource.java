package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.Page;
import com.cheche365.cheche.core.service.GiftService;
import com.cheche365.cheche.core.service.ModuleService;
import com.cheche365.cheche.core.service.QuoteRecordCacheService;
import com.cheche365.cheche.core.service.QuoteRecordService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhengwei on 6/3/15.
 */

@RestController
@RequestMapping("/"+ ContextResource.VERSION_NO+"/gifts")
@VersionedResource(from = "1.0")
public class GiftsResource extends ContextResource {

    private Logger logger = LoggerFactory.getLogger(GiftsResource.class);

    @Autowired
    GiftService giftService;

    @Autowired
    private QuoteRecordCacheService cacheService;


    @Autowired
    private QuoteRecordService quoteRecordService;

    @Autowired
    private ModuleService moduleService;


    @RequestMapping(value = "/exchange", method = RequestMethod.POST)
    @VersionedResource(from = "1.1")
    public HttpEntity<RestResponseEnvelope<List<Gift>>> exchangeGiftCodeFromOneDotOne(@RequestBody @Valid GiftCard giftCard){

        logger.debug("start to exchange gift code ...");
        List<Gift> gifts = this.giftService.exchangeGiftCard(giftCard, this.currentUser());

        logger.debug("finish to exchange gift code ...");
        RestResponseEnvelope<List<Gift>> envelope = new RestResponseEnvelope(gifts);
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }


    @VersionedResource(from = "1.1")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getGifts11(@RequestParam(value = "status", required = false) String status,
                                                       @RequestParam(value = "quoteRecordId", required = false) Long quoteRecordId,
                                                       @RequestParam(value = "quoteRecordKey", required = false) String quoteRecordKey,
                                                       @RequestParam(value = "page", required = false) Integer page,
                                                       @RequestParam(value = "size", required = false) Integer size,
                                                       @RequestParam(value = "type", required = false,defaultValue = "0") Integer type,
                                                       HttpServletRequest request) {


        int start = toPageStart(page);
        int pageSize = toPageSize(size);

        if(type==1){
            logger.debug("start to search the tk_discount ...");
            List displayMessages =(List)moduleService.homeMessages(MessageType.Enum.TK_DISCOUNT.getType(),null,ClientTypeUtil.getChannel(request));
            Page discountFreeShips;
            if (displayMessages!=null&&displayMessages.size()>0){
                discountFreeShips= new Page(start,pageSize,displayMessages.size(),displayMessages);
            }else {
                discountFreeShips= new Page(start,pageSize,0,null);
            }

            HashMap hashMap = CacheUtil.doJacksonDeserialize(CacheUtil.doJacksonSerialize(discountFreeShips),HashMap.class);
            hashMap.put("title","9.9包邮("+discountFreeShips.getTotalElements()+")");
            return new ResponseEntity<>(new RestResponseEnvelope(hashMap),HttpStatus.OK);
        }

        logger.debug("start to search the gift ...");
        Pageable pageable = new PageRequest(start, toPageSize(pageSize));
        Page<Gift> gifts = getGiftPage(status, quoteRecordId, quoteRecordKey, request, pageable);

        logger.debug("finish the search of gift ...");
        if (gifts == null) {
            gifts = new Page<>(start, pageSize, 0, null);
        }
        HashMap hashMap = CacheUtil.doJacksonDeserialize(CacheUtil.doJacksonSerialize(gifts,true),HashMap.class);
        hashMap.put("title","车车车险("+gifts.getTotalElements()+")");
        return new ResponseEntity<>(new RestResponseEnvelope(hashMap),HttpStatus.OK);
    }


    private Page<Gift> getGiftPage(String status,Long quoteRecordId, String quoteRecordKey, HttpServletRequest request, Pageable pageable) {
        List<GiftStatus> targetGiftStatus = GiftStatus.Enum.ALL_VALID_STATUS;
        Page<Gift> gifts;
        if (giftCannotBeUsed()) {
            gifts = null;
        } else if (StringUtils.isBlank(status)) {
            gifts = this.giftService.searchGift(this.currentUser(), targetGiftStatus, pageable, ClientTypeUtil.getChannel(request));
        } else {
            QuoteRecord quoteRecord = (quoteRecordId != null && quoteRecordId > 0) ? quoteRecordService.getById(quoteRecordId) :
                !StringUtils.isBlank(quoteRecordKey) ? this.cacheService.getQuoteRecordByHashKey(quoteRecordKey) :
                    cacheService.getSavedQuoteRecord(request.getSession().getId(), this.currentUser());

            if(quoteRecord==null){
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "缓存中未找到任何报价信息");
            }

            quoteRecord.setChannel(ClientTypeUtil.getChannel(request));
            gifts = this.giftService.findByQR(this.currentUser(), quoteRecord, pageable);
        }
        return gifts;
    }

    private boolean giftCannotBeUsed() {
        User user = this.currentUser();

        if( user != null && UserType.Enum.isAgent(user.getUserType())) {
            return true;
        }

        BusinessActivity cpsBusinessActivity = businessActivity();
        return cpsBusinessActivity != null && (!cpsBusinessActivity.checkActivityDate() || !cpsBusinessActivity.isEnable());
    }

}
