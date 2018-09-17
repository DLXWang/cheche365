package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.exception.handler.LackOfSupplementInfoHandler
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.AutoType
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.FuelType
import com.cheche365.cheche.core.model.IdentityType
import com.cheche365.cheche.core.model.InsuranceBasicInfo
import com.cheche365.cheche.core.model.ParentIdentityType
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.UseCharacter
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.UserAuto
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.core.repository.AutoRepository
import com.cheche365.cheche.core.repository.UserAutoRepository
import com.cheche365.cheche.core.repository.VehicleLicenseRepository
import com.cheche365.cheche.core.util.AutoUtils
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.core.util.PublicCarUtil
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.core.model.Channel.Enum.IOS_4
import static com.cheche365.cheche.core.service.QuoteRecordCacheService.getEncryptionInsuranceBasicInfoKey

/**
 * 车辆查询服务，主要查询车架号，发动机号
 *
 * @author liqiang
 */
@Service
@Transactional
class AutoService {

    private Logger logger = LoggerFactory.getLogger(AutoService.class);

    @Autowired
    private AutoRepository autoRepo;

    @Autowired
    private UserAutoRepository userAutoRepo;

    @Autowired
    private VehicleLicenseRepository vehicleLicenseRepo;

    @Autowired
    private IAutoTypeService autoTypeService;

    @Autowired
    private QuoteRecordCacheService cacheService;

    @Autowired
    private UserAutoService userAutoService;

    @Autowired(required = false)
    @Qualifier("supplementInfoMappings")
    private Map<String, Object> supplementInfoMappings;

    @Autowired(required = false)
    @Qualifier("carModelSupplementInfoMappings")
    private Map<String, Object> carModelSupplementInfoMappings;

    Auto getAutoByUser(Long userId, String licensePlateNo) {
        UserAuto result = userAutoRepo.searchByUserAndPlate(userId, licensePlateNo);
        return result == null || PublicCarUtil.isPublicType(result.auto) ? null : result.getAuto();
    }

    Auto saveAuto(Auto auto) {
        if (null != auto.getAutoType()) {
            AutoType autoType = autoTypeService.saveAutoType(auto.getAutoType());
            auto.setAutoType(autoType);
            logger.debug("save the auto type with id " + auto.getAutoType().getId());
        } else {
            logger.debug("will save the auto without auto type");
        }

        auto.identityType ?: auto.setIdentityType(IdentityType.Enum.IDENTITYCARD);
        Auto autoAfterSave = this.autoRepo.save(auto);
        logger.debug("save the auto with id " + autoAfterSave.getId());

        return autoAfterSave;

    }

    Auto update(UserAuto userAuto, Auto newOne) {

        this.validAreaOfAuto(newOne);
        Auto existedOne = userAuto.getAuto();

        if (hasSameSixElements(existedOne, newOne)) {
            logger.debug("车辆无变化，不保存");
            return existedOne;
        }

        if (existedOne.isBillRelated() || this.autoRepo.countAutoRelatedUser(existedOne.getLicensePlateNo()) > 1) {
            existedOne.setDisable(true);
            Auto afterMerge = this.mergeAuto(existedOne, newOne);
            this.saveAuto(existedOne);

            Auto afterSave = this.saveAuto(afterMerge);
            userAuto.setAuto(afterSave);
            this.userAutoRepo.save(userAuto);

            logger.debug("生成一条新车辆记录，{}", afterSave.id)

            return afterSave;
        } else {
            updateAutoInfo(newOne, existedOne);
            Auto autoSaved = this.saveAuto(existedOne);
            return autoSaved;

        }
    }

    Auto saveOrMerge(Auto newAuto, User user, StringBuilder debugMessage) {
        return saveOrMerge(newAuto, user, true, debugMessage);
    }

    Auto saveOrMerge(Auto newAuto, User user, boolean doSyncVehicleLicense, StringBuilder debugMessage) {
        return saveOrMerge(newAuto, user, doSyncVehicleLicense, debugMessage, true);
    }

    Auto saveOrMerge(Auto newAuto, User user, boolean doSyncVehicleLicense, StringBuilder debugMessage, boolean validArea) {
        if (validArea) validAreaOfAuto(newAuto);//是否校验车辆的区域
        if (doSyncVehicleLicense)
            syncVehicleLicense(newAuto);

        List<Auto> existedAutos = autoRepo.searchByPlateNo(newAuto.getLicensePlateNo());

        if (existedAutos == null || existedAutos.isEmpty()) {
            Auto autoSaved = insertNewAuto(newAuto);
            userAutoService.saveUserAuto(autoSaved, user);
            debugMessage.append("车牌号不存在，新增一条车辆纪录。 ");
            logger.debug("<<<<1111<<<<<<<<<<< {}", autoSaved.getAutoType() != null ? autoSaved.getAutoType().getCode() : null);
            return autoSaved;
        }

        for (Auto existedAuto : existedAutos) {
            if (hasSameSixElements(existedAuto, newAuto)) {
                logger.debug("数据库存在六要素相同的车辆信息，不重复保存");
                userAutoService.saveUserAuto(existedAuto, user);
                debugMessage.append("存在六要素相同的车辆信息，没有更新，使用现有车辆。 ");
                logger.debug("<<<<2222<<<<<<<<<<< {}", existedAuto.getAutoType() != null ? existedAuto.getAutoType().getCode() : null);
                return existedAuto;
            }
        }

        List<Auto> autos = autoRepo.searchByUserAndPlateNo(user.getId(), newAuto.getLicensePlateNo());

        if (autos == null || autos.isEmpty()) {
            Auto autoSaved = insertNewAuto(newAuto);
            userAutoService.saveUserAuto(autoSaved, user);
            debugMessage.append("车牌号已存在，但六要素不同，在当前用户名下新增一条车辆纪录。 ");
            logger.debug("<<<<<3333<<<<<<<<<< {}", autoSaved.getAutoType() != null ? autoSaved.getAutoType().getCode() : null);
            return autoSaved;
        }

        List<Auto> billRelatedAutos = getBillRelatedAutos(autos);
        List<Auto> renewableAutos = getRenewableAutos(autos, newAuto);
        if (billRelatedAutos) {
            autoRepo.save(billRelatedAutos);
            logger.debug("用户{}名下已存在车牌号为{}的车辆，但六要素不一致，disable同车牌的号的车辆。 ", user.getId(), newAuto.getLicensePlateNo());
        }

        List<Auto> autosNotRelatedOther = autoRepo.findAutoNotRelatedOtherUser(user.getId(), newAuto.getLicensePlateNo());
        if (autosNotRelatedOther != null && !autosNotRelatedOther.isEmpty() && newCarUpdatable(autosNotRelatedOther[0], newAuto)) {
            Auto updateAuto = autosNotRelatedOther.get(0);
            updateAutoInfo(newAuto, updateAuto);
            Auto autoSaved = this.saveAuto(updateAuto);
            userAutoService.saveUserAuto(autoSaved, user);  //多余？
            debugMessage.append("车牌号为{}的车辆只有当前用户关联，更新该车辆信息。 ");
            logger.debug("<<<<4444<<<<<<<<<<< {}", autoSaved.getAutoType() != null ? autoSaved.getAutoType().getCode() : null);
            return autoSaved;
        } else if (renewableAutos != null && !renewableAutos.isEmpty()) {
            Auto updateAuto = renewableAutos.get(0);
            Auto autoSaved = insertNewAuto(newAuto);
            UserAuto userAuto = userAutoRepo.findFirstByUserAndAuto(user, updateAuto);
            userAuto.setAuto(autoSaved);
            this.userAutoRepo.save(userAuto);
            debugMessage.append("车牌号为{}的车辆有其他用户关联，新增一条车辆记录。 ");
            logger.debug("<<<<5555<<<<<<<<<<< {}", autoSaved.getAutoType() != null ? autoSaved.getAutoType().getCode() : null);
            return autoSaved;
        } else {
            Auto autoSaved = insertNewAuto(newAuto);
            userAutoService.saveUserAuto(autoSaved, user);
            logger.debug("<<<<<6666<<<<<<<<<< {}", autoSaved.getAutoType() != null ? autoSaved.getAutoType().getCode() : null);
            return autoSaved;
        }
    }

    UserAuto searchUserAuto(Long userId, Long autoId) {
        return this.userAutoRepo.searchByIds(userId, autoId);
    }

    static void validAreaOfAuto(Auto auto) {

        Area actualArea = AutoUtils.getAreaOfAuto(auto.licensePlateNo)
        if (auto.licensePlateNo && actualArea && auto.area != actualArea) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "车牌号与车辆所属区域信息不一致。");
        }

    }

    static List<Auto> getBillRelatedAutos(List<Auto> autos) {
        List<Auto> billRelatedAutos = new ArrayList<>();

        for (Auto auto : autos) {
            if (auto.isBillRelated()) {
                auto.setDisable(true);
                billRelatedAutos.add(auto);
            }
        }

        return billRelatedAutos;
    }

    List<Auto> getRenewableAutos(List<Auto> autos, Auto newAuto) {
        autos.findAll { !it.isBillRelated() && newCarUpdatable(it, newAuto) }
    }

    static newCarUpdatable(Auto auto, Auto newAuto) {
        (
            Auto.NEW_CAR_PLATE_NO != newAuto.licensePlateNo ||
                (
                    Auto.NEW_CAR_PLATE_NO == newAuto.licensePlateNo &&
                        newAuto.licensePlateNo == auto.licensePlateNo &&
                        newAuto.vinNo == auto.vinNo
                )
        )
    }

    private Auto mergeAuto(Auto existedOne, Auto newOne) {
        Auto cloneExisted = new Auto();
        BeanUtils.copyProperties(existedOne, cloneExisted, "id");
        cloneExisted.id = null;
        cloneExisted.disable = false;
        cloneExisted.billRelated = false;
        this.updateAutoInfo(newOne, cloneExisted);
    }

    boolean hasSameSixElements(Auto existedAuto, Auto newAuto) {
        if (needUpdate(newAuto.getLicensePlateNo(), existedAuto.getLicensePlateNo())) {
            return false;
        }
        if (needUpdate(newAuto.getEngineNo(), existedAuto.getEngineNo())) {
            return false;
        }
        if ((newAuto.getEnrollDate() == null && existedAuto.getEnrollDate() != null)
                || (newAuto.getEnrollDate() != null && existedAuto.getEnrollDate() == null)
                || (newAuto.getEnrollDate() != null && existedAuto.getEnrollDate() != null
                && !DateUtils.truncate(newAuto.getEnrollDate(), Calendar.DAY_OF_MONTH).equals(DateUtils.truncate(existedAuto.getEnrollDate(), Calendar.DAY_OF_MONTH)))) {
            return false;
        }
        if (needUpdate(newAuto.getLicenseColorCode(), existedAuto.getLicenseColorCode())) {
            return false;
        }
        if (needUpdate(newAuto.identityType?.id as String, existedAuto.identityType?.id as String)) {
            return false
        }
        if (needUpdate(newAuto.getIdentity(), existedAuto.getIdentity())) {
            return false;
        }
        if (needUpdate(newAuto.getInsuredIdNo(), existedAuto.getInsuredIdNo())) {
            return false;
        }
        if (needUpdate(newAuto.getVinNo(), existedAuto.getVinNo())) {
            return false;
        }
        if (needUpdate(newAuto.getOwner(), existedAuto.getOwner())) {
            return false;
        }
        if ((newAuto.getAutoType() == null && existedAuto.getAutoType() != null)
                || (newAuto.getAutoType() != null && existedAuto.getAutoType() == null)
                || (newAuto.getAutoType() != null && existedAuto.getAutoType() != null
                && (!StringUtils.equals(newAuto.getAutoType().getCode(), existedAuto.getAutoType().getCode())
                || newAuto.getAutoType().getSeats() != existedAuto.getAutoType().getSeats())
        )) {
            return false;
        }
        if ((newAuto.getArea() == null && existedAuto.getArea() != null)
                || (newAuto.getArea() != null && existedAuto.getArea() == null)
                || (newAuto.getArea() != null && existedAuto.getArea() != null
                && !StringUtils.equals(newAuto.getArea().getId().toString(), existedAuto.getArea().getId().toString())
        )) {
            return false;
        }
        return true;
    }

    private boolean needUpdate(String newValue, String existedValue) {
        if (StringUtils.equals(newValue, existedValue)) {
            return false;
        }
        if (StringUtils.isBlank(newValue) && !StringUtils.isBlank(existedValue)) {
            return false;
        }
        return true;
    }

    Auto insertNewAuto(Auto newAuto) {
        newAuto.setId(null);
        newAuto.setBillRelated(false);
        newAuto.setCreateTime(Calendar.getInstance().getTime());
        newAuto.setUpdateTime(newAuto.getCreateTime());
        return this.saveAuto(newAuto);
    }

    Auto updateAutoInfo(Auto newAuto, Auto existedAuto) {

        existedAuto.with { existed ->
            ["engineNo", "enrollDate", "licensePlateNo", "area", "identity", "owner", "vinNo", "licenseColorCode", "insuredIdNo", "identityType"].each { propName ->
                existed[propName] = newAuto[propName] ?: existed[propName]
            }
            existed
        }

        existedAuto.autoType = autoTypeService.saveAutoType(newAuto.autoType)
        existedAuto.updateTime = Calendar.getInstance().getTime()
        existedAuto
    }

    Auto findAuto(Auto auto, User user) {
        return this.autoRepo.searchByUserAuto(user.getId(), auto.getEngineNo(), auto.getIdentity(), auto.getLicensePlateNo(), auto.getOwner(), auto.getVinNo());
    }

    List<Auto> getAutoByLicense(String plateNo) {
        return autoRepo.findByLicense(plateNo);
    }

    List<Auto> getAutoByLicenseExact(String plateNo) {
        return autoRepo.findByLicenseExact(plateNo);
    }

    Auto insertAuto(User user, Auto newAuto) {
        newAuto.setCreateTime(Calendar.getInstance().getTime());
        newAuto.setUpdateTime(newAuto.getCreateTime());
        newAuto = this.saveAuto(newAuto);
        UserAuto userAuto = new UserAuto();
        userAuto.setUser(user);
        userAuto.setAuto(newAuto);
        this.userAutoRepo.save(userAuto);
        return newAuto;
    }

    Auto delAuto(Long autoId, User user) {
        Auto result = this.autoRepo.getAutoByIdAndUser(autoId, user.getId());

        if (result == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "根据当前用户和车辆ID未查询到车辆信息");
        }

        if (result.isDisable()) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "此车辆已处于删除状态");
        }

        result.setDisable(true);
        result = this.autoRepo.save(result);
        return result;
    }

    Page<Auto> listByUserPage(User user, Pageable pageable) {
        return autoRepo.searchAutoListPageable(user, pageable);
    }


    def syncVehicleLicense(Auto auto) {
        logger.debug '将Auto表的数据同步到VehicleLicense,根据{}和{}新增或更新行驶本', auto.getLicensePlateNo(), auto.getOwner()
        def vehicleLicense = VehicleLicense.createVLByAuto(auto)
        this.vehicleLicenseRepo.findFirstByLicensePlateNoAndOwner(auto.licensePlateNo, auto.owner).with { internalVL ->
            if (!internalVL) {
                logger.debug 'DB不存在当前车辆：{} , {} 对应的行驶本,新增行驶本信息 {}', auto.licensePlateNo, auto.owner, vehicleLicense
                return this.vehicleLicenseRepo.save(vehicleLicense)
            }

            logger.debug '当前车辆：{} , {} 对应的行驶本已存在,更新前行驶本信息为 {}', auto.licensePlateNo, auto.owner, internalVL
            VehicleLicense merged = this.vehicleLicenseRepo.save(VehicleLicense.merge(vehicleLicense, internalVL))
            logger.debug '当前车辆：{} , {} 对应的行驶本已存在,更新后行驶本信息为 {}', auto.licensePlateNo, auto.owner, merged

            merged
        }
    }

    List getSupplementInfo(Long cityCode, Boolean isTransfer, Channel channel = IOS_4) {  //目前不能用新格式，否则导致报价参数格式不对
        def rawData = getSupplementInfoByCity supplementInfoMappings.get("supplementInfoMappings"), cityCode, isTransfer
        LackOfSupplementInfoHandler.formatResponse LackOfSupplementInfoHandler.writeResponse(rawData, channel)
    }

    List getCarModelSupplementInfo(Long cityCode, Channel channel) {
        def carModelSupplementInfo = [:]
        carModelSupplementInfoMappings.get("carModelSupplementInfoMappings").each { it ->
            carModelSupplementInfo.put(it.key, LackOfSupplementInfoHandler.writeResponse(it.value, channel))
        }
        getSupplementInfoByCity carModelSupplementInfo, cityCode, true
    }

    private List getSupplementInfoByCity(Map mappings, Long cityCode, Boolean isTransfer) {
        List result = (List) (mappings.get(cityCode) != null ? mappings.get(cityCode) : mappings.get("default"));
        if (result == null || isTransfer) {
            return result;
        }

        List<Map> list = new ArrayList<>();
        Iterator iterator = result.iterator();
        while (iterator.hasNext()) {
            Map object = (Map) iterator.next();
            if ("transfer-date".equals(((Map) object).get("validationType"))) {
                continue;
            }
            list.add(object);
        }
        return list;
    }

    /**
     * 加密车辆信息(中间以*代替)
     */
    void encryptAuto(String clientIdentifier, Auto auto) {
        if (auto == null) {
            return;
        }
        cacheService.cacheEncryptionAuto(cacheService.getEncryptionAutoKey(clientIdentifier), auto.clone())
        AutoUtils.encrypt(AutoUtils.AUTO_ENCRYPT_PROPS, auto, Auto.PROPERTIES)
    }

    /**
     *  加密行驶证信息(中间以*代替)
     */
    void encryptVehicleLicense(String clientIdentifier, VehicleLicense vehicleLicense) {
        if (vehicleLicense == null) {
            return
        }

        def unencryptedAuto = new Auto(licensePlateNo: vehicleLicense.licensePlateNo, owner: vehicleLicense.owner,
                identity: vehicleLicense.identity, vinNo: vehicleLicense.vinNo, engineNo: vehicleLicense.engineNo)

        cacheService.cacheEncryptionAuto(cacheService.getEncryptionAutoKey(clientIdentifier), unencryptedAuto)
        AutoUtils.encrypt(AutoUtils.VEHICLE_LICENSE_ENCRYPT_PROPS_NEW, vehicleLicense, VehicleLicense.PROPERTIES)
    }

    /**
     * 解密车辆信息
     */
    void decryptAuto(Auto encryptedAuto, User currentUser, String clientIdentifier) {
        if (encryptedAuto == null || !AutoUtils.isAutoContainStarChars(encryptedAuto)) {
            return
        }

        String key = cacheService.getEncryptionAutoKey(clientIdentifier)
        Auto unencryptedAuto = (encryptedAuto.getId() != null) ? autoRepo.findOne(encryptedAuto.getId()) : cacheService.getEncryptionAuto(key);
        if (unencryptedAuto == null && currentUser != null) {
            unencryptedAuto = autoRepo.searchByUserAndOwnerAndPlateNo(currentUser.getId(), encryptedAuto.getOwner(), encryptedAuto.getLicensePlateNo())
        }

        if (unencryptedAuto == null) {
            logger.error("缓存中未发现任何key= {} 的车辆信息", key)
            throw new BusinessException(BusinessException.Code.CAR_INPUT_FIELD_OUT_TIME, "未找到加密前的车辆信息")
        }

        AutoUtils.decrypt(unencryptedAuto, encryptedAuto)
    }

    void decryptPurchaseOrder(PurchaseOrder po ,String clientIdentifier){
        if(AutoUtils.isContainStrChars(po)){
            InsuranceBasicInfo insuranceBasicInfo = cacheService.getEncryptionInsuranceBasicInfo(getEncryptionInsuranceBasicInfoKey(clientIdentifier))
            if (insuranceBasicInfo) {
                AutoUtils.decrypt(po, insuranceBasicInfo)
            } else {
                String key = cacheService.getEncryptionAutoKey(clientIdentifier)
                Auto unencryptedAuto = cacheService.getEncryptionAuto(key)
                if (unencryptedAuto == null) {
                    logger.info("提交订单未在session:{},找到缓存的车辆信息,解密po：{}中的加密信息失败", clientIdentifier, CacheUtil.doJacksonSerialize(po))
                }
                AutoUtils.decrypt(po, unencryptedAuto)
            }
        }
    }

    Auto findLatestByUserAndPlateNo(Long userId, String licensePlateNo) {
        return autoRepo.searchByUserAndPlateNoAndDisableOrderByUpdateTimeDesc(userId, licensePlateNo);
    }

    List<Auto> findRenewalAuto(Long userId,String licensePlateNo){
        return autoRepo.searchRenewalAuto(userId,licensePlateNo)
    }

    static getDictionaries() {
        [
            fuelTypes          : FuelType.Enum.ALL.collect { [value: it.id, text: it.name] },
            useCharacters      : UseCharacter.Enum.USE_CHARACTERS_TOA.collect { [value: it.id, text: it.description] },
            parentIdentityTypes: ParentIdentityType.Enum.ALL.collect { [value: it.id, text: it.name] },
            identityTypes      : IdentityType.Enum.IDENTITY_TYPES_TOA.collect {
                [value: it.id, text: it.name, parent: it.parent.id]
            }
        ]
    }
}
