/**
 * Created by wangshaobin on 2017/3/30.
 */
/* 内部用户列表查询 */
var list = {
    "url": '/orderCenter/outerUser/list',
    "type": "GET",
    "table_id": "user_tab",
    "columns": [
        {"data": null, "title": "用户ID", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": null, "title": "手机号", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": null, "title": "注册时间", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": null, "title": "注册渠道", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
        {"data": null, "title": "注册IP", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
        {"data": null, "title": "最后登录", 'sClass': "text-center", "orderable": false, "sWidth": "240px"},
    ]
};

var dataFunction = {
    "data": function (data) {
        data.keyword = $("#keyword").val();
        data.keyType = $("#keyType").val();
    },
    "fnRowCallback": function (nRow, aData) {
        $id = "<a href='../../page/user/user_detail.html?id=" + aData.id + "' target='_blank'>" + aData.id + "</a>";
        $mobile = common.checkToEmpty(aData.mobile);
        $regtime = common.checkToEmpty(aData.regtime);
        $regChannel = common.tools.checkToEmpty(aData.regChannel);
        $regIp = common.checkToEmpty(aData.regIp);
        $lastLoginTime = common.checkToEmpty(aData.lastLoginTime);
        $('td:eq(0)', nRow).html($id);
        $('td:eq(1)', nRow).html($mobile);
        $('td:eq(2)', nRow).html($regtime);
        $('td:eq(3)', nRow).html($regChannel);
        $('td:eq(4)', nRow).html($regIp);
        $('td:eq(5)', nRow).html($lastLoginTime);
    }
};

var datatables = datatableUtil.getByDatatables(list, dataFunction.data, dataFunction.fnRowCallback);

$(function() {
    /* 搜索 */
    $("#searchBtn").bind({
        click : function(){
            var keyword = $("#keyword").val();
            if(common.validations.isEmpty(keyword)){
                popup.mould.popTipsMould(false,"请输入搜索内容！", popup.mould.first, popup.mould.warning, "", "57%", null);
                return false;
            }
            datatableUtil.params.keyType = $("#keyType").val();
            datatableUtil.params.keyword = keyword;
            datatables.ajax.reload();
        }
    });
});
