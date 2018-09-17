/**
 * Created by wangfei on 2015/4/29.
 */
$(function(){

    var validOptions_new = {
        onkeyup: false,
        onfocusout: false,
        rules: {
            licensePlateNo: {
                required: true
            },
            //identity: {
            //    required: true
            //},
            insuranceCompany: {
                required: true
            },
            originalPremium: {
                required: true,
                number: true
            }
        },
        messages: {
            licensePlateNo: {
                required: "请输入车牌号"
            },
            //identity: {
            //    required: "请输入车主身份证号"
            //},
            insuranceCompany: {
                required: "请选择保险公司"
            },
            originalPremium: {
                required: "请输入保险原始总金额",
                number: "请输入正确的保险原始总金额"
            }
        },
        showErrors: function(errorMap, errorList) {
            if (errorList.length) {
                showErrorMsg(errorList[0].message);
            }
        },
        submitHandler: function(form) {
            $(".premium").each(function(){
                if(common.isEmpty($(this).val())){
                    $(this).val("0.00");
                }
            });
            if (!personalValidation.valid()) {
                return ;
            }
            orderInsurance.saveInsurance(form);
        }
    };

    $("#insuranceForm").validate(validOptions_new);

    var id = common.getUrlParam("id");
    var defs = []
    /* 保险公司 */
    defs.push(common.getByAjax(true, "get", "json", "/orderCenter/resource/insuranceCompany/getEnableCompanies", null,
        function(data){
            if(data == null){
                return false;
            }
            var options = "";
            $.each(data, function(i,model){
                options += "<option value='"+ model.id +"'>" + model.name + "</option>";
            });
            $("#insuranceCompany").append(options);
        },function(){}
    ));

    /* 投保人证件类型 */
    defs.push(common.getByAjax(false, "get", "json", "/orderCenter/resource/identityTypes",{},
        function(data){
            if (data) {
                var options = "";
                $.each(data, function(i, model){
                    options += "<option value='"+model.id+"'>" + model.description + "</option>";
                });
                $("#identityType").append(options);
                $("#applicantIdType").append(options);
            }
        },
        function(){}
    ));

    if(id != null){
        $("#purchaseOrderId").val(id);
        common.getByAjax(true,"get","json","/orderCenter/insurance/init",{id :id},
            function(data){
                if(!data){
                    showErrorMsg("获取订单数据失败！");
                    return false;
                }
                common.tools.setSelectReadonly($("#identityType"));
                $("#identity").attr("readonly","readonly");
                $("#licensePlateNo").attr("readonly","readonly");

                var options = "";
                for (var index = 0; index < 25; index++) {
                    options += "<option value='" + index + "'>" + index + "</option>"
                }
                $("#commercialEffectiveHour").html(options);
                $("#commercialExpireHour").html(options);

                $("#commercialEffectiveHour option:first").prop("selected", "selected");
                $("#commercialExpireHour option:last").prop("selected", "selected");
                $("#compulsoryEffectiveHour").html(options);
                $("#compulsoryExpireHour").html(options);
                $("#compulsoryEffectiveHour option:first").prop("selected", "selected");
                $("#compulsoryExpireHour option:last").prop("selected", "selected");

                $.when.apply(this, defs).done(function(){
                    writeHtmlData(data);
                    initInstitution(data.insuranceCompany,data.institution);
                });

                //if(data.insuranceCompany == 65000){
                //    $("select").attr("readonly","readonly");
                //    $("input:not(.express)").attr("readonly","readonly");
                //}

            },function(){showErrorMsg("获取数据异常");}
        )
    }




    $("#myTab a").click(function(e) {
        e.preventDefault();
        $(this).tab("show");
        var href = $(this).attr("href").replace("#", "");
        if ("commercial" == href) {
            $("#commercial").show();
            $("#compulsory").hide();
        } else {
            $("#commercial").hide();
            $("#compulsory").show();
        }
    });

    $("#insuranceCompany").bind({
        change:function(){
            initInstitution($(this).val());
        }
    });
    $("#institution").bind({
        change:function(){
            $("#commercialRebate").val($(this).find("option:selected").attr("commercialRebate"));
            $("#compulsoryRebate").val($(this).find("option:selected").attr("compulsoryRebate"));
        }
    });

    /* 保单失效日期联动 */
    $("#commercialEffectiveDate").bind({
        focus:function(){
            var commercialDate = $("#commercialEffectiveDate").val();
            if(!commercialDate){
                $("#commercialExpireDate").val("");
            }else{
                var endDate=common.tools.addDays(commercialDate,364)
                $("#commercialExpireDate").val(endDate);
            }
        }
    });
    $("#compulsoryEffectiveDate").bind({
        focus:function(){
            var compulsoryDate = $("#compulsoryEffectiveDate").val();
            if(!compulsoryDate){
                $("#compulsoryExpireDate").val("");
            }else{
                var endDate=common.tools.addDays(compulsoryDate,364)
                $("#compulsoryExpireDate").val(endDate);
            }
        }
    });
});


var orderInsurance = {
    saveInsurance: function(form) {
        $("#saveButton").attr("disabled", true);
        common.getByAjax(true,"get","json","/orderCenter/insurance/checkPremium",
            {
                id : $("#purchaseOrderId").val(),
                premium : $('#commercialPremium').val(),
                compulsoryPremium : $('#compulsoryPremium').val(),
                autoTax : $("#autoTax").val()
            },
            function(data){
                if(!data.pass){
                    $("#saveButton").attr("disabled", false);
                    popup.mould.popConfirmMould(true, "您当前录入的总保费与原订单的总保费不一致，是否覆盖原订单？", "first", false,  "56%",
                        function() {
                            orderInsurance.saveInsuranceData(form);
                            popup.mask.hideFirstMask(true);
                        },
                        function() {
                            popup.mask.hideFirstMask(true);
                            return;
                        }
                    );
                }else{
                    orderInsurance.saveInsuranceData(form);
                }

            },function() {
                showErrorMsg("系统异常");
                $("#saveButton").attr("disabled", false);
            });
    },
    saveInsuranceData: function(form){
        common.getByAjax(true, "get", "json", "/orderCenter/insurance/save", $(form).serialize(),
            function (data) {
                if (data.pass) {
                    popup.mould.popTipsMould(true, "添加保单成功", "first", "success", "", "56%",
                        function() {
                            popup.mask.hideFirstMask(true);
                            window.close();
                        }
                    );
                } else {
                    showErrorMsg(data.message);
                }
            }, function () {
                popup.mould.popTipsMould(true, "系统异常", "first", "error", "", "56%",
                    function() {
                        popup.mask.hideFirstMask(true);
                    }
                );
            }
        );
    }
};

//错误提示框
var showErrorMsg = function(msg){
    popup.mould.popTipsMould(true, msg, "first", "warning", "", "",
        function() {
            popup.mask.hideFirstMask(true);
        }
    );
};
popup.insertHtml("#popupHtml");

var personalValidation = {
    valid: function() {
        return this.validation.validLicenseNo()     //验证车牌号
            && this.validation.validIdentity()      //验证身份证号
            && this.validation.validOthers()        //验证商业险和交强险至少选择一种
            && this.validation.validateCommercial() //验证商业险保单
            && this.validation.validateCompulsory()//验证交强险保单
            && this.validation.validateApplicant();
    },
    validation: {
        validLicenseNo: function() {
            if (!common.validateLicenseNo($("#licensePlateNo").val())) {
                showErrorMsg("车牌号填写错误");
                return false;
            }

            return true;
        },
        validIdentity: function() {
            return true;
            //common.validIdentity(("#applicantIdType").val(), ("#applicantIdType").val());
        },
        validOthers: function() {
            var _commercialPremium = $("#commercialPremium").val();//商业险总保费
            var _compulsoryPremium = $("#compulsoryPremium").val();//交强险保费
            var institution=$("#institution").val();
            if (_commercialPremium <= 0 && _compulsoryPremium <= 0) {
                showErrorMsg("商业险和交强险请至少选择一种进行录入");
                return false;
            }
            //if(common.isEmpty(institution)){
            //    showErrorMsg("请选择出单机构");
            //    return false;
            //}
            return true;
        },
        /* 验证商业险表单 */
        validateCommercial: function(){
            var flag = true;
            var error_text="";
            //是否投保
            var isBuying = false;
            /*不计免赔总额*/
            var iop = $("#iop").val();
            if(!common.isEmpty(iop) && !common.isMoney(iop)) {
                error_text = "不计免赔总额格式错误";
                flag = false;
            }
            /*商业险浮动系数*/
            var discount = $("#discount").val();
            if(!common.isEmpty(discount)&&(discount<0||discount>100||!common.isNumber(discount))) {
                error_text = "商业险浮动系数格式错误";
                flag = false;
            }
            /* 机动车辆损失险 */
            var _amount = $("#damageAmount").val();//保额
            var _premium = $("#damagePremium").val();//保费
            var _iop = $("#damageIop").val();//不计免赔

            if(_iop != 0){
                isBuying = true;
                if(_amount==0){
                    error_text="请填入机动车辆损失险的保额";
                    flag = false;
                }else if(_premium==0){
                    error_text="请填入机动车辆损失险的保费";
                    flag = false;
                }
            }else{
                if(_premium==0){
                    if(_amount!=0){
                        error_text="请填入机动车辆损失险的保费";
                        flag = false;
                    }
                }else{
                    if(_amount==0){
                        error_text="请填入机动车辆损失险的保额";
                        flag = false;
                    }else{
                        isBuying = true;
                    }
                }
            }
            if(flag){
                if(!common.isEmpty(_amount) && !common.isMoney(_amount)){
                    error_text="机动车辆损失险的保额格式错误";
                    flag = false;
                }else if(!common.isEmpty(_premium) && !common.isMoney(_premium)){
                    error_text="机动车辆损失险的保费格式错误";
                    flag = false;
                }else if(!common.isEmpty(_iop) && !common.isMoney(_iop)){
                    error_text="机动车辆损失险的不计免赔格式错误";
                    flag = false;
                }
            }
            /* 第三者责任险 */
            if(flag){
                _amount = $("#thirdPartyAmount").val();//保额
                _premium = $("#thirdPartyPremium").val();//保费
                _iop = $("#thirdPartyIop").val();//不计免赔

                if(_iop != 0){
                    isBuying = true;
                    if(_amount==0){
                        error_text="请选择第三者责任险的保额";
                        flag = false;
                    }else if(_premium==0){
                        error_text="请输入第三者责任险的保费";
                        flag = false;
                    }
                }else{
                    if(_premium==0){
                        if(_amount!=0){
                            error_text="请输入第三者责任险的保费";
                            flag = false;
                        }
                    }else{
                        if(_amount==0){
                            error_text="请选择第三者责任险的保额";
                            flag = false;
                        }else{
                            isBuying = true;
                        }
                    }
                }
                if(flag){
                    if(!common.isEmpty(_premium) && !common.isMoney(_premium)){
                        error_text="第三者责任险的保费格式错误";
                        flag = false;
                    }else if(!common.isEmpty(_iop) && !common.isMoney(_iop)){
                        error_text="第三者责任险的不计免赔格式错误";
                        flag = false;
                    }
                }
            }

            /* 车上人员责任险(司机) */
            if(flag){
                _amount = $("#driverAmountSel").val();//保额
                _premium = $("#driverPremium").val();//保费
                _iop = $("#driverIop").val();//不计免赔
                if(_iop != 0){
                    isBuying = true;
                    if(_amount==0){
                        error_text="请选择车上人员责任险(司机)的保额";
                        flag = false;
                    }else if(_premium==0){
                        error_text="请输入车上人员责任险(司机)的保费";
                        flag = false;
                    }
                }else{
                    if(_premium==0){
                        if(_amount!=0){
                            error_text="请输入车上人员责任险(司机)的保费";
                            flag = false;
                        }
                    }else{
                        if(_amount==0){
                            error_text="请选择车上人员责任险(司机)的保额";
                            flag = false;
                        }else{
                            isBuying = true;
                        }
                    }
                }
                if(flag){
                    if(!common.isEmpty(_premium) && !common.isMoney(_premium)){
                        error_text="车上人员责任险(司机)的保费格式错误";
                        flag = false;
                    }else if(!common.isEmpty(_iop) && !common.isMoney(_iop)){
                        error_text="车上人员责任险(司机)的不计免赔格式错误";
                        flag = false;
                    }
                }
            }

            /* 车上人员责任险(乘客) */
            if(flag){
                _amount = $("#passengerAmountSel").val();//保额
                _premium = $("#passengerPremium").val();//保费
                _iop = $("#passengerIop").val();//不计免赔

                if(_iop != 0){
                    isBuying = true;
                    if(_amount==0){
                        error_text="请选择车上人员责任险(乘客)的保额";
                        flag = false;
                    }else if(_premium==0){
                        error_text="请输入车上人员责任险(乘客)的保费";
                        flag = false;
                    }
                }else{
                    if(_premium==0){
                        if(_amount!=0){
                            error_text="请输入车上人员责任险(乘客)的保费";
                            flag = false;
                        }
                    }else{
                        if(_amount==0){
                            error_text="请选择车上人员责任险(乘客)的保额";
                            flag = false;
                        }else{
                            isBuying = true;
                        }
                    }
                }
                if(flag){
                    if(!common.isEmpty(_premium) && !common.isMoney(_premium)){
                        error_text="车上人员责任险(乘客)的保费格式错误";
                        flag = false;
                    }else if(!common.isEmpty(_iop) && !common.isMoney(_iop)){
                        error_text="车上人员责任险(乘客)的不计免赔格式错误";
                        flag = false;
                    }
                }
            }

            /* 盗抢险 */
            if(flag){
                _amount = $("#theftAmount").val();//保额
                _premium = $("#theftPremium").val();//保费
                _iop = $("#theftIop").val();//不计免赔

                if(_iop != 0){
                    isBuying = true;
                    if(_amount==0){
                        error_text="请输入盗抢险的保额";
                        flag = false;
                    }else if(_premium==0){
                        error_text="请输入盗抢险的保费";
                        flag = false;
                    }
                }else{
                    if(_premium==0){
                        if(_amount!=0){
                            error_text="请输入盗抢险的保费";
                            flag = false;
                        }
                    }else{
                        if(_amount==0){
                            error_text="请输入盗抢险的保额";
                            flag = false;
                        }else{
                            isBuying = true;
                        }
                    }
                }
                if(flag){
                    if(!common.isEmpty(_amount) && !common.isMoney(_amount)){
                        error_text="盗抢险的保额格式错误";
                        flag = false;
                    }else if(!common.isEmpty(_premium) && !common.isMoney(_premium)){
                        error_text="盗抢险的保费格式错误";
                        flag = false;
                    }else if(!common.isEmpty(_iop) && !common.isMoney(_iop)){
                        error_text="盗抢险的不计免赔格式错误";
                        flag = false;
                    }
                }
            }

            /* 车身划痕损失险 */
            if(flag){
                _amount = $("#scratchAmountSel").val();//保额
                _premium = $("#scratchPremium").val();//保费
                _iop = $("#scratchIop").val();//不计免赔

                if(_iop != 0){
                    isBuying = true;
                    if(_amount==0){
                        error_text="请选择车身划痕损失险的保额";
                        flag = false;
                    }else if(_premium==0){
                        error_text="请输入车身划痕损失险的保费";
                        flag = false;
                    }
                }else{
                    if(_premium==0){
                        if(_amount!=0){
                            error_text="请输入车身划痕损失险的保费";
                            flag = false;
                        }
                    }else{
                        if(_amount==0){
                            error_text="请选择车身划痕损失险的保额";
                            flag = false;
                        }else{
                            isBuying = true;
                        }
                    }
                }
                if(flag){
                    if(!common.isEmpty(_premium) && !common.isMoney(_premium)){
                        error_text="车身划痕损失险的保费格式错误";
                        flag = false;
                    }else if(!common.isEmpty(_iop) && !common.isMoney(_iop)){
                        error_text="车身划痕损失险的不计免赔格式错误";
                        flag = false;
                    }
                }
            }

            /* 自燃损失险 */
            if(flag){
                _amount = $("#spontaneousLossAmount").val();//保额
                _premium = $("#spontaneousLossPremium").val();//保费

                if(_premium==0){
                    if(_amount!=0){
                        error_text="请输入自燃损失险的保费";
                        flag = false;
                    }
                }else{
                    if(_amount==0){
                        error_text="请输入自燃损失险的保额";
                        flag = false;
                    }else{
                        isBuying = true;
                    }
                }
                if(flag){
                    if(!common.isEmpty(_amount) && !common.isMoney(_amount)){
                        error_text="自燃损失险的保额格式错误";
                        flag = false;
                    }else if(!common.isEmpty(_premium) && !common.isMoney(_premium)){
                        error_text="自燃损失险的保费格式错误";
                        flag = false;
                    }
                }
            }

            /* 玻璃单独破碎险 */
            if(flag){
                var _glassTypeSel = $("#glassTypeSel").val();//类型
                _premium = $("#glassPremium").val();//保费

                if(_premium==0){
                    if(_glassTypeSel!=0){
                        error_text="请选择玻璃单独破碎险的保费";
                        flag = false;
                    }
                }else{
                    if(_glassTypeSel==0){
                        error_text="请输入玻璃单独破碎险的类型";
                        flag = false;
                    }else{
                        isBuying = true;
                    }
                }
                if(flag){
                    if(!common.isEmpty(_premium) && !common.isMoney(_premium)){
                        error_text="玻璃单独破碎险的保费格式错误";
                        flag = false;
                    }
                }
            }

            /* 发动机特别损失险 */
            if(flag){
                _premium = $("#enginePremium").val();//保费
                _iop = $("#engineIop").val();//不计免赔

                if(_iop!=0){
                    isBuying = true;
                    if(_premium==0){
                        error_text="请输入发动机特别损失险的保费";
                        flag = false;
                    }
                }
                if(flag){
                    if(!common.isEmpty(_premium) && !common.isMoney(_premium)){
                        error_text="发动机特别损失险的保费格式错误";
                        flag = false;
                    }else if(!common.isEmpty(_iop) && !common.isMoney(_iop)){
                        error_text="发动机特别损失险的不计免赔格式错误";
                        flag = false;
                    }
                }
            }

            /* 验证商业险基本信息 */
            if(flag && isBuying){
                var _commercialPolicyNo = $("#commercialPolicyNo").val();//保单号
                var _commercialPremium = $("#commercialPremium").val();//商业险总保费
                var _commercialEffectiveDate = $("#commercialEffectiveDate").val();//保险生效日期
                var _commercialExpireDate = $("#commercialExpireDate").val();//保险失效日期
                if(!common.isMoney(_commercialPremium)){
                    error_text="商业险总保费格式错误";
                    flag = false;
                }else if(common.isEmpty(_commercialPolicyNo)){
                    error_text="请输入商业险保单号";
                    flag = false;
                }else if(common.isEmpty(_commercialPremium)){
                    error_text="请输入商业险总保费";
                    flag = false;
                }else if(!_commercialEffectiveDate){
                    error_text="请输入商业险的生效日期";
                    flag = false;
                }else if(!_commercialExpireDate){
                    error_text="请输入商业险的失效日期";
                    flag = false;
                }
            }

            if(!flag){
                showErrorMsg(error_text);
            }

            return flag;
        },
        /* 验证交强险表单 */
        validateCompulsory: function() {
            var flag = true;
            var error_text="";
            /*交强险险浮动系数*/
            var discountCI = $("#discountCI").val();
            if(!common.isEmpty(discountCI)&&(discountCI<0||discountCI>100||!common.isNumber(discountCI))) {
                error_text = "交强险浮动系数格式错误";
                flag = false;
            }
            //是否投保
            var isBuying = false;
            var _compulsoryPremium = $("#compulsoryPremium").val();//交强险保费
            var _autoTax = $("#autoTax").val();//车船税
            if(!common.isMoney(_compulsoryPremium)){
                error_text="交强险总保费格式错误";
                flag = false;
            }
            if(!common.isMoney(_autoTax)){
                error_text="车船税格式错误";
                flag = false;
            }

            if(_autoTax!=0){
                isBuying=true;
                if(_compulsoryPremium==0){
                    error_text="请填入交强险的车船税";
                    flag = false;
                }
            }else{
                if(_compulsoryPremium!=0){
                    isBuying=true;
                }
            }

            /* 验证交强险基本信息 */
            if(flag && isBuying){
                var _compulsoryPolicyNo = $("#compulsoryPolicyNo").val();//保单号
                var _compulsoryEffectiveDate = $("#compulsoryEffectiveDate").val();//保险生效日期
                var _compulsoryExpireDate = $("#compulsoryExpireDate").val();//保险失效日期
                if(common.isEmpty(_compulsoryPolicyNo)){
                    error_text="请输入交强险保单号";
                    flag = false;
                }else if(!_compulsoryEffectiveDate){
                    error_text="请输入交强险的生效日期";
                    flag = false;
                }else if(!_compulsoryExpireDate){
                    error_text="请输入交强险的失效日期";
                    flag = false;
                }
            }

            if(!flag){
                showErrorMsg(error_text);
            }

            return flag;
        },
        /* 验证投保人是否填写 */
        validateApplicant:function(){
            var flag=true;
            var error_text='';
            if(common.isEmpty($("#applicantName").val())){
                error_text="请输入投保人姓名";
                flag = false;
            }
            //if(common.isEmpty($("#applicantIdNo").val())){
            //    error_text="请输入投保人身份证号";
            //    flag = false;
            //}
            if(!flag){
                showErrorMsg(error_text);
            }
            return flag;
        }
    }
};


function writeHtmlData(_details){
    $("#saveButton").attr("disabled", true);
    $("#identity").val(common.checkToEmpty(_details.identity));
    $("#identityType").val(common.checkToEmpty(_details.identityType.id));
    $("#licensePlateNo").val(common.checkToEmpty(_details.licensePlateNo));
    $("#insuranceCompany").val(common.checkToEmpty(_details.insuranceCompany));
    $("#confirmOrderDate").val(common.checkToEmpty(_details.confirmOrderDate));
    $("#commercialRebate").val(common.checkToEmpty(_details.insurancePurchaseOrderRebateViewModel!=null?_details.insurancePurchaseOrderRebateViewModel.downCommercialRebate:""));
    $("#compulsoryRebate").val(common.checkToEmpty(_details.insurancePurchaseOrderRebateViewModel!=null?_details.insurancePurchaseOrderRebateViewModel.downCompulsoryRebate:""));
    // 商业险
    $("#commercialPolicyNo").val(common.checkToEmpty(_details.commercialPolicyNo));
    $("#commercialEffectiveDate").val(common.checkToEmpty(_details.commercialEffectiveDate));
    $("#commercialExpireDate").val(common.checkToEmpty(_details.commercialExpireDate));
    $("#commercialEffectiveHour").val(_details.commercialEffectiveHour == null? 0 : _details.commercialEffectiveHour);
    $("#commercialExpireHour").val(_details.commercialExpireHour == null? 24: _details.commercialExpireHour);
    $("#commercialPremium").val(common.formatMoney(_details.commercialPremium,2));
    $("#thirdPartyPremium").val(common.formatMoney(_details.thirdPartyPremium,2));
    $("#thirdPartyAmount").val(common.isEmpty(_details.thirdPartyAmount)?"0":_details.thirdPartyAmount);
    $("#damagePremium").val(common.formatMoney(_details.damagePremium,2));
    $("#damageAmount").val(common.formatMoney(_details.damageAmount,2));
    $("#theftPremium").val(common.formatMoney(_details.theftPremium,2));
    $("#theftAmount").val(common.formatMoney(_details.theftAmount,2));
    $("#enginePremium").val(common.formatMoney(_details.enginePremium,2));
    $("#engineAmount").val(common.formatMoney(_details.engineAmount,2));
    $("#driverPremium").val(common.formatMoney(_details.driverPremium,2));
    $("#driverAmountSel").val(common.isEmpty(_details.driverAmount)?"0":_details.driverAmount);
    $("#passengerPremium").val(common.formatMoney(_details.passengerPremium,2));
    $("#passengerAmountSel").val(common.isEmpty(_details.passengerAmount)?"0":_details.passengerAmount);
    $("#passengerCount").val(_details.passengerCount == null ? 0 : _details.passengerCount);
    $("#spontaneousLossPremium").val(common.formatMoney(_details.spontaneousLossPremium,2));
    $("#spontaneousLossAmount").val(common.formatMoney(_details.spontaneousLossAmount,2));
    $("#glassPremium").val(common.formatMoney(_details.glassPremium,2));
    $("#glassTypeSel").val(common.formatMoney(_details.glassPremium,2) == 0.00?
        0 : (_details.glassType == null ? 0 : _details.glassType));
    $("#scratchAmountSel").val(common.isEmpty(_details.scratchAmount)?"0":_details.scratchAmount);
    $("#scratchPremium").val(common.formatMoney(_details.scratchPremium,2));
    $("#unableFindThirdPartyPremium").val(common.formatMoney(_details.unableFindThirdPartyPremium,2));
    $("#designatedRepairShopPremium").val(common.formatMoney(_details.designatedRepairShopPremium,2));
    $("#damageIop").val(common.formatMoney(_details.damageIop,2));
    $("#thirdPartyIop").val(common.formatMoney(_details.thirdPartyIop,2));
    $("#theftIop").val(common.formatMoney(_details.theftIop,2));
    $("#engineIop").val(common.formatMoney(_details.engineIop,2));
    $("#driverIop").val(common.formatMoney(_details.driverIop,2));
    $("#passengerIop").val(common.formatMoney(_details.passengerIop,2));
    $("#scratchIop").val(common.formatMoney(_details.scratchIop,2));
    $("#spontaneousLossIop").val(common.formatMoney(_details.spontaneousLossIop,2));
    $("#iop").val(common.formatMoney(_details.iop,2));
    $("#discount").val(common.formatMoney(_details.discount,2));
    if(!common.isEmpty(_details.insuranceImage)){
        $("#commercialDiv").removeClass("unvisable-hidden")
        $("#insuranceImage").attr("src", _details.insuranceImage.endsWith("pdf") ? "../../images/pdf.jpg" : _details.insuranceImage);
        $("#insuranceImage").attr("url", _details.insuranceImage);
        $("#insuranceImage").show();
        $("#insuranceImage").next().show();
        $("#inputInsuranceImage").val(_details.insuranceImage);

        var filename = _details.insuranceImage.substring(_details.insuranceImage.lastIndexOf("/") + 1);
        $("#downloadCommercial").attr("href", _details.insuranceImage);
        $("#downloadCommercial").attr("download", filename);
    }
    $("#trackingNo").val(common.checkToEmpty(_details.trackingNo));
    $("#expressCompany").val(common.checkToEmpty(_details.expressCompany));

    // 交强险
    $("#compulsoryPolicyNo").val(common.checkToEmpty(_details.compulsoryPolicyNo));
    $("#compulsoryEffectiveDate").val(_details.compulsoryEffectiveDate);
    $("#compulsoryExpireDate").val(_details.compulsoryExpireDate);
    $("#compulsoryEffectiveHour").val(_details.compulsoryEffectiveHour == null? 0 : _details.compulsoryEffectiveHour);
    $("#compulsoryExpireHour").val(_details.compulsoryExpireHour == null? 24: _details.compulsoryExpireHour);
    $("#compulsoryPremium").val(common.formatMoney(_details.compulsoryPremium,2));
    $("#autoTax").val(common.formatMoney(_details.autoTax,2));
    $("#discountCI").val(common.formatMoney(_details.discountCI,2));
    $("#inputInsuranceImage").val(_details.insuranceImage);
    $("#compulsoryInsuranceImage").attr("src",_details.compulsoryInsuranceImage);
    if(!common.isEmpty(_details.compulsoryInsuranceImage)){
        $("#compulsoryDiv").removeClass("unvisable-hidden")
        $("#compulsoryInsuranceImage").attr("src", _details.compulsoryInsuranceImage.endsWith("pdf") ? "../../images/pdf.jpg" : _details.compulsoryInsuranceImage);
        $("#compulsoryInsuranceImage").attr("url", _details.compulsoryInsuranceImage);
        $("#inputCompulsoryInsuranceImage").val(_details.compulsoryInsuranceImage);
        $("#compulsoryInsuranceImage").show();
        $("#compulsoryInsuranceImage").next().show();

        var filename = _details.compulsoryInsuranceImage.substring(_details.compulsoryInsuranceImage.lastIndexOf("/") + 1);
        $("#downloadCompulsory").attr("href", _details.compulsoryInsuranceImage);
        $("#downloadCompulsory").attr("download", filename);
    }
    if(!common.isEmpty(_details.compulsoryStampFile)){
        $("#compulsoryStampDiv").removeClass("unvisable-hidden")
        $("#compulsoryInsuranceStamp").attr("src", _details.compulsoryStampFile.endsWith("pdf") ? "../../images/pdf.jpg" : _details.compulsoryStampFile);
        $("#compulsoryInsuranceStamp").attr("url", _details.compulsoryStampFile);
        $("#inputCompulsoryInsuranceStamp").val(_details.compulsoryStampFile);
        $("#compulsoryInsuranceStamp").show();
        $("#compulsoryInsuranceStamp").next().show();

        var filename = _details.compulsoryStampFile.substring(_details.compulsoryStampFile.lastIndexOf("/") + 1);
        $("#downloadCompulsoryStamp").attr("href", _details.compulsoryStampFile);
        $("#downloadCompulsoryStamp").attr("download", filename);
    }
    $("#applicantName").val(_details.applicantName);
    $("#applicantIdNo").val(_details.applicantIdNo);
    $("#applicantIdType").val((_details.applicantIdType)?_details.applicantIdType.id:1);
    $("#saveButton").attr("disabled", false);
}

/* 出单机构 */
function initInstitution(insuranceCompanyId,institutionId){
    common.getByAjax(true, "get", "json", "/orderCenter/nationwide/institution/rebate",
        {
            licensePlateNo:$("#licensePlateNo").val(),
            insuranceCompanyId:insuranceCompanyId,
            confirmOrderDate:$("#confirmOrderDate").val()
        },
        function(data){
            $("#institution").empty();
            //if(data.length==0){
            //    showErrorMsg("请先添加当前车辆所在城市和保险公司的费率信息！");
            //    return false;
            //}
            var options = "<option value=''>请选择出单机构</option>";
            $.each(data, function(i,model){
                //var select="";
                //if(model.institutionId==institutionId){
                //    select="selected" ;
                //}
                options += "<option value='"+ model.institutionId +"' commercialRebate='"+model.commercialRebate+"' compulsoryRebate='"+model.compulsoryRebate+"' >" + model.institutionName + "</option>";

            });
            $("#institution").html(options);
            $("#institution").val(institutionId);
        },function(){}
    );
}
