package com.cheche365.cheche.core.exception.handler

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.QuoteSupplementInfo
import com.cheche365.cheche.core.util.CacheUtil
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.SerializationUtils

import java.text.SimpleDateFormat

import static com.cheche365.cheche.core.exception.Constants.getFIND_INDEX
import static com.cheche365.cheche.core.exception.Constants.getGET_VISIBLE_FIELD
import static com.cheche365.cheche.core.exception.Constants.getEXCEPTION_TEMPLATE_NEW
import static com.cheche365.cheche.core.exception.Constants.getEXCEPTION_TEMPLATE_OLD
import static com.cheche365.cheche.core.exception.Constants.getFIELD_PATH_MAPPING
import static com.cheche365.cheche.core.exception.Constants.getOLD_PATH_FIELD
import static com.cheche365.cheche.core.exception.Constants.getPERSIST_FIELD_JSON

/**
 * Created by zhengwei on 2/17/17.
 */

@Slf4j
class LackOfSupplementInfoHandler {

    static writeResponse(supplementInfo, Channel channel) {
        def clonedInfo = SerializationUtils.clone(supplementInfo)
        clonedInfo?.each { Map infoItem ->
            def fieldPathEntry = infoItem.find { it.key == 'fieldPath' }
            infoItem.key = fieldPathEntry.value?.split("\\.")?.last()
            if (![Channel.Enum.WE_CHAT_APP_39].contains(channel)) {
                fieldPathEntry.value = addPrefix(fieldPathEntry.value)
            } else {
                def mappingEntry = EXCEPTION_TEMPLATE_OLD.find { fieldPathEntry.value.endsWith it.key }
                fieldPathEntry.value = mappingEntry.value.fieldPath
            }
        }

        clonedInfo
    }

    static void readRequest(requestParams) {  //从客户端请求参数读取补充信息，并根调整参数格式，以符合与parser的约定。
        // 之所以不区别新旧格式执行两次，是因为目前有些补充信息不是从报价抛出，比如根据城市区分通用补充信息，这些在报价之前提供给客户端的补充信息还要放在old path下
        def metaData = FIELD_PATH_MAPPING.findAll {!it.value.readIgnore}

        metaData.each { mappingEntry ->  //处理旧版本参数格式
            parsePath(requestParams, mappingEntry.value.oldPath)?.with { sourceObj, String sourceAttr ->
                [mappingEntry.value.targetPath, mappingEntry.value.persistPath].each {
                    copyToTargetObj(sourceObj[sourceAttr], requestParams, it, mappingEntry.value.writeFormatter)
                }
            }
        }

        requestParams.additionalParameters?.supplementInfo?.findAll {
            it.key && it.value
        }?.each { infoKey, infoValue ->  //处理新版本参数格式
            metaData.find { it.key == infoKey }?.with { mappingEntry ->
                    [mappingEntry.value.targetPath, mappingEntry.value.persistPath].each {
                        copyToTargetObj(infoValue, requestParams, it, mappingEntry.value.writeFormatter)
                    }
            }
        }
    }

    static def transformFormat(original, Channel channel) {
        def oldParams = EXCEPTION_TEMPLATE_OLD.collect { mappingEntry ->
            parsePath(original, mappingEntry.value.fieldPath)?.with { sourceObj, String sourceAttr ->
                if (sourceObj[sourceAttr]) {
                    SerializationUtils.clone(mappingEntry.value).with {
                        it.key = sourceAttr
                        it.originalValue = sourceObj[sourceAttr]
                        it.remove('hints')
                        it
                    }
                }
            }
        }.findAll { it }

        def newParams = original.additionalParameters?.supplementInfo?.findAll {
            it.key && it.value
        }?.collect { infoKey, infoValue ->
            EXCEPTION_TEMPLATE_NEW.find { it.key == infoKey }?.with { mappingEntry ->
                SerializationUtils.clone(mappingEntry.value).with {
                    it.key = infoKey
                    it.fieldPath = addPrefix(it.fieldPath)
                    it.originalValue = infoValue
                    it.remove('hints')
                    it
                }
            }
        }?.findAll { it }


        (oldParams + newParams).sort { a, b -> FIND_INDEX(GET_VISIBLE_FIELD(channel), a.key) <=> FIND_INDEX(GET_VISIBLE_FIELD(channel), b.key) }
    }

    static def copyToTargetObj(sourceObj, targetRoot, String targetPath, Closure formatter) {
        if (sourceObj && targetRoot && targetPath) {
            parsePath(targetRoot, targetPath)?.with { targetObj, String targetAttr ->
                targetObj[targetAttr] = formatter ? formatter(sourceObj) : sourceObj
            }
        }
    }

    static def parsePath(sourceObj, String path) {
        def pathSegments = path.split('\\.')
        def targetObj = sourceObj

        int i = 0
        for (; (i != pathSegments.length - 1); i++) {
            String segment = pathSegments[i]
            if (null != targetObj[segment]) {
                targetObj = targetObj[segment]
            } else {
                break
            }
        }

        (i == pathSegments.length - 1 && targetObj != null) ? [targetObj, pathSegments.last()] : null
    }

    static List<Map> toOrderCenterFormat(List<QuoteSupplementInfo> info) {
        def customEntry=[selectedAutoModel : [
            oldPath          : 'auto.autoType.supplementInfo.selectedAutoModel',
            targetPath       : 'supplementInfo.selectedAutoModel',
            fieldType        : 'text',
            fieldLabel       : '车型列表',
            options          : null
        ]]

        info?.collect { infoEntry ->
            ( FIELD_PATH_MAPPING+customEntry).find { infoEntry.fieldPath.endsWith(it.key) }?.value?.with { mappingEntry ->
                new HashMap<>().with { result ->
                    result.label = mappingEntry.fieldLabel
                    result.value = mappingEntry.fieldType == 'single-selection' ? infoEntry.valueName : infoEntry.value
                    result.fieldPath = infoEntry.fieldPath
                    result
                }
            }
        }?.findAll { it }

    }

    static findByCode(String fullPath) {
        FIELD_PATH_MAPPING.find { fullPath.endsWith(it.key) }?.value
    }

    static getByQuoteSupplementInfo(List<QuoteSupplementInfo> quoteSupplementInfoList, Map additionalParameters) {
        Map supplementMap = new HashMap()
        quoteSupplementInfoList?.each { quoteSupplementInfo ->
            def fieldPath = quoteSupplementInfo.fieldPath.split("\\.")
            if ('date' == findByCode(fieldPath.last())?.fieldType) {
                if(quoteSupplementInfo.value){
                    supplementMap.put(fieldPath.last(), new SimpleDateFormat('yyyy-MM-dd').parse(quoteSupplementInfo.value))
                } else {
                    log.error('null date value when read supplement info, path: {}',quoteSupplementInfo.fieldPath)
                }

            } else if (PERSIST_FIELD_JSON.contains(fieldPath.last())) {
                supplementMap.put(fieldPath.last(), CacheUtil.doJacksonDeserialize(quoteSupplementInfo.value, Map.class))
            } else {
                supplementMap.put(fieldPath.last(), quoteSupplementInfo.value)
            }
        }
        if (additionalParameters?.supplementInfo) {
            additionalParameters.supplementInfo.putAll(supplementMap)
        } else {
            additionalParameters.put("supplementInfo", supplementMap)
        }
        additionalParameters
    }

    static addPrefix(String path) {
        path.startsWith('supplementInfo') ? "additionalParameters.$path".toString() : path
    }

    static def formatResponse(supplementInfo) {
        def cloneSupplementInfo = SerializationUtils.clone(supplementInfo)
        cloneSupplementInfo.findAll { Map infoItem ->
            def fieldPathEntry = infoItem.find { it.key == 'fieldPath' }
            fieldPathEntry && OLD_PATH_FIELD.find { fieldPathEntry.value.endsWith it}
        }.each { Map infoItem ->
            infoItem.fieldPath = FIELD_PATH_MAPPING.find {
                infoItem.find { it.key == 'fieldPath' }.value.endsWith it.key
            }.value.oldPath
        }
        cloneSupplementInfo
    }

    def static formatFields(Map original, visibleFields) {
        def props = original?.findAll { visibleFields.contains(it.key) }
        props?.collect { infoKey, infoValue ->
            EXCEPTION_TEMPLATE_NEW.find { it.key == infoKey }?.with { mappingEntry ->
                if ('single-selection' == mappingEntry.value.fieldType) {
                    SerializationUtils.clone(mappingEntry.value).with {
                        it.key = infoKey
                        it.fieldPath = addPrefix(it.fieldPath)
                        it.remove('hints')
                        if (infoValue.options?.size() > 0) {
                            it.options = infoValue.options
                        } else {
                            it.remove('options')
                        }
                        it.originalValue = infoValue.originalValue
                        it
                    }
                } else {
                    SerializationUtils.clone(mappingEntry.value).with {
                        it.key = infoKey
                        it.fieldPath = addPrefix(it.fieldPath)
                        it.originalValue = it.default ? it.default(infoValue) : infoValue
                        it.remove('hints')
                        it.remove('default')
                        it
                    }
                }
            }
        }?.sort { a, b -> FIND_INDEX(visibleFields, a.key) <=> FIND_INDEX(visibleFields, b.key) }
    }

}
