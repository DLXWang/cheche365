(function ($, undefined) {

    var columnsOption = [
        {title: '日期', data: 'createTime', defaultContent: ''},
        {title: '合约名称', data: 'contractName', defaultContent: '', visible: false},
        {title: '点位编号', data: 'contractRebateCode', defaultContent: '', orderable: false},
        {title: '支持投保所在城市', data: 'supportAreaName', defaultContent: ''},
        {title: '保险公司', data: 'insuranceCompanyName', defaultContent: ''},
        {title: '投保类型', data: 'insuranceTypeStr', defaultContent: '', visible: false},
        {title: '使用性质', data: 'carTypeStr', defaultContent: '', visible: false},
        {title: '条件', data: 'chooseCondition', defaultContent: '', visible: false, orderable: false},
        {title: '商业险原始点位', data: 'originalCommecialRate', defaultContent: ''},
        {title: '交强险原始点位', data: 'originalCompulsoryRate', defaultContent: ''},
        {
            title: '车船税退税情况', data: 'autoTaxReturnValue', defaultContent: '',
            render: function (data, type, row) {
                return (row.autoTaxReturnTypeStr || '') + '  ' + data + (row.autoTaxReturnType == 2 ? '元' : '')
            }
        },
        {title: '商业险市场投放点位', data: 'marketCommercialRate', defaultContent: '', visible: false},
        {title: '交强险市场投放点位', data: 'marketCompulsoryRate', defaultContent: '', visible: false},
        {
            title: '车船税市场退税情况', data: 'marketAutoTaxReturnValue', defaultContent: '', visible: false,
            render: function (data, type, row) {
                return (row.marketAutoTaxReturnTypeStr || '') + '  ' + data + (row.marketAutoTaxReturnType == 2 ? '元' : '')
            }
        },
        {title: '生效日期', data: 'effectiveDate', defaultContent: '', visible: false},
        {title: '失效日期', data: 'expireDate', defaultContent: '', visible: false}
    ]
    var nodeList = []
    columnsOption.forEach((it, index) => {
        let selected = '';
        if (it.visible !== false) {
            selected = 'selected'
        }
        nodeList.push('<option value="' + index + '" ' + selected + '>' + it.title + '</option>')
    })

    var tables = datatableUtil.getByDatatables(
        {
            order: false,
            table_id: 'tide_rebate_history_table',
            url: '/operationcenter/tide/rebate/record',
            type: 'get',
            columns: columnsOption
        }, function (param) {
            $.extend(param, {
                contractId: $('#search_contract').val(),
                supportAreaId: $('#search_support_area').val(),
                status: $('#search_rebate_status').val(),
                insuranceType: $('#search_insurance_type').val(),
                carType: $('#search_car_type').val(),
                createTime: $('#search_date').val()
            })
        }
    );

    new $.fn.dataTable.Buttons(tables, {
        buttons: [
            {
                'extend': 'excel',
                'text': 'Excel',//定义导出excel按钮的文字
                'filename': "点位信息-" + new Date().toLocaleDateString(), //导出的excel标题
                'title': "点位信息"
            },
            {
                'extend': 'csv',
                'text': 'CSV',//定义导出excel按钮的文字
                'filename': "点位信息-" + new Date().toLocaleDateString(), //导出的excel标题
                'title': "点位信息"
            }
        ]
    });

    $('.btn-export').append(tables.buttons().container());

    $('.exportCSV').click(function () {
        $('.btn-export .buttons-csv').click()
    });

    $('.exportExcel').click(function () {
        $('.btn-export .buttons-excel').click()
    });

    // 设置列选择
    $('#columnCofnig').append(nodeList.join('')).multiselect({
        includeSelectAllOption: true,
        selectAllText: '全选',
        allSelectedText: '已全选',
        nonSelectedText: '未选择',
        nSelectedText: '已选择',
        numberDisplayed: 1,
        onChange: function (option, checked, select) {
            if (option) {
                tables.columns(option[0].value).visible(checked)
            } else {
                tables.columns().visible(checked)
            }
        }
    })

    $('#submitBtn').click(function () {
        tables.ajax.reload()
    })

    $('#clearBtn').click(function () {
        $('#search-collapse input,select').val('')
        $('#search_contract').trigger('change')
    })
}(window.jQuery))
