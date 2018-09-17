$(function() {
    var reqParams = {
        currentPage :     1,
        pageSize :       20,
        orderStatus:      $("#orderStatus").val(),
        //paymentStatus:    $("#paymentStatus").val(),
        paymentChannel:   $("#paymentChannel").val(),
        area:             $("#area").val(),
        sort:             $("#sort").val()
    };
    $('#list_tab').DataTable( {
        "ajax": '/orderCenter/dataTable',

        "columns": [
            { "data": "orderNo" },
            { "data": "currentStatus.description" },
            { "data": "assignerName" },
            { "data": "auto.owner" },
            { "data": "paymentChannel.channel" },
            { "data": "area.name" }
        ]
    } );
});
