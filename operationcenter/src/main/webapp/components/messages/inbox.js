module.exports = {
	path: 'inbox',
	getComponent(location, cb) {
		require('./components/inbox.js')
		// require.ensure([], (require)=>{
		// 	cb(null, require('./components/inbox.js'))
		// })
	}

}