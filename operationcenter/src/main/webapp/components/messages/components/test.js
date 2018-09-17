import React from 'react'
import $ from 'jquery'

class Pagination extends React.Component {
	constructor(props){
		super(props);
	}
  	render() {
  		return(
  			<div style={{margin:'20px'}}>
				<button type="button" className="btn btn-primary btn-lg" data-toggle="modal" data-target="#myModal">
				  Launch demo modal
				</button>

				<div className="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
				  <div className="modal-dialog" role="document">
				    <div className="modal-content">
				      <div className="modal-header">
				        <button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				        <h4 className="modal-title" id="myModalLabel">Modal title</h4>
				      </div>
				      <div className="modal-body">
				        ...test
				      </div>
				      <div className="modal-footer">
				        <button type="button" className="btn btn-default" data-dismiss="modal">Close</button>
				        <button type="button" className="btn btn-primary">Save changes</button>
				      </div>
				    </div>
				  </div>
				</div>
  			</div>
  		)
  	}
}
Pagination.defaultProps = { template:{}};

export default Pagination