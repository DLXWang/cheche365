


$(function () {
    var list = {
        "url": '/thirdParty/toaCooperate/partnerList',
        "type": "GET",
        "table_id": "toa_tab",
        "columns": [
            {"data": "id", "title": "序号", 'sClass': "text-center", "orderable": false,"sWidth":""},
            {"data": "name", "title": "合作商", 'sClass': "text-center", "orderable": false,"sWidth":""},
            {"data": "enName", "title": "渠道英文简称", 'sClass': "text-center", "orderable": false,"sWidth":""},
            {"data": "channelName", "title": "第三方渠道名称", 'sClass': "text-center", "orderable": false,"sWidth":""},
            {"data": "landingPage", "title": "落地页", 'sClass': "text-center", "orderable": false,"sWidth":""},
            {"data": "quoteWay", "title": "报价方式", 'sClass': "text-center", "orderable": false,"sWidth":""},
            //{"data": "productLink", "title": "生产环境链接", 'sClass': "text-center", "orderable": false,"sWidth":""},
            {"data": null, "title": "启用/禁用", 'sClass': "text-center", "orderable": false,"sWidth":""},
            {
                "data": "id",
                "title": "编辑",
                render: function (data, type, row) {
                    return " <a href='/views/thirdParty/toA_details.html?id=" + data + "' target='_blank'>查看详情</a>";
                },
                "className": "text-center",
                "orderable": false
            },
        ]
    };


    var dataFunction = {
        "data": function (data) {
            data.partner = common.getUrlParam("id");
        },
        "fnRowCallback": function (nRow, aData) {
            $enable = "";
            if (aData.status) {
                $enable = '<span style="color: green;">启用中</span>' + ' <a id="disable" class="btn btn-danger" onclick="partner_list.chgStatus('+ aData.id +',false,\'datatables1\')">禁用</a>';
            } else {
                $enable = '<span style="color: darkred;">禁用中</span>' + ' <a id="disable" class="btn btn-danger" onclick="partner_list.chgStatus('+ aData.id +',true,\'datatables1\')">启用</a>';
            }
            $('td:eq(6)', nRow).html($enable);

            $('#table2').show()
            $('#notable2').hide()
        },
    }

    $('#changetips').click(function(){
        $("#changetips").hide();
        $("#ok").show();
        $("#cancel").show();
        var td=$('#tips'); //为后面文本框变成文本铺垫
        var text=$('#tips').text();
        var newtips=$('<input id ="newtips" type="text" class="edit" maxlength="200" value="'+text+'">');
        $('#tips').html(newtips);

        $("#newtips").click(function(){
            return false;
        }); //阻止表单默认点击行为

        $('#newtips').select(); //点击自动全选中表单的内容

        $('#newtips').blur(
            function(){
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


window.datatables1 = datatableUtil.getByDatatables(list, dataFunction.data, dataFunction.fnRowCallback);


});
