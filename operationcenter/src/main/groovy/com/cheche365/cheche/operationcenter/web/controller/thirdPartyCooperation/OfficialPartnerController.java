package com.cheche365.cheche.operationcenter.web.controller.thirdPartyCooperation;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.Partner;
import com.cheche365.cheche.core.model.ResultModel;
import com.cheche365.cheche.core.repository.PartnerRepository;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.operationcenter.model.OfficialPartnerQuery;
import com.cheche365.cheche.operationcenter.service.partner.PartnerActionLogService;
import com.cheche365.cheche.operationcenter.service.thirdPartyCooperation.OfficialPartnerService;
import com.cheche365.cheche.operationcenter.web.model.DataTablesPageViewModel;
import com.cheche365.cheche.operationcenter.web.model.thirdParty.OfficiaPartnerViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liulu on 2018/4/15.
 * 合作商管理controller
 */
@RestController
@RequestMapping("/operationcenter/thirdParty/officialPartner")
public class OfficialPartnerController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OfficialPartnerService officialPartnerService;

    @Autowired
    private PartnerActionLogService partnerActionLogService;

    @Autowired
    private PartnerRepository partnerRepository;

    /**
     * 根据条件查询合作商
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
    @VisitorPermission("op0101")
    public DataTablesPageViewModel<OfficiaPartnerViewModel> search(OfficialPartnerQuery query) {
        Page<Partner> partnerPage = officialPartnerService.findChannelAgentList(query, officialPartnerService.buildPageable(query.getCurrentPage(), query.getPageSize()));
        PageInfo pageInfo = new PageInfo();
        pageInfo.setTotalElements(partnerPage.getTotalElements());
        pageInfo.setTotalPage(partnerPage.getTotalPages());
        List<OfficiaPartnerViewModel> list = new ArrayList<>();
        OfficiaPartnerViewModel data = null;
        for (Partner partner : partnerPage.getContent()) {
            data =  OfficiaPartnerViewModel.createViewData(partner);
            list.add(data);
        }
        return new DataTablesPageViewModel<>(partnerPage.getTotalElements(), partnerPage.getTotalElements(), query.getDraw(), list);
    }

    /**
     * 添加合作商
     * @param model
     * @param result
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.POST)
    public ResultModel save(@Valid OfficiaPartnerViewModel model, BindingResult result){
        logger.info("add new officiaPartner start...");
        if (result.hasErrors()){
            return new ResultModel(false,"请将信息填写完整");
        }
        officialPartnerService.saveOfficiaPartner(model);
        Partner partner = partnerRepository.findFirstByName(model.getName());
        Map params = new HashMap();
        params.put("comment",null);
        partnerActionLogService.saveChangeInActionLog(partner,params,null);
        return new ResultModel();
    }

    /**
     * 校验合作商名称是否重复
     * @param name
     * @return
     */
    @RequestMapping(value = "/check",method = RequestMethod.GET)
    public boolean checkCode(@RequestParam(value = "name",required = true) String name) {
        if(logger.isDebugEnabled()) {
            logger.debug("check partner name is unique, name:" + name);
        }
        return officialPartnerService.checkPartnerName(name);
    }

}
