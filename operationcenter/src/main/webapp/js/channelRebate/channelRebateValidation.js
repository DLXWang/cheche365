//@ sourceURL=channelRebateValidation.js
var channel_rebate_validation = {
    rebateFields: ['onlyCommercialRebate', 'onlyCompulsoryRebate', 'commercialRebate', 'compulsoryRebate'],
    readyRebateFields: ['onlyReadyCommercialRebate', 'onlyReadyCompulsoryRebate', 'readyCommercialRebate', 'readyCompulsoryRebate'],

    addValidation: function (formData) {
        channel_rebate_validation.commonValidation(formData);

        if (!formData.channelId) {
            layer.alert("请选择渠道！", {time: 2000});
            return false;
        }
        if (!formData.insuranceCompanyId) {
            layer.alert("请选择保险公司！", {time: 2000});
            return false;
        }
        if (common.isEmpty(formData.areaId)) {
            layer.alert("请输入支持的地区！", {time: 2000});
            return false;
        }
        return true;
    },
    updateValidation: function (formData) {
        channel_rebate_validation.commonValidation(formData);
    },
    commonValidation: function (formData) {
        let isRebateEmpty = true;
        let reg = /^(([0-4]?[0-9])|50)(\.[0-9]{1,2})?$/;
        for (let field of channel_rebate_validation.rebateFields) {
            let fieldValue = formData[field];
            if (fieldValue) {
                if (reg.test(fieldValue.trim())) {
                    isRebateEmpty = isRebateEmpty && false;
                } else {
                    layer.alert("费率必须是[0-50]之间的数字！", {time: 2000});
                    throw '费率必须是[0-50]之间的数字';
                }
            }
        }
        formData.status = isRebateEmpty ? 0 : 1; //判断是否是即使生效

        let isReadyRebateEmpty = true;
        for (let field of channel_rebate_validation.readyRebateFields) {
            let fieldValue = formData[field];
            if (fieldValue) {
                if (reg.test(fieldValue.trim())) {
                    isReadyRebateEmpty = isReadyRebateEmpty && false;
                } else {
                    layer.alert("费率必须是[0-50]之间的数字！", {time: 2000});
                    throw '费率必须是[0-50]之间的数字';
                }
            }
        }

        if (isRebateEmpty && isReadyRebateEmpty) {
            layer.alert("当前费率和预生效费率不能同时为空或者同时为0 ！", {time: 2000});
            throw '当前费率和预生效费率不能同时为空或者同时为0';
        }


        let isReadyDateEmpty = common.isEmpty(formData.readyEffectiveDate);
        if (isReadyDateEmpty !== isReadyRebateEmpty) {
            layer.alert("预生效时间和预生效费率必须同时为空或同时不为空 ！", {time: 2000});
            throw '预生效时间和预生效费率必须同时为空或同时不为空 !';
        }

        if (isRebateEmpty && !isReadyDateEmpty) {
            formData.readyFlag = true;
        }
    },
};
