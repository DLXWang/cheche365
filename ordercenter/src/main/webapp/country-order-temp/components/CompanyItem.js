import React, { Component, PropTypes } from 'react'
import { TableRow, TableRowColumn, FlatButton, RaisedButton} from 'material-ui' 

export default class TableItem extends Component {

	constructor() {
		super();
	}
	_toggleStatus() {
		console.log('修改出单机构状态', this.props.company);
		this.props.actions.changeCompanyStatus(this.props.company);
	}
	__handleOpenTemplate(company) {
		this.props._handleOpenTemplate(company);
	}
    _reInputRebate(){
        var parent = window.parent;
        popup.pop.popInput(false, re_input_rebate_pop.inputContent, "first", "700px", "520px", "38%", "49%");
        re_input_rebate_pop.init(this.props.company.id);
        parent.$("#institution").val(this.props.company.id);
        parent.$("#popover_normal_input .theme_poptit .close").unbind("click").bind({
            click: function () {
                popup.mask.hideFirstMask(false);
            }
        });
        parent.$(".cancel").unbind("click").bind({
            click: function () {
                popup.mask.hideFirstMask(false);
            }
        });
        parent.$(".submit").unbind("click").bind({
            click: function () {
                re_input_rebate_pop.save();
            }
        });
        parent.$("#area").unbind("change").bind({
            change:function(){
               re_input_rebate_pop.initInsuranceCompany($(this).val());
            }
        })
    }
	render() {
			let item = this.props.company;
			let closeOrOpen = <FlatButton label={item.enable ? '禁用': '启用'} secondary={item.enable} primary={!item.enable} onClick={this._toggleStatus.bind(this)} />;
			let areas = '';
			for (let i=0; i < item.areaList.length; i++) {
				areas += item.areaList[i].name;
				if (i != item.areaList.length - 1)
					areas += ',';
			}
            let simpleAreas = areas;
            let splitAreas = areas.split(",");
            if(splitAreas.length > 3) {
                simpleAreas = splitAreas[0] + "," + splitAreas[1] + "," + splitAreas[2] + "...";
            }
            let simpleName = item.name.length > 10? (item.name.substring(0, 10) + "...") : item.name;
            let simpleComment = item.comment != null && item.comment.length > 10? (item.comment.substring(0, 10) + "...") : item.comment;
			let status = item.enable ? '已启用': '已禁用';
            if ($("#re_input_rebate").length > 0) {
                re_input_rebate_pop.inputContent = $("#re_input_rebate").html();
                $("#re_input_rebate").remove();
            }
			return (

					<TableRow>
					      <TableRowColumn><div>{item.id}</div></TableRowColumn>
					      <TableRowColumn><div title={item.name}>{simpleName}</div></TableRowColumn>
					      <TableRowColumn><div className='white-space' title={areas}>{simpleAreas}</div></TableRowColumn>
					      <TableRowColumn>
					      		<div>{item.operator}</div>
					      		<div>{item.updateTime}</div>
					      </TableRowColumn>
					      <TableRowColumn><div>{status}</div></TableRowColumn>
					      <TableRowColumn><div title={item.comment}>{simpleComment}</div></TableRowColumn>
					      <TableRowColumn style={{textAlign: 'center'}} colSpan="2">
					      	<div>{closeOrOpen}
					      		<RaisedButton label="查看详情" backgroundColor='#d9534f' onClick={this.__handleOpenTemplate.bind(this)} />
                                &nbsp;&nbsp;&nbsp;<RaisedButton label="回录历史费率" backgroundColor='#d9534f' onClick={this._reInputRebate.bind(this)} />
				      		</div>
			      		  </TableRowColumn>

					    </TableRow>
		)
	}
}

TableItem.propTypes = {
  company: PropTypes.object.isRequired,
  _handleOpenTemplate: PropTypes.func.isRequired
}

var re_input_rebate_pop={
    inputContent:"",
    parent:window.parent,
    init:function(institutionId){
        this.initArea();
        this.initList(institutionId);
    },
    initList: function(institutionId) {
        common.getByAjax(true, "get", "json", "/orderCenter/nationwide/institutionTemp/rebate/historyList",
            {
                institutionId : institutionId
            },
            function(data) {
                parent.$("#tabHistory tbody").empty();
                if (data) {
                    var content = "";
                    $.each(data, function(i, model){
                        content += "<tr>" +
                            "<td>" + model.areaName + "</td>" +
                            "<td>" + model.companyName + "</td>" +
                            "<td align='center'>" + model.commercialRebate + "%</td>" +
                            "<td align='center'>" + model.compulsoryRebate + "%</td>" +
                            "<td>" + model.startTime + "</td>" +
                            "<td>" + common.checkToEmpty(model.endTime) + "</td>" +
                            "</tr>";
                    });
                    parent.$("#tabHistory tbody").append(content);
                }
            },function() {}
        );
    },
    initArea:function(){
        var option="";
        common.ajax.getByAjax(false,"get","json","/orderCenter/resource/areas",null,
            function(data){
                if(data == null){
                    return false;
                }

                var areaId='';
                $.each(data, function(i,model){
                    option += "<option value='"+ model.id +"'>" + model.name + "</option>";
                    if(i==0){
                        areaId=model.id;
                    }
                });
                parent.$("#area").html(option);
                re_input_rebate_pop.initInsuranceCompany(areaId);
            },function(){}
        );
    },
    initInsuranceCompany:function(areaId){
        var option="";
        common.ajax.getByAjax(false,"get","json","/orderCenter/resource/insuranceCompany/getQuotableCompaniesByArea",{areaId:areaId},
            function(data){
                if(data == null){
                    return false;
                }

                $.each(data, function(i,model){
                    option += "<option value='"+ model.id +"'>" + model.name + "</option>";
                });
                parent.$("#insuranceCompany").html(option);
            },function(){}
        );
    },
    validate:function(){
        var insuranceCompany=parent.$("#insuranceCompany").val();
        var area=parent.$("#area").val();
        var compulsoryRebate=parent.$("#compulsoryRebate").val();
        var commercialRebate=parent.$("#commercialRebate").val();
        var startTime=parent.$("#startTime").val();
        if(common.isEmpty(area)){
            re_input_rebate_pop.error("请选择城市");
            return false;
        }
        if(common.isEmpty(insuranceCompany)){
            re_input_rebate_pop.error("请选择保险公司");
            return false;
        }
        if(common.isEmpty(compulsoryRebate)||!common.isNumber(compulsoryRebate)){
            re_input_rebate_pop.error("请输入正确的交强险费率");
            return false;
        }
        if(common.isEmpty(commercialRebate)||!common.isNumber(commercialRebate)){
            re_input_rebate_pop.error("请输入正确的商业费率");
            return false;
        }
        if(common.isEmpty(startTime)){
            re_input_rebate_pop.error("请输入开始时间");
            return false;
        }
        return true;
    },
    error:function(msg){
        parent.$("#errorText").html(msg);
        parent.$(".error-msg").show().delay(2000).hide(0);
    },
    success:function(msg){
        parent.$("#successText").html(msg);
        parent.$(".success-msg").show().delay(2000).hide(0);
    },
    save:function(){
        if(!re_input_rebate_pop.validate()){
            return;
        }
        common.ajax.getByAjax(false,"post","json","/orderCenter/nationwide/institutionTemp/rebate/history",parent.$("#history_rebate_form").serialize(),
            function(data){
                if(data.pass) {
                    re_input_rebate_pop.initList(parent.$("#institution").val());
                    re_input_rebate_pop.success("保存出单机构历史费率成功！");
                    //popup.mould.popTipsMould(false, "保存出单机构历史费率成功！", popup.mould.first, popup.mould.success, "", "57%", null);
                } else {
                    re_input_rebate_pop.error(data.message);
                }
            },function(){
                popup.mould.popTipsMould(false, "设置出单机构历史费率异常", popup.mould.first, popup.mould.error, "", "57%", null);
            }
        );
    }
}

