package com.cheche365.cheche.marketing.web.controller;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.MarketingRepository;
import com.cheche365.cheche.core.service.QuoteService;
import com.cheche365.cheche.core.service.image.ImgAssembleSrcService;
import com.cheche365.cheche.marketing.service.MarketingService;
import com.cheche365.cheche.marketing.service.MarketingServiceFactory;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/***************************************************************************/
/*                              MarketingResource.java                 */
/*   文   件 名: MarketingResource.java                                  */
/*   模  块： 动态活动运营平台                                                */
/*   功  能:  运营互动同一入口

/*   初始创建:2015/5/11                                            */
/*   版本更新:V1.0                                                         */
/*   版权所有:北京车与车科技有限公司                                       */

/***************************************************************************/


@RestController
@RequestMapping(ContextResource.VERSION_NO + "/marketings")
@VersionedResource(from = "1.0")
public class MarketingResource extends ContextResource {

    private Logger logger = LoggerFactory.getLogger(MarketingResource.class);

    @Autowired
    @Qualifier("marketingService")
    private MarketingService marketingService;

    @Autowired
    private MarketingServiceFactory marketingServiceFactory;

    @Autowired
    private QuoteService quoteService;

    @Autowired
    private MarketingRepository marketingRepository;

    @RequestMapping(method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getActivities() {

        List<Marketing> latestMarketing = marketingRepository.findLatestMarketing();
        return getResponseEntity(latestMarketing);

    }

    /**
     * 双十二参与活动的人员名单
     */
    @RequestMapping(value = "/{code}/attends",method = RequestMethod.GET)
    public HttpEntity attends(@PathVariable(value = "code") String code){

        RestResponseEnvelope envelope = new RestResponseEnvelope(this.getService(code).attends(code));
        return new ResponseEntity<>(envelope, HttpStatus.OK);

    }



    /**
     * 参与活动，以手机号作为用户参与的唯一凭证。适合简单领券活动
     */
    @RequestMapping(value = "/{code}", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope> attendActivity(@PathVariable(value = "code") String code, @RequestBody Map<String, Object> payload, HttpServletRequest request) {

        MarketingService service = this.getService(code);
        Marketing marketing = service.getMarketingByCode(code);
        Channel channel = ClientTypeUtil.getChannel(request);

        //对于需要登录的活动，并且为通过/partner/{partnerCode}/marketing/{marketingCode}进来的会在session中放入一个未绑定的user，所以去掉活动免登录tag的判断
        String mobile = payload.get("mobile") != null && StringUtils.isNotEmpty(payload.get("mobile").toString().trim()) ? payload.get("mobile").toString().trim() : this.currentUser().getMobile();

        try{
            service.preCheck(marketing, mobile, channel);
        } catch (BusinessException e){
            throw e;
        }
        if(request.getSession().getAttribute(WebConstants.SESSION_KEY_PARTNER_UID)!=null){
           payload.put("uid",request.getSession().getAttribute(WebConstants.SESSION_KEY_PARTNER_UID));
        }
        Object result = service.attend(marketing, this.safeGetCurrentUser(), channel, payload);

        return getResponseEntity(result);

    }

    @RequestMapping(value = "/{code}/attendance", method = RequestMethod.GET)
    public HttpEntity isAttended(@PathVariable(value = "code") String code, @RequestParam Map<String,String> params) {

        User user = this.safeGetCurrentUser();
        RestResponseEnvelope envelope = new RestResponseEnvelope(this.getService(code).isAttend(code, user, params));

        return new ResponseEntity<>(envelope, HttpStatus.OK);

    }

    @RequestMapping(value = "/{code}/active", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getIsActive(@PathVariable(value = "code") String code) {
        Marketing marketing = marketingRepository.findFirstByCode(code);

        if (Calendar.getInstance().getTime().after(marketing.getEndDate())) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "活动已结束");
        }
        RestResponseEnvelope envelope = new RestResponseEnvelope(new HashMap());
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }

    @RequestMapping(value = "/{code}/area", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getAreas(@PathVariable(value = "code") String code) {

        RestResponseEnvelope envelope = new RestResponseEnvelope(Area.Enum.ACTIVE_AREAS);
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }


    @RequestMapping(value = "/{area}/marketing", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> listMarketingByArea(@PathVariable Long area, HttpServletRequest httpServletRequest) {
        final Channel channel = ClientTypeUtil.getChannel(httpServletRequest);
        BusinessActivity businessActivity= businessActivity();

        Marketing marketing = marketingService.getAvailableMarketing(channel.getId(), area);
        Object marketingList = null==marketing ? new ArrayList<>() : marketingService.format(marketing, area, channel);
        ImgAssembleSrcService.handleChannelType(marketingList,businessActivity,channel);
        RestResponseEnvelope envelope = new RestResponseEnvelope(marketingList);
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }

    @GetMapping(value = "/{code}/share")
    public HttpEntity<RestResponseEnvelope> share(@PathVariable(value = "code") String code,
                                                  @RequestParam Map<String, Object> params) {
        MarketingService service = this.getService(code);
        params.put("user", this.currentUser());
        logger.debug("{} 分享活动回调query string:{}", this.currentUser().getMobile(), request.getQueryString());
        RestResponseEnvelope envelope = new RestResponseEnvelope(service.shareCallback(params));
        return new ResponseEntity<>(envelope, HttpStatus.OK);

    }

    @RequestMapping(value = "active", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<?>> activeMarketing(@RequestParam(value = "quoteRecordId") Long quoteRecordId, HttpServletRequest httpServletRequest) {
        final Channel channel = ClientTypeUtil.getChannel(httpServletRequest);
        List<Marketing> marketingList = marketingService.findByQR(quoteRecordId) ;
        setMarketingDetailUrl(marketingList, channel, (quoteRecordId != null && quoteRecordId > 0) ? quoteService.getById(quoteRecordId) : null);
        RestResponseEnvelope envelope = new RestResponseEnvelope(marketingList);
        envelope.setDebugMessage("查询营销活动成功");
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }

    private void setMarketingDetailUrl(List<Marketing> marketingList, Channel channel, QuoteRecord quoteRecord) {
        marketingList.forEach(marketing -> marketing.setDetailUrl(marketingService.marketingDetailUrl(marketing, channel, quoteRecord)));
    }

    private MarketingService getService(String code) {
        return marketingServiceFactory.getService(code);
    }

}
