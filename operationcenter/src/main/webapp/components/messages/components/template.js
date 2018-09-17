import React from 'react'
import $ from 'jquery'
import mui from 'material-ui'
import injectTapEventPlugin from 'react-tap-event-plugin'
injectTapEventPlugin();

let ThemeManager = new mui.Styles.ThemeManager();
let {
	Dialog,
	FlatButton,
	RaisedButton,
	TextField
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
  		console.log(template);
    	this.refs.messageTemplate.show();
  	}
  	 _canceMessageTemplate() {
    	this.refs.messageTemplate.dismiss();
  	}
  	 _submitMessageTemplate() {
    	this.refs.messageTemplate.dismiss();
    	const template = {
	    	name : this.refs.name.getValue().trim(),
			md : this.refs.md.getValue().trim(),
			yxt : this.refs.yxt.getValue().trim(),
			content : this.refs.content.getValue().trim(),
			remarks : this.refs.remarks.getValue().trim(),
			id : this.props.template.id
		}
    	this._submitTemplate(template)
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
		return (
			<div style={{display:'inline'}}>
				<Dialog
				  title="新建短信模板"
				  ref='messageTemplate'
				  actions={customActions}
				  style={{top:'-30px'}}
				  autoScrollBodyContent={true}
				  modal={false}>
				  	<div style={{height:"600px"}}>
					  <div>
					  		<label className='label-name'>模板名：</label>
					  		<TextField hintText="模板名" ref='name' defaultValue={this.props.template.name}/>
					  </div>
					  <div>
					  		<label className='label-name'>漫道模板号：</label>
					  		<TextField hintText="漫道模板号" ref='md' defaultValue={this.props.template.md}/>
					  </div>
					  <div>
					  		<label className='label-name'>盈信通模板号：</label>
					  		<TextField hintText="盈信通模板号：" ref='yxt' defaultValue={this.props.template.yxt}/>
					  </div>
					  <div>
					  		<label className='label-name'>短信内容：</label>
					  		{/*<textarea  rows="8" cols="80" placeholder='输入模板内容' ref='content'  defaultValue='dddddd' />
					  		*/}
					  		<TextField  hintText="请输入短信内容"  cols="200"  multiLine={true} defaultValue={this.props.template.content} ref='content' />
					  </div>
					  <div>
					  		<label className='label-name'>备注：</label>
					  		<TextField id='remarks' hintText="请输入备注"  multiLine={true} defaultValue={this.props.template.remarks} ref='remarks' />
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
Template.defaultProps = { template:{}};

export default Template