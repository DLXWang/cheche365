package com.cheche365.cheche.core.exception

import java.text.SimpleDateFormat

/**
 * Created by zhengwei on 3/26/17.
 */
class Constants {

    static final NON_SELF_EXPRESS_TYPES = ['single-selection']
    static final VL_CLIENT_VISIBLE_FIELD = ['licensePlateNo', 'owner', 'identity', 'vinNo', 'engineNo', 'enrollDate', 'code']
    static final FIELD_ORDER_TOC = ['licensePlateNo', 'owner', 'identity', 'vinNo', 'engineNo', 'enrollDate', 'code', 'autoModel', 'seats', 'commercialStartDate', 'compulsoryStartDate', 'transferDate']
    static final FIELD_ORDER = ['licensePlateNo', 'owner', 'vinNo', 'engineNo', 'enrollDate', 'code', 'autoModel', 'seats', 'fuelType', 'useCharacter', 'parentIdentityType', 'identityType', 'identity', 'compulsoryStartDate', 'commercialStartDate', 'transferDate']
    static final FIELD_ORDER_TOA = FIELD_ORDER - ['autoModel', 'parentIdentityType']

    static final GET_VISIBLE_FIELD = { channel -> channel.isStandardAgent() ? FIELD_ORDER : FIELD_ORDER_TOC }
    static final FIND_INDEX = { fields, it -> fields.contains(it) ? fields.indexOf(it) : Integer.MAX_VALUE }

    static final OLD_PATH_FIELD = ['enrollDate', 'code', 'transferDate']
    static final DATE_FORMATTER = { it instanceof Date ? it : new SimpleDateFormat('yyyy-MM-dd').parse(it) }
    static final CLIENT_INVISIBLE_FIELDS = ['oldPath', 'targetPath', 'persistPath', 'writeFormatter']
    static final PERSIST_FIELD = ['autoModel', 'commercialStartDate', 'compulsoryStartDate', 'transferDate', 'selectedAutoModel', 'newCarFlag', 'metaInfo']
    static final PERSIST_FIELD_JSON = ['selectedAutoModel', 'metaInfo']
    static final ADDITIONAL_PARAM_KEYS = PERSIST_FIELD - ['metaInfo']
    static final EXCEPT_FIELDS = ["class", "metaClass", "id"]
    static final CAPTCHA_IMAGE_FIELD = ["compulsoryCaptchaImage", "commercialCaptchaImage"]

    static FIELD_PATH_MAPPING = [

        enrollDate            : [
            oldPath       : 'auto.enrollDate',
            targetPath    : 'supplementInfo.enrollDate',
            persistPath   : 'auto.enrollDate',
            fieldType     : 'date',
            fieldLabel    : '车辆注册日期',
            validationType: 'enroll-date',
            writeFormatter: DATE_FORMATTER
        ],

        code                  : [
            oldPath       : 'auto.autoType.code',
            targetPath    : 'auto.autoType.code',
            persistPath   : 'supplementInfo.auto.autoType.code',
            fieldType     : 'text',
            fieldLabel    : '品牌型号',
            hints         : ['行驶证中的品牌型号'],
            validationType: 'auto-type-code'
        ],

        autoModel             : [
            oldPath   : 'auto.autoType.supplementInfo.autoModel',
            targetPath: 'supplementInfo.autoModel',
            fieldType : 'single-selection',
            fieldLabel: '车型列表',
            options   : null
        ],

        commercialStartDate   : [
            oldPath       : 'auto.autoType.supplementInfo.commercialStartDate',
            targetPath    : 'supplementInfo.commercialStartDate',
            fieldType     : 'date',
            fieldLabel    : '商业险保单生效日期',
            validationType: 'effective-date',
            writeFormatter: DATE_FORMATTER,
            default       : { date -> return handleEffectDate(date) }
        ],

        compulsoryStartDate   : [
            oldPath       : 'auto.autoType.supplementInfo.compulsoryStartDate',
            targetPath    : 'supplementInfo.compulsoryStartDate',
            fieldType     : 'date',
            fieldLabel    : '交强险保单生效日期',
            validationType: 'effective-date',
            writeFormatter: DATE_FORMATTER,
            default       : { date -> return handleEffectDate(date) }
        ],

        seats                 : [
            oldPath       : 'auto.autoType.seats',
            targetPath    : 'supplementInfo.seats',
            persistPath   : 'auto.autoType.seats',
            fieldType     : 'integer',
            fieldLabel    : '车辆座位数',
            validationType: 'seats',
            default       : { infoValue -> return infoValue ?: 5 },
            writeFormatter: { it as Integer },
        ],

        transferDate          : [
            oldPath       : 'auto.autoType.supplementInfo.transferDate',
            targetPath    : 'supplementInfo.transferDate',
            fieldType     : 'date',
            fieldLabel    : '过户日期',
            validationType: 'transfer-date',
            writeFormatter: DATE_FORMATTER
        ],

        transferFlag          : [
            oldPath       : 'auto.autoType.supplementInfo.transferFlag',
            targetPath    : 'supplementInfo.transferFlag',
            fieldType     : 'boolean',
            fieldLabel    : '过户标志',
            validationType: 'boolean'
        ],

        newCarFlag            : [
            oldPath       : 'supplementInfo.newCarFlag',
            targetPath    : 'supplementInfo.newCarFlag',
            fieldType     : 'boolean',
            fieldLabel    : '新车未上牌标志',
            validationType: 'boolean'
        ],

        verificationCode      : [
            oldPath       : 'additionalParameters.supplementInfo.verificationCode',
            targetPath    : 'supplementInfo.verificationCode',
            fieldType     : 'text',
            fieldLabel    : '保险行业协会发送的验证码',
            validationType: 'text',
            meta          : null // 元信息，是一个map，由运行时指定，可以为null
        ],

        verificationMobile    : [
            oldPath       : 'additionalParameters.supplementInfo.verificationMobile',
            targetPath    : 'supplementInfo.verificationMobile',
            fieldType     : 'text',
            fieldLabel    : '保险行业协会发送的手机号',
            validationType: 'text',
            meta          : null // 元信息，是一个map，由运行时指定，可以为null
        ],

        images                : [
            oldPath       : 'additionalParameters.supplementInfo.images',
            targetPath    : 'supplementInfo.images',
            fieldType     : 'binary',
            fieldLabel    : '影像上传',
            validationType: 'image',
            meta          : null // 元信息，是一个map，由运行时指定，可以为null
        ],

        commercialCaptchaImage: [
            oldPath       : 'additionalParameters.supplementInfo.commercialCaptchaImage',
            targetPath    : 'supplementInfo.commercialCaptchaImage',
            fieldType     : 'binary',
            fieldLabel    : '商业险图片验证码',
            validationType: 'captcha',
            meta          : null // 元信息，是一个map，由运行时指定，可以为null
        ],

        compulsoryCaptchaImage: [
            oldPath       : 'additionalParameters.supplementInfo.compulsoryCaptchaImage',
            targetPath    : 'supplementInfo.compulsoryCaptchaImage',
            fieldType     : 'binary',
            fieldLabel    : '交强险图片验证码',
            validationType: 'captcha',
            meta          : null // 元信息，是一个map，由运行时指定，可以为null
        ],

        identity              : [
            oldPath       : 'auto.identity',
            targetPath    : 'auto.identity',
            fieldType     : 'text',
            fieldLabel    : '车主身份证',
            hints         : null,
            validationType: 'id',
            originalValue : null
        ],

        insuredIdNo           : [
            oldPath       : 'auto.insuredIdNo',
            targetPath    : 'auto.insuredIdNo',
            fieldType     : 'text',
            fieldLabel    : '去年被保人身份证',
            hints         : null,
            validationType: 'id',
            originalValue : null
        ],

        owner                 : [
            oldPath       : 'auto.owner',
            targetPath    : 'auto.owner',
            fieldType     : 'text',
            fieldLabel    : '车主姓名',
            hints         : ['可能不正确'],
            validationType: 'name',
            originalValue : null
        ],

        licensePlateNo        : [
            oldPath       : 'auto.licensePlateNo',
            targetPath    : 'auto.licensePlateNo',
            fieldType     : 'text',
            fieldLabel    : '车牌号',
            hints         : ['可能不正确'],
            validationType: 'license-plate-no',
            originalValue : null
        ],

        vinNo                 : [
            oldPath       : 'auto.vinNo',
            targetPath    : 'auto.vinNo',
            fieldType     : 'text',
            fieldLabel    : '车辆识别代码',
            hints         : ['可能不正确'],
            validationType: 'vin-no',
            originalValue : null
        ],

        engineNo              : [
            oldPath       : 'auto.engineNo',
            targetPath    : 'auto.engineNo',
            fieldType     : 'text',
            fieldLabel    : '发动机号',
            hints         : ['可能不正确'],
            validationType: 'engine-no',
            originalValue : null
        ],

        fuelType              : [
            oldPath       : 'auto.fuelType.id',
            targetPath    : 'auto.fuelType.id',
            fieldType     : 'single-selection',
            fieldLabel    : '燃料种类',
            validationType: 'fuel-type-id',
            originalValue : null,
            options       : null
        ],

        useCharacter          : [
            oldPath       : 'auto.useCharacter.id',
            targetPath    : 'auto.useCharacter.id',
            fieldType     : 'single-selection',
            fieldLabel    : '使用性质',
            validationType: 'use-character-id',
            originalValue : null,
            options       : null
        ],

        parentIdentityType    : [
            oldPath       : 'supplementInfo.parentIdentityType.id',
            targetPath    : 'supplementInfo.parentIdentityType.id',
            fieldType     : 'single-selection',
            fieldLabel    : '所属人类型',
            validationType: 'parent-identity-type-id',
            originalValue : null,
            options       : null
        ],

        identityType          : [
            oldPath       : 'auto.identityType.id',
            targetPath    : 'auto.identityType.id',
            fieldType     : 'single-selection',
            fieldLabel    : '证件类型',
            validationType: 'identity-type-id',
            originalValue : null,
            options       : null
        ],

        newPrice              : [
            oldPath       : 'auto.autoType.newPrice',
            targetPath    : 'auto.autoType.newPrice',
            fieldType     : 'hidden',
            fieldLabel    : '新车价格',
            hints         : ['可能不正确'],
            validationType: 'autoType-newPrice',
            originalValue : null
        ],

        idNation              : [
            oldPath       : 'supplementInfo.idCard.nation',
            targetPath    : 'supplementInfo.idCard.nation',
            fieldType     : 'text',
            fieldLabel    : '民族',
            validationType: '',
            originalValue : null,
            readIgnore    : true  //从客户端读取参数是，不用重新折腾参数位置
        ],

        idIssueDate           : [
            oldPath       : 'supplementInfo.idCard.issueDate',
            targetPath    : 'supplementInfo.idCard.issueDate',
            fieldType     : 'date',
            fieldLabel    : '发证日期',
            validationType: '',
            originalValue : null,
            readIgnore    : true
        ],


        idExpirationDate      : [
            oldPath       : 'supplementInfo.idCard.expirationDate',
            targetPath    : 'supplementInfo.idCard.expirationDate',
            fieldType     : 'date',
            fieldLabel    : '截止日期',
            validationType: '',
            originalValue : null,
            readIgnore    : true
        ],

        idIssuingAuthority    : [
            oldPath       : 'supplementInfo.idCard.issuingAuthority',
            targetPath    : 'supplementInfo.idCard.issuingAuthority',
            fieldType     : 'text',
            fieldLabel    : '发证机关',
            validationType: '',
            originalValue : null,
            readIgnore    : true
        ],

        idAddress             : [
            oldPath       : 'supplementInfo.idCard.address',
            targetPath    : 'supplementInfo.idCard.address',
            fieldType     : 'text',
            fieldLabel    : '住址',
            validationType: '',
            originalValue : null,
            readIgnore    : true
        ]

    ]

    static final EXCEPTION_TEMPLATE_OLD = FIELD_PATH_MAPPING.inject([:]) { result, info ->
        result[info.key] = info.value.findAll {
            !CLIENT_INVISIBLE_FIELDS.contains(it.key)
        } + ([fieldPath: info.value.oldPath])
        result
    }

    static final EXCEPTION_TEMPLATE_NEW = FIELD_PATH_MAPPING.inject([:]) { result, info ->
        result[info.key] = info.value.findAll {
            !CLIENT_INVISIBLE_FIELDS.contains(it.key)
        } + ([fieldPath: info.value.targetPath])
        result
    }

    static handleEffectDate(infoValue) {
        (infoValue && infoValue.after(new Date())) ? infoValue : null
    }

}
