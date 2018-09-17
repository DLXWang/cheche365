/**
 * Created by wangshaobin on 2017/7/11.
 */
var parent;
var quoteResendGift = {
    position: "",
    resendGiftDisplayContent: "",
    selectorContent: "",
    totalPremium: "",
    giftList: new Array(),
    order: "",
    unit: ['个','元','张','台','对','双','条','斤','只','份','件'],
    num: 0,
    initDisplayContent: function() {
        var $resendGiftContent = $("#resendGiftContent");
        if ($resendGiftContent.length > 0) {
            quoteResendGift.resendGiftDisplayContent = $resendGiftContent.html();
            $resendGiftContent.remove();
        }
    },
    displayResendGifts: function(isParent,position) {
        quoteResendGift.num = 0;
        quoteResendGift.position = position;
        // quoteResendGift.order = order;
        parent=isParent?window:window.parent;
        quoteResendGift.interface.getResendGifts(
            function(data) {
                //初始化弹出框
                quoteResendGift.initDisplayContent();
                popup.pop.popInput(isParent, quoteResendGift.resendGiftDisplayContent, quoteResendGift.position, "420px", "auto", "40%", "51%");

                var $popInput = common.tools.getPopInputDom(quoteResendGift.position, isParent);
                $popInput.find(".theme_poptit_color #regend_gift_close").unbind("click").bind({
                    click: function () {
                        popup.mask.hideFirstMask(isParent);
                    }
                });

                var addBtn = $("#addResendGift").clone(true).attr('id', "addBtn").removeClass("none");
                addBtn.unbind("click").bind({
                    click: function() {
                        /*var addBtn = $("#addBtn").clone(true);*/
                        var selectInfo = quoteResendGift.createSelect(quoteResendGift.num, data).append($("#addBtn").clone(true));
                        $("#addBtn").remove();
                        $popInput.find("#resend_gift_list").append(selectInfo);
                        quoteResendGift.num++;

                        window.parent.$(selectInfo).find('[id^="gift_sel_"]').select2({
                            dropdownCss:{'z-index':9999}
                        });
                    }
                });

                //获取数据，进行数据填充
                if(quoteResendGift.giftList.length == 0){
                    var selectInfo = quoteResendGift.createSelect(quoteResendGift.num, data).append(addBtn);
                    $popInput.find("#resend_gift_list").append(selectInfo);
                    quoteResendGift.num++;
                }else{
                    //数据回显
                    for(var i=0;i<quoteResendGift.giftList.length;i++){
                        var selectedInfo = quoteResendGift.giftList[i];
                        var selectInfo = quoteResendGift.createSelect(quoteResendGift.num, data, selectedInfo);
                        if(i == quoteResendGift.giftList.length-1){//在最后一行，显示“添加”按钮
                            selectInfo.append(addBtn);
                        }
                        $popInput.find("#resend_gift_list").append(selectInfo);
                        quoteResendGift.num++;
                    }
                }
                window.parent.$("[id^='gift_sel_']").select2({
                    dropdownCss:{'z-index':9999}
                });

                parent.$("#addOK").unbind("click").bind({
                    click: function() {
                        quoteResendGift.giftList.length = 0;
                        quoteResendGift.selectorContent = "";
                        var errorMsg = "";
                        parent.$("div[name='selectDiv']").each(function(){
                            var money = $(this).find("span input").val();
                            var selectedIndex = $(this).find("select[id^=gift_sel_]").val();
                            var selectedContent = $(this).find("select[id^=gift_sel_]").find("option:selected").text();
                            var unit = $(this).find("span select").val();
                            if(money == ""){
                                errorMsg = "不能为空";
                            }else if(money == 0){
                                errorMsg = "必须大于0";
                            }else if(unit != "元" && money > 127){
                                errorMsg = "必须小于128";
                            }
                            var giftObj =
                                {
                                    amount: money,
                                    type:   selectedIndex,
                                    unit:   unit
                                };
                            quoteResendGift.giftList.push(giftObj);
                            quoteResendGift.selectorContent += money + unit + selectedContent + "；";
                        });
                        if(errorMsg != ""){
                            quoteResendGift.giftList.length = 0;
                            popup.mould.popTipsMould(isParent, "礼品单位数量" + errorMsg + "，请重新核对礼品信息", popup.mould.second, popup.mould.warning, "好", "",
                                function() {
                                    popup.mask.hideSecondMask(isParent);
                                }
                            );
                        }else{
                            popup.mask.hideFirstMask(isParent);
                            var prefix = "";
                            if($("#giftAmountText").html())
                                prefix = "；";
                            $("#resendGiftAmountText").html(prefix + quoteResendGift.selectorContent);
                        }
                    }
                });
                parent.$("#addCancel").unbind("click").bind({
                    click: function() {
                        popup.mask.hideFirstMask(isParent);
                    }
                });
            },
            function() {
                popup.mould.popTipsMould(isParent, "获取额外赠送礼品异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },
    createSelect: function(id, giftList, selectedInfo){
        var selectorDiv = $('<div></div>');
        selectorDiv.attr('id', id).attr('name', 'selectDiv');
        selectorDiv.css('display', 'block').css('padding-bottom', '10px');
        var selector = $("#regend_gift_select").clone(true).attr('id', "gift_sel_" + id);
        var spaner = $("#regend_gift_span").clone(true).attr('id', "gift_span_" + id);
        var inputer = spaner.find("input").attr('id', 'input_' + id);
        common.tools.setDomNumAction(inputer);
        quoteResendGift.createSelectUnit(spaner,selectedInfo,id);
        if (giftList) {
            var options = "";
            if(selectedInfo!=undefined )
                $.each(giftList, function(i, gift){
                    options += "<option value=\"" + gift.id + "\" " + (gift.id == selectedInfo.type?'selected':'') + ">" + gift.description + "</option>";
                    inputer.val(selectedInfo.amount);
                });
            else
                $.each(giftList, function(i, gift){
                    options += "<option value=\"" + gift.id + "\">" + gift.description + "</option>";
                });
            if(selector.length>0){
                selector.append(options);
            }
            selector.css("display", "inline-block");
        }
        var delBtn = $("#delResendGift").clone(true).attr('id', "del_" + id).removeClass("none");
        delBtn.unbind("click").bind({

            click: function() {
                var len = parent.$("div[name='selectDiv']").length;
                var delRow = parent.$("#" + id);
                if(len > 1){
                    if(delRow.find("#addBtn").length > 0){//如果是删除的最后一行，“添加”按钮需要显示在到被删除后列表的最后一行
                        var addBtn = $("#addBtn").clone(true);
                        delRow.prev().append(addBtn);
                    }
                }else{
                    var addBtn = $("#addBtn").clone(true);
                    parent.$("#popover_normal_input").find("#resend_gift_list").append(addBtn);
                }
                delRow.remove();
            }
        });
        return selectorDiv.append(spaner).append(selector).append(delBtn);
    },
    createSelectUnit:function(spaner,selectedInfo,id){
        var giftList = quoteResendGift.unit;
        var selector = spaner.find("#resend_gift_unit_select").attr('id', "gift_unit_sel_" + id);

        var options = "";
        if(selectedInfo!=undefined)
            $.each(giftList, function(i, gift){
                options += "<option value=\"" + gift + "\" " + (gift == selectedInfo.unit?'selected':'') + ">" + gift + "</option>";
            });
        else
            $.each(giftList, function(i, gift){
                options += "<option value=\"" + gift + "\">" + gift + "</option>";
            });
        if(selector.length>0){
            selector.append(options);
        }
        selector.css("display", "inline-block");
    },
    interface: {
        getResendGifts: function(callback_success, callback_fail) {
            common.ajax.getByAjax(true, "get", "json", "/orderCenter/gift/resendGifts", {},
                function(data) {
                    callback_success(data);
                },
                function() {
                    callback_fail();
                }
            );
        }
    }
}
