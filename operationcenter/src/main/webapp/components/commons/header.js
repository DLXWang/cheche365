import React from 'react'
import mui from 'material-ui'
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

class Header extends React.Component {
	constructor(props) {
		super(props);
	}
	getChildContext() {
	    return {
	      muiTheme: ThemeManager.getCurrentTheme()
	    }
  	}
	render(){
		let Headers = this.props.titles.map((title)=>{
			return <TableHeaderColumn tooltip={title.name} colSpan={title.colSpan} style={{textAlign: 'center'}} >{title} </TableHeaderColumn>
		})
		return(
			<div>
				{Headers}
			</div>
		)
	}
}

Header.childContextTypes = {
  muiTheme: React.PropTypes.object
};

export default Header
