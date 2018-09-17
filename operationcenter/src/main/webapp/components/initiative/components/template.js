import React from 'react'
import $ from 'jquery'
import mui from 'material-ui'
import injectTapEventPlugin from 'react-tap-event-plugin'
injectTapEventPlugin();

let ThemeManager = new mui.Styles.ThemeManager();
let {
	Dialog,
	DropDownMenu,
	FlatButton,
	RaisedButton,
	TextField,
	DatePicker,
	TimePicker,
	Checkbox
} = mui;

class Template extends React.Component{
	constructor(props){
		super(props);
		// this.state={config:{}}
	}
	getChildContext() {
	    return {
	      muiTheme: ThemeManager.getCurrentTheme()
	    }
  	}
	_submitTemplate(template){  // add/edit edit 包括 修改模板，修改模板状态
		console.log('save/edit tempalte', template);
		this.props._loadServerMessages(template);
		return;
		$.ajax({
			'type':'POST',
			'url':'/data',
			'data':JSON.stringify(template),
			'contentType':'application/json',
			'success':function(result){
				this.setState({data:result});
				console.log(result);
			}.bind(this)
		})
	}
  	_showMessageTemplate(template) {
  		//work around
  		console.log(template);
    	this.refs.messageTemplate.show();
  	}
  	 _canceMessageTemplate() {
    	this.refs.messageTemplate.dismiss();
  	}
  	_onCheck(){
		$('.msg-send-date').siblings().css('height', 'auto');
  	}
  	_changeSendDate(nill, date){
  		console.log(nill);
  		console.log(date);
  	}
  	_changeSendTime(nill, date){
  		console.log(nill);
  		console.log(date);
  	}
  	 _submitMessageTemplate() {
    	this.refs.messageTemplate.dismiss();
    	console.log(this.refs.sendImmedia.isChecked());
  	}
	render(){
		let customActions = [
			  <FlatButton
			    label="取消"
			    secondary={true}
			    onTouchTap={this._canceMessageTemplate.bind(this)} />,
			  <FlatButton
			    label="保存"
			    primary={true}
			    onTouchTap={this._submitMessageTemplate.bind(this)} />
		];
		const userTypes = [
			   { payload: '1', text: '用户群' },
			   { payload: '2', text: '单一用户' }
		]
		const userGroups = [
				{ id: '1', text: '测试群' },
			   	{ id: '2', text: '开发群' },
			   	{ id: '3', text: '开发群' },
			   	{ id: '4', text: '开发群' },
		]
		const [label2,label3,label4,label5] = [1,2,3,4,5]
		const msgContent = <span>尊敬的车车用户您好！车车车险为您报价：交强险  <span style={{color:'red'}}>2000</span>   元，商业险  "${label2}"   元，另代缴车船税 ' ${label3} '  元，共计 ' ${label4} '  元。最终价格以出单时保单价格为准。如需详询请致电车车客服专线4000150999，或登录  '${label5} '  自行查询配比保险价格。</span>
		return (
			<div style={{display:'inline'}}>
				<Dialog
				  title="新建主动发送短信"
				  ref='messageTemplate'
				  actions={customActions}
				  style={{top:'-30px',width:'1200px',height:'800px'}}
				  autoScrollBodyContent={true}
				  className='mgs-initia-tpl'
				  modal={false}>
				  	<div className='mgs-initia-tpl'>
					  <div style={{float:'left'}}>
					  		<label className='label-name'>发送用户：</label>
					  		<DropDownMenu menuItems={userTypes} ref='userType'/>
					  </div>
					  <div>
					  <TextField  hintText="手机号码" style={{width:'100px'}} defaultValue={this.props.template.content} ref='content' />
					  		<DropDownMenu menuItems={userGroups} ref='userGroup'/>
					  </div>
					  <div style={{float:'left'}}>
					  		<label className='label-name'>短信模板</label>
					  		<DropDownMenu menuItems={userTypes} ref='userType'/>
					  </div>
					  <div>
					  		<label className='label-name'>供应商</label>
					  		<DropDownMenu menuItems={userTypes} ref='userType'/>
					  </div>
					  <div style={{margin: '33px 0 33px'}}>
					  		<label className='label-name'>短信内容：</label>
					  		{msgContent}
					  </div>
					  <div style={{marginBottom: '32px'}}>
					  		<div className='label-name'>发送时间：</div>
					  		<div className='float-left' style={{width:'200px'}} >
					  			<Checkbox name="sendImmedia" style={{float:'left'}} value="sendImmedia" ref="sendImmedia" label="立即发送" defaultChecked={true}/>
					  		</div>
					  		<div className=''>
						  		<Checkbox style={{width: '20%',float: 'left'}} name="sendImmedia" value="sendDely" label="定时发送" onCheck={this._onCheck.bind(this)} defaultChecked={false}/>
						  		<DatePicker 
											  hintText="Ranged Date Picker"
											  autoOk={true}
											  defaultDate={this.props.data.sendDate}
											  minDate={new Date('02/22/2013')}
											  maxDate={new Date('02/22/2016')} 
											  textFieldStyle={{width:'150px',float:'left', marginRight: '20px'}}
											  className='msg-send-date' 
											  onChange={this._changeSendDate.bind(this)} />
						  		<TimePicker onChange={this._changeSendTime.bind(this)} defaultTime={this.props.data.sendTime} format="24hr" hintText="24hr Format" style={{width:'150px'}}  className='msg-send-date' />
					 		</div>
					  </div>
					  <div style={{clear:'both'}}></div>
					  <div>
					  		<label className='label-name'>备注</label>
					  		<TextField hintText="请输入备注" cols="200" fullWidth={true} multiLine={true} defaultValue={msgContent} ref='content' />
					  </div>
				  </div>
				</Dialog>
				<RaisedButton label={this.props.title} primary={true} onClick={this._showMessageTemplate.bind(this, this.props.template)}/>
			</div>
		)
	}
}
Template.childContextTypes = {
  muiTheme: React.PropTypes.object
};
Template.defaultProps = { template:{},data:{sendDate:new Date(),sendTime:new Date()}};

export default Template