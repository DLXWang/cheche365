var baseInfo = {
    id:"",
    partner:'',
    channel:'',
    apiPartner:'',
    customer:'',
    thirdPartyManage:'',
    elecAgreement:'',
    wallet:'',
    award:'',
    isOrder:'',
    isOrderCenter:'',
    synchro:'',
    address:'',
    supportPhoto:'',
    showCustomService:'',
    serviceTel:'',
    googleTrackId:'',
    themeColor:''
}
var chanelAdd = {
    downloadUrl:function(){
        window.location.href="/operationcenter/thirdParty/tocCooperate/updateUrl?id="+baseInfo.id;
    },
    selectChannel: function(){//第一步
        if(common.isEmpty($('#partnerSel').val())){
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
        baseInfo.thirdPartyManage = $("input[name='third_party_manage']:checked").val();
        baseInfo.elecAgreement = $("input[name='elec_agreement']:checked").val();
        baseInfo.wallet = $("input[name='wallet']:checked").val();
        baseInfo.award = $("input[name='award']:checked").val();
        baseInfo.supportPhoto = $("input[name='supportPhoto']:checked").val();
        baseInfo.showCustomService = $("input[name='showCustomService']:checked").val();
        baseInfo.serviceTel = $("#serviceTel").val();
        baseInfo.googleTrackId = $("#googleTrackId").val();
        baseInfo.themeColor = $("#themeColor").val();
        $("#process2").removeClass('active');
        $("#process3").addClass('active');
        $(".add_list").hide();
        $("#add_3").show();
    },
    channelParam:function(){//第三

        baseInfo.isOrder = $("input[name='is_order']:checked").val();
        baseInfo.isOrderCenter = $("input[name='is_ordercenter']:checked").val();
        baseInfo.synchro = $("input[name='synchro']:checked").val();
        baseInfo.address = $("#address").val();
        common.getByAjax(true, 'POST', 'json', "/operationcenter/thirdParty/toaCooperate/add", baseInfo,
            function (data) {
                if(data.pass){
                    baseInfo.id = data.message;
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
        window.open('/views/thirdParty/toA_details.html?id=' + baseInfo.id);
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
