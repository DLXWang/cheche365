package com.cheche365.cheche.bihu.util

import com.cheche365.cheche.bihu.model.BihuCustKey
import com.cheche365.cheche.core.model.InsurancePackage
import com.cheche365.cheche.core.model.mongo.MongoUser
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.bihu.Constants.get_INSURANCE_COMPANY_MAPPING
import static com.cheche365.cheche.common.util.HashUtils.MD5
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.core.model.LogType.Enum.BIHU_53
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT
import static com.cheche365.cheche.parser.Constants._DRIVER_IOP
import static com.cheche365.cheche.parser.Constants._ENGINE
import static com.cheche365.cheche.parser.Constants._ENGINE_IOP
import static com.cheche365.cheche.parser.Constants._GLASS
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT
import static com.cheche365.cheche.parser.Constants._PASSENGER_IOP
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT
import static com.cheche365.cheche.parser.Constants._SCRATCH_IOP
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS_IOP
import static com.cheche365.cheche.parser.Constants._THEFT
import static com.cheche365.cheche.parser.Constants._THEFT_IOP
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_IOP
import static com.cheche365.cheche.parser.Constants._UNABLE_FIND_THIRDPARTY
import static groovy.json.StringEscapeUtils.unescapeJava
import static groovyx.net.http.ContentType.JSON
import static java.util.concurrent.TimeUnit.SECONDS
import static org.apache.commons.lang3.RandomUtils.nextInt as random

/**
 * 工具集
 * Created by Huabin on 2016/6/24.
 */
@Slf4j
class BusinessUtils {

    private static final _KIND_CODE_INSURANCE_PACKAGE_MAPPINGS = [
        ['SanZhe', _THIRD_PARTY_AMOUNT, 'BuJiMianSanZhe', _THIRD_PARTY_IOP, false], // 第三者责任险
        ['SiJi', _DRIVER_AMOUNT, 'BuJiMianSiJi', _DRIVER_IOP, false], // 车上人员责任险-司机
        ['ChengKe', _PASSENGER_AMOUNT, 'BuJiMianChengKe', _PASSENGER_IOP, false], // 车上人员责任险-乘客
        ['HuaHen', _SCRATCH_AMOUNT, 'BuJiMianHuaHen', _SCRATCH_IOP, false], // 车身划痕损失险
        ['CheSun', _DAMAGE, 'BuJiMianCheSun', _DAMAGE_IOP, true], // 机动车辆损失险
        ['DaoQiang', _THEFT, 'BuJiMianDaoQiang', _THEFT_IOP, true], // 盗抢险
        ['ZiRan', _SPONTANEOUS_LOSS, 'BuJiMianZiRan', _SPONTANEOUS_LOSS_IOP, true], // 自燃损失险
        ['SheShui', _ENGINE, 'BuJiMianSheShui', _ENGINE_IOP, true], // 发动机特别损失险
        ['HcSanFangTeYue', _UNABLE_FIND_THIRDPARTY, null, null, true], // 无法找到第三方特约险
        ['BoLi', _GLASS, null, null, true, true], // 玻璃险
    ]

    /**
     * 获取续保套餐
     */
    static generateRenewalPackage(allItemKinds) {
        def renewalPackage = new InsurancePackage()
        renewalPackage.compulsory = true
        renewalPackage.autoTax = true

        _KIND_CODE_INSURANCE_PACKAGE_MAPPINGS.each { outKindCode, propName, outIopCode, iopName,
                                                     isBoolean, isGlass = false ->
            def amount = allItemKinds[outKindCode] ? (allItemKinds[outKindCode] as double) : 0
            renewalPackage[propName] = isBoolean ? (amount as boolean) : amount
            if (iopName) {
                renewalPackage[iopName] = (1 == allItemKinds[outIopCode])
            }

            if (isGlass && renewalPackage.glass) {
                renewalPackage.glassType = (1 == amount ? DOMESTIC_1 : IMPORT_2)
            }
        }

        renewalPackage
    }

    /**
     * 判断是否车辆之前（当天）有过查询记录
     * @param context
     * @return
     */
    static wasVehicleInfoFinished(context) {
        def findVehicleInfoFailedKey = getFindVehicleInfoFinishedKey context
        def result = new JsonSlurper().parseText(context.globalContext.get(findVehicleInfoFailedKey) ?: '{}')
        if (result) {
            context.globalContext.unbind getFindingVehicleInfoKey(context)
            context.expireTTLInSeconds = context.globalContext.getTTL findVehicleInfoFailedKey, SECONDS
        }
        result
    }

    /**
     * 标记在壁虎查询过的车辆（有效期到翌日0点）
     * @param context
     * @param result
     * @return
     */
    static markFinishedVehicleInfo(context, result, expireTime) {
        def globalContext = context.globalContext
        def findVehicleInfoFinishedKey = getFindVehicleInfoFinishedKey context
        globalContext.bindIfAbsentWithTTL findVehicleInfoFinishedKey, new JsonBuilder(result).toString(), expireTime, SECONDS
        context.expireTTLInSeconds = expireTime
        globalContext.unbind getFindingVehicleInfoKey(context)
    }

    private static getFindVehicleInfoFinishedKey(context) {
        def licensePlateNo = context.auto.licensePlateNo
        "find-vehicle-info-finished-$licensePlateNo"
    }

    static getFindingVehicleInfoKey(context) {
        def licensePlateNo = context.auto.licensePlateNo
        "finding-vehicle-info-$licensePlateNo"
    }

    static saveApplicationLog(context, message, id) {
        def modelBuilder = new ObjectGraphBuilder().with {
            classNameResolver = [name: 'reflection', root: 'com.cheche365.cheche.core.model']
            identifierResolver = 'refId'
            it
        }
        modelBuilder.moApplicationLog {
            createTime new Date()
            instanceNo context.auto.licensePlateNo
            logMessage message
            logType BIHU_53
            objId id
            objTable BIHU_53?.name
            user context.additionalParameters?.user?.with {
                MongoUser.toMongoUser(it)
            }
        }.with {
            context.dbService?.saveApplicationLog(it)
        }
    }

    static sendAndReceive(context, path, params, stepId) {
        params << [
            Agent  : context.agent,
            CustKey: context.custKey
        ]

        def args = [
            contentType: JSON,
            path       : path,
            query      : params + [SecCode: secCode(context.agentPwd, params)]
        ]

        log.info '壁虎{}请求参数-> {}', stepId, args
        saveApplicationLog context, unescapeJava(new JsonBuilder(args).toString()), stepId

        def client = context.client
        def result = client.get args, { resp, json ->
            json
        }

        log.info "壁虎{}响应-> {}", stepId, result
        def vlApplicationLog = saveApplicationLog context, result ? unescapeJava(new JsonBuilder(result).toString()) : '调用壁虎行驶证查询服务失败', this.class.simpleName
        result.vlApplicationLog = result ? [id: vlApplicationLog?.id] : null

        result
    }

    static secCode(agentPwd, queryBody) {
        def secCode = queryBody.collect { k, v ->
            k + '=' + v
        }.join('&') + agentPwd

        MD5(secCode, false)
    }

    static getCustKey() {
        BihuCustKey.Enum.ALL.with { custKeys ->
            custKeys[random(0, custKeys.size())].custKey
        }
    }

    /**
     * 获取报哪些保险公司的参数
     * context.insuranceCompanyCodes获取是报一家还是多家保险公司
     * @param context
     * @return
     */
    static getQuoteGroup(context) {
        context.insuranceCompanyCodes.inject(0) { prev, curr ->
            prev + _INSURANCE_COMPANY_MAPPING[curr]
        }
    }

    static getCurrentInsuranceCompany(context) {
        context.insuranceCompany.code
    }

}
