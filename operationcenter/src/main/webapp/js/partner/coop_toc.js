(function(){

    var dataFunction = {
        "data": function (data) {
            data.partner = common.getUrlParam("id");
        },
        "fnRowCallback": function (nRow, aData) {
            $enable = "";
            if (aData.status) {
                $enable = '<span style="color: green;">启用中</span>' + ' <a id="disable" class="btn btn-warning" onclick="partner_list.chgStatus('+ aData.id +',false,\'datatables2\')">禁用</a>';
            } else {
                $enable = '<span style="color: darkred;">禁用中</span>' + ' <a id="disable" class="btn btn-warning" onclick="partner_list.chgStatus('+ aData.id +',true,\'datatables2\')">启用</a>';
            }
            $('td:eq(6)', nRow).html($enable);
            $('#table1').show()
            $('#notable1').hide()
        },
    }
    var tocList = {
        "url": '/thirdParty/tocCooperate/partnerList',
        "type": "GET",
        "table_id": "toc_tab",
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
                    return " <a href='/views/thirdParty/toC_details.jsp?id=" + data + "' target='_blank'>查看详情</a>";
                },
                "className": "text-center",
                "orderable": false
            },
        ],
    }

    window.datatables2 = datatableUtil.getByDatatables(tocList,dataFunction.data,dataFunction.fnRowCallback);

}())
