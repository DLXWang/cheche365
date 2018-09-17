package com.cheche365.cheche.operationcenter.service.channelRebate

import com.cheche365.cheche.common.math.NumberUtils
import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.common.util.StringUtil
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.*
import com.cheche365.cheche.core.service.AreaService
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import com.cheche365.cheche.operationcenter.exception.OperationCenterException
import com.cheche365.cheche.operationcenter.service.resource.AreaResource
import com.cheche365.cheche.operationcenter.web.model.channelRebate.ChannelRebateHistoryViewData
import com.cheche365.cheche.operationcenter.web.model.channelRebate.ChannelRebateViewModel
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import javax.transaction.Transactional

/**
 * Created by yinJianBin on 2017/6/13.
 */
@Service
class ChannelRebateManageService {

    @Autowired
    InternalUserManageService internalUserManageService

    @Autowired
    TelMarketingCenterRepository telMarketingCenterRepository

    @Autowired
    ChannelRebateRepository channelRebateRepository

    @Autowired
    ChannelRebateHistoryRepository channelRebateHistoryRepository

    @Autowired
    ChannelRepository channelRepository

    @Autowired
    AreaService areaService

    @Autowired
    AreaRepository areaRepository

    @Autowired
    AreaResource areaResource

    @Autowired
    InsuranceCompanyRepository insuranceCompanyRepository

    @Transactional
    void add(List<ChannelRebateViewModel> channelRebateViewModels) {
        def channelRebateList = []
        def now = new Date()
        channelRebateViewModels.each {
            ChannelRebateViewModel channelRebateViewModel ->
                def channelRebate = StringUtils.isEmpty(channelRebateViewModel.getId()) ? buildAddEntity(channelRebateViewModel, now) : buildUpdateEntity(channelRebateViewModel, now)
                channelRebate.setOperator(internalUserManageService.getCurrentInternalUser())
                channelRebate.setUpdateTime(now)

                channelRebateList << channelRebate
        }

        channelRebateRepository.save(channelRebateList)
    }

    @Transactional
    void save(ChannelRebate channelRebate) {
        channelRebateRepository.save(channelRebate)
    }

    @Transactional
    void batchAdd(ChannelRebateViewModel channelRebateViewModel) {
        def channelIds = channelRebateViewModel.getChannelId().split(',')
        def areaIds = channelRebateViewModel.getAreaId().split(',')
        def insuranceCompanyIds = channelRebateViewModel.getInsuranceCompanyId().split(',')
        def now = new Date()

        def channelRebateList = []
        def channelRebateHistoryList = []
        channelIds.each { channelId ->
            areaIds.each { areaId ->
                insuranceCompanyIds.each { insuranceCompanyId ->
                    channelRebateList << batchSaveSingle(channelRebateViewModel, channelId as Long, areaId as Long, insuranceCompanyId as Long, now, channelRebateHistoryList)
                }
            }
        }
        println channelRebateList.size()
        channelRebateList.size() > 0 && channelRebateRepository.save(channelRebateList)
        channelRebateHistoryList.size() > 0 && channelRebateHistoryRepository.save(channelRebateHistoryList)

    }

    def batchSaveSingle = { ChannelRebateViewModel channelRebateViewModel, channelId, areaId, insuranceCompanyId, now, channelRebateHistoryList ->
        def channel = new Channel()
        channel.setId(channelId)

        def area = new Area()
        area.setId(areaId)

        InsuranceCompany insuranceCompany = new InsuranceCompany()
        insuranceCompany.setId(insuranceCompanyId)

        def existChannelRebate = channelRebateRepository.findByChannelAndAreaAndInsuranceCompany(channelId, areaId, insuranceCompanyId)
        if (!existChannelRebate) {
            ChannelRebate channelRebate = new ChannelRebate()

            channelRebate.setArea(area)
            channelRebate.setChannel(channel)
            channelRebate.setInsuranceCompany(insuranceCompany)
            if (channelRebateViewModel.readyFlag) {
                channelRebate.setOnlyReadyCommercialRebate(NumberUtils.toDouble(channelRebateViewModel.getOnlyReadyCommercialRebate()))
                channelRebate.setOnlyReadyCompulsoryRebate(NumberUtils.toDouble(channelRebateViewModel.getOnlyReadyCompulsoryRebate()))
                channelRebate.setReadyCommercialRebate(NumberUtils.toDouble(channelRebateViewModel.getReadyCommercialRebate()))
                channelRebate.setReadyCompulsoryRebate(NumberUtils.toDouble(channelRebateViewModel.getReadyCompulsoryRebate()))
                channelRebate.setReadyEffectiveDate(channelRebateViewModel.getReadyEffectiveDate())
                channelRebate.setStatus(ChannelRebate.Enum.NOT_EFFECTIVE_0)
            } else {
                channelRebate.setOnlyCommercialRebate(NumberUtils.toDouble(channelRebateViewModel.getOnlyCommercialRebate()))
                channelRebate.setOnlyCompulsoryRebate(NumberUtils.toDouble(channelRebateViewModel.getOnlyCompulsoryRebate()))
                channelRebate.setCommercialRebate(NumberUtils.toDouble(channelRebateViewModel.getCommercialRebate()))
                channelRebate.setCompulsoryRebate(NumberUtils.toDouble(channelRebateViewModel.getCompulsoryRebate()))
                channelRebate.setEffectiveDate(channelRebateViewModel.getEffectiveDate())
                channelRebate.setStatus(ChannelRebate.Enum.EFFECTIVED_1)
            }
            channelRebate.setCreateTime(now)
            channelRebate.setUpdateTime(now)

            channelRebate
        } else {//已经存在,覆盖原来的数据
            if (channelRebateViewModel.readyFlag) {//预生效数据
                existChannelRebate.setOnlyReadyCommercialRebate(NumberUtils.toDouble(channelRebateViewModel.getOnlyReadyCommercialRebate()))
                existChannelRebate.setOnlyReadyCompulsoryRebate(NumberUtils.toDouble(channelRebateViewModel.getOnlyReadyCompulsoryRebate()))
                existChannelRebate.setReadyCommercialRebate(NumberUtils.toDouble(channelRebateViewModel.getReadyCommercialRebate()))
                existChannelRebate.setReadyCompulsoryRebate(NumberUtils.toDouble(channelRebateViewModel.getReadyCompulsoryRebate()))
                existChannelRebate.setReadyEffectiveDate(channelRebateViewModel.getReadyEffectiveDate())
                existChannelRebate.setStatus(existChannelRebate.getStatus())
            } else {//即时生效
                channelRebateHistoryList << createRebateHistory(existChannelRebate, now)//即时生效的数据要记录历史

                existChannelRebate.setOnlyCommercialRebate(NumberUtils.toDouble(channelRebateViewModel.getOnlyCommercialRebate()))
                existChannelRebate.setOnlyCompulsoryRebate(NumberUtils.toDouble(channelRebateViewModel.getOnlyCompulsoryRebate()))
                existChannelRebate.setCommercialRebate(NumberUtils.toDouble(channelRebateViewModel.getCommercialRebate()))
                existChannelRebate.setCompulsoryRebate(NumberUtils.toDouble(channelRebateViewModel.getCompulsoryRebate()))
                existChannelRebate.setEffectiveDate(channelRebateViewModel.getEffectiveDate())
                existChannelRebate.setStatus(ChannelRebate.Enum.EFFECTIVED_1)
                existChannelRebate.setDescription(StringUtil.isNull(channelRebateViewModel.getDescription()) ? existChannelRebate.getDescription() : channelRebateViewModel.getDescription())
            }

            existChannelRebate.setUpdateTime(now)
            existChannelRebate
        }

    }

    def createRebateHistory = { ChannelRebate channelRebate, Date now ->
        def channelRebateHistory = new ChannelRebateHistory()
        channelRebateHistory.setOnlyCommercialRebate(channelRebate.getOnlyCommercialRebate())
        channelRebateHistory.setOnlyCompulsoryRebate(channelRebate.getOnlyCompulsoryRebate())
        channelRebateHistory.setCommercialRebate(channelRebate.getCommercialRebate())
        channelRebateHistory.setCompulsoryRebate(channelRebate.getCompulsoryRebate())
        channelRebateHistory.setEffectiveDate(channelRebate.getEffectiveDate())
        channelRebateHistory.setExpireDate(now)
        channelRebateHistory.setChannelRebate(channelRebate)
        channelRebateHistory.setCreateTime(now)
        channelRebateHistory.setOperator(internalUserManageService.getCurrentInternalUser())
        channelRebateHistory
    }

    ChannelRebate buildAddEntity(ChannelRebateViewModel channelRebateViewModel, now) {
        ChannelRebate channelRebate = new ChannelRebate()

        def area = new Area(id: channelRebateViewModel.getAreaId() as Long)
        channelRebate.setArea(area)

        def channel = new Channel(id: channelRebateViewModel.getChannelId() as Long);
        channelRebate.setChannel(channel)

        def insuranceCompany = new InsuranceCompany(id: channelRebateViewModel.getInsuranceCompanyId() as Long)
        channelRebate.setInsuranceCompany(insuranceCompany)

        def existChannelRebate = channelRebateRepository.findFirstByChannelAndAreaAndInsuranceCompanyAndStatus(channel, area, insuranceCompany, ChannelRebate.Enum.EFFECTIVED_1)
        if (existChannelRebate) {
            throw new OperationCenterException('duplicate',
                    """渠道为[${existChannelRebate.getChannel().getDescription()}] ,
                        地区为[${existChannelRebate.getArea().getName()}] ,
                        保险公司为[${existChannelRebate.getInsuranceCompany().getName()}] 的渠道已经存在,不能重复新增 !""")
        }

        if (channelRebateViewModel.status == ChannelRebate.Enum.EFFECTIVED_1) {
            channelRebate.setOnlyCommercialRebate(NumberUtils.toDouble(channelRebateViewModel.getOnlyCommercialRebate()))
            channelRebate.setOnlyCompulsoryRebate(NumberUtils.toDouble(channelRebateViewModel.getOnlyCompulsoryRebate()))
            channelRebate.setCommercialRebate(NumberUtils.toDouble(channelRebateViewModel.getCommercialRebate()))
            channelRebate.setCompulsoryRebate(NumberUtils.toDouble(channelRebateViewModel.getCompulsoryRebate()))

            channelRebate.setEffectiveDate(now)
            channelRebate.setStatus(ChannelRebate.Enum.EFFECTIVED_1)
        } else {
            channelRebate.setStatus(ChannelRebate.Enum.NOT_EFFECTIVE_0)
        }

        channelRebateViewModel.getOnlyReadyCommercialRebate() && channelRebate.setOnlyReadyCommercialRebate(NumberUtils.toDouble(channelRebateViewModel.getOnlyReadyCommercialRebate()))
        channelRebateViewModel.getOnlyReadyCompulsoryRebate() && channelRebate.setOnlyReadyCompulsoryRebate(NumberUtils.toDouble(channelRebateViewModel.getOnlyReadyCompulsoryRebate()))
        channelRebateViewModel.getReadyCommercialRebate() && channelRebate.setReadyCommercialRebate(NumberUtils.toDouble(channelRebateViewModel.getReadyCommercialRebate()))
        channelRebateViewModel.getReadyCompulsoryRebate() && channelRebate.setReadyCompulsoryRebate(NumberUtils.toDouble(channelRebateViewModel.getReadyCompulsoryRebate()))
        channelRebateViewModel.getReadyEffectiveDate() && channelRebate.setReadyEffectiveDate(channelRebateViewModel.getReadyEffectiveDate())

        channelRebate.setCreateTime(now)
        channelRebate
    }


    void update(List<ChannelRebateViewModel> channelRebateViewModels) {
        def channelRebateList = []
        def now = new Date()
        channelRebateViewModels.each {
            channelRebateViewModel ->
                def channelRebate = buildUpdateEntity(channelRebateViewModel, now)
                channelRebateList << channelRebate
        }

        channelRebateRepository.save(channelRebateList)
    }

    def buildUpdateEntity(ChannelRebateViewModel channelRebateViewModel, now) {
        ChannelRebate channelRebate = channelRebateRepository.findOne(channelRebateViewModel.getId() as Long)

        def onlyCommChangeFlag = channelRebate.getOnlyCommercialRebate() != NumberUtils.toDouble(channelRebateViewModel.getOnlyCommercialRebate())
        def onlyCompChangeFlag = channelRebate.getOnlyCompulsoryRebate() != NumberUtils.toDouble(channelRebateViewModel.getOnlyCompulsoryRebate())
        def commChangeFlag = channelRebate.getCommercialRebate() != NumberUtils.toDouble(channelRebateViewModel.getCommercialRebate())
        def compChangeFlag = channelRebate.getCompulsoryRebate() != NumberUtils.toDouble(channelRebateViewModel.getCompulsoryRebate())
        def changeFlag = onlyCommChangeFlag || onlyCompChangeFlag || commChangeFlag || compChangeFlag
        if (changeFlag) {
            channelRebateHistoryRepository.save(createRebateHistory(channelRebate, now))

            channelRebate.setOnlyCommercialRebate(NumberUtils.toDouble(channelRebateViewModel.getOnlyCommercialRebate()))
            channelRebate.setOnlyCompulsoryRebate(NumberUtils.toDouble(channelRebateViewModel.getOnlyCompulsoryRebate()))
            channelRebate.setCommercialRebate(NumberUtils.toDouble(channelRebateViewModel.getCommercialRebate()))
            channelRebate.setCompulsoryRebate(NumberUtils.toDouble(channelRebateViewModel.getCompulsoryRebate()))

            channelRebate.setStatus(ChannelRebate.Enum.EFFECTIVED_1)
            channelRebate.setEffectiveDate(now)
        }

        if (channelRebateViewModel.getReadyEffectiveDate()) {
            channelRebate.setOnlyReadyCommercialRebate(NumberUtils.toDouble(channelRebateViewModel.getOnlyReadyCommercialRebate()))
            channelRebate.setOnlyReadyCompulsoryRebate(NumberUtils.toDouble(channelRebateViewModel.getOnlyReadyCompulsoryRebate()))
            channelRebate.setReadyCommercialRebate(NumberUtils.toDouble(channelRebateViewModel.getReadyCommercialRebate()))
            channelRebate.setReadyCompulsoryRebate(NumberUtils.toDouble(channelRebateViewModel.getReadyCompulsoryRebate()))
            channelRebate.setReadyEffectiveDate(channelRebateViewModel.getReadyEffectiveDate())
        }

        channelRebate
    }

    void delete(Long channelRebateId) {
        def channelRebate = channelRebateRepository.findOne(channelRebateId)
        channelRebate.setUpdateTime(new Date())
        channelRebate.setStatus(2)

        channelRebateRepository.delete(channelRebate.id)
    }


    Page findPage(ChannelRebateViewModel channelRebateViewModel) {
        Pageable pageable = new PageRequest(channelRebateViewModel.getCurrentPage() - 1, channelRebateViewModel.getPageSize(), Sort.Direction.DESC, "updateTime")
        Page<ChannelRebate> page = channelRebateRepository.findAll(new Specification<ChannelRebate>() {
            @Override
            Predicate toPredicate(Root<ChannelRebate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList()

                channelRebateViewModel.getStatus() && predicateList << cb.equal(root.get("status"), channelRebateViewModel.getStatus() as Integer)
                channelRebateViewModel.getInsuranceCompanyId() && predicateList << (cb.in(root.get('insuranceCompany').get('id'), channelRebateViewModel.getInsuranceCompanyId().split(',')))
                if (channelRebateViewModel.getAreaIds()) {
                    predicateList << cb.in(root.get('area').get('id'), channelRebateViewModel.getAreaIds().split(','))
                } else if (channelRebateViewModel.getProvinceId()) {
                    def province = channelRebateViewModel.getProvinceId() as Long
                    def isDirectCity = Area.isMunicipalityArea(province)
                    def isSpecialCity = isSpecialCity(province);
                    if (isDirectCity || isSpecialCity) {
                        predicateList << cb.in(root.get('area').get('id'), province)
                    } else {
                        List<Area> cityList = areaService.findCityAreaListByProvinceId(channelRebateViewModel.getProvinceId() as String, 2, 3L);
                        CollectionUtils.isNotEmpty(cityList) && predicateList << cb.in(root.get('area'), cityList)
                    }
                }
                def channels = getChannels(channelRebateViewModel)
                CollectionUtils.isEmpty(channels) ? predicateList << (cb.lessThan(root.get('id'), 0L)) : predicateList << (cb.in(root.get('channel'), channels))

                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return query.where(predicates).getRestriction();
            }
        }, pageable)
        page
    }


    static Boolean isSpecialCity(long province) {
        String areaIdStr = String.valueOf(province);
        return areaIdStr.startsWith("71") || areaIdStr.startsWith("81") || areaIdStr.startsWith("82")
    }

    List<InsuranceCompany> getCompanyList() {
        def companyList = channelRebateRepository.getCompanys()
        return companyList;
    }

    Page<ChannelRebateHistory> findHistoryPage(ChannelRebateHistoryViewData channelRebateHistoryViewData) {
        def channelRebateId = channelRebateHistoryViewData.getChannelRebateId()
        Pageable pageable = new PageRequest(channelRebateHistoryViewData.getCurrentPage() - 1, channelRebateHistoryViewData.getPageSize(), Sort.Direction.DESC, "id")
        channelRebateHistoryRepository.findByChannelRebateId(channelRebateId, pageable)
    }

    List<Channel> getChannels(ChannelRebateViewModel formData) {
        String clientType = formData.getClientType()
        String channelType = formData.getChannelType()
        def resultList = []
        if (formData.getChannelId()) {
            List<String> channelIds = formData.getChannelId().split(',')
            resultList.addAll(channelRepository.findByIds(channelIds))
            return resultList
        } else if (!clientType && !channelType) {
            resultList = channelRepository.findAll()
            return resultList
        } else if (clientType && !channelType) {
            clientType = clientType == '1' ? 'toA' : 'toC'
            if (clientType == 'toA') {
                resultList = Channel.agents()
            } else {//toC
                def allList = channelRepository.findAll()
                resultList = allList - Channel.agents()
            }
            return resultList
        } else if (!clientType && channelType) {
            def isPartner = channelType == '2'
            if (isPartner) {
                resultList = Channel.thirdPartnerChannels()
            } else {
                resultList = Channel.self()
            }
            return resultList
        } else if (clientType && channelType) {
            def isPartner = channelType == '2'
            if (isPartner) {//第三方
                if (clientType == '1') {//toA
                    def allList = channelRepository.findAll()
                    resultList = Channel.thirdPartnerChannels() - (allList - Channel.agents())
                } else {
                    resultList = Channel.thirdPartnerChannels() - Channel.agents()
                }
                return resultList
            } else {//自有渠道
                if (clientType == '1') {//toA
                    def allList = channelRepository.findAll()
                    resultList = Channel.self() - (allList - Channel.agents())
                } else {//toC
                    resultList = Channel.self() - Channel.agents()
                }
                return resultList
            }
        }
        return resultList;
    }

    /**
     * 获取省下面的市
     *
     * @param province
     * @return
     */
    public List<Area> getCityAreaListByProvinceId(String province) {
        if (org.apache.commons.lang.StringUtils.isNotBlank(province)) {
            def isDirectCity = Area.isMunicipalityArea(province as Long)
            def isSpecialCity = isSpecialCity(province as Long);
            if (isDirectCity || isSpecialCity) {
                def area = areaRepository.findOne(province as Long)
                return [area]
            }
            Long childType = (AreaService.BEIJING_CODE.equals(province) || AreaService.TIANJIN_CODE.equals(province) || AreaService.SHANGHAI_CODE.equals(province) || AreaService.CHONGQING_CODE.equals(province)) ? 4L : 3L;
            List<Area> provinceAreaList = areaService.findCityAreaListByProvinceId(province, 2, childType);
            return provinceAreaList;
        }
        return null;
    }

    ChannelRebate findById(Long id) {
        return channelRebateRepository.findOne(id)
    }

    /**
     * @param dataList
     */
    @Transactional
    List<ChannelRebateViewModel> importDatas(List<List<String>> dataList) {
        List<ChannelRebateViewModel> viewList = new ArrayList<>()
        def channelRebateList = []
        def channelRebateHistoryList = []
        for (List<String> rowList : dataList) {
            ChannelRebateViewModel model = new ChannelRebateViewModel()
            def insetArr = ["clientType", "channelType", "channelName", "areaName", "insuranceCompanyName", "effectiveDateStr",
                            "onlyCommercialRebate", "onlyCompulsoryRebate", "commercialRebate", "compulsoryRebate", "readyEffectiveDateStr",
                            "onlyReadyCommercialRebate", "onlyReadyCompulsoryRebate", "readyCommercialRebate", "readyCompulsoryRebate", "description"]

            for (int i = 0; i < insetArr.size(); i++) {
                model."${insetArr[i]}" = rowList[i]
            }

            if (DateUtils.compareSameDate(model.effectiveDate, new Date())) {//如果时间选中的是当天,即时生效,readyFlag=false
                model.readyFlag = false
            }
            //装入rebate 如果出现格式不对则装入viewModel进viewList
            Channel channel = Channel.findByDescription(model.getChannelName())
            if (channel == null) {
                model.setExcelErr("渠道名称错误")
                viewList.add(model)
                continue
            }

            Area area = Area.Enum.findByName(model.getAreaName())
            if (area == null) {
                model.setExcelErr("城市名称错误")
                viewList.add(model)
                continue
            }

            InsuranceCompany company = InsuranceCompany.findByName(model.getInsuranceCompanyName())
            if (company == null) {
                model.setExcelErr("保险公司错误")
                viewList.add(model)
                continue
            }
            if (model.getEffectiveDate() == null) {
                model.setExcelErr("时间格式有误")
                viewList.add(model)
                continue
            }

            channelRebateList << batchSaveSingle(model, channel.getId() as Long, area.getId() as Long, company.getId() as Long, new Date(), channelRebateHistoryList)
        }
        channelRebateList.size() > 0 && channelRebateRepository.save(channelRebateList)
        channelRebateHistoryList.size() > 0 && channelRebateHistoryRepository.save(channelRebateHistoryList)
        return viewList
    }
}
