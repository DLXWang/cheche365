var baseInfo = {
    id:'',
    partner:'',
    channel:'',
    apiPartner:'',

    reserve:'',
    supportPhoto:'',
    showCustomService:'',
    serviceTel:'',
    home:'',
    homeFixBottom:'',
    showPartner:'',
    baseLogin:'',
    baseCustomAndPhoto:'',
    award:'',
    base:'',
    base_select:'',
    hasWallet:'',
    cheWallet:'',
    orderGift:'',
    orderInsuredCar:'',
    successOrder:'',
    isOrderPage:'',
    homeUrl:'',
    googleTrackId:'',
    themeColor:'',
    synchro:'',
    address:'',
    baseBanner:'',
    supplement:'',

    baseOrder:false,
    baseMine:false

}
var chanelAdd = {
    downloadUrl:function(){
        window.location.href="/operationcenter/thirdParty/tocCooperate/updateUrl?id="+baseInfo.id;
    },
    selectChannel: function(){//第一步
        if(common.isEmpty($('#partnerSel').val())) {
            alert('请选择合作商');
        }else{
            baseInfo.partner = $("#partnerSel").val();
            baseInfo.channel = $("#channelName").val();
            baseInfo.apiPartner = $("#channelEngName").val();
            var channelCode = document.getElementById("channelCode");
            if($("#channelEngName").val().length>30){
                channelCode.style.display = "inline ";
                return false;
            }else{
                channelCode.style.display  = "none";
            }
            $("#process1").removeClass('active');
            $("#process2").addClass('active');
            $(".add_list").hide();
            $("#add_2").show();
        }
    },
    pageConfig:function(){///第二
        baseInfo.baseBanner = $("input[name='baseBanner']:checked").val();
        baseInfo.reserve = $("input[name='reserve']:checked").val();
        baseInfo.supportPhoto = $("input[name='supportPhoto']:checked").val();

        baseInfo.showCustomService = $("input[name='showCustomService']:checked").val();
        baseInfo.serviceTel = $("#serviceTel").val();

        baseInfo.home = $("#home").val();
        baseInfo.homeFixBottom = $("input[name='homeFixBottom']:checked").val();

        baseInfo.showPartner = $("input[name='showPartner']:checked").val();
        baseInfo.baseLogin = $("input[name='baseLogin']:checked").val();

        baseInfo.baseCustomAndPhoto = $("input[name='baseCustomAndPhoto']:checked").val();
        baseInfo.award = $("input[name='award']:checked").val();

        baseInfo.base = $("input[name='base']:checked").val();
        if( $("input[name='base']:checked").val() == 1){
            if( $("#base_select").val() == 1){
                baseInfo.baseOrder = true;
            }else{
                baseInfo.baseMine = true;
            }
        }
        baseInfo.hasWallet = $("input[name='hasWallet']:checked").val();
        if ($("input[name='hasWallet']:checked").val()==1){
            baseInfo.cheWallet = $("#cheWallet").val();
        }
        baseInfo.orderGift = $("input[name='orderGift']:checked").val();
        baseInfo.orderInsuredCar = $("input[name='orderInsuredCar']:checked").val();

        baseInfo.successOrder = $("input[name='successOrder']:checked").val();
        baseInfo.isOrderPage = $("#isOrderPage").val();
        baseInfo.themeColor = $("#themeColor").val();

        baseInfo.customer = $("input[name='customer']:checked").val();
        baseInfo.thirdPartyManage = $("input[name='third_party_manage']:checked").val();
        baseInfo.elecAgreement = $("input[name='elec_agreement']:checked").val();
        baseInfo.homeUrl = $("#homeUrl").val();
        baseInfo.googleTrackId = $("#googleTrackId").val();
        baseInfo.wallet = $("input[name='wallet']:checked").val();
        baseInfo.award = $("input[name='award']:checked").val();
        $("#process2").removeClass('active');
        $("#process3").addClass('active');
        $(".add_list").hide();
        $("#add_3").show();
    },
    channelParam:function(){//第三
        baseInfo.isOrder = $("input[name='is_order']:checked").val();
        baseInfo.isOrderCenter = $("input[name='is_ordercenter']:checked").val();
        baseInfo.supplement = $("input[name='is_supplement']:checked").val();
        baseInfo.synchro = $("input[name='synchro']:checked").val();
        baseInfo.address = $("#address").val();
        common.getByAjax(true, 'POST', 'json', "/operationcenter/thirdParty/tocCooperate/add", baseInfo,
            function (data) {
                if(data.pass){
                    $("#process3").removeClass('active');
                    $("#process4").addClass('active');
                    $(".add_list").hide();
                    $("#add_4").show();
                    baseInfo.id = data.message;
                }else{
                    alert(data.message);
                    popup.mould.popTipsMould( data.message, popup.mould.first, popup.mould.error, "", "", null);
                }
            }, function () {
                popup.mould.popTipsMould( "出现异常！", popup.mould.first, popup.mould.error, "", "", null);
            }
        );
    },
    back:function(num){
        $("#process_li").removeClass('active');
        $("#process" + num).addClass('active');
        $(".add_list").hide();
        $("#add_" + num).show();
    },
    /* 合作商下拉列表查询 */
    partnerList: function () {
        common.getByAjax(true, "get", "json", "/operationcenter/thirdParty/tocCooperate/partnerNameList", {},
            function (data) {
                if (data) {
                    var options = "";
                    $.each(data, function (i, model) {
                        options += "<option value=\"" + model.id + "\">" + model.name + "</option>";
                    });
                    $("#partnerSel").append(options);
                }
            },
            function () {
                popup.mould.popTipsMould("系统异常！", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    },

    junmInfo: function(){
        window.open('/views/thirdParty/toC_details.jsp?id=' + baseInfo.id );
    },
}

$(function () {
    chanelAdd.partnerList();
    var channelCode = document.getElementById("channelCode");
    channelCode.style.display  = "none";
    $('[data-change]').change(function () {
        var showValue = this.getAttribute('show-value')
        var showTarget = this.getAttribute('show-target')
        if (this.value == showValue) {
            $(showTarget).show()
        } else {
            $(showTarget).hide()
        }
    })

});
