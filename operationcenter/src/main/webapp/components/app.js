import React from 'react'
import {Router,Route, Link} from 'react-router'
import Tempalte from './messages/components/template'
// import Test from './messages/components/test'
import Messages from './messages/messages.js'
import Initiative from './initiative/initiative.js'
import $ from 'jquery'
import mui from 'material-ui'

class App extends React.Component {
	constructor() {
		super();
		// this.state ={todoList:[], showStatus:'all'};
		// this._bind('_submitValue', '_toggleAll');
	}
	render(){
		return(
			<div>
				<ul id='message_ul' className='collapse list-unstyled sednav message_ul'>
					<li><Link to='/messages' test='222'><i className="glyphicon glyphicon-edit"></i>&nbsp;<span>短信模板管理</span></Link></li>
					<li><Link to='/initiative'><i className="glyphicon glyphicon-edit"></i>&nbsp;<span>主动发送短信</span></Link></li>
				</ul>
				<div className='react-body'>{this.props.children}</div>
			</div>
		)
	}
}


// const routeConfig = {
//   component: 'div',
//   childRoutes: [{
//     path: '/',
//     component: App,
//     childRoutes: [
//       require('./templates/index.js'),
//       require('./templates/inbox.js')
//     ]
//   }]
// };


// const routeConfig = [
//   {
//     path:'/',
//     component: App,
//     childRoutes: [
//       {
//         path: 'templates',component: Tempaltes
//       },
//       {
//         path: 'inbox',
//         component: Inbox      }
//     ]
//   }
// ]

React.render((
  <Router>
    <Route path="/" component={App}>
      //<Route path="templates" component={Tempalte} />
      <Route path="messages" component={Messages} />
      <Route path="initiative" component={Initiative} />
    </Route>
  </Router>
), document.getElementById('message-component'))

// React.render(
//   <Router routes={routeConfig} />
// , document.getElementById('message-component'));

	/*
					<li><Link to='/users'><i className="glyphicon glyphicon-list-alt"></i>&nbsp;<span>用户功能管理</span></Link></li>
					<li><Link to='/logs'><i className="glyphicon glyphicon-list-alt"></i>&nbsp;<span>条件触发短信</span></Link></li>
					
					<li><Link to=''><i className="glyphicon glyphicon-list-alt"></i>&nbsp;<span>短信日志</span></Link></li>
					<li><Link to=''><i className="glyphicon glyphicon-list-alt"></i>&nbsp;<span>主动发送短信</span></Link></li>
					*/