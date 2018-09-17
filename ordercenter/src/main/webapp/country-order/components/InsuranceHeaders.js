/*
   佣金设置表头 保险公司 下一级，交强险，车船税 组件
*/
import React, { Component, PropTypes } from 'react'
import {  TableRow, TableHeaderColumn} from 'material-ui' 

class InsuranceHeaders extends Component {
	constructor(props) {
		super(props); 
	}
	render() {
		let Components = this.props.insurances.map((item)=>{
			return 	[
						<TableHeaderColumn style={{textAlign: 'center'}}>
							<div>交强险</div>
						</TableHeaderColumn>,
						<TableHeaderColumn style={{textAlign: 'center'}}>
							<div>商业险</div>
						</TableHeaderColumn>
					]
		});
		return (
			<TableRow>
				<TableHeaderColumn></TableHeaderColumn>
				{Components}
		    </TableRow>
			  
		)
	}
}

InsuranceHeaders.propTypes = {
  insurances: PropTypes.array.isRequired
}

export default InsuranceHeaders
