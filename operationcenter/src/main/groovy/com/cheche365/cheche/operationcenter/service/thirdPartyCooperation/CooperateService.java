package com.cheche365.cheche.operationcenter.service.thirdPartyCooperation;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.mongodb.repository.MoChannelPageConfigRepository;
import com.cheche365.cheche.core.repository.ApiPartnerRepository;
import com.cheche365.cheche.core.service.ChannelService;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.operationcenter.service.partner.PartnerService;
import com.cheche365.cheche.operationcenter.web.model.thirdParty.TocDetailsViewModel;
import com.cheche365.cheche.operationcenter.web.model.thirdParty.mapToBean.ChannelPageConfigModel;
import com.cheche365.cheche.operationcenter.web.model.thirdParty.mapToBean.ParamsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

@Service
public class CooperateService {

    private static final Logger logger = LoggerFactory.getLogger(CooperateService.class);
    @Autowired
    private ChannelService channelService;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private MoChannelPageConfigRepository moChannelPageConfigRepository;
    @Autowired
    private ApiPartnerRepository apiPartnerRepository;
    @Autowired
    private ChannelFilterService channelFilterService;
    @Autowired
    private ResourceService resourceService;

    public static String SYNC_SIGN_METHOD = "sync.sign.method";


    public TocDetailsViewModel findDetailsInfo(Long id) {
        ParamsModel paramsModel = null;
        ChannelPageConfigModel channelPageConfigModel = null;
        String logoImage = "";
        TocDetailsViewModel tocDetailsViewModel = null;
        String path = resourceService.getResourceAbsolutePath(resourceService.getProperties().getChannelPath());
        //渠道信息
        Channel channel = Channel.toChannel(id);
        if (channel.getIcon() !=null) {
            logoImage = resourceService.absoluteUrl(path,channel.getIcon());
            logger.debug("logoImage =========>>>>>" + logoImage);
        }

        //合作商信息
        Partner partner = partnerService.findOne(channel.getPartner());
        ApiPartner apiPartner = apiPartnerRepository.findOne(channel.getApiPartner().getId());

        //数据是否进电销
        Boolean isTelemarketing = channelFilterService.isEntry(channel);

        //渠道配置部分参数
        Map paramsMap = channelService.getConfig(channel);
        if (null != paramsMap) {
            paramsModel = BeanUtil.mapToBean(paramsMap, new ParamsModel());
        }

        //mongo中前端配置信息
        MoChannelPageConfig config = moChannelPageConfigRepository.findByChannelId(id);
        if (null != config) {
            channelPageConfigModel = BeanUtil.mapToBean(config.getConfig(), new ChannelPageConfigModel());
        }

        ApiPartnerProperties properties = ApiPartnerProperties.findByPartnerAndKey(channel.getApiPartner(), SYNC_SIGN_METHOD);
        tocDetailsViewModel = TocDetailsViewModel.createPageInfo(channel, partner, paramsModel, channelPageConfigModel, apiPartner,properties,isTelemarketing);
        if (new File(path,channel.getIcon()).exists()) {
            tocDetailsViewModel.setLogoImage(logoImage);
        }else {
            tocDetailsViewModel.setLogoImage("");
        }
        return tocDetailsViewModel;
    }

}
