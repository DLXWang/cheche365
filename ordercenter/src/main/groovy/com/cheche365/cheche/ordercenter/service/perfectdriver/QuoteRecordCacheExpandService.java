package com.cheche365.cheche.ordercenter.service.perfectdriver;

import com.alibaba.fastjson.JSON;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.QuoteModificationService;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.ordercenter.service.quote.DefaultQuoteService;
import com.cheche365.cheche.ordercenter.service.quote.QuotePhoneService;
import com.cheche365.cheche.ordercenter.service.quote.QuotePhotoService;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import com.cheche365.cheche.ordercenter.web.controller.perfectdriver.QuoteRecordCacheController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by wangfei on 2016/3/21.
 */
@Service
public class QuoteRecordCacheExpandService {
    private Logger logger = LoggerFactory.getLogger(QuoteRecordCacheExpandService.class);

    @Autowired
    private QuoteRecordCacheRepository quoteRecordCacheRepository;

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    @Autowired
    private InsuranceCompanyRepository insuranceCompanyRepository;

    @Autowired
    private InsurancePackageRepository insurancePackageRepository;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private QuotePhoneService quotePhoneService;

    @Autowired
    private QuotePhotoService quotePhotoService;

    @Autowired
    private QuoteModificationService quoteModificationService;

    @Autowired
    private QuoteModificationRepository quoteModificationRepository;

    @Autowired
    private AutoRepository autoRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private MoApplicationLogRepository applicationLogMongoRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;


    public QuoteRecordCache getQuoteRecordCache(InsuranceCompany insuranceCompany, int type,
                                                                             QuoteModification quoteModification) {
        return quoteRecordCacheRepository.findFirstByInsuranceCompanyAndTypeAndQuoteModificationOrderByCreateTimeDesc(insuranceCompany,
            type, quoteModification);
    }

    public QuoteRecordCache getQuoteRecordCache(Long insuranceCompanyId, int type, String quoteSource, Long quoteSourceId) {
        OcQuoteSource source = DefaultQuoteService.QuoteSource.formatVal(quoteSource);
        InsuranceCompany insuranceCompany = insuranceCompanyRepository.findOne(insuranceCompanyId);
        QuoteModification quoteModification = quoteModificationService.getByQuoteSourceAndQuoteSourceId(source, quoteSourceId);
        return getQuoteRecordCache(insuranceCompany, type, quoteModification);
    }

    @Transactional
    public void saveOrUpdateModificationQuoteRecordCache(QuoteRecordCache[] quoteRecordCaches) {
        String licensePlateNo = quoteRecordCaches[0].getQuoteRecord().getAuto().getLicensePlateNo();
        InsuranceCompany insuranceCompany = insuranceCompanyRepository.findOne(quoteRecordCaches[0].getInsuranceCompany().getId());
        OcQuoteSource quoteSource = DefaultQuoteService.QuoteSource.formatVal(quoteRecordCaches[0].getStrQuoteSource());

        Long quoteSourceId;
        for (QuoteRecordCache quoteRecordCache : quoteRecordCaches) {
            quoteSourceId = quoteRecordCache.getQuoteModification().getQuoteSourceId();
            quoteRecordCache.setInsuranceCompany(insuranceCompany);
            logger.debug("缓存车牌[{}]数据来源[{}]来源ID[{}]在保险公司[{}]的[{}]报价", licensePlateNo, quoteSource.getDescription(), quoteSourceId,
                insuranceCompany.getName(), QuoteRecordCacheController.quoteRecordCacheType_1 == quoteRecordCache.getType() ? "电销" : "修改");

            //计算uniqueString
            quoteRecordCache.getQuoteModification().getInsurancePackage().calculateUniqueString();
            quoteRecordCache.getQuoteRecord().getInsurancePackage().calculateUniqueString();

            //取最新的修改报价记录作查询  mark: 若不加上quoteModification作查询条件有可能查出好车主的电销报价
            QuoteModification quoteModificationNew = saveOrUpdateModification(quoteSource, quoteSourceId,
                quoteRecordCache.getQuoteModification().getInsurancePackage(), quoteRecordCache, insuranceCompany);

            //旧的缓存记录
            QuoteRecordCache recordCacheOld = getQuoteRecordCache(insuranceCompany, quoteRecordCache.getType(), quoteModificationNew);
            QuoteRecordCache quoteRecordCacheSave;
            if (null == recordCacheOld) {
                logger.debug("车牌[{}]数据来源[{}]来源ID[{}]在保险公司[{}]的quote_recode_cache不存在，新建记录", licensePlateNo, quoteSource.getDescription(),
                    insuranceCompany.getName(), quoteSourceId);
                quoteRecordCacheSave = new QuoteRecordCache();
                quoteRecordCacheSave.setCreateTime(new Date());
                quoteRecordCacheSave.setType(quoteRecordCache.getType());
                quoteRecordCacheSave.setInsuranceCompany(insuranceCompany);
            } else {
                logger.debug("车牌[{}]数据来源[{}]来源ID[{}]在保险公司[{}]的quote_recode_cache已存在，更新记录", licensePlateNo, quoteSource.getDescription(),
                    insuranceCompany.getName(), quoteSourceId);
                quoteRecordCacheSave = recordCacheOld;
            }

            quoteRecordCacheSave.setUpdateTime(new Date());
            quoteRecordCacheSave.setOperator(orderCenterInternalUserManageService.getCurrentInternalUser());
            quoteRecordCacheSave.setPolicyDescription(StringUtils.trimToEmpty(quoteRecordCache.getPolicyDescription()));
            quoteRecordCacheSave.setQuoteModification(quoteModificationNew);

            //todo 保存报价记录quote_record
            quoteRecordCacheSave.setQuoteRecord(saveModificationQuoteRecord(null==recordCacheOld?null:recordCacheOld.getQuoteRecord(),
                quoteRecordCache.getQuoteRecord(), quoteModificationNew, insuranceCompany));

            quoteRecordCacheRepository.save(quoteRecordCacheSave);
        }
    }

    private QuoteModification saveOrUpdateModification(OcQuoteSource quoteSource, Long quoteSourceId, InsurancePackage insurancePackagePd,
                                                       QuoteRecordCache quoteRecordCache, InsuranceCompany insuranceCompany) {
        String licensePlateNo = quoteRecordCache.getQuoteRecord().getAuto().getLicensePlateNo();
        QuoteModification quoteModification = quoteModificationService.getByQuoteSourceAndQuoteSourceId(quoteSource,
            quoteRecordCache.getQuoteModification().getQuoteSourceId());

        if (null == quoteModification) {
            logger.debug("车牌[{}]数据来源[{}]来源ID[{}]的quote_modification不存在，新建记录", licensePlateNo, quoteSource.getDescription(), quoteSourceId);
            return quoteModificationRepository.save(quoteModificationService.createNew(quoteSource, quoteSourceId, saveInsurancePackage(insurancePackagePd),
                quoteRecordCache.getInsuranceCompany()));
        } else {
            boolean isModify = false;

            //todo 套餐发生变化，更新预选套餐
            if (!insurancePackagePd.getUniqueString().equals(quoteModification.getInsurancePackage().getUniqueString())) {
                logger.debug("车牌[{}]数据来源[{}]来源ID[{}]预选套餐发生变更，同步更新quote_modification套餐", licensePlateNo, quoteSource.getDescription(), quoteSourceId);
                quoteModification.setInsurancePackage(saveInsurancePackage(insurancePackagePd));
                isModify = true;
            }

            //todo 增加新的保险公司报价，同步更新预选保险公司
            List<String> companyIds = quoteModificationService.getCompanyIdsAsList(quoteModification);
            if (CollectionUtils.isEmpty(companyIds) || !companyIds.contains(insuranceCompany.getId().toString())) {
                logger.debug("车牌[{}]数据来源[{}]来源ID[{}]预选保险公司发生变更，同步更新quote_modification保险公司，新增保险公司[{}]", licensePlateNo, quoteSource.getDescription(),
                    quoteSourceId, insuranceCompany.getName());
                quoteModification.setInsuranceCompanyIds((StringUtils.isBlank(quoteModification.getInsuranceCompanyIds()) ? "" : quoteModification.getInsuranceCompanyIds()
                    + ",") + insuranceCompany.getId().toString());
                isModify = true;
            }

            if (isModify) {
                quoteModification.setUpdateTime(new Date());
                return quoteModificationRepository.save(quoteModification);
            }

            return quoteModification;
        }
    }

    private QuoteRecord saveModificationQuoteRecord(QuoteRecord quoteRecordOld, QuoteRecord quoteRecordNew, QuoteModification quoteModification,
                                        InsuranceCompany insuranceCompany) {
        String licensePlateNo = quoteRecordNew.getAuto().getLicensePlateNo();

        QuoteRecord quoteRecordSave;
        if (null == quoteRecordOld) {
            quoteRecordSave = quoteRecordNew;
            logger.debug("车牌[{}]数据来源[{}]来源ID[{}]在保险公司[{}]的quote_recode不存在，新建记录", licensePlateNo, quoteModification.getQuoteSource().getDescription(),
                quoteModification.getQuoteSourceId(), insuranceCompany.getName());
            //先设置成一般流程，以后有续保可以再添加
            quoteRecordSave.setQuoteFlowType(QuoteFlowType.Enum.GENERAL);
            quoteRecordSave.setCreateTime(new Date());
            quoteRecordSave.setType(QuoteSource.Enum.TELEMARKETING_3);
            quoteRecordSave.setInsuranceCompany(insuranceCompany);
        } else {
            quoteRecordSave = quoteRecordOld;
            logger.debug("车牌[{}]数据来源[{}]来源ID[{}]在保险公司[{}]的quote_recode已存在，更新记录", licensePlateNo, quoteModification.getQuoteSource().getDescription(),
                quoteModification.getQuoteSourceId(), insuranceCompany.getName());
            String[] contains = new String[] {"compulsoryPremium", "autoTax", "thirdPartyPremium", "thirdPartyAmount", "damagePremium", "damageAmount",
                "theftPremium", "theftAmount", "enginePremium", "driverPremium", "driverAmount", "passengerPremium", "passengerAmount", "spontaneousLossPremium",
                "spontaneousLossAmount", "glassPremium", "scratchPremium", "scratchAmount", "iopTotal","unableFindThirdPartyPremium","designatedRepairShopPremium"};
            BeanUtil.copyPropertiesContain(quoteRecordNew, quoteRecordSave, contains);
        }

        if (Objects.equals(OcQuoteSource.Enum.QUOTE_SOURCE_PHONE.getId(), quoteModification.getQuoteSource().getId())) {
            QuotePhone quotePhone = quotePhoneService.getById(quoteModification.getQuoteSourceId());
            quoteRecordSave.setApplicant(quotePhone.getUser());
            quoteRecordSave.setChannel(quotePhone.getSourceChannel());
        } else if (Objects.equals(OcQuoteSource.Enum.QUOTE_SOURCE_PHOTO.getId(), quoteModification.getQuoteSource().getId())) {
            QuotePhoto quotePhoto = quotePhotoService.findById(quoteModification.getQuoteSourceId());
            quoteRecordSave.setApplicant(quotePhoto.getUser());
            quoteRecordSave.setChannel(quotePhoto.getUserImg().getSourceChannel());
        } else if (Objects.equals(OcQuoteSource.Enum.QUOTE_SOURCE_RECORD.getId(), quoteModification.getQuoteSource().getId())){
            MoApplicationLog log = applicationLogMongoRepository.findById(String.valueOf(quoteModification.getQuoteSourceId()));
            QuoteRecord record =  CacheUtil.doJacksonDeserialize(JSON.toJSONString(log.getLogMessage()), QuoteRecord.class);
            quoteRecordSave.setApplicant(record.getApplicant());
            quoteRecordSave.setChannel(record.getChannel());
        } else if (Objects.equals(OcQuoteSource.Enum.QUOTE_SOURCE_RENEW_INSURANCE.getId(), quoteModification.getQuoteSource().getId())){
            PurchaseOrder order = purchaseOrderRepository.findOne(quoteModification.getQuoteSourceId());
            quoteRecordSave.setApplicant(order.getApplicant());
            quoteRecordSave.setChannel(order.getSourceChannel());
        }
        quoteRecordSave.setArea(areaRepository.findOne(quoteRecordNew.getArea().getId()));
        quoteRecordSave.setAuto(autoRepository.findOne(quoteRecordNew.getAuto().getId()));
        quoteRecordSave.setInsurancePackage(saveInsurancePackage(quoteRecordNew.getInsurancePackage()));
        quoteRecordSave.formatEmptyPremium();
        quoteRecordSave.setPremium(quoteRecordSave.calculatePremium());
        quoteRecordSave.setUpdateTime(new Date());

        return quoteRecordRepository.save(quoteRecordSave);
    }



    private InsurancePackage saveInsurancePackage(InsurancePackage insurancePackage) {
        InsurancePackage old = insurancePackageRepository.findFirstByUniqueString(insurancePackage.getUniqueString());
        if (null != old)
            return old;
        return insurancePackageRepository.save(insurancePackage);
    }

}
