/**
 * Created by cxy on 2016/12/13.
 */

var dt_labels = {
    "aLengthMenu": [[10, 15, 20], [10, 15, 20]],//暂时只写这一个
    "order": true,
    "hasCallBack": true,
    "hasDrawCallback": false,
    "hasData": true,
    "bPaginate": true,// 分页按钮
    "bLengthChange": true,
    "paging": true,
    "info": true,
    "selected": [],
    "tableId": "",
    "language":    //DataTable中文化
    {
        "sProcessing": "正在加载中......",
        "sLengthMenu": "每页显示 _MENU_ 条记录",
        "sZeroRecords": "对不起，查询不到相关数据！",
        "sEmptyTable": "表中无数据存在！",
        "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录",
        "sInfoFiltered": "数据表中共为 _MAX_ 条记录",
        "sInfoEmpty": "",
        "sSearch": "搜索",
        "oPaginate": {
            "sFirst": "首页",
            "sPrevious": "上一页",
            "sNext": "下一页",
            "sLast": "末页"
        }
    }
}
var datatableUtil = {
    params: {
        userRoles: "",
        keyType: "",
        keyWord: ""
    },
    /* ajax请求 */
    getByDatatables: function (dataList, dataFunc, callBackFunc, drawCallbackFunc) {
        dt_labels.tableId = dataList.table_id;
        var datatables = $('#' + dataList.table_id).DataTable({
            //var param;param[11] = 12;
            "paging": dt_labels.paging,
            "aLengthMenu": dt_labels.aLengthMenu,
            "order": dt_labels.order,
            "processing": true,
            "searching": false,
            "bPaginate": true, //翻页功能
            "bLengthChange": dt_labels.bLengthChange, //改变每页显示数据数量
            "bFilter": true, //过滤功能
            "bSort": true, //排序功能
            "serverSide": true,
            "sPaginationType": "full_numbers",      //翻页界面类型
            //"sAjaxSource": "/orderCenter/dataTable",
            //"fnServerData":retrieveData, //与后台交互获取数据的处理函数
            "info": dt_labels.info,
            "oLanguage": dt_labels.language,
            ajax: {
                "type": dataList.type,
                "url": dataList.url,
                "data": function (data) {
                    window.parent.scrollTo(0, 0);
                    data.currentPage = data.start / data.length + 1;
                    data.pageSize = data.length;
                    if (dt_labels.hasData) {
                        dataFunc(data);
                    }
                },
                error: function (xhr) {
                    if (!common.sessionTimeOut(xhr) && !common.accessDenied(xhr) && !common.noPermissionLogin(xhr)) {
                        popup.mould.popTipsMould(false, "获取列表失败", popup.mould.first, popup.mould.error, "", "", null);
                    }
                }
            },
            "bPaginate": dt_labels.bPaginate,// 分页按钮

            "sPaginationType": "full_numbers",
            "columns": dataList.columns,
            "fnRowCallback": function (nRow, aData) {
                if ($.inArray(aData.id + "", dt_labels.selected) != -1) {
                    $(nRow).find(".check-box-single").attr("checked", true);
                }
                if (dt_labels.hasCallBack) {
                    callBackFunc(nRow, aData);
                }

            },
            "fnDrawCallback": function () {
                if (drawCallbackFunc != undefined)
                    drawCallbackFunc(this);
                if (dt_labels.hasDrawCallback) {
                    var api = this.api();
                    var startIndex = api.context[0]._iDisplayStart;//获取到本页开始的条数
                    api.column(0).nodes().each(function (cell, i) {
                        cell.innerHTML = startIndex + i + 1;
                    });
                }
            }
        });
        var tableId = dataList.table_id;
        datatables.on("draw", function () {
            $("#" + tableId + " tbody tr:odd").css("background-color", "");
            $("#" + tableId + " tbody tr:even").css("background-color", "#ececec");
        });
        $("select[name=" + tableId + "_length]").addClass('form-control');
        $("select[name=" + tableId + "_length]").width("60px");
        $("select[name=" + tableId + "_length]").css("margin-top", "5px");
        $("select[name=" + tableId + "_length]").css("margin-bottom", "10px");

        $("#" + dt_labels.tableId + " tbody").on('click', 'tr', function () {
            var id = $(this).find(".check-box-single").val();
            var index = $.inArray(id, dt_labels.selected);
            if (index == -1) {
                dt_labels.selected.push(id);
                $(this).find(".check-box-single").prop("checked", true);
            } else {
                dt_labels.selected.splice(index, 1);
                $(this).find(".check-box-single").prop("checked", false);
            }
        });

        $(".check-box-all").bind({
            click: function () {
                var checked = $(this).prop("checked");
                if (checked) {
                    $(".check-box-single").prop("checked", true);
                    $(".check-box-single").each(function (i) {
                        if ($.inArray($(this).val(), dt_labels.selected) == -1) {
                            dt_labels.selected.push($(this).val());
                        }
                    });
                } else {
                    $(".data-checkbox").prop("checked", false);
                    $(".check-box-single").each(function (i) {
                        var index = $.inArray($(this).val(), dt_labels.selected);
                        dt_labels.selected.splice(index, 1);
                    });
                }
            }
        });
        return datatables;
    }
}


