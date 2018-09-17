/**
 * Created by luly on 2017/03/22.
 */
$(function () {
    $("#assignStartTime").val(common.tools.formatDate(new Date(), "yyyy-MM-dd"));
    $("#assignEndTime").val(common.tools.formatDate(new Date(), "yyyy-MM-dd"));
    historySelect.select();
    /* 查询 */
    $("#searchBtn").bind({
        click: function () {
            historySelect.select();
        }
    });
    popup.insertHtml($("#popupHtml"));
    historySelect.initDetail();
});


var dataFunction = {
    "data": function (data) {
        data.startTime = $("#assignStartTime").val();
        data.endTime = $("#assignEndTime").val();
    },
    "fnRowCallback": function (nRow, aData) {
        $id = nRow._DT_RowIndex + 1;
        $('td:eq(0)', nRow).html($id);
        if (aData.sourceName == null) {
            $operate = "<a href=\"javascript:;\" onclick=\"historySelect.getDetail(" + aData.id + ");\">详情</a>";
            $('td:eq(7)', nRow).html($operate);
        } else {
            $('td:eq(7)', nRow).html("<span></span>");
        }
    },
}

var historySelect = {
    param: {
        dataTables: null,
        detailTables: null,
        dataList: null,
        detailList: null
    },
    page: new Properties(1, ""),

    /* 初始化弹出页信息 */
    initDetail: function () {
        var detailContent = $("#detail_content");
        historySelect.detailContent = detailContent.html();
        detailContent.remove();
    },

    check: function () {
        var flag = true, msg = "";
        var assignStartTime = $("#assignStartTime").val();
        var assignEndTime = $("#assignEndTime").val();
        if (!assignStartTime || !assignEndTime) {
            flag = false;
            msg = "请将数据分配时间填写完整";
        }
        if (common.tools.dateTimeCompare(assignStartTime, assignEndTime) < 0) {
            flag = false;
            msg = "数据分配结束时间不能早于开始时间";
        }

        return {flag: flag, msg: msg}
    },

    select: function () {
        var checkJson = this.check();
        if (!checkJson.flag) {
            common.showTips(checkJson.msg);
            return;
        }
        if (historySelect.param.dataTables != null) {
            historySelect.param.dataTables.ajax.reload();
            return false;
        }
        historySelect.param.dataList = {
            "url": '/orderCenter/telMarketingCenter/assign/dataAssignHistory',
            "type": "get",
            "table_id": "result_tab",
            "columns": [
                {"data": null, "title": '序号', className: "text-center", "orderable": false, "sWidth": "30px"},
                {"data": "createTime", "title": "分配时间", 'sClass': "text-center", "orderable": false, "sWidth": "140px"},
                {"data": "assignNum", "title": "分配数量", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},
                {"data": "targetName", "title": "指定人", 'sClass': "text-center", "orderable": false, "sWidth": "90px"},
                {
                    data: "channel", "title": '渠道',
                    render: function (data, type) {
                        if (type === 'display') {
                            return "<span class='textShow' title='" + data + "'>" + data + "</span>";
                        }
                        return data;
                    }, className: "text-center", "orderable": false, "sWidth": "100px"
                },
                {
                    data: "sourceType", "title": '类型',
                    render: function (data, type) {
                        if (type === 'display') {
                            return "<span class='textShow' title='" + data + "'>" + data + "</span>";
                        }
                        return data;
                    }, className: "text-center", "orderable": false, "sWidth": "100px"
                },
                {
                    data: "area", "title": '城市',
                    render: function (data, type) {
                        if (type === 'display') {
                            return "<span class='textShow' title='" + data + "'>" + data + "</span>";
                        }
                        return data;
                    }, className: "text-center", "orderable": false, "sWidth": "100px"
                },

                {"data": null, "title": "操作", 'sClass': "text-center", "orderable": false, "sWidth": "100px"}
            ]
        };
        dt_labels.aLengthMenu = [[15, 20], [15, 20]];
        historySelect.param.dataTables = datatableUtil.getByDatatables(historySelect.param.dataList, dataFunction.data, dataFunction.fnRowCallback);
    },
     /*getCountInfo: function () {
        var reqJson = historySelect.generateReqJson();
        common.getByAjax(false, "get", "json", "/orderCenter/telMarketingCenter/assign/getCountInfo", reqJson,
            function (data) {
                $("#inputNum").text(data.inputNum);
                $("#assignedNum").text(data.assignNum);
                $("#newDataNum").text(data.newDataNum);
                $("#oldDataNum").text(data.oldDataNum);
            }, function () {}
        );
     },*/

    getDetail: function (id) {
        popup.pop.popInput(false, historySelect.detailContent, 'first', "800px", "600px", "30%", "47%");
        historySelect.detail(id);
        parent.$("#close").unbind("click").bind({
            click: function () {
                popup.mask.hideFirstMask(false);
            }
        });
    },

    detail: function (id) {
        historySelect.param.detailList = {
            "url": '/orderCenter/telMarketingCenter/assign/dataAssignDetail',
            "type": "get",
            "table_id": "detail_tab",
            "columns": [
                {"data": null, "title": '序号', className: "text-center", "orderable": false, "sWidth": "30px"},
                {"data": "assignNum", "title": "分配数量", 'sClass': "text-center", "orderable": false, "sWidth": "100px"},
                {"data": "targetName", "title": "指定人", 'sClass': "text-center", "orderable": false, "sWidth": "90px"}
            ]
        };
        dt_labels.aLengthMenu = [[10, 15], [10, 15]];
        historySelect.param.detailTables = datatableUtil.getByDatatables(historySelect.param.detailList, function (data) {
            data.id = id;
        }, function (nRow) {
            $id = nRow._DT_RowIndex + 1;
            $('td:eq(0)', nRow).html($id);
        });
    },

    generateReqJson: function () {
        return {
            startTime: $("#assignStartTime").val(),
            endTime: $("#assignEndTime").val()
        }
    },
}
