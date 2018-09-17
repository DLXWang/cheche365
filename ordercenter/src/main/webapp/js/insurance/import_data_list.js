var dataFunction = {
    "data": function (data) {
        data.area = $("#area").val();
        data.dataTypeId = $("#dataTypeId").val();
        data.importDateStart = $("#importDateStart").val();
        data.importDateEnd = $("#importDateEnd").val();
    },
    "fnRowCallback": function (nRow, aData) {
        var $operate = '';
        if(aData.dataTypeId == 2){
            $operate =  '<a style="margin-left: 10px;color: coral;" class="checkbox-width edit_button_single edit" onclick="dataList.batch.editRow(this);">编辑</a>' +
                '<a style="margin-left: 10px;color: coral;" class="checkbox-width cancel none" onclick="dataList.batch.cancel(this);">取消</a>';
        }else{
            $operate =  "";
        }
        $('td:eq(6)', nRow).html('<span class="value_span">'+aData.balanceTime+'</span>' +
            '<input type="text" class="balanceDate text-input-50 form-inline form-control input_flag hidden " historyId="'+ aData.historyId +'" value="" placeholder="天道到账时间"' +
            'style="width: 160px;background:#fff url(../../My97DatePicker/skin/datePicker.gif) no-repeat right;" ' +  'onfocus="WdatePicker({dateFmt:\'yyyy-MM-dd\'});">');
        $('td:eq(9)', nRow).html($operate);
    }
};
var dataList = {
    "url": '/orderCenter/offlineOrderImportHistory',
    "type": "GET",
    "table_id": "list_tab",
    "columns": [
        {"data": null, "title": "序号", 'sClass': "text-center", "orderable": false, "sWidth": "140px"},
        {"data": "importDateStart", "title": "导入时间", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},
        {"data": "historyId", "title": "导入批次", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": "dataType", "title": "数据类型", 'sClass': "text-center", "orderable": false, "sWidth": "70px"},
        {"data": "area", "title": "省市", 'sClass': "text-center", "orderable": false, "sWidth": "70px"},
        {
            "data": 'orderNum',
            "title": "保单数（单）",
            'sClass': "text-center",
            "orderable": false,
            "sWidth": "70px"
        },
        {"data": null,"title": "天道到账时间",'sClass': "text-center readyCommercialRebatetd","orderable": false},
        {"data": "description", "title": "备注的信息", 'sClass': "text-center", "orderable": false, "sWidth": "70px"},
        {"data": "comment", "title": "处理状态", 'sClass': "text-center", "orderable": false, "sWidth": "70px"},
        {
            "data": null,
            "title": "操作",
            'sClass': "text-center",
            "orderable": false,
            "sWidth": "150px",
            render: function (data, type, row) {
                if (type === 'display') {
                    return '<a style="margin-left: 10px;color: coral;" class="checkbox-width edit_button_single edit" onclick="dataList.batch.editRow(this);">编辑</a>' +
                        '<a style="margin-left: 10px;color: coral;" class="checkbox-width cancel none" onclick="dataList.batch.cancel(this);">取消</a>';
                }
                return "";
            },
        },
    ],
    batch:{
        formData:{
            id:'',
            balanceTime:''
        },
        singleUpdate: function (thisA) {
            var $row = $(thisA).parents('tr');
            var formData = dataList.batch.formatEach($row);
            if(formData){
                dataList.batch.save(formData,thisA)
            }else{
                popup.mould.popTipsMould(false,"请填全信息！", popup.mould.first, popup.mould.error, "", "53%", null);
            }
        },
        editRow: function (thisA) {
            $(thisA).parents('tr').find('.cancel').show();
            $(thisA).parents('tr').find('.value_span').addClass('hidden');
            $(thisA).parents('tr').find('.input_flag').removeClass('hidden');
            $(thisA).html('保存');
            $(thisA).removeClass('edit_button_single');
            $(thisA).addClass('save_button_single');
            $(thisA).attr('onclick', 'dataList.batch.singleUpdate(this);');
        },
        cancel: function (thisA) {
            $(thisA).parents('tr').find('.cancel').hide();
            $(thisA).parents('tr').find('.input_flag').addClass('hidden');
            $(thisA).parents('tr').find('.value_span').removeClass('hidden');
            $(thisA).parents('tr').find('.edit').html('编辑');
            $(thisA).parents('tr').find('.edit').removeClass('save_button_single');
            $(thisA).parents('tr').find('.edit').addClass('edit_button_single');
            $(thisA).parents('tr').find('.edit').attr('onclick', 'dataList.batch.editRow(this);');
        },
        save: function (formData,thisA) {
            $.ajax({
                async: 'false',
                type: 'post',
                dataType: 'json',
                contentType: "application/json",
                url: '/orderCenter/offlineOrderImportHistory/add',
                data: JSON.stringify(formData),
                success: function (data) {
                    if (data.pass) {
                        $(thisA).parents('tr').find('.value_span').html($(thisA).parents('tr').find('.balanceDate').val());
                        dataList.batch.cancel(thisA);
                        popup.mould.popTipsMould(false,"保存成功！", popup.mould.first, popup.mould.success, "", "53%", null);
                    } else {
                        popup.mould.popTipsMould(false,data.message, popup.mould.first, popup.mould.error, "", "53%", null);
                    }
                },
                error: function () {
                    popup.mould.popTipsMould(false,"保存失败！", popup.mould.first, popup.mould.error, "", "53%", null);
                }
            });
        },

        formatEach: function ($row) {
            var formData = dataList.batch.setForm();
            formData.balanceTime = $row.find('.balanceDate').val();{
            formData.historyId = $row.find('.balanceDate').attr('historyId');
            if(common.isEmpty(formData.balanceTime)){
                return false;
            }else
                return formData;
            }
        },
        setForm: function () {
            var temp = {};
            $.extend(temp, dataList.batch.channelRebateForm);
            return temp;
        }
    }
};
var datatables;
$(function () {
    dt_labels.hasDrawCallback = true;
    datatables = datatableUtil.getByDatatables(dataList, dataFunction.data, dataFunction.fnRowCallback);
    $("#dataTypeId, #area").unbind("change").bind({
        change: function () {
            datatables.ajax.reload();
        }
    });
    $("#search").unbind("click").bind({
        click: function () {
            datatables.ajax.reload();
        }
    });

    $("#area_input").bind({
        keyup: function () {
            if (common.isEmpty($(this).val())) {
                CUI.select.hide();
                $("#area_input").val("");
                $("#area").val("");
                return;
            }
            common.getByAjax(true, "get", "json", "/orderCenter/resource/areas/"+ $(this).val(),{},
                function (data) {
                    if (data == null) {
                        return;
                    }
                    var map = new Map();
                    $.each(data, function (i, model) {
                        map.put(model.id, model.name);
                    });
                    CUI.select.show($("#area_input"), 300, map, false, $("#area"));
                }, function () {
                }
            );
        }
    });
});


