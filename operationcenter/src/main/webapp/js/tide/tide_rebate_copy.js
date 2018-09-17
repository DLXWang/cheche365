(function ($, undefined) {
    $('#contractCopy').select2({
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
                    results: data
                }
            }
        }
    }).on('change', function () {
        $('#supportAreaCopy').val('').trigger('change')
    })

    $('#supportAreaCopy').select2({
        language: 'zh-CN',
        theme: 'bootstrap',
        ajax: {
            delay: 300,
            url: '/operationcenter/tide/resource/area',
            data: function (params) {
                return {
                    contractId: $('#contractCopy').val(),
                    areaName: params.term,
                }
            },
            processResults: function (data) {
                data.forEach(it => it.text = it.name)
                return {
                    results: data
                }
            }
        }
    })

}(window.jQuery))
