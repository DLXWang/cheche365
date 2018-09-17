var list = {
    "url": '/operationcenter/partners/log',
    "type": "GET",
    "table_id": "log_list",
    "columns": [
        {"data": null, "title": "操作时间", 'sClass': "text-center", "orderable": false, "sWidth": "120px"},
        {"data": null, "title": "操作员", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},
        {"data": null, "title": "操作内容", 'sClass': "text-center", "orderable": false, "sWidth": "350px"},
        {"data": null, "title": "执行结果", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
    ]
};

var dataFunction = {
    "data": function (data) {
        data.operator = datatableUtil.params.keyWord;
        data.partnerId = common.getUrlParam("id");

    },
    "fnRowCallback": function (nRow, aData) {
        $operationTime = common.checkToEmpty(aData.operationTime);
        $operator = common.checkToEmpty(aData.operator);
        $operationContent = common.checkToEmpty(aData.operationContent);
        $status = common.checkToEmpty(aData.status);
        $('td:eq(0)', nRow).html($operationTime);
        $('td:eq(1)', nRow).html($operator);
        $('td:eq(2)', nRow).html($operationContent);
        $('td:eq(3)', nRow).html($status);
    },
    "fnDrawCallback": function (datatable) {

    }
};

dt_labels.aLengthMenu = [[100], [100]];


$(function () {
    $('#changetips').click(function(){
        $("#ok").show();
        $("#cancel").show();
        $("#changetips").hide();
        var td=$('#tips'); //为后面文本框变成文本铺垫
        var text=$('#tips').text();
        var newtips=$('<input id ="newtips" type="text" class="edit" maxlength="200" value="'+text+'">');
        $('#tips').html(newtips);

        $("#newtips").click(function(){
            return false;
        }); //阻止表单默认点击行为

        $('#newtips').select(); //点击自动全选中表单的内容

        $('#newtips').blur(function(){
            var nextxt=$(this).val();
            td.html(nextxt);
        }); //表单失去焦点文本框变成文本

    });

    $('#ok').mousedown(function(){
        var element=document.getElementById("newtips");
        if (typeof(element)== "undefined" || element == null){
            $.ajax({
                type: "POST",
                url: "/operationcenter/partners/comment",
                data: {comment:document.getElementById("tips").innerHTML,partner:common.getUrlParam("id")},
                dataType: "json",
                success: function(data){
                    popup.mould.popTipsMould(false, "备注修改成功！", popup.mould.second, popup.mould.success, "", "57%", null);
                },
                error: function () {
                    popup.mould.popTipsMould(false, "备注修改失败！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            });
        }else{
            $.ajax({
                type: "POST",
                url: "/operationcenter/partners/comment",
                data: {comment:$("#newtips").val(),partner:common.getUrlParam("id")},
                dataType: "json",
                success: function(data){
                    popup.mould.popTipsMould(false, "备注修改成功！", popup.mould.second, popup.mould.success, "", "57%", null);
                },
                error: function () {
                    popup.mould.popTipsMould(false, "备注修改失败！", popup.mould.second, popup.mould.error, "", "57%", null);
                }
            });
        }

        $("#ok").hide();
        $("#cancel").hide();
        $("#changetips").show();
    });

    $('#cancel').mousedown(function(){
        $("#ok").hide();
        $("#cancel").hide();
        $("#changetips").show();
    });

var datatables = datatableUtil.getByDatatables(list, dataFunction.data, dataFunction.fnRowCallback, dataFunction.fnDrawCallback);


    /**
     * 搜索
     */
    $("#searchBtn").bind({
        click: function () {
            var keyword = $("#keyword").val();
            if (common.isEmpty(keyword)) {
                popup.mould.popTipsMould("请输入搜索内容", "first", "warning", "", "", null);
                return false;
            }
            datatableUtil.params.keyWord = keyword;
            datatables.ajax.reload();
        }
    });




});
