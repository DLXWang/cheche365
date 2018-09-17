package com.cheche365.cheche.ordercenter.web.controller.resource

import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.service.IInternalUserService
import com.cheche365.cheche.core.service.InstitutionService
import com.cheche365.cheche.core.service.InsuranceCompanyService
import com.cheche365.cheche.core.service.RoleService
import com.cheche365.cheche.manage.common.model.TelMarketingCenterStatus
import com.cheche365.cheche.ordercenter.constants.TelMarketingCenterType
import com.cheche365.cheche.ordercenter.service.resource.*
import com.cheche365.cheche.ordercenter.web.model.InternalUserData
import com.cheche365.cheche.ordercenter.web.model.agent.AgentViewData
import com.cheche365.cheche.ordercenter.web.model.area.AreaViewData
import com.cheche365.cheche.ordercenter.web.model.channel.ChannelViewData
import com.cheche365.cheche.ordercenter.web.model.order.BusinessActivityViewModel
import com.cheche365.cheche.ordercenter.web.model.user.InternalUserViewData
import com.cheche365.cheche.ordercenter.web.model.user.RoleViewModel
import com.cheche365.cheche.ordercenter.web.model.vip.VipCompanyViewData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


/**
 * Created by wangfei on 2015/6/8.
 */
@RestController
@RequestMapping("/orderCenter/resource")
class ResourceController {

    @Autowired
    private InsuranceCompanyResource insuranceCompanyResource

    @Autowired
    private InternalUserResource internalUserResource

    @Autowired
    private AgentResource agentResource

    @Autowired
    private ChannelResource channelResource

    @Autowired
    private AreaResource areaResource

    @Autowired
    private CPSChannelResource cpsChannelResource

    @Autowired
    private PaymentChannelResource paymentChannelResource

    @Autowired
    private TelMarketingCenterResource telMarketingCenterResource

    @Autowired
    private IInternalUserService internalUserService

    @Autowired
    private RoleResource roleResource

    @Autowired
    private RoleService roleService

    @Autowired
    private InstitutionService institutionService
    @Autowired
    private InsuranceCompanyService companyService

    /**
     * 获取所有保险公司
     *
     * @return
     */
    @RequestMapping(value = "/insuranceCompany/getAllCompanies", method = RequestMethod.GET)
    List<InsuranceCompany> getAllCompanies() {
        return insuranceCompanyResource.listAll()
    }


    /**
     * 获取可用保险公司
     * 此接口被getQuotableCompanies接口代替，不要再调用这个接口
     *
     * @return
     */
    @RequestMapping(value = "/insuranceCompany/getEnableCompanies", method = RequestMethod.GET)
    List<InsuranceCompany> getEnableCompanies() {
        return this.getQuotableCompanies()
    }

    /**
     * 获取可报价的保险公司
     *
     * @return
     */
    @RequestMapping(value = "/insuranceCompany/getQuotableCompanies", method = RequestMethod.GET)
    List<InsuranceCompany> getQuotableCompanies() {
        return insuranceCompanyResource.findQuotableCompanies()
    }

    /**
     * 获取指定城市下的可报价保险公司
     *
     * @return
     */
    @RequestMapping(value = "/insuranceCompany/getQuotableCompaniesByArea", method = RequestMethod.GET)
    List<InsuranceCompany> getQuotableCompaniesByArea(@RequestParam(value = "areaId", required = true) Long areaId) {
        return insuranceCompanyResource.findQuotableCompaniesByArea(areaId)
    }

    /**
     * 获取所有内部用户
     *
     * @return
     */
    @RequestMapping(value = "/internalUser/getAllInternalUsers", method = RequestMethod.GET)
    List<InternalUserViewData> getAllInternalUsers() {
        List<InternalUser> internalUserList = internalUserResource.listAll()
        return internalUserResource.createViewData(internalUserList)
    }

    /**
     * 获取可用内部用户
     *
     * @return
     */
    @RequestMapping(value = "/internalUser/getEnableInternalUsers", method = RequestMethod.GET)
    List<InternalUserViewData> getEnableInternalUsers() {
        List<InternalUser> internalUserList = internalUserResource.findEnableInternalUsers()
        return internalUserResource.createViewData(internalUserList)
    }

    /**
     * 获取所有可用客服
     *
     * @return
     */
    @RequestMapping(value = "/internalUser/getAllEnableCustomers", method = RequestMethod.GET)
    List<InternalUserViewData> getAllEnableCustomers() {
        List<InternalUser> internalUserList = internalUserResource.listAllEnableCustomer()
        return internalUserResource.createViewData(internalUserList)
    }

    /**
     * 获取所有可用内勤
     *
     * @return
     */
    @RequestMapping(value = "/internalUser/internal", method = RequestMethod.GET)
    List<InternalUserViewData> getAllEnableInternal() {
        List<InternalUser> internalUserList = internalUserService.listAllEnableInternal()
        return internalUserResource.createViewData(internalUserList)
    }

    /**
     * 获取所有可用录单员
     *
     * @return
     */
    @RequestMapping(value = "/internalUser/input", method = RequestMethod.GET)
    List<InternalUserViewData> getAllEnableInput() {
        List<InternalUser> internalUserList = internalUserService.listAllEnableInput()
        return internalUserResource.createViewData(internalUserList)
    }

    /**
     * 获取所有可用管理员
     *
     * @return
     */
    @RequestMapping(value = "/internalUser/admin", method = RequestMethod.GET)
    List<InternalUserViewData> getAllEnableAdmin() {
        List<InternalUser> internalUserList = internalUserService.listAllEnableAdmin()
        return internalUserResource.createViewData(internalUserList)
    }

    /**
     * 获取所有客服（包括不可用的）
     *
     * @return
     */
    @RequestMapping(value = "/internalUser/getAllCustomers", method = RequestMethod.GET)
    List<InternalUserViewData> getAllCustomers() {
        List<InternalUser> internalUserList = internalUserResource.listAllCustomer()
        return internalUserResource.createViewData(internalUserList)
    }

    /**
     * 获取所有可用电销专员
     *
     * @return
     */
    @RequestMapping(value = "/internalUser/getAllEnableTelCommissioner", method = RequestMethod.GET)
    List<InternalUserData> getAllEnableTelCommissioner() {
        List<InternalUser> internalUserList = internalUserResource.listAllEnableTelCommissioner()
        return internalUserResource.createInternalUserData(internalUserList)
    }

    /**
     * 获取所有出单状态
     *
     * @return
     */
    @RequestMapping(value = "/orderTransmissionStatus", method = RequestMethod.GET)
    List<OrderTransmissionStatus> getAllStatus() {
        return OrderTransmissionStatus.Enum.getNewStatusList()
    }

    /**
     * 获取所有出单状态
     *
     * @return
     */
    @RequestMapping(value = "/orderTransmissionStatusKeys", method = RequestMethod.GET)
    Map<String, Long> getAllStatusKeys() {
        return OrderTransmissionStatus.Enum.ALL_STATUS_MAP
    }

    /**
     * 获取所有出单状态
     *
     * @return
     */
    @RequestMapping(value = "/orderTransmissionStatus/getAllCooperationStatus", method = RequestMethod.GET)
    List<OrderCooperationStatus> getAllCooperationStatus() {
        return OrderCooperationStatus.Enum.allAvailable()
    }

    /**
     * 获取所有订单状态
     *
     * @return
     */
    @RequestMapping(value = "/orderStatus", method = RequestMethod.GET)
    List<OrderStatus> getAllOrderStatus() {
        return OrderStatus.Enum.orderCenterAllAvailable()
    }


    /**
     * 获取所有代理人
     *
     * @return
     */
    @RequestMapping(value = "/agent/getAllAgents", method = RequestMethod.GET)
    List<AgentViewData> getAllAgents() {
        List<Agent> agentList = agentResource.listAll()
        return agentResource.createViewData(agentList)
    }

    /**
     * 获取所有有效的代理人
     *
     * @return
     */
    @RequestMapping(value = "/agent/getEnableAgents", method = RequestMethod.GET)
    List<AgentViewData> getEnableAgents() {
        List<Agent> agentList = agentResource.listEnable()
        return agentResource.createViewData(agentList)
    }

    /**
     * 按关键词查询有效代理人
     *
     * @return
     */
    @RequestMapping(value = "/agent/getEnableAgentsByKeyword", method = RequestMethod.GET)
    List<AgentViewData> getEnableAgentsByKeyWord(@RequestParam(value = "keyword", required = true) String keyword) {
        List<Agent> agentList = agentResource.listEnableByKeyWord(keyword)
        return agentResource.createViewData(agentList)
    }

    /**
     * 获取所有大客户
     *
     * @return
     */
    @RequestMapping(value = "/vipCompany/getAllVipCompanies", method = RequestMethod.GET)
    List<VipCompanyViewData> getAllVipCompanies() {
        return null
    }


    /**
     * 获取所有渠道
     *
     * @return
     */
    @RequestMapping(value = "/channel/getAllChannels", method = RequestMethod.GET)
    List<ChannelViewData> getAllChannels() {
        List<Channel> channels = channelResource.listAll()
        return ChannelViewData.createViewData(channels)
    }

    /**
     * 获取所有渠道
     *
     * @return
     */
    @RequestMapping(value = "/channel/getAllChannelsEnable", method = RequestMethod.GET)
    List<ChannelViewData> getAllChannelsEnable() {
        List<Channel> channels = channelResource.listAll() - Channel.disables()
        return ChannelViewData.createViewData(channels)
    }

    /**
     * 获取出单中心所有订单渠道
     *
     * @return
     */
    @RequestMapping(value = "/channel/getOrderChannels", method = RequestMethod.GET)
    List<Channel> getOrderChannels() {
        return Channel.orderCenterChannels()
    }

    /**
     * 获取出单中心enable订单渠道
     *
     * @return
     */
    @RequestMapping(value = "/channel/getOrderChannelsEnable", method = RequestMethod.GET)
    List<Channel> getOrderChannelsEnable() {
        return Channel.orderCenterChannels() - Channel.disables()
    }


    /**
     * 根据渠道id获取渠道详情
     *
     * @return 渠道对象
     */
    @RequestMapping(value = "/channel/{id}", method = RequestMethod.GET)
    Channel getChannelById(@PathVariable Long id) {
        return channelResource.findById(id)
    }

    /**
     * 获取所有可用报价区域
     *
     * @return
     */
    @RequestMapping(value = "/areas", method = RequestMethod.GET)
    List<AreaViewData> getAllEnableAreas() {
        List<Area> areas = areaResource.listByCache()
        return areaResource.createViewData(areas)
    }

    /**
     * 按关键字检索城市
     *
     * @param keyWord
     * @return
     */
    @RequestMapping(value = "/areas/getByKeyWord", method = RequestMethod.GET)
    List<AreaViewData> listAreasByKeyWord(@RequestParam(value = "keyword", required = true) String keyWord) {
        List<AreaViewData> areaViewDatas = areaResource.listByKeyWord(keyWord)
        return areaViewDatas
    }

    /**
     * 按关键字检索省市
     *
     * @param keyWord
     * @return
     */
    @RequestMapping(value = "/areas/{keyword}", method = RequestMethod.GET)
    List<AreaViewData> listProvinceByKeyWord(@PathVariable(value = "keyword", required = false) String keyWord,
                                                    @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        List<AreaViewData> areaViewDatas = areaResource.listByProvinceKeyWord(keyWord)
        return areaViewDatas.size() < pageSize ? areaViewDatas : areaViewDatas.subList(0, pageSize)
    }

    /**
     * 获取所有CPS渠道
     *
     * @return
     */
    @RequestMapping(value = "/cps", method = RequestMethod.GET)
    List<BusinessActivityViewModel> getAllCPSChannels() {
        List<BusinessActivity> cpsChannelList = cpsChannelResource.listAll()
        return cpsChannelResource.createViewData(cpsChannelList)
    }


    /**
     * 获取所有支付方式(不包括活动和优惠券的)
     *
     * @return
     */
    @RequestMapping(value = "/paymentChannels", method = RequestMethod.GET)
    List<PaymentChannel> getAllPaymentChannels() {
        return paymentChannelResource.listCustomerPayChannels()
    }

    /**
     * 获取线上支付方式
     *
     * @return
     */
    @RequestMapping(value = "/paymentChannels/online", method = RequestMethod.GET)
    List<PaymentChannel> getOnlinePaymentChannels() {
        return paymentChannelResource.listOnLinePayChannels()
    }

    /**
     * 获取所有的电话营销标记状态
     *
     * @return
     */
    @RequestMapping(value = "/telMarketingStatus", method = RequestMethod.GET)
    List<TelMarketingCenterStatus> getTelMarketingStatus() {
        return telMarketingCenterResource.listAllStatus()
    }

    /**
     * 获取所有的省和直辖市
     *
     * @return
     */
    @RequestMapping(value = "/provinces", method = RequestMethod.GET)
    List<AreaViewData> getProvinces() {
        return areaResource.createAreaViewDataList(areaResource.getprovincesAndDirectCitys())
    }

    /**
     * 获取省下面的市
     *
     * @return
     */
    @RequestMapping(value = "/{province}/cities", method = RequestMethod.GET)
    List<AreaViewData> getCityAreaListByProvinceId(@PathVariable String province) {
        return areaResource.createAreaViewDataList(areaResource.getCityAreaListByProvinceId(province))
    }

    /**
     * 获取市上面的省
     */
    @RequestMapping(value = "/{city}/province", method = RequestMethod.GET)
    AreaViewData getProvinceByCityId(@PathVariable String city) {
        return AreaViewData.createViewModel(areaResource.getProvinceByCity(city))
    }

    /**
     * 获取市下面的区或县
     *
     * @return
     */
    @RequestMapping(value = "/{city}/districts", method = RequestMethod.GET)
    List<AreaViewData> getDistrictAreaListCityId(@PathVariable String city) {
        return areaResource.createAreaViewDataList(areaResource.getDistrictAreaListCityId(city))
    }

    /**
     * 获取所有的电话营销来源
     *
     * @return
     */
    @RequestMapping(value = "/telMarketingSource", method = RequestMethod.GET)
    List<TelMarketingCenterSource> getTelMarketingSource() {
        return telMarketingCenterResource.listAllSource()
    }

    /**
     * 获取电话专员
     *
     * @return
     */
    @RequestMapping(value = "/telMarketingOperator", method = RequestMethod.GET)
    List<InternalUser> getTelMarketingOperator() {
        return telMarketingCenterResource.listAllTelMarketingOperator()
    }

    /**
     * 获取可登陆出单中心的角色
     *
     * @return
     */
    @RequestMapping(value = "/roles/oc/enable", method = RequestMethod.GET)
    List<RoleViewModel> getOrderCenterRoles() {
        return RoleViewModel.createViewModel(roleService.getOrderCenterRoleList(Permission.Enum.ORDER_CENTER))
    }

    /**
     * 获取所有角色
     *
     * @return
     */
    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    List<RoleViewModel> getAllRoles() {
        return RoleViewModel.createViewModel(roleResource.findAll())
    }


    /**
     * 获取所有电销支持的第三方渠道
     *
     * @return
     */
    @RequestMapping(value = "/dataSourceChannel", method = RequestMethod.GET)
    List<Channel> getTelMarketingCenterChannel() {
        return Channel.allPartners()
    }

    /**
     * 获取所有enable可用的电销支持的第三方渠道
     *
     * @return
     */
    @RequestMapping(value = "/dataSourceChannelEnable", method = RequestMethod.GET)
    List<Channel> getTelMarketingCenterChannelEnable() {
        return Channel.allPartners() - Channel.disables()
    }


    /**
     * 根据渠道获取对应的类型
     *
     * @return
     */
    @RequestMapping(value = "/dataSourceType", method = RequestMethod.GET)
    List<TelMarketingCenterType> getTelMarketingCenterType() {
        List<TelMarketingCenterType> telMarketingCenterTypeList = telMarketingCenterResource.getTelMarketingCenterType()
        return telMarketingCenterTypeList
    }

    /**
     * 获取所有的报价类型
     *
     * @return
     */
    @RequestMapping(value = "/orderSourceTypes", method = RequestMethod.GET)
    List<OrderSourceType> getEnableInstitutions() {
        return OrderSourceType.Enum.ALL
    }

    @RequestMapping(value = "/isAgentChannel/{channelId}", method = RequestMethod.GET)
    boolean isAgentChannel(@PathVariable Long channelId) {
        return Channel.toChannel(channelId).isAgentChannel()
    }

    /**
     * 获取所有ToA渠道
     *
     * @return
     */
    @RequestMapping(value = "/channel/getToAChannels", method = RequestMethod.GET)
    List<Channel> getToAChannels() {
        return Channel.agents()
    }

    /**
     * 获取所有自有渠道+出单中心自有
     *
     * @return
     */
    @RequestMapping(value = "/channel/getSelfChannel", method = RequestMethod.GET)
    List<Channel> getSelfChannels() {
        return Channel.orderCenterChannelsAndSelf()
    }

    /**
     *
     * 获取所有enable可用的电销支持的自有渠道
     *
     * @return
     */
    @RequestMapping(value = "/channel/getSelfChannelEnable", method = RequestMethod.GET)
    List<Channel> getSelfChannelsEnable() {
        return Channel.orderCenterChannelsAndSelf() - Channel.disables()
    }

    /**
     * 按关键字检索出单机构
     *
     * @return
     */
    @RequestMapping(value = "/identityTypes", method = RequestMethod.GET)
    List<IdentityType> getIdentityTypeList() {
        return IdentityType.Enum.QUOTE_TYPE
    }


    /**
     * 按关键字检索城市
     *
     * @param keyWord
     * @return
     */
    @RequestMapping(value = "/institution/getByKeyWord", method = RequestMethod.GET)
    List<Institution> institutionByKeyWord(@RequestParam(value = "keyword", required = true) String keyWord) {
        return  institutionService.listByKeyWord(keyWord)
    }

    /**
     * 按关键字检索保险公司
     *
     * @param keyWord
     * @return
     */
    @RequestMapping(value = "/insuranceComp/getByKeyWord", method = RequestMethod.GET)
    List<InsuranceCompany> insureCompByKeyword(@RequestParam(value = "keyword", required = true) String keyWord) {
        return  companyService.listByKeyWord(keyWord)
    }

}
