package com.cheche365.cheche.operationcenter.web.controller.thirdPartyCooperation;


import com.cheche365.cheche.core.service.ChannelService;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.operationcenter.model.PartnerQuery;
import com.cheche365.cheche.operationcenter.service.partner.PartnerService;
import com.cheche365.cheche.operationcenter.service.thirdPartyCooperation.CooperateCommonService;
import com.cheche365.cheche.operationcenter.service.thirdPartyCooperation.CooperateService;
import com.cheche365.cheche.operationcenter.web.model.thirdParty.ChannelManagerViewModel;
import com.cheche365.cheche.operationcenter.web.model.thirdParty.TocDetailsViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Created by chenxy on 2018/4/21.
 * ToA合作管理Controller
 */
@RestController
@RequestMapping("/operationcenter/thirdParty/toaCooperate")
public class ToACooperateController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private CooperateCommonService commonService;
    @Autowired
    private CooperateService cooperateService;


    /**
     * toC详情修改
     *
     * @return
     */
    @RequestMapping(value = "/updateDetails", method = RequestMethod.POST)
    public ResultModel update(@Valid PartnerQuery query) {
        query.setPartnerType(PartnerQuery.PartnerType.TOA);
        return commonService.updateChannel(query);
    }

    /**
     * 详情页查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/findDetailsInfo", method = RequestMethod.GET)
    public TocDetailsViewModel findDetailsInfo(@RequestParam(value = "id", required = true) Long id) {
        return cooperateService.findDetailsInfo(id);
    }
    /**
     * toc 列表
     *
     * @param query
     * @return
     */
    @RequestMapping(value = "partnerList", method = RequestMethod.GET)
    public DataTablePageViewModel<ChannelManagerViewModel> partnerList(PartnerQuery query) {
        query.setPartnerType(PartnerQuery.PartnerType.TOA);
        Page<ChannelManagerViewModel> channelList = commonService.findChannelBySpecAndPaginate(query);
        return new DataTablePageViewModel<>(channelList.getTotalElements(), channelList.getTotalElements(), query.getDraw(), channelList.getContent());
    }

    /**
     * 添加
     *
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
        public ResultModel add(@Valid PartnerQuery query) {
        query.setPartnerType(PartnerQuery.PartnerType.TOA);
        return commonService.createChannel(query);
    }

}
