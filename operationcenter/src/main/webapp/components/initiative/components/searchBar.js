import React from 'react'
import mui from 'material-ui'
import injectTapEventPlugin from 'react-tap-event-plugin'
import Template from './template'

injectTapEventPlugin();


let ThemeManager = new mui.Styles.ThemeManager();
let {
	RaisedButton,
	Styles,
	FlatButton,
	FontIcon,
	Utils,
	DropDownMenu,
	TextField,
	TableBody,
	TableHeaderColumn, 
	TableRowColumn,
	TableRow, 
	TableHeader,
	TableFooter,
	Table,
	Dialog
} = mui;


class SearchBar extends React.Component {
	constructor(props) {
		super(props);
		// this.state ={todoList:[], showStatus:'all'};
		// this._bind('_submitValue', '_toggleAll');
	}
	getChildContext() {
	    return {
	      muiTheme: ThemeManager.getCurrentTheme()
	    }
  	}
	_searchFunc(){
		const selectIndex = this.refs.searchType.refs.menuItems.props.selectedIndex;
		this.props._searchMessages({
			key: this.refs.key.getValue(),
			type: this.props.menus[selectIndex].text
		});
	}
	render(){
		    let menus = [
			   { payload: '1', text: '模板号' },
			   { payload: '2', text: '模板名' },
			   { payload: '3', text: '短信内容' },
		   ]   
		return (
			<div>
			<ul className="navigation">
			  <li><DropDownMenu menuItems={this.props.menus} ref='searchType' style={{margin:'15px'}}/></li>
			  <li><TextField hintText="请输入查询内容" ref='key' /></li>
			  <li><RaisedButton label="搜索" style={{margin:'20px'}} primary={true} onClick={this._searchFunc.bind(this)}/></li>
			  <li><Template title={ {title:'新建主动发送短信'}} _loadServerMessages={this.props._loadServerMessages} /></li>
			</ul>
			 {/*
				<DropDownMenu menuItems={menus} />
				<TextField hintText="请输入查询内容" ref='myField' />
				<RaisedButton label="搜索" style={{margin:'20px'}} primary={true} onClick={this._searchFunc.bind(this)}/>
				<Template />	
				*/}			
			</div>
		)
	}
}
SearchBar.childContextTypes = {
  muiTheme: React.PropTypes.object
}

SearchBar.defaultProps = {
	 menus : [
	   { payload: '1', text: '模板号' },
	   { payload: '2', text: '模板名' },
	   { payload: '3', text: '短信内容' }
	]   
}
export default SearchBar