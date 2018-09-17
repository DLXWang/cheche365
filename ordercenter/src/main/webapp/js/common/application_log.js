/**
 * Created by wangfei on 2015/11/20.
 */
var applicationLog = {
    popCommentList: function(objTable,objId,position,callback) {
        $.post("../comment/application_log.html", {}, function (commentComment) {
            applicationLog.interface.getLogList(objTable,objId,function(logData) {
                popup.pop.popInput(false, commentComment, position, "560px", "auto", "45%", "54%");
                var $popInput;
                switch (position) {
                    case popup.mould.first:
                        $popInput = window.parent.$("#popover_normal_input");
                        break;
                    case popup.mould.second:
                        $popInput = window.parent.$("#popover_normal_input_second");
                        break;
                }
                $popInput.find(".theme_poptit .close").unbind("click").bind({
                    click: function () {
                        switch (position) {
                            case popup.mould.first:
                                popup.mask.hideFirstMask(false);
                                break;
                            case popup.mould.second:
                                popup.mask.hideSecondMask(false);
                                break;
                        }
                    }
                });
                var addBtn = $popInput.find(".toAddComment");
                addBtn.unbind("click").bind({
                    click: function () {
                        var content = $popInput.find("#content");
                        if (content.val()) {
                            addBtn.attr("disabled", true);
                            applicationLog.interface.addLog(objId,objTable,content.val(),
                                function() {
                                    applicationLog.interface.getLogList(objTable,objId,
                                        function(logList) {
                                            applicationLog.fixLogList(logList, $popInput);
                                            addBtn.attr("disabled", false);
                                            content.val("");
                                            if (callback) {
                                                callback();
                                            }
                                        }
                                    );
                                }
                            );
                        }
                    }
                });
                applicationLog.fixLogList(logData, $popInput);
            });
        });
    },
    fixLogList: function(data, $popInput) {
        var listContent = "";
        if (data) {
            $.each(data, function(index, comment) {
                listContent +=
                    "<p>" +
                    "<span style=\"color: #0099FF;\">" +
                    "[" + comment.createTime + "] " + comment.operatorName + "：" +
                    "</span>" +
                    comment.logMessage +
                    "</p>";
            });
        }
        if (listContent) {
            $popInput.find("#comment_list").html(listContent);
        }
        applicationLog.scrollToBottom();
    },
    scrollToBottom: function() {
        var commentDoc = window.parent.document.getElementById("comment_list");
        commentDoc.scrollTop = commentDoc.scrollHeight;
    },
    interface: {
        getLogList: function(objTable,objId,callback) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/applicationLog/" +objTable+"/"+ objId, {},
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "获取备注记录异常！", popup.mould.second, popup.mould.error, "", "58%", null);
                }
            );
        },
        addLog: function(objId,objTable,comment,callback) {
            common.ajax.getByAjaxWithJson(true, "post", "json", "/orderCenter/applicationLog",
                {
                    objId:objId,
                    objTable:objTable,
                    logMessage: comment
                },
                function(data) {
                    callback(data);
                },
                function() {
                    popup.mould.popTipsMould(false, "新增备注异常！", popup.mould.second, popup.mould.error, "", "58%", null);
                }
            );
        }
    }
};
