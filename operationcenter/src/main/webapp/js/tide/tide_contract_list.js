//@ sourceURL=tide_contract_list.js

var tide_contract = {
    param: {
        platformId: 0,
        platformName: '',

        allInsuranceCompanyOptions: '',
        dataTable: null
    },
    dt_table: {
        param: function (paramMap) {
            paramMap.platformId = $(".platformSelSearch").val();
            paramMap.branchId = $(".branchSelSearch").val();
            paramMap.institutionId = $(".institutionSelSearch").val();
            paramMap.contractName = $(".contractNameSearch").val();
        },
        dt_list: {
            url: "/operationcenter/tide/contract",
            type: "get",
            table_id: "tide_contract_list_table",
            columns: [
                {
                    "data": "contractCode",
                    "title": "合约编号",
                    'sClass': "text-center contractCodeTd width_50",
                    "orderable": false
                },
                {
                    "data": "contractName",
                    "title": "合约名称",
                    'sClass': "text-center contractNameTd width_50",
                    "orderable": false
                },
                {
                    "data": "platformName", "title": "平台机构",
                    'sClass': "text-center platformNameTd",
                    "orderable": false
                },
                {
                    "data": "branchName",
                    "title": "营业部(牌照)",
                    'sClass': "text-center branchNameTd",
                    "orderable": false
                },
                {
                    "data": "institutionName",
                    "title": "保险公司分支",
                    'sClass': "text-center institutionNameTd",
                    "orderable": false
                },
                {
                    "data": "insuranceCompanyName",
                    "title": "保险公司",
                    'sClass': "text-center insuranceCompanyNameTd",
                    "orderable": false
                },
                {
                    "data": "disable",
                    "title": "是否启用",
                    render: function (disable, type, row) {
                        if (type === 'display') {
                            let checkedFlag = !disable ? 'checked' : '';
                            return '<input class="contractDisable" value="' + row.id + '" type="checkbox" ' + checkedFlag + '/>';
                        }
                        return "";
                    },
                    'sClass': "text-center disableTd",
                    "orderable": false
                },
                {
                    data: "id",
                    "title": '操作',
                    render: function (data, type, row) {
                        if (type === 'display') {
                            return '<a class="checkbox-width" onclick="tide_contract.operation.toDetail(' + data + ');">查看详情</a>';
                        }
                        return "";
                    },
                    className: "text-center",
                    "orderable": false
                }
            ]
        }
    },
    init: {
        initPlatParam: function () {
            tide_contract.param.platformId = $(".platformSelSearch").val();
            tide_contract.param.platformName = $('.platformSelSearch').text();
        },
        initTidePlatform: function () {
            let platformList = tide_contract_common.interface.getPlatform();
            if (platformList && platformList.length > 0) {
                $('button').removeAttr('disabled');
                let content = '<option value="">请选择</option>';
                $.each(platformList, function (i, model) {
                    content += '<option value="' + model.id + '">' + model.name + '</option>';
                });
                $('.platformSelSearch').html(content);
            }
            tide_contract.init.initPlatParam();
        },
        initTideBranch: function () {
            let paramData = {
                platformId: tide_contract.param.platformId
            };
            let tideBranchList = tide_contract_common.interface.getBranch(paramData);
            let content = '<option value="">请选择</option>';
            $.each(tideBranchList, function (i, tideBranch) {
                content += '<option value="' + tideBranch.id + '">' + tideBranch.name + '</option>';
            });
            $('.branchSelSearch').html(content);
        },
        initTideInstitions: function () {
            let formData = {
                platformId: $('.platformSelSearch').val(),
                branchId: $('.branchSelSearch').val()
            };
            let institutionList = tide_contract_common.interface.getTideInstitution(formData);
            let content = '<option value="">请选择</option>';
            $.each(institutionList, function (i, institution) {
                content += '<option value="' + institution.id + '">' + institution.name + '</option>';
            });
            $('.institutionSelSearch').html(content);
        },
        initInsuranceCompany: function () {
            let insuranceCompanys = tide_contract.interface.initInsuranceCompany();
            $.each(insuranceCompanys, function (i, insuranceCompany) {
                tide_contract.param.allInsuranceCompanyOptions += '<option value="' + insuranceCompany.id + '">' + insuranceCompany.name + '</option>';
            });
            $('#search_div .insuranceCompanySelSearch').append(tide_contract.param.allInsuranceCompanyOptions);
            $('#search_div .insuranceCompanySelSearch').multiselect({
                nonSelectedText: '请选择保险公司',
                buttonContainer: "<div class='multiselect-wrapper'/>",
                buttonWidth: '100%',
                width: '100%',
                maxWidth: '100%',
                maxHeight: '400px',
                includeSelectAllOption: false,
                selectAllNumber: true,
                selectAllText: '全部',
                allSelectedText: '全部',
                nSelectedText: '个保险公司'
            });
            $('#search_div .insuranceCompanySelSearch').multiselect('refresh');
        }
    },
    operation: {
        toAdd: function () {
            $.get("/views/tide/tide_contract_add.html", {}, function (content) {
                layer.open({
                    type: 1,
                    title: '新建合约',
                    skin: 'layui-layer-rim', //加上边框
                    area: ['75%', '75%'], //宽高
                    btn: ['提交', '取消'],
                    btnAlign: 'c',
                    content: content,
                    scrollbar: false,
                    yes: function (index, layero) {
                        tide_contract_add.operation.submit(index);
                    },
                    btn2: function (index, layero) {
                    },
                    success: function (index, layero) {
                        $(':focus').blur();
                    }
                });
            });
        },
        toDetail: function (contractId) {
            window.open("/views/tide/tide_contract_detail.html?id=" + contractId);
        }
    },
    interface: {}
};

$(function () {
    tide_contract.init.initTidePlatform();
    tide_contract.init.initTideBranch();
    tide_contract.init.initTideInstitions();
    tide_contract.param.dataTable = datatableUtil.getByDatatables(tide_contract.dt_table.dt_list, tide_contract.dt_table.param, function () {
    });

    tide_contract.param.dataTable.on("draw", function () {
        $('#tide_contract_list_table .contractDisable').bootstrapSwitch({
            onText: '<i class="glyphicon glyphicon-ok">',
            offText: '<i class="glyphicon glyphicon-remove" style="color:#666;">',
            size: "mini",
            onSwitchChange: function (event, state) {
                let that = this;
                let statusString = state ? '启用' : '禁用';
                layer.confirm('确认' + statusString + '此合约吗？', {closeBtn: 0}, function (index) {
                    let contractId = that.value;
                    tide_contract_common.interface.updateContractStatus(contractId, !state);
                    layer.close(index);
                }, function () {
                    $(that).bootstrapSwitch('state', !state, true);
                });
            }
        });
    });

    $('.param_search_button').click(function () {
        tide_contract.param.dataTable.ajax.reload();
    });

    $('.param_reset_button').click(function () {
        $('.param_search_item').val('');
    });

    $('.platformSelSearch').bind({
        change: function () {
            tide_contract.init.initPlatParam();
            tide_contract.init.initTideBranch();
            tide_contract.init.initTideInstitions();
        }
    });

    $('.branchSelSearch').bind({
        change: function () {
            tide_contract.init.initTideInstitions();
        }
    });
});
