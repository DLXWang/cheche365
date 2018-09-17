var dataFunction = {
    "data": function (data) {
    },
    "fnRowCallback": function (nRow, aData) {
    },
};
var orderList = {
    "url": '/orderCenter/telMarketingCenter/telMarketer',
    "type": "GET",
    "table_id": "list_tab",
    "columns": [
        {"data": "id", "title": "id", 'sClass': "text-center idtd", "orderable": false, "sWidth": "140px"},
        {"data": "name", "title": "用户名", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},
        {"data": "bindTel", "title": "绑定座机号",
            render: function (data, type, row) {
                return '<span class="value_span">' + data + '</span><input type="text" class="editPart form-control input_flag hidden" value="' + data + '" placeholder="请输入绑定座机号">';
            },
            'sClass': "text-center bindTeltd", "orderable": false, "sWidth": "120px"},
        {"data": "cno", "title": "工号",
            render: function (data, type, row) {
                return '<span class="value_span">' + data + '</span><input type="text" class="editPart form-control input_flag hidden" value="' + data + '" placeholder="请输入工号">';
            },
            'sClass': "text-center cnotd", "orderable": false, "sWidth": "70px"},
        {"data": "id", "title": "操作 ",
            render: function (data, type, row) {
                return '<a style="margin-left: 10px;color: coral;" class="checkbox-width edit_button_single" onclick="telMarketer.operation.editRow(this);">编辑</a>';
            },
            'sClass': "text-center", "orderable": false, "sWidth": "70px"},
    ],
};
var telMarketer = {
    param:{
        datatable:null,
        channelRebateForm: {
            id: '',
            bindTel: '',
            cno: '',
        },
    },
    operation: {
        editRow: function (thisA) {
            $(thisA).parents('tr').find('.value_span').addClass('hidden');
            $(thisA).parents('tr').find('.input_flag').removeClass('hidden');
            // if (!$(thisA).parents('tr').find('.check-box-single').prop("checked")) {
            //     $(thisA).parents('tr').find('.check-box-single').prop("checked", true);
            // }
            $(thisA).html('保存');
            $(thisA).removeClass('edit_button_single');
            $(thisA).addClass('save_button_single');
            telMarketer.operation.countChecked();
            $(thisA).attr('onclick', 'telMarketer.operation.singleUpdate(this);');
        },
        countChecked: function () {
            var length = $('.check-box-single:checked').length;
            if (length > 1) {
                $('.check-box-single:checked').parents('tr').find('a').addClass('useless');
                $('.batch_readyEffectiveDate').prop('disabled', false);
            }
        },
        processUpdateEachRow: function ($row) {
            var formData = telMarketer.operation.NewChannelRebateForm();
            formData.id = $row.find('.idtd').html();
            formData.bindTel = $row.find('.bindTeltd').find('input').val();
            formData.cno = $row.find('.cnotd').find('input').val();
            if(common.isEmpty(formData.bindTel) || common.isEmpty(formData.cno)){
                return null;
            }else{
                return formData;
            }
        },
        singleUpdate: function (thisA) {
            var $row = $(thisA).parents('tr');
            var formData = telMarketer.operation.processUpdateEachRow($row);
            if(common.isEmpty(formData)){
                popup.mould.popTipsMould(false,'请填写数据', popup.mould.first, popup.mould.error, "", "53%", null);
            }else{
                var formList = [];
                formList.push(formData);
                telMarketer.operation.save(formList);
            }
        },
        save: function (formList) {
            $.ajax({
                async: 'false',
                type: 'post',
                dataType: 'json',
                contentType: "application/json",
                url: '/orderCenter/telMarketingCenter/telMarketer/edit',
                data: JSON.stringify(formList),
                success: function (data) {
                    if (data.pass) {
                        popup.mould.popTipsMould(false,"保存成功！", popup.mould.first, popup.mould.success, "", "53%", null);
                        telMarketer.param.datatables.ajax.reload();
                        //dt_labels.selected = [];
                        //$('.data-checkbox').prop('checked', false);
                    } else {
                        popup.mould.popTipsMould(false,data.message, popup.mould.first, popup.mould.error, "", "53%", null);
                    }
                },
                error: function () {
                    popup.mould.popTipsMould(false,"保存失败！", popup.mould.first, popup.mould.error, "", "53%", null);
                }
            });
        },

        NewChannelRebateForm: function () {
            var temp = {};
            $.extend(temp, telMarketer.param.channelRebateForm);
            return temp;
        },
    },
}

$(function () {
    telMarketer.param.datatables = datatableUtil.getByDatatables(orderList, dataFunction.data, dataFunction.fnRowCallback);
});


