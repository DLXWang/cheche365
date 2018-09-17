//@ sourceURL=tide_contract_list.js
//
// var tide_institution_add = {
//     param: {
//         branchId: 0
//     },
//     init: {},
//     operation: {
//         submit: function () {
//             debugger;
//             alert(tide_contract.param.branchId);
//             let formData = {
//                 branchId: tide_contract.param.branchId,
//                 institutionName: $('.tideInstitutionAdd').val(),
//                 description: $(".tideDescriptionAdd").val(),
//             };
//             tide_institution_add.interface.save(formData);
//         }
//     },
//     interface: {
//         save: function (formData) {
//             $.ajax({
//                 async: 'false',
//                 type: 'post',
//                 dataType: 'json',
//                 contentType: "application/json",
//                 url: '/operationcenter/tide/contract/add',
//                 data: JSON.stringify(formData),
//                 success: function (data) {
//                     if (data.pass) {
//                         popup.mould.popTipsMould("保存成功！", popup.mould.second, popup.mould.success, "", "53%", function () {
//                             popup.mask.hideSecondMask();
//                             popup.mask.hideFirstMask();
//                         });
//                         tide_contract.param.dataTable.ajax.reload();
//                     } else {
//                         popup.mould.popTipsMould(data.message, popup.mould.second, popup.mould.error, "", "53%", null);
//                     }
//                 },
//                 error: function () {
//                     popup.mould.popTipsMould("保存失败！", popup.mould.second, popup.mould.error, "", "53%", null);
//                 }
//             });
//         }
//     }
// };

$(function () {
    // tide_institution_add.param.branchId = $(".branchSelAdd").val();
});
