/**
 * Created by taguangyao on 2015/11/20.
 */

var personalValidation = {
    valid: function() {
        return this.validation.validInsuredName()
            && this.validation.validInsuredIdNo()
            && this.validation.validOthers()
            && this.validation.validateCommercial()
            && this.validation.validateCompulsory()
            && this.validation.validateApplicant();
        //return this.validation.validLicenseNo() && this.validation.validOwner() &&  this.validation.validIdentity()
        //    && this.validation.validVinNo() && this.validation.validEngineNo() && this.validation.validInsuredName()
        //    && this.validation.validInsuredIdNo() && this.validation.validOthers() && this.validation.validateCommercial() && this.validation.validateCompulsory();
    },
    validation: {
        //validLicenseNo: function() {
        //    if (!common.validateLicenseNo($("#licensePlateNo").val())) {
        //        showErrorMsg("车牌号填写错误");
        //        return false;
        //    }
        //
        //    return true;
        //},
        //validOwner: function() {
        //    if (!common.validateName($("#owner").val())) {
        //        showErrorMsg("车主姓名填写错误");
        //        return false;
        //    }
        //
        //    return true;
        //},
        //validIdentity: function() {
        //    if (!common.isIdCardNo($("#identity").val())) {
        //        showErrorMsg("车主身份证号填写错误");
        //        return false;
        //    }
        //
        //    return true;
        //},
        //validVinNo: function() {
        //    if (!common.validateVinNo($("#vinNo").val())) {
        //        showErrorMsg("车架号填写错误");
        //        return false;
        //    }
        //
        //    return true;
        //},
        //validEngineNo: function() {
        //    if (!common.validateEngineNo($("#engineNo").val())) {
        //        showErrorMsg("发动机号填写错误");
        //        return false;
        //    }
        //
        //    return true;
        //},
        validInsuredIdNo: function() {
            if (!common.isIdCardNo($("#insuredIdNo").val())) {
                showErrorMsg("被保险人身份证号填写错误");
                return false;
            }
            return true;
        },
        validInsuredName: function() {
            if (!common.validateName($("#insuredName").val())) {
                showErrorMsg("被保险人姓名填写错误");
                return false;
            }

            return true;
        },
        validOthers: function() {
            var _commercialPremium = $("#commercialPremium").val();//商业险总保费
            var _compulsoryPremium = $("#compulsoryPremium").val();//交强险保费

            if (_commercialPremium <= 0 && _compulsoryPremium <= 0) {
                showErrorMsg("商业险和交强险请至少选择一种进行录入");
                return false;
            }

            return true;
        },
        /* 验证商业险表单 */
        validateCommercial: function(){
            var flag = true;
            var error_text="";
            //是否投保
            var isBuying = false;
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
                _amount = $("#thirdPartyAmountSel").val();//保额
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
                _count = $("#passengerCount").val();//人数

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
                if(flag){
                    if(!common.isEmpty(_count) && !common.validations.isZeroAndPosNumber(_count)){
                        error_text="车上人员(乘客)人数为零或正整数";
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
                }else if(!_commercialPolicyNo){
                    error_text="请输入商业险保单号";
                    flag = false;
                }else if(!_commercialPremium){
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
                if(!_compulsoryPolicyNo){
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
            if(common.isEmpty($("#applicantIdNo").val())){
                error_text="请输入投保人身份证号";
                flag = false;
            }
            if(!flag){
                showErrorMsg(error_text);
            }
            return flag;
        }
    }
};

var orderInsuranceRecord = {
    init : function() {
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

        /* 保险公司 */
        common.getByAjax(true, "get", "json", "/orderCenter/resource/insuranceCompany/getQuotableCompanies", null,
            function(data){
                if(data == null){
                    return false;
                }
                var options = "";
                $.each(data, function(i,model){
                    options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                });
                $("#insuranceCompanySel").append(options);
            },function(){}
        );
        common.getByAjax(true, "get", "json", "/orderCenter/nationwide/institution/enable", null,
            function(data){
                if(data == null){
                    return false;
                }
                var options = "";
                $.each(data, function(i,model){
                    options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                });
                $("#institutionSel").append(options);
            },function(){}
        );
        /* 投保区域 */
        common.getByAjax(true,"get","json","/orderCenter/nationwide/areaContactInfo/allArea",null,
            function(data){
                if(data == null){
                    return false;
                }

                var options = "";
                $.each(data, function(i,model){
                    options += "<option value='"+ model.id +"'>" + model.name + "</option>";
                });

                $("#areaSel").append(options);
            },function(){}
        );
    },
    edit: function(orderNo) {
        common.getByAjax(true, "get", "json", "/orderCenter/nationwide/" + orderNo + "/orderInsuranceRecord", {},
            function(data) {
                $("#orderNo").val(common.tools.checkToEmpty(data.orderNo));
                $("#licensePlateNo").val(common.tools.checkToEmpty(data.licensePlateNo));
                $("#owner").val(common.tools.checkToEmpty(data.owner));
                $("#identity").val(common.tools.checkToEmpty(data.identity));
                $("#vinNo").val(common.tools.checkToEmpty(data.vinNo));
                $("#engineNo").val(common.tools.checkToEmpty(data.engineNo));
                $("#enrollDate").val(common.tools.checkToEmpty(data.enrollDate));
                $("#brand").val(common.tools.checkToEmpty(data.brand));
                $("#insuredIdNo").val(common.tools.checkToEmpty(data.insuredIdNo));
                $("#insuredName").val(common.tools.checkToEmpty(data.insuredName));
                $("#insuranceCompanySel").val(common.tools.checkToEmpty(data.insuranceCompany));
                $("#originalPremium").val(common.formatMoney(data.originalPremium, 2));
                $("#rebateExceptPremium").val(common.formatMoney(data.rebateExceptPremium, 2));
                $("#trackingNo").val(common.tools.checkToEmpty(data.trackingNo));
                $("#areaSel").val(common.tools.checkToEmpty(data.area));
                $("#institutionSel").val(common.tools.checkToEmpty(data.institution));
                $("#institutionSel").attr('title', $("#institutionSel").find("option:selected").text());
                $("#expressCompany").val(common.tools.checkToEmpty(data.expressCompany));

                $("#insuranceCompanySel").attr("disabled", true);
                $("#areaSel").attr("disabled", true);
                $("#institutionSel").attr("disabled", true);
                $("#originalPremium").attr("disabled", true);
                $("#licensePlateNo").attr("disabled", true);
                $("#owner").attr("disabled", true);
                $("#identity").attr("disabled", true);
                $("#vinNo").attr("disabled", true);
                $("#engineNo").attr("disabled", true);
                $("#enrollDate").attr("disabled", true);
                $("#brand").attr("disabled", true);
                $("#applicantName").val(data.applicantName);
                $("#applicantIdNo").val(data.applicantIdNo);

                // 商业险
                if (data.commercialPremium) {
                    $("#commercialPolicyNo").val(common.tools.checkToEmpty(data.commercialPolicyNo));
                    $("#commercialPremium").val(common.formatMoney(data.commercialPremium, 2));
                    $("#commercialEffectiveDate").val(common.tools.checkToEmpty(data.commercialEffectiveDate));
                    $("#commercialEffectiveHour").val(data.commercialEffectiveHour == null ? 0 : data.commercialEffectiveHour);
                    $("#commercialExpireHour").val(data.commercialExpireHour == null ? 24 : data.commercialExpireHour);
                    $("#commercialExpireDate").val(common.tools.checkToEmpty(data.commercialExpireDate));
                    $("#damagePremium").val(common.formatMoney(data.damagePremium,2));
                    $("#damageAmount").val(common.formatMoney(data.damageAmount,2));
                    $("#damageIop").val(common.formatMoney(data.damageIop,2));
                    $("#thirdPartyPremium").val(common.formatMoney(data.thirdPartyPremium,2));
                    $("#thirdPartyAmountSel").val(data.thirdPartyAmount == null ? 0 : data.thirdPartyAmount);
                    $("#thirdPartyIop").val(common.formatMoney(data.thirdPartyIop,2));
                    $("#driverPremium").val(common.formatMoney(data.driverPremium,2));
                    $("#driverAmountSel").val(data.driverAmount == null ? 0 : data.driverAmount);
                    $("#driverIop").val(common.formatMoney(data.driverIop,2));
                    $("#passengerPremium").val(common.formatMoney(data.passengerPremium,2));
                    $("#passengerAmountSel").val(data.passengerAmount == null ? 0 : data.passengerAmount);
                    $("#passengerIop").val(common.formatMoney(data.passengerIop,2));
                    $("#passengerCount").val(common.tools.checkToEmpty(data.passengerCount));
                    $("#theftPremium").val(common.formatMoney(data.theftPremium,2));
                    $("#theftAmount").val(common.formatMoney(data.theftAmount,2));
                    $("#theftIop").val(common.formatMoney(data.theftIop,2));
                    $("#scratchAmountSel").val(data.scratchAmount == null ? 0 : data.scratchAmount);
                    $("#scratchPremium").val(common.formatMoney(data.scratchPremium,2));
                    $("#scratchIop").val(common.formatMoney(data.scratchIop,2));
                    $("#spontaneousLossPremium").val(common.formatMoney(data.spontaneousLossPremium,2));
                    $("#spontaneousLossAmount").val(common.formatMoney(data.spontaneousLossAmount,2));
                    $("#enginePremium").val(common.formatMoney(data.enginePremium,2));
                    $("#engineAmount").val(common.formatMoney(data.engineAmount,2));
                    $("#engineIop").val(common.formatMoney(data.engineIop,2));
                    $("#glassPremium").val(common.formatMoney(data.glassPremium,2));
                    $("#glassTypeSel").val(common.formatMoney(data.glassPremium,2) == 0.00?
                        0 : (data.glassType == null ? 0 : data.glassType));
                    $("#inputInsuranceImage").val(common.tools.checkToEmpty(data.insuranceImage));
                    $("#insuranceImage").attr("src",common.tools.checkToEmpty(data.insuranceImage));
                    if(!common.isEmpty(data.insuranceImage)){
                        $("#insuranceImage").show();
                    }
                    $("#discount").val(common.formatMoney(data.discount,2));
                }
                $("#quoteCompulsoryPolicyNo").val(data.quoteCompulsoryPolicyNo == null ? "" : data.quoteCompulsoryPolicyNo);
                $("#quoteCommercialPolicyNo").val(data.quoteCommercialPolicyNo == null ? "" : data.quoteCommercialPolicyNo);
                // 交强险
                if (data.compulsoryPremium) {
                    $("#compulsoryPolicyNo").val(common.tools.checkToEmpty(data.compulsoryPolicyNo));
                    $("#compulsoryEffectiveDate").val(common.tools.checkToEmpty(data.compulsoryEffectiveDate));
                    $("#compulsoryExpireDate").val(common.tools.checkToEmpty(data.compulsoryExpireDate));
                    $("#compulsoryEffectiveHour").val(data.compulsoryEffectiveHour == null ? 0 : data.compulsoryEffectiveHour);
                    $("#compulsoryExpireHour").val(data.compulsoryExpireHour == null ? 24 : data.compulsoryExpireHour);
                    $("#compulsoryPremium").val(common.formatMoney(data.compulsoryPremium,2));
                    $("#autoTax").val(common.formatMoney(data.autoTax,2));
                    $("#inputCompulsoryInsuranceImage").val(common.tools.checkToEmpty(data.compulsoryInsuranceImage));
                    $("#compulsoryInsuranceImage").attr("src",common.tools.checkToEmpty(data.compulsoryInsuranceImage));
                    if(!common.isEmpty(data.compulsoryInsuranceImage)){
                        $("#compulsoryInsuranceImage").show();
                    }
                    $("#discountCI").val(common.formatMoney(data.discountCI,2));
                }
            },
            function() {
                popup.mould.popTipsMould(true, "获取保单信息异常", "first", "error", "", "56%",
                    function() {
                        popup.mask.hideFirstMask(true);
                    }
                );
            }
        );
    },
    update: function(form) {
        $("#toCreate").attr("disabled", true);
        common.getByAjax(true, "put", "json", "/orderCenter/nationwide/" + $("#orderNo").val() + "/orderInsuranceRecord", $(form).serialize(),
            function(data) {
                $("#toCreate").attr("disabled", false);
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
            },
            function() {
                popup.mould.popTipsMould(true, "系统异常", "first", "error", "", "56%",
                    function() {
                        popup.mask.hideFirstMask(true);
                    }
                );
                $("#toCreate").attr("disabled", false);
            }
        );
    }
};
$(function(){
    orderInsuranceRecord.init();
    var orderNo = common.getUrlParam("orderNo");
    if(orderNo != null){
        orderInsuranceRecord.edit(orderNo);
    }
    var validOptions_new = {
        onkeyup: false,
        onfocusout: false,
        rules: {
            //licensePlateNo: {
            //    required: true
            //},
            //owner: {
            //    required: true
            //},
            //identity: {
            //    required: true
            //},
            //vinNo: {
            //    required: true
            //},
            //engineNo: {
            //    required: true
            //},
            //enrollDate: {
            //    required: true
            //},
            //brand: {
            //    required: true
            //},
            insuredIdNo: {
                required: true
            },
            insuredName: {
                required: true
            },
            insuranceCompany: {
                required: true
            },
            originalPremium: {
                required: true,
                number: true
            },
            rebateExceptPremium: {
                required: true,
                number: true
            }
        },
        messages: {
            //licensePlateNo: {
            //    required: "请输入车牌号"
            //},
            //owner: {
            //    required: "请输入车主姓名"
            //},
            //identity: {
            //    required: "请输入车主身份证号"
            //},
            //vinNo: {
            //    required: "请输入车架号"
            //},
            //engineNo: {
            //    required: "请输入发动机号"
            //},
            //enrollDate: {
            //    required: "请选择初登日期"
            //},
            //brand: {
            //    required: "请输入厂牌型号"
            //},
            insuredIdNo: {
                required: "请输入被保险人身份证"
            },
            insuredName: {
                required: "请输入被保险人姓名"
            },
            insuranceCompany: {
                required: "请选择保险公司"
            },
            originalPremium: {
                required: "请输入保险原始总金额",
                number: "请输入正确的保险原始总金额"
            },
            rebateExceptPremium: {
                required: "请输入扣除返点后总金额",
                number: "请输入正确的扣除返点后总金额"
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
            if (common.isEmpty($("#orderNo").val())) {

            } else {
                var str = "";
                _quoteCompulsoryPolicyNo = $("#quoteCompulsoryPolicyNo").val(); //交强险
                _quoteCommercialPolicyNo = $("#quoteCommercialPolicyNo").val(); //商业险
                _compulsoryPolicyNo = $("#compulsoryPolicyNo").val(); //交强险
                _commercialPolicyNo = $("#commercialPolicyNo").val(); //商业险
                if(_quoteCompulsoryPolicyNo != _compulsoryPolicyNo) {
                    str = "交强险保单号("+_compulsoryPolicyNo+")与出单信息的保单号("+_quoteCompulsoryPolicyNo+")不一致可能有错误，是否继续?";
                }
                if(_quoteCommercialPolicyNo != _commercialPolicyNo) {
                    str = "商业险保单号("+_commercialPolicyNo+")与出单信息的保单号("+_quoteCommercialPolicyNo+")不一致可能有错误，是否继续?"
                }
                if(str == "") {
                    orderInsuranceRecord.update(form);
                } else {
                    popup.mould.popConfirmMould(true, str, "first", false,  "56%",
                        function() {
                            orderInsuranceRecord.update(form);
                            popup.mask.hideFirstMask(true);
                        },
                        function() {
                            popup.mask.hideFirstMask(true);
                        }
                    );
                }
            }
        }
    };
    $("#insuranceForm").validate(validOptions_new);


    $("#toCancel").bind({
        click : function(){
            parent.window.returnValue =true;
            window.close();
        }
    });


    $("#insuranceImageFile").bind({
        change : function(){
            var url="/orderCenter/insurance/upload/commercial";
            uploadFile(url,function(result){
                $("#insuranceImage").show();
                $("#insuranceImage").attr("src",result);
                $("#inputInsuranceImage").val(result);
            })
        }
    });
    $("#compulsoryInsuranceImageFile").bind({
        change : function(){
            var url="/orderCenter/insurance/upload/compulsory";
            uploadFile(url,function(result){
                $("#compulsoryInsuranceImage").show();
                $("#compulsoryInsuranceImage").attr("src",result);
                $("#inputCompulsoryInsuranceImage").val(result);
            })
        }
    });

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

});
//错误提示框
var showErrorMsg = function(msg){
    popup.mould.popTipsMould(true, msg, "first", "warning", "", "",
        function() {
            popup.mask.hideFirstMask(true);
        }
    );
};
popup.insertHtml("#popupHtml");

function uploadFile(url,callbackMethod){
    var form = $("#insuranceForm");
    var options = {
        url : url,
        type : "post",
        dataType: "text",
        success : function(result) {
            if(result == "error") {
                popup.mould.popTipsMould(true, "图片上传失败，文件格式错误！", "first", "error", "", "56%",
                    function() {
                        popup.mask.hideFirstMask(true);
                    }
                );
                return false;
            } else {
                if(callbackMethod){
                    callbackMethod(result);
                }
            }
        },
        error : function() {
            common.showTips("系统异常");
            common.hideMask();
        }
    };
    form.ajaxSubmit(options);
}
