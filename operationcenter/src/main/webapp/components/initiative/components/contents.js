import React from 'react'
import mui from 'material-ui'
import TableItem from './item'
// import $ from 'jquery'
import injectTapEventPlugin from 'react-tap-event-plugin'
injectTapEventPlugin();


let ThemeManager = new mui.Styles.ThemeManager();
let {
	RaisedButton,
	Dialog,
	Styles,
	FlatButton,
	FontIcon,
	Utils,
	DropDownMenu,
	TableBody,
	TableHeaderColumn, 
	TableRowColumn,
	TableRow, 
	TableHeader,
	TableFooter,
	Table
} = mui;


class Contents extends React.Component{
	constructor(props) {
		super(props);
	}
	getChildContext() {
	    return {
	      muiTheme: ThemeManager.getCurrentTheme()
	    }
  	}
  	componentDidMount() {
  		$('.mui-body-table').css('overflow-x', 'auto');
  	}
	render(){
		let Headers = this.props.titles.map((title)=>{
			return <TableHeaderColumn tooltip={title.name} colSpan={title.colSpan} style={{textAlign: 'center'}} >{title} </TableHeaderColumn>
		})
		let Contents = this.props.messages.map((item)=>{
			return <TableItem item={item} />
		})
	    return(
	        <div >
	          	<Table height={'450px'}  style={{width:'2500px'}} selectable={false} multiSelectable={false} >{/* multiSelectable={true} selectable={false}*/ }
				  <TableHeader displaySelectAll={false} adjustForCheckbox={false}> {/*enableSelectAll={true} multiSelectable={true} selectable={false}*/ }
				    <TableRow>
				      <TableHeaderColumn tooltip='序号' colSpan='1' style={{textAlign: 'center'}} >序号 </TableHeaderColumn>
				      <TableHeaderColumn tooltip='模板号漫道，盈信通' colSpan='2' style={{textAlign: 'center'}} >模板信息 </TableHeaderColumn>
				      <TableHeaderColumn tooltip='短信内容' colSpan='4' style={{textAlign: 'center'}} >短信内容 </TableHeaderColumn>
				      <TableHeaderColumn tooltip='状态' colSpan='1' style={{textAlign: 'center'}} >发送用户 </TableHeaderColumn>
				      <TableHeaderColumn tooltip='备注' colSpan='1' style={{textAlign: 'center'}} >发送状态 </TableHeaderColumn>
				      <TableHeaderColumn tooltip='操作' colSpan='2' style={{textAlign: 'center'}} >发送数据 </TableHeaderColumn>
				      <TableHeaderColumn tooltip='操作' colSpan='2' style={{textAlign: 'center'}} >备注 </TableHeaderColumn>
				      <TableHeaderColumn tooltip='操作' colSpan='2' style={{textAlign: 'center'}} >最后编辑 </TableHeaderColumn>
				      <TableHeaderColumn tooltip='操作' colSpan='2' style={{textAlign: 'center'}} >操作 </TableHeaderColumn>
				    </TableRow>
				  </TableHeader>
				  <TableBody showRowHover={true} stripedRows={false} displayRowCheckbox={false}>
				  	{Contents}
				  </TableBody>
				</Table>
	        </div>
	      )
	  }
}

Contents.childContextTypes = {
  muiTheme: React.PropTypes.object
};

export default Contents