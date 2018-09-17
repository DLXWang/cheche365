//@ sourceURL=tide_contract_add.js

var tide_institution_add = {
    param: {
        branchId: 0
    },
    init: {},
    operation: {
        submit: function (index) {
            if ($('#institutionForm').valid()) {
                let formData = {
                    branchId: $(".branchSelAdd").val(),
                    institutionName: $('.tideInstitutionAdd').val(),
                    description: $(".tideDescriptionAdd").val(),
                };
                tide_institution_add.interface.save(formData, index);
            }
            return false;
        }
    },
    interface: {
        save: function (formData, index) {
            $.ajax({
                async: 'false',
                type: 'post',
                dataType: 'json',
                contentType: "application/json",
                url: '/operationcenter/tide/institution/add',
                data: JSON.stringify(formData),
                success: function (data) {
                    if (data.pass) {
                        tide_contract_add.init.initTideInstitions();
                        tide_contract.init.initTideBranch();
                        tide_contract.init.initTideInstitions();
                        layer.close(index);
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
        }
    }
};


var tide_contract_add = {
    param: {},
    init: {
        initTidePlatform: function () {
            let platformList = tide_contract_common.interface.getPlatform();
            if (platformList && platformList.length > 0) {
                let content = '<option value="">请选择</option>';
                $.each(platformList, function (i, model) {
                    content += '<option value="' + model.id + '">' + model.name + '</option>';
                });
                $('.platformSelAdd').html(content);
            }
            tide_contract.init.initPlatParam();
        },
        initTideBranch: function () {
            let paramData = {
                platformId: $('.platformSelAdd').val()
            };
            let tideBranchList = tide_contract_common.interface.getBranch(paramData);
            let content = '<option value="">请选择</option>';
            $.each(tideBranchList, function (i, tideBranch) {
                content += '<option value="' + tideBranch.id + '">' + tideBranch.name + '</option>';
            });
            $('.branchSelAdd').html(content);
        },
        initTideInstitions: function () {
            let formData = {
                branchId: $('.branchSelAdd').val(),
                platformId: $('.platformSelAdd').val()
            };
            let institutionList = tide_contract_common.interface.getTideInstitution(formData);
            let content = '<option value="">请选择</option>';
            $.each(institutionList, function (i, institution) {
                content += '<option value="' + institution.id + '">' + institution.name + '</option>';
            });
            $('.institutionSelAdd').html(content);
        },
        initInsuranceCompany: function () {
            let insuranceCompanys = tide_contract_common.interface.initInsuranceCompany();
            let content = '<option value="">请选择</option>';
            $.each(insuranceCompanys, function (i, insuranceCompany) {
                content += '<option value="' + insuranceCompany.id + '">' + insuranceCompany.name + '</option>';
            });
            $('.insuranceCompanySelAdd').html(content);
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
            let areaList = tide_contract_common.interface.getCities(provinceId);
            let content = '';
            $.each(areaList, function (i, model) {
                content += '<option value="' + model.id + '">' + model.name + '</option>';
            });
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
        }
    },
    operation: {
        submit: function (index) {
            if ($('#contractForm').valid()) {
                let selectedCitys = [];
                $('.supportAreaCitySelAdd option:selected').each(function () {
                    selectedCitys.push($(this).val());
                });
                let fileIds = [];
                $('.files').each(function () {
                    fileIds.push($(this).attr('fileId'));
                });
                let formData = {
                    platformId: $('.platformSelAdd').val(),
                    branchId: $(".branchSelAdd").val(),
                    institutionId: $(".institutionSelAdd").val(),
                    insuranceCompanyId: $(".insuranceCompanySelAdd").val(),
                    contractName: $('.contractNameAdd').val(),
                    effectiveDate: $(".effectiveDateAdd").val(),
                    expireDate: $(".expireDateAdd").val(),
                    partnerUserName: $(".partnerUserNameAdd").val(),
                    partnerPassword: $(".partnerPasswordAdd").val(),
                    orderCode: $(".orderCodeAdd").val(),
                    description: $(".descriptionAdd").val(),
                    fileIds: fileIds,
                    cityIds: selectedCitys,
                    loginUrl: $('.loginUrlAdd').val(),
                    contractCode: $('.contractCodeAdd').val()
                };
                return tide_contract_add.interface.save(formData, index);
            }
            return false;
        },
        uploadFile: function () {
            tide_contract_add.interface.uploadFile('contractFileAdd');
        },
        toAddTideInstitution: function () {
            let branchId = $(".branchSelAdd").val();
            if (common.isEmpty(branchId)) {
                layer.tips('请先选择营业部! ', '.branchSelAdd');
                return false;
            }
            $.get("/views/tide/tide_institution_add.html", {}, function (content) {
                layer.open({
                    type: 1,
                    title: '新建保险公司',
                    skin: 'layui-layer-rim', //加上边框
                    area: ['40%', '40%'], //宽高
                    btn: ['提交', '取消'],
                    btnAlign: 'c',
                    content: content,
                    scrollbar: false,
                    yes: function (index, layero) {
                        tide_institution_add.operation.submit(index);
                    },
                    btn2: function (index, layero) {
                    },
                    success: function (index, layero) {
                        $(':focus').blur();
                    }
                });
            });
        }
    },
    interface: {
        save: function (formData, pIndex) {
            $.ajax({
                async: 'false',
                type: 'post',
                dataType: 'json',
                contentType: "application/json",
                url: '/operationcenter/tide/contract/add',
                data: JSON.stringify(formData),
                success: function (data) {
                    if (data.pass) {
                        layer.alert("保存成功!", {closeBtn: 0}, function (index, layerDom) {
                            tide_contract.param.dataTable.ajax.reload();
                            layer.close(index);
                            layer.close(pIndex);
                        });
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
        uploadFile: function (elementName) {
            let formData = new FormData();
            let file = $("." + elementName + "")[0].files[0];
            formData.append("codeFile", file);
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
            let loadIndex;
            $.ajax({
                url: "/operationcenter/tide/contract/upload",
                type: 'POST',
                data: formData,
                processData: false, // 告诉jQuery不要去处理发送的数据
                contentType: false, // 告诉jQuery不要去设置Content-Type请求头
                beforeSend: function () {
                    loadIndex = layer.load(0, {shade: false});
                },
                success: function (responseStr) {
                    layer.close(loadIndex);
                    let result = JSON.parse(responseStr);
                    $('.display-file').append('' +
                        '<div class="fileDisplayRow col-sm-12"> ' +
                        '   <a class=""><i fileId="' + result.id + '" class="glyphicon glyphicon-remove fileDelete"> </i></a> ' +
                        '   <span class="files" fileId="' + result.id + '">' + result.fileName + '</span>' +
                        '</div>');
                    $("." + elementName).val('');
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
    tide_contract_add.init.initTidePlatform();
    tide_contract_add.init.initProvince();
    tide_contract_add.init.initTideBranch();
    tide_contract_add.init.initInsuranceCompany();

    $('.platformSelAdd').bind({
        change: function () {
            tide_contract_add.init.initTideBranch();
            tide_contract_add.init.initTideInstitions();
        }
    });

    $('.branchSelAdd').bind({
        change: function () {
            tide_contract_add.init.initTideInstitions();
        }
    });


    $('.supportAreaProvinceSelAdd').bind({
        change: function () {
            $('.supportAreaCitySelAdd').removeClass('hidden');
            tide_contract_add.init.initCity($(this).val());
        }
    });


    $('div').on('click', '.fileDelete', function () {
        $(this).closest('.fileDisplayRow').remove();
    });


});
