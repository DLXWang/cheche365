import React from 'react'
import Contents from './components/contents'
import SearchBar from './components/searchBar'
import Pagination from '../commons/pagination'
import {configs} from './model.js'
import $ from 'jquery'

// import Bootstrap from 'bootstrap'

class Messages extends React.Component{
	constructor(props) {
		super(props);
		// this.state ={todoList:[], showStatus:'all'};
		this.state = {
			messages:configs.messages
		}
		this.props.pageCfg = { // 从调用处传递过来
  			total:50,
  			size:6,
  			currIndex:1,
  			totalNums: Math.ceil(50 / 6 )
  		};
	}
	componentDidMount(){
		console.log('load ajax to get server data');
		this._loadServerMessages({});
	}
	_searchMessages(searchCfg){
		console.log('searh key is ', searchCfg);
		const messages = this.state.messages;
		messages.shift();
		this.setState({messages:messages});
	}
	_loadServerMessages(config){  //在template 统一增加或者编辑。  message 重新ajax获取。 先手动调用这个函数 手动刷新数据 测试用
		const searchCfg = {
			size:8,
			total:20,
			index:0,
			search:{
				type:'模板号',
				q:'工作'
			}
		}
		return;
		$.ajax({
			'type':'GET',
			'url': '/data',
			'contentType': 'application/json',
			'dataType':'json',
			'data':JSON.stringify({}),
			'success': function(data){
				console.log(data);
				this.setState({data:data});
			}.bind(this)
		});
	}
	_pushTemplate(template){
		if(!template.id){
			template.id = new Date().getTime();
		}
		template.index = 11111;
		const messages = this.state.messages;
		messages.push(template);
		this.setState({messages:messages});
	}
	render(){
		return(
			<div>
				<div><SearchBar _loadServerMessages={this._pushTemplate.bind(this)} _searchMessages={this._searchMessages.bind(this)}  /></div>
				<div><Contents data='2' titles={configs.titles} messages={this.state.messages}/></div>
				<div><Pagination data={this.props.pageCfg} _loadServerMessages={this._loadServerMessages.bind(this)} /></div>
			</div>
		)
	}
}

export default Messages