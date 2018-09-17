var parent = window.parent;
$(function () {
    var id = common.getUrlParam("id");
    var no = common.getUrlParam("no");
    if (id == null) {
        popup.mould.popTipsMould(false, "异常参数", popup.mould.second, popup.mould.error, "", "57%", null);
        return false;
    }
    //checkAll();

});


var imageTypeAdd = {
    //新增照片类型的弹窗
    pop: function (orderId, callback) {
        //渲染静态页
        popup.pop.popInput(false, detail.imageTypeContent, popup.mould.first, "800px", "700px", "30%", "50%");
        parent.$(".theme_poptit .close").unbind("click").bind({
            click: function () {
                popup.mask.hideFirstMask(false);
                param.ulLeft = 0;
                parent.$("#box").animate({left: param.ulLeft + "px"}, 500);
                window.location.reload();
            }
        });
        parent.$("#myClose").unbind("click").bind({
            click: function () {
                popup.mask.hideFirstMask(false);
                param.ulLeft = 0;
                parent.$("#box").animate({left: param.ulLeft + "px"}, 500);
                window.location.reload();
            }
        });
        imageTypeAdd.init(orderId);

    },

    init: function (orderId) {
        common.getByAjax(false, "get", "json", "/orderCenter/order/image/" + orderId,
            {
                'purchaseOrderId': orderId
            },
            function (data) {

                var dataList = data.dataList;
                var mustTypeIds = data.mustTypeIds;
                if (!common.validations.isEmpty(dataList) && dataList.length > 0) {
                    var imageTypeKindDivText = "";
                    $.each(dataList, function (index, viewModel) {
                        var imageType = viewModel.purchaseOrderImageType;
                        var imageTypeId = imageType.id;
                        var currentItemDivId = imageTypeId + "_itemDiv";

                        var checkedText;
                        var needChange;
                        if ($.inArray(imageTypeId, mustTypeIds) != -1) {
                            checkedText = " checked ='checked' disabled='true' ";
                            needChange = false;
                        } else {
                            checkedText = " ";
                            needChange = true;
                        }

                        imageTypeKindDivText +=
                            "<div style='margin-left: 20px ; clear:both;' >                                                                                                        " +
                            "   <div >                                                                                                                                                                       " +
                            "   	<img src='../../images/kuang.jpg' style='height: 15px; width: 15px;'>                                       " +
                                //"   	<input class='parentType' id="+imageTypeId+" name="+imageType.name+" type='checkbox' " + checkedText + " >                                       " +
                            "   	<label for=" + imageTypeId + " >                                                                                                                                                                 " +
                            "   		<div class='text'>                                                                                                                                      " +
                            "   			<p><span>" + imageType.name + "</span></p>                                                                                                                       " +
                            "   		</div>                                                                                                                                                               " +
                            "   	</label>                                                                                                                                                                 " +
                            "   </div>                                                                                                                                                                       " +
                            "                                                                                                                                                                                " +
                            "   <div style='margin-left: 20px' id=' " + currentItemDivId + " '>                                                                                                          " +

                            imageTypeAdd.initItemData(viewModel.subTypeList, checkedText, imageType, needChange) +//拼接子div(小类型)

                            "   </div>                                                                                                                                                                       " +
                            "</div>                                                                                                                                                                       ";
                    });
                    parent.$("#imageTypeKindDiv").html(imageTypeKindDivText);
                }
            },
            function () {
            }
        );
    },

    initItemData: function (subTypeList, checkedText, parentImageType, needChange) {
        var imageTypeKindDiv_itemDivText = "";
        $.each(subTypeList, function (index, subTypeModel) {
            var subImageType = subTypeModel.purchaseOrderImageType;
            if (needChange) {
                if (subTypeModel.checkedFlag) {
                    checkedText = " checked ='checked' disabled='true' ";
                } else {
                    checkedText = " ";
                }
            }

            imageTypeKindDiv_itemDivText +=
                "   	<div style='float: left;  margin-left: 20px'>                                                                                                                          " +
                "   	    <input class='childType' id=" + subImageType.id + " name=" + parentImageType.name + " type='checkbox' " + checkedText + " >  " +
                "   		<label for=" + subImageType.id + " >                                                                                                                                 " +
                "   			<div class='text'>                                                                                                                                             " +
                "   				<p><span>" + subImageType.name + "</span></p>                                                                                                                " +
                "   			</div>                                                                                                                                                           " +
                "   		</label>                                                                                                                                                             " +
                "   	</div>                                                                                                                                                                   ";
        });
        return imageTypeKindDiv_itemDivText;
    },

    //保存自定义类型
    appendImageTypeItem: function () {
        var $customTypeInputDiv = $("#customTypeInputDiv");
        var customTypeInputDivText =
            '<div  style="float: left; margin-left: 50px ; margin-top: 20px">  ' +
            '   <input name="customType" value="（限10个汉字)" type="text" style="width: 300px;" onfocus="if (value ==\'（限10个汉字)\'){value =\'\'}" onblur="if (value ==\'\'){value=\'（限10个汉字)\'}">    ' +
            '</div>';
        $customTypeInputDiv.append(customTypeInputDivText);
    },

    //保存自定义类型
    saveImageTypeItem: function () {
        var customTypes = [];
        var imageTypeIds = [];
        var flag = true;

        //获取所有新的勾选中的子类型
        var allCheckBoxs = $("div#popupHtml input:checkbox[class='childType']:checked:not([disabled])");

        $(allCheckBoxs).each(function (i) {
            imageTypeIds.push($(this).attr("id"));
        });

        //获取所有新增的自定义类型名称
        $("[name=customType]").each(function (i) {
            var customType = $(this).val().trim();
            if (!common.validations.isEmpty(customType) && customType != '（限10个汉字)') {
                var size = customType.length;
                if (size > 16) {
                    popup.mould.popTipsMould(false, "名称长度不能超过十个汉字!", popup.mould.second, popup.mould.error, "", "57%", null);
                    flag = false;
                    $(this).focus();
                    return false;
                }
                customTypes.push(customType);
            }
        });

        if (!flag) {
            return false;
        }

        if (customTypes.length == 0 && imageTypeIds.length == 0) {
            popup.mould.popTipsMould(false, "您未进行任何操作!", popup.mould.second, popup.mould.error, "", "57%", null);
            return false;
        }

        var id = common.getUrlParam("id");
        common.getByAjax(false, "post", "json", "/orderCenter/order/image/imageType",
            {
                "purchaseOrderId": id,
                "customTypes": customTypes.toString(),
                "imageTypeIds": imageTypeIds.toString()
            }, function (data) {
                popup.mould.popTipsMould(false, "增加成功!", popup.mould.second, popup.mould.success, "", "57%", null);
                imageTypeAdd.pop(id, "first");
            }, function () {
                popup.mould.popTipsMould(false, "增加失败!", popup.mould.second, popup.mould.error, "", "57%", null);
            }
        );
    },
    checkAll: function (obj, isParent) {
        if (isParent) {
            var objname = obj.name;
            var isChecked = obj.checked;

            var $list = $("input[name^=" + objname + "]");
            $.each($list, function (n, obj) {
                obj.checked = isChecked;
            });
        } else {
            var objname = obj.name;
            var checkedList = $("input[name^=" + objname + "]:gt(0):checked");
            var first = $("input[name^=" + objname + "]").get(0);
            if (checkedList.length > 0) {
                first.checked = true;
            } else {
                first.checked = false;
            }
        }
    }

}


