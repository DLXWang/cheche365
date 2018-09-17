(function ($, undefined) {
    var tables = $('#tide_rebate_list_table').dataTable().api(true)

    $.getJSON('/operationcenter/tide/rebate/draft', function (result) {
        var nodeList = []
        result.forEach(function (it, index) {
            nodeList.push($('<li><a data-id="' + it.id + '">' + (it.name + ' ' + it.createTime) + '<i class="glyphicon glyphicon-remove text-danger pull-right hide"></i></a></li>').data('node', it))
        })

        $('#draftNav').html(nodeList)
        setCount(nodeList.length)
    })

    $('#draftNav').on('click', 'i', function (e) {
        e.stopPropagation()
        var node = $(this).closest('li')
        var data = node.data('node')
        layer.confirm('确定删除此草稿吗?', {closeBtn: 0}, function (index) {
            layer.close(index)
            var l = layer.load(1)
            $.get('/operationcenter/tide/rebate/draft/remove', {id: data.id}, function () {
                if (node.hasClass('active')) $('form').addClass('hide')
                node.remove()
                setCount()
            }).always(function () {
                layer.close(l)
            })
        })
    })

    $('#draftNav').on('click', 'li', function () {
        $(this).addClass('active').siblings().removeClass('active')
        var data = $(this).data('node')
        $('#draftId').val(data.id)
        $('#draftName').val(data.name)
        $.get('/operationcenter/tide/rebate/draft/' + data.id, function (result) {
            var rebate = result.contractRebateList[0]
            $('#contractId').append(new Option(rebate.contractName, rebate.contractId, true, true))
            $('#supportAreaId').append(new Option(rebate.supportAreaName, rebate.supportAreaId, true, true))
            tables.clear()
            result.contractRebateList.forEach(function (it) {
                tables.row.add(it)
            })
            tables.draw()
            $('#tide_rebate_list_table').find('.returnType').trigger('change')
            $('form').removeClass('hide')
        })
    })

    $('#draftEdit').click(function () {
        var $this = $(this)
        $this.toggleClass('edit')
        $('#draftNav').find('i').toggleClass('hide')
        if ($this.hasClass('edit')) {
            $this.text('取消')
        } else {
            $this.text('编辑')
        }
    })

    // 注册草稿事件
    $('#saveBtn').click(function () {
        saveDraft.call(this, '/operationcenter/tide/rebate/draft/update', '保存')
    })

    // 注册提交事件
    $('#submitBtn').click(function () {
        saveDraft.call(this, '/operationcenter/tide/rebate/draft/public', '发布', function () {
            $('#draftNav li.active').remove()
            $('form').addClass('hide')
            setCount()
        })
    })

// -------------------------------------------------- function ------------------------------------------------

    function setCount(num) {
        var count
        if (typeof num === 'number') {
            count = num
        } else {
            count = $('#draftNav li').length
        }

        $('#draftCount').html('草稿箱  <span class="label label-default">' + count + '</span>')
    }

    function saveDraft(url, buttonText, callback) {
        var that = this
        var data = getAllRebate()
        if (data === undefined) return

        layer.confirm('确定' + buttonText + '草稿吗？', {closeBtn: 0}, function (index) {
            var model = {
                id: $('#draftId').val(),
                name: $('#draftName').val(),
                contractRebateList: data
            }
            that.setAttribute('disabled', 'true')
            that.innerHTML = buttonText + '中...'
            var l = layer.load(1)
            $.ajax({
                url: url,
                type: 'post',
                contentType: 'application/json',
                data: JSON.stringify(model),
                success: function (result) {
                    if (result.pass) {
                        layer.success('草稿' + buttonText + '成功。', callback, '返回列表')
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
                that.innerHTML = buttonText
            })

            layer.close(index)
        })
    }

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
