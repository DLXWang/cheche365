package com.cheche365.cheche.operationcenter.web.controller.partner

import com.alibaba.fastjson.JSONObject
import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.InternalUser
import com.cheche365.cheche.core.model.Partner
import com.cheche365.cheche.core.repository.ChannelRepository
import com.cheche365.cheche.core.repository.InternalUserRepository
import com.cheche365.cheche.core.repository.PartnerRepository
import com.cheche365.cheche.core.service.ResourceService
import com.cheche365.cheche.manage.common.model.PartnerActionLogHistory;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel
import com.cheche365.cheche.operationcenter.model.PartnerQuery
import com.cheche365.cheche.operationcenter.service.partner.PartnerActionLogService
import com.cheche365.cheche.operationcenter.web.model.partner.PartnerActionLogModel
import com.mysql.jdbc.StringUtils
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*

import static com.cheche365.cheche.common.util.DateUtils.getDateString;

/**
 * Created by zhangpengcheng on 2018/4/14
 */
@RestController
@RequestMapping("/operationcenter/partners")
public class PartnerActionController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PartnerActionLogService partnerService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private ChannelRepository channelRepository;

    private final static String DATE_FORMAT_TEXT = "yyyyMMddHHmmssSSS";

    @RequestMapping(value = "/getcomment", method = RequestMethod.POST)
    JSONObject getTheComment(@RequestParam(value = "partner")long partner){
         partnerService.getCommentInPartner(partner)
    }

    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    setTheComment(@RequestParam(value = "comment")String comment,@RequestParam(value = "partner")long partner ){
        partnerService.modifyCommentInPartner(comment,partner);
    }


    @RequestMapping(value = "/log", method = RequestMethod.GET)
    DataTablePageViewModel<PartnerActionLogModel> uploadHistoryList(PartnerQuery partnerQuery) {
        Pageable pageable = partnerService.buildPageable(partnerQuery.currentPage, partnerQuery.pageSize, Sort.Direction.DESC, "id");
        Partner partner = partnerRepository.findOne(partnerQuery.partnerId);
        Page<PartnerActionLogHistory> entityPage = null;
        if(!StringUtils.isNullOrEmpty(partnerQuery.operator) && partnerQuery.channel != null) {
            Channel channel=channelRepository.findById(partnerQuery.channel);
            entityPage = partnerService.getActionLogHistory(channel,partner,partnerQuery.operator,pageable)
        } else if(!StringUtils.isNullOrEmpty(partnerQuery.operator) ){
            entityPage = partnerService.getActionLogHistory(null,partner,partnerQuery.operator,pageable)
        } else if(partnerQuery.channel != null){
            Channel channel=channelRepository.findById(partnerQuery.channel);
            entityPage = partnerService.getActionLogHistory(channel,partner,null,pageable)
        } else {
            entityPage = partnerService.getActionLogHistory(null,partner,null,pageable)
        }

        List<PartnerActionLogModel> modelList = entityPage.collect {
            new PartnerActionLogModel(
                id: it.id,
                operationTime: getDateString(it.createTime, DateUtils.DATE_LONGTIME24_PATTERN),
                operationContent: it.operationContent,
                status: it.status == 1 ? "成功" : "失败",
                operator: it.operator.name
            )
        }
        def dataViewModel = new DataTablePageViewModel<>(entityPage.totalElements, entityPage.totalElements, partnerQuery.draw, modelList)
        return dataViewModel
    }

}
