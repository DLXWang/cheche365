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

/*
	this.props.data = { // 从调用处传递过来
  			total:50,
  			size:6,
  			currIndex:1,
  			totalNums: Math.ceil(50 / 6 )
  		};
  	this.props._loadServerMessages// 交互函数
*/
class Pagination extends React.Component {
	constructor(props){
		super(props);
		this.state = {
			currentIndex:0
		}
	}
	_nextPage(){
		const currentIndex = this.state.currentIndex + 1;
		if (currentIndex == this.props.data.totalNums) {
			return;
		}
		this.setState({currentIndex:currentIndex});

		this.props._loadServerMessages({});
	}
	_prePage(){
		const currentIndex = this.state.currentIndex - 1;
		if (currentIndex < 0) {
			return;
		}
		this.setState({currentIndex:currentIndex})
		this.props._loadServerMessages({});
	}
	_selectPage(index){
		this.setState({currentIndex:index -1});
		this.props._loadServerMessages({});
	}
	getChildContext() {
	    return {
	      muiTheme: ThemeManager.getCurrentTheme()
	    }
  	}
  	render() {
  		const totalNums = this.props.data.totalNums;
 		const currentIndex = this.state.currentIndex;

  		let Pages = [];
		let preItem = <li><a href="javascript:void(0)" onClick={this._prePage.bind(this)} aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>
  		if (currentIndex == 0) {
			preItem = <li className='disabled'><a href="javascript:void(0)" onClick={this._prePage.bind(this)} aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>
		}  
		Pages.push(preItem);

  		for(let i = 0; i< totalNums;i++){
  			let li = <li><a href="javascript:void(0)" onClick={this._selectPage.bind(this, i+1)}>{i+1}</a></li>
  			if (currentIndex == i) {
  				li = <li className='active'><a href="javascript:void(0)" onClick={this._selectPage.bind(this, i+1)}>{i+1}</a></li>
  			}
  			Pages.push(li)
  		}

  		let nextItem = <li><a href="javascript:void(0)" onClick={this._nextPage.bind(this)} aria-label="Previous"><span aria-hidden="true">&raquo;</span></a></li>
  		if (totalNums == currentIndex + 1) {
			nextItem = <li className='disabled'><a href="javascript:void(0)" onClick={this._nextPage.bind(this)} aria-label="Previous"><span aria-hidden="true">&raquo;</span></a></li>
		}
		Pages.push(nextItem);
  		return(
  			<div style={{margin:'20px'}}>
  				<nav>
				  <ul className="pagination">
				    {Pages}
				  </ul>
				</nav>
  			</div>
  		)
  	}
}

Pagination.childContextTypes = {
  muiTheme: React.PropTypes.object
};
Pagination.defaultProps = { template:{}};

export default Pagination