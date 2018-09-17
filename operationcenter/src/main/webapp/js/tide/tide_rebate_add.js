(function ($, undefined) {
    // if (!common.permission.validUserHasPermission('')) {
    //     $('#saveBtn').remove()
    // }

    var contractId = common.getUrlParam('contractId')
    var supportAreaId = common.getUrlParam('supportAreaId')

    if (contractId && supportAreaId) {
        $.get('/operationcenter/tide/rebate', {
            contractId: contractId,
            supportAreaId: supportAreaId,
            currentPage: 1,
            pageSize: 100
        }, function (result) {
            var tables = $('#tide_rebate_list_table').dataTable().api(true)
            if (result.aaData && result.aaData.length) {
                result.aaData.forEach(function (it) {
                    delete it.id
                    it.contractRebateCode = $.RandCode()
                    tables.row.add(it)
                })
                tables.draw()
                $('#tide_rebate_list_table').find('.returnType').trigger('change')
            }
        })
    }

    // 注册取消事件
    $('#cancelBtn').click(function () {
        layer.confirm('确定取消保存吗？',{closeBtn: 0}, function () {
            $.close()
        })
    })

    // 注册草稿事件
    $('#saveBtn').click(function () {
        var that = this
        var data = getAllRebate()
        if (data === undefined) return

        layer.open({
            title: '保存草稿',
            type: 1,
            btn: ['确定', '取消'],
            content: '<div class="form-horizontal"><div class="col-md-12">' +
            '<div class="panel">' +
            '<div class="panel-body">' +
            '<div class="col-md-12"><div class="from-group"><label class="control-label">草稿名称：</label><input id="draftName" type="text" class="form-control"></div></div>' +
            '</table>' +
            '</div></div></div></div>',
            yes: function (index, panel) {
                var draftName = $(panel).find('#draftName').val()
                if (!draftName) {
                    layer.msg('请输入草稿名称')
                    return
                }
                var model = {
                    name: draftName,
                    contractRebateList: data
                }
                that.setAttribute('disabled', 'true')
                that.innerHTML = '保存中...'
                var l = layer.load(1)
                $.ajax({
                    url: '/operationcenter/tide/rebate/draft/add',
                    type: 'post',
                    contentType: 'application/json',
                    data: JSON.stringify(model),
                    success: function (result) {
                        if (result.pass) {
                            layer.success('草稿保存成功。', function () {
                                $.close()
                            }, '返回列表')
                        } else {
                            layer.error(result.message, null, '返回')
                        }
                    },
                    error: function () {
                        layer.error('保存出现错误', null, '返回')
                    }
                }).always(function () {
                    layer.close(l)
                    that.removeAttribute('disabled')
                    that.innerHTML = '保存草稿'
                })

                layer.close(index)
            }
        })
    })

    // 注册提交事件
    $('#submitBtn').click(function () {
        var that = this
        var data = getAllRebate()

        if (data === undefined) return

        that.setAttribute('disabled', 'true')
        that.innerHTML = '发布中...'

        var l = layer.load(1)
        $.ajax({
            url: '/operationcenter/tide/rebate/batchAdd',
            type: 'post',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function (result) {
                if (result.pass) {
                    layer.success('点位已发布成功，详情见点位管理列表。', function () {
                        if (window.opener) {
                            window.opener.jQuery('#tide_rebate_list_table').dataTable().api(true).ajax.reload()
                        }
                        $.close()
                    }, '返回列表')
                } else {
                    layer.error(result.message, null, '返回')
                }
            },
            error: function () {
                layer.error('保存出现错误', null, '返回')
            }
        }).always(function () {
            layer.close(l)
            that.removeAttribute('disabled')
            that.innerHTML = '发布'
        })
    })

// -------------------------------------------------- function ------------------------------------------------
    function getAllRebate() {
        if ($('form').valid()) {
            var commonData = {contractId: $('#contractId').val(), supportAreaId: $('#supportAreaId').val()}
            var data = []
            // 获取行数据
            $('#tide_rebate_list_table tbody tr[role="row"]').each(function () {
                var row = {}
                $(this).find('input,select').each(function () {
                    var attr = this.getAttribute('attr')
                    if (attr) {
                        row[attr] = this.value
                    }
                })
                data.push($.extend(row, commonData))
            })

            if (!data.length) {
                layer.msg('请输入点位信息')
                return
            }

            return data
        }
    }
}(window.jQuery))
