
var marketingAdd = {
    activityTypeId:[4,5,6,7],
    discountCount:1,
    presentCount:1,
    extraCount:1,
    insuranceTypeId:['insuranceMust','fullIncludes','insuranceType','notMoreThanInsuranceType','notMoreThanInsuranceTypeByGift'], 

    overview:function(id){
        common.getByAjax(true, "get", "json", "/operationcenter/marketingRule/findOneOverview",{id:id},
            function(data){
                if (data) {
                    $("#titleTbl").html(data.title);
                    $("#subTitleTbl").html(data.subTitle);
                    $("#strategyTbl").html("<div  style='width: 300px;word-break:break-all'>" + data.description + "</div>");
                    $("#userGuideTbl").html(data.userGuide);
                    $("#activityTypeInfo").html(data.activityTypeInfo + "</p>" + data.activityInfo.join('</p>'));

                    $("#insuranceMustDiv").html(data.insuranceMust);
                    $("#fullIncludesDiv").html(data.fullIncludes);
                    $("#channelTbl").html(data.channel);
                    $("#insuranceCompanyDiv").html(data.insuranceCompany);
                    $("#areaTbl").html(data.area);
                    $("#expireDateId").html(data.expireDate);
                    $("#effectiveDateId").html(data.effectiveDate);
                    if(data.activityType == marketingAdd.activityTypeId[3]){
                        $(".gift_hidden").hide();;
                    }
                    if(data.marketingShared.wechatShared == 1){
                        $("#wechatSharedTbl").html("是");
                        $("#weixinShareTr").show();
                        $("#wechatMainTitleTbl").html("主标题："+ common.checkToEmpty(data.marketingShared.wechatMainTitle));
                        $("#wechatSubTitleTbl").html("副标题：" + common.checkToEmpty(data.marketingShared.wechatSubTitle));
                    }else{
                        $("#wechatSharedTbl").html("否");
                    }
                    if(data.marketingShared.alipayShared == 1){
                        $("#alipaySharedTbl").html("是");
                        $("#alipayShareTr").show();
                        $("#alipayMainTitleTbl").html("主标题：" + common.checkToEmpty(data.marketingShared.alipayMainTitle));
                        $("#alipaySubTitleTbl").html("副标题：" + common.checkToEmpty(data.marketingShared.alipaySubTitle));
                    }else{
                        $("#alipaySharedTbl").html("否");
                    }

                    if(!common.isEmpty(data.marketingShared.sharedIcon)){
                        $("#sharedPicTr").show();
                        $("#shardIconTbl").html("<image width='200' height='200' src='" +  data.marketingShared.sharedIcon +"'></image>");
                    }
                    if(!common.isEmpty(data.topImage)){
                        $("#activityPicTbl").html("<image width='375' height='210' src='" +  data.topImage +"'></image>");
                    }else{
                        $("#activityPicTr").hide();
                    }
                    $("#toSave").hide();
                }
            },
            function(){
                $("#add_form").html("系统异常");
                $("#toSave").hide();
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "53%", null);
            }
        );
    },
    edit:function(id){
        common.getByAjax(false, "get", "json", "/operationcenter/marketingRule/findOneEdit",{id:id},
            function(data){
                if (data) {
                    marketingAdd.activityTypeList();
                    marketingAdd.insuranceCheckbox(marketingAdd.insuranceTypeId);
                    marketingAdd.present("choosePresent");
                    marketingAdd.extraPresent("chooseExtraPresent");
                    $("#id").val(data.id);
                    $("#title").val(data.title);
                    $("#sub_title").val(data.subTitle);
                    $("#description").val(data.description);
                    $("#userGuide").val(data.userGuide);
                    $("#effectiveDate").val(data.effectiveDate);
                    $("#expireDate").val(data.expireDate);
                    if(data.activityType == marketingAdd.activityTypeId[3]){
                        $(".gift_hidden").hide();
                    }else{
                        var insuranceMustArr = data.insuranceMust.split(',');
                        for(var i = 0;i<insuranceMustArr.length;i++){
                            $("#insuranceMust" + insuranceMustArr[i]).attr("checked",true);
                        }
                        var fullIncludesArr = data.fullIncludes.split(',');
                        for(var i = 0;i<fullIncludesArr.length;i++){
                            $("#fullIncludes" + fullIncludesArr[i]).attr("checked",true);
                        }
                    }
                    marketingAdd.activityEdit(data);
                    //$("#activityTypeInfo").html(data.activityType + "</p>" + data.activityInfo.join('</p>'));
                    //$("#insuranceMustDiv").html(data.insuranceMust);
                    //$("#fullIncludesDiv").html(data.fullIncludes);
                    $("#channelTbl").html(data.channel + "<input type='hidden' name='channel' value='" + data.channelId + "'/>");
                    $("#insuranceCompanyDiv").html(data.insuranceCompany + "<input type='hidden' name='insuranceCompany' value='" + data.insuranceCompanyId + "'/>");
                    $("#areaTbl").html(data.area + "<input type='hidden' name='area' value='" + data.areaId + "'/>");
                    if(data.marketingShared.wechatShared == 1){
                        $("#weixinShareTr").show();
                        $("#wechatyes").attr("checked","checked");
                        $("#wechatMainTitle").val(data.marketingShared.wechatMainTitle);
                        $("#wechatSubTitle").val(data.marketingShared.wechatSubTitle);
                    }else{
                        $("#wechatno").attr("checked","checked");
                    }
                    if(data.marketingShared.alipayShared == 1){
                        $("#alipayShareTr").show();
                        $("#alipayyes").attr("checked","checked");
                        $("#alipayMainTitle").val(data.marketingShared.alipayMainTitle);
                        $("#alipaySubTitle").val(data.marketingShared.alipaySubTitle);
                    }else{
                        $("#alipayno").attr("checked","checked");
                    }
                    if(!common.isEmpty(data.marketingShared.sharedIcon)){
                        $("#sharedPicTr").show();
                        $("#sharedIconImage").show();
                        $("#sharedIconImage").next().show();
                        $("#sharedIconPic").val(data.marketingShared.sharedIcon);
                        $("#sharedIconImage").attr("src", data.marketingShared.sharedIcon);
                    }
                    if(!common.isEmpty(data.topImage)){
                        $("#activityImage").show();
                        $("#activityImage").next().show();
                        $("#activityImage").attr("src", data.topImage);
                        $("#activityPic").val(data.topImage);
                    }
                }
                marketingAdd.activityType();
            },
            function(){
                $("#add_form").html("系统异常");
                $("#toSave").hide();
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "53%", null);
            }
        );

        window.parent.$("#toSave").unbind("click").bind({
            click: function() {
                if(!marketingAdd.validateAdd()){
                    return;
                }
                common.getByAjax(false, "POST", "json","/operationcenter/marketingRule/add",window.parent.$("#add_form").serialize(),
                    function(data) {
                        if (data.pass) {
                            popup.mould.popTipsMould("保存成功！", popup.mould.first, popup.mould.success, "", "53%", null);
                        } else {
                            popup.mould.popTipsMould("发生异常,请稍后再试！！", popup.mould.first, popup.mould.error, "", "53%", null);
                            window.parent.$("#toSave").attr("disabled",false);
                        }
                    },
                    function() {
                    }
                );
            }
        });
    },
    add:function(){
        window.parent.$("#toSave").unbind("click").bind({
            click: function() {
                if(!marketingAdd.validateAdd()){
                    return;
                }if(!marketingAdd.validateCompanyAreaChannel()){
                    return;
                }
                window.parent.$("#toSave").attr("disabled",true);
                common.getByAjax(true, "post", "json","/operationcenter/marketingRule/add",window.parent.$("#add_form").serialize(),
                    function(data) {
                        if (data.pass) {
                            popup.mould.popTipsMould("保存成功！", popup.mould.first, popup.mould.success, "", "53%", null);
                        } else {
                            popup.mould.popTipsMould("发生异常,data返回异常,请稍后再试！！", popup.mould.first, popup.mould.error, "", "53%", null);
                            window.parent.$("#toSave").attr("disabled",false);
                        }
                    },
                    function() {
                        popup.mould.popTipsMould("发生异常,获取不到data,请稍后再试！！", popup.mould.first, popup.mould.error, "", "53%", null);
                        window.parent.$("#toSave").attr("disabled",false);
                    }
                );
            }
        });
        window.parent.$("#result_detail").unbind("keyup").bind({
            keyup: function() {
                if(common.isEmpty($(this).val())){
                    CUI.select.hide();
                    $("#result_detail").val("");
                    return;
                }
                common.getByAjax(true,"get","json","/operationcenter/resource/areas/getByKeyWord",
                    {
                        keyword:$(this).val()
                    },
                    function(data){
                        if(data == null){
                            return;
                        }
                        var map=new Map();
                        $.each(data, function(i,model){
                            map.put(model.id,model.name);
                        });
                        CUI.select.showTag(window.parent.$("#result_detail"),300,map,false,window.parent.$("#trigger_city"));
                    }
                );
            }
        });
        marketingAdd.initSelect();
        marketingAdd.activityType();
        marketingAdd.insuranceCheckbox(marketingAdd.insuranceTypeId);
        marketingAdd.companyCheckbox();
        marketingAdd.present("choosePresent");
        marketingAdd.extraPresent("chooseExtraPresent");
        marketingAdd.channel();
        marketingAdd.activityTypeList();
    },

    /* 活动类型列表查询 */
    activityTypeList : function(){
        common.getByAjax(false, "get", "json", "/operationcenter/marketingRule/activityTypeList",{},
            function(data){
                if (data) {
                    var options = "";
                    $.each(data, function(i, model){
                        for(j=0;j<marketingAdd.activityTypeId.length;j++){
                            if(marketingAdd.activityTypeId[j] == model.id){
                                options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                            }
                        }
                    });
                    $("#activityType").append(options);
                }
            },
            function(){
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "53%", null);
            }
        );
    },
    initSelectDiscount:function(){
        $("#addDiscount").unbind("change").bind({
            click:function(){
                if($("#isAccumulateTrue").prop('checked') == true) {
                    popup.mould.popTipsMould( "累计时只能选一个", popup.mould.first, popup.mould.error, "", "57%", null);
                    return false;
                }
                $('#discountUL').append("<li class='discount_repeat'><p>"
                    + "         满<input type='text' class='discountByMoneyInput form-control text-input-150' name='discountByMoneyList[" + marketingAdd.discountCount + "].full'  style='width: 100px' style='margin-top: 5px;'/>元"
                    + "  减<input type='text' class='discountByMoneyInput form-control text-input-150' name='discountByMoneyList[" + marketingAdd.discountCount + "].discount'   style='width: 100px' style='margin-top: 5px;'/>元"
                    + "<span class='glyphicon glyphicon-remove add_del_span' onclick='this.parentNode.parentNode.remove()'></span></p></li>");
                marketingAdd.discountCount++;
            }
        });
    },
    initPresent:function(){
        $("#addPresent").unbind("change").bind({
            click:function(){
                if($("#isAccumulateTrue").prop('checked') == true) {
                    popup.mould.popTipsMould( "累计时只能选一个", popup.mould.first, popup.mould.error, "", "57%", null);
                    return false;
                }
                $('#presentUL').append("<li class='discount_repeat'><p style='padding-left:17px;'>"
                    + "  满<input type='text' class=' presentInput form-control text-input-150' name='presentList[" + marketingAdd.presentCount + "].full'  class='presentInput' style='width: 100px' style='margin-top: 5px;'/>元"
                    + "  送<input type='text' class=' presentInput form-control text-input-150' name='presentList[" + marketingAdd.presentCount + "].discount' class='presentInput'  style='width: 100px' style='margin-top: 5px;'/>元  "
                    + "<select id='choosePresent"+ marketingAdd.presentCount +"' name='presentList[" + marketingAdd.presentCount + "].present' class='form-control text-input-150 presentSelect' style='margin-left:15px;margin-right:15px;margin-top: 5px;'>"
                    + "<option value='0'>请选择礼物 </option></select>"
                    + "<span class='glyphicon glyphicon-remove add_del_span'  onclick='this.parentNode.parentNode.remove()'></span></p></li>");
                marketingAdd.present("choosePresent"+ marketingAdd.presentCount);
                marketingAdd.presentCount++;
            }
        });
    },
    initPresentExtra:function(){
        $("#addExtra").unbind("change").bind({
            click:function(){
                $('#extraPresentUL').append("<li class='discount_repeat'><p style='padding-left:17px;'>"
                    + "  满<input type='text' class='extraInput form-control text-input-150' name='extraPresentList[" + marketingAdd.extraCount + "].full'   style='width: 100px' style='margin-top: 5px;'/>元"
                    + "  送<input type='text' class='extraInput form-control text-input-150' name='extraPresentList[" + marketingAdd.extraCount + "].discount'  style='width: 100px' style='margin-top: 5px;'/>元  "
                    + "<select id='chooseExtraPresent"+ marketingAdd.extraCount +"' name='extraPresentList[" + marketingAdd.extraCount + "].present' class='extraSelect form-control text-input-150' style='margin-left:15px;margin-right:15px;margin-top: 5px;'>"
                    + "<option value='0'>请选择礼物 </option></select>"
                    + "<span class='glyphicon glyphicon-remove add_del_span' onclick='this.parentNode.parentNode.remove()'></span></p></li>");
                marketingAdd.extraPresent("chooseExtraPresent"+ marketingAdd.extraCount);
                marketingAdd.extraCount++;
            }
        });
    },
    initSelect:function(){
        marketingAdd.initSelectDiscount();
        marketingAdd.initPresent();
        marketingAdd.initPresentExtra();
    },

    present:function(name){
        common.getByAjax(false, "get", "json", "/operationcenter/marketingRule/giftTypeList",{},
            function(data){
                if (data) {
                    var options = "";
                    $.each(data, function(i, model){
                        options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                    });
                    $("#" + name).append(options);
                }
            },
            function(){
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "53%", null);
            }
        );
    },
    extraPresent:function(name){
        common.getByAjax(false, "get", "json", "/operationcenter/marketingRule/extraGiftTypeList",{},
            function(data){
                if (data) {
                    var options = "";
                    $.each(data, function(i, model){
                        options += "<option value=\"" + model.id + "\">" + model.description + "</option>";
                    });
                    $("#" + name).append(options);
                }
            },
            function(){
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "53%", null);
            }
        );
    },
    activityTypeShow:function(id){
        $("#discountByMoney").css("display","none");
        $("#discountByInsurance").css("display","none");
        $("#present").css("display","none");
        $("#present").css("display","none");
        $("#extraPresent").css("display","none");
        $("#discountGift").css("display","none");
        $("#isAccumulate1").html("");
        $("#isAccumulate2").html("");
        $("#notMoreThan1").html("");
        $("#notMoreThan2").html("");
        $(".gift_hidden").show();;
        if(id == marketingAdd.activityTypeId[0]){
            $("#discountByMoney").css("display","inline");
            $("#extraPresent").css("display","inline");
            marketingAdd.isAccumulate("isAccumulate1");
            $("#notMoreThan1Parent").hide();
            //marketingAdd.notMoreThan("notMoreThan1");
        }else if(id == marketingAdd.activityTypeId[2]){;
            $("#discountByInsurance").css("display","inline");
            $("#extraPresent").css("display","inline");
        }else if(id == marketingAdd.activityTypeId[1]){
            $("#present").css("display","inline");
            $("#extraPresent").css("display","inline");
            marketingAdd.isAccumulate("isAccumulate2");
            $("#notMoreThan2Parent").hide();
            //marketingAdd.notMoreThan("notMoreThan2");
        }else if(id == marketingAdd.activityTypeId[3]){
            $("#discountGift").css("display","inline");
            $("#extraPresent").css("display","inline");
            $(".gift_hidden").hide();
        }
    },
    activityType:function(){
        $("#activityType").unbind("change").bind({
            change:function(){
                marketingAdd.activityTypeShow($("#activityType").val());
            }
        });
    },
    notMoreThan:function(data){
        if (data.isAccumulate == '1') {
            $("#isAccumulateTrue").attr("checked", true);
            marketingAdd.notMoreThan("notMoreThan1");
            $("#notMoreThan").val(data.notMoreThan);
        } else {
            $("#isAccumulateFalse").attr("checked", true);
            $("#notMoreThan").hide();
        }
    },
    activityEdit:function(data){
        $("#activityType").val(data.activityType);
        marketingAdd.activityTypeShow(data.activityType);
        if(data.activityType == marketingAdd.activityTypeId[0]){//满减
            if (data.isAccumulate == '1') {
                $("#isAccumulateTrue").attr("checked", true);
                marketingAdd.notMoreThan("notMoreThan1");
                $("#notMoreThan1Parent").css("display","inline");
                $("#notMoreThan").val(data.notMoreThan);
            } else {
                $("#isAccumulateFalse").attr("checked", true);
                $("#notMoreThan").hide();
            }
            if(data.discountByMoneyList.length>0){
                marketingAdd.discountCount = data.discountByMoneyList.length;
                var options = "<li class='discount_repeat'><p>"+
                    "满<input type='text' class='discountByMoneyInput form-control text-input-150' name='discountByMoneyList[0].full' value='" + data.discountByMoneyList[0].full + "'  style='width: 100px' style='margin-top: 5px;'/>元" +
                    "减<input type='text' class='discountByMoneyInput form-control text-input-150' name='discountByMoneyList[0].discount'  value='" + data.discountByMoneyList[0].discount + "'  style='width: 100px' style=''margin-top: 5px;'/>元" +
                    "<span class='glyphicon glyphicon-plus add_del_span' id='addDiscount'></span></p></li>";
                for(var i = 1;i<data.discountByMoneyList.length;i++){
                    options += "<li class='discount_repeat'><p>"
                        + "         满<input type='text' class='discountByMoneyInput form-control text-input-150' value='" + data.discountByMoneyList[i].full + "'  name='discountByMoneyList[" + i + "].full'  style='width: 100px' style='margin-top: 5px;'/>元"
                        + "  减<input type='text' class='discountByMoneyInput form-control text-input-150' value='" + data.discountByMoneyList[i].discount + "'  name='discountByMoneyList[" + i + "].discount'   style='width: 100px' style='margin-top: 5px;'/>元"
                        + "<span class='glyphicon glyphicon-remove add_del_span'  onclick='this.parentNode.parentNode.remove()'></span></p></li>";
                }
                $('#discountUL').html(options);
            }
        }else if(data.activityType == marketingAdd.activityTypeId[1]){//满送
            if (data.isAccumulate == '1') {
                $("#isAccumulateTrue").attr("checked", true);
                marketingAdd.notMoreThan("notMoreThan2");
                $("#notMoreThan2Parent").css("display","inline");
                $("#notMoreThan").val(data.notMoreThan);
            } else {
                $("#isAccumulateFalse").attr("checked", true);
                $("#notMoreThan").hide();
            }
            if(data.presentList.length>0){
                marketingAdd.presentCount = data.presentList.length;
                $('#presentUL').html("");
                for(var i = 0;i<data.presentList.length;i++) {
                    var options = "<li class='discount_repeat'><p>" +
                        "满<input type='text' class='presentInput form-control text-input-150' value='" + data.presentList[i].full + "' name='presentList["+i+"].full' style='width: 100px' style='margin-top: 5px;'/>元" +
                        "送<input type='text' class='presentInput form-control text-input-150' value='" + data.presentList[i].discount + "' name='presentList["+i+"].discount'  style='width: 100px' style='margin-top: 5px;'/>元" +
                        "<select name='presentList["+i+"].present' id='choosePresent"+i+"' class='presentSelect form-control text-input-150'  style='margin-left:15px;margin-right:15px;margin-top: 5px;'>" +
                        "    <option value='0'>请选择礼物 </option></select>";
                    if(i == 0){
                        options += "<span class='glyphicon glyphicon-plus add_del_span' id='addPresent'></span></p></li>";
                    }else{
                        options += "<span class='glyphicon glyphicon-remove add_del_span'  onclick='this.parentNode.parentNode.remove()'></span></p></li>";
                    }
                    $('#presentUL').append(options);
                    marketingAdd.present("choosePresent"+i);
                    $("#choosePresent"+i).val(data.presentList[i].present);
                    checkboxPresent = "";
                }
            }
        }else if(data.activityType == marketingAdd.activityTypeId[2]){//减免险种
            var typeArr = data.discountByInsurance.insuranceType.split(',');
            for(var i=0;i<typeArr.length;i++){
                $("#insuranceTypeId" + typeArr[i]).attr("checked",true);
            }
            typeArr = data.discountByInsurance.notMoreThanInsuranceType.split(',');
            for(var i=0;i<typeArr.length;i++){
                $("#notMoreThanInsuranceTypeId" + typeArr[i]).attr("checked",true);
            }
            $("#discountInsuranceNotMoreThan").val(data.discountByInsurance.notMoreThan);
        }else if(data.activityType == marketingAdd.activityTypeId[3]){//折扣赠送
            if(data.discountGift.notMoreThanInsuranceTypeByGift){
                var typeArr = data.discountGift.notMoreThanInsuranceTypeByGift.split(',');
                for(var i=0;i<typeArr.length;i++){
                    $("#notMoreThanInsuranceTypeByGiftId" + typeArr[i]).attr("checked",true);
                }
            }
            $("#notMoreThanInsurancePerc").val(data.discountGift.notMoreThanPerc);
            $("#discountGiftInsuranceMust").val(data.discountGift.insuranceMust);
            $("#discountGiftCommercial").val(data.discountGift.commercial);
            $("#discountGiftVehicleTax").val(data.discountGift.vehicleTax);
            $("#notMoreThanMoney").val(data.discountGift.notMoreThanMoney);
        }
        if(data.extraPresentList){
            if(data.extraPresentList.length>0){
                marketingAdd.extraCount = data.extraPresentList.length;
                $('#extraPresentUL').html("");
                var checkboxPresent = "<input type='checkbox' name='activityTypeInfo' value='extraPresent' id='extraCheckbox' checked='checked'/>";
                for(var i = 0;i<data.extraPresentList.length;i++) {
                    var options = "<li class='discount_repeat'><p>" +
                        checkboxPresent +
                        "满<input type='text' class='extraInput  form-control text-input-150' value='" + data.extraPresentList[i].full + "' name='extraPresentList["+i+"].full' style='width: 100px' style='margin-top: 5px;'/>元" +
                        "送<input type='text' class='extraInput  form-control text-input-150' value='" + data.extraPresentList[i].discount + "' name='extraPresentList["+i+"].discount'  style='width: 100px' style='margin-top: 5px;'/>元" +
                        "<select name='extraPresentList["+i+"].present' id='chooseExtraPresent"+i+"' class='chooseExtraPresent  form-control text-input-150'  style='margin-left:15px;margin-right:15px;margin-top: 5px;'>" +
                        "    <option value='0'>请选择礼物 </option></select>";
                    if(i == 0){
                        options += "<span class='glyphicon glyphicon-plus add_del_span' id='addExtra'></span></p></li>";
                    }else{
                        options += "<span class='glyphicon glyphicon-remove add_del_span' onclick='this.parentNode.parentNode.remove()'></span></p></li>";
                    }
                    $('#extraPresentUL').append(options);
                    marketingAdd.extraPresent("chooseExtraPresent"+i);
                    $("#chooseExtraPresent"+i).val(data.extraPresentList[i].present);
                    checkboxPresent = "";
                }
            }
        }
        marketingAdd.initSelect();
    },
    channel:function(){
        $("#channelSelect").unbind("change").bind({
            change:function(){
                $("#channelDiv").html("");
                if($("#channelSelect").val() == "official"){
                    marketingAdd.getDescriptiongByUrl("channel","/operationcenter/resource/channel/official");
                }else if($("#channelSelect").val() == "thirdParty"){
                    marketingAdd.getDescriptiongByUrl("channel","/operationcenter/resource/channel/thirdParty");
                }else if($("#channelSelect").val() == "all"){
                    marketingAdd.getDescriptiongByUrl("channel","/operationcenter/resource/channel/all");
                }
            }
        });
    },

    getDescriptiongByUrl:function(name,url){
        common.getByAjax(false, "get", "json", url,{},
            function(data){
                if (data) {
                    var options = "";
                    $.each(data, function(i, model){
                        options += "<label class='checkbox-inline width-180'><input type='checkbox' name='" + name + "' value='" + model.id + "'  id='" + name + model.id + "'/>" + model.description + "</label>";
                    });
                    $("#" + name + "Div").append(options);
                }
            },
            function(){
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "53%", null);
            }
        );
    },

    insuranceCheckbox:function(idArr){
        common.getByAjax(false, "get", "json", "/operationcenter/marketingRule/insuranceTypeList",{},
            function(data){
                if (data) {
                    for(var i=0;i<idArr.length;i++){
                        var options = "";
                        if(idArr[i] == "insuranceType" || idArr[i] == "notMoreThanInsuranceType"){
                            $.each(data, function(j, model){
                                options += "<label class='checkbox-inline'><input type='checkbox' id='"+idArr[i]+"Id"+model.id+"' name='discountByInsurance." + idArr[i] + "' value='" + model.id + "'/>" + model.description + "</label>";
                            });
                            $("#" + idArr[i] + "Div").append(options);
                        }else if(idArr[i] == "notMoreThanInsuranceTypeByGift" ){

                            $.each(data, function(j, model){
                                options += "<label class='checkbox-inline'><input type='checkbox' id='"+idArr[i]+"Id"+model.id+"' name='discountGift." + idArr[i] + "' value='" + model.id + "'/>" + model.description + "</label>";
                            });
                            $("#" + idArr[i] + "Div").append(options);
                        }else{
                            $.each(data, function(j, model){
                                options += "<label class='checkbox-inline'><input type='checkbox' name='" + idArr[i] + "' value='" + model.id + "'  id='" + idArr[i] + model.id + "'/>" + model.description + "</label>";
                            });
                            $("#" + idArr[i] + "Div").append(options);
                        }
                    }
                }
            },
            function(){
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "53%", null);
            }
        );
    },

    companyCheckbox:function(){
        common.getByAjax(true, "get", "json", "/operationcenter/resource/insuranceCompanys",{},
            function(data){
                if (data) {
                    var options = "";
                    $.each(data, function(i, model){
                        options += "<label class='checkbox-inline width-180'><input type='checkbox' name='insuranceCompany' value='" + model.id + "'/>" + model.name + "</label>";
                    });
                    $("#insuranceCompanyDiv").append(options);
                }
            },
            function(){
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "53%", null);
            }
        );
    },
    isAccumulate:function(name){
        var str="是否累计  <label class='radio-inline'><input type='radio' name='isAccumulate' id='isAccumulateTrue' value='1' onchange=' marketingAdd.checkAccumulate()' />是</label>"
            + "<label class='radio-inline'><input type='radio' name='isAccumulate' value='0'id='isAccumulateFalse'  checked=‘checked' onchange='marketingAdd.checkNotAccumulate()' />否</label>";
        $("#" + name).html(str);
    },

    checkAccumulate:function(){
        if ($("#activityType").val() == "4"){//满减
            if($('#discountUL').children().length >1){
                popup.mould.popTipsMould("累计时只能有一条满减", popup.mould.first, popup.mould.error, "", "53%", null);
                marketingAdd.isAccumulate("isAccumulate1");
                return false;
            }else{
                $("#notMoreThan1Parent").css("display","inline");
                marketingAdd.notMoreThan("notMoreThan1");
            }
        }else if($("#activityType").val() == "5"){
            if($('#presentUL').children().length >1){
                popup.mould.popTipsMould("累计时只能有一条满送", popup.mould.first, popup.mould.error, "", "53%", null);
                marketingAdd.isAccumulate("isAccumulate2");
                return false;
            }else{
                $("#notMoreThan2Parent").css("display","inline");
                marketingAdd.notMoreThan("notMoreThan2");
            }
        }
        return true;
    },
    checkNotAccumulate:function(){
        if ($("#activityType").val() == "4"){//满减
            $("#notMoreThan1Parent").css("display","none");
            $("#notMoreThan1").html("");
        }else if($("#activityType").val() == "5"){
            $("#notMoreThan2Parent").css("display","none");
            $("#notMoreThan2").html("");
        }
    },
    notMoreThan:function(name){
        var str = "<input type='text' class='form-control text-input-150' name='notMoreThan' id='notMoreThan'  style='width: 100px' style='margin-top: 5px;'/>";
        $("#" + name).append(str);
    },

    checkCheckBox:function(checkBoxName){
        var result = false;
        $("input[name='" + checkBoxName + "']").each(function(){		//type=checkbox实行便利循环
            if(this.checked == true){
                result = true;
            }				//删除type=checkbox里面添加checked="checked" 取消选中的意思
        });
        return result;
    },
    checkInput:function(name) {
        var result = true;
        $("." + name).each(function(){
            if( !common.isNumber(this.value)){
                result = false;
            }
        });
        return result;
    },
    checkSelect:function(name) {
        var result = true;
        $("." + name).each(function(){
            if(this.value == "0"){
                result = false;
            }
        });
        return result;
    },
    wechatyes:function(){
        $("#weixinShareTr").show();
        $("#sharedPicTr").show();
    },
    wechatno:function(){
        $("#weixinShareTr").hide();
        if($("#alipayno").prop('checked') ==  true){
            $("#sharedPicTr").hide();
        }
    },
    alipayyes:function(){
        $("#alipayShareTr").show();
        $("#sharedPicTr").show();
    },
    alipayno:function(){
        $("#alipayShareTr").hide();
        if($("#wechatno").prop('checked') ==  true){
            $("#sharedPicTr").hide();
        }
    },

    validateAdd:function() {
        if ($("#activityType").val() == '0') {
            popup.mould.popTipsMould( "请选择优惠类别", popup.mould.first, popup.mould.error, "", "57%", null);
            return false;
        } else if ($("#activityType").val() == "4") {//满减
            //满减输入框要填全
            if(!marketingAdd.checkInput('discountByMoneyInput')){
                popup.mould.popTipsMould( "请填全满减信息", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
            if($("#isAccumulateTrue").prop('checked') == true) {
                if($('#notMoreThan').val() == ""){
                    popup.mould.popTipsMould( "最高减免钱数未填", popup.mould.first, popup.mould.error, "", "57%", null);
                    return false;
                }
            }
        } else if ($("#activityType").val() == "6") {//保险优惠
            if(!marketingAdd.checkCheckBox('discountByInsurance.insuranceType')){
                popup.mould.popTipsMould( "减免信息不全", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
            if(!marketingAdd.checkCheckBox('discountByInsurance.notMoreThanInsuranceType')){
                popup.mould.popTipsMould( "减免信息不全", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
            if($("#discountInsuranceNotMoreThan").val() == ""){
                popup.mould.popTipsMould( "最高不超过百分比", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
        } else if ($("#activityType").val() == "5") {//送礼
            if(!marketingAdd.checkInput('presentInput')){
                popup.mould.popTipsMould( "请填全满送信息", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
            if(!marketingAdd.checkSelect('presentSelect')){
                popup.mould.popTipsMould( "请填全满送信息", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
            if($("#isAccumulateTrue").prop('checked') == true) {
                if($('#notMoreThan').val() == ""){
                    popup.mould.popTipsMould( "请填全优惠信息", popup.mould.first, popup.mould.error, "", "57%", null);
                    return false;
                }
            }
        } else if ($("#activityType").val() == "7") {//折扣赠送
            if(common.isEmpty($("#discountGiftInsuranceMust").val()) && common.isEmpty($("#discountGiftCommercial").val()) && common.isEmpty($("#discountGiftVehicleTax").val())){
                popup.mould.popTipsMould( "请填满送信息", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
        }
        if ($("#extraCheckbox").prop('checked') == true) {//其他
            if(!marketingAdd.checkInput('extraInput')){
                popup.mould.popTipsMould( "请填全减免险种信息", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
            if(!marketingAdd.checkSelect('extraSelect')){
                popup.mould.popTipsMould( "请填全减免险种信息", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
        }
        if (common.isEmpty($("#title").val())) {
            popup.mould.popTipsMould( "请输入标题", popup.mould.first, popup.mould.error, "", "57%", null);
            return false;
        }
        if($("#title").val().length>14){
            popup.mould.popTipsMould( "标题过长", popup.mould.first, popup.mould.error, "", "57%", null);
            return false;
        }
        if (common.isEmpty($("#sub_title").val())) {
            popup.mould.popTipsMould( "请输入副标题", popup.mould.first, popup.mould.error, "", "57%", null);
            return false;
        }
        if($("#sub_title").val().length>16){
            popup.mould.popTipsMould( "副标题过长", popup.mould.first, popup.mould.error, "", "57%", null);
            return false;
        }
        if (common.isEmpty($("#description").val())) {
            popup.mould.popTipsMould( "请输入政策", popup.mould.first, popup.mould.error, "", "57%", null);
            return false;
        }
        if (common.isEmpty($("#userGuide").val())) {
            popup.mould.popTipsMould( "请输入使用规则", popup.mould.first, popup.mould.error, "", "57%", null);
            return false;
        }
        if ($("#wechatyes").prop('checked') == true) {
            if (common.isEmpty($("#wechatMainTitle").val())) {
                popup.mould.popTipsMould( "请输入微信主标题", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
            if($("#wechatMainTitle").val().length>15){
                popup.mould.popTipsMould( "微信主标题过长", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
            if (common.isEmpty($("#wechatSubTitle").val())) {
                popup.mould.popTipsMould( "请输入微信副标题", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
            if($("#wechatSubTitle").val().length>50){
                popup.mould.popTipsMould( "微信副标题过长", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
            if (common.isEmpty($("#sharedIconPic").val())) {
                popup.mould.popTipsMould( "上传分享图片", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
        }
        if ($("#alipayyes").prop('checked') == true) {
            if (common.isEmpty($("#alipayMainTitle").val())) {
                popup.mould.popTipsMould( "请输入支付宝主标题", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
            if($("#alipayMainTitle").val().length>15){
                popup.mould.popTipsMould( "支付宝主标题过长", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
            if (common.isEmpty($("#alipaySubTitle").val())) {
                popup.mould.popTipsMould( "请输入支付宝副标题", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
            if($("#alipaySubTitle").val().length>50){
                popup.mould.popTipsMould( "支付宝副标题过长", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
            if (common.isEmpty($("#sharedIconPic").val())) {
                popup.mould.popTipsMould( "上传分享图片", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
        }
        if(common.isEmpty($("#activityPic").val())){
            popup.mould.popTipsMould( "上传活动图片", popup.mould.first, popup.mould.error, "", "57%", null);
            return false;
        }
        if ($("#activityType").val() != "7") {//折扣赠送
            if (!marketingAdd.checkCheckBox('insuranceMust')) {
                popup.mould.popTipsMould("需购买哪几种险种才能享受优惠", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }
            if (!marketingAdd.checkCheckBox('fullIncludes')) {
                popup.mould.popTipsMould("满额包含的险种", popup.mould.first, popup.mould.error, "", "57%", null);
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }

    },
    validateCompanyAreaChannel:function(){
        if (!marketingAdd.checkCheckBox('insuranceCompany')) {
            popup.mould.popTipsMould( "请选择保险公司", popup.mould.first, popup.mould.error, "", "57%", null);
            return false;
        }
        if (!marketingAdd.checkCheckBox('channel')) {
            popup.mould.popTipsMould( "请选择活动平台", popup.mould.first, popup.mould.error, "", "57%", null);
            return false;
        }
        if ($("[class='tagator_tag']").length <= 0) {
            popup.mould.popTipsMould( "没有城市", popup.mould.first, popup.mould.error, "", "57%", null);
            return false;
        } else {
            return true;
        }
    },
}

$(function(){
    var id = common.getUrlParam("id");
    var clickType = common.getUrlParam("clickType");
    if(id == null){
        marketingAdd.add();
    }else if(clickType == 'edit'){
        marketingAdd.edit(id);
    }else{
        marketingAdd.overview(id);
    }
});
