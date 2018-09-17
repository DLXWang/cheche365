//@ sourceURL=channelRebate.js
/**
 * Created by cxy on 2016/12/13.
 */

var dt_labels = {
    "aLengthMenu": [[10, 15, 20], [10, 15, 20]],//暂时只写这一个
    "order": true,
    "hasCallBack": true,
    "hasDrawCallback": false,
    "hasData": true,
    "bPaginate": true,// 分页按钮
    "bLengthChange": true,
    "paging": true,
    "info": true,
    "selected": [],
    "tableId": "",
    "checkedType": "tr",
    "language":    //DataTable中文化
        {
            "sProcessing": "正在加载中......",
            "sLengthMenu": "每页显示 _MENU_ 条记录",
            "sZeroRecords": "对不起，查询不到相关数据！",
            "sEmptyTable": "表中无数据存在！",
            "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录",
            "sInfoFiltered": "数据表中共为 _MAX_ 条记录",
            "sInfoEmpty": "",
            "sSearch": "搜索",
            "oPaginate": {
                "sFirst": "首页",
                "sPrevious": "上一页",
                "sNext": "下一页",
                "sLast": "末页"
            }
        }
};
var datatableUtil = {
    getByDatatables: function (dataList, dataFunc, callBackFunc) {
        dt_labels.tableId = dataList.table_id;
        let datatables = $('#' + dataList.table_id).DataTable({
            //let param;param[11] = 12;
            "paging": dt_labels.paging,
            "aLengthMenu": dt_labels.aLengthMenu,
            "order": dt_labels.order,
            "processing": true,
            "searching": false,
            "bLengthChange": dt_labels.bLengthChange, //改变每页显示数据数量
            "bFilter": true, //过滤功能
            "bSort": true, //排序功能
            "serverSide": true,
            "info": dt_labels.info,
            "dom": '<"row"<"col-sm-6"f>>' + '<"row"<"col-sm-12"tr>>' + '<"row"<"col-sm-3"i><"col-sm-7"p><"col-sm-2"l>>',
            "oLanguage": dt_labels.language,  //DataTable中文化
            "bAutoWidth": false, //是否自适应宽度
            "bScrollInfinite": true, //是否启动初始化滚动条,
            ajax: {
                "type": dataList.type,
                "url": dataList.url,
                //"data" :param,
                "data": function (data) {
                    window.parent.scrollTo(0, 0);
                    data.currentPage = data.start / data.length + 1;
                    data.pageSize = data.length;
                    if (dt_labels.hasData) {
                        dataFunc(data);
                    }
                },
                error: function (xhr) {
                    if (!common.sessionTimeOut(xhr) && !common.accessDenied(xhr) && !common.noPermissionLogin(xhr)) {
                        layer.alert("获取列表失败！", {time: 1000});
                    }
                }
            },
            "bPaginate": dt_labels.bPaginate,// 分页按钮

            "sPaginationType": "full_numbers",
            "columns": dataList.columns,
            "fnRowCallback": function (nRow, aData) {
                $(nRow).find('.input_flag').on('mouseover', function () {
                    if (this.placeholder) {
                        this.title = this.placeholder;
                    }
                });
                if ($.inArray(aData.id + "", dt_labels.selected) !== -1) {
                    $(nRow).find(".check-box-single").attr("checked", true);
                }
                if (dt_labels.hasCallBack) {
                    callBackFunc(nRow, aData);
                }

            },
            "fnDrawCallback": function () {
                dt_labels.selected = [];
                if (dt_labels.hasDrawCallback) {
                    let api = this.api();
                    let startIndex = api.context[0]._iDisplayStart;//获取到本页开始的条数
                    api.column(0).nodes().each(function (cell, i) {
                        cell.innerHTML = startIndex + i + 1;
                    });
                }
            }
        });

        let tableId = dataList.table_id;
        datatables.on("draw", function () {
            $("#" + tableId + " tbody tr:odd").css("background-color", "");
            $("#" + tableId + " tbody tr:even").css("background-color", "#ececec");
        });

        datatables.on('page.dt', function (e) {
            let length = $('#' + tableId).find('.input_flag').not('.hidden').not('.useless').parents('tr').length;
            if (length > 0) {
                layer.alert("请先保存新增或修改的数据！", {time: 1000});
                throw '先保存数据';
            }
        });

        let $selectLength = $("select[name=" + tableId + "_length]");
        $selectLength.addClass('form-control');
        $selectLength.width("60px");
        $selectLength.css("margin-top", "5px");
        $selectLength.css("margin-bottom", "10px");

        $("#" + dt_labels.tableId + " tbody").on('click', dt_labels.checkedType, function () {
            let id = $(this).parents('tr').find('.check-box-single').val();
            let index = $.inArray(id, dt_labels.selected);
            if (index === -1) {
                dt_labels.selected.push(id);
                $(this).parents('tr').find('.check-box-single').prop("checked", true);
                $(this).parents('tr').find('.input_flag,.save_button_single').removeClass('useless');
                channel_rebate.operation.editRow($(this).parents('tr').find('.edit_button_single'));
            } else {
                if ($(this).hasClass('text-center')) {//如果点击的是链接  就什么都不干了
                    dt_labels.selected.splice(index, 1);
                    $(this).find(".check-box-single").prop("checked", false);
                    revertData($(this).parents('tr'));
                } else {
                }
            }
        });

        function revertData(thistr) {
            $(thistr).find('.input_flag').map(function () {
                this.value = $(this).prev('span').html();
            });
            thistr.find('.value_span').removeClass('hidden');
            thistr.find('.input_flag').addClass('hidden');

            thistr.find('.save_button_single').html('编辑');
            thistr.find('.save_button_single').addClass('edit_button_single');
            thistr.find('.save_button_single').removeClass('save_button_single');
            channel_rebate.operation.countChecked();
            thistr.find('a').removeClass('useless');
            thistr.find('.edit_button_single').attr('onclick', 'channel_rebate.operation.editRow(this);');
        }

        $(".check-box-all").bind({
            click: function () {
                let checked = $(this).prop("checked");
                let $checkBoxSingle = $(".check-box-single");
                if (checked) {
                    $checkBoxSingle.prop("checked", true);
                    channel_rebate.operation.checkAll();
                    $checkBoxSingle.each(function (i) {
                        if ($.inArray($(this).val(), dt_labels.selected) === -1) {
                            dt_labels.selected.push($(this).val());
                        }
                    });
                } else {
                    let $batchReadyEffectiveDate = $(".batch_readyEffectiveDate");
                    $batchReadyEffectiveDate.val(null);
                    $batchReadyEffectiveDate.prop('disabled', true);
                    $checkBoxSingle.prop("checked", false).trigger('click');
                }
            }
        });

        return datatables;
    },
    /* ajax请求 */
    params: {
        userRoles: "",
        keyType: "",
        keyWord: ""
    }
};

var channel_rebate = {
    param: {
        rowId: 0,
        channelRebateId: 0,
        search_channelId: 0,
        statusOptions: '',
        clientTypeOptions: '<option value="1">ToA</option><option value="2">ToC</option>',
        channelTypeOptions: '<option value="1">自有渠道</option><option value="2">第三方渠道</option>',
        allChannelOptions: '',
        ownChannelOptions: '',
        partnerChannelOptions: '',
        provinceOptions: '',
        insuranceCompanyOptions: '',
        allInsuranceCompanyOptions: '',
        dataTable: null,
        dataTableHistory: null,
        channelRebateForm: {
            id: '',
            clientType: '',
            channelType: '',
            channelId: '',
            channelName: '',
            areaId: '',
            areaName: '',
            insuranceCompanyId: '',
            insuranceCompanyName: '',
            effectiveDate: '',
            commercialRebate: '',
            compulsoryRebate: '',
            status: '',
            readyEffectiveDate: '',
            readyCommercialRebate: '',
            readyCompulsoryRebate: '',
            createTime: '',
            updateTime: '',
            operator: ''
        },
        directCitys: ['110000', '120000', '310000', '500000'],
        NewChannelRebateForm: function () {
            let temp = {};
            $.extend(temp, channel_rebate.param.channelRebateForm);
            return temp;
        }
    },
    changeValue: function () {
        $("#codeFileFake").val($("#codeFile").val());
    },
    uploadFile: function () {
        common.excelImport("/operationcenter/channelRebate/upload", $("#file_form"), $("#codeFile").val(), function (response) {
            channel_rebate.param.dataTable.ajax.reload();
        });
    },
    initTemplateUrl: function () {
        common.getByAjax(true, "get", "json", "/operationcenter/channelRebate/template/url", null, function (response) {
            $("#url_template").prop("href", response.message);
        }, function () {
            layer.alert("模版地址初始化异常！！", {time: 1000});
        });
    },
    dt_table: {
        dt_list: {
            url: "/operationcenter/channelRebate",
            type: "get",
            table_id: "channel_rebate_list_table",
            columns: [
                {
                    data: "id",
                    "title": '<input type="checkbox" class="data-checkbox check-box-all width_50">',
                    render: function (data, type, row) {
                        if (type === 'display') {
                            return '<input type="checkbox" value="' + data + '" class="data-checkbox check-box-single">';
                        }
                        return data;
                    },
                    className: "text-center checkbox-width",
                    "orderable": false
                },
                {
                    "data": "id",
                    "title": "是否选中",
                    render: function (data, type, row) {
                        return 'true';
                    },
                    'sClass': "checkedFlagtd text-center hidden",
                    "orderable": false
                },
                {"data": "id", "title": "ID", 'sClass': "text-center idtd width_50", "orderable": false},
                {
                    "data": "clientType",
                    "title": "客户类型",
                    'sClass': "text-center clientTypetd",
                    "orderable": false
                },
                {
                    "data": "channelType", "title": "渠道类型",
                    'sClass': "text-center channelTypetd",
                    "orderable": false
                },
                {
                    "data": "channelName", "title": "渠道名称",
                    'sClass': "text-center channelNametd",
                    "orderable": false
                },
                {
                    "data": "areaName",
                    "title": "地区",
                    'sClass': "text-center areaNametd",
                    "orderable": false
                },
                {
                    "data": "insuranceCompanyName",
                    "title": "保险公司",
                    'sClass': "text-center insuranceCompanyNametd",
                    "orderable": false
                },
                {
                    "data": "effectiveDate",
                    "title": "生效时间",
                    'sClass': "text-center effectiveDatetd",
                    "orderable": false
                },
                {
                    "data": "onlyCommercialRebate",
                    "title": "单商业险",
                    render: function (data, type, row) {
                        return `<span class="value_span">${data}</span><input type="text" class="commercialRebate form-control input_flag hidden" value="${data}" placeholder="请输入单商业险">`;
                    },
                    'sClass': "text-center onlyCommercialRebatetd",
                    "orderable": false
                },
                {
                    "data": "onlyCompulsoryRebate",
                    "title": "单交强险",
                    render: function (data, type, row) {
                        return `<span class="value_span">${data}</span><input type="text" class="compulsoryRebate form-control input_flag hidden" value="${data}" placeholder="请输入单交强险">`;
                    },
                    'sClass': "text-center onlyCompulsoryRebatetd",
                    "orderable": false
                },
                {
                    "data": "commercialRebate",
                    "title": "组合商业险",
                    render: function (data, type, row) {
                        return `<span class="value_span">${data}</span><input type="text" class="commercialRebate form-control input_flag hidden" value="${data}" placeholder="请输入组合商业险">`;
                    },
                    'sClass': "text-center commercialRebatetd",
                    "orderable": false
                },
                {
                    "data": "compulsoryRebate",
                    "title": "组合交强险",
                    render: function (data, type, row) {
                        return `<span class="value_span">${data}</span><input type="text" class="compulsoryRebate form-control input_flag hidden" value="${data}" placeholder="请输入组合交强险">`;
                    },
                    'sClass': "text-center compulsoryRebatetd",
                    "orderable": false
                },
                {
                    "data": "readyEffectiveDate",
                    "title": "预生效时间",
                    render: function (data, type, row) {
                        return `<span class="value_span">${common.checkToEmpty(data)}</span>
                                <input type="text" class="readyEffectiveDate text-input-50 form-inline form-control input_flag hidden batch_readyEffectiveDate_single datePicker" value="${common.checkToEmpty(data)}" placeholder="预生效时间" 
                                    onfocus="WdatePicker({onpicked:function(dq){channel_rebate.operation.fillReadyRebate(this);},dateFmt:'yyyy-MM-dd',readOnly:true,minDate:'%y-%M-{%d+1}',startDate:'%y-%M-{%d+1}'});">`;
                    },
                    'sClass': "text-center readyEffectiveDatetd",
                    "orderable": false
                },
                {
                    "data": "onlyReadyCommercialRebate",
                    "title": "预生效单商业险",
                    render: function (data, type, row) {
                        return `<span class="value_span">${data}</span><input type="text" class="readyCommercialRebate form-control input_flag hidden" value="${data}" placeholder="请输入预生效单商业险">`;
                    },
                    'sClass': "text-center onlyReadyCommercialRebatetd",
                    "orderable": false
                },
                {
                    "data": "onlyReadyCompulsoryRebate",
                    "title": "预生效单交强险",
                    render: function (data, type, row) {
                        return `<span class="value_span">${data}</span><input type="text" class="readyCompulsoryRebate form-control input_flag hidden" value="${data}" placeholder="请输入预生效单交强险">`;
                    },
                    'sClass': "text-center onlyReadyCompulsoryRebatetd",
                    "orderable": false
                },
                {
                    "data": "readyCommercialRebate",
                    "title": "预生效组合商业险",
                    render: function (data, type, row) {
                        return `<span class="value_span">${data}</span><input type="text" class="readyCommercialRebate form-control input_flag hidden" value="${data}" placeholder="请输入预生效组合商业险">`;
                    },
                    'sClass': "text-center readyCommercialRebatetd",
                    "orderable": false
                },
                {
                    "data": "readyCompulsoryRebate",
                    "title": "预生效组合交强险",
                    render: function (data, type, row) {
                        return `<span class="value_span">${data}</span><input type="text" class="readyCompulsoryRebate form-control input_flag hidden" value="${data}" placeholder="请输入预生效组合交强险">`;
                    },
                    'sClass': "text-center readyCompulsoryRebatetd",
                    "orderable": false
                },
                {
                    data: "id",
                    "title": '操作',
                    render: function (data, type, row) {
                        if (type === 'display') {
                            return `<a style="margin-left: 10px;color: coral;" class="checkbox-width edit_button_single" onclick="channel_rebate.operation.editRow(this);">编辑</a>
                                    <a style="margin-left: 10px;color: coral;" onclick="channel_rebate.operation.toDescription(${data})">查看出险政策</a>
                                    <a style="margin-left: 10px;color: coral;" onclick="channel_rebate.operation.toHistory(${data})">查看历史费率</a>`;
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
        initClientType: {},
        initStatus: function () {
            let statusMapping = channel_rebate.interface.initStatus();
        },
        initChannels: function () {
            let $ownChannelSel = $('#search_div .ownChannelSel');
            let channels = channel_rebate.interface.getChannels(channel_rebate.param.NewChannelRebateForm());
            $.each(channels, function (i, channel) {
                channel_rebate.param.allChannelOptions += `<option value="${channel.id}">${channel.description}</option>`
            });
            $ownChannelSel.html(channel_rebate.param.allChannelOptions);
            $ownChannelSel.multiselect({
                nonSelectedText: '请选择渠道名称',
                buttonWidth: '225',
                maxHeight: '400',
                includeSelectAllOption: false,
                selectAllNumber: false,
                selectAllText: '全部',
                allSelectedText: '全部',
                nSelectedText: '个渠道'
            });
            $ownChannelSel.multiselect('refresh');
        },
        initThirdPartChannelType: {},
        initInsuranceCompany: function () {
            let insuranceCompanys = channel_rebate.interface.initInsuranceCompany();
            $.each(insuranceCompanys, function (i, insuranceCompany) {
                channel_rebate.param.allInsuranceCompanyOptions += `<option value="${insuranceCompany.id}">${insuranceCompany.name}</option>`;
            });
            let $insuranceCompanySel = $('#search_div .insuranceCompanySel');
            $insuranceCompanySel.append(channel_rebate.param.allInsuranceCompanyOptions);
            $insuranceCompanySel.multiselect({
                nonSelectedText: '请选择保险公司',
                buttonWidth: '160',
                maxHeight: '400',
                includeSelectAllOption: false,
                selectAllNumber: true,
                selectAllText: '全部',
                allSelectedText: '全部',
                nSelectedText: '个保险公司'
            });
            $insuranceCompanySel.multiselect('refresh');
        },
        initProvince: function () {
            channel_rebate.interface.initAddress(1, 1, function (data) {
                let options = '<option value="">请选择省份</option>';
                if (data.length > 0) {
                    $.each(data, function (i, model) {
                        options += `<option value="${model.id}">${model.name}</option>`;
                    });
                    $('.provinceSel').html(options);
                    channel_rebate.param.provinceOptions = options;
                }
            });
        }
    },
    operation: {
        fillReadyRebate: function (thisInput) {
            let commVal = $(thisInput).parents('tr').find('.readyCommercialRebate').val();
            commVal ? console.log('do nothing') : $(thisInput).parents('tr').find('.readyCommercialRebate').val();
            let compVal = $(thisInput).parents('tr').find('.readyCompulsoryRebate').val();
            compVal ? console.log('do nothing') : $(thisInput).parents('tr').find('.readyCompulsoryRebate').val();
        },
        countChecked: function () {
            let $check = $('.check-box-single:checked');
            let length = $check.length;
            if (length > 1) {
                $check.parents('tr').find('a').addClass('useless');
                $('.batch_readyEffectiveDate').prop('disabled', false);
                $('.batch_save_button').removeClass('disabled');
            } else {
                $check.parents('tr').find('a').removeClass('useless');
                $('.batch_readyEffectiveDate').prop('disabled', true);
                $('.batch_save_button').addClass('disabled');
            }
        },
        checkAll: function () {
            let $editButtonSingle = $('.edit_button_single');
            let $saveButtonSingle = $('.save_button_single');
            $('.value_span').addClass('hidden');
            $('.input_flag').removeClass('hidden');
            $editButtonSingle.html('保存');
            $editButtonSingle.addClass('save_button_single');

            $saveButtonSingle.removeClass('edit_button_single');
            channel_rebate.operation.countChecked();
            $saveButtonSingle.attr('onclick', 'channel_rebate.operation.singleUpdate(this);')
        },
        batchEdit: function () {
            $('.check-box-single:checked').parents('tr').find('.edit_button_single').click();
        },
        addRow: function () {
            let rowId = 'row_' + channel_rebate.param.rowId;
            let inputId = 'result_detail_' + channel_rebate.param.rowId;
            let hiddenInputId = inputId + '_value';
            let date = new Date();
            let formatDate = common.formatDate(date, 'yyyy-MM-dd');
            let eachRowHtml =
                `<tr id="${rowId}" role="row" class="odd" style="background-color: rgb(236, 236, 236);">
                    <td class="text-center checkbox-width"><input type="checkbox" value="1" class="data-checkbox check-box-single" checked = true></td>
                    <td class="checkedFlagtd text-center hidden">true</td>
                    <td class="idtd text-center"> </td>
                    <td class="clientTypetd text-center">
                        <select class="form-control clientTypeSel input_flag channel_select_param"><option value="">客户类型</option>${channel_rebate.param.clientTypeOptions}</select>
                    </td>
                    <td class="channelTypetd text-center">
                        <select class="form-control channelTypeSel input_flag channel_select_param"><option value="">渠道类型</option>${channel_rebate.param.channelTypeOptions}</select>
                    </td>
                    <td class="channelNametd text-center">
                        <select class="form-control ownChannelSel input_flag"><option value="">渠道名称</option>${channel_rebate.param.allChannelOptions}</select>
                        <select class="form-control partnerChannelSel input_flag hidden"><option value="">第三方渠道</option>${channel_rebate.param.partnerChannelOptions}</select>
                    </td>
                    <td class="areaNametd text-center"><input id=${inputId} type="text" class="result_detail form-control input_flag" placeholder="支持城市"></td>
                    <td class="insuranceCompanyNametd text-center">
                        <select class="insuranceCompanySel form-control input_flag"><option value="">保险公司</option>${channel_rebate.param.allInsuranceCompanyOptions}</select>
                    </td>
                    <td class="effectiveDatetd text-center"> ${formatDate}</td>
                    <td class="onlyCommercialRebatetd text-center"><input type="text" value="" class="onlyCommercialRebate form-control input_flag" placeholder="单商业险"></td>
                    <td class="onlyCompulsoryRebatetd text-center"><input type="text" value="" class="onlyCompulsoryRebate form-control input_flag" placeholder="单交强险"></td>
                    <td class="commercialRebatetd text-center"><input type="text" value="" class="commercialRebate form-control input_flag" placeholder="组合商业险"></td>
                    <td class="compulsoryRebatetd text-center"><input type="text" value="" class="compulsoryRebate form-control input_flag" placeholder="组合交强险"></td>
                    <td class="readyEffectiveDatetd text-center">
                        <input type="text" class="readyEffectiveDate form-control form-inline input_flag batch_readyEffectiveDate_single datePicker" placeholder="预生效时间" 
                            onfocus="WdatePicker({onpicked:function(dq){channel_rebate.operation.fillReadyRebate(this);},dateFmt:'yyyy-MM-dd',readOnly:true, minDate:'%y-%M-{%d+1}',startDate:'%y-%M-{%d+1}'});">
                    </td>
                    <td class="onlyReadyCommercialRebatetd text-center"><input type="text" class="onlyReadyCommercialRebate form-control input_flag" placeholder="预生效单商业险"></td>
                    <td class="onlyReadyCompulsoryRebatetd text-center"><input type="text" class="onlyReadyCompulsoryRebate form-control input_flag" placeholder="预生效单交强险"></td>
                    <td class="readyCommercialRebatetd text-center"><input type="text" class="readyCommercialRebate form-control input_flag" placeholder="预生效组合商业险"></td>
                    <td class="readyCompulsoryRebatetd text-center"><input type="text" class="readyCompulsoryRebate form-control input_flag" placeholder="预生效组合交强险"></td>
                    <td class="text-center">
                        <a style="margin-left: 10px;color: coral;" class="checkbox-width" disabled="disabled" onclick="channel_rebate.operation.singleSave(this);">保存</a>
                    </td>
                </tr>`;

            if ($('.check-box-single').length === 0) {
                $("#channel_rebate_list_table tbody:last").html(eachRowHtml);
            } else {
                $("#channel_rebate_list_table tbody:last").prepend(eachRowHtml);
            }

            $('.input_flag').on('mouseover', function () {
                if (this.placeholder) {
                    this.title = this.placeholder;
                }
            });
            let $hiddenChannelDiv = $("#hidden_channel_div");
            $hiddenChannelDiv.html($hiddenChannelDiv.html() + '<input type="hidden" id="' + hiddenInputId + '" name="triggerCity">');
            channel_rebate.param.rowId++;
            channel_rebate.operation.countChecked();

            $('.channel_select_param').on('change', function () {
                let formData = channel_rebate.param.NewChannelRebateForm();
                formData.clientType = $(this).parents('tr').find('.clientTypeSel').val();
                formData.channelType = $(this).parents('tr').find('.channelTypeSel').val();
                let channels = channel_rebate.interface.getChannels(formData);
                let channelOptions = '<option value="">渠道名称</option>';
                $.each(channels, function (i, channel) {
                    channelOptions += `<option value="${channel.id}">${channel.description}</option>`
                });
                $(this).parents('tr').find('.ownChannelSel').html(channelOptions);
            });

            $("#" + inputId + "").unbind("change").bind({
                keyup: function () {
                    if (common.isEmpty($(this).val())) {
                        CUI.select.hide();
                        $("#" + inputId + "").val("");
                        $("#" + hiddenInputId + "").val("");
                        return;
                    }
                    common.getByAjax(true, "get", "json", "/operationcenter/resource/areas/getByKeyWord",
                        {
                            keyword: $(this).val()
                        },
                        function (data) {
                            if (data == null) {
                                return;
                            }
                            let map = new Map();
                            $.each(data, function (i, model) {
                                map.put(model.id, model.name);
                            });
                            CUI.select.show($("#" + inputId), 300, map, false, $("#" + hiddenInputId));
                        }, function () {
                        }
                    );
                }
            });
        },
        editRow: function (thisA) {
            $(thisA).parents('tr').find('.value_span').addClass('hidden');
            $(thisA).parents('tr').find('.input_flag').removeClass('hidden');
            if (!$(thisA).parents('tr').find('.check-box-single').prop("checked")) {
                $(thisA).parents('tr').find('.check-box-single').prop("checked", true);
            }
            $(thisA).html('保存');
            $(thisA).removeClass('edit_button_single');
            $(thisA).addClass('save_button_single');
            channel_rebate.operation.countChecked();
            $(thisA).attr('onclick', 'channel_rebate.operation.singleUpdate(this);');
        },
        singleUpdate: function (thisA) {
            let $row = $(thisA).parents('tr');
            let formData = channel_rebate.operation.processUpdateEachRow($row);
            let formList = [];
            formList.push(formData);

            channel_rebate.interface.save(formList)
        },
        singleSave: function (thisA) {
            let $row = $(thisA).parents('tr');
            let formData = channel_rebate.operation.processAddEachRow($row);
            let formList = [];
            formList.push(formData);

            channel_rebate.interface.save(formList)
        }
        ,
        batchSave: function () {
            let formList = [];
            let $newRowTds = $("#channel_rebate_list_table tbody input:checkbox:checked");//所有新增的tr中的 check-box-flag=true 的td

            $.each($newRowTds, function (i, newRowTd) {
                let $row = $(newRowTd).parents("tr");
                let id = $row.find('.idtd').html().trim();
                let formData = !id ? channel_rebate.operation.processAddEachRow($row) : channel_rebate.operation.processUpdateEachRow($row);
                formList.push(formData);
            });

            channel_rebate.interface.save(formList);
        },
        processAddEachRow: function ($row) {
            let formData = channel_rebate.param.NewChannelRebateForm();

            formData.clientType = $row.find('.clientTypetd').find('select').val();
            formData.channelType = $row.find('.channelTypetd').find('select').val();
            formData.channelId = $row.find('.channelNametd').find('select').val();
            let inputId = $row.find('.areaNametd').find('input').attr('id');
            formData.areaId = $("#" + inputId + "_value").val();
            formData.insuranceCompanyId = $row.find('.insuranceCompanyNametd').find('select').val();
            formData.effectiveDate = $row.find('.effectiveDatetd').find('input').val();
            formData.onlyCommercialRebate = $row.find('.onlyCommercialRebatetd').find('input').val();
            formData.onlyCompulsoryRebate = $row.find('.onlyCompulsoryRebatetd').find('input').val();
            formData.commercialRebate = $row.find('.commercialRebatetd').find('input').val();
            formData.compulsoryRebate = $row.find('.compulsoryRebatetd').find('input').val();
            formData.onlyReadyCommercialRebate = $row.find('.onlyReadyCommercialRebatetd').find('input').val();
            formData.onlyReadyCompulsoryRebate = $row.find('.onlyReadyCompulsoryRebatetd').find('input').val();
            formData.readyCommercialRebate = $row.find('.readyCommercialRebatetd').find('input').val();
            formData.readyCompulsoryRebate = $row.find('.readyCompulsoryRebatetd').find('input').val();
            formData.readyEffectiveDate = $row.find('.readyEffectiveDatetd').find('input').val();

            let flag = channel_rebate_validation.addValidation(formData);
            if (!flag) {
                throw '数据校验未通过';
            }
            return formData;
        },
        processUpdateEachRow: function ($row) {
            let formData = channel_rebate.param.NewChannelRebateForm();

            formData.id = $row.find('.idtd').html().trim();
            formData.onlyCommercialRebate = $row.find('.onlyCommercialRebatetd').find('input').val();
            formData.onlyCompulsoryRebate = $row.find('.onlyCompulsoryRebatetd').find('input').val();
            formData.commercialRebate = $row.find('.commercialRebatetd').find('input').val();
            formData.compulsoryRebate = $row.find('.compulsoryRebatetd').find('input').val();
            formData.onlyReadyCommercialRebate = $row.find('.onlyReadyCommercialRebatetd').find('input').val();
            formData.onlyReadyCompulsoryRebate = $row.find('.onlyReadyCompulsoryRebatetd').find('input').val();
            formData.readyCommercialRebate = $row.find('.readyCommercialRebatetd').find('input').val();
            formData.readyCompulsoryRebate = $row.find('.readyCompulsoryRebatetd').find('input').val();
            formData.readyEffectiveDate = $row.find('.readyEffectiveDatetd').find('input').val();
            channel_rebate_validation.updateValidation(formData);

            return formData;
        },
        batchEditDate: function () {
            let date = $('.batch_readyEffectiveDate').val();
            $('.batch_readyEffectiveDate_single').prop('value', date)
        },
        toHistory: function (id) {
            channel_rebate.param.channelRebateId = id;
            $.get("/views/channelRebate/channel_rebate_history_pop.html", {}, function (content) {
                layer.open({
                    type: 1,
                    title: '历史费率',
                    skin: 'layui-layer-rim', //加上边框
                    area: ['75%', '75%'], //宽高
                    content: content,
                    scrollbar: false
                });
            });
        },
        toDescription: function (id) {
            channel_rebate.param.channelRebateId = id;
            $.get("/views/channelRebate/channel_rebate_description_pop.html", {}, function (content) {
                layer.open({
                    type: 1,
                    title: '核保政策',
                    skin: 'layui-layer-rim', //加上边框
                    area: ['40%', '50%'], //宽高
                    content: content,
                    scrollbar: false,
                    success: function (layero, index) {
                        $(':focus').blur();
                        $(layero).find("#close_button").on('click', function () {
                            layer.close(index);
                        });
                    }
                });

            });
        },
        toBatchAdd: function () {
            $.get("/views/channelRebate/channel_rebate_batch_add_pop.html", function (content, status, xhr) {
                layer.open({
                    type: 1,
                    title: '批量导入历史费率',
                    skin: 'layui-layer-rim', //加上边框
                    area: ['70%', '70%'], //宽高
                    content: content,
                    scrollbar: false,
                    success: function (layero, index) {
                        $(layero).find('.submit_button').data('layer-index', index);
                    }
                });

                channel_rebate_batch_add.init.initBatchAddProvince();
                channel_rebate_batch_add.init.initBatchAddChannels(2, 1);
                channel_rebate_batch_add.init.initBatchAddCompany();

                window.parent.$('.channel_select').bind({
                    change: function () {
                        channel_rebate_batch_add.init.initBatchAddChannels(window.parent.$('.client_type_radio:checked').val(), window.parent.$('.channel_type_radio:checked').val());
                    }
                });

                window.parent.$('.batch_add_province_sel').bind({
                    change: function () {
                        channel_rebate.interface.initAddress($('.batch_add_province_sel').val(), 2, function (data) {
                            let cityCheckbox = '';
                            $.each(data, function (i, city) {
                                cityCheckbox += '<label style="min-width: 85px;"><input type="checkbox" name="city" class="city_checkbox_single" value="' + city.id + '" checked>' + city.name + '</label>';
                            });
                            window.parent.$('.city_checkbox_div').html(cityCheckbox);
                        });
                    }
                });
            });
        }
    },
    interface: {
        getChannels: function (formData) {
            let channels;
            common.getByAjax(false, 'get', 'json', '/operationcenter/channelRebate/channels', formData, function (data) {
                channels = data;
            }, function () {
            });
            return channels;
        },
        save: function (formList) {
            $.ajax({
                async: 'false',
                type: 'post',
                dataType: 'json',
                contentType: "application/json",
                url: '/operationcenter/channelRebate/add',
                data: JSON.stringify(formList),
                success: function (data) {
                    if (data.pass) {
                        layer.alert("保存成功！", {time: 500});
                        channel_rebate.param.dataTable.ajax.reload();
                        dt_labels.selected = [];
                        $('.data-checkbox').prop('checked', false);
                    } else {
                        layer.alert(data.message, {time: 1000});
                    }
                },
                error: function () {
                    layer.alert("保存失败！", {time: 1000});
                }
            });
        },
        batchSave: function (formList) {
            $.ajax({
                async: 'false',
                type: 'post',
                dataType: 'json',
                contentType: "application/json",
                url: '/operationcenter/channelRebate/batchAdd',
                data: JSON.stringify(formList),
                success: function (data) {
                    if (data.pass) {
                        layer.alert("保存成功！", {time: 500});
                        layer.close($('.submit_button').data('layer-index'));
                        dt_labels.selected = [];
                        channel_rebate.param.dataTable.ajax.reload();
                        $('.data-checkbox').prop('checked', false);
                    } else {
                        layer.alert(data.message, {time: 1000});
                    }
                },
                error: function () {
                    layer.alert("保存失败！", {time: 1000});
                }
            });
        },
        initOwnChannels: function () {
            let channels;
            common.getByAjax(false, 'get', 'json', '/operationcenter/resource/ownChannels', {}, function (data) {
                channels = data;
            }, function () {
            });
            return channels;
        },
        initPartnerChannels: function () {
            let channels;
            common.getByAjax(false, 'get', 'json', '/operationcenter/resource/channel/thirdParty', {}, function (data) {
                channels = data;
            }, function () {
            });
            return channels;
        },
        initInsuranceCompany: function () {
            let insuranceCompanys;
            common.getByAjax(false, 'get', 'json', '/operationcenter/resource/insuranceCompanys', {}, function (data) {
                insuranceCompanys = data;
            }, function () {
            });
            return insuranceCompanys;
        },
        initArea: function () {
            let areas = null;
            common.getByAjax(false, "get", "json", "/operationcenter/resource/areas", {},
                function (data) {
                    areas = data;
                }, function () {
                }
            );
            return areas;
        },
        initStatus: function () {
            common.getByAjax(false, "get", "json", "/operationcenter/channelRebate/status", {}, function (data) {
                    for (let key in data) {
                        channel_rebate.param.statusOptions += `<option value=${key}>${data[key]}</option>`
                    }
                    $('#search_div').find('.statusSel').append(channel_rebate.param.statusOptions);
                }, function () {
                }
            );
        },
        getProvinces: function () {
            let resultList;
            common.getByAjax(false, "get", "json", "/operationcenter/channelRebate/provinces", {}, function (data) {
                    resultList = data;
                }, function () {
                }
            );
            return resultList;
        },
        initAddress: function (parent, level, callback) {
            if (level !== 1 && parent <= 0) {
                return;
            }
            let href = "";
            if (level === 1) {
                href = "/operationcenter/channelRebate/provinces";
            } else if (level === 2) {
                href = "/operationcenter/channelRebate/" + parent + "/cities";
            }
            common.getByAjax(true, "get", "json", href, {},
                function (data) {
                    callback(data);
                },
                function () {
                    layer.alert("系统异常！", {time: 1000});
                }
            );
        }
    }
};

var channel_rebate_batch_add = {
    init: {
        initBatchAddChannels: function (clientType, channelType) {
            let formData = channel_rebate.param.NewChannelRebateForm();
            formData.clientType = clientType;
            formData.channelType = channelType;
            let channels = channel_rebate.interface.getChannels(formData);
            let channelCheckbox = '';
            $.each(channels, function (i, channel) {
                channelCheckbox += '<label style="min-width: 160px;"><input type="checkbox" name="channel" class="channel_checkbox_single" value="' + channel.id + '">' + channel.description + '</label>';
            });
            if (channelCheckbox) {
                $('.channel_checkbox_div').html(channelCheckbox);
            } else {
                $('.channel_checkbox_div').html('无此类型渠道!');
            }
        },
        initBatchAddProvince: function () {
            window.parent.$('.batch_add_province_sel').html(channel_rebate.param.provinceOptions);
        },
        getCities: function () {
            channel_rebate.interface.initAddress(parent, 3, window.parent.$('.'), function (data) {
                let options = '请选择城市';
                if (data.length > 0) {
                    $.each(data, function (i, model) {
                        options += `<option value="${model.id}">${model.name}</option>`;
                    });
                    selector.html(options);
                    if (level === 2) {
                        let $searchDiv = $('#search_div .citySel');
                        $searchDiv.multiselect('destroy');
                        $searchDiv.multiselect({
                            nonSelectedText: '请选择城市',
                            buttonWidth: '160',
                            maxHeight: '400',
                            includeSelectAllOption: true,
                            selectAllNumber: true,
                            selectAllText: '全部',
                            allSelectedText: '全部',
                            nSelectedText: '个城市'
                        });
                        $searchDiv.multiselect('refresh');
                    } else {//省 直辖市
                        channel_rebate.param.provinceOptions = options;
                    }
                }
            })
        },
        initBatchAddCompany: function () {
            let insuranceCompanys = channel_rebate.interface.initInsuranceCompany();
            let insuranceCompanyCheckbox = '';
            $.each(insuranceCompanys, function (i, insuranceCompany) {
                insuranceCompanyCheckbox += '<label style="min-width: 100px;"><input type="checkbox" name="insurance_company" class="insurance_company_checkbox_single" value="' + insuranceCompany.id + '">' + insuranceCompany.name + '</label>';
            });
            window.parent.$('.insurance_company_div').html(insuranceCompanyCheckbox);
        }
    },
    operation: {
        batchAddSave: function (thisButton) {
            let formList = [];
            let formData = channel_rebate.param.NewChannelRebateForm();

            formData.channelId = window.parent.$('input[name=channel]:checked').map(function () {
                return this.value;
            }).get().join(',');
            formData.areaId = window.parent.$('.city_checkbox_single:checked').map(function () {
                return this.value;
            }).get().join(',');
            formData.insuranceCompanyId = window.parent.$('.insurance_company_checkbox_single:checked').map(function () {
                return this.value;
            }).get().join(',');

            let inputDateString = window.parent.$('.batch_add_readyEffectiveDate').val();
            if (!inputDateString) {
                layer.alert("请输入生效时间！", {time: 1000});
                return false;
            }
            let currDate = new Date();
            if (moment(inputDateString).isSame(currDate, 'day')) {//如果时间选中的是当天,即时生效
                formData.readyFlag = false;
                formData.onlyCommercialRebate = window.parent.$('.only_commercial_input').val();
                formData.onlyCompulsoryRebate = window.parent.$('.only_compulsory_input').val();
                formData.commercialRebate = window.parent.$('.commercial_input').val();
                formData.compulsoryRebate = window.parent.$('.compulsory_input').val();
                formData.effectiveDate = window.parent.$('.batch_add_readyEffectiveDate').val();
            } else {//预生效
                formData.readyFlag = true;
                formData.onlyReadyCommercialRebate = window.parent.$('.only_commercial_input').val();
                formData.onlyReadyCompulsoryRebate = window.parent.$('.only_compulsory_input').val();
                formData.readyCommercialRebate = window.parent.$('.commercial_input').val();
                formData.readyCompulsoryRebate = window.parent.$('.compulsory_input').val();
                formData.readyEffectiveDate = window.parent.$('.batch_add_readyEffectiveDate').val();
            }

            let checkFlag = channel_rebate_validation.addValidation(formData);
            if (!checkFlag) {
                return checkFlag;
            }
            formList.push(formData);
            channel_rebate.interface.batchSave(formList);
        }
    }

};


$(function () {
    dt_labels.checkedType = '.checkbox-width';
    channel_rebate.init.initChannels();
    channel_rebate.init.initInsuranceCompany();
    channel_rebate.init.initStatus();
    channel_rebate.init.initProvince();
    channel_rebate.initTemplateUrl();
    channel_rebate.param.dataTable = datatableUtil.getByDatatables(channel_rebate.dt_table.dt_list, function (data) {
        let $searchDiv = $("#search_div");
        let clientType = $searchDiv.find('.clientTypeSel').val();
        let channelType = $searchDiv.find('.channelTypeSel').val();
        let $ownChannelSel = $('#search_div .ownChannelSel');
        let channelId = $ownChannelSel.val() ? $ownChannelSel.val().join(',') : null;
        let $insuranceCompanySel = $('#search_div .insuranceCompanySel');
        let insuranceCompanyId = $insuranceCompanySel.val() ? $insuranceCompanySel.val().join(',') : null;
        let areaIds = [];
        $.each($('#search_div input[name="area"]'), function (i, input) {
            areaIds.push($(input).val());
        });

        let status = $('#search_div .statusSel').val();

        data.clientType = clientType;
        data.channelType = channelType;
        data.channelId = channelId;
        data.insuranceCompanyId = insuranceCompanyId;
        data.provinceId = $('#search_div .provinceSel').val();
        let $citySel = $('#search_div .citySel');
        data.areaIds = $citySel.val() ? $citySel.val().join(',') : '';
        data.status = status;
    }, function () {
    });

    $("#city_name_search").unbind("keyup").bind({
        keyup: function () {
            if (common.isEmpty($(this).val())) {
                CUI.select.hide();
                $("#city_name_search").val("");
                return;
            }
            common.getByAjax(true, "get", "json", "/operationcenter/resource/areas/getByKeyWord",
                {
                    keyword: $(this).val()
                },
                function (data) {
                    if (data == null) {
                        return;
                    }
                    let map = new Map();
                    $.each(data, function (i, model) {
                        map.put(model.id, model.name);
                    });
                    CUI.select.showTag($("#city_name_search"), 300, map, false, $("#trigger_city_search"));
                }
            );
        }
    });

    $('.param_search_button').click(function () {
        $(".check-box-all").prop("checked", false);
        dt_labels.selected = [];
        channel_rebate.param.dataTable.ajax.reload();
    });


    $('.channel_select_param_search').on('change', function () {
        let formData = channel_rebate.param.NewChannelRebateForm();
        formData.clientType = $('#search_div .clientTypeSel').val();
        formData.channelType = $('#search_div .channelTypeSel').val();
        let channels = channel_rebate.interface.getChannels(formData);
        let channelSelDisplay = '请选择渠道名称';
        if (formData.channelType === '1') {
            channelSelDisplay = '请选择自有渠道名称';
        } else if (formData.channelType === '2') {
            channelSelDisplay = '请选择第三方渠道名称';
        }
        let channelOptoins = '';
        $.each(channels, function (i, channel) {
            channelOptoins += `<option value="${channel.id}">${channel.description}</option>`
        });
        let $ownChannelSel = $('#search_div .ownChannelSel');
        $ownChannelSel.html(channelOptoins);
        $ownChannelSel.multiselect("destroy").multiselect({
            nonSelectedText: channelSelDisplay,
            buttonWidth: '225',
            maxHeight: '400'
        });
        $ownChannelSel.multiselect('refresh');
    });

    $('#search_div .provinceSel').bind({
        change: function () {
            let provinceId = $(this).val();
            let $citySel = $('#search_div .citySel');
            if (provinceId) {
                let index = $.inArray(provinceId, channel_rebate.param.directCitys);
                if (index >= 0) {
                    $('#search_div .city_sel_div').addClass('hidden');
                    $citySel.html('<option> </option>');
                    $citySel.multiselect('refresh');
                } else {//如果是省则联动
                    $('#search_div .city_sel_div').removeClass('hidden');
                    channel_rebate.interface.initAddress(provinceId, 2, function (data) {
                        let options = '请选择城市';
                        if (data.length > 0) {
                            $.each(data, function (i, model) {
                                options += `<option value="${model.id}">${model.name}</option>`;
                            });
                            $citySel.html(options);
                            $citySel.multiselect('destroy');
                            $citySel.multiselect({
                                nonSelectedText: '请选择城市',
                                buttonWidth: '160',
                                maxHeight: '400',
                                includeSelectAllOption: true,
                                selectAllNumber: false,
                                selectAllText: '全部',
                                allSelectedText: '全部',
                                nSelectedText: '个城市'
                            });
                            $citySel.multiselect('refresh');
                        }
                    });
                }
            } else {
                $('#search_div .city_sel_div').addClass('hidden');
                $citySel.html('<option> </option>');
                $citySel.multiselect('refresh');
            }
        }
    });


});
