const { request } = require('../utils/request.js');

function login(data) {
	return request({ url: '/auth/login', method: 'POST', data });
}
function validate() {
	return request({ url: '/auth/validate', method: 'GET' });
}

module.exports = { login, validate };


