package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.ApiPartner;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.InsuranceCompany;
import com.cheche365.cheche.core.model.MoChannelPageConfig;
import com.cheche365.cheche.core.mongodb.repository.MoChannelPageConfigRepository;
import com.cheche365.cheche.core.repository.ApiPartnerRepository;
import com.cheche365.cheche.core.repository.ChannelRepository;
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.counter.annotation.NonProduction;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.version.VersionedResource;
import groovy.json.JsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

import static com.baidu.tool.RsaUtil.encryptByPublicKey;
import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps;
import static com.cheche365.cheche.core.exception.BusinessException.Code.UNAUTHORIZED_ACCESS;
import static com.cheche365.cheche.core.model.ApiPartner.Enum.BDINSUR_PARTNER_50;
import static com.cheche365.cheche.core.model.ApiPartner.findByCode;
import static com.cheche365.cheche.core.model.ApiPartnerProperties.findByPartnerAndKey;
import static com.cheche365.cheche.core.model.Channel.findByApiPartner;
import static com.cheche365.cheche.partner.service.index.BaiduInsurService.CHECHE_PUBLIC_KEY;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;

@RestController
@RequestMapping("/" + ContextResource.VERSION_NO)
@VersionedResource(from = "1.5")
public class ChannelsResource extends ContextResource {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ApiPartnerRepository partnerRepository;

    @Autowired
    private InsuranceCompanyRepository companyRepository;

    @Autowired
    private MoChannelPageConfigRepository channelPageConfigRepository;

    @RequestMapping(value = "/channels/{channelId}/tags", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getTagsByChannelId(@PathVariable Long channelId) {
        Channel channel = channelRepository.findById(channelId);
        return getResponseEntity(Channel.explainTag(channel.getTag()));
    }


    @RequestMapping(value = "/partners/{partnerId}/tags", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getPartnerTagsByChannelId(@PathVariable Long partnerId) {
        ApiPartner partner = partnerRepository.findOne(partnerId);
        return getResponseEntity(ApiPartner.explainTag(partner.getTag()));
    }

    @RequestMapping(value = "/companies/{companyId}/tags", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getCompanyTagsById(@PathVariable Long companyId) {
        InsuranceCompany company = companyRepository.findOne(companyId);
        return getResponseEntity(InsuranceCompany.explainTag(company.getTag()));
    }

    @GetMapping("/channel/{code}/config")
    public HttpEntity<RestResponseEnvelope> getChannelConfig(@PathVariable String code) {
        Channel channel = findChannel(code);
        MoChannelPageConfig channelPageConfig = channelPageConfigRepository.findByChannelId(channel.getId());
        return getResponseEntity(null != channelPageConfig ? channelPageConfig.getConfig() : null);
    }

    @NonProduction
    @PostMapping("/channel/{code}/config")
    public HttpEntity<RestResponseEnvelope> setChannelConfig(@PathVariable String code, @RequestBody @Valid Map body) {
        Channel channel = findChannel(code);
        MoChannelPageConfig channelPageConfig = channelPageConfigRepository.findByChannelId(channel.getId());
        channelPageConfig = null != channelPageConfig ? channelPageConfig : new MoChannelPageConfig();
        channelPageConfig.setChannelId(channel.getId());
        channelPageConfig.setConfig(mergeMaps(channelPageConfig.getConfig(), body));
        channelPageConfigRepository.save(channelPageConfig);
        return getResponseEntity("配置成功！");
    }

    @NonProduction
    @PostMapping("/channel/bdinsur/convert")
    public HttpEntity<RestResponseEnvelope> convertIndexUrl(@RequestBody @Valid Map body) {
        try {
            String encryptData = encryptByPublicKey(new JsonBuilder(body).toString(), findByPartnerAndKey(BDINSUR_PARTNER_50, CHECHE_PUBLIC_KEY).getValue());
            return getResponseEntity(null != encryptData ? getDomainURL() + "/partner/bdinsur/index?param=" + encodeBase64String(encryptData.getBytes("UTF-8")) : "生成url失败！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getResponseEntity("生成url失败！");
    }

    private Channel findChannel(String code) {
        ApiPartner partner = findByCode(code);
        if (null == partner) {
            throw new BusinessException(UNAUTHORIZED_ACCESS, code + " 渠道不存在！");
        }
        return findByApiPartner(partner);
    }

}
