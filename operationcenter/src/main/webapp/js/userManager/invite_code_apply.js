(function ($, undefined) {
    let cityIdList = [];

    common.getByAjax(true, 'get', 'json', '/operationcenter/channleAgent/resource/channels', {}, function (data) {
        let content = '<option value="">请选择渠道</option>';
        $.each(data, function (i, model) {
            content += `<option value=${model.id}>${model.description}</option>`;
        });
        $('#channelAddSel').html(content);
    }, function () {
    });

    $("#channelAddSel").on('change', function () {
        cityIdList = [];
        initSupportArea(this.value);
    });


    let tables;
    $(".generateInviteCode").on("click", function () {
        let genCodeBtn = this;
        let applicantName = $.trim($("#applicantName").val());
        if (!applicantName) {
            layer.tips('请输入邀请码申请人! ', '#applicantName');
            return false;
        }
        let channel = $('#channelAddSel').val();
        if (!channel) {
            layer.tips('请选择渠道! ', '#channelAddSel');
            return false;
        }
        let areas = $('#supportAreaSel').val();
        if (!areas) {
            layer.tips('请选择地区! ', '#supportAreaSel');
            return false;
        } else if (areas === '0') {
            areas = null;
        } else {
            areas = [areas];
        }
        let number = $("#inviteNum").val();
        if (!number) {
            layer.tips("请输入申请数量!", '#inviteNum');
            return false;
        }
        if (isNaN(number)) {
            layer.tips("申请数量必须是数字!", '#inviteNum');
            return false;
        }
        if (number <= 0) {
            layer.tips("申请数字的范围应该为[1-50]个 !", '#inviteNum');
            return false;
        }
        if (number > 50) {
            layer.tips("一次最多只能生成50个!", '#inviteNum');
            return false;
        }
        common.getByAjax(true, "post", "json", "/operationcenter/channleAgent/inviteCode/apply/batch",
            {
                number: number,
                applicantName: applicantName,
                channelId: channel,
                areaList: areas
            },
            function (data) {
                if (tables) {
                    tables.clear();
                    tables.rows.add(data).draw();
                } else {
                    tables = $('#inviteCodeTable').DataTable({
                        paging: false,
                        searching: false,
                        data: data,
                        columns: [
                            {title: "申请人", data: "applicantName"},
                            {title: "邀请码", data: "inviteCode"}
                        ],
                        dom: 'Bfrtip',
                        buttons: [
                            {
                                'extend': 'excel',
                                'text': '导出Excel',//定义导出excel按钮的文字
                                'filename': "邀请码申请明细-" + applicantName + "-" + new Date().toLocaleDateString(), //导出的excel标题
                                'title': null
                            }
                        ]
                    });
                }
                $('.buttons-excel').click();
                layer.msg("导出成功!", {icon: 1, time: 500}, function (index, layero) {
                    $(genCodeBtn).attr('disabled', false);
                    layer.close($(".popDiv").data("layer-index"));
                });
            },
            function () {
                layer.msg("生成错误", {icon: 5});
            }
        );
    });


    // ==================functions ========================

    function initSupportArea(channelId) {
        let content = '<option value="">请选择</option>';
        let $select = $('#supportAreaSel');
        if (!channelId) {
            $select.html(content);
            return false;
        }
        common.getByAjax(false, 'get', 'json', `/operationcenter/channleAgent/resource/${channelId}/supportArea`, {}, function (data) {
            if (data.length > 0) {
                content += '<option value="0">全部</option>';
            }
            $.each(data, function (index, province) {
                content += `<optgroup label=${province.provinceName} class=${"province" + province.provinceId}>`;
                let cityList = province.cityList;
                for (let city of cityList) {
                    content += `<option value=${city.cityId} >${city.cityName}</option>`;
                    cityIdList.push(city.cityId);
                }
            });
            $select.html(content);
        }, function () {
        });
    }
}(window.jQuery));
