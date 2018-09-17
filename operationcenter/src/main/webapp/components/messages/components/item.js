import React from 'react'
import mui from 'material-ui'
import Template from './template'
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


class TableItem extends React.Component{
	constructor(props) {
		super(props);
	}
	getChildContext() {
	    return {
	      muiTheme: ThemeManager.getCurrentTheme()
	    }
  	}
	render(){
		let content = this.props.item
	    return(
          	<TableRow >
			      <TableRowColumn colSpan="1" style={{textAlign: 'center'}}>{content.index}</TableRowColumn>
			      <TableRowColumn colSpan="2" style={{whiteSpace: 'normal'}}>{content.md} {content.yxt}</TableRowColumn>
			      <TableRowColumn colSpan="2" style={{textAlign: 'center',whiteSpace: 'normal'}}>{content.name}</TableRowColumn>
			      <TableRowColumn colSpan="4" style={{whiteSpace: 'normal'}}>{content.content}</TableRowColumn>
			      <TableRowColumn colSpan="1" style={{textAlign: 'center'}}>{content.status}</TableRowColumn>
			      <TableRowColumn colSpan="2" style={{whiteSpace: 'normal',textAlign: 'center'}}>{content.remarks}</TableRowColumn>
			      <TableRowColumn colSpan="2" style={{textAlign: 'center'}}>
			      	{content.status ? <RaisedButton label="启用" secondary={true} style={{marginRight:'5px'}}/> 
			      		  : <RaisedButton label="禁用" labelColor={'red'} style={{marginRight:'5px'}}/> 
			      	}
					<Template template={content} title={ {title:'编辑'}} />
			      </TableRowColumn>
			    </TableRow>
	      )
	  }
}

TableItem.childContextTypes = {
  muiTheme: React.PropTypes.object
};

export default TableItem