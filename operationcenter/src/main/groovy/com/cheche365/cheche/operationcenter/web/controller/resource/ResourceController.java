package com.cheche365.cheche.operationcenter.web.controller.resource;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.ChannelRepository;
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository;
import com.cheche365.cheche.core.service.AreaService;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.manage.common.jsonfilter.RestResponseEnvelope;
import com.cheche365.cheche.manage.common.service.sms.FilterUserService;
import com.cheche365.cheche.manage.common.service.sms.SqlTemplateResource;
import com.cheche365.cheche.manage.common.web.model.MarketingViewModel;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.manage.common.web.model.sms.FilterUserViewModel;
import com.cheche365.cheche.manage.common.web.model.sms.MessageVariableViewModel;
import com.cheche365.cheche.manage.common.web.model.sms.SmsTemplateViewModel;
import com.cheche365.cheche.manage.common.web.model.sms.SqlTemplateViewModel;
import com.cheche365.cheche.operationcenter.service.resource.*;
import com.cheche365.cheche.operationcenter.web.model.area.AreaViewData;
import com.cheche365.cheche.operationcenter.web.model.partner.*;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cheche365.cheche.core.model.InsuranceCompany.allCompanies;

/**
 * Created by wangfei on 2015/6/8.
 */
@RestController
@RequestMapping("/operationcenter/resource")
public class ResourceController {

    @Autowired
    private InternalUserResource internalUserResource;

    @Autowired
    private AreaResource areaResource;

    @Autowired
    private PartnerResource partnerResource;

    @Autowired
    private CooperationModeResource cooperationModeResource;

    @Autowired
    private MonitorDataTypeResource monitorDataTypeResource;

    @Autowired
    private ArithmeticOperatorResource arithmeticOperatorResource;

    @Autowired
    private PartnerTypeResource partnerTypeResource;

    @Autowired
    private PaymentChannelResource paymentChannelResource;

    @Autowired
    @Qualifier("opMarketingResource")
    private MarketingResource marketingResource;

    @Autowired
    private SqlTemplateResource sqlTemplateResource;

    @Autowired
    private FilterUserResource filterUserResource;

    @Autowired
    private FilterUserService filterUserService;

    @Autowired
    private MessageVariableResource messageVariableResource;

    @Autowired
    private SmsTemplateResource smsTemplateResource;

    @Autowired
    private ScheduleConditionResource scheduleConditionResource;

    @Autowired
    private AreaService areaService;


    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private InsuranceCompanyRepository insuranceCompanyRepository;


    /**
     * 获取可用内部用户
     *
     * @return
     */
    @RequestMapping(value = "/internalUsers", method = RequestMethod.GET)
    public List<InternalUserViewData> getEnableInternalUsers() {
        List<InternalUser> internalUserList = internalUserResource.findEnableInternalUsers();
        return internalUserResource.createViewData(internalUserList);
    }

    /**
     * 获取所有可用客服
     *
     * @return
     */
    @RequestMapping(value = "/customers", method = RequestMethod.GET)
    public List<InternalUserViewData> getAllEnableCustomers() {
        List<InternalUser> internalUserList = internalUserResource.listAllEnableCustomer();
        return internalUserResource.createViewData(internalUserList);
    }


//    /**
//     * 获取所有可用报价区域
//     * @return
//     */
//    @RequestMapping(value = "/quoteAreas", method = RequestMethod.GET)
//    public List<AreaViewData> getAllEnableQuoteAreas() {
//        List<Area> areas = areaResource.getAllEnableQuoteAreas();
//        return areaResource.createViewData(areas);
//    }

    /**
     * 按关键字检索城市
     *
     * @param keyWord
     * @return
     */
    @RequestMapping(value = "/areas/getByKeyWord", method = RequestMethod.GET)
    public List<AreaViewData> listAreasByKeyWord(@RequestParam(value = "keyword", required = true) String keyWord) {
        List<AreaViewData> areaViewDatas = areaResource.listByKeyWord(keyWord);
        return areaViewDatas;
    }

    /**
     * 获取所有可用报价区域
     *
     * @return
     */
    @RequestMapping(value = "/quoteAreas", method = RequestMethod.GET)
    public List<AreaViewData> getAllEnableAreas() {
        List<Area> areas = areaResource.getAllEnableAreas();
        return areaResource.createViewData(areas);
    }

    /**
     * 获取所有合作商
     *
     * @return
     */
    @RequestMapping(value = "/partners", method = RequestMethod.GET)
    public List<PartnerViewModel> getAllPartners() {
        List<Partner> partnerList = partnerResource.getAllEnablePartners();
        return partnerResource.createViewData(partnerList);
    }

    /**
     * 获取可显示基础监控类型
     *
     * @return
     */
    @RequestMapping(value = "/monitorDataTypes/enable", method = RequestMethod.GET)
    public List<MonitorDataTypeViewModel> getEnableMonitorDataTypes() {
        List<MonitorDataType> monitorDataTypeList = monitorDataTypeResource.listEnable();
        return monitorDataTypeResource.createViewData(monitorDataTypeList);
    }

    /**
     * 获取所有基础监控类型
     *
     * @return
     */
    @RequestMapping(value = "/monitorDataTypes", method = RequestMethod.GET)
    public List<MonitorDataTypeViewModel> getAllMonitorDataTypes() {
        List<MonitorDataType> monitorDataTypeList = monitorDataTypeResource.listAll();
        return monitorDataTypeResource.createViewData(monitorDataTypeList);
    }

    /**
     * 获取所有运算符
     *
     * @return
     */
    @RequestMapping(value = "/operators", method = RequestMethod.GET)
    public List<ArithmeticOperatorViewModel> getAllArithmeticOperators() {
        List<ArithmeticOperator> arithmeticOperatorList = arithmeticOperatorResource.listAll();
        return arithmeticOperatorResource.createViewData(arithmeticOperatorList);
    }

    /**
     * 获取所有合作方式
     *
     * @return
     */
    @RequestMapping(value = "/cooperationModes", method = RequestMethod.GET)
    public List<CooperationModeViewModel> getAllCooperationModes() {
        List<CooperationMode> cooperationModeList = cooperationModeResource.listAllExcludeMarketing();
        return cooperationModeResource.createViewData(cooperationModeList);
    }

    /**
     * 获取所有合作商类型
     *
     * @return
     */
    @RequestMapping(value = "/partnerTypes", method = RequestMethod.GET)
    public List<PartnerTypeViewModel> getAllPartnerTypes() {
        List<PartnerType> partnerTypeList = partnerTypeResource.listAll();
        return partnerTypeResource.createViewData(partnerTypeList);
    }

    /**
     * 获取可用的支付方式
     *
     * @return
     */
    @RequestMapping(value = "/paymentChannels/enable", method = RequestMethod.GET)
    public List<PaymentChannelViewModel> getEnablePaymentChannels() {
        List<PaymentChannel> paymentChannelList = paymentChannelResource.listEnable();
        return paymentChannelResource.createViewData(paymentChannelList);
    }

    /**
     * 获取所有的推广活动
     *
     * @return
     */
    @RequestMapping(value = "/marketing", method = RequestMethod.GET)
    public List<MarketingViewModel> getAllMarketing() {
        List<Marketing> paymentChannelList = marketingResource.listAll();
        return marketingResource.createViewData(paymentChannelList);
    }

    /**
     * 获取可用的推广活动
     *
     * @return
     */
    @RequestMapping(value = "/marketing/enable", method = RequestMethod.GET)
    public List<MarketingViewModel> getEnableMarketing(@RequestParam(value = "marketingType", required = true) String marketingType) {
        List<Marketing> marketingList = marketingResource.listEnable(marketingType);
        return marketingResource.createViewData(marketingList);
    }

    /**
     * 获取所有Sql模板
     *
     * @return
     */
    @RequestMapping(value = "/sqlTemplates", method = RequestMethod.GET)
    public List<SqlTemplateViewModel> getAllSqlTemplates() {
        return sqlTemplateResource.getAllSqlTemplates();
    }

    /**
     * 获取Sql模板
     *
     * @return
     */
    @RequestMapping(value = "/sqlTemplate/{sqlTemplateId}", method = RequestMethod.GET)
    public SqlTemplateViewModel getSqlTemplate(@PathVariable Long sqlTemplateId) {
        return sqlTemplateResource.getSqlTemplate(sqlTemplateId);
    }

    /**
     * 获取所有可用的筛选用户功能
     *
     * @return
     */
    @RequestMapping(value = "/filterUsers", method = RequestMethod.GET)
    public List<FilterUserViewModel> getAllEnableFilterUser() {
        return filterUserResource.getAllEnableFilterUser();
    }

    /**
     * 获取发送短信用户组的发送数量
     */
    @RequestMapping(value = "/{filterUserId}/filterUsersCount", method = RequestMethod.GET)
    public Integer getAllEnableFilterUserCount(@PathVariable Long filterUserId) {
        return filterUserService.getFilterUserCount(filterUserId);
    }

    /**
     * 获取所有短信变量
     *
     * @return
     */
    @RequestMapping(value = "/messageVariables", method = RequestMethod.GET)
    public List<MessageVariableViewModel> getAllMessageVariables() {
        List<MessageVariable> messageVariableList = messageVariableResource.listAll();
        return messageVariableResource.createViewData(messageVariableList);
    }

    /**
     * 根据条件查询短信模板
     *
     * @param currentPage
     * @param pageSize
     * @param keyword
     * @return
     */
    @RequestMapping(value = "/smsTemplate", method = RequestMethod.GET)
    public PageViewModel<SmsTemplateViewModel> list(@RequestParam(value = "currentPage", required = true) Integer currentPage,
                                                    @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                    @RequestParam(value = "keyword", required = false) String keyword) {
        if (currentPage == null || currentPage < 1) {
            throw new FieldValidtorException("list smsTemplate, currentPage can not be null or less than 1");
        }

        if (pageSize == null || pageSize < 1) {
            throw new FieldValidtorException("list smsTemplate, pageSize can not be null or less than 1");
        }

        return smsTemplateResource.list(currentPage, pageSize, keyword);
    }

    /**
     * 获取触发短信的条件
     *
     * @return
     */
    @RequestMapping(value = "/scheduleCondition", method = RequestMethod.GET)
    public List<ScheduleConditionViewModel> getScheduleCondition() {
        return scheduleConditionResource.createViewData();
    }

    /**
     * 获取短信参数
     */
    @RequestMapping(value = "/getMessageVariable", method = RequestMethod.GET)
    public List<MessageVariableViewModel> getMessageVariable() {
        return smsTemplateResource.getMessageVariable();
    }

    /**
     * 获取所有的省
     *
     * @return
     */
    @RequestMapping(value = "/provinces", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<List>> getProvinces() {
        List provinceList = areaService.findProvinceAreaList(Lists.newArrayList(1L, 2L, 5L));
        return new ResponseEntity<>(new RestResponseEnvelope(provinceList), HttpStatus.OK);
    }

    /**
     * 获取省下面的市
     *
     * @param province
     * @return
     */
    @RequestMapping(value = "/{province}/cities", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<List<Area>>> getCityAreaListByProvinceId(@PathVariable String province) {
        RestResponseEnvelope envelope = new RestResponseEnvelope("");
        List<Area> provinceAreaList = areaService.findCityAreaListByProvinceId(province, 2, 3L);
        envelope = new RestResponseEnvelope(provinceAreaList);
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }

    /**
     * 获取市下面的区或县
     *
     * @param city
     * @return
     */
    @RequestMapping(value = "/{city}/districts", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<List<Area>>> getDistrictAreaListCityId(@PathVariable String city) {
        RestResponseEnvelope envelope = new RestResponseEnvelope("");
        int endPosition = (AreaService.BEIJING_CODE.equals(city) || AreaService.TIANJIN_CODE.equals(city) || AreaService.SHANGHAI_CODE.equals(city) || AreaService.CHONGQING_CODE.equals(city)) ? 2 : 4;
        List<Area> districtList = areaService.findCityAreaListByProvinceId(city, endPosition, 4L);
        envelope = new RestResponseEnvelope(districtList);
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }

    /**
     * 获取自有渠道
     *
     * @return
     */
    @RequestMapping(value = "/ownChannels", method = RequestMethod.GET)
    public List<Channel> getOwnChannels() {
        List<Channel> channels = Channel.self();
        return channels;
    }


    /**
     * 获取第三方渠道
     *
     * @return
     */
    @RequestMapping(value = "/partnerChannels", method = RequestMethod.GET)
    public List<Channel> getPartnerChannels() {
        List<Channel> channels = Channel.allPartners();
        return channels;
    }

    /**
     * @return
     */
    @RequestMapping(value = "/insuranceCompanys", method = RequestMethod.GET)
    public List<InsuranceCompany> getAllEnableInsuranceCompanys() {
        List<InsuranceCompany> insuranceCompanyList = InsuranceCompany.quoteAndDisplayCompanies();
        return insuranceCompanyList;
    }

    @RequestMapping(value = "/insuranceCompanyList", method = RequestMethod.GET)
    public List<InsuranceCompany> insuranceCompanyList(){
        return allCompanies();
    }
    @RequestMapping(value = "/channel/apiPartner", method = RequestMethod.GET)
    public List<Channel> getChannelApiPartner(){
        return Channel.allPartners();
    }
    @RequestMapping(value = "/channel/official", method = RequestMethod.GET)
    public List<Channel> getChannelByOfficial(){
        return Channel.self();
    }

    @RequestMapping(value = "/channel/thirdParty", method = RequestMethod.GET)
    public List<Channel> getChannelByThirdParty(){
        return Channel.thirdPartnerChannels();
    }

    @RequestMapping(value = "/channel/all", method = RequestMethod.GET)
    public List<Channel> getChannelByAll(){
        return Channel.allChannels();
    }
    @RequestMapping(value = "/quoteSource", method = RequestMethod.GET)
    public List<QuoteSource> getQuoteSource(){
        return QuoteSource.Enum.ALL;
    }

    @RequestMapping(value = "/channel/ableChannel", method = RequestMethod.GET)
    public List<Channel> getAbleChannel(){return Channel.findActiveChannel();}
}
