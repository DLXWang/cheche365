//@ sourceURL=tide_contract_detail.js

var tide_contract_detail = {
    param: {
        contractId: 0
    },
    init: {
        initEffectiveLog: function () {
            let logs = tide_contract_detail.interface.getEffectiveLog().aaData;
            let content = '';
            $.each(logs, function (i, model) {
                content +=
                    `<tr>
                        <td>${model.createTime}</td>    
                        <td>${model.name}</td>    
                        <td>${model.mess}</td>
                    </tr>`;
            });
            $('.effectiveLogTableBody').html(content);
        },
        initCompanyLog: function () {
            let logs = tide_contract_detail.interface.getCompanyLog().aaData;
            let content = '';
            $.each(logs, function (i, model) {
                content +=
                    '<tr>' +
                    '    <td>' + model.createTime + '</td>' +
                    '    <td>' + model.name + '</</td>' +
                    '    <td>' + model.mess + '</td>' +
                    '</tr>';
            });
            $('.companyLogTableBody').html(content);
        },
        initAreaLog: function () {
            let areaLogs = tide_contract_detail.interface.getAreaLog().aaData;
            let content = '';
            $.each(areaLogs, function (i, model) {
                content +=
                    '<tr>' +
                    '    <td>' + model.createTime + '</td>' +
                    '    <td>' + model.name + '</</td>' +
                    '    <td>' + model.mess + '</td>' +
                    '</tr>';
            });
            $('.areaLogTableBody').html(content);
        },
        initProvince: function () {
            let areaList = tide_contract_common.interface.getProvinces();
            let content = '<option value="">请选择省</option>';
            $.each(areaList, function (i, model) {
                content += '<option value="' + model.id + '">' + model.name + '</option>';
            });
            $('.supportAreaProvinceSelAdd').html(content);
        },
        initCity: function (provinceId) {
            let content = ' ';
            if (provinceId) {
                let areaList = tide_contract_common.interface.getCities(provinceId);
                $.each(areaList, function (i, model) {
                    content += '<option value="' + model.id + '">' + model.name + '</option>';
                });
            }
            $('.supportAreaCitySelAdd').html(content);
            $('.supportAreaCitySelAdd').multiselect('destroy');
            $('.supportAreaCitySelAdd').multiselect({
                nonSelectedText: '请选择地区',
                buttonContainer: "<div class='multiselect-wrapper'/>",
                includeSelectAllOption: true,
                selectAllNumber: true,
                selectAllText: '全部',
                allSelectedText: '全部',
                nSelectedText: '个地区',
                onChange: function (option, checked) {
                    $('.supportAreaCitySelAdd').trigger('blur');
                }
            });
            $('.supportAreaCitySelAdd').multiselect('refresh');
        },
        fillFileInfo: function (fileViewModelList) {
            let fileContent = '';
            $.each(fileViewModelList, function (index, fileModel) {
                fileContent += '' +
                    '<div class="row">' +
                    '    <div class="form-group text-left">' +
                    '        <div class="col-sm-1 text-right"><i class="glyphicon glyphicon-link"> </i></div>' +
                    '        <div class="col-sm-5"><a href="' + fileModel.fileUrl + '" download>' + fileModel.fileName + '</a></div>' +
                    '        <div class="col-sm-1"><i fileId="' + fileModel.fileId + '" class="glyphicon glyphicon-trash fileDelete"> </i></div>' +
                    '        <div class="col-sm-2">' + fileModel.operator + '</div>' +
                    '        <div class="col-sm-3">' + fileModel.createTime + '</div>' +
                    '    </div>' +
                    '</div>';
            });
            $('.contractFileContentDiv').html(fileContent);
        },
        fillHistoryInfo: function (viewModelList) {
            let content = '';
            let maxNum = viewModelList.length;
            $.each(viewModelList, function (index, model) {
                content += '' +
                    '<tr>' +
                    '    <td>' + (maxNum - index) + '</td>' +
                    '    <td>' + model.effectiveDate + '</td>' +
                    '    <td>' + model.expireDate + '</td>' +
                    '</tr>';
            });
            $('.historyTableBody').html(content);
            $('#nextNum').html(maxNum + 1);
        },
        fillAreaInfo: function (areaViewModelList) {
            let areaContent = '';
            $.each(areaViewModelList, function (index, areaModel) {
                let checked = areaModel.disable ? '' : 'checked';
                areaContent += '<tr><td>' + areaModel.areaName + '</td><td><input type="checkbox" value="' + areaModel.contractAreaId + '" class="statusSwitch" ' + checked + ' /></td></tr>'
            });
            $('.areaTableBody').html(areaContent);

            $('.statusSwitch').bootstrapSwitch({
                onText: '<i class="glyphicon glyphicon-ok">',
                offText: '<i class="glyphicon glyphicon-remove" style="color:#666;">',
                size: "mini",
                onSwitchChange: function (event, state) {
                    let that = this;
                    if (!tide_contract_detail.operation.checkContractStatus()) {
                        $(that).bootstrapSwitch('state', !state, true);
                        return;
                    }

                    let statusString = state ? '启用' : '禁用';
                    layer.confirm('确认' + statusString + '此地区吗？', {closeBtn: 0}, function (index) {
                        tide_contract_common.interface.updateContractAreaStatus(that.value, !state);
                        tide_contract_detail.init.initAreaLog();
                        layer.close(index)
                    }, function () {
                        $(that).bootstrapSwitch('state', !state, true);
                    });
                }
            });

            $("#addSupportArea").removeClass('hidden');
        },
        initFileInfo: function () {
            let fileViewModelList = tide_contract_common.interface.getContractFileInfo(tide_contract_detail.param.contractId);
            tide_contract_detail.init.fillFileInfo(fileViewModelList);
        },
        initContractInfo: function () {
            let contractInfo = tide_contract_detail.interface.getContractInfo();
            let usable = !contractInfo.disable;
            if (usable) {
                $("#contractStatus").attr('checked', true);
            }
            for (let key in contractInfo) {
                $('.' + key).val(contractInfo[key]);
            }

            let areaViewModelList = contractInfo.areaViewModel;
            tide_contract_detail.init.fillAreaInfo(areaViewModelList);

            let fileViewModelList = contractInfo.fileViewModel;
            tide_contract_detail.init.fillFileInfo(fileViewModelList);

            let historyModelList = contractInfo.historyViewModel;
            tide_contract_detail.init.fillHistoryInfo(historyModelList);
        },
        initAreaInfo: function () {
            let areaViewModelList = tide_contract_common.interface.getContractAreaInfo(tide_contract_detail.param.contractId);
            tide_contract_detail.init.fillAreaInfo(areaViewModelList);
        }
    },
    operation: {
        renewalCancel: function () {
            $('#historyAddTR').addClass('hidden');
            $("#historyAddTR").find("input").val('');
        },
        doRenewal: function () {
            if ($("#historyAddForm").valid()) {
                let formData = {
                    contractId: tide_contract_detail.param.contractId,
                    effectiveDate: $('.effectiveDateAdd').val(),
                    expireDate: $('.expireDateAdd').val()
                };
                tide_contract_detail.interface.renewal(formData);
            }
        },
        checkContractStatus: function () {
            let contractUsable = $("#contractStatus").prop('checked');
            if (!contractUsable) {
                layer.msg('当前合约被禁用,不能执行此操作！');
                return false;
            }
            return true;
        },
        toRenewal: function () {
            $('#historyAddTR').removeClass('hidden');
            document.getElementById('historyPanelDiv').scrollIntoView(true);
            return false;
        },
        updateDescription: function () {
            if ($("#descriptionForm").valid()) {
                let formData = {
                    id: tide_contract_detail.param.contractId,
                    description: $('.description').val(),
                };
                tide_contract_detail.interface.updateContract(formData, function () {
                    $('.description').attr('disabled', true).addClass('form-control-static').removeClass('form-control');
                    $('.descriptionSave').addClass('hidden');
                    $('.descriptionEdit').removeClass('hidden');
                });
            }
        },
        updateCompanyInfo: function () {
            if ($("#companyInfoForm").valid()) {
                let flag = false;
                let formData = {};
                $.each($('.insuranceRefContentInfoDiv').find('input'), function (index, model) {
                    flag = $(model).attr('oldValue') === model.value;
                    if (!flag) {
                        formData[model.name] = model.value;
                    }
                });
                let revertInput = () => {
                    $('.companyInfoSave').closest('.panel').find('input').attr('disabled', true).addClass('form-control-static').removeClass('form-control');
                    $('.companyInfoSave').addClass('hidden');
                    $('.companyInfoEdit').removeClass('hidden');
                };
                if ($.isEmptyObject(formData)) {
                    revertInput();
                    return false;
                }
                formData.id = tide_contract_detail.param.contractId;
                tide_contract_detail.interface.updateContract(formData, function () {
                    layer.msg('更新成功！');
                    revertInput();
                    tide_contract_detail.init.initCompanyLog();
                });
            }
        },
        deleteFile: function (fileId) {
            common.getByAjax(false, 'post', 'json', '/operationcenter/tide/contract/file/del/' + fileId, {}, function (data) {
                let fileViewModelList = tide_contract_common.interface.getContractFileInfo(tide_contract_detail.param.contractId);
                tide_contract_detail.init.fillFileInfo(fileViewModelList);
            }, function () {
                layer.alert("删除失败!");
                return false;
            });
        },
        uploadFile: function () {
            tide_contract_detail.interface.uploadFile('contractFileAdd');
        }
    },
    interface: {
        renewal: function (formData) {
            $.ajax({
                async: 'false',
                type: 'post',
                dataType: 'json',
                contentType: "application/json",
                url: '/operationcenter/tide/contract/renewal',
                data: JSON.stringify(formData),
                success: function (data) {
                    if (data.pass) {
                        tide_contract_detail.operation.renewalCancel();
                        let historyLists = tide_contract_common.interface.getContractHistoryInfo(tide_contract_detail.param.contractId);
                        tide_contract_detail.init.fillHistoryInfo(historyLists);
                    }
                },
                error: function (message) {
                    layer.alert("保存失败！", {icon: 2});
                    return false;
                }
            });
        },
        getEffectiveLog: function () {
            let result;
            common.getByAjax(false, 'get', 'json', '/operationcenter/tide/log/contract_disable',
                {
                    keyword: tide_contract_detail.param.contractId,
                    pageSize: 20
                }, function (data) {
                    result = data;
                },
                function () {
                }
            );
            return result;
        },
        getCompanyLog: function () {
            let result;
            common.getByAjax(false, 'get', 'json', '/operationcenter/tide/log/contract_insurance_company',
                {
                    keyword: tide_contract_detail.param.contractId,
                    pageSize: 20
                }, function (data) {
                    result = data;
                },
                function () {
                }
            );
            return result;
        },
        getAreaLog: function () {
            let result;
            common.getByAjax(false, 'get', 'json', '/operationcenter/tide/log/tide_support_area',
                {
                    keyword: tide_contract_detail.param.contractId,
                    pageSize: 20
                }, function (data) {
                    result = data;
                },
                function () {
                }
            );
            return result;
        },
        updateContract: function (formData, successFuntion) {
            $.ajax({
                async: 'false',
                type: 'post',
                dataType: 'json',
                contentType: "application/json",
                url: '/operationcenter/tide/contract/update',
                data: JSON.stringify(formData),
                success: function (data) {
                    if (data.pass) {
                        layer.msg('更新成功！');
                        successFuntion();
                    } else {
                        layer.alert(data.message, {icon: 5});
                        return false;
                    }
                },
                error: function (message) {
                    layer.alert("保存失败！", {icon: 2});
                    return false;
                }
            });
        },
        getContractInfo: function () {
            let contractInfo;
            common.getByAjax(false, 'get', 'json', '/operationcenter/tide/contract/' + tide_contract_detail.param.contractId, {}, function (data) {
                    contractInfo = data;
                },
                function () {
                }
            );
            return contractInfo;
        },
        uploadFile: function (elementName) {
            let formData = new FormData();
            let file = $("." + elementName + "")[0].files[0];
            let fileName = $("." + elementName).val();

            if (common.isEmpty(fileName)) {
                layer.msg('请选择要上传的文件！');
                return false;
            }
            if (!fileName.toLowerCase().endsWith('pdf')) {
                layer.msg('只能上传pdf格式的文件! ');
                return false;
            }
            if ((file.size).toFixed(2) >= (20 * 1024 * 1024)) {
                layer.msg('请上传小于20M的文件！');
                return false;
            }
            formData.append("codeFile", file);
            formData.append("contractId", tide_contract_detail.param.contractId);
            let loadIndex;
            $.ajax({
                url: "/operationcenter/tide/contract/upload",
                type: 'POST',
                data: formData,
                processData: false, // 告诉jQuery不要去处理发送的数据
                contentType: false, // 告诉jQuery不要去设置Content-Type请求头
                beforeSend: function () {
                    loadIndex = layer.load(0, {shade: false});
                    $("." + elementName).val('');
                },
                success: function (responseStr) {
                    layer.close(loadIndex);
                    tide_contract_detail.init.initFileInfo();
                },
                error: function (responseStr) {
                    layer.close(loadIndex);
                    console.log(responseStr);
                    layer.alert('上传失败!', {icon: 2});
                }
            });
        }
    }
};

$(function () {
    tide_contract_detail.param.contractId = common.getUrlParam('id');
    tide_contract_detail.init.initContractInfo();
    tide_contract_detail.init.initProvince();
    tide_contract_detail.init.initCity(null);
    tide_contract_detail.init.initAreaLog();


    $('.contractFileDiv').on("click", '.fileDelete', function () {
        let fileId = $(this).attr('fileId');
        tide_contract_detail.operation.deleteFile(fileId);
    });

    $('#contractStatus').bootstrapSwitch({
        onText: "已启用",
        offText: "已禁用",
        size: "mini",
        onSwitchChange: function (event, state) {
            let that = this;
            let statusString = state ? '启用' : '禁用';
            layer.confirm('确认' + statusString + '此合约吗？', {closeBtn: 0}, function (index) {
                tide_contract_common.interface.updateContractStatus(tide_contract_detail.param.contractId, !state);
                layer.close(index);
                tide_contract_detail.init.initAreaInfo();
                tide_contract_detail.init.initAreaLog();
                tide_contract_detail.init.initEffectiveLog();
            }, function () {
                $(that).bootstrapSwitch('state', !state, true);
            });
        }
    });

    $("#addSupportArea").on("click", function () {
        $("#supportAreaAddTR").removeClass('hidden');
        $(this).addClass('hidden');
        tide_contract_detail.init.initProvince();
        tide_contract_detail.init.initCity(null);
    });

    $('.supportAreaProvinceSelAdd').on("change", function () {
        tide_contract_detail.init.initCity($(this).val());
    });

    $('.saveSupportArea').on("click", function () {
        let selectedCitys = [];
        $('.supportAreaCitySelAdd option:selected').each(function () {
            selectedCitys.push($(this).val());
        });
        if (selectedCitys && selectedCitys.length === 0) {
            layer.tips('请勾选城市！ ', '#supportAreaCitySelAddDiv');
            return false;
        }
        tide_contract_common.interface.addSupportArea(tide_contract_detail.param.contractId, selectedCitys, function (areaViewModelList) {
            tide_contract_detail.init.fillAreaInfo(areaViewModelList);
            $("#addSupportArea").removeClass("hidden");
            $("#supportAreaAddTR").addClass('hidden');
            tide_contract_detail.init.initAreaLog();
        });
    });

    $('.cancelSupportArea').on("click", function () {
        $("#addSupportArea").removeClass("hidden");
        $("#supportAreaAddTR").addClass('hidden');
    });

    $(".companyInfoEdit").on('click', function () {
        $(this).addClass('hidden');
        $('.companyInfoSave').removeClass('hidden');
        $(this).closest('.panel').find('input').removeAttr('disabled').removeClass('form-control-static').addClass('form-control');
        $.each($(this).closest('.panel').find('input'), function (index, each) {
            $(each).attr('oldValue', $(each).val());
        });
    });

    $(".companyInfoSave").on('click', function () {
        tide_contract_detail.operation.updateCompanyInfo();
    });

    $('.descriptionEdit').on('click', function () {
        $('.description').removeAttr('disabled').removeClass('form-control-static').addClass('form-control');
        $(this).addClass('hidden');
        $('.descriptionSave').removeClass('hidden');
    });

    $('.descriptionSave').on('click', function () {
        tide_contract_detail.operation.updateDescription();
    });

    $('a[href="#companyInfoLogDiv"]').on('shown.bs.tab', function (e) {
        tide_contract_detail.init.initCompanyLog()
    });

    $('a[href="#effectiveLogDiv"]').on('shown.bs.tab', function (e) {
        tide_contract_detail.init.initEffectiveLog()
    });
});
