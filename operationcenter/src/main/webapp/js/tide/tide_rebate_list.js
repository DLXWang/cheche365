(function ($, undefined) {

    var columnsOption = [
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
        {title: '失效日期', data: 'expireDate', defaultContent: '', visible: false},
        {title: '状态', data: 'statusStr', defaultContent: ''},
        {
            title: '是否启用', data: 'disable', defaultContent: '',
            render: function (disable, type, row) {
                let checkedFlag = disable ? '' : 'checked';
                return '<input class="rebateDisable" value="' + row.id + '" type="checkbox" ' + checkedFlag + '/>';
            }
        },
        {
            title: '操作', orderable: false,
            render: function (data, type, row, meta) {
                return '<div style="width: 100%;text-align: center;"><a class="showOplog" rowid="' + row.id + '">查看操作日志</a>　<a class="showHistory" rowid="' + row.id + '">查看历史点位</a>　<a class="edit" rowid="' + row.id + '">修改</a></div>'
            }
        }
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
            table_id: 'tide_rebate_list_table',
            url: '/operationcenter/tide/rebate',
            type: 'get',
            columns: columnsOption
        }, function (param) {
            $.extend(param, {
                contractId: $('#search_contract').val(),
                supportAreaId: $('#search_support_area').val(),
                status: $('#search_rebate_status').val(),
                insuranceType: $('#search_insurance_type').val(),
                carType: $('#search_car_type').val()
            })
        }
    )

    tables.on("draw", function () {
        $('#tide_rebate_list_table .rebateDisable').bootstrapSwitch({
            onText: '<i class="glyphicon glyphicon-ok">',
            offText: '<i class="glyphicon glyphicon-remove" style="color:#666;">',
            size: 'mini',
            onSwitchChange: function (even, status) {
                var that = this
                var data = tables.row($(this).closest('tr')).data()
                if (status && data.contractDisable) {
                    layer.msg('合约未启用不能启用相应点位')
                    $(that).bootstrapSwitch('state', !status, true)
                    return
                }

                var mess = '确定禁用该点位吗?'
                if (status) mess = '确定启用该点位吗?'
                layer.confirm(mess, {closeBtn: 0},function (index) {
                    $.get('/operationcenter/tide/rebate/disable', {
                        id: that.value,
                        disable: !status
                    })
                    layer.close(index)
                }, function () {
                    $(that).bootstrapSwitch('state', !status, true)
                })
            }
        })
    })

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


    $('#tide_rebate_list_table').on('click', '.edit', function () {
        var that = this
        var returntypeOption = []
        var constants = window.tideConstants
        for (var key in constants.AUTOTAXRETURNTYPE_MAP) {
            returntypeOption.push('<option value="' + key + '">' + constants.AUTOTAXRETURNTYPE_MAP[key] + '</option>')
        }
        $.get('tide/tide_rebate_edit.html', function (result) {
            layer.open({
                title: '修改',
                type: 1,
                btn: ['确定', '取消'],
                btnAlign: 'c',
                area: ['70%', '80%'],
                content: result,
                success: function (panel, index) {
                    var data = tables.row($(that).closest('tr')).data()
                    $(panel).find('.autotaxreturntype').append(returntypeOption.join(''))
                    $(panel).find('[id]').each(function () {
                        this.value = data[this.id]
                    })
                    $(panel).find('.autotaxreturntype').change(function () {
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
                    }).trigger('change')
                },
                yes: function (index, panel) {
                    var data = tables.row($(that).closest('tr')).data()
                    if ($(panel).find('#rebateEditForm').valid()) {
                        layer.close(index)
                        var resultData = {}
                        $(panel).find('input,select').each(function () {
                            resultData[this.id] = this.value
                        })

                        var flag = false
                        for (var key in resultData) {
                            if (resultData[key] != data[key]) {
                                flag = true
                                break
                            }
                        }
                        if (!flag) {
                            return
                        }

                        var l = layer.load(1)
                        $.ajax({
                            url: '/operationcenter/tide/rebate/update',
                            type: 'post',
                            contentType: 'application/json',
                            data: JSON.stringify(resultData),
                            success: function (result) {
                                if (result.pass) {
                                    layer.success('点位修改成功')
                                    tables.ajax.reload()
                                } else {
                                    layer.error(result.message, null, '返回')
                                }
                            },
                            error: function () {
                                layer.error('保存出现错误', null, '返回')
                            }
                        }).always(function () {
                            layer.close(l)
                        })
                    }
                }
            })
        })
    })

    var columnsOption = [
        {title: '操作时间', data: 'createTime', defaultContent: ''},
        {title: '操作员', data: 'name', defaultContent: ''},
        {title: '操作内容', data: 'mess', defaultContent: ''},
    ]

    $('#tide_rebate_list_table').on('click', '.showOplog', function () {
        var rowid = this.getAttribute('rowid')
        layer.open({
            title: "操作日志",
            type: 1,
            area: ['70%', '80%'],
            content: '<div class="container-fluid"><div class="col-md-12">' +
            '<div class="panel">' +
            '<div class="panel-body">' +
            '<table id="tide_rebate_log_table" class="table">' +
            '</table>' +
            '</div></div></div></div>',
            success: function (panel, index) {
                datatableUtil.getByDatatables(
                    {
                        order: false,
                        table_id: 'tide_rebate_log_table',
                        url: '/operationcenter/tide/log/tide_contract_rebate',
                        type: 'get',
                        columns: columnsOption
                    }, function (param) {
                        $.extend(param, {
                            keyword: rowid
                        })
                    }
                )
            }
        })
    })

    var columnsHisOption = [
        {title: '点位编号', data: 'contractRebateCode', defaultContent: ''},
        {title: '生效时间', data: 'effectiveDate', defaultContent: ''},
        {title: '失效时间', data: 'expireDate', defaultContent: ''},
        {title: '商业险原始点位', data: 'originalCommecialRate', defaultContent: ''},
        {title: '交强险原始点位', data: 'originalCompulsoryRate', defaultContent: ''},
        {
            title: '车船税退税情况', data: 'autoTaxReturnValue', defaultContent: '',
            render: function (data, type, row) {
                return (row.autoTaxReturnTypeStr || '') + '  ' + data
            }
        },
        {title: '商业险市场投放点位', data: 'marketCommercialRate', defaultContent: ''},
        {title: '交强险市场投放点位', data: 'marketCompulsoryRate', defaultContent: ''},
        {
            title: '车船税市场退税情况', data: 'marketAutoTaxReturnValue', defaultContent: '',
            render: function (data, type, row) {
                return (row.marketAutoTaxReturnTypeStr || '') + '  ' + data
            }
        },
        {title: '变更时间', data: 'createTime', defaultContent: ''},
        {title: '状态', data: 'status', defaultContent: ''}
    ]

    $('#tide_rebate_list_table').on('click', '.showHistory', function () {
        var rowid = this.getAttribute('rowid')
        layer.open({
            title: "点位历史",
            type: 1,
            area: ['70%', '80%'],
            content: '<div style="container-fluid"><div class="col-md-12">' +
            '<div class="panel">' +
            '<div class="panel-body">' +
            '<table id="tide_rebate_history_table" class="table">' +
            '</table>' +
            '</div></div></div></div>',
            success: function (panel, index) {
                datatableUtil.getByDatatables(
                    {
                        table_id: 'tide_rebate_history_table',
                        url: '/operationcenter/tide/rebate/history',
                        type: 'get',
                        columns: columnsHisOption,
                        order: false
                    }, function (param) {
                        $.extend(param, {
                            keyword: rowid
                        })
                    }
                )
            }
        })
    })

    $('#submitBtn').click(function () {
        tables.ajax.reload()
    })

    $('#clearBtn').click(function () {
        $('#search-collapse').find('input,select').val('')
        $('#search_contract').trigger('change')
    })
    $('#rebateCopy').click(function () {
        $.get('tide/tide_rebate_copy.html', function (result) {
            layer.open({
                title: "复制合约点位",
                type: 1,
                btn: ['确定', '取消'],
                btnAlign: 'c',
                area: ['500px'],
                content: result,
                zIndex: 100,
                yes: function (index, panel) {
                    if ($(panel).find('form').valid()) {
                        layer.close(index)
                        window.open('tide/tide_rebate_add.html?contractId=' + $(panel).find('#contractCopy').val() + '&supportAreaId=' + $(panel).find('#supportAreaCopy').val())

                    }
                }
            })
        })
    })

    $('#noChangeBtn').click(function () {
        common.getByAjax(false, 'post', 'json', '/operationcenter/tide/rebate/updateChangeStatus', {},
            function () {
                layer.msg("更新成功!");
                tables.ajax.reload();
            }, function () {
                layer.alert("更新失败!", {icon: 2});
            });
    });
}(window.jQuery));
