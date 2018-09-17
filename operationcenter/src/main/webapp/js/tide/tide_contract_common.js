//@ sourceURL=tide_contract_common.js
var tide_contract_common = {
    interface: {
        getPlatform: function () {
            let dataList;
            common.getByAjax(false, 'get', 'json', '/operationcenter/tide/resource/platform', {}, function (data) {
                dataList = data;
            }, function () {
            });
            return dataList;
        },
        getBranch: function (paramData) {
            let dataList;
            common.getByAjax(false, 'get', 'json', '/operationcenter/tide/resource/branch', paramData, function (data) {
                dataList = data;
            }, function () {
            });
            return dataList;
        },
        getTideInstitution: function (formData) {
            let dataList;
            common.getByAjax(false, 'get', 'json', '/operationcenter/tide/resource/tideInstitution', formData, function (data) {
                dataList = data;
            }, function () {
            });
            return dataList;
        },
        initInsuranceCompany: function () {
            let insuranceCompanys;
            common.getByAjax(false, 'get', 'json', '/operationcenter/tide/resource/insuranceCompanys', {}, function (data) {
                insuranceCompanys = data;
            }, function () {
            });
            return insuranceCompanys;
        },
        getProvinces: function (formData) {
            let dataList;
            common.getByAjax(false, 'get', 'json', '/operationcenter/tide/resource/provinces', formData, function (data) {
                dataList = data
            }, function () {
            });
            return dataList;
        },
        getCities: function (provinceId) {
            let dataList;
            common.getByAjax(false, 'get', 'json', '/operationcenter/tide/resource/' + provinceId + '/cities', {}, function (data) {
                dataList = data
            }, function () {
            });
            return dataList;
        },
        getContractAreaInfo: function (contractId) {
            let areaViewModelList;
            common.getByAjax(false, 'get', 'json', '/operationcenter/tide/resource/contract/' + contractId + '/area', {}, function (data) {
                areaViewModelList = data;
            }, function () {
            });
            return areaViewModelList;
        },
        getContractFileInfo: function (contractId) {
            let fileViewModel;
            common.getByAjax(false, 'get', 'json', '/operationcenter/tide/resource/contract/' + contractId + '/file', {}, function (data) {
                fileViewModel = data;
            }, function () {
            });
            return fileViewModel;
        },
        getContractHistoryInfo: function (contractId) {
            let list;
            common.getByAjax(false, 'get', 'json', '/operationcenter/tide/resource/contract/' + contractId + '/history', {}, function (data) {
                list = data;
            }, function () {
            });
            return list;
        },

        updateContractAreaStatus: function (id, status) {
            common.getByAjax(false, 'post', 'json', '/operationcenter/tide/contract/contractArea/' + id + '/' + status, {}, function (data) {
            }, function () {
            });
        },
        updateContractStatus: function (id, disable) {
            common.getByAjax(false, 'post', 'json', '/operationcenter/tide/contract/' + id + '/' + disable, {}, function (data) {
            }, function () {
            });
        },

        addSupportArea: function (contractId, areaIds, callbackFunction) {
            let formData = {
                id: contractId,
                cityIds: areaIds
            };

            $.ajax({
                async: 'false',
                type: 'post',
                dataType: 'json',
                contentType: "application/json",
                url: '/operationcenter/tide/contract/contractArea/add',
                data: JSON.stringify(formData),
                success: function (data) {
                    debugger;
                    if (data.true) {
                        callbackFunction(data.true);
                    } else {
                        layer.alert(data.false, {icon: 5});
                        return false;
                    }
                },
                error: function (message) {
                    layer.alert("保存失败！", {icon: 2});
                    return false;
                }
            });
        }

    }
};
