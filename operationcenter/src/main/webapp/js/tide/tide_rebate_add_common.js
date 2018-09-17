(function ($, undefined) {
    var template = {}

    // 设置select替换模板
    $.ajax({
        url: '/operationcenter/tide/resource/constants',
        async: false,
        success: function (result) {
            var key, nodeList = []
            for (key in result.INSURANCETYPE_MAP) {
                nodeList.push('<option value="' + key + '">' + result.INSURANCETYPE_MAP[key] + '</option>')
            }
            template.insuranceType = '<select class="form-control" attr="insuranceType" name="insuranceType">' + nodeList.join('') + '</select>'

            nodeList = []
            for (key in result.CARTYPE_MAP) {
                nodeList.push('<option value="' + key + '">' + result.CARTYPE_MAP[key] + '</option>')
            }
            template.carType = '<select class="form-control" attr="carType" name="carType">' + nodeList.join('') + '</select>'

            nodeList = []
            for (key in result.AUTOTAXRETURNTYPE_MAP) {
                nodeList.push('<option value="' + key + '">' + result.AUTOTAXRETURNTYPE_MAP[key] + '</option>')
            }
            template.autoTaxReturnType = '<select class="form-control returnType" style="width: 50%;" attr="autoTaxReturnType" name="autoTaxReturnType">' + nodeList.join('') + '</select>'
        }
    })

    $('#contractId').select2({
        language: 'zh-CN',
        theme: 'bootstrap',
        ajax: {
            delay: 300,
            url: '/operationcenter/tide/resource/contract',
            data: function (params) {
                return {
                    contractName: params.term,
                }
            },
            processResults: function (data) {
                data.forEach(it => it.text = it.name)
                return {
                    results: data
                }
            }
        }
    }).on('change', function () {
        $('#supportAreaId').val('').trigger('change')
    }).on('select2:select', function (e) {
        var data = e.params.data

        $('#contractEffectiveDate').val(data.effectiveDate)
        $('#contractExpireDate').val(data.expireDate)
    })

    $('#supportAreaId').select2({
        language: 'zh-CN',
        theme: 'bootstrap',
        ajax: {
            delay: 300,
            url: '/operationcenter/tide/resource/area',
            data: function (params) {
                return {
                    contractId: $('#contractId').val(),
                    areaName: params.term,
                }
            },
            processResults: function (data) {
                data.forEach(it => it.text = it.name)
                return {
                    results: data
                }
            }
        }
    })


    // table渲染参数
    var columnsOption = [
        {
            title: '点位编号', data: 'contractRebateCode', defaultContent: '', width: '70px',
            render: function (data, type, row, meta) {
                return '<span class="form-control-static">' + data + '</span><input type="hidden" attr="contractRebateCode" name="' + row.contractRebateCode + '_contractRebateCode" value="' + (data || '') + '">'
            }
        },
        {
            title: '投保类型', data: 'insuranceType', defaultContent: '', width: '90px',
            render: function (data, type, row, meta) {
                var str = template.insuranceType
                str = str.replace('value="' + (data || '') + '"', 'value="' + (data || '') + '" selected')
                return str
            }
        },
        {
            title: '使用性质', data: 'carType', defaultContent: '', width: '100px',
            render: function (data, type, row, meta) {
                var str = template.carType
                str = str.replace('value="' + (data || '') + '"', 'value="' + (data || '') + '" selected')
                return str
            }
        },
        {
            title: '条件', data: 'chooseCondition', defaultContent: '', width: '150px',
            render: function (data, type, row, meta) {
                return '<input class="form-control" type="text" attr="chooseCondition" name="' + row.contractRebateCode + '_chooseCondition" value="' + (data || '') + '">'
            }
        },
        {
            title: '商业险原始点位', data: 'originalCommecialRate', defaultContent: '', width: '70px',
            render: function (data, type, row, meta) {
                return '<input class="form-control required isFloat" type="number" attr="originalCommecialRate" min="0" step="0.0001" max="1" name="' + row.contractRebateCode + '_originalCommecialRate" value="' + (data || '') + '">'
            }
        },
        {
            title: '交强险原始点位', data: 'originalCompulsoryRate', defaultContent: '', width: '70px',
            render: function (data, type, row, meta) {
                return '<input class="form-control required isFloat" type="number" attr="originalCompulsoryRate" min="0" step="0.0001" max="1" name="' + row.contractRebateCode + '_originalCompulsoryRate" value="' + (data || '') + '">'
            }
        },
        {
            title: '车船税退税情况', data: 'autoTaxReturnValue', defaultContent: '', width: '160px',
            render: function (data, type, row, meta) {
                var str = template.autoTaxReturnType
                str = str.replace('value="' + (row.autoTaxReturnType || '') + '"', 'value="' + (row.autoTaxReturnType || '') + '" selected')
                return '<div class="input-group">' + str +
                    '<input class="form-control required number" style="width: 40%;" type="number" attr="autoTaxReturnValue" min="0" step="0.0001" max="1" name="' + row.contractRebateCode + '_autoTaxReturnValue" value="' + (data || '') + '">' +
                    '</div>'
            }
        },
        {
            title: '商业险市场投放点位', data: 'marketCommercialRate', defaultContent: '', width: '70px',
            render: function (data, type, row, meta) {
                return '<input class="form-control required isFloat" type="number" attr="marketCommercialRate" min="0" step="0.0001" max="1" name="' + row.contractRebateCode + '_marketCommercialRate" value="' + (data || '') + '">'
            }
        },
        {
            title: '交强险市场投放点位', data: 'marketCompulsoryRate', defaultContent: '', width: '70px',
            render: function (data, type, row, meta) {
                return '<input class="form-control required isFloat" type="number" attr="marketCompulsoryRate" min="0" step="0.0001" max="1" name="' + row.contractRebateCode + '_marketCompulsoryRate" value="' + (data || '') + '">'
            }
        },
        {
            title: '车船税市场退税情况', data: 'marketAutoTaxReturnValue', defaultContent: '', width: '160px',
            render: function (data, type, row, meta) {
                var str = template.autoTaxReturnType.replace('autoTaxReturnType', 'marketAutoTaxReturnType')
                str = str.replace('value="' + (row.marketAutoTaxReturnType || '') + '"', 'value="' + (row.marketAutoTaxReturnType || '') + '" selected')
                return '<div class="input-group">' + str +
                    '<input class="form-control required number" style="width: 40%;" type="number" attr="marketAutoTaxReturnValue" min="0" step="0.0001" max="1" name="' + row.contractRebateCode + '_marketAutoTaxReturnValue" value="' + (data || '') + '">' +
                    '<span class="unit"></span></div>'
            }
        },
        {
            title: '生效日期', data: 'effectiveDate', defaultContent: '', width: '100px',
            render: function (data, type, row, meta) {
                return '<input class="form-control required date geToday ltDay" lt-target="#' + row.contractRebateCode + '_expireDate" type="text" attr="effectiveDate" id="' + row.contractRebateCode + '_effectiveDate" name="' + row.contractRebateCode + '_effectiveDate" style="padding-right: 17px;background:#fff url(../../libs/My97DatePicker/skin/datePicker.gif) no-repeat right;" onclick="WdatePicker({minDate:\'%y-%M-%d\',maxDate:\'#F{$dp.$D(\\\'' + row.contractRebateCode + '_expireDate\\\')}\'})" value="' + (data || '') + '">'
            }
        },
        {
            title: '失效日期', data: 'expireDate', defaultContent: '', width: '100px',
            render: function (data, type, row, meta) {
                return '<input class="form-control required date gtDay" gt-target="#' + row.contractRebateCode + '_effectiveDate" type="text" attr="expireDate" id="' + row.contractRebateCode + '_expireDate" name="' + row.contractRebateCode + '_expireDate" style="padding-right: 17px;background:#fff url(../../libs/My97DatePicker/skin/datePicker.gif) no-repeat right;" onclick="WdatePicker({minDate:\'#F{$dp.$D(\\\'' + row.contractRebateCode + '_effectiveDate\\\')||\\\'%y-%M-%d\\\'}\'})" value="' + (data || '') + '">'
            }
        },
        {
            title: '', defaultContent: '', render: function (data, type, row, meta) {
                return '<input type="hidden" attr="id" value="' + (row.id || '') + '"><span class="glyphicon glyphicon-remove text-danger rowRemove" style="top:7px;cursor:pointer;" rowid="' + (row.id || '') + '"></span>'
            }
        }
    ]

    // 初始化table
    var tables = datatableUtil.getByDatatables(
        {
            order: false,
            paging: false,
            serverSide: false,
            table_id: 'tide_rebate_list_table',
            columns: columnsOption
        }
    )

    // 注册行删除事件
    $('#tide_rebate_list_table').on('click', '.rowRemove', function () {
        var that = this
        layer.confirm('确定删除该点位吗？', {closeBtn: 0}, function (index) {
            tables.row($(that).closest('tr'))
                .remove()
                .draw()
            layer.close(index)
        })
    })

    // 注册类型转换注册
    $('#tide_rebate_list_table').on('change', '.returnType', function () {
        var $this = $(this)
        if (this.value == 1) {
            $this.next().attr({
                min: '0',
                step: '0.0001',
                max: '1'
            }).addClass('isFloat')
                .removeClass('number')
                .parent()
                .removeClass('yuan')
        } else {
            $this.next().attr({
                min: '0',
                step: '1',
                max: null
            }).addClass('number')
                .removeClass('isFloat')
                .parent()
                .addClass('yuan')
        }
    })

    // 注册行添加事件
    $('#addBtn').click(function () {
        tables.row.add({contractRebateCode: $.RandCode()})
            .draw()
    })
}(window.jQuery))
