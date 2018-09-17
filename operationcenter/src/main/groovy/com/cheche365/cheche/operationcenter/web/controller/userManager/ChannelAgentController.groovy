package com.cheche365.cheche.operationcenter.web.controller.userManager

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.ResultModel
import com.cheche365.cheche.core.model.agent.AgentInviteCodeArea
import com.cheche365.cheche.core.model.agent.ApproveStatus
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.model.agent.ChecheAgentInviteCode
import com.cheche365.cheche.core.model.agent.ProfessionApprove
import com.cheche365.cheche.core.repository.ApproveStatusRepository
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.repository.ProfessionApproveRepository
import com.cheche365.cheche.core.repository.ShopTypeRepository
import com.cheche365.cheche.core.repository.agent.AgentInviteCodeAreaRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository
import com.cheche365.cheche.core.service.AreaService
import com.cheche365.cheche.manage.common.service.QuoteFlowConfigService
import com.cheche365.cheche.operationcenter.model.ChannelAgentQuery
import com.cheche365.cheche.operationcenter.model.UserManngerQuery
import com.cheche365.cheche.operationcenter.service.resource.AreaResource
import com.cheche365.cheche.operationcenter.service.userManager.ChecheAgentInviteCodeService
import com.cheche365.cheche.operationcenter.service.userManager.UserManagerService
import com.cheche365.cheche.operationcenter.web.model.DataTablesPageViewModel
import com.cheche365.cheche.operationcenter.web.model.userManager.ChannelAgentViewData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*

import javax.validation.Valid
/**
 * created by liulu on 2018/3/16.
 */
@RestController
@RequestMapping("/operationcenter/channleAgent")
class ChannelAgentController {

    @Autowired
    private UserManagerService userManagerService
    @Autowired
    private ChecheAgentInviteCodeService checheAgentInviteCodeService
    @Autowired
    private ShopTypeRepository shopTypeRepository;
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private QuoteFlowConfigService quoteFlowConfigService
    @Autowired
    private AreaResource areaResource
    @Autowired
    private ChannelAgentRepository channelAgentRepository
    @Autowired
    private AgentInviteCodeAreaRepository agentInviteCodeAreaRepository
    @Autowired
    private AreaService areaService
    @Autowired
    private ApproveStatusRepository approveStatusRepository
    @Autowired
    private ProfessionApproveRepository professionApproveRepository


    @RequestMapping(value = "/channelAgentList ", method = RequestMethod.GET)
    DataTablesPageViewModel<ChannelAgentViewData> marketingList(ChannelAgentQuery query) {
        return channelAgentList(query)
    }

    DataTablesPageViewModel<ChannelAgentViewData> channelAgentList(ChannelAgentQuery query) {
        Page<ChannelAgent> inviteCodePage = userManagerService.findChannelAgentList(query, userManagerService.buildPageable(query.getCurrentPage(), query.getPageSize()))
        def viewModelList = inviteCodePage.collect() {
            createViewData(it)
        }
        return new DataTablesPageViewModel<>(inviteCodePage.getTotalElements(), inviteCodePage.getTotalElements(), query.getDraw(), viewModelList)
    }

    /**
     * 创建展示data
     *
     * @param channelAgent
     * @return
     */
    ChannelAgentViewData createViewData(ChannelAgent channelAgent) {
        List<Area> areaNameList = areaRepository.findAreaByChannelAgent(channelAgent.getId())
        ProfessionApprove professionApprove = professionApproveRepository.findByChannelAgent(channelAgent)
        ChecheAgentInviteCode inviteCode = checheAgentInviteCodeService.findByChannelAgentId(channelAgent.id)
        if (!inviteCode) {
            inviteCode = new ChecheAgentInviteCode(channelAgent: channelAgent)
        }
        if (!channelAgent.getParent()) {
            List<Object[]> object = userManagerService.findOrderCountAndTotleAmount(channelAgent.getId())
            if (object.get(0) != null && Integer.parseInt(object.get(0)[1].toString()) != 0) {
                String totalAmount = object.get(0)[0].toString()
                String orderCount = object.get(0)[1].toString()
                return ChannelAgentViewData.createViewModel(inviteCode, null, orderCount, totalAmount, areaNameList,professionApprove)
            } else {
                return ChannelAgentViewData.createViewModel(inviteCode, null, null, null, areaNameList,professionApprove)
            }
        } else {
            //通过channel.parent获取邀请人信息
            ChannelAgent chanAgent = userManagerService.findOne(channelAgent.parent.id)
            String invitePerson = chanAgent.getUser().getName()
            List<Object[]> object = userManagerService.findOrderCountAndTotleAmount(channelAgent.getId())
            if (object.get(0) != null && Integer.parseInt(object.get(0)[1].toString()) != 0) {
                String totalAmount = object.get(0)[0].toString()
                String orderCount = object.get(0)[1].toString()
                return ChannelAgentViewData.createViewModel(inviteCode, invitePerson, orderCount, totalAmount, areaNameList,professionApprove)
            } else {
                return ChannelAgentViewData.createViewModel(inviteCode, invitePerson, null, null, areaNameList,professionApprove)
            }
        }
    }

    /**
     * 获取邀请码
     *
     * @return
     */
    @RequestMapping(value = "/createInvitationCode", method = RequestMethod.GET)
    Map<String, String> createInvitationCode() {
        Map<String, String> map = new HashMap<>()
        String inviteCode = checheAgentInviteCodeService.createInvitationCode(null, null)
        map.put("invite", inviteCode)
        return map
    }

    @RequestMapping(value = "/inviteCode/apply/batch", method = RequestMethod.POST)
    def applyInviteCodeBatch(
        @RequestParam Integer number,
        @RequestParam String applicantName,
        @RequestParam Long channelId, @RequestParam(value = "areaList[]", required = false) String[] areaList) {
        def inviteCodeList = checheAgentInviteCodeService.applyInviteCodeBatch(number, applicantName, channelId, areaList?.toList())
        return inviteCodeList
    }

    @RequestMapping(value = "/channels", method = RequestMethod.GET)
    def getChannels() {
        shopTypeRepository.findAll().collect() {
            [
                id         : it.id,
                description: it.description
            ]
        }
    }

    @RequestMapping(value = "/approveStatusList",method = RequestMethod.GET)
    def getApproveStatusList() {
        approveStatusRepository.findAll().collect() {
            [
                id         : it.id,
                description: it.description
            ]
        }
    }

    @RequestMapping(value = "/resource/channels", method = RequestMethod.GET)
    def getToAChannels() {
        def channels = Channel.levelAgents() - Channel.orderCenterChannels()
        channels.collect {
            [
                id         : it.id,
                description: it.description
            ]
        }
    }

    @RequestMapping(value = "/areaList", method = RequestMethod.GET)
    def areaList(@RequestParam Long id) {
        List<Area> areaNameList;
        ChannelAgent channelAgent = channelAgentRepository.findOne(id)
        areaNameList = areaService.getInviteCodeAreas(channelAgent)
        if (areaNameList == null) {
            areaNameList = quoteFlowConfigService.getProvinceByChannelId(channelAgentRepository.findOne(id).getChannel().getId())
        }
        return getProvinceIdCityMap(areaNameList)
    }

    @RequestMapping(value = "/resource/{channelId}/supportArea", method = RequestMethod.GET)
    def getSupportArea(@PathVariable(value = "channelId") Long channelId) {
        def areaList = quoteFlowConfigService.getProvinceByChannelId(channelId)
        return getProvinceIdCityMap(areaList)
    }

    def getProvinceIdCityMap(List<Area> areaList) {
        def provinceIdCityMap = areaList.groupBy {
            it.id / 10000 as int
        }
        def modelList = provinceIdCityMap.collect { provinceId, cityAreaList ->
            def province = areaResource.findById(provinceId * 10000 as Long)
            [
                provinceId  : province.id,
                provinceName: province.name,
                cityList    : cityAreaList.collect {
                    [
                        cityId  : it.id,
                        cityName: it.name
                    ]
                }
            ]
        }
        modelList
    }

    @RequestMapping(value = "/updateArea", method = RequestMethod.POST)
    ResultModel updateArea(@Valid UserManngerQuery query) {
        List<AgentInviteCodeArea> areaList = agentInviteCodeAreaRepository.findAgentInviteCodeAreaByChannelAgent(query.getId())
        if (areaList.size() > 0) {
            for (int i = 0; i < areaList.size(); i++) {
                agentInviteCodeAreaRepository.delete(areaList[i].getId())
            }
        }
        ChecheAgentInviteCode checheAgentInviteCode = checheAgentInviteCodeService.findByChannelAgentId(query.getId())
        if (query.channelId != null) {
            List<Area> allArea = quoteFlowConfigService.getProvinceByChannelId(query.channelId)
            for (int i = 0; i < allArea.size(); i++) {
                AgentInviteCodeArea agentInviteCodeArea = new AgentInviteCodeArea()
                agentInviteCodeArea.setChecheAgentInviteCode(checheAgentInviteCode)
                agentInviteCodeArea.setArea(allArea[i])
                agentInviteCodeAreaRepository.save(agentInviteCodeArea)
            }
        } else {
            List<Long> cityList = query.getArea().toList()
            for (int i = 0; i < cityList.size() - 1; i++) {
                AgentInviteCodeArea agentInviteCodeArea = new AgentInviteCodeArea()
                agentInviteCodeArea.setChecheAgentInviteCode(checheAgentInviteCode)
                agentInviteCodeArea.setArea(areaRepository.findOne(cityList[i]))
                agentInviteCodeAreaRepository.save(agentInviteCodeArea)
            }
        }

        new ResultModel(true, query.getId().toString())
    }

    @RequestMapping(value = "/selectStatus", method = RequestMethod.GET)
    ResultModel selectStatus(@RequestParam Long id, @RequestParam Boolean status) {
        ChannelAgent channelAgent1 = channelAgentRepository.findOne(id)
        channelAgent1.setDisable(status)
        channelAgent1.setUpdateTime(new Date())
        channelAgentRepository.save(channelAgent1)
        new ResultModel(true, id.toString())
    }

}
