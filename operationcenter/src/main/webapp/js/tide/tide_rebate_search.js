(function ($, undefined) {
    var constants = window.tideConstants
    if (!constants) {
        $.get('/operationcenter/tide/resource/constants', function (result) {
            initConstants(result)
        })
    } else {
        initConstants(constants)
    }

    function initConstants(result) {
        window.tideConstants = result
        var key, nodeList = []
        for (key in result.REBATESTATUS_MAP) {
            nodeList.push('<option value="' + key + '">' + result.REBATESTATUS_MAP[key] + '</option>')
        }
        $('#search_rebate_status').append(nodeList.join(''))

        var key, nodeList = []
        for (key in result.INSURANCETYPE_MAP) {
            nodeList.push('<option value="' + key + '">' + result.INSURANCETYPE_MAP[key] + '</option>')
        }
        $('#search_insurance_type').append(nodeList.join(''))

        var key, nodeList = []
        for (key in result.CARTYPE_MAP) {
            nodeList.push('<option value="' + key + '">' + result.CARTYPE_MAP[key] + '</option>')
        }
        $('#search_car_type').append(nodeList.join(''))
    }


    $('#search_contract').select2({
        language: 'zh-CN',
        theme: 'bootstrap',
        ajax: {
            delay: 300,
            url: '/operationcenter/tide/resource/contract',
            data: function (params) {
                return {
                    contractName: params.term,
                }
            },
            processResults: function (data) {
                data.forEach(it => it.text = it.name)
                return {
                    results: [{id: '', text: '请选择'}].concat(data)
                }
            }
        }
    }).on('change', function () {
        $('#search_support_area').val('').trigger('change')
    })

    $('#search_support_area').select2({
        language: 'zh-CN',
        theme: 'bootstrap',
        ajax: {
            delay: 300,
            url: '/operationcenter/tide/resource/area',
            data: function (params) {
                return {
                    contractId: $('#search_contract').val(),
                    areaName: params.term,
                }
            },
            processResults: function (data) {
                data.forEach(it => it.text = it.name)
                return {
                    results: [{id: '', text: '请选择'}].concat(data)
                }
            }
        }
    })
}(window.jQuery))
